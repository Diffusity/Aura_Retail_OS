import { useEffect } from 'react';
import { motion } from 'framer-motion';
import { CheckCircle, XCircle, Info, AlertTriangle, X } from 'lucide-react';

export default function Toast({ id, message, type, onClose }) {
  useEffect(() => {
    const timer = setTimeout(() => {
      onClose(id);
    }, 4000);
    return () => clearTimeout(timer);
  }, [id, onClose]);

  const config = {
    success: { icon: CheckCircle },
    error: { icon: XCircle },
    warning: { icon: AlertTriangle },
    info: { icon: Info }
  };

  const { icon: Icon } = config[type] || config.info;

  return (
    <motion.div
      initial={{ opacity: 0, x: 50 }}
      animate={{ opacity: 1, x: 0 }}
      exit={{ opacity: 0, x: 50, transition: { duration: 0.15 } }}
      className="flex items-center gap-3 px-4 py-3 pr-10 rounded border border-[var(--border-color)] bg-[var(--surface)] shadow-lg relative min-w-[280px]"
      style={{ boxShadow: 'var(--shadow)' }}
    >
      <Icon className="w-4 h-4 text-[var(--text-primary)] flex-shrink-0" />
      <span className="text-sm font-medium text-[var(--text-primary)]">{message}</span>
      <button 
        onClick={() => onClose(id)} 
        className="absolute right-3 top-1/2 -translate-y-1/2 text-[var(--text-secondary)] hover:text-[var(--text-primary)] transition-colors"
      >
        <X className="w-3.5 h-3.5" />
      </button>
    </motion.div>
  );
}
