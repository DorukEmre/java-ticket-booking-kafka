import { Link } from "react-router-dom";

import { useCart } from "@/hooks/useCart";
import { useEffect, useState } from "react";

function HeaderCartButton({ path, text, icon, handleClick }
  : { path: string; text: string, icon?: string, handleClick: () => void }) {

  const { cart } = useCart();
  const [totalTicketCount, setTotalTicketCount] = useState(0);

  useEffect(() => {
    // calculate total ticket count from cart

    let count = 0;

    for (const item of cart?.items || []) {
      cart?.items && (count += item.ticketCount);
    }

    setTotalTicketCount(count);

  }, [cart?.items]);

  return (
    <li className="nav-item rounded header-button">
      <Link
        to={path}
        className="nav-link p-3 text-neutral-300 text-decoration-none icon-link position-relative"
        onClick={handleClick}
      >
        {text}
        {icon && <img src={icon} alt="icon" aria-hidden="true" />}
        {(cart?.items?.length || 0) > 0 &&
          <span className="position-absolute top-0 end-0 badge rounded-pill bg-danger">
            {totalTicketCount}
            <span className="visually-hidden">unread messages</span>
          </span>
        }
      </Link>
    </li>
  )
}

export default HeaderCartButton;