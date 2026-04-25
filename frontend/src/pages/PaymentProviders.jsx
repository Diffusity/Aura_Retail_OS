import { useState, useEffect } from 'react';
import { getPayments, registerProvider } from '../api';
import { CreditCard, Plus } from 'lucide-react';

export default function PaymentProviders() {
  const [providers, setProviders] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchProviders = async () => {
      try {
        const res = await getPayments();
        setProviders(res.data || []);
      } catch (e) {
        console.error(e);
      } finally {
        setLoading(false);
      }
    };
    fetchProviders();
  }, []);

  const handleRegisterCrypto = async () => {
    try {
      const res = await registerProvider('Crypto');
      alert(`Successfully registered ${res.data.registered}`);
      setProviders([...providers, { name: 'Crypto' }]);
    } catch (e) {
      alert('Registration failed');
    }
  };

  if (loading) return <div className="p-10 text-center">Loading providers...</div>;

  return (
    <div className="space-y-6 max-w-4xl mx-auto">
      <div>
        <h1 className="text-2xl font-bold text-gray-900">Payment Providers</h1>
        <p className="text-gray-500">Manage registered payment adapters</p>
      </div>

      <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-4">
        {providers.map((p, i) => (
          <div key={i} className="bg-white rounded-xl border border-gray-200 shadow-sm p-6 flex items-center">
            <div className="p-3 rounded-full bg-blue-50 text-blue-600 mr-4">
              <CreditCard className="w-6 h-6" />
            </div>
            <div>
              <h3 className="font-bold text-gray-900">{p.name}</h3>
              <p className="text-xs text-green-600 font-medium">Active</p>
            </div>
          </div>
        ))}
        
        {/* Add Provider Card */}
        <button 
          onClick={handleRegisterCrypto}
          disabled={providers.some(p => p.name === 'Crypto')}
          className="bg-gray-50 border-2 border-dashed border-gray-300 rounded-xl flex flex-col items-center justify-center p-6 text-gray-500 hover:text-blue-600 hover:border-blue-400 transition-colors disabled:opacity-50 disabled:hover:border-gray-300 disabled:hover:text-gray-500"
        >
          <Plus className="w-8 h-8 mb-2" />
          <span className="font-medium">Register Crypto Adapter</span>
          <span className="text-xs mt-1 text-center">(Scenario B Demo)</span>
        </button>
      </div>

    </div>
  );
}
