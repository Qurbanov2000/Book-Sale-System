package booksale.service;

import booksale.dto.request.LoginRequest;
import booksale.dto.request.RegisterRequest;
import booksale.dto.response.LoginResponse;
import booksale.dto.response.UserResponse;
import booksale.entity.User;
import booksale.exceptions.DuplicateResourceException;
import booksale.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import booksale.repo.UserRepo;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepo userRepository;

    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Bu email artıq istifadə olunur");
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Bu username artıq istifadə olunur");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());

        User saved = userRepository.save(user);
        return mapToResponse(userRepository.save(user));
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Bu email ilə istifadəçi tapılmadı"));

        if (!user.getPassword().equals(request.getPassword())) {
            throw new RuntimeException("Şifrə yanlışdır");
        }

        return new LoginResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                "Uğurla daxil oldunuz"
        );
    }

    public UserResponse getUserById(Long id) {
        User user = (User) userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("İstifadəçi tapılmadı"));
        return mapToResponse(user);
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map( user -> mapToResponse((User) user))
                .collect(Collectors.toList());
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("İstifadəçi tapılmadı");
        }
        userRepository.deleteById(id);
    }

    private UserResponse mapToResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail()
        );
    }
}