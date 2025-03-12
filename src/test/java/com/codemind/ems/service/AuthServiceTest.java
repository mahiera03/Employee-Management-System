package com.codemind.ems.service;

import com.codemind.ems.config.JwtUtil;
import com.codemind.ems.dto.AuthRequest;
import com.codemind.ems.dto.AuthResponse;
import com.codemind.ems.dto.RegisterRequest;
import com.codemind.ems.entity.User;
import com.codemind.ems.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    private User user;
    private RegisterRequest registerRequest;
    private AuthRequest authRequest;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("testuser");
        user.setPassword("encodedpassword");

        registerRequest = new RegisterRequest();
        registerRequest.setUsername("testuser");
        registerRequest.setPassword("testpassword");

        authRequest = new AuthRequest();
        authRequest.setUsername("testuser");
        authRequest.setPassword("testpassword");
    }

    @Test
    void testRegister_SuccessfulRegistration() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("testpassword")).thenReturn("encodedpassword");

        String response = authService.register(registerRequest);

        assertThat(response).isEqualTo("User registered successfully!");

        verify(userRepository, times(1)).findByUsername("testuser");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testRegister_UsernameAlreadyExists_ShouldThrowException() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Username already exists");

        verify(userRepository, times(1)).findByUsername("testuser");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testAuthenticate_SuccessfulLogin() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("testpassword", "encodedpassword")).thenReturn(true);
        when(jwtUtil.generateToken("testuser")).thenReturn("mocked-jwt-token");

        AuthResponse response = authService.authenticate(authRequest);

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("mocked-jwt-token");

        verify(userRepository, times(1)).findByUsername("testuser");
        verify(passwordEncoder, times(1)).matches("testpassword", "encodedpassword");
        verify(jwtUtil, times(1)).generateToken("testuser");
    }

    @Test
    void testAuthenticate_InvalidUsername_ShouldThrowException() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.authenticate(authRequest))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("Invalid username or password");

        verify(userRepository, times(1)).findByUsername("testuser");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtUtil, never()).generateToken(anyString());
    }

    @Test
    void testAuthenticate_InvalidPassword_ShouldThrowException() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("testpassword", "encodedpassword")).thenReturn(false);

        assertThatThrownBy(() -> authService.authenticate(authRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid username or password");

        verify(userRepository, times(1)).findByUsername("testuser");
        verify(passwordEncoder, times(1)).matches("testpassword", "encodedpassword");
        verify(jwtUtil, never()).generateToken(anyString());
    }

    @Test
    void testRefreshToken_Success() {
        when(jwtUtil.extractUsername("valid-token")).thenReturn("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(jwtUtil.generateToken("testuser")).thenReturn("new-jwt-token");

        AuthResponse response = authService.refreshToken("valid-token");

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("new-jwt-token");

        verify(jwtUtil, times(1)).extractUsername("valid-token");
        verify(userRepository, times(1)).findByUsername("testuser");
        verify(jwtUtil, times(1)).generateToken("testuser");
    }

    @Test
    void testRefreshToken_InvalidToken_ShouldThrowException() {
        when(jwtUtil.extractUsername("invalid-token")).thenThrow(new IllegalArgumentException("Invalid or expired token"));

        assertThatThrownBy(() -> authService.refreshToken("invalid-token"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid or expired token");

        verify(jwtUtil, times(1)).extractUsername("invalid-token");
        verify(userRepository, never()).findByUsername(anyString());
        verify(jwtUtil, never()).generateToken(anyString());
    }
}
