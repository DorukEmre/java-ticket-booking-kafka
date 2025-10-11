import { createRoot } from 'react-dom/client'
import { BrowserRouter } from "react-router-dom";
import { QueryClientProvider } from "@tanstack/react-query";

import { CartProvider } from '@/context/CartContext';

import 'bootstrap/dist/css/bootstrap.min.css';
import "@/css/custom.scss";
import '@/css/reset.css'
import '@/css/index.css'

import App from '@/App.tsx'
import queryClient from '@/config/queryClient';

createRoot(document.getElementById('root')!).render(

  <BrowserRouter>
    <QueryClientProvider client={queryClient}>
      <CartProvider>
        <App />
      </CartProvider>
    </QueryClientProvider>
  </BrowserRouter>
)
