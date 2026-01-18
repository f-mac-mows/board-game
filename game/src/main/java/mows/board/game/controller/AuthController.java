package mows.board.game.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import mows.board.game.dto.EmailRequest;
import mows.board.game.dto.EmailVerifyRequest;
import mows.board.game.dto.LoginRequest;
import mows.board.game.dto.SignUpRequest;
import mows.board.game.service.EmailService;
import mows.board.game.service.LogoutService;
import mows.board.game.service.UserService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final LogoutService logoutService;
    private final EmailService emailService;

    // 인증 번호 발송
    @PostMapping("/email-request")
    public ResponseEntity<String> requestEmailAuth(@RequestBody EmailRequest request) {
        emailService.sendVerificationEmail(request.getEmail());
        return ResponseEntity.ok("인증 번호가 발송되었습니다.");
    }

    // 인증 번호 확인
    @PostMapping("/email-verify")
    public ResponseEntity<Boolean> verifyEmail(@RequestBody EmailVerifyRequest request) {
        boolean isVerified = emailService.verifyCode(request.getEmail(), request.getCode());
        if (isVerified) {
            return ResponseEntity.ok(true);
        } else {
            return ResponseEntity.badRequest().body(false);
        }
    }
    
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

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        String token = resolveToken(request);
        if (token != null) {
            logoutService.logout(token);
        }
        return ResponseEntity.ok("로그아웃 되었습니다.");
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    @GetMapping("/check-nickname")
    public ResponseEntity<Boolean> checkNickname(@RequestParam String nickname) {
        return ResponseEntity.ok(userService.checkNickname(nickname));
    }
}
