import { NavLink } from 'react-router-dom';
import { LayoutDashboard, Receipt, Play, CreditCard } from 'lucide-react';

export default function Sidebar() {
  return (
    <div className="w-64 bg-slate-900 text-white flex flex-col">
      <div className="p-6">
        <h1 className="text-2xl font-bold tracking-wider text-blue-400">AURA</h1>
        <p className="text-xs text-slate-400 mt-1 uppercase tracking-widest">Retail OS</p>
      </div>
      <nav className="flex-1 px-4 space-y-2 mt-4">
        <NavLink to="/" className={({ isActive }) => `flex items-center px-4 py-3 rounded-lg transition-colors ${isActive ? 'bg-blue-600 text-white' : 'text-slate-300 hover:bg-slate-800'}`}>
          <LayoutDashboard className="w-5 h-5 mr-3" />
          Dashboard
        </NavLink>
        <NavLink to="/transactions" className={({ isActive }) => `flex items-center px-4 py-3 rounded-lg transition-colors ${isActive ? 'bg-blue-600 text-white' : 'text-slate-300 hover:bg-slate-800'}`}>
          <Receipt className="w-5 h-5 mr-3" />
          Transactions
        </NavLink>
        <NavLink to="/simulate" className={({ isActive }) => `flex items-center px-4 py-3 rounded-lg transition-colors ${isActive ? 'bg-blue-600 text-white' : 'text-slate-300 hover:bg-slate-800'}`}>
          <Play className="w-5 h-5 mr-3" />
          Simulate
        </NavLink>
        <NavLink to="/payments" className={({ isActive }) => `flex items-center px-4 py-3 rounded-lg transition-colors ${isActive ? 'bg-blue-600 text-white' : 'text-slate-300 hover:bg-slate-800'}`}>
          <CreditCard className="w-5 h-5 mr-3" />
          Payments
        </NavLink>
      </nav>
    </div>
  );
}
