import { useState, useEffect } from 'react';
import { getTransactions, refundTransaction } from '../api';
import { useToast } from '../context/ToastContext';
import ConfirmModal from '../components/ConfirmModal';
import LoadingSpinner from '../components/LoadingSpinner';
import ErrorBanner from '../components/ErrorBanner';
import AnimatedCounter from '../components/AnimatedCounter';
import { Search, ChevronLeft, ChevronRight, DollarSign, Ban, ArrowRightLeft } from 'lucide-react';

export default function Transactions() {
  const [txns, setTxns] = useState([]);
  const [searchTerm, setSearchTerm] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);
  const [currentPage, setCurrentPage] = useState(1);
  const [refundTarget, setRefundTarget] = useState(null);
  const { addToast } = useToast();
  
  const ITEMS_PER_PAGE = 8;

  const fetchTxns = () => {
    setLoading(true);
    setError(false);
    getTransactions()
      .then(res => { setTxns(res.data); setLoading(false); })
      .catch(() => { setError(true); setLoading(false); });
  };

  useEffect(() => { fetchTxns(); }, []);

  const handleRefund = async () => {
    if (!refundTarget) return;
    try {
      await refundTransaction(refundTarget);
      addToast(`Refund processed for ${refundTarget}`, 'success');
      fetchTxns();
    } catch (e) {
      addToast(`Failed to refund ${refundTarget}`, 'error');
    }
  };

  if (loading) return <LoadingSpinner text="Loading ledger..." />;
  if (error) return <ErrorBanner message="Failed to load transactions." onRetry={fetchTxns} />;

  const filtered = txns.filter(t => 
    t.txnId?.toLowerCase().includes(searchTerm.toLowerCase()) ||
    t.productId?.toLowerCase().includes(searchTerm.toLowerCase()) ||
    t.userId?.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const totalPages = Math.ceil(filtered.length / ITEMS_PER_PAGE) || 1;
  const paginated = filtered.slice((currentPage - 1) * ITEMS_PER_PAGE, currentPage * ITEMS_PER_PAGE);

  const totalRevenue = txns.filter(t => t.status === 'COMPLETED').reduce((acc, t) => acc + (t.amount || 0), 0);
  const refundCount = txns.filter(t => t.status === 'REFUNDED').length;

  return (
    <div className="space-y-6">
      <div className="flex items-end justify-between">
        <div>
          <h1 className="text-2xl font-bold text-[var(--text-primary)] tracking-tight">Transaction Ledger</h1>
          <p className="text-sm text-[var(--text-secondary)] mt-1">Immutable record of all system exchanges</p>
        </div>
        <div className="relative">
          <Search className="w-4 h-4 text-[var(--text-secondary)] absolute left-3 top-1/2 -translate-y-1/2" />
          <input 
            type="text" 
            placeholder="Search TXN..." 
            value={searchTerm}
            onChange={(e) => { setSearchTerm(e.target.value); setCurrentPage(1); }}
            className="pl-9 pr-4 py-2 bg-[var(--surface)] border border-[var(--border-color)] rounded text-sm text-[var(--text-primary)] focus:outline-none focus:border-[var(--text-primary)] transition-colors w-64"
          />
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <div className="mono-card p-4 flex items-center gap-4">
          <div className="p-2 border border-[var(--border-color)] rounded text-[var(--text-secondary)]"><ArrowRightLeft className="w-5 h-5" /></div>
          <div><p className="text-xs text-[var(--text-secondary)] font-medium">Total Volume</p><p className="text-xl font-bold text-[var(--text-primary)]">{txns.length}</p></div>
        </div>
        <div className="mono-card p-4 flex items-center gap-4">
          <div className="p-2 border border-[var(--border-color)] rounded text-[var(--text-secondary)]"><DollarSign className="w-5 h-5" /></div>
          <div><p className="text-xs text-[var(--text-secondary)] font-medium">Gross Revenue</p><p className="text-xl font-bold text-[var(--text-primary)]"><AnimatedCounter value={totalRevenue} prefix="$" duration={1} /></p></div>
        </div>
        <div className="mono-card p-4 flex items-center gap-4">
          <div className="p-2 border border-[var(--border-color)] rounded text-[var(--text-secondary)]"><Ban className="w-5 h-5" /></div>
          <div><p className="text-xs text-[var(--text-secondary)] font-medium">Refunds Issued</p><p className="text-xl font-bold text-[var(--text-primary)]">{refundCount}</p></div>
        </div>
      </div>

      <div className="mono-card overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-left text-sm">
            <thead className="bg-[var(--bg-secondary)] text-[var(--text-secondary)] text-xs uppercase font-semibold border-b border-[var(--border-color)]">
              <tr>
                <th className="px-6 py-4">Transaction ID</th>
                <th className="px-6 py-4">Timestamp</th>
                <th className="px-6 py-4">User</th>
                <th className="px-6 py-4">Item</th>
                <th className="px-6 py-4 text-right">Amount</th>
                <th className="px-6 py-4 text-center">Status</th>
                <th className="px-6 py-4 text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-[var(--border-color)]">
              {paginated.length === 0 ? (
                <tr><td colSpan="7" className="px-6 py-8 text-center text-[var(--text-secondary)]">No transactions found</td></tr>
              ) : paginated.map(t => (
                <tr key={t.txnId} className="hover:bg-[var(--bg-secondary)] transition-colors">
                  <td className="px-6 py-4 font-mono text-[var(--text-primary)]">{t.txnId}</td>
                  <td className="px-6 py-4 text-[var(--text-secondary)]">{new Date(t.timestamp).toLocaleString([], { dateStyle: 'short', timeStyle: 'short' })}</td>
                  <td className="px-6 py-4 text-[var(--text-primary)]">{t.userId}</td>
                  <td className="px-6 py-4">
                    <span className="font-mono text-xs text-[var(--text-secondary)] mr-2">{t.productId}</span>
                    <span className="text-[var(--text-primary)] text-xs">x{t.qty}</span>
                  </td>
                  <td className="px-6 py-4 font-mono text-right text-[var(--text-primary)]">${(t.amount || 0).toFixed(2)}</td>
                  <td className="px-6 py-4 text-center">
                    <span className={`inline-block px-2 py-0.5 rounded text-[10px] font-bold border ${
                      t.status === 'COMPLETED' ? 'text-[var(--success)] border-[var(--success)]' :
                      'text-[var(--error)] border-[var(--error)]'
                    }`}>
                      {t.status}
                    </span>
                  </td>
                  <td className="px-6 py-4 text-right">
                    {t.status === 'COMPLETED' && (
                      <button 
                        onClick={() => setRefundTarget(t.txnId)}
                        className="text-xs font-medium text-[var(--error)] hover:underline"
                      >
                        Issue Refund
                      </button>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
        
        <div className="border-t border-[var(--border-color)] p-4 flex items-center justify-between bg-[var(--bg-secondary)]">
          <p className="text-xs text-[var(--text-secondary)]">
            Showing <span className="font-medium text-[var(--text-primary)]">{(currentPage - 1) * ITEMS_PER_PAGE + 1}</span> to <span className="font-medium text-[var(--text-primary)]">{Math.min(currentPage * ITEMS_PER_PAGE, filtered.length)}</span> of <span className="font-medium text-[var(--text-primary)]">{filtered.length}</span> results
          </p>
          <div className="flex gap-2">
            <button 
              disabled={currentPage === 1} 
              onClick={() => setCurrentPage(p => p - 1)}
              className="p-1 rounded border border-[var(--border-color)] text-[var(--text-secondary)] hover:text-[var(--text-primary)] disabled:opacity-50"
            >
              <ChevronLeft className="w-5 h-5" />
            </button>
            <button 
              disabled={currentPage === totalPages} 
              onClick={() => setCurrentPage(p => p + 1)}
              className="p-1 rounded border border-[var(--border-color)] text-[var(--text-secondary)] hover:text-[var(--text-primary)] disabled:opacity-50"
            >
              <ChevronRight className="w-5 h-5" />
            </button>
          </div>
        </div>
      </div>

      <ConfirmModal 
        isOpen={!!refundTarget}
        onClose={() => setRefundTarget(null)}
        onConfirm={handleRefund}
        title="Confirm Refund"
        message={`Are you sure you want to issue a refund for transaction ${refundTarget}?`}
        confirmText="Issue Refund"
      />
    </div>
  );
}
