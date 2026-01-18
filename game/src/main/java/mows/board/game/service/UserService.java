package mows.board.game.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import mows.board.game.config.JwtTokenProvider;
import mows.board.game.dto.UserProfileReponse;
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
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public void register(String email, String password, String nickname) {
        // 닉네임 중복 확인
        if (userRepository.existsByNickname(nickname)) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다");
        }
        // 1. 중복 가입 확인
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("이미 존재하는 이메일입니다.");
        }

        // 2. 유저 저장
        User user = User.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .nickname(nickname)
                .oauthProvider(OAuthProvider.LOCAL)
                .build();
        userRepository.save(user);

        // 3. 초기 자산 설정
        UserAsset asset = UserAsset.builder()
                .user(user)
                .gold(1000L)
                .point(0L)
                .build();
        userAssetRepository.save(asset);

        // 4. 게임별 초기 스탯 생성
        for (GameType type : GameType.values()) {
            UserStat stat = UserStat.builder()
                    .user(user)
                    .gameType(type)
                    .build();
            userStatRepository.save(stat);
        }
    }

    public String login(String email, String password) {
        // 1. 이메일로 유저 찾기
        User user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("가입되지 않은 이메일입니다."));

        // 2. 비밀번호 일치 확인
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // 3. 토큰 생성 및 반환
        return jwtTokenProvider.createToken(user.getEmail());
    }

    public UserProfileReponse getUserProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다,"));

        return UserProfileReponse.builder()
                .email(user.getEmail())
                .nickname(user.getNickname())
                .createdAt(user.getCreatedAt())
                .build();
    }

    public Boolean checkNickname(String nickname) {
        return userRepository.existsByNickname(nickname);
    }
}
