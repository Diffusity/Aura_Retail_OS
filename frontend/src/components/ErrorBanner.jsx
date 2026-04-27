import { AlertTriangle } from 'lucide-react';

export default function ErrorBanner({ message, onRetry }) {
  return (
    <div className="p-6">
      <div className="mono-card border-[var(--error)] p-6 bg-red-50 dark:bg-red-950/20 flex flex-col items-center justify-center text-center space-y-4">
        <AlertTriangle className="w-12 h-12 text-[var(--error)]" />
        <div>
          <h2 className="text-lg font-bold text-[var(--text-primary)]">Failed to load data</h2>
          <p className="text-sm text-[var(--text-secondary)] mt-1">{message || "The backend API might be offline or unreachable."}</p>
        </div>
        {onRetry && (
          <button 
            onClick={onRetry}
            className="px-4 py-2 bg-[var(--text-primary)] text-[var(--bg-primary)] rounded font-medium text-sm hover:opacity-90 transition-opacity"
          >
            Try Again
          </button>
        )}
      </div>
    </div>
  );
}
