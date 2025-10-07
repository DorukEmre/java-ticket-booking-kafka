function getBaseUrl() {
  const baseUrl = import.meta.env.VITE_API_BASE_URL;
  if (!baseUrl) {
    throw new Error("VITE_API_BASE_URL is not defined");
  }
  return baseUrl;
}

const imageBaseUrl = getBaseUrl() + "/images/";

export { getBaseUrl, imageBaseUrl };