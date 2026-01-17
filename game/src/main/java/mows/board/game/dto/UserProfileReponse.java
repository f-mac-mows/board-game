package mows.board.game.dto;

import java.time.LocalDateTime;

public record UserProfileReponse(
    String email,
    String nickname,
    LocalDateTime createdAt
) {}
