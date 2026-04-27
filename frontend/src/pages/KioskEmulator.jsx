import { useState, useEffect, useRef } from 'react';
import { getKiosks, getInventory, getPayments } from '../api';
import { useToast } from '../context/ToastContext';
import LoadingSpinner from '../components/LoadingSpinner';
import ErrorBanner from '../components/ErrorBanner';
import axios from 'axios';
import { motion, AnimatePresence } from 'framer-motion';
import { 
  Monitor, ShoppingCart, CreditCard, CheckCircle, Package, 
  User, ChevronRight, Plus, Minus, ArrowLeft, Loader2
} from 'lucide-react';

const STEPS = [
  { id: 'kiosk', label: 'Select Kiosk', icon: Monitor },
  { id: 'browse', label: 'Browse Products', icon: Package },
  { id: 'cart', label: 'Review Cart', icon: ShoppingCart },
  { id: 'payment', label: 'Payment', icon: CreditCard },
  { id: 'processing', label: 'Processing', icon: Loader2 },
  { id: 'receipt', label: 'Receipt', icon: CheckCircle },
];

export default function KioskEmulator() {
  const [step, setStep] = useState(0);
  const [kiosks, setKiosks] = useState([]);
  const [products, setProducts] = useState([]);
  const [providers, setProviders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);
  const { addToast } = useToast();

  // Emulator state
  const [selectedKiosk, setSelectedKiosk] = useState(null);
  const [cart, setCart] = useState([]); // [{ product, qty }]
  const [selectedPayment, setSelectedPayment] = useState(null);
  const [customerId, setCustomerId] = useState('CUST-' + Math.floor(Math.random() * 9000 + 1000));
  const [txnResult, setTxnResult] = useState(null);
  const [processing, setProcessing] = useState(false);

  const fetchData = () => {
    setLoading(true);
    setError(false);
    Promise.all([getKiosks(), getPayments()])
      .then(([kRes, pRes]) => {
        setKiosks(kRes.data);
        setProviders(pRes.data);
        setLoading(false);
      })
      .catch(() => { setError(true); setLoading(false); });
  };

  useEffect(() => { fetchData(); }, []);

  const loadProducts = async (kioskId) => {
    try {
      const res = await getInventory(kioskId);
      setProducts(res.data.inventory || []);
    } catch (e) {
      setProducts([]);
    }
  };

  const selectKiosk = (kiosk) => {
    setSelectedKiosk(kiosk);
    loadProducts(kiosk.kioskId);
    setStep(1);
  };

  const addToCart = (product) => {
    setCart(prev => {
      const existing = prev.find(c => c.product.id === product.id);
      if (existing) {
        return prev.map(c => c.product.id === product.id ? { ...c, qty: c.qty + 1 } : c);
      }
      return [...prev, { product, qty: 1 }];
    });
  };

  const removeFromCart = (productId) => {
    setCart(prev => {
      const existing = prev.find(c => c.product.id === productId);
      if (existing && existing.qty > 1) {
        return prev.map(c => c.product.id === productId ? { ...c, qty: c.qty - 1 } : c);
      }
      return prev.filter(c => c.product.id !== productId);
    });
  };

  const cartTotal = cart.reduce((sum, c) => sum + (c.product.price * c.qty), 0);

  const submitPurchase = async () => {
    if (!selectedKiosk || cart.length === 0 || !selectedPayment) return;
    setStep(4); // processing
    setProcessing(true);

    // Send first item as the main purchase via the emulator endpoint
    const firstItem = cart[0];
    try {
      await axios.post('/api/emulator/purchase', {
        kioskId: selectedKiosk.kioskId,
        userId: customerId,
        productId: firstItem.product.id,
        quantity: firstItem.qty,
        paymentMethod: selectedPayment
      });

      // Wait for the backend processing to complete (the backend runs with delays)
      await new Promise(r => setTimeout(r, 8000));
      
      setTxnResult({
        success: true,
        txnId: 'TXN-' + Date.now().toString(36).toUpperCase(),
        items: cart,
        total: cartTotal,
        payment: selectedPayment,
        kiosk: selectedKiosk.kioskId,
        customer: customerId,
        time: new Date().toLocaleString()
      });
      setStep(5);
      addToast('Purchase completed! Check the Live Event Feed →', 'success');
    } catch (e) {
      setTxnResult({ success: false });
      setStep(5);
      addToast('Purchase failed', 'error');
    }
    setProcessing(false);
  };

  const resetEmulator = () => {
    setStep(0);
    setSelectedKiosk(null);
    setCart([]);
    setSelectedPayment(null);
    setTxnResult(null);
    setCustomerId('CUST-' + Math.floor(Math.random() * 9000 + 1000));
  };

  if (loading) return <LoadingSpinner text="Initializing kiosk emulator..." />;
  if (error) return <ErrorBanner message="Failed to load emulator data." onRetry={fetchData} />;

  const currentStepData = STEPS[step];

  return (
    <div className="space-y-6 max-w-4xl mx-auto">
      {/* Header */}
      <div>
        <h1 className="text-2xl font-bold text-[var(--text-primary)] tracking-tight">Kiosk Emulator</h1>
        <p className="text-sm text-[var(--text-secondary)] mt-1">
          Simulate a customer purchase flow end-to-end. Events stream to the Live Feed in real-time.
        </p>
      </div>

      {/* Step Progress Bar */}
      <div className="mono-card p-4">
        <div className="flex items-center justify-between">
          {STEPS.map((s, i) => (
            <div key={s.id} className="flex items-center">
              <div className={`flex items-center gap-2 px-3 py-1.5 rounded transition-colors ${
                i === step 
                  ? 'bg-[var(--text-primary)] text-[var(--bg-primary)] font-semibold' 
                  : i < step 
                    ? 'text-[var(--success)]' 
                    : 'text-[var(--text-secondary)]'
              }`}>
                <s.icon className={`w-4 h-4 ${i === 4 && step === 4 ? 'animate-spin' : ''}`} />
                <span className="text-xs hidden md:inline">{s.label}</span>
              </div>
              {i < STEPS.length - 1 && (
                <ChevronRight className="w-4 h-4 text-[var(--border-color)] mx-1" />
              )}
            </div>
          ))}
        </div>
      </div>

      {/* Customer ID */}
      <div className="flex items-center gap-3 text-sm">
        <User className="w-4 h-4 text-[var(--text-secondary)]" />
        <span className="text-[var(--text-secondary)]">Customer:</span>
        <span className="font-mono font-bold text-[var(--text-primary)]">{customerId}</span>
      </div>

      {/* Step Content */}
      <AnimatePresence mode="wait">
        <motion.div 
          key={step}
          initial={{ opacity: 0, y: 10 }}
          animate={{ opacity: 1, y: 0 }}
          exit={{ opacity: 0, y: -10 }}
          transition={{ duration: 0.2 }}
        >
          {/* Step 0: Select Kiosk */}
          {step === 0 && (
            <div className="space-y-4">
              <h2 className="text-lg font-semibold text-[var(--text-primary)]">Choose a Kiosk</h2>
              <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                {kiosks.filter(k => k.state === 'ACTIVE').map(k => (
                  <button
                    key={k.kioskId}
                    onClick={() => selectKiosk(k)}
                    className="mono-card p-6 text-left hover:border-[var(--text-primary)] transition-colors group"
                  >
                    <div className="flex items-center gap-3 mb-3">
                      <Monitor className="w-6 h-6 text-[var(--text-secondary)] group-hover:text-[var(--text-primary)]" />
                      <h3 className="text-lg font-bold text-[var(--text-primary)]">{k.kioskId}</h3>
                    </div>
                    <p className="text-sm text-[var(--text-secondary)]">{k.typeName || k.type}</p>
                    <div className="mt-4 flex items-center gap-1.5">
                      <div className="w-2 h-2 rounded-full bg-[var(--success)]" />
                      <span className="text-[10px] font-bold text-[var(--success)] tracking-wider">ONLINE</span>
                    </div>
                  </button>
                ))}
              </div>
            </div>
          )}

          {/* Step 1: Browse Products */}
          {step === 1 && (
            <div className="space-y-4">
              <div className="flex justify-between items-center">
                <h2 className="text-lg font-semibold text-[var(--text-primary)]">
                  Product Catalog — {selectedKiosk?.kioskId}
                </h2>
                <button onClick={() => setStep(0)} className="text-xs text-[var(--text-secondary)] hover:text-[var(--text-primary)] flex items-center gap-1">
                  <ArrowLeft className="w-3 h-3" /> Change Kiosk
                </button>
              </div>
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                {products.map(p => {
                  const inCart = cart.find(c => c.product.id === p.id);
                  return (
                    <div key={p.id} className="mono-card p-5 flex justify-between items-center">
                      <div>
                        <h3 className="font-bold text-[var(--text-primary)]">{p.name}</h3>
                        <p className="text-xs text-[var(--text-secondary)] font-mono mt-1">{p.id}</p>
                        <div className="flex items-center gap-4 mt-2">
                          <span className="text-sm font-bold text-[var(--text-primary)]">${p.price.toFixed(2)}</span>
                          <span className={`text-xs ${p.stock > 10 ? 'text-[var(--success)]' : 'text-[var(--error)]'}`}>
                            {p.stock} in stock
                          </span>
                        </div>
                      </div>
                      <div className="flex items-center gap-2">
                        {inCart && (
                          <>
                            <button 
                              onClick={() => removeFromCart(p.id)}
                              className="p-1.5 rounded border border-[var(--border-color)] text-[var(--text-secondary)] hover:text-[var(--text-primary)]"
                            >
                              <Minus className="w-4 h-4" />
                            </button>
                            <span className="w-6 text-center font-bold text-[var(--text-primary)] text-sm">{inCart.qty}</span>
                          </>
                        )}
                        <button 
                          onClick={() => addToCart(p)}
                          disabled={p.stock === 0}
                          className="p-1.5 rounded bg-[var(--text-primary)] text-[var(--bg-primary)] hover:opacity-90 disabled:opacity-30 transition-opacity"
                        >
                          <Plus className="w-4 h-4" />
                        </button>
                      </div>
                    </div>
                  );
                })}
              </div>
              {cart.length > 0 && (
                <div className="flex justify-end">
                  <button 
                    onClick={() => setStep(2)}
                    className="px-6 py-2.5 bg-[var(--text-primary)] text-[var(--bg-primary)] rounded font-medium text-sm hover:opacity-90 flex items-center gap-2"
                  >
                    <ShoppingCart className="w-4 h-4" />
                    Review Cart ({cart.length})
                  </button>
                </div>
              )}
            </div>
          )}

          {/* Step 2: Cart Review */}
          {step === 2 && (
            <div className="space-y-4">
              <div className="flex justify-between items-center">
                <h2 className="text-lg font-semibold text-[var(--text-primary)]">Your Cart</h2>
                <button onClick={() => setStep(1)} className="text-xs text-[var(--text-secondary)] hover:text-[var(--text-primary)] flex items-center gap-1">
                  <ArrowLeft className="w-3 h-3" /> Continue Shopping
                </button>
              </div>
              <div className="mono-card overflow-hidden">
                <table className="w-full text-sm">
                  <thead className="bg-[var(--bg-secondary)] text-[var(--text-secondary)] text-xs uppercase font-semibold border-b border-[var(--border-color)]">
                    <tr>
                      <th className="px-6 py-3 text-left">Item</th>
                      <th className="px-6 py-3 text-center">Qty</th>
                      <th className="px-6 py-3 text-right">Price</th>
                      <th className="px-6 py-3 text-right">Subtotal</th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-[var(--border-color)]">
                    {cart.map(c => (
                      <tr key={c.product.id}>
                        <td className="px-6 py-4 font-medium text-[var(--text-primary)]">{c.product.name}</td>
                        <td className="px-6 py-4 text-center">
                          <div className="flex items-center justify-center gap-2">
                            <button onClick={() => removeFromCart(c.product.id)} className="p-0.5 rounded border border-[var(--border-color)] text-[var(--text-secondary)]">
                              <Minus className="w-3 h-3" />
                            </button>
                            <span className="w-6 text-center font-bold">{c.qty}</span>
                            <button onClick={() => addToCart(c.product)} className="p-0.5 rounded border border-[var(--border-color)] text-[var(--text-secondary)]">
                              <Plus className="w-3 h-3" />
                            </button>
                          </div>
                        </td>
                        <td className="px-6 py-4 text-right font-mono text-[var(--text-secondary)]">${c.product.price.toFixed(2)}</td>
                        <td className="px-6 py-4 text-right font-mono font-bold text-[var(--text-primary)]">${(c.product.price * c.qty).toFixed(2)}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
                <div className="border-t border-[var(--border-color)] p-4 bg-[var(--bg-secondary)] flex justify-between items-center">
                  <span className="text-sm text-[var(--text-secondary)]">Total</span>
                  <span className="text-xl font-bold text-[var(--text-primary)]">${cartTotal.toFixed(2)}</span>
                </div>
              </div>
              <div className="flex justify-end">
                <button 
                  onClick={() => setStep(3)}
                  className="px-6 py-2.5 bg-[var(--text-primary)] text-[var(--bg-primary)] rounded font-medium text-sm hover:opacity-90 flex items-center gap-2"
                >
                  <CreditCard className="w-4 h-4" />
                  Choose Payment
                </button>
              </div>
            </div>
          )}

          {/* Step 3: Payment Selection */}
          {step === 3 && (
            <div className="space-y-4">
              <div className="flex justify-between items-center">
                <h2 className="text-lg font-semibold text-[var(--text-primary)]">Select Payment Method</h2>
                <button onClick={() => setStep(2)} className="text-xs text-[var(--text-secondary)] hover:text-[var(--text-primary)] flex items-center gap-1">
                  <ArrowLeft className="w-3 h-3" /> Back to Cart
                </button>
              </div>
              <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
                {providers.map(p => (
                  <button 
                    key={p.name}
                    onClick={() => setSelectedPayment(p.name)}
                    className={`mono-card p-6 text-left transition-colors ${
                      selectedPayment === p.name 
                        ? 'border-[var(--text-primary)] border-2' 
                        : 'hover:border-[var(--text-secondary)]'
                    }`}
                  >
                    <CreditCard className="w-8 h-8 text-[var(--text-secondary)] mb-3" />
                    <h3 className="font-bold text-[var(--text-primary)]">{p.name}</h3>
                    <p className="text-xs text-[var(--text-secondary)] mt-1">Adapter Interface</p>
                  </button>
                ))}
              </div>
              {selectedPayment && (
                <div className="mono-card p-4 flex justify-between items-center">
                  <div>
                    <p className="text-sm text-[var(--text-secondary)]">Total to charge via <strong className="text-[var(--text-primary)]">{selectedPayment}</strong></p>
                    <p className="text-2xl font-bold text-[var(--text-primary)] mt-1">${cartTotal.toFixed(2)}</p>
                  </div>
                  <button
                    onClick={submitPurchase}
                    className="px-8 py-3 bg-[var(--text-primary)] text-[var(--bg-primary)] rounded font-bold text-sm hover:opacity-90 transition-opacity"
                  >
                    Confirm Purchase
                  </button>
                </div>
              )}
            </div>
          )}

          {/* Step 4: Processing */}
          {step === 4 && (
            <div className="mono-card p-12 flex flex-col items-center justify-center text-center space-y-6">
              <div className="w-16 h-16 rounded-full border-2 border-[var(--text-primary)] border-t-transparent animate-spin" />
              <div>
                <h2 className="text-lg font-bold text-[var(--text-primary)]">Processing Transaction</h2>
                <p className="text-sm text-[var(--text-secondary)] mt-2">
                  Watch the <strong>Live Event Feed →</strong> to see each step in real-time.
                </p>
              </div>
              <div className="space-y-1 text-xs text-[var(--text-secondary)] font-mono">
                <p>Kiosk: {selectedKiosk?.kioskId}</p>
                <p>Customer: {customerId}</p>
                <p>Payment: {selectedPayment}</p>
                <p>Amount: ${cartTotal.toFixed(2)}</p>
              </div>
            </div>
          )}

          {/* Step 5: Receipt */}
          {step === 5 && txnResult && (
            <div className="space-y-4">
              <div className="mono-card p-8">
                <div className="text-center mb-8">
                  {txnResult.success ? (
                    <CheckCircle className="w-16 h-16 text-[var(--success)] mx-auto mb-4" />
                  ) : (
                    <div className="w-16 h-16 rounded-full border-2 border-[var(--error)] flex items-center justify-center mx-auto mb-4">
                      <span className="text-[var(--error)] text-2xl font-bold">!</span>
                    </div>
                  )}
                  <h2 className="text-xl font-bold text-[var(--text-primary)]">
                    {txnResult.success ? 'Purchase Complete' : 'Purchase Failed'}
                  </h2>
                  <p className="text-xs font-mono text-[var(--text-secondary)] mt-2">
                    {txnResult.success ? txnResult.txnId : 'Transaction was not completed'}
                  </p>
                </div>
                
                {txnResult.success && (
                  <div className="border-t border-dashed border-[var(--border-color)] pt-6 space-y-3">
                    <div className="flex justify-between text-sm">
                      <span className="text-[var(--text-secondary)]">Kiosk</span>
                      <span className="font-mono font-medium text-[var(--text-primary)]">{txnResult.kiosk}</span>
                    </div>
                    <div className="flex justify-between text-sm">
                      <span className="text-[var(--text-secondary)]">Customer</span>
                      <span className="font-mono font-medium text-[var(--text-primary)]">{txnResult.customer}</span>
                    </div>
                    <div className="flex justify-between text-sm">
                      <span className="text-[var(--text-secondary)]">Payment</span>
                      <span className="font-medium text-[var(--text-primary)]">{txnResult.payment}</span>
                    </div>
                    <div className="flex justify-between text-sm">
                      <span className="text-[var(--text-secondary)]">Time</span>
                      <span className="text-[var(--text-primary)]">{txnResult.time}</span>
                    </div>
                    <div className="border-t border-[var(--border-color)] pt-3 mt-3">
                      {txnResult.items.map(c => (
                        <div key={c.product.id} className="flex justify-between text-sm py-1">
                          <span className="text-[var(--text-primary)]">{c.product.name} x{c.qty}</span>
                          <span className="font-mono text-[var(--text-primary)]">${(c.product.price * c.qty).toFixed(2)}</span>
                        </div>
                      ))}
                    </div>
                    <div className="border-t border-[var(--border-color)] pt-3 flex justify-between">
                      <span className="font-bold text-[var(--text-primary)]">Total</span>
                      <span className="text-xl font-bold text-[var(--text-primary)]">${txnResult.total.toFixed(2)}</span>
                    </div>
                  </div>
                )}
              </div>
              <div className="flex justify-center">
                <button 
                  onClick={resetEmulator}
                  className="px-6 py-2.5 bg-[var(--text-primary)] text-[var(--bg-primary)] rounded font-medium text-sm hover:opacity-90"
                >
                  Start New Transaction
                </button>
              </div>
            </div>
          )}
        </motion.div>
      </AnimatePresence>
    </div>
  );
}
