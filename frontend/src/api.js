import axios from 'axios';
import mockKiosks from './mocks/kiosks.json';
import mockTransactions from './mocks/transactions.json';
import mockEvents from './mocks/events.json';

const MOCK = import.meta.env.VITE_MOCK_MODE === 'true';
const api = axios.create({ baseURL: '/api' });

export const getKiosks = () => MOCK ? Promise.resolve({ data: mockKiosks }) : api.get('/kiosks');
export const getTransactions = () => MOCK ? Promise.resolve({ data: mockTransactions }) : api.get('/transactions');
export const getEvents = () => MOCK ? Promise.resolve({ data: mockEvents }) : api.get('/events/stream');
export const runScenario = (s) => MOCK ? Promise.resolve({ data: { success: true } }) : api.post(`/simulate/${s}`);
export const getPayments = () => MOCK ? Promise.resolve({ data: [{ name: 'CreditCard' }, { name: 'DigitalWallet' }, { name: 'UPI' }] }) : api.get('/payments');
export const getKioskDetail = (id) => MOCK ? Promise.resolve({ data: mockKiosks.find(k => k.kioskId === id) }) : api.get(`/kiosks/${id}`);
export const toggleSystemMode = (mode) => MOCK ? Promise.resolve({ data: { systemMode: mode }}) : api.post('/system/mode', { mode });
export const registerProvider = (name) => MOCK ? Promise.resolve({ data: { registered: name }}) : api.post('/payments/register', { providerName: name });
export const refundTransaction = (id) => MOCK ? Promise.resolve({ data: { success: true }}) : api.post(`/transactions/${id}/refund`);
export const restockItem = (kioskId, productId, qty) => MOCK ? Promise.resolve({ data: { success: true }}) : api.post(`/kiosks/${kioskId}/restock`, { productId, quantity: qty });
export const purchaseItem = (kioskId, userId, productId, qty) => MOCK ? Promise.resolve({ data: { success: true }}) : api.post(`/kiosks/${kioskId}/purchase`, { userId, productId, quantity: qty });
