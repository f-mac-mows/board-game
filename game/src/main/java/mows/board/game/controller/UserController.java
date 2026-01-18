package mows.board.game.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import mows.board.game.dto.UserProfileReponse;
import mows.board.game.entity.User;
import mows.board.game.repository.UserRepository;
import mows.board.game.service.UserService;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;
    
    @PatchMapping("/nickname")
    @Transactional
    public ResponseEntity<String> updateNickname(@RequestBody String nickname,
                                                @AuthenticationPrincipal String email) {

        if (userRepository.existsByNickname(nickname)) {
            return ResponseEntity.badRequest().body("이미 사용 중인 닉네임입니다.");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        user.updateNickname(nickname);
        
        return ResponseEntity.ok("닉네임 설정 완료");
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileReponse> getMyInfo(@AuthenticationPrincipal String email) {
        UserProfileReponse profile = userService.getUserProfile(email);
        return ResponseEntity.ok(profile);
    }
}
