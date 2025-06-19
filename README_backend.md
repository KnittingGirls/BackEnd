# 🛠️ Knitting Girls Backend 서버

본 문서는 Knitting Girls 프로젝트 중 **백엔드(API & 인증·게시판 기능 담당)** 모듈의 사용법과 구조를 정리한 README입니다.  
(ML 서버는 별도의 [README](../ml_server/README.md)를 참고하세요.)

---

## 목차

1. [프로젝트 개요](#프로젝트-개요)  
2. [주요 기능](#주요-기능)  
3. [요구사항](#요구사항)  
4. [설치 및 빌드](#설치-및-빌드)  
5. [실행 및 테스트](#실행-및-테스트)  
6. [Java 클래스 설명](#java-클래스-설명)  
7. [API 사용 예시](#api-사용-예시-postman--curl)  
8. [DB 스키마 및 덤프](#db-스키마-및-덤프)  
9. [라이선스](#라이선스)  

---

## 프로젝트 개요

- **목표**: 1인 가구 대상 AI 패턴 분석 결과(ML 서버)와 연동해  
  - 이미지 업로드 → 저장 → URL 반환  
  - 게시글·댓글·북마크 CRUD  
  - 카카오 OAuth2 기반 로그인 → JWT 토큰 발급  
  - 해시태그 검색, 작성자별 조회 등  

- **구성**  
  - `backend/` : Spring Boot 기반 REST API 서버  
  - `ml_server/` : Python FastAPI 기반 ML 서버 (별도 배포)

---

## 주요 기능

1. **인증(Authentication & Authorization)**  
2. **게시판(Post) 관리**  
3. **댓글(Comment) 관리**  
4. **이미지 업로드(Image)**  
5. **북마크(Bookmark) & 좋아요(Like) 토글**  
6. **해시태그 검색 & 작성자별 게시글 조회**  
7. **모델 서버 연동 → PDF 도안 생성**

---

## 요구사항

- **Java** ≥ 11  
- **Spring Boot** 2.5.x 이상  
- **Maven** 또는 **Gradle**  
- **MySQL** 5.7 이상 (이미 서버에 배포되어 있으므로 별도 실행 불필요)  
- **Redis** (선택) – 세션·캐시 용

---

## 설치 및 빌드

```bash
# 1) 리포지토리 클론
git clone https://github.com/your-org/knitting-girls-backend.git
cd knitting-girls-backend

# 2) 환경 설정
#    src/main/resources/application.yml 에
#    - MySQL 접속 정보(JDBC URL, 사용자/비밀번호)
#    - JWT 시크릿·토큰 만료 시간
#    - CORS 허용 도메인 등 수정

# 3) 빌드
# Maven
mvn clean package -DskipTests

# 또는 Gradle
./gradlew clean build -x test
```

---

## 실행 및 테스트

```bash
# 실행
java -jar target/knitting-girls-0.0.1-SNAPSHOT.jar

# Swagger UI
# http://localhost:8080/swagger-ui.html
```

---

## Java 클래스 설명

### configuration  
- **MultipartConfig**  
  파일 업로드(최대 크기, 임시 저장소) 설정  
- **SecurityConfig**  
  JWT 필터, 인증·인가 정책 설정  
- **WebConfig**  
  CORS, 메시지 변환, 인터셉터 설정  

### controller  
- **AuthController**  
  - `/api/auth/login` (카카오 OAuth2 → JWT 발급)  
- **PostController**  
  - `/posts` (게시글 CRUD, 해시태그·작성자별 조회)  
- **CommentController**  
  - `/posts/{postId}/comment` (댓글 CRUD)  
- **ImageController**  
  - `/images/upload` (MultipartFile 저장 → URL 반환)  
- **ModelServerController**  
  - `/predict` (ML 서버 연동 → PDF 도안 생성)  

### dto  
- **PostDto** : `content`, `hashtags`  
- **CommentDto** : `content`  
- **ImageDto** : `url`  

### entity  
- **User, Post, Comment, Image, Bookmark**  
  JPA @Entity 클래스  

### repository  
- **UserRepository, PostRepository, CommentRepository, ImageRepository, BookmarkRepository**  
  Spring Data JPA 인터페이스  

### security  
- **JwtTokenProvider**  
  JWT 생성·파싱·유효성 검사 메서드  

### service  
- **AuthService** : 로그인 로직, 사용자 조회 및 JWT 생성  
- **PostService** : 게시글 CRUD, 해시태그 파싱·검색  
- **ImageService** : 파일 저장 경로 결정, URL 생성  

### utils  
- **MultipartInputStreamFileResource**  
  MultipartFile → InputStreamResource 변환 유틸  

---

## API 사용 예시 (Postman / cURL)

> **Backend**: `http://43.201.186.153:8080`  
> **ML 서버**: `http://43.201.186.153:8000`

| Method | Path                                          | 설명                            | 예시 호출                                                                                                                                                                                                                                                                                                                                                       |
|:------:|:----------------------------------------------|:--------------------------------|:-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| GET    | `/posts`                                      | 전체 게시글 조회                | `GET http://43.201.186.153:8080/posts`                                                                                                                                                                                                                                                                                                                         |
| GET    | `/posts/{postId}`                             | 특정 게시글 조회                | `GET http://43.201.186.153:8080/posts/27`                                                                                                                                                                                                                                                                                                                       |
| POST   | `/posts?nickname={닉네임}`                    | 게시글 작성                     | **URL**<br>`POST http://43.201.186.153:8080/posts?nickname=서자영`<br>**Headers**<br>`Content-Type: multipart/form-data`<br>**Body** (form-data)<br> - `postDto` (Text):<br>   ```json<br>   { "content": "고양이 귀여워", "hashtags": ["#cat"] }```<br> - `images` (File)<br> - `nickname` (Text) |
| PUT    | `/posts/{postId}?nickname={닉네임}`           | 게시글 수정                     | **URL**<br>`PUT http://43.201.186.153:8080/posts/13?nickname=서자영`<br>**Body** (raw JSON)<br>```json<br>{ "content": "이제배부름", "hashtags": ["#오늘존재의이유닭백숙"] }```                                                                                                                                                                                     |
| DELETE | `/posts/{postId}?nickname={닉네임}`           | 게시글 삭제                     | `DELETE http://43.201.186.153:8080/posts/15?nickname=서자영`                                                                                                                                                                                                                                                                                                     |
| GET    | `/posts/search?tag={인코딩된해시태그}`        | 해시태그 검색                   | `GET http://43.201.186.153:8080/posts/search?tag=%23JPA`  (`%23`= `#`)                                                                                                                                                                                                                                                                                          |
| GET    | `/posts/user?nickname={닉네임}`               | 작성자별 게시글 조회            | `GET http://43.201.186.153:8080/posts/user?nickname=성준수`                                                                                                                                                                                                                                                                                                     |
| POST   | `/posts/{postId}/comment?nickname={닉네임}&content={댓글}` | 댓글 작성             | `POST http://43.201.186.153:8080/posts/1/comment?nickname=서자영&content=테스트`                                                                                                                                                                                                                                                                                 |
| POST   | `/posts/{postId}/bookmark?nickname={닉네임}` | 북마크 토글 (등록/해제)         | `POST http://43.201.186.153:8080/posts/5/bookmark?nickname=서자영`                                                                                                                                                                                                                                                                                              |
| GET    | `/posts/{postId}/like?nickname={닉네임}`     | 좋아요 토글 (조회 후 토글)      | `GET http://43.201.186.153:8080/posts/5/like?nickname=서자영`                                                                                                                                                                                                                                                                                                    |
| GET    | `/posts/bookmarks?nickname={닉네임}`         | 사용자 북마크 목록 조회         | `GET http://43.201.186.153:8080/posts/bookmarks?nickname=서자영`                                                                                                                                                                                                                                                                                                 |
| POST   | `/predict` (ML 서버)                          | 이미지 업로드 → PDF 도안 생성   | **URL**<br>`POST http://43.201.186.153:8000/predict`<br>**Headers**<br>`Content-Type: multipart/form-data`<br>**Body** (form-data)<br>- `file` (File)                                                                                                                                                                                                            |
| GET    | `/pdfs/{filename}.pdf` (ML 서버)              | 생성된 PDF 다운로드 / 확인      | `GET http://43.201.186.153:8000/pdfs/082714d0768bd426aa4ed6fe7457ddf3f_grid.pdf`                                                                                                                                                                                                                                                                                 |

---

## DB 스키마 및 덤프

현재 MySQL 인스턴스는 이미 EC2(`43.201.186.153`)에 배포되어 있어 따로 실행할 필요가 없습니다.  
필요 시 아래 덤프 파일과 스키마 생성·Import 예시를 참고하세요.

1. **스키마 생성** (`schema.sql`)
   ```sql
   CREATE DATABASE IF NOT EXISTS knitting_girls;
   USE knitting_girls;
   -- 사용자·게시글·댓글·이미지·북마크 테이블 DDL…
   ```

2. **덤프 Import** (`data_dump.sql`)
   ```bash
   mysql -u <user> -p knitting_girls < data_dump.sql
   ```

---

## 라이선스

이 프로젝트는 [MIT License](LICENSE) 하에 배포됩니다.
