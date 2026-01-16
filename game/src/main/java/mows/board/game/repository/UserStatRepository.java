package mows.board.game.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import mows.board.game.entity.UserStat;

public interface UserStatRepository extends JpaRepository<UserStat, Long> {

}
