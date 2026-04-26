import { useState, useEffect, useRef } from 'react';
import { runScenario, getEvents } from '../api';
import { useToast } from '../context/ToastContext';
import { Terminal, Play, Cpu, CreditCard, Layers, Trash2, Radio } from 'lucide-react';
import { motion } from 'framer-motion';

export default function SimulationConsole() {
  const [output, setOutput] = useState([]);
  const [running, setRunning] = useState(null);
  const [runningAll, setRunningAll] = useState(false);
  const { addToast } = useToast();
  const termRef = useRef(null);
  const pollingRef = useRef(null);

  const scenarios = [
    { id: 'A', name: 'Hardware Upgrade', desc: 'Adds Refrigeration + Solar modules to a Food Kiosk via Decorator, swaps dispenser via Bridge.', icon: Cpu, patterns: ['Decorator', 'Bridge', 'Factory'] },
    { id: 'B', name: 'New Payment Adapter', desc: 'Registers a CryptoPaymentAdapter at runtime and processes a test transaction.', icon: CreditCard, patterns: ['Adapter', 'Strategy', 'Open/Closed'] },
    { id: 'C', name: 'Nested Inventory', desc: 'Creates bundles containing products and other bundles, tests availability propagation.', icon: Layers, patterns: ['Composite', 'Iterator'] }
  ];

  // Poll for simulation log events while a scenario is running
  const startPolling = () => {
    if (pollingRef.current) return;
    pollingRef.current = setInterval(async () => {
      try {
        const res = await getEvents();
        if (res.data && res.data.length > 0) {
          const simLogs = res.data.filter(e => e.type === 'SimulationLogEvent');
          if (simLogs.length > 0) {
            setOutput(prev => [
              ...prev,
              ...simLogs.map(e => ({
                time: new Date(e.timestamp).toLocaleTimeString(),
                msg: e.data.message,
                type: e.data.level || 'info'
              }))
            ]);
          }
        }
      } catch (e) {}
    }, 1500);
  };

  const stopPolling = () => {
    if (pollingRef.current) {
      clearInterval(pollingRef.current);
      pollingRef.current = null;
    }
  };

  useEffect(() => {
    return () => stopPolling();
  }, []);

  // Auto-scroll terminal
  useEffect(() => {
    if (termRef.current) {
      termRef.current.scrollTop = termRef.current.scrollHeight;
    }
  }, [output]);

  const log = (msg, type = 'info') => {
    setOutput(prev => [...prev, { time: new Date().toLocaleTimeString(), msg, type }]);
  };

  const executeScenario = async (id) => {
    setRunning(id);
    log(`▶ Initializing Scenario ${id}...`, 'system');
    startPolling();
    try {
      await runScenario(id);
      // The backend runs async, so we wait a reasonable time for it to complete
      await new Promise(r => setTimeout(r, 12000));
      log(`✓ Scenario ${id} execution stream ended.`, 'system');
      addToast(`Scenario ${id} completed`, 'success');
    } catch (e) {
      log(`✗ Scenario ${id} failed: ${e.message}`, 'error');
      addToast(`Scenario ${id} failed`, 'error');
    }
    stopPolling();
    setRunning(null);
    return true;
  };

  const runAll = async () => {
    setRunningAll(true);
    for (const s of scenarios) {
      await executeScenario(s.id);
      await new Promise(r => setTimeout(r, 2000));
    }
    setRunningAll(false);
    log('━━━ All scenarios completed ━━━', 'system');
  };

  const getLogColor = (type) => {
    switch(type) {
      case 'success': return 'text-[var(--success)]';
      case 'error': return 'text-[var(--error)]';
      case 'warning': return 'text-amber-500';
      case 'header': return 'text-[var(--text-primary)] font-bold';
      case 'system': return 'text-[var(--text-secondary)] font-bold italic';
      default: return 'text-[var(--text-primary)]';
    }
  };

  return (
    <div className="space-y-6 max-w-6xl mx-auto">
      <div>
        <h1 className="text-2xl font-bold text-[var(--text-primary)] tracking-tight">Simulation Console</h1>
        <p className="text-sm text-[var(--text-secondary)] mt-1">
          Execute evaluation scenarios asynchronously. Events stream to both the terminal below and the Live Event Feed.
        </p>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="space-y-4">
          <div className="flex justify-between items-center">
            <h2 className="text-lg font-semibold text-[var(--text-primary)]">Scenarios</h2>
            <button 
              onClick={runAll} 
              disabled={!!running || runningAll} 
              className="text-xs font-bold bg-[var(--text-primary)] text-[var(--bg-primary)] px-3 py-1.5 rounded transition-opacity hover:opacity-90 disabled:opacity-50"
            >
              Run All
            </button>
          </div>
          
          {scenarios.map(s => (
            <div key={s.id} className={`mono-card p-5 border-l-4 ${running === s.id ? 'border-l-[var(--success)]' : 'border-l-[var(--text-primary)]'}`}>
              <div className="flex justify-between items-start mb-2">
                <div className="flex items-center gap-2">
                  <div className="p-1.5 rounded border border-[var(--border-color)] text-[var(--text-secondary)]"><s.icon className="w-4 h-4" /></div>
                  <h3 className="font-bold text-[var(--text-primary)]">Scenario {s.id}</h3>
                </div>
                <button 
                  onClick={() => executeScenario(s.id)}
                  disabled={!!running || runningAll}
                  className="p-1.5 rounded border border-[var(--border-color)] text-[var(--text-primary)] hover:bg-[var(--bg-secondary)] disabled:opacity-50 transition-colors"
                  title="Execute"
                >
                  {running === s.id ? (
                    <Radio className="w-4 h-4 text-[var(--success)] animate-pulse" />
                  ) : (
                    <Play className="w-4 h-4" />
                  )}
                </button>
              </div>
              <p className="text-sm text-[var(--text-secondary)] mb-4">{s.desc}</p>
              <div className="flex gap-2 flex-wrap">
                {s.patterns.map(p => (
                  <span key={p} className="text-[10px] font-mono font-medium px-2 py-0.5 rounded border border-[var(--border-color)] bg-[var(--bg-secondary)] text-[var(--text-secondary)]">
                    {p}
                  </span>
                ))}
              </div>
            </div>
          ))}
        </div>

        <div className="lg:col-span-2 mono-card overflow-hidden flex flex-col h-[520px]">
          <div className="bg-[var(--bg-secondary)] px-4 py-3 border-b border-[var(--border-color)] flex justify-between items-center">
            <div className="flex items-center gap-2 text-[var(--text-secondary)]">
              <Terminal className="w-4 h-4" />
              <span className="text-xs font-mono font-medium">simulation_output</span>
              {running && (
                <span className="ml-2 flex items-center gap-1.5 text-[var(--success)]">
                  <span className="w-2 h-2 rounded-full bg-[var(--success)] animate-pulse" />
                  <span className="text-[10px] font-bold tracking-wider">STREAMING</span>
                </span>
              )}
            </div>
            <button onClick={() => setOutput([])} className="text-[var(--text-secondary)] hover:text-[var(--text-primary)] transition-colors">
              <Trash2 className="w-4 h-4" />
            </button>
          </div>
          <div ref={termRef} className="flex-1 p-4 overflow-y-auto font-mono text-sm bg-[#050505] dark:bg-[#000000]">
            {output.length === 0 ? (
              <p className="text-slate-500 italic">Ready. Select a scenario to execute. Output will stream here in real-time.</p>
            ) : (
              output.map((line, i) => (
                <motion.div 
                  initial={{ opacity: 0, x: -5 }} 
                  animate={{ opacity: 1, x: 0 }} 
                  key={i} 
                  className="mb-1 leading-relaxed"
                >
                  <span className="text-slate-600 mr-3 select-none">[{line.time}]</span>
                  <span className={getLogColor(line.type)}>{line.msg}</span>
                </motion.div>
              ))
            )}
            {running && (
              <div className="mt-2 flex items-center gap-2 text-slate-500">
                <span className="w-2 h-4 bg-slate-500 animate-pulse" />
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}
