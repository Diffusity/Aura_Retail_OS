import { NavLink } from 'react-router-dom';
import { LayoutDashboard, Receipt, Play, CreditCard, Activity, Monitor } from 'lucide-react';
import { useState, useEffect } from 'react';
import { getSystemStatus } from '../api';

export default function Sidebar() {
  const [status, setStatus] = useState({ systemMode: 'NORMAL' });

  useEffect(() => {
    const fetchStatus = async () => {
      try {
        const res = await getSystemStatus();
        setStatus(res.data);
      } catch (e) {
        // Silently fail, status will remain whatever it was
      }
    };
    fetchStatus();
    const interval = setInterval(fetchStatus, 5000);
    return () => clearInterval(interval);
  }, []);

  const navItems = [
    { to: "/", icon: LayoutDashboard, label: "Dashboard" },
    { to: "/transactions", icon: Receipt, label: "Transactions" },
    { to: "/simulate", icon: Play, label: "Simulation" },
    { to: "/payments", icon: CreditCard, label: "Payments" },
    { to: "/emulator", icon: Monitor, label: "Emulator" }
  ];

  return (
    <div className="w-64 bg-[var(--surface)] border-r border-[var(--border-color)] flex flex-col transition-colors duration-300">
      <div className="p-6 pb-2">
        <div className="flex items-center gap-3">
          <div className="w-10 h-10 bg-[var(--accent)] text-[var(--accent-text)] rounded flex items-center justify-center font-bold text-xl">
            A
          </div>
          <div>
            <h1 className="text-xl font-bold tracking-widest text-[var(--text-primary)]">AURA</h1>
            <p className="text-[10px] text-[var(--text-secondary)] font-medium tracking-[0.2em] uppercase">Retail OS</p>
          </div>
        </div>
      </div>
      
      <nav className="flex-1 px-4 mt-8 space-y-1">
        {navItems.map((item) => (
          <NavLink 
            key={item.to}
            to={item.to} 
            className={({ isActive }) => `
              flex items-center px-4 py-3 rounded transition-colors
              ${isActive 
                ? 'bg-[var(--accent)] text-[var(--accent-text)] font-semibold' 
                : 'text-[var(--text-secondary)] hover:bg-[var(--bg-secondary)] hover:text-[var(--text-primary)]'
              }
            `}
          >
            {({ isActive }) => (
              <>
                <item.icon className={`w-5 h-5 mr-3`} />
                <span className="text-sm">{item.label}</span>
              </>
            )}
          </NavLink>
        ))}
      </nav>

      {/* System Status Indicator */}
      <div className="p-6 mt-auto">
        <div className="mono-card p-4 flex items-center gap-3">
          <Activity className={`w-5 h-5 ${status.systemMode === 'EMERGENCY' ? 'text-[var(--error)] animate-pulse' : 'text-[var(--success)]'}`} />
          <div>
            <p className="text-xs font-semibold text-[var(--text-secondary)]">System Status</p>
            <p className={`text-[10px] font-bold tracking-wider mt-0.5 ${status.systemMode === 'EMERGENCY' ? 'text-[var(--error)]' : 'text-[var(--success)]'}`}>
              {status.systemMode === 'EMERGENCY' ? 'LOCKDOWN' : 'HEALTHY'}
            </p>
          </div>
        </div>
        <p className="text-center text-[10px] font-medium text-[var(--text-secondary)] mt-4">Aura Retail OS v1.0.0</p>
      </div>
    </div>
  );
}
