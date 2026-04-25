import { useState } from 'react';
import { runScenario } from '../api';
import { Play } from 'lucide-react';

export default function SimulationConsole() {
  const [output, setOutput] = useState([]);
  const [loading, setLoading] = useState(false);

  const handleRun = async (scenario) => {
    setLoading(true);
    setOutput(prev => [...prev, `> Starting Scenario ${scenario}...`]);
    try {
      const res = await runScenario(scenario);
      setOutput(prev => [...prev, `[SUCCESS] Scenario ${scenario} completed. Check backend console for full logs.`]);
    } catch (e) {
      setOutput(prev => [...prev, `[ERROR] Scenario ${scenario} failed: ${e.message}`]);
    } finally {
      setLoading(false);
    }
  };

  const scenarios = [
    { id: 'A', name: 'Add Hardware Module', desc: 'Demonstrates Decorator and Bridge patterns by swapping dispensers and adding refrigeration.' },
    { id: 'B', name: 'New Payment Provider', desc: 'Demonstrates Adapter pattern by registering a Crypto payment API at runtime.' },
    { id: 'C', name: 'Nested Bundles', desc: 'Demonstrates Composite pattern by checking availability of nested emergency kits.' }
  ];

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-gray-900">Simulation Console</h1>
        <p className="text-gray-500">Execute grading scenarios to verify design patterns</p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        {scenarios.map(s => (
          <div key={s.id} className="bg-white rounded-xl border border-gray-200 shadow-sm p-6 flex flex-col">
            <div className="flex items-center justify-between mb-4">
              <h3 className="font-bold text-lg">Scenario {s.id}</h3>
              <span className="w-8 h-8 rounded-full bg-blue-100 text-blue-600 flex items-center justify-center font-bold">{s.id}</span>
            </div>
            <p className="text-sm text-gray-600 mb-6 flex-1">{s.desc}</p>
            <button 
              onClick={() => handleRun(s.id)}
              disabled={loading}
              className="w-full flex items-center justify-center px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-blue-600 hover:bg-blue-700 disabled:opacity-50 transition-colors"
            >
              <Play className="w-4 h-4 mr-2" />
              Run Scenario
            </button>
          </div>
        ))}
      </div>

      <div className="bg-gray-900 rounded-xl overflow-hidden shadow-lg border border-gray-700">
        <div className="bg-gray-800 px-4 py-2 border-b border-gray-700 flex items-center">
          <div className="flex space-x-2">
            <div className="w-3 h-3 rounded-full bg-red-500"></div>
            <div className="w-3 h-3 rounded-full bg-yellow-500"></div>
            <div className="w-3 h-3 rounded-full bg-green-500"></div>
          </div>
          <span className="ml-4 text-xs font-mono text-gray-400">console output</span>
        </div>
        <div className="p-4 h-64 overflow-y-auto font-mono text-sm">
          {output.length === 0 ? (
            <div className="text-gray-500 italic">Ready to run simulations. Output will appear here.</div>
          ) : (
            output.map((line, i) => (
              <div key={i} className={line.startsWith('[ERROR]') ? 'text-red-400' : 'text-green-400'}>
                {line}
              </div>
            ))
          )}
        </div>
      </div>
    </div>
  );
}
