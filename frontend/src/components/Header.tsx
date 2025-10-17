import { Link, useLocation } from "react-router-dom"

import HeaderButton from "@/components/HeaderButton"
import HeaderCartButton from "@/components/HeaderCartButton"

import { cartIcon } from "@/assets"
import { useEffect, useState } from "react"

function Header() {

  const location = useLocation()
  const [pathIsCheckout, setPathIsCheckout] = useState(false);

  const closeNavbar = () => {
    const navbar = document.getElementById('navbarNav');

    if (!navbar) return;

    if (navbar.classList.contains('show')) {
      navbar.classList.remove('show');
    }
  }

  useEffect(() => {

    // Close navbar on path change
    closeNavbar()

    // Hide header links if path is checkout
    if (location.pathname.startsWith("/checkout")) {
      setPathIsCheckout(true);
    } else {
      setPathIsCheckout(false);
    }

  }, [location.pathname])

  return (
    <header>
      <nav className="navbar navbar-expand-md bg-back-200" data-bs-theme="dark">
        <div className="container-fluid">

          {!pathIsCheckout ? (
            <Link className="navbar-brand text-neutral-300 p-3 text-decoration-none" to={"/"}>
              Ticket Booking
            </Link>
          ) : (
            <span className="navbar-brand text-neutral-300 p-3">
              Ticket Booking
            </span>
          )}

          {!pathIsCheckout && (
            <>
              <button className="navbar-toggler me-3" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav" aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation" >
                <span className="navbar-toggler-icon"></span>
              </button>

              <div className="collapse navbar-collapse flex-grow-0" id="navbarNav">
                <ul className="navbar-nav align-items-end gap-2 me-3">
                  <HeaderButton path={"/events"} text={"Events"} handleClick={closeNavbar} />
                  <HeaderButton path={"/venues"} text={"Venues"} handleClick={closeNavbar} />
                  <HeaderButton path={"/orders"} text={"My orders"} handleClick={closeNavbar} />
                  <HeaderCartButton path={"/cart"} text={"Cart"} icon={cartIcon} handleClick={closeNavbar} />
                </ul>
              </div>
            </>
          )}

        </div>
      </nav>
    </header>
  )
}

export default Header
