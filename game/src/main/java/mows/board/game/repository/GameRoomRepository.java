package mows.board.game.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import mows.board.game.entity.GameRoom;

public interface GameRoomRepository extends JpaRepository<GameRoom, Long> {
    List<GameRoom> findByIsStartedFalse();
}
