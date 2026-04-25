import { Outlet } from 'react-router-dom';
import Sidebar from './Sidebar';
import TopBar from './TopBar';
import LiveEventFeed from './LiveEventFeed';

export default function Layout() {
  return (
    <div className="flex h-screen bg-gray-100 font-sans">
      <Sidebar />
      <div className="flex-1 flex flex-col overflow-hidden">
        <TopBar />
        <main className="flex-1 overflow-x-hidden overflow-y-auto bg-gray-50 p-6">
          <Outlet />
        </main>
      </div>
      <div className="w-80 border-l bg-white border-gray-200 hidden lg:block">
        <LiveEventFeed />
      </div>
    </div>
  );
}
