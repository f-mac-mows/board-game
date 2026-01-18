
package mows.board.game.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameMessage {
    public enum MessageType {
        ENTER, TALK, LEAVE
    }

    private MessageType type;
    private Long roomId;
    private String sender;
    private String message;
    private int currentPlayers;
}
