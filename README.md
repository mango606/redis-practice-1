## 실습: Redis 기본 기능

### 프로젝트 설정
- **사용 기술**: Spring Boot, Redis
- **주요 의존성**: `spring-boot-starter-data-redis`, `spring-boot-starter-web`

### 환경 설정
- `application.properties` 파일 대신 `application.yaml` 사용한다.
- Redis 접속 설정: `host`, `port`, `username`, `password` 정보를 명시한다.

### 데이터 모델과 리포지토리
- `Item` 클래스: Redis에 저장될 객체 모델
- `ItemRepository`: Spring Data의 `CrudRepository`를 확장하여 기본적인 CRUD 기능을 제공한다.

### 기본 CRUD 작업
- CRUD 작업은 테스트 코드를 통해 검증한다.
- Redis에서 데이터 조회, 수정, 삭제 작업 수행한다.

### RedisTemplate의 활용
- `StringRedisTemplate`은 문자열의 조작을 위한 것이 아니다. 데이터를 Java 내부에서 문자열로 취급하기 위해서 쓰는 것이다.
- **사용자 정의 RedisTemplate**: 복잡한 객체 타입 (예: DTO)을 Redis에 저장하기 위한 사용자 정의 `RedisTemplate`을 구현한다.
- **연산 인터페이스**: `ValueOperations`, `SetOperations`, `HashOperations` 등을 활용하여 다양한 Redis 연산 수행한다.
   - `HashOperations`는 키-값 쌍의 복잡한 데이터 구조를 관리할 때 사용되며, 상대적으로 복잡한 사용 방법을 가진다.

&nbsp;

---

&nbsp;

## 실습2: Redis 설치 및 기본 명령어 사용해보기

```
1. 1-2 강의 영상에 소개된 방식으로 Redis를 컴퓨터에 설치해보자.
2. 내 블로그 글 별 조회수를 Redis로 확인하고 싶다.
   1. 블로그 URL의 PATH는 `/articles/{id}` 형식이다.
   2. 로그인 여부와 상관없이 새로고침 될때마다 조회수가 하나 증가한다.
   3. 이를 관리하기 위해 적당한 데이터 타입을 선정하고,
   4. 사용자가 임의의 페이지에 접속할 때 실행될 명령을 작성해보자.
3. 블로그에 로그인한 사람들의 조회수와 가장 많은 조회수를 기록한 글을 Redis로 확인하고 싶다.
   1. 블로그 URL의 PATH는 `/articles/{id}` 형식이다.
   2. 로그인 한 사람들의 계정은 영문으로만 이뤄져 있다.
   3. 이를 관리하기 위해 적당한 데이터 타입을 선정하고,
   4. 사용자가 임의의 페이지에 접속할 때 실행될 명령을 작성해보자.
   5. 만약 상황에 따라 다른 명령이 실행되어야 한다면, 주석으로 추가해보자.
```

### Docker로 Redis Insight 설치
Redis Insight의 정보를 남기고 싶다면, 볼륨을 추가해야 한다.
```bash
docker run -d --name redisinsight -p 5540:5540 redis/redisinsight:latest -v redisinsight:/data
```

### 블로그 조회수 관리

- **데이터 타입 선택**: 각 블로그 글의 조회수는 Redis의 String 데이터 타입을 사용하여 관리한다.
- **기본 명령어**:
  - **조회수 증가**: 블로그 글 접속 시 조회수를 1 증가시키는 명령이다.
    ```redis
    INCR articles:{id}
    ```
  - **예시**:
    ```redis
    INCR articles:1  # 글 ID가 1인 블로그 글의 조회수를 증가한다
    ```

- **특정 날짜의 조회수 관리**: 오늘의 조회수만 별도로 관리하고 싶을 때 사용하는 명령이다.
  - **조회수 증가 및 날짜 설정**:
    ```redis
    INCR articles:{id}:today
    RENAME articles:{id}:today articles:{id}:{date}
    ```
  - **예시**:
    ```redis
    INCR articles:1:today
    RENAME articles:1:today articles:1:2025-03-12
    ```

### 로그인 사용자의 조회수 관리

- **데이터 타입 선택**: 로그인 사용자의 조회수는 중복 방지를 위해 Set 데이터 타입을 사용하여 관리한다.
- **기본 명령어**:
  - **사용자 조회수 추가**:
    ```redis
    SADD articles:{id} {username}
    ```
  - **조회수 확인**:
    ```redis
    SCARD articles:{id}
    ```
  - **예시**:
    ```redis
    SADD articles:1 alex  # 사용자 alex가 글 ID 1을 조회한다
    SCARD articles:1      # 글 ID 1의 총 조회수 확인한다
    ```

### 가장 많은 조회수를 기록한 글 확인

- **데이터 타입 선택**: 글의 랭킹을 관리하기 위해 Sorted Set 데이터 타입을 사용한다.
- **기본 명령어**:
  - **조회수에 따른 랭킹 업데이트**:
    ```redis
    ZINCRBY articles:ranks 1 articles:{id}
    ```
  - **최고 조회수 글 확인**:
    ```redis
    ZREVRANGE articles:ranks 0 1
    ```
  - **예시**:
    ```redis
    ZINCRBY articles:ranks 1 articles:1  # 글 ID 1의 랭크를 증가한다
    ZREVRANGE articles:ranks 0 1         # 가장 많이 조회된 글 확인한다
    ```
