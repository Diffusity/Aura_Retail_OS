import { useState, useEffect } from 'react';
import { ShieldAlert } from 'lucide-react';
import { toggleSystemMode } from '../api';

export default function TopBar() {
  const [mode, setMode] = useState('NORMAL');

  const handleToggle = async () => {
    const newMode = mode === 'NORMAL' ? 'EMERGENCY' : 'NORMAL';
    try {
      const res = await toggleSystemMode(newMode);
      setMode(res.data.systemMode);
    } catch (e) {
      console.error(e);
    }
  };

  return (
    <header className={`h-16 flex items-center justify-between px-6 border-b transition-colors ${mode === 'EMERGENCY' ? 'bg-red-600 text-white border-red-700' : 'bg-white border-gray-200'}`}>
      <div className="flex items-center">
        {mode === 'EMERGENCY' && <ShieldAlert className="w-6 h-6 mr-2 animate-pulse" />}
        <h2 className="text-lg font-semibold">{mode === 'EMERGENCY' ? 'SYSTEM IN EMERGENCY LOCKDOWN' : 'System Normal'}</h2>
      </div>
      <div>
        <button onClick={handleToggle} className={`px-4 py-2 rounded font-medium transition-colors ${mode === 'EMERGENCY' ? 'bg-white text-red-600 hover:bg-gray-100' : 'bg-red-600 text-white hover:bg-red-700'}`}>
          {mode === 'EMERGENCY' ? 'Deactivate Emergency Mode' : 'Trigger Emergency Mode'}
        </button>
      </div>
    </header>
  );
}
