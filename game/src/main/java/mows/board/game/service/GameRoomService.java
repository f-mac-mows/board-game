package mows.board.game.service;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import mows.board.game.dto.CreateRoomRequest;
import mows.board.game.dto.GameMessage;
import mows.board.game.entity.GameRoom;
import mows.board.game.repository.GameRoomRepository;

@Service
@RequiredArgsConstructor
public class GameRoomService {

    private final GameRoomRepository gameRoomRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final SimpMessagingTemplate messagingTemplate;

    // Redis 키 접두사
    private static final String USER_ROOM_KEY = "user_at_room:";

    // 참여 여부 확인
    private void validateUserNotInAnyRoom(String nickname) {
        if (Boolean.TRUE.equals(redisTemplate.hasKey(USER_ROOM_KEY + nickname))) {
            throw new IllegalStateException("이미 참여 중인 방이 있습니다.");
        }
    }

    @Transactional
    public GameRoom createRoom(CreateRoomRequest dto, String nickname) {
        validateUserNotInAnyRoom(nickname); // 중복 체크

        GameRoom room = GameRoom.builder()
                        .title(dto.getTitle())
                        .hostNickname(nickname)
                        .maxPlayers(dto.getMaxPlayers())
                        .build();
        GameRoom savedRoom = gameRoomRepository.save(room);
        
        // Redis에 유저 상태 저장
        redisTemplate.opsForValue().set(USER_ROOM_KEY + nickname, savedRoom.getId(), 24, TimeUnit.HOURS);

        return savedRoom;
    }

    public List<GameRoom> getAllWaitingRooms() {
        return gameRoomRepository.findByIsStartedFalse();
    }

    @Transactional
    public void joinRoom(Long roomId, String nickname) {
        validateUserNotInAnyRoom(nickname);

        GameRoom room = gameRoomRepository.findById(roomId)
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 방입니다."));

        room.join();

        redisTemplate.opsForValue().set(USER_ROOM_KEY + nickname, roomId, 24, TimeUnit.HOURS);
        
        // 알림 전송: /topic/room/{roomId}
        GameMessage enterMessage = GameMessage.builder()
                .type(GameMessage.MessageType.ENTER)
                .roomId(roomId)
                .sender(nickname)
                .message(nickname + "님이 입장하셨습니다.")
                .currentPlayers(room.getCurrentPlayers())
                .build();

        messagingTemplate.convertAndSend("/topic/room/" + roomId, enterMessage);
    }

    @Transactional
    public void leaveRoom(Long roomId, String nickname) {
        GameRoom room = gameRoomRepository.findById(roomId).orElseThrow();
        room.leave();

        // Redis에서 유저 상태 삭제
        redisTemplate.delete(USER_ROOM_KEY + nickname);

        // 퇴장 알림 + 인원 감소 반영
        GameMessage leaveMessage = GameMessage.builder()
                .type(GameMessage.MessageType.LEAVE)
                .roomId(roomId)
                .sender(nickname)
                .message(nickname + "님이 나갔습니다.")
                .currentPlayers(room.getCurrentPlayers())
                .build();

        messagingTemplate.convertAndSend("/topic/room/" + roomId, leaveMessage);
    }

    public void sendChatMessage(GameMessage message) {
        messagingTemplate.convertAndSend("/topic/room/" + message.getRoomId(), message);
    }
}
