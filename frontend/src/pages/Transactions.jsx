import { useState, useEffect } from 'react';
import { getTransactions, refundTransaction } from '../api';
import { Search } from 'lucide-react';

export default function Transactions() {
  const [transactions, setTransactions] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchTx = async () => {
      try {
        const res = await getTransactions();
        setTransactions(res.data || []);
      } catch (e) {
        console.error(e);
      } finally {
        setLoading(false);
      }
    };
    fetchTx();
  }, []);

  const handleRefund = async (id) => {
    try {
      await refundTransaction(id);
      alert('Refund processed');
      setTransactions(transactions.map(t => t.txnId === id ? { ...t, status: 'REFUNDED' } : t));
    } catch (e) {
      alert('Refund failed');
    }
  };

  const getStatusStyle = (status) => {
    if (status === 'COMPLETED') return 'bg-green-100 text-green-800';
    if (status === 'REFUNDED') return 'bg-orange-100 text-orange-800';
    if (status === 'FAILED') return 'bg-red-100 text-red-800';
    return 'bg-gray-100 text-gray-800';
  };

  if (loading) return <div className="p-10 text-center">Loading transactions...</div>;

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-end">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Transaction History</h1>
          <p className="text-gray-500">View and manage all system transactions</p>
        </div>
        <div className="relative">
          <input type="text" placeholder="Search TXN ID..." className="pl-10 pr-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500" />
          <Search className="w-5 h-5 text-gray-400 absolute left-3 top-2.5" />
        </div>
      </div>

      <div className="bg-white rounded-xl shadow-sm border border-gray-200 overflow-hidden">
        <table className="min-w-full divide-y divide-gray-200">
          <thead className="bg-gray-50">
            <tr>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Transaction ID</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Date</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Product</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Amount</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Status</th>
              <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
            </tr>
          </thead>
          <tbody className="bg-white divide-y divide-gray-200">
            {transactions.map(tx => (
              <tr key={tx.txnId} className="hover:bg-gray-50">
                <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">{tx.txnId}</td>
                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{new Date(tx.timestamp).toLocaleString()}</td>
                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{tx.productId} (x{tx.qty})</td>
                <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">${tx.amount.toFixed(2)}</td>
                <td className="px-6 py-4 whitespace-nowrap text-sm">
                  <span className={`px-2 inline-flex text-xs leading-5 font-semibold rounded-full ${getStatusStyle(tx.status)}`}>
                    {tx.status}
                  </span>
                </td>
                <td className="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                  {tx.status === 'COMPLETED' && (
                    <button onClick={() => handleRefund(tx.txnId)} className="text-blue-600 hover:text-blue-900">Refund</button>
                  )}
                </td>
              </tr>
            ))}
            {transactions.length === 0 && (
              <tr>
                <td colSpan="6" className="px-6 py-8 text-center text-gray-500">No transactions found</td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}
