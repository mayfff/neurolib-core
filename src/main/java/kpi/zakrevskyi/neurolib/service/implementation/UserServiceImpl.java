package kpi.zakrevskyi.neurolib.service.implementation;

import java.util.Optional;
import java.util.UUID;

import kpi.zakrevskyi.neurolib.domain.FileType;
import kpi.zakrevskyi.neurolib.domain.dto.request.RegisterRequestDto;
import kpi.zakrevskyi.neurolib.domain.dto.request.UpdateUserRequestDto;
import kpi.zakrevskyi.neurolib.domain.entity.Role;
import kpi.zakrevskyi.neurolib.domain.entity.User;
import kpi.zakrevskyi.neurolib.repository.CommentRepository;
import kpi.zakrevskyi.neurolib.repository.UserRepository;
import kpi.zakrevskyi.neurolib.service.FileStorageService;
import kpi.zakrevskyi.neurolib.service.UserService;
import kpi.zakrevskyi.neurolib.service.exception.BadRequestException;
import kpi.zakrevskyi.neurolib.service.exception.ConflictException;
import kpi.zakrevskyi.neurolib.service.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final PasswordEncoder passwordEncoder;
    private final FileStorageService fileStorageService;

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
        user.setRole(Role.USER);

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

    @Override
    @Transactional
    public User update(UUID id, UpdateUserRequestDto updateUserRequestDto) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("User with id [%s] not found".formatted(id)));

        boolean usernameChanged = !user.getUsername().equals(updateUserRequestDto.username());
        if (usernameChanged && userRepository.existsByUsername(updateUserRequestDto.username())) {
            throw new ConflictException("Username already taken");
        }

        user.setFullName(updateUserRequestDto.fullName());
        user.setUsername(updateUserRequestDto.username());
        updatePasswordIfRequested(user, updateUserRequestDto);

        String storedProfileImageUrl = fileStorageService.uploadFile(
            updateUserRequestDto.profileImage(),
            FileType.AVATAR,
            user.getId().toString()
        );
        if (StringUtils.hasText(storedProfileImageUrl)) {
            user.setProfileImageUrl(storedProfileImageUrl);
        }

        return userRepository.save(user);
    }

    @Override
    @Transactional
    public String delete(UUID id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("User with id [%s] not found".formatted(id)));

        fileStorageService.deleteAllByOwner(FileType.AVATAR, user.getId().toString());

        commentRepository.deleteByUserId(id);
        userRepository.deleteLikedBooksByUserId(id);
        userRepository.deleteDislikedBooksByUserId(id);
        userRepository.deleteSavedBooksByUserId(id);
        userRepository.deleteById(id);
        return "User with id [%s] deleted".formatted(id);
    }

    @Override
    @Transactional
    public String deleteAvatar(UUID id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("User with id [%s] not found".formatted(id)));

        if (!StringUtils.hasText(user.getProfileImageUrl())) {
            return "User avatar not set";
        }

        fileStorageService.deleteAllByOwner(FileType.AVATAR, user.getId().toString());
        user.setProfileImageUrl(null);
        userRepository.save(user);
        return "User avatar deleted";
    }

    private void updatePasswordIfRequested(User user, UpdateUserRequestDto request) {
        boolean hasCurrentPassword = StringUtils.hasText(request.currentPassword());
        boolean hasNewPassword = StringUtils.hasText(request.newPassword());
        boolean hasConfirmPassword = StringUtils.hasText(request.confirmPassword());

        if (!hasCurrentPassword && !hasNewPassword && !hasConfirmPassword) {
            return;
        }

        if (!hasCurrentPassword || !hasNewPassword || !hasConfirmPassword) {
            throw new BadRequestException("Provide currentPassword, newPassword and confirmPassword");
        }
        if (!passwordEncoder.matches(request.currentPassword(), user.getPasswordHash())) {
            throw new BadRequestException("Current password is incorrect");
        }
        if (!request.newPassword().equals(request.confirmPassword())) {
            throw new BadRequestException("newPassword and confirmPassword do not match");
        }
        if (passwordEncoder.matches(request.newPassword(), user.getPasswordHash())) {
            throw new BadRequestException("New password must be different from current password");
        }

        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
    }
}
