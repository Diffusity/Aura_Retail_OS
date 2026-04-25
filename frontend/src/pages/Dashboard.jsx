import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { getKiosks, getTransactions, getPayments } from '../api';
import { Activity, Server, DollarSign, Plug } from 'lucide-react';

export default function Dashboard() {
  const [kiosks, setKiosks] = useState([]);
  const [stats, setStats] = useState({ totalTx: 0, revenue: 0, providers: 0 });
  const navigate = useNavigate();

  useEffect(() => {
    const loadData = async () => {
      try {
        const [kRes, tRes, pRes] = await Promise.all([
          getKiosks(),
          getTransactions(),
          getPayments()
        ]);
        
        setKiosks(kRes.data || []);
        
        const txns = tRes.data || [];
        const completed = txns.filter(t => t.status === 'COMPLETED');
        const revenue = completed.reduce((sum, t) => sum + t.amount, 0);
        
        setStats({
          totalTx: txns.length,
          revenue: revenue,
          providers: (pRes.data || []).length
        });
      } catch (e) {
        console.error("Failed to load dashboard data", e);
      }
    };
    loadData();
  }, []);

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-gray-900">System Overview</h1>
        <p className="text-gray-500">Aura Retail OS Central Command</p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <StatCard title="Total Kiosks" value={kiosks.length} icon={<Server />} color="bg-blue-500" />
        <StatCard title="Active Kiosks" value={kiosks.filter(k => k.state === 'ACTIVE').length} icon={<Activity />} color="bg-green-500" />
        <StatCard title="Revenue" value={`$${stats.revenue.toFixed(2)}`} icon={<DollarSign />} color="bg-indigo-500" />
        <StatCard title="Payment Providers" value={stats.providers} icon={<Plug />} color="bg-purple-500" />
      </div>

      <div>
        <h2 className="text-xl font-bold text-gray-900 mb-4">Kiosk Fleet Status</h2>
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {kiosks.map(kiosk => (
            <div 
              key={kiosk.kioskId} 
              onClick={() => navigate(`/kiosk/${kiosk.kioskId}`)}
              className="bg-white rounded-xl border border-gray-200 shadow-sm hover:shadow-md hover:border-blue-300 transition-all cursor-pointer p-5"
            >
              <div className="flex justify-between items-start mb-4">
                <div>
                  <h3 className="font-bold text-lg text-gray-900">{kiosk.kioskId}</h3>
                  <p className="text-sm text-gray-500">{kiosk.type}</p>
                </div>
                <StateBadge state={kiosk.state} />
              </div>
              
              <div className="pt-4 border-t border-gray-100 flex justify-between items-center text-sm">
                <span className="text-gray-600">Dispenser Status:</span>
                {kiosk.dispenserOperational ? (
                   <span className="text-green-600 font-medium flex items-center"><div className="w-2 h-2 rounded-full bg-green-500 mr-2"></div> Operational</span>
                ) : (
                   <span className="text-red-600 font-medium flex items-center"><div className="w-2 h-2 rounded-full bg-red-500 mr-2"></div> Offline</span>
                )}
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}

function StatCard({ title, value, icon, color }) {
  return (
    <div className="bg-white rounded-xl border border-gray-200 shadow-sm p-6 flex items-center">
      <div className={`${color} p-4 rounded-lg text-white mr-4`}>
        {icon}
      </div>
      <div>
        <p className="text-sm font-medium text-gray-500">{title}</p>
        <h4 className="text-2xl font-bold text-gray-900">{value}</h4>
      </div>
    </div>
  );
}

export function StateBadge({ state }) {
  const styles = {
    'ACTIVE': 'bg-green-100 text-green-800 border-green-200',
    'POWER_SAVING': 'bg-yellow-100 text-yellow-800 border-yellow-200',
    'MAINTENANCE': 'bg-red-100 text-red-800 border-red-200',
    'EMERGENCY_LOCKDOWN': 'bg-purple-100 text-purple-800 border-purple-200'
  };
  const style = styles[state] || 'bg-gray-100 text-gray-800 border-gray-200';
  
  return (
    <span className={`px-2.5 py-1 rounded-full text-xs font-medium border ${style}`}>
      {state}
    </span>
  );
}
