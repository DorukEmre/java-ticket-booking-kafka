import type { ApiErrorResponse } from "@/types/error";
import axios from "axios";

async function axiosGetWithErrorHandling<T>(url: string): Promise<T> {
  console.log("GET request to:", url);

  try {
    const response = await axios.get(url, { withCredentials: true });
    return response.data as T;

  } catch (err: any) {
    const apiError: ApiErrorResponse = {
      status: err.response?.status || 500,
      message: err.response?.data?.message || "API request failed",
    };
    throw apiError;
  }
}

export { axiosGetWithErrorHandling };