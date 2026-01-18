package mows.board.game.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;
import mows.board.game.dto.GameMessage;
import mows.board.game.service.GameRoomService;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final GameRoomService gameRoomService;

    @MessageMapping("/chat/message")
    public void message(GameMessage message) {
        message.setType(GameMessage.MessageType.TALK);
        gameRoomService.sendChatMessage(message);
    }
}
