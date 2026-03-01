package booksale.service;

import booksale.dto.request.LoginRequest;
import booksale.dto.request.RegisterRequest;
import booksale.dto.response.LoginResponse;
import booksale.dto.response.UserResponse;
import booksale.entity.User;
import booksale.exceptions.DuplicateResourceException;
import booksale.exceptions.ResourceNotFoundException;
import booksale.repo.UserRepo;
import booksale.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepo userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

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
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        userRepository.save(user);
        User saved = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Xəta baş verdi"));
        return mapToResponse(saved);
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Bu email ilə istifadəçi tapılmadı"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Şifrə yanlışdır");
        }

        String token = jwtUtil.generateToken(user.getEmail());

        return new LoginResponse(user.getId(), user.getUsername(), user.getEmail(), token);
    }

    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("İstifadəçi tapılmadı"));
        return mapToResponse(user);
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(user -> mapToResponse(user))
                .collect(Collectors.toList());
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("İstifadəçi tapılmadı");
        }
        userRepository.deleteById(id);
    }

    private UserResponse mapToResponse(User user) {
        return new UserResponse(user.getId(), user.getUsername(), user.getEmail());
    }
}