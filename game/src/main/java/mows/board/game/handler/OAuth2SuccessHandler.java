package mows.board.game.handler;

import java.io.IOException;

import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import mows.board.game.config.JwtTokenProvider;
import mows.board.game.entity.User;
import mows.board.game.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        User user = userRepository.findByEmail(email).orElseThrow();
        
        String token = jwtTokenProvider.createToken(email);
        
        String targetUrl;
        if (user.isProfileCompeted()) {
            targetUrl = "http://localhost:3000/login-success?token=" + token;
        } else {
            targetUrl = "http://localhost:3000/set-nickname?token=" + token;
        }

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
