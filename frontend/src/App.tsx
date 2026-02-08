import { Routes, Route } from "react-router-dom";

import Header from '@/components/Header';

import HomePage from '@/pages/HomePage';
import VenuesPage from '@/pages/VenuesPage';
import VenueDetailPage from '@/pages/VenueDetailPage';
import EventsPage from '@/pages/EventsPage';
import EventDetailPage from '@/pages/EventDetailPage';
import CartPage from '@/pages/CartPage';
import CheckoutPage from '@/pages/CheckoutPage';
import OrderConfirmationPage from "@/pages/OrderConfirmationPage";
import UserOrdersPage from '@/pages/UserOrdersPage';
import OrderDetailsPage from '@/pages/OrderDetailsPage';
import Page404 from "@/pages/Page404";
import AboutPage from "@/pages/AboutPage";

// import AdminPage from '@/pages/AdminPage';

function App() {

  return (

    <div className="d-flex flex-column min-vh-100 bg-back-300 text-neutral-300">
      <Header />

      <main className="flex-fill py-5 px-3 px-md-5">
        <Routes>

          {/* Public Routes */}
          <Route path="/" element={<HomePage />} />
          <Route path="/venues" element={<VenuesPage />} />
          <Route path="/venues/:venueId" element={<VenueDetailPage />} />
          <Route path="/events" element={<EventsPage />} />
          <Route path="/events/:eventId" element={<EventDetailPage />} />

          <Route path="/cart" element={<CartPage />} />

          <Route path="/checkout" element={<CheckoutPage />} />
          <Route path="/checkout/:orderId" element={<CheckoutPage />} />

          <Route path="/orders/:orderId/confirmation" element={<OrderConfirmationPage />} />

          <Route path="/orders" element={<UserOrdersPage />} />
          <Route path="/orders/:orderId" element={<OrderDetailsPage />} />

          <Route path="/about" element={<AboutPage />} />

          {/* Admin Route */}
          {/* <Route path="/admin" element={<AdminPage />} /> */}

          {/* 404 / Fallback */}
          <Route path="*" element={<Page404 />} />

        </Routes>
      </main>

    </div>
  )
}

export default App
