package mows.board.game.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import mows.board.game.dto.CreateRoomRequest;
import mows.board.game.entity.GameRoom;
import mows.board.game.service.GameRoomService;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class GameRoomController {

    private final GameRoomService gameRoomService;

    // 방 생성
    @PostMapping("/create")
    public ResponseEntity<GameRoom> createRoom(@RequestBody CreateRoomRequest dto,
                                            @AuthenticationPrincipal String nickname) {
        return ResponseEntity.ok(gameRoomService.createRoom(dto, nickname));                       
    }

    // 모든 대기 중인 방 목록 조회
    @GetMapping
    public ResponseEntity<List<GameRoom>> getRooms() {
        return ResponseEntity.ok(gameRoomService.getAllWaitingRooms());
    }

    // ID로 방 참가
    @PostMapping("/join/{roomId}")
    public ResponseEntity<String> joinRoom(@PathVariable Long roomId,
                                        @AuthenticationPrincipal String nickname) {
        gameRoomService.joinRoom(roomId, nickname);
        return ResponseEntity.ok("방에 참가하였습니다. ID: " + roomId);
    }

    @PostMapping("/{roomId}/leave")
    public ResponseEntity<String> leaveRoom(@PathVariable Long roomId,
                                        @AuthenticationPrincipal String nickname) {
        gameRoomService.leaveRoom(roomId, nickname);
        return ResponseEntity.ok("방 퇴장 성공");
    }
}