package kpi.zakrevskyi.neurolib.controller;

import jakarta.validation.Valid;
import kpi.zakrevskyi.neurolib.domain.dto.request.LoginRequestDto;
import kpi.zakrevskyi.neurolib.domain.dto.request.RefreshTokenRequestDto;
import kpi.zakrevskyi.neurolib.domain.dto.request.RegisterRequestDto;
import kpi.zakrevskyi.neurolib.domain.dto.response.AuthResponseDto;
import kpi.zakrevskyi.neurolib.service.AuthService;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Register a new user")
    @PostMapping("/sign-up")
    public ResponseEntity<AuthResponseDto> register(@Valid @RequestBody RegisterRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @Operation(summary = "Authenticate user and issue access/refresh tokens")
    @PostMapping("/sign-in")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody LoginRequestDto request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @Operation(summary = "Refresh access token using refresh token")
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDto> refresh(@Valid @RequestBody RefreshTokenRequestDto request) {
        return ResponseEntity.ok(authService.refresh(request));
    }
}
