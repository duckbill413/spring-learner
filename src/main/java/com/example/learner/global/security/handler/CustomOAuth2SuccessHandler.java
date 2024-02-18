package com.example.learner.global.security.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    @Value("${client.redirect-url.success}")
    private String REDIRECT_URI_SUCCESS;

    @Value("${client.redirect-url.anonymous}")
    private String REDIRECT_URI_ANONYMOUS;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException {

        log.trace("5. 유저의 소셜 정보에서 이메일을 불러옵니다.");
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = (String) oAuth2User.getAttributes().get("email");
        CustomUserDetails userDetails = userDetailsService.loadUserByUsername(email);

        log.trace("6. 유저에게 Access Token과 Refresh Token을 발급합니다.");
        String accessToken = jwtService.createAccessToken(userDetails);
        String refreshToken = jwtService.createRefreshToken(userDetails);
        UriComponentsBuilder uriComponentsBuilder;

        if (isAnonymousMember(userDetails)) {
            log.trace("10. 추가 기입 정보를 입력하지 않은 유저입니다. 추가 정보 기입 페이지로 URL을 설정합니다.");
            uriComponentsBuilder = UriComponentsBuilder.fromUriString(REDIRECT_URI_ANONYMOUS);
        } else {
            log.trace("11. 추가 기입 정보를 입력한 유저입니다. 로그인 완료 페이지로 URL을 설정합니다.");
            uriComponentsBuilder = UriComponentsBuilder.fromUriString(REDIRECT_URI_SUCCESS);
        }

        String redirectURI = uriComponentsBuilder.queryParam("access-token", accessToken)
                .queryParam("refresh-token", refreshToken).toUriString();

        response.setHeader(HttpHeaders.SET_COOKIE, jwtService.buildResponseCookie(refreshToken).toString());
        response.sendRedirect(redirectURI);
    }

    public boolean isAnonymousMember(UserDetails userDetails) {
        if (userDetails.getAuthorities().isEmpty()) {
            throw new BaseExceptionHandler(ErrorCode.UNAUTHORIZED_USER_EXCEPTION);
        }
        return userDetails.getAuthorities().contains(new SimpleGrantedAuthority(Privilege.ANONYMOUS.name()));
    }
}