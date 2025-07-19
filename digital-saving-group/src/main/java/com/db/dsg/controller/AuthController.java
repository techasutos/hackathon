package com.db.dsg.controller;

import com.db.dsg.dtos.AuthRequest;
import com.db.dsg.dtos.MemberUserDto;
import com.db.dsg.dtos.RegisterRequest;
import com.db.dsg.model.MemberUser;
import com.db.dsg.service.impl.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        MemberUser user = authService.register(request);
        String token = authService.generateToken(user);
        return ResponseEntity.ok(Map.of(
                "message", "Registration submitted for approval",
                "user", MemberUserDto.from(user),
                "token", token
        ));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        MemberUser user = authService.authenticate(request);
        String token = authService.generateToken(user);
        return ResponseEntity.ok(Map.of(
                "token", token,
                "user", MemberUserDto.from(user)
        ));
    }

    @GetMapping("/me")
    public ResponseEntity<MemberUserDto> getCurrentUser(@AuthenticationPrincipal MemberUser user) {
        return ResponseEntity.ok(MemberUserDto.from(user));
    }
}