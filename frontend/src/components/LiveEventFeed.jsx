import { useState, useEffect, useRef } from 'react';
import { getEvents } from '../api';
import { Activity, RefreshCw, Trash2, Terminal, Monitor } from 'lucide-react';
import { motion, AnimatePresence } from 'framer-motion';

export default function LiveEventFeed() {
  const [events, setEvents] = useState([]);
  const bottomRef = useRef(null);

  useEffect(() => {
    const fetchEvents = async () => {
      try {
        const res = await getEvents();
        if (res.data && res.data.length > 0) {
           setEvents(prev => {
             const newEvents = res.data.filter(ne => !prev.some(pe => pe.timestamp === ne.timestamp && pe.type === ne.type));
             return [...prev, ...newEvents].slice(-80);
           });
        }
      } catch (e) {}
    };
    
    fetchEvents();
    const interval = setInterval(fetchEvents, 2000);
    return () => clearInterval(interval);
  }, []);

  useEffect(() => {
    bottomRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [events]);

  const clearEvents = () => setEvents([]);

  const formatRelativeTime = (timestamp) => {
    const diffInSeconds = Math.floor((Date.now() - timestamp) / 1000);
    if (diffInSeconds < 5) return 'just now';
    if (diffInSeconds < 60) return `${diffInSeconds}s ago`;
    const diffInMinutes = Math.floor(diffInSeconds / 60);
    if (diffInMinutes < 60) return `${diffInMinutes}m ago`;
    return new Date(timestamp).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
  };

  const getEventIcon = (type) => {
    if (type === 'SimulationLogEvent') return <Terminal className="w-3 h-3" />;
    if (type === 'EmulatorStepEvent') return <Monitor className="w-3 h-3" />;
    return <Activity className="w-3 h-3" />;
  };

  const getEventLabel = (ev) => {
    if (ev.type === 'SimulationLogEvent') return 'Simulation';
    if (ev.type === 'EmulatorStepEvent') return 'Emulator';
    return ev.type.replace('Event', '');
  };

  const getEventBody = (ev) => {
    if (ev.type === 'SimulationLogEvent') {
      return ev.data?.message || JSON.stringify(ev.data);
    }
    if (ev.type === 'EmulatorStepEvent') {
      return ev.data?.message || JSON.stringify(ev.data);
    }
    // For other events, show a compact JSON view
    return JSON.stringify(ev.data, null, 2).replace(/[{}]/g, '').trim();
  };

  const getEventAccent = (ev) => {
    if (ev.type === 'SimulationLogEvent') {
      const level = ev.data?.level;
      if (level === 'success') return 'border-l-[var(--success)]';
      if (level === 'error') return 'border-l-[var(--error)]';
      if (level === 'header') return 'border-l-[var(--text-primary)]';
      return 'border-l-[var(--border-color)]';
    }
    if (ev.type === 'EmulatorStepEvent') {
      const step = ev.data?.step;
      if (step === 'success' || step === 'complete') return 'border-l-[var(--success)]';
      if (step === 'error') return 'border-l-[var(--error)]';
      return 'border-l-[var(--text-secondary)]';
    }
    if (ev.type === 'TransactionCompletedEvent') return 'border-l-[var(--success)]';
    if (ev.type === 'TransactionFailedEvent') return 'border-l-[var(--error)]';
    return 'border-l-[var(--border-color)]';
  };

  return (
    <div className="flex flex-col h-full bg-[var(--surface)]">
      <div className="p-4 border-b border-[var(--border-color)] flex items-center justify-between">
        <div className="flex items-center gap-2">
          <Activity className="w-4 h-4 text-[var(--success)] animate-pulse" />
          <h3 className="text-sm font-semibold text-[var(--text-primary)]">Live Event Feed</h3>
          {events.length > 0 && (
            <span className="ml-1 px-1.5 py-0.5 text-[10px] font-bold border border-[var(--border-color)] text-[var(--text-secondary)] rounded">
              {events.length}
            </span>
          )}
        </div>
        <button onClick={clearEvents} className="text-[var(--text-secondary)] hover:text-[var(--text-primary)] transition-colors" title="Clear Feed">
          <Trash2 className="w-4 h-4" />
        </button>
      </div>
      
      <div className="flex-1 overflow-y-auto p-3 space-y-2 custom-scrollbar">
        {events.length === 0 ? (
          <div className="flex flex-col items-center justify-center h-full text-[var(--text-secondary)] space-y-2 opacity-50">
            <RefreshCw className="w-6 h-6 animate-spin" style={{ animationDuration: '3s' }} />
            <p className="text-xs font-medium">Awaiting events...</p>
          </div>
        ) : (
          <AnimatePresence initial={false}>
            {events.map((ev, i) => (
              <motion.div 
                key={`${ev.type}-${ev.timestamp}-${i}`}
                initial={{ opacity: 0, scale: 0.97 }}
                animate={{ opacity: 1, scale: 1 }}
                className={`p-2.5 rounded border border-[var(--border-color)] bg-[var(--bg-primary)] border-l-2 ${getEventAccent(ev)}`}
              >
                <div className="flex justify-between items-center mb-1">
                  <span className="flex items-center gap-1.5 text-[10px] font-bold tracking-wide text-[var(--text-secondary)]">
                    {getEventIcon(ev.type)}
                    {getEventLabel(ev)}
                  </span>
                  <span className="text-[10px] text-[var(--text-secondary)]">
                    {formatRelativeTime(ev.timestamp)}
                  </span>
                </div>
                <p className="text-[11px] text-[var(--text-primary)] font-mono leading-relaxed break-words whitespace-pre-wrap">
                  {getEventBody(ev)}
                </p>
              </motion.div>
            ))}
          </AnimatePresence>
        )}
        <div ref={bottomRef} />
      </div>
    </div>
  );
}
