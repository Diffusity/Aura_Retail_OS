import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { getKiosks, getSystemStatus } from '../api';
import AnimatedCounter from '../components/AnimatedCounter';
import LoadingSpinner from '../components/LoadingSpinner';
import ErrorBanner from '../components/ErrorBanner';
import { useTheme } from '../context/ThemeContext';
import { 
  Server, MonitorPlay, Activity, Layers, Package, Database, 
  AlertTriangle, CheckCircle2, ChevronRight, Pill, Utensils
} from 'lucide-react';
import { AreaChart, Area, XAxis, YAxis, Tooltip, ResponsiveContainer } from 'recharts';

const chartData = [
  { time: '08:00', txns: 12 }, { time: '09:00', txns: 25 },
  { time: '10:00', txns: 45 }, { time: '11:00', txns: 30 },
  { time: '12:00', txns: 65 }, { time: '13:00', txns: 80 },
  { time: '14:00', txns: 50 }
];

export default function Dashboard() {
  const [kiosks, setKiosks] = useState([]);
  const [status, setStatus] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);
  const navigate = useNavigate();
  const { theme } = useTheme();

  const fetchData = () => {
    setLoading(true);
    setError(false);
    Promise.all([getKiosks(), getSystemStatus()])
      .then(([kioskRes, statusRes]) => {
        setKiosks(kioskRes.data);
        setStatus(statusRes.data);
        setLoading(false);
      })
      .catch(() => {
        setError(true);
        setLoading(false);
      });
  };

  useEffect(() => {
    fetchData();
  }, []);

  if (loading) return <LoadingSpinner text="Initializing Dashboard..." />;
  if (error || !status) return <ErrorBanner message="Unable to connect to the Aura Retail OS backend." onRetry={fetchData} />;

  const activeKiosks = kiosks.filter(k => k.state === 'ACTIVE').length;
  
  const getKioskIcon = (type) => {
    if (type.includes('Food')) return <Utensils className="w-5 h-5" />;
    if (type.includes('Pharmacy')) return <Pill className="w-5 h-5" />;
    return <AlertTriangle className="w-5 h-5" />;
  };

  const chartColor = theme === 'dark' ? '#ffffff' : '#000000';

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-[var(--text-primary)] tracking-tight">System Overview</h1>
          <p className="text-sm text-[var(--text-secondary)] mt-1">Real-time metrics and fleet status</p>
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        {[
          { label: "Total Kiosks", val: status.totalKiosks, icon: Server },
          { label: "Active Fleet", val: activeKiosks, icon: MonitorPlay },
          { label: "Uptime (h)", val: (status.uptimeSeconds / 3600).toFixed(1), icon: Activity },
          { label: "Total Txns", val: status.totalTransactions, icon: Database }
        ].map((s, i) => (
          <div key={i} className="mono-card p-5">
            <div className="flex items-start justify-between">
              <div>
                <p className="text-sm font-medium text-[var(--text-secondary)]">{s.label}</p>
                <h3 className="text-3xl font-bold text-[var(--text-primary)] mt-2 tracking-tight">
                  <AnimatedCounter value={s.val} />
                </h3>
              </div>
              <div className="p-2 rounded bg-[var(--bg-secondary)] text-[var(--text-secondary)]">
                <s.icon className="w-5 h-5" />
              </div>
            </div>
          </div>
        ))}
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="lg:col-span-2 space-y-4">
          <h2 className="text-lg font-semibold text-[var(--text-primary)] flex items-center gap-2">
            <Layers className="w-5 h-5 text-[var(--text-secondary)]" /> Kiosk Fleet
          </h2>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            {kiosks.map(k => (
              <div 
                key={k.kioskId} 
                onClick={() => navigate(`/kiosk/${k.kioskId}`)}
                className="mono-card p-5 cursor-pointer hover:border-[var(--text-primary)] group"
              >
                <div className="flex justify-between items-start mb-4">
                  <div className="flex items-center gap-3">
                    <div className="p-2 rounded bg-[var(--bg-secondary)] text-[var(--text-secondary)]">
                      {getKioskIcon(k.type)}
                    </div>
                    <div>
                      <h3 className="text-lg font-bold text-[var(--text-primary)]">{k.kioskId}</h3>
                      <p className="text-xs text-[var(--text-secondary)]">{k.typeName || k.type}</p>
                    </div>
                  </div>
                  <div className="flex items-center gap-1.5 px-2 py-1 rounded border border-[var(--border-color)]">
                    <div className={`w-2 h-2 rounded-full ${k.state === 'ACTIVE' ? 'bg-[var(--success)]' : 'bg-[var(--error)]'}`} />
                    <span className="text-[10px] font-bold tracking-wider text-[var(--text-secondary)]">{k.state}</span>
                  </div>
                </div>
                
                <div className="flex items-center justify-between text-sm mt-6 pt-4 border-t border-[var(--border-color)]">
                  <div className="flex items-center gap-2 text-[var(--text-secondary)]">
                    <Package className="w-4 h-4" />
                    <span>{k.hardwareModules?.length || 1} Modules</span>
                  </div>
                  <ChevronRight className="w-4 h-4 text-[var(--text-secondary)] group-hover:text-[var(--text-primary)] transition-colors" />
                </div>
              </div>
            ))}
          </div>
        </div>

        <div className="space-y-4">
          <h2 className="text-lg font-semibold text-[var(--text-primary)] flex items-center gap-2">
            <Activity className="w-5 h-5 text-[var(--text-secondary)]" /> Today's Volume
          </h2>
          <div className="mono-card p-5 h-[300px] flex flex-col">
            <div className="mb-4">
              <h3 className="text-2xl font-bold text-[var(--text-primary)]">307 <span className="text-sm font-medium text-[var(--text-secondary)]">txns</span></h3>
              <p className="text-xs text-[var(--success)] flex items-center gap-1 mt-1">
                <CheckCircle2 className="w-3 h-3" /> +12% from yesterday
              </p>
            </div>
            <div className="flex-1 -mx-2">
              <ResponsiveContainer width="100%" height="100%">
                <AreaChart data={chartData} margin={{ top: 10, right: 10, left: -20, bottom: 0 }}>
                  <XAxis dataKey="time" stroke="var(--text-secondary)" fontSize={10} tickLine={false} axisLine={false} />
                  <YAxis stroke="var(--text-secondary)" fontSize={10} tickLine={false} axisLine={false} />
                  <Tooltip 
                    contentStyle={{ backgroundColor: 'var(--surface)', border: '1px solid var(--border-color)', borderRadius: '4px', color: 'var(--text-primary)' }}
                    itemStyle={{ color: 'var(--text-primary)' }}
                  />
                  <Area type="monotone" dataKey="txns" stroke={chartColor} strokeWidth={2} fillOpacity={0.1} fill={chartColor} />
                </AreaChart>
              </ResponsiveContainer>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
