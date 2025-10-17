function LoadingSpinner(
  {
    className = "ms-5",
    size
  }: {
    className?: string;
    size?: number;
  }) {
  return (
    <div className={`spinner-border ${className}`}
      role="status" style={{ width: size ?? 36, height: size ?? 36 }}>
      <span className="visually-hidden">Loading...</span>
    </div>
  );
}

export default LoadingSpinner;