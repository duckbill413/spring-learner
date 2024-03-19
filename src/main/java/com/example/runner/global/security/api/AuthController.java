package com.example.runner.global.security.api;

import com.example.runner.global.common.BaseResponse;
import com.example.runner.global.common.code.SuccessCode;
import com.example.runner.global.security.dto.TokenDto;
import com.example.runner.global.security.exception.RefreshTokenException;
import com.example.runner.global.security.service.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequestMapping("/api/token")
@RequiredArgsConstructor
@Tag(name = "인증", description = "토큰 인증 컨트롤러")
public class AuthController {

    private final JwtService jwtService;

    @Operation(summary = "액세스 토큰 재발급 요청하기", description = "액세스 토큰 없거나 만료됐으면 재발급 요청하기")
    @PostMapping("/refresh")
    public ResponseEntity<BaseResponse<TokenDto>> rotateJwtTokensRequest(
            @Valid @NotNull @CookieValue(value = "refresh-token") String refreshToken) throws RefreshTokenException {
        TokenDto tokenDto = jwtService.rotateJwtTokens(refreshToken);

        return BaseResponse.success(
                SuccessCode.SELECT_SUCCESS,
                tokenDto
        );
    }
}
