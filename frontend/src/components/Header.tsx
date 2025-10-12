import { Link } from "react-router-dom"

import HeaderButton from "@/components/HeaderButton"

import cartIcon from "@/assets/shopping_cart_24dp_C5C0B9_FILL0_wght400_GRAD0_opsz24.svg"

function Header() {
  return (
    <header className="bg-back-200 p-3 d-flex justify-content-between align-items-center">

      <Link to="/" className="text-decoration-none">
        <div className="p-1">
          <h1>Ticket Booking System</h1>
        </div>
      </Link>

      <div className="d-flex gap-3">
        <HeaderButton path={"/events"} text={"Events"} />
        <HeaderButton path={"/orders"} text={"My orders"} />
        <HeaderButton path={"/cart"} text={"Cart"} icon={cartIcon} />
      </div>

    </header>
  )
}

export default Header
