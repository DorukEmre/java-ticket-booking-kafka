import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { BrowserRouter } from "react-router-dom";

// import { CartProvider } from './contexts/CartContext';

import './css/reset.css'
import './css/index.css'
import "./css/custom.scss";

import App from './App.tsx'

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <BrowserRouter>
      {/* <CartProvider> */}
      <App />
      {/* </CartProvider> */}
    </BrowserRouter>
  </StrictMode>,
)
