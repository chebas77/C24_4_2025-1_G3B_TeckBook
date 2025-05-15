package tecsup.com.oauth2.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
public class AuthController {

    @GetMapping("/")
    @ResponseBody
    public String index() {
        return "Bienvenido al backend!";
    }

    @GetMapping("/login")
    @ResponseBody
    public String loginPage() {
        return "Página de login (serás redirigido a Google)";
    }

    @GetMapping("/success")
    @ResponseBody
    public String success(@AuthenticationPrincipal OAuth2User principal) {
        if (principal != null) {
            Map<String, Object> attributes = principal.getAttributes();
            return "¡Autenticación exitosa! Hola, " + attributes.get("name") + " (" + attributes.get("email") + ")";
        }
        return "¡Autenticación exitosa!";
    }
}
