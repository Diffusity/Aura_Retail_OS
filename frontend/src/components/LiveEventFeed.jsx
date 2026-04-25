import { useState, useEffect, useRef } from 'react';
import { getEvents } from '../api';

export default function LiveEventFeed() {
  const [events, setEvents] = useState([]);
  const bottomRef = useRef(null);

  useEffect(() => {
    // Polling simulation since SSE isn't fully set up in basic Javalin mock
    const fetchEvents = async () => {
      try {
        const res = await getEvents();
        if (res.data && res.data.length) {
           setEvents(prev => [...prev, ...res.data].slice(-50)); // keep last 50
        }
      } catch (e) {
        console.error(e);
      }
    };
    
    // Initial fetch
    fetchEvents();
    
    // Poll every 2s
    const interval = setInterval(fetchEvents, 2000);
    return () => clearInterval(interval);
  }, []);

  useEffect(() => {
    bottomRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [events]);

  const getColor = (type) => {
    if (type.includes('Failure')) return 'text-red-600 border-red-200 bg-red-50';
    if (type.includes('LowStock')) return 'text-orange-600 border-orange-200 bg-orange-50';
    if (type.includes('Completed')) return 'text-green-600 border-green-200 bg-green-50';
    if (type.includes('Emergency')) return 'text-purple-600 border-purple-200 bg-purple-50';
    return 'text-blue-600 border-blue-200 bg-blue-50';
  };

  return (
    <div className="flex flex-col h-full">
      <div className="p-4 border-b border-gray-200 bg-gray-50">
        <h3 className="font-semibold text-gray-700 flex items-center">
          <div className="w-2 h-2 rounded-full bg-green-500 animate-pulse mr-2"></div>
          Live Event Feed
        </h3>
      </div>
      <div className="flex-1 overflow-y-auto p-4 space-y-3 bg-gray-50/50">
        {events.length === 0 ? (
          <p className="text-sm text-gray-500 text-center italic mt-10">Waiting for events...</p>
        ) : (
          events.map((ev, i) => (
            <div key={i} className={`p-3 rounded border text-sm shadow-sm ${getColor(ev.type)}`}>
              <div className="flex justify-between items-start mb-1">
                <span className="font-bold">{ev.type.replace('Event', '')}</span>
                <span className="text-xs opacity-70">
                  {new Date(ev.timestamp).toLocaleTimeString()}
                </span>
              </div>
              <pre className="text-xs mt-2 overflow-x-auto opacity-90 font-mono">
                {JSON.stringify(ev.data, null, 2)}
              </pre>
            </div>
          ))
        )}
        <div ref={bottomRef} />
      </div>
    </div>
  );
}
