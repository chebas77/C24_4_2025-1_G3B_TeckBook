package com.usuario.backend.security.oauth2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private static final Logger logger = LoggerFactory.getLogger(CustomOAuth2UserService.class);

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        try {
            logger.info("Loading OAuth2 user info");
            Map<String, Object> attributes = oAuth2User.getAttributes();

            // Verificar que los atributos esperados estén presentes
            if (!attributes.containsKey("email")) {
                logger.error("Email attribute not found in OAuth2 user info");
                throw new OAuth2AuthenticationException(new OAuth2Error("missing_attribute"), "Email not found in OAuth2 user info");
            }

            String email = (String) attributes.get("email");
            logger.info("OAuth2 user email: {}", email);

            // Verificar dominio institucional
            if (!email.endsWith("@tecsup.edu.pe")) {
                logger.error("Email domain not allowed: {}", email);
                throw new OAuth2AuthenticationException(new OAuth2Error("invalid_domain"), "Solo se permite acceso con correo institucional tecsup.edu.pe");
            }

            logger.info("OAuth2 user loaded successfully");

            return new DefaultOAuth2User(
                    Collections.singleton(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_USER")),
                    attributes,
                    "email");

        } catch (OAuth2AuthenticationException ex) {
            logger.error("OAuth2 authentication exception: {}", ex.getMessage(), ex);
            throw ex;
        } catch (Exception ex) {
            logger.error("Error processing OAuth2 user: {}", ex.getMessage(), ex);
            throw new OAuth2AuthenticationException(new OAuth2Error("processing_error"), ex);
        }
    }
}