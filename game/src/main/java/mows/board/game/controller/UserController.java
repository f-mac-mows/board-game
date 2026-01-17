package mows.board.game.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import mows.board.game.dto.UserProfileReponse;
import mows.board.game.service.UserService;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    
    @GetMapping("/me")
    public ResponseEntity<UserProfileReponse> getMyInfo(@AuthenticationPrincipal String email) {
        UserProfileReponse profile = userService.getUserProfile(email);
        return ResponseEntity.ok(profile);
    }
}
