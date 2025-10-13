import { Link } from "react-router-dom";

import { useCart } from "@/hooks/useCart";

function HeaderCartButton({ path, text, icon }
  : { path: string; text: string, icon?: string }) {

  const { cart } = useCart();

  return (
    <li className="nav-item p-2 rounded header-button">
      <Link to={path} className="nav-link text-neutral-300 text-decoration-none icon-link position-relative">
        {text}
        {icon && <img src={icon} alt="icon" aria-hidden="true" />}
        {(cart?.items?.length || 0) > 0 &&
          <span className="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger">
            {cart?.items?.length}
            <span className="visually-hidden">unread messages</span>
          </span>
        }
      </Link>
    </li>
  )
}

export default HeaderCartButton;