import { useState, useEffect } from 'react';
import { getPayments, registerProvider } from '../api';
import { useToast } from '../context/ToastContext';
import { useTheme } from '../context/ThemeContext';
import { PieChart, Pie, Cell, ResponsiveContainer, Tooltip, Legend } from 'recharts';
import { CreditCard, Wallet, Smartphone, Bitcoin, Plus, CheckCircle2 } from 'lucide-react';
import LoadingSpinner from '../components/LoadingSpinner';
import ErrorBanner from '../components/ErrorBanner';

export default function PaymentProviders() {
  const [providers, setProviders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);
  const [newProvider, setNewProvider] = useState('');
  const [registering, setRegistering] = useState(false);
  const { addToast } = useToast();
  const { theme } = useTheme();

  const fetchProviders = () => {
    setLoading(true);
    setError(false);
    getPayments()
      .then(res => { setProviders(res.data); setLoading(false); })
      .catch(() => { setError(true); setLoading(false); });
  };

  useEffect(() => { fetchProviders(); }, []);

  const handleRegister = async (e) => {
    e.preventDefault();
    if (!newProvider) return;
    setRegistering(true);
    try {
      await registerProvider(newProvider);
      addToast(`Successfully registered ${newProvider} adapter`, 'success');
      setNewProvider('');
      fetchProviders();
    } catch (err) {
      addToast('Failed to register provider', 'error');
    }
    setRegistering(false);
  };

  const getProviderIcon = (name) => {
    const n = name.toLowerCase();
    if (n.includes('crypto') || n.includes('bitcoin')) return <Bitcoin className="w-6 h-6" />;
    if (n.includes('wallet') || n.includes('upi')) return <Smartphone className="w-6 h-6" />;
    return <CreditCard className="w-6 h-6" />;
  };

  const chartData = providers.map((p, i) => ({
    name: p.name,
    value: Math.floor(Math.random() * 50) + 10
  }));

  const COLORS = theme === 'dark' 
    ? ['#ffffff', '#cccccc', '#999999', '#666666', '#333333'] 
    : ['#000000', '#333333', '#666666', '#999999', '#cccccc'];

  if (loading) return <LoadingSpinner text="Loading payment registry..." />;
  if (error) return <ErrorBanner message="Failed to load payment providers." onRetry={fetchProviders} />;

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-[var(--text-primary)] tracking-tight">Payment Gateways</h1>
        <p className="text-sm text-[var(--text-secondary)] mt-1">Manage registered Strategy pattern adapters</p>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="lg:col-span-2 space-y-4">
          <h2 className="text-lg font-semibold text-[var(--text-primary)]">Active Providers</h2>
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            {providers.map((p, i) => (
              <div key={i} className="mono-card p-5 flex items-start gap-4">
                <div className={`p-3 border border-[var(--border-color)] rounded text-[var(--text-primary)]`}>
                  {getProviderIcon(p.name)}
                </div>
                <div className="flex-1">
                  <div className="flex justify-between items-center">
                    <h3 className="font-bold text-[var(--text-primary)]">{p.name}</h3>
                    <CheckCircle2 className="w-4 h-4 text-[var(--success)]" />
                  </div>
                  <p className="text-xs text-[var(--text-secondary)] mt-1">Adapter Interface</p>
                  <div className="mt-3 text-[10px] font-mono text-[var(--text-secondary)] border border-[var(--border-color)] px-2 py-1 rounded inline-block">
                    Status: OK
                  </div>
                </div>
              </div>
            ))}
            
            <div className="rounded p-5 border-2 border-dashed border-[var(--border-color)] flex flex-col justify-center bg-[var(--bg-secondary)] transition-colors">
              <h3 className="font-semibold text-[var(--text-primary)] mb-3 text-sm">Register New Adapter</h3>
              <form onSubmit={handleRegister} className="flex gap-2">
                <input 
                  type="text" 
                  value={newProvider}
                  onChange={(e) => setNewProvider(e.target.value)}
                  placeholder="e.g. Crypto" 
                  className="flex-1 bg-[var(--surface)] border border-[var(--border-color)] rounded px-3 text-sm text-[var(--text-primary)] focus:outline-none focus:border-[var(--text-primary)]"
                />
                <button 
                  type="submit"
                  disabled={registering || !newProvider}
                  className="bg-[var(--text-primary)] text-[var(--bg-primary)] p-2 rounded disabled:opacity-50 transition-opacity flex items-center justify-center hover:opacity-90"
                >
                  {registering ? <div className="w-5 h-5 border-2 border-current border-t-transparent rounded-full animate-spin" /> : <Plus className="w-5 h-5" />}
                </button>
              </form>
            </div>
          </div>
        </div>

        <div className="space-y-4">
          <h2 className="text-lg font-semibold text-[var(--text-primary)]">Usage Distribution</h2>
          <div className="mono-card p-5 h-[350px] flex flex-col items-center justify-center">
            {providers.length > 0 ? (
              <ResponsiveContainer width="100%" height="100%">
                <PieChart>
                  <Pie
                    data={chartData}
                    cx="50%"
                    cy="45%"
                    innerRadius={60}
                    outerRadius={80}
                    paddingAngle={2}
                    dataKey="value"
                    stroke="var(--bg-primary)"
                    strokeWidth={2}
                  >
                    {chartData.map((entry, index) => (
                      <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                    ))}
                  </Pie>
                  <Tooltip 
                    contentStyle={{ backgroundColor: 'var(--surface)', border: '1px solid var(--border-color)', borderRadius: '4px', color: 'var(--text-primary)' }}
                    itemStyle={{ color: 'var(--text-primary)' }}
                  />
                  <Legend verticalAlign="bottom" height={36} iconType="circle" wrapperStyle={{ color: 'var(--text-primary)' }} />
                </PieChart>
              </ResponsiveContainer>
            ) : (
              <p className="text-slate-500 text-sm">No providers registered</p>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}
