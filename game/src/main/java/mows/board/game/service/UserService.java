package mows.board.game.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import mows.board.game.entity.User;
import mows.board.game.entity.UserAsset;
import mows.board.game.entity.UserStat;
import mows.board.game.repository.UserAssetRepository;
import mows.board.game.repository.UserRepository;
import mows.board.game.repository.UserStatRepository;
import mows.board.game.type.GameType;
import mows.board.game.type.OAuthProvider;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserAssetRepository userAssetRepository;
    private final UserStatRepository userStatRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public void register(String email, String password, String nickname) {
        // 1. 중복 가입 확인
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("이미 존재하는 이메일입니다.");
        }

        // 2. 유저 저장
        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setNickmname(nickname);
        user.setOauthProvider(OAuthProvider.LOCAL);
        userRepository.save(user);

        // 3. 초기 자산 설정
        UserAsset asset = new UserAsset();
        asset.setUser(user);
        asset.setGold(1000L);
        userAssetRepository.save(asset);

        // 4. 게임별 초기 스탯 생성
        for (GameType type : GameType.values()) {
            UserStat stat = new UserStat();
            stat.setUser(user);
            stat.setGameType(type);
            stat.setMmr(1000);
            stat.setLevel(1);
            userStatRepository.save(stat);
        }
    }
}
