import type { ApiErrorResponse } from "@/types/error";
import axios from "axios";
import { addBaseUrl } from "@/utils/globals";

async function axiosGetWithErrorHandling<T>(path: string): Promise<T> {
  console.log("axios GET request to:", path);

  const url = addBaseUrl(path);

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

async function axiosPostWithErrorHandling<T>(path: string, data: any, returnStatus = false): Promise<T> {
  console.log("axios POST request to:", path);

  const url = addBaseUrl(path);

  try {
    const response = await axios.post(url, data, { withCredentials: true });
    if (returnStatus)
      return response.status as T;
    else
      return response.data as T;

  } catch (err: any) {
    const apiError: ApiErrorResponse = {
      status: err.response?.status || 500,
      message: err.response?.data?.message || "API request failed",
    };
    throw apiError;
  }
}

async function axiosPutWithErrorHandling<T>(path: string, data: any, returnStatus = false): Promise<T> {
  console.log("axios PUT request to:", path);

  const url = addBaseUrl(path);

  try {
    const response = await axios.put(url, data, { withCredentials: true });
    if (returnStatus)
      return response.status as T;
    else
      return response.data as T;

  } catch (err: any) {
    const apiError: ApiErrorResponse = {
      status: err.response?.status || 500,
      message: err.response?.data?.message || "API request failed",
    };
    throw apiError;
  }
}

async function axiosDeleteWithErrorHandling<T>(path: string, data?: any, returnStatus = false): Promise<T> {
  console.log("axios DELETE request to:", path);

  const url = addBaseUrl(path);

  try {
    const response = await axios.delete(url, { data, withCredentials: true });
    if (returnStatus)
      return response.status as T;
    else
      return response.data as T;

  } catch (err: any) {
    const apiError: ApiErrorResponse = {
      status: err.response?.status || 500,
      message: err.response?.data?.message || "API request failed",
    };
    throw apiError;
  }
}

export { axiosGetWithErrorHandling, axiosPostWithErrorHandling, axiosPutWithErrorHandling, axiosDeleteWithErrorHandling };