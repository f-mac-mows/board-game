package mows.board.game.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "game_rooms")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class GameRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String title;
    private String hostNickname;
    private int maxPlayers;

    @Builder.Default
    private int currentPlayers = 1;

    @Builder.Default
    private boolean isStarted = false;

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    public void join() {
        if (this.currentPlayers >= this.maxPlayers) {
            throw new IllegalStateException("방이 가득 찼습니다.");
        }
        if (this.isStarted) {
            throw new IllegalStateException("이미 시작된 게임입니다.");
        }
        this.currentPlayers++;
    }

    public void leave() {
        if (this.currentPlayers > 0) {
            this.currentPlayers--;
        }
    }
}
