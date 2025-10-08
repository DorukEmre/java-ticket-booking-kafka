import { Link } from "react-router-dom"

function Header() {
  return (
    <header className="bg-back-200 p-3 d-flex justify-content-between align-items-center">
      <Link to="/" className="text-decoration-none">
        <div className="p-1">
          <h1>Ticket Booking System</h1>
        </div>
      </Link>
      <div className="d-flex gap-3">
        <Link to="/events" className="text-decoration-none">
          <div className="p-3 bg-back-400 rounded">
            Events
          </div>
        </Link>
        <Link to="/orders" className="text-decoration-none">
          <div className="p-3 bg-back-400 rounded">
            Orders
          </div>
        </Link>
        <Link to="/cart" className="text-decoration-none">
          <div className="p-3 bg-back-400 rounded">
            Cart
          </div>
        </Link>
      </div>
    </header>
  )
}

export default Header
