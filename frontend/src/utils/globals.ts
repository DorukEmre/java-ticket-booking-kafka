function getBaseUrl() {
  const baseUrl = import.meta.env.VITE_API_BASE_URL;
  if (!baseUrl) {
    throw new Error("VITE_API_BASE_URL is not defined");
  }

  return baseUrl;
}

const imageBaseUrl = getBaseUrl() + "/images/";

function addBaseUrl(path: string): string {
  const baseURL = getBaseUrl();

  return `${baseURL}${path}`;
}

const CartStatus = {
  PENDING: 'PENDING',
  IN_PROGRESS: 'IN_PROGRESS',
  INVALID: 'INVALID',
  CONFIRMED: 'CONFIRMED',
  FAILED: 'FAILED'
} as const;

export { getBaseUrl, imageBaseUrl, addBaseUrl, CartStatus };