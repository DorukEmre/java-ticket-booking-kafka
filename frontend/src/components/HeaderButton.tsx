import { Link } from "react-router-dom";

interface Props {
  path: string;
  text: string;
  icon?: string;
  handleClick: () => void;
  className?: string;
}

function HeaderButton({ path, text, icon, handleClick, className }: Props) {

  return (
    <li className={`nav-item rounded header-button ${className || ""}`}>
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