package kpi.zakrevskyi.neurolib.service.implementation;

import kpi.zakrevskyi.neurolib.domain.entity.User;
import kpi.zakrevskyi.neurolib.domain.entity.RefreshToken;
import kpi.zakrevskyi.neurolib.security.JwtService;
import kpi.zakrevskyi.neurolib.domain.dto.request.LoginRequestDto;
import kpi.zakrevskyi.neurolib.domain.dto.request.RefreshTokenRequestDto;
import kpi.zakrevskyi.neurolib.domain.dto.request.RegisterRequestDto;
import kpi.zakrevskyi.neurolib.domain.dto.response.AuthResponseDto;
import kpi.zakrevskyi.neurolib.service.AuthService;
import kpi.zakrevskyi.neurolib.service.RefreshTokenService;
import kpi.zakrevskyi.neurolib.service.UserService;
import kpi.zakrevskyi.neurolib.service.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final String TOKEN_TYPE = "Bearer";

    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Override
    public AuthResponseDto register(RegisterRequestDto registerRequestDto) {
        User user = userService.register(registerRequestDto);
        return buildAuthResponse(user);
    }

    @Override
    public AuthResponseDto login(LoginRequestDto loginRequestDto) {
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDto.email(), loginRequestDto.password())
            );
        } catch (BadCredentialsException ex) {
            throw new UnauthorizedException("Invalid email or password");
        }

        User user = userService.findByEmail(loginRequestDto.email())
            .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));
        return buildAuthResponse(user);
    }

    @Override
    public AuthResponseDto refresh(RefreshTokenRequestDto refreshTokenRequestDto) {
        RefreshToken currentRefreshToken = refreshTokenService.validateToken(refreshTokenRequestDto.refreshToken());
        refreshTokenService.revokeToken(currentRefreshToken);
        return buildAuthResponse(currentRefreshToken.getUser());
    }

    private AuthResponseDto buildAuthResponse(User user) {
        String accessToken = jwtService.generateToken(user.getEmail());
        RefreshToken refreshToken = refreshTokenService.createToken(user);
        return new AuthResponseDto(
            accessToken,
            TOKEN_TYPE,
            jwtService.getExpirationMs(),
            refreshToken.getToken(),
            refreshTokenService.getExpirationMs()
        );
    }
}
