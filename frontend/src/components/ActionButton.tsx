import { useState, useRef, useEffect } from "react";

function ActionButton({ text, icon, animDisabled = false, clickDisabled = false }
  : { text: string; icon?: string, animDisabled?: boolean, clickDisabled?: boolean }) {
  const [showIcon, setShowIcon] = useState(false);
  const timeoutRef = useRef<number | null>(null);

  function displayIcon() {
    if (!icon)
      return;

    setShowIcon(true);

    if (timeoutRef.current) {
      window.clearTimeout(timeoutRef.current);
    }

    timeoutRef.current = window.setTimeout(() => {
      setShowIcon(false);
      timeoutRef.current = null;
    }, 1000);
  }

  const handleClick = () => {
    if (animDisabled || clickDisabled)
      return;
    displayIcon();
  };

  useEffect(() => {
    return () => {
      if (timeoutRef.current) window.clearTimeout(timeoutRef.current);
    };
  }, []);

  return (
    <button
      type="submit"
      className="px-4 py-2 text-compl-300 border-2 border-compl-300 control"
      style={{ width: "200px", height: "42px" }}
      onClick={handleClick}
      disabled={clickDisabled}
    >
      {showIcon && icon ? (
        <img src={icon} alt="" style={{ display: "inline-block" }} />
      ) : (
        text
      )}
    </button>
  );
}

export default ActionButton;