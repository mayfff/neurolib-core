package kpi.zakrevskyi.neurolib.service;

import java.util.Optional;
import java.util.UUID;

import kpi.zakrevskyi.neurolib.domain.dto.request.RegisterRequestDto;
import kpi.zakrevskyi.neurolib.domain.dto.request.UpdateUserRequestDto;
import kpi.zakrevskyi.neurolib.domain.entity.User;

public interface UserService {
    User register(RegisterRequestDto registerRequestDto);

    Optional<User> findByEmail(String email);

    Optional<User> findById(UUID id);

    User update(UUID id, UpdateUserRequestDto updateUserRequestDto);
}
