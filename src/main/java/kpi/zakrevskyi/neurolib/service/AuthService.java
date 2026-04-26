package kpi.zakrevskyi.neurolib.service;

import kpi.zakrevskyi.neurolib.domain.dto.request.LoginRequestDto;
import kpi.zakrevskyi.neurolib.domain.dto.request.RegisterRequestDto;
import kpi.zakrevskyi.neurolib.domain.dto.response.AuthResponseDto;

public interface AuthService {
    AuthResponseDto login(LoginRequestDto loginRequestDto);

    AuthResponseDto register(RegisterRequestDto registerRequestDto);
}
