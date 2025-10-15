import { Link, useLocation } from "react-router-dom"
import { Collapse } from "bootstrap"

import HeaderButton from "@/components/HeaderButton"
import HeaderCartButton from "@/components/HeaderCartButton"

import { cartIcon } from "@/assets"
import { useEffect } from "react"

function Header() {

  const location = useLocation()

  const handleClick = () => {
    const navbar = document.getElementById('navbarNav');
    if (!navbar) return;

    if (navbar.classList.contains('show')) {
      try {
        const inst = Collapse.getOrCreateInstance(navbar);
        inst.hide();
      } catch (e) {
        navbar.classList.remove('show');
      }
    }
  }

  useEffect(() => {
    handleClick()
  }, [location.pathname])

  return (
    <header>
      <nav className="navbar navbar-expand-md bg-back-200" data-bs-theme="dark">
        <div className="container-fluid">

          <Link className="navbar-brand text-neutral-300 p-3 text-decoration-none" to={"/"}>
            Ticket Booking
          </Link>

          <button className="navbar-toggler me-3" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav" aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
            <span className="navbar-toggler-icon"></span>
          </button>

          <div className="collapse navbar-collapse flex-grow-0" id="navbarNav">
            <ul className="navbar-nav align-items-end gap-2 me-3">
              <HeaderButton path={"/events"} text={"Events"} handleClick={handleClick} />
              <HeaderButton path={"/venues"} text={"Venues"} handleClick={handleClick} />
              <HeaderButton path={"/orders"} text={"My orders"} handleClick={handleClick} />
              <HeaderCartButton path={"/cart"} text={"Cart"} icon={cartIcon} handleClick={handleClick} />
            </ul>
          </div>

        </div>
      </nav>
    </header>
  )
}

export default Header
