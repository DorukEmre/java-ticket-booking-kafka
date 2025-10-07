import { QueryClient } from "@tanstack/react-query";

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      staleTime: 1000 * 60 * 5, // 5 minutes

      retry: 2,

      refetchOnWindowFocus: true, // when the window regains focus
      refetchOnMount: false, // when component mounts
      refetchOnReconnect: true, // when the browser reconnects to the internet
    },
    mutations: {
      retry: 1, // for POST/PUT/DELETE actions
    },
  },
});

export default queryClient;