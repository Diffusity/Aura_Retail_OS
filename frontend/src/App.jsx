import { BrowserRouter, Routes, Route } from 'react-router-dom';
import Layout from './components/Layout';
import Dashboard from './pages/Dashboard';
import KioskDetail from './pages/KioskDetail';
import Transactions from './pages/Transactions';
import SimulationConsole from './pages/SimulationConsole';
import PaymentProviders from './pages/PaymentProviders';

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route element={<Layout />}>
          <Route path="/" element={<Dashboard />} />
          <Route path="/kiosk/:id" element={<KioskDetail />} />
          <Route path="/transactions" element={<Transactions />} />
          <Route path="/simulate" element={<SimulationConsole />} />
          <Route path="/payments" element={<PaymentProviders />} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
}
