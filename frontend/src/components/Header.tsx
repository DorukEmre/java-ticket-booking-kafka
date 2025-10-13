import { Link } from "react-router-dom"

import HeaderButton from "@/components/HeaderButton"

import { cartIcon } from "@/assets"

function Header() {

  return (
    <header>
      <nav className="navbar navbar-expand-md bg-back-200" data-bs-theme="dark">
        <div className="container-fluid">

          <Link className="navbar-brand text-neutral-300 p-3 text-decoration-none" to={"/"}>
            Ticket Booking
          </Link>

          <button className="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav" aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
            <span className="navbar-toggler-icon"></span>
          </button>

          <div className="collapse navbar-collapse flex-grow-0" id="navbarNav">
            <ul className="navbar-nav align-items-end gap-2">
              <HeaderButton path={"/events"} text={"Events"} />
              <HeaderButton path={"/venues"} text={"Venues"} />
              <HeaderButton path={"/orders"} text={"My orders"} />
              <HeaderButton path={"/cart"} text={"Cart"} icon={cartIcon} />
            </ul>
          </div>

        </div>
      </nav>
    </header>
  )
}

export default Header
