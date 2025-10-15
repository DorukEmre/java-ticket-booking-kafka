import { Link } from "react-router-dom";

function HeaderButton({ path, text, icon, handleClick }
  : { path: string; text: string, icon?: string, handleClick: () => void }) {

  return (
    <li className="nav-item rounded header-button">
      <Link
        to={path}
        className="nav-link p-3 text-neutral-300 text-decoration-none icon-link"
        onClick={handleClick}
      >
        {text}
        {icon && <img src={icon} alt="icon" aria-hidden="true" />}
      </Link>
    </li>
  )
}

export default HeaderButton;