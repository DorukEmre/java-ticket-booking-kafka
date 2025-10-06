
function getBaseUrl() {
  const baseURL = import.meta.env.VITE_API_BASE_URL;
  if (!baseURL) {
    throw new Error("VITE_API_BASE_URL is not defined");
  }
  return baseURL;
}

export { getBaseUrl };