import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { getKioskDetail, getInventory } from '../api';
import { useToast } from '../context/ToastContext';
import LoadingSpinner from '../components/LoadingSpinner';
import ErrorBanner from '../components/ErrorBanner';
import { 
  ArrowLeft, Cpu, Package, Server, Activity, AlertTriangle, CheckCircle, ListTree
} from 'lucide-react';

export default function KioskDetail() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { addToast } = useToast();
  const [data, setData] = useState(null);
  const [inventory, setInventory] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);

  const fetchData = () => {
    setLoading(true);
    setError(false);
    Promise.all([getKioskDetail(id), getInventory(id)])
      .then(([kioskRes, invRes]) => {
        setData(kioskRes.data);
        if (invRes.data?.inventory) setInventory(invRes.data.inventory);
        else if (kioskRes.data?.inventory) setInventory(kioskRes.data.inventory);
        setLoading(false);
      })
      .catch(() => {
        setError(true);
        setLoading(false);
      });
  };

  useEffect(() => {
    fetchData();
  }, [id]);

  const handleAction = (action) => {
    addToast(`${action} command sent to ${id}`, "info");
  };

  if (loading) return <LoadingSpinner text="Loading kiosk diagnostics..." />;
  if (error || !data) return <ErrorBanner message={`Failed to load diagnostics for ${id}`} onRetry={fetchData} />;

  const isHealthy = data.state === 'ACTIVE' && data.dispenserOperational;

  return (
    <div className="space-y-6">
      <div className="flex items-center gap-4">
        <button 
          onClick={() => navigate('/')}
          className="p-2 rounded border border-[var(--border-color)] bg-[var(--surface)] text-[var(--text-secondary)] hover:text-[var(--text-primary)] transition-colors"
        >
          <ArrowLeft className="w-5 h-5" />
        </button>
        <div>
          <h1 className="text-2xl font-bold text-[var(--text-primary)] tracking-tight flex items-center gap-3">
            {id} Diagnostics
            <span className={`px-2 py-0.5 text-[10px] rounded font-bold tracking-wider border ${
              isHealthy ? 'text-[var(--success)] border-[var(--success)]' : 'text-[var(--error)] border-[var(--error)]'
            }`}>
              {isHealthy ? 'NOMINAL' : 'ATTENTION'}
            </span>
          </h1>
          <p className="text-sm text-[var(--text-secondary)] mt-1">{data.typeName || data.type}</p>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div className="mono-card p-6">
          <h2 className="text-lg font-semibold text-[var(--text-primary)] flex items-center gap-2 mb-6">
            <Cpu className="w-5 h-5 text-[var(--text-secondary)]" /> Hardware Configuration
          </h2>
          
          <div className="space-y-4">
            <div className="flex items-center justify-between p-4 rounded bg-[var(--bg-secondary)] border border-[var(--border-color)]">
              <div className="flex items-center gap-3">
                <Server className="w-5 h-5 text-[var(--text-secondary)]" />
                <div>
                  <p className="text-sm font-medium text-[var(--text-primary)]">Dispenser Unit</p>
                  <p className="text-xs text-[var(--text-secondary)] mt-0.5">{data.dispenserType || 'Standard'}</p>
                </div>
              </div>
              {data.dispenserOperational ? (
                <CheckCircle className="w-5 h-5 text-[var(--success)]" />
              ) : (
                <AlertTriangle className="w-5 h-5 text-[var(--error)]" />
              )}
            </div>

            {data.hardwareModules?.map((mod, i) => (
              <div key={i} className="flex items-center justify-between p-4 rounded bg-[var(--bg-secondary)] border border-[var(--border-color)]">
                <div className="flex items-center gap-3">
                  <Activity className="w-5 h-5 text-[var(--text-secondary)]" />
                  <div>
                    <p className="text-sm font-medium text-[var(--text-primary)]">{mod.replace(/([A-Z])/g, ' $1').trim()}</p>
                    <p className="text-xs text-[var(--text-secondary)] mt-0.5">Decorator Module</p>
                  </div>
                </div>
                <span className="text-xs font-bold text-[var(--success)]">ONLINE</span>
              </div>
            ))}
          </div>
          
          <div className="mt-6 flex gap-3">
            <button onClick={() => handleAction('Reboot')} className="flex-1 py-2 rounded font-medium text-sm border border-[var(--border-color)] text-[var(--text-primary)] hover:bg-[var(--bg-secondary)] transition-colors">
              Restart System
            </button>
            <button onClick={() => handleAction('Self-Test')} className="flex-1 py-2 rounded font-medium text-sm bg-[var(--text-primary)] text-[var(--bg-primary)] hover:opacity-90 transition-opacity">
              Run Diagnostics
            </button>
          </div>
        </div>

        <div className="mono-card p-6">
          <div className="flex justify-between items-start mb-6">
            <h2 className="text-lg font-semibold text-[var(--text-primary)] flex items-center gap-2">
              <ListTree className="w-5 h-5 text-[var(--text-secondary)]" /> Inventory Directory
            </h2>
            <span className="text-xs font-mono text-[var(--text-secondary)] border border-[var(--border-color)] px-2 py-1 rounded">Composite</span>
          </div>
          
          <div className="space-y-2 max-h-[350px] overflow-y-auto custom-scrollbar pr-2">
            <div className="pl-2 border-l border-[var(--border-color)] ml-2 pb-2">
              <div className="flex items-center gap-2 text-[var(--text-primary)] font-medium mb-3">
                <Package className="w-4 h-4 text-[var(--text-secondary)]" /> Root Catalog
              </div>
              
              {inventory.map((item, i) => (
                <div key={i} className="flex items-center justify-between pl-6 py-2 border-l border-[var(--border-color)] ml-2 relative">
                  <div className="absolute w-4 border-b border-[var(--border-color)] left-0 top-1/2 -translate-y-1/2"></div>
                  <div>
                    <p className="text-sm text-[var(--text-primary)]">{item.name}</p>
                    <p className="text-[10px] text-[var(--text-secondary)] font-mono mt-0.5">{item.id}</p>
                  </div>
                  <div className="flex items-center gap-4">
                    <span className="text-xs text-[var(--text-secondary)]">${item.price?.toFixed(2)}</span>
                    <span className={`text-xs font-bold w-12 text-right ${item.stock < 10 ? 'text-[var(--error)]' : 'text-[var(--success)]'}`}>
                      {item.stock} qty
                    </span>
                  </div>
                </div>
              ))}
              
              {inventory.length === 0 && (
                <div className="pl-6 text-sm text-[var(--text-secondary)]">No inventory data available.</div>
              )}
            </div>
          </div>
          
          <button onClick={() => handleAction('Restock')} className="w-full mt-6 py-2 rounded font-medium text-sm bg-[var(--text-primary)] text-[var(--bg-primary)] hover:opacity-90 transition-opacity">
            Trigger Restock Command
          </button>
        </div>
      </div>
    </div>
  );
}
