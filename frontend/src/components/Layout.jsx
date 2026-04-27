import { Outlet } from 'react-router-dom';
import { AnimatePresence } from 'framer-motion';
import Sidebar from './Sidebar';
import TopBar from './TopBar';
import LiveEventFeed from './LiveEventFeed';

export default function Layout() {
  return (
    <div className="flex h-screen bg-[var(--bg-primary)] font-sans text-[var(--text-primary)] overflow-hidden transition-colors duration-300">
      <Sidebar />
      <div className="flex-1 flex flex-col min-w-0">
        <TopBar />
        <main className="flex-1 overflow-x-hidden overflow-y-auto p-4 md:p-8">
          <div className="max-w-7xl mx-auto">
            <AnimatePresence mode="wait">
              <Outlet />
            </AnimatePresence>
          </div>
        </main>
      </div>
      
      {/* Event Feed Sidebar - visible on lg screens */}
      <div className="w-80 hidden xl:block bg-[var(--surface)] border-l border-[var(--border-color)] z-20">
        <LiveEventFeed />
      </div>
    </div>
  );
}
