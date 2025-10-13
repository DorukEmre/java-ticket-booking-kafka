import { Link } from "react-router-dom";

function HeaderButton({ path, text, icon }
  : { path: string; text: string, icon?: string }) {

  return (
    <li className="nav-item p-2 rounded header-button">
      <Link to={path} className="nav-link text-neutral-300 text-decoration-none icon-link">
        {text}
        {icon && <img src={icon} alt="icon" aria-hidden="true" />}
      </Link>
    </li>
  )
}

export default HeaderButton;