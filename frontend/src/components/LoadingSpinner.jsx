import { Loader2 } from 'lucide-react';

export default function LoadingSpinner({ text = "Loading...", fullScreen = false }) {
  const content = (
    <div className="flex flex-col items-center justify-center space-y-3">
      <Loader2 className="w-6 h-6 text-[var(--text-primary)] animate-spin" />
      <span className="text-sm font-medium text-[var(--text-secondary)]">{text}</span>
    </div>
  );

  if (fullScreen) {
    return (
      <div className="fixed inset-0 z-50 flex items-center justify-center" style={{ backgroundColor: 'rgba(0,0,0,0.4)' }}>
        {content}
      </div>
    );
  }

  return (
    <div className="p-12 w-full flex justify-center items-center">
      {content}
    </div>
  );
}

export function SkeletonLine({ className = "" }) {
  return <div className={`animate-shimmer rounded bg-[var(--bg-tertiary)] ${className}`} />;
}
