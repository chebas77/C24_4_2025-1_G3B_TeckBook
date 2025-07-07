import { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { ROUTES } from '../config/apiConfig';

function OAuth2Redirect() {
  const navigate = useNavigate();

  useEffect(() => {
    const href = window.location.href;
    console.log("🌐 URL completa:", href);

    const queryParams = new URLSearchParams(window.location.search);
    const token = queryParams.get("token");
    const isIncomplete = queryParams.get("incomplete") === "true";
    const isNew = queryParams.get("new") === "true";

    console.log("🔐 Token:", token);
    console.log("🧩 Incompleto:", isIncomplete);
    console.log("🆕 Usuario nuevo:", isNew);

    if (token) {
      localStorage.setItem("token", token);

      let target = ROUTES.PROTECTED.DASHBOARD;
      if (isNew || isIncomplete) {
        target += "?incomplete=true";
      }

      window.location.href = target;
    } else {
      navigate(ROUTES.PUBLIC.LOGIN);
    }
  }, [navigate]);

  return <p>Redirigiendo... revisa la consola del navegador 🔍</p>;
}

export default OAuth2Redirect;