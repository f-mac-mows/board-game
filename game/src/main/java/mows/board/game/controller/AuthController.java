package mows.board.game.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import mows.board.game.dto.LoginRequest;
import mows.board.game.dto.SignUpRequest;
import mows.board.game.service.UserService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody SignUpRequest dto) {
        userService.register(dto.getEmail(), dto.getPassword(), dto.getNickname());
        return ResponseEntity.ok("회원가입 성공!");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest dto) {
        String token = userService.login(dto.getEmail(), dto.getPassword());
        return ResponseEntity.ok(token);
    }
}
