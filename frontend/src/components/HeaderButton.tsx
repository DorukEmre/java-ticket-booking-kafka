import { Link } from "react-router-dom";

function HeaderButton({ path, text, icon }
  : { path: string; text: string, icon?: string }) {
  return (
    <Link to={path} className="bg-back-100 text-decoration-none p-3 rounded icon-link header-button">
      {text}
      {icon && <img src={icon} alt="icon" aria-hidden="true" />}
    </Link>
  )
}

export default HeaderButton;