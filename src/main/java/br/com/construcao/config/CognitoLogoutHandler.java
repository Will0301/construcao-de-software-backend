//Em apis rest precisa de logout para o backend? pelo que eu sei o logout acontece jogando fora o token no front
/*
package br.com.construcao.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;

public class CognitoLogoutHandler extends SimpleUrlLogoutSuccessHandler {

    private String domain = "https://<SEU-DOMINIO-COGNITO>.auth.us-east-1.amazoncognito.com";
    private String logoutRedirectUrl = "https://d84l1y8p4kdic.cloudfront.net";
    private String userPoolClientId = "us-east-1_cJESLfNJj";

    @Override
    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        return UriComponentsBuilder
                .fromUri(URI.create(domain + "/logout"))
                .queryParam("client_id", userPoolClientId)
                .queryParam("logout_uri", logoutRedirectUrl)
                .encode(StandardCharsets.UTF_8)
                .build()
                .toUriString();
    }
}
 */
