import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { getKioskDetail, restockItem, purchaseItem } from '../api';
import { StateBadge } from './Dashboard';
import { ArrowLeft, Box, Server, Settings, Zap } from 'lucide-react';

export default function KioskDetail() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchDetail = async () => {
      try {
        const res = await getKioskDetail(id);
        setData(res.data);
      } catch (e) {
        console.error(e);
      } finally {
        setLoading(false);
      }
    };
    fetchDetail();
  }, [id]);

  if (loading) return <div className="p-10 text-center">Loading...</div>;
  if (!data) return <div className="p-10 text-center text-red-500">Kiosk not found or not registered.</div>;

  const mockInventory = [
    { id: 'FOOD-001', name: 'Energy Bar', stock: 50, price: 3.50 },
    { id: 'MED-001', name: 'Paracetamol', stock: 20, price: 1.50 }
  ];

  const handleAction = async (action, productId) => {
    try {
      if (action === 'restock') {
        await restockItem(id, productId, 10);
        alert('Restock request sent!');
      } else {
        await purchaseItem(id, 'user_demo', productId, 1);
        alert('Purchase request sent!');
      }
    } catch (e) {
      alert('Action failed: ' + e.message);
    }
  };

  return (
    <div className="max-w-5xl mx-auto space-y-6">
      <div className="flex items-center space-x-4 mb-8">
        <button onClick={() => navigate('/')} className="p-2 hover:bg-gray-200 rounded-full transition-colors">
          <ArrowLeft className="w-5 h-5 text-gray-600" />
        </button>
        <div>
          <h1 className="text-2xl font-bold text-gray-900 flex items-center">
            {id} <span className="ml-4"><StateBadge state={data.state || data.currentState} /></span>
          </h1>
          <p className="text-gray-500">Kiosk Diagnostics & Hardware Control</p>
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        
        {/* Hardware Status Panel */}
        <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
          <div className="flex items-center mb-4 text-blue-600">
            <Server className="w-5 h-5 mr-2" />
            <h3 className="font-bold text-lg text-gray-900">Hardware Subsystem</h3>
          </div>
          
          <div className="space-y-4">
            <div className="flex justify-between items-center py-2 border-b border-gray-100">
              <span className="text-gray-600">Operational Status</span>
              {data.operationalStatus !== false ? 
                <span className="text-green-600 font-medium">ONLINE</span> : 
                <span className="text-red-600 font-medium">OFFLINE</span>}
            </div>
            <div className="flex justify-between items-center py-2 border-b border-gray-100">
              <span className="text-gray-600">Dispenser</span>
              <span className="font-medium">{data.dispenserOperational !== false ? 'Operational' : 'Fault'}</span>
            </div>
            
            {/* Decorator Panel */}
            <div className="mt-6">
              <h4 className="text-sm font-semibold text-gray-500 uppercase tracking-wider mb-3">Attached Modules (Decorator Pattern)</h4>
              <div className="flex flex-wrap gap-2">
                {id.startsWith('F-') && <span className="px-3 py-1 bg-blue-50 text-blue-700 rounded border border-blue-200 text-sm flex items-center"><Zap className="w-4 h-4 mr-1"/> Refrigeration</span>}
                <span className="px-3 py-1 bg-slate-50 text-slate-700 rounded border border-slate-200 text-sm flex items-center"><Settings className="w-4 h-4 mr-1"/> Base Dispenser</span>
              </div>
            </div>
          </div>
        </div>

        {/* Inventory Control */}
        <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
          <div className="flex items-center mb-4 text-orange-600">
            <Box className="w-5 h-5 mr-2" />
            <h3 className="font-bold text-lg text-gray-900">Inventory Control</h3>
          </div>
          
          <div className="space-y-3 mt-4">
            {mockInventory.map(item => (
              <div key={item.id} className="flex justify-between items-center p-3 bg-gray-50 rounded-lg border border-gray-100">
                <div>
                  <p className="font-semibold text-gray-900">{item.name}</p>
                  <p className="text-xs text-gray-500">{item.id} · Stock: {item.stock}</p>
                </div>
                <div className="space-x-2">
                  <button onClick={() => handleAction('purchase', item.id)} className="px-3 py-1 bg-blue-600 text-white rounded text-sm hover:bg-blue-700 transition">Buy</button>
                  <button onClick={() => handleAction('restock', item.id)} className="px-3 py-1 bg-white border border-gray-300 text-gray-700 rounded text-sm hover:bg-gray-50 transition">Restock</button>
                </div>
              </div>
            ))}
          </div>
        </div>

      </div>
    </div>
  );
}
