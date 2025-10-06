import { Link } from "react-router-dom"

function Header() {
  return (
    <header className="bg-back-200 p-3">
      <Link to="/" className="text-decoration-none">
        <div className="container">
          <h1>Ticket Booking System</h1>
        </div>
      </Link>
    </header>
  )
}

export default Header
