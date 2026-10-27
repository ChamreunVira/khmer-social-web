package com.kh.chamreunvira.springboot.controller;

import com.kh.chamreunvira.springboot.data.ApiResponse;
import com.kh.chamreunvira.springboot.dto.AuthRequest;
import com.kh.chamreunvira.springboot.dto.UserRequest;
import com.kh.chamreunvira.springboot.dto.UserResponse;
import com.kh.chamreunvira.springboot.security.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping(value = "/register" , consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<UserResponse>> register(@ModelAttribute UserRequest request) throws Exception {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("User register successfully." , authService.register(request)));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserResponse>> login(@RequestBody AuthRequest request) {
        UserResponse response = authService.login(request);
        return ResponseEntity.ok().body(ApiResponse.success("User login successfully." , response));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<?>> logout() {
        try{
            ResponseCookie cookie = ResponseCookie
                    .from("token" , "")
                    .httpOnly(true)
                    .sameSite("Strict")
                    .maxAge(0)
                    .secure(false)
                    .build();
            return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE , cookie.toString()).body(ApiResponse.success("Logout successfully."));
        }catch (Exception ex) {
            log.info("Logout is fail.");
            throw new RuntimeException("Logout is fail.");
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserResponse>> getProfile() {
        return ResponseEntity.ok().body(ApiResponse.success("User profile." , authService.getProfile()));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long userId) {
        return ResponseEntity.ok().body(ApiResponse.success("User profile fetched.", authService.getUserById(userId)));
    }

    @GetMapping("/is-authenticated")
    public ResponseEntity<?> isAuthenticated(@CurrentSecurityContext(expression = "authentication.name") String email) {
        try {
            return ResponseEntity.ok().body(!Objects.equals(email, "anonymousUser"));
        }catch (Exception ex) {
            throw new RuntimeException("User isn't authenticated.");
        }
    }


}
