package mows.board.game.service;

import java.util.List;

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
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public GameRoom createRoom(CreateRoomRequest dto, String nickname) {
        GameRoom room = GameRoom.builder()
                        .title(dto.getTitle())
                        .hostNickname(nickname)
                        .maxPlayers(dto.getMaxPlayers())
                        .build();
        return gameRoomRepository.save(room);
    }

    public List<GameRoom> getAllWaitingRooms() {
        return gameRoomRepository.findByIsStartedFalse();
    }

    @Transactional
    public void joinRoom(Long roomId, String nickname) {
        GameRoom room = gameRoomRepository.findById(roomId)
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 방입니다."));

        room.join();

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
