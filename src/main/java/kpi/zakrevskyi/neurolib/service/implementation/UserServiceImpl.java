package kpi.zakrevskyi.neurolib.service.implementation;

import java.util.Optional;
import java.util.UUID;

import kpi.zakrevskyi.neurolib.domain.dto.request.RegisterRequestDto;
import kpi.zakrevskyi.neurolib.domain.entity.User;
import kpi.zakrevskyi.neurolib.repository.UserRepository;
import kpi.zakrevskyi.neurolib.service.UserService;
import kpi.zakrevskyi.neurolib.service.exception.BadRequestException;
import kpi.zakrevskyi.neurolib.service.exception.ConflictException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public User register(RegisterRequestDto registerRequestDto) {
        if (!registerRequestDto.password().equals(registerRequestDto.confirmPassword())) {
            throw new BadRequestException("Password and confirmPassword do not match");
        }

        if (userRepository.existsByEmail(registerRequestDto.email())) {
            throw new ConflictException("Email already registered");
        }
        if (userRepository.existsByUsername(registerRequestDto.username())) {
            throw new ConflictException("Username already taken");
        }

        User user = new User();
        user.setEmail(registerRequestDto.email());
        user.setUsername(registerRequestDto.username());
        user.setPasswordHash(passwordEncoder.encode(registerRequestDto.password()));
        user.setFullName(registerRequestDto.fullName());

        return userRepository.save(user);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return userRepository.findById(id);
    }
}
