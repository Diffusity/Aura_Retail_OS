import { useState, useEffect } from 'react';
import { ShieldAlert, Clock, Sun, Moon } from 'lucide-react';
import { toggleSystemMode, getSystemStatus } from '../api';
import { useToast } from '../context/ToastContext';
import { useTheme } from '../context/ThemeContext';

export default function TopBar() {
  const [mode, setMode] = useState('NORMAL');
  const [time, setTime] = useState(new Date());
  const { addToast } = useToast();
  const { theme, toggleTheme } = useTheme();

  useEffect(() => {
    const timer = setInterval(() => setTime(new Date()), 1000);
    return () => clearInterval(timer);
  }, []);

  useEffect(() => {
    const fetchMode = async () => {
      try {
        const res = await getSystemStatus();
        setMode(res.data.systemMode);
      } catch (e) {}
    };
    fetchMode();
    const modeTimer = setInterval(fetchMode, 5000);
    return () => clearInterval(modeTimer);
  }, []);

  const handleToggle = async () => {
    const newMode = mode === 'NORMAL' ? 'EMERGENCY' : 'NORMAL';
    try {
      const res = await toggleSystemMode(newMode);
      setMode(res.data.systemMode);
      if (newMode === 'EMERGENCY') {
        addToast('Emergency Lockdown Activated!', 'error');
      } else {
        addToast('System returned to Normal operation.', 'success');
      }
    } catch (e) {
      addToast('Failed to toggle system mode', 'error');
    }
  };

  return (
    <header className={`h-16 flex items-center justify-between px-6 border-b transition-colors duration-300
      ${mode === 'EMERGENCY' 
        ? 'bg-[#fee2e2] dark:bg-[#450a0a] border-[var(--error)]' 
        : 'bg-[var(--surface)] border-[var(--border-color)]'}
    `}>
      <div className="flex items-center gap-4">
        {mode === 'EMERGENCY' ? (
          <div className="flex items-center gap-2 text-[var(--error)]">
            <ShieldAlert className="w-5 h-5 animate-pulse" />
            <h2 className="text-sm font-bold tracking-widest uppercase">Emergency Lockdown</h2>
          </div>
        ) : (
          <h2 className="text-sm font-semibold text-[var(--text-primary)]">Command Center</h2>
        )}
      </div>
      
      <div className="flex items-center gap-6">
        <div className="hidden md:flex items-center gap-2 text-[var(--text-secondary)]">
          <Clock className="w-4 h-4" />
          <span className="text-xs font-mono font-medium tracking-wider">{time.toLocaleTimeString()}</span>
        </div>
        
        <button 
          onClick={toggleTheme}
          className="p-2 rounded text-[var(--text-secondary)] hover:bg-[var(--bg-secondary)] transition-colors"
          title="Toggle Theme"
        >
          {theme === 'light' ? <Moon className="w-5 h-5" /> : <Sun className="w-5 h-5" />}
        </button>

        <button 
          onClick={handleToggle} 
          className={`px-4 py-1.5 rounded font-medium text-sm transition-colors flex items-center gap-2 border
            ${mode === 'EMERGENCY' 
              ? 'bg-[var(--error)] text-white border-[var(--error)] hover:opacity-90' 
              : 'bg-transparent text-[var(--text-primary)] border-[var(--border-color)] hover:bg-[var(--bg-secondary)]'}
          `}
        >
          {mode === 'EMERGENCY' ? 'Deactivate Emergency' : 'Trigger Emergency'}
        </button>
      </div>
    </header>
  );
}
