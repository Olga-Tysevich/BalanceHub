package by.testtask.balancehub.controllers;

import by.testtask.balancehub.dto.req.UserLoginDTO;
import by.testtask.balancehub.dto.resp.LoggedUserDTO;
import by.testtask.balancehub.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

import static by.testtask.balancehub.utils.Constants.REFRESH_TOKEN_KEY;
import static by.testtask.balancehub.utils.Constants.TOKEN_TYPE;

@RestController
@RequiredArgsConstructor
@RequestMapping("v1/api/auth")
@Validated
public class AuthController {
    private final AuthService authService;

    @Operation(
            summary = "Login a user",
            description = "Authenticates the user with the provided credentials " +
                    "(email or phone and password) and returns the logged-in user data along with a refresh token in a cookie."
    )
    @ApiResponse(
            responseCode = "200",
            description = "User logged in successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = LoggedUserDTO.class))
    )
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid UserLoginDTO req) {
        LoggedUserDTO respBody = authService.loginUser(req);
        ResponseCookie cookie = createRefreshTokenCookie(respBody.getRefreshToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(respBody);
    }

    @Operation(
            summary = "Logout the user",
            description = "Logs out the user by clearing the refresh token cookie."
    )
    @ApiResponse(
            responseCode = "200",
            description = "User logged out successfully"
    )
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        ResponseCookie cookie = ResponseCookie.from(REFRESH_TOKEN_KEY, "")
                .httpOnly(true)
                .path("/v1/api/auth/refresh")
                .maxAge(0)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(HttpServletRequest request) {
        String refreshToken = extractRefreshToken(request);

        if (Objects.isNull(refreshToken) || refreshToken.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No refresh token provided");
        }

        LoggedUserDTO newTokens = authService.reLoginUser(refreshToken);
        ResponseCookie cookie = createRefreshTokenCookie(newTokens.getRefreshToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(newTokens);
    }

    private String extractRefreshToken(HttpServletRequest request) {
        // 1. Из куки
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (REFRESH_TOKEN_KEY.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        // 2. Из заголовка Authorization
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (Objects.nonNull(authHeader) && authHeader.startsWith(TOKEN_TYPE)) {
            return authHeader.substring(TOKEN_TYPE.length());
        }

        return null;
    }

    private ResponseCookie createRefreshTokenCookie(String refreshToken) {
        return ResponseCookie.from(REFRESH_TOKEN_KEY, refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/v1/api/auth/refresh")
                .sameSite("Strict")
                .build();
    }
}