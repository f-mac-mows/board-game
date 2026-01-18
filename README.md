# Multi-BoardGame Platform Project
실시간 보드게임 서비스를 제공하는 고가용성 백엔드 시스템 프로젝트입니다

## Tech Stack
- **Language:** Java 17
- **Framework:** Spring Boot 3.x
- **Persistence:** PostgreSQL (User/Rank), mongoDB (Game Logs), Redis (Session)
- **Security:** Spring Security, JWT, OAuth 2.0
- **Infrastructure:** Docker, Ubuntu (UTM), GitHub Actions
- **Real-time:** WebSocket (STOMP)

## Project Roadmap

### Phase 1: Infrastructure & Authentification (현재 진행 중)
- [x] Ubuntu/Docker 기반 개발 환경 구축 (PostgreSQL, MongoDB)
- [x] Spring Security + JWT를 이용한 인증 시스템 구현
- [ ] 회원가입/로그인 및 소셜 로그인(OAuth2) 연동

### Phase 2: Core Game Logic (오목/Gomoku)
- [ ] 2D 배열 기반 승리 판별 알고리즘 구현
- [ ] WebSocket을 활용한 실시간 대전 기능 (1:1 매칭)
- [ ] 게임 진행 수순(Move Log) MongoDB 저장 및 복기 가능

### Phase 3: Expansion & Optimization
- [ ] 추가 게임 구현 (원카드, 블랙잭 등)
- [ ] Redis를 도입한 실시간 랭킹 및 시스템 대기열(Queue) 관리
- [ ] QueryDSL을 활용한 복잡한 전적 검색 최적화

### Phase 4: DevOps & Deployment
- [ ] GitHub Actions를 이용한 CI/CD 파이프라인 구축
- [ ] Docker Compose를 넘어선 Kubernetes(Minikube) 오케스트레이션 적용
- [ ] Prometheus & Grafana를 활용한 서버 모니터링 구축

### 🗄️ Database Design
- **PostgreSQL**: User, Assets, Ranking (Transactional data)
  - `users`: 기본 계정 정보 및 OAuth 정보
  - `user_assets`: 재화(Gold, Point) 정보
  - `user_stats`: 게임 타입별 Level, EXP, MMR (게임별 독립적 숙련도 관리)
- **MongoDB**: Game Move Logs, Activity Logs (Big data / Flexible schema)
  - `game_records`: 게임 결과 요약 데이터
  - `game_move_logs`: 복기용 상세 수순 데이터
- **Redis**: Real-time Room info, Matchmaking Queue, Session management

## How to Run
```bash
docker compose up -d
./gradlew bootRun

## DB 접속
`docker exec -it board-game-db psql -U user -d game_db`