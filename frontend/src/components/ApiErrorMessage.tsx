
type ErrorMessageProps = {
  error: Error | null;
};

export default function ApiErrorMessage({ error }: ErrorMessageProps) {
  if (!error) return null;

  const message =
    (error as any)?.response?.data?.message ||
    (error as Error).message ||
    "An unknown error occurred.";

  return <p style={{ color: "red" }}>Error: {message}</p>;

}