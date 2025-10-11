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

// import AdminPage from '@/pages/AdminPage';
// import NotFoundPage from '@/pages/NotFoundPage';

function App() {

  return (

    <div className="d-flex flex-column min-vh-100 bg-back-300 text-neutral-300">
      <Header />

      <main className="flex-fill p-4">
        <Routes>
          {/* Public Routes */}
          <Route path="/" element={<HomePage />} />
          <Route path="/venues" element={<VenuesPage />} />
          <Route path="/venues/:venueId" element={<VenueDetailPage />} />
          <Route path="/events" element={<EventsPage />} />
          <Route path="/events/:eventId" element={<EventDetailPage />} />

          <Route path="/cart" element={<CartPage />} />

          <Route path="/checkout/:orderId" element={<CheckoutPage />} />

          <Route path="/orders/:orderId/confirmation" element={<OrderConfirmationPage />} />

          <Route path="/orders" element={<UserOrdersPage />} />
          <Route path="/orders/:orderId" element={<OrderDetailsPage />} />

          {/* Admin Route */}
          {/* <Route path="/admin" element={<AdminPage />} /> */}

          {/* 404 / Fallback */}
          {/* <Route path="*" element={<NotFoundPage />} /> */}
        </Routes>
      </main>

    </div>
  )
}

export default App
