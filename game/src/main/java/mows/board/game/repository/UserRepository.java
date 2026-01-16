package mows.board.game.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import mows.board.game.entity.User;

public interface UserRepository extends JpaRepository<User, Long>{
    Optional<User> findByEmail(String email);
}
