package mows.board.game.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import mows.board.game.entity.UserAsset;

public interface UserAssetRepository extends JpaRepository<UserAsset, Long> {
    
}
