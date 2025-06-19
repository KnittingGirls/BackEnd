# 🛠️ Knitting Girls Backend

Knitting Girls 프로젝트 중 **백엔드(모델 서버 API & CRUD API 및 서버)** 모듈의 사용법과 구조를 정리한 README입니다.  

> **Backend 서버**: `http://43.201.186.153:8080`  
> **ML 서버**: `http://43.201.186.153:8000`
---

## 목차

1. [리포지토리 개요](#리포지토리-개요)  
2. [주요 기능](#주요-기능)  
3. [요구사항](#요구사항)  
4. [설치 및 빌드](#설치-및-빌드)
5. [ML server 코드 개요](#ml-server-코드-개요) 
6. [BE Java 클래스 개요](#be-java-클래스-개요)  
7. [API 사용 예시](#api-사용-예시-postman--curl)  
8. [DB 스키마 및 덤프](#db-스키마-및-덤프)
   
---

## 리포지토리 개요

- **기능**
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
2. **게시글(Post) 관리**  
3. **댓글(Comment) 관리**  
4. **북마크(Bookmark) & 좋아요(Like) 토글**  
5. **해시태그 검색 & 작성자별 게시글 조회**  
6. **모델 서버 연동 → PDF 도안 생성**

---

## 요구사항

- **Java** ≥ 17
- **Spring Boot** 2.5.x 이상  
- **Maven** 또는 **Gradle**  
- **MySQL** 8.0 이상 (이미 서버에 배포되어 있으므로 별도 실행 불필요)
- **Python** 3.8 이상 (이미 서버에 배포되어 있으므로 별도 실행 불필요)

---

## 설치 및 빌드

```bash
1) 리포지토리 클론
git clone https://github.com/KnittingGirls/BackEnd.git

2) 환경 설정
src/main/resources/application.properties 복사 및
- MySQL 접속 정보(JDBC URL, 사용자/비밀번호)
- JWT 시크릿·토큰 만료 시간
- CORS 허용 도메인 등 수정

best_model.pth, pascal.pth, lip.pth, atr.pth는 ml_server 하위에 이동

# 3) 로컬 실행 방법 (테스트용)
**주의: EC2 서버(43.201.186.153)에 이미 배포되어 있으므로, 로컬 실행 없이 API 호출만으로 충분합니다.**

1. 백엔드 서버 실행
- IDE(IntelliJ 등)에서 Application.java 파일 좌측의 ▶️ 아이콘 클릭
- 또는 gradle bootjar 후, 터미널에서 java -jar build/libs/knitting-girls-0.0.1-SNAPSHOT.jar

2. 모델 서버 실행 (터미널)
cd ml_server
source venv/bin/activate
uvicorn final_model_server:app --host 0.0.0.0 --port 8000 --reload
```

---
## ML server 코드 개요
ml_server/ 디렉터리에 포함된 주요 스크립트 및 서버 코드

### final_model_server.py
- FastAPI 기반 엔드포인트(/predict, /pdfs/{filename})
- DeepLabV3+ 모델 예측 → 마스크 생성
- SCHP(extractor) 호출 → 파트별 아웃라인
- fin_fin.py 실행 → PDF 도안 생성
- 처리 시간 로깅 반환

### model_server.py
- 구버전 FastAPI 서버
- patch_single_pdf.py를 이용해 segmentation 결과를 텍스트 그리드 형태 PDF로 변환

### simple_extractor.py
- SCHP(Self-Correction Human Parsing) 모델 호출 CLI
- --dataset, --model-restore 등 옵션으로 다양한 데이터셋 지원
- 입력 폴더 내 모든 이미지에 대해 파트별 마스크(.png) 생성

### fin_fin.py
- DeepLab 결과 이미지 + SCHP 결과 이미지 → dominant symbol 추출
- 심볼 패턴(symbol_patterns)으로 변환 → matplotlib 그리드에 렌더링
- 결과 front & sleeve 페이지를 하나의 PDF로 저장

### patch_single_pdf.py
- RGB 라벨 이미지를 3×3 문자 기호 패턴(symbol_patterns)으로 변환
- matplotlib → PDF 출력 (model_server.py와 연동 사용)

### make_grid.py
- 딥랩 & SCHP 결과 이미지를 세로로 합쳐 단순 PDF 저장
- 간단한 비교용 초기 테스트 스크립트

---
## BE Java 클래스 개요

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

> **Backend 서버**: `http://43.201.186.153:8080`  
> **ML 서버**: `http://43.201.186.153:8000`

| Method | Path                                          | 설명                            | 예시 호출                                                                                                                                                                                                                                                                                                                                                       |
|:------:|:----------------------------------------------|:--------------------------------|:-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| GET    | `/posts`                                      | 전체 게시글 조회                | `GET http://43.201.186.153:8080/posts`                                                                                                                                                                                                                                                                                                                         |
| GET    | `/posts/{postId}`                             | 특정 게시글 조회                | `GET http://43.201.186.153:8080/posts/27`                                                                                                                                                                                                                                                                                                                       |
| POST   | `/posts?nickname={닉네임}`                    | 게시글 작성                     | **URL**<br>`POST http://43.201.186.153:8080/posts?nickname=이화연`<br>**Headers**<br>`Content-Type: multipart/form-data`<br>**Body** (form-data)<br> - `postDto` (Text):<br>   ```json<br>   { "content": "게시글1", "hashtags": ["#tag"] }```<br> - `images` (File)<br> - `nickname` (Text) |
| PUT    | `/posts/{postId}?nickname={닉네임}`           | 게시글 수정                     | **URL**<br>`PUT http://43.201.186.153:8080/posts/13?nickname=이화연`<br>**Body** (raw JSON)<br>```json<br>{ "content": "게시글2", "hashtags": ["#tag2"] }```                                                                                                                                                                                     |
| DELETE | `/posts/{postId}?nickname={닉네임}`           | 게시글 삭제                     | `DELETE http://43.201.186.153:8080/posts/15?nickname=이화연`                                                                                                                                                                                                                                                                                                     |
| GET    | `/posts/search?tag={인코딩된해시태그}`        | 해시태그 검색                   | `GET http://43.201.186.153:8080/posts/search?tag=%23JPA`  (`%23`= `#`)                                                                                                                                                                                                                                                                                          |
| GET    | `/posts/user?nickname={닉네임}`               | 작성자별 게시글 조회            | `GET http://43.201.186.153:8080/posts/user?nickname=이화연`                                                                                                                                                                                                                                                                                                     |
| POST   | `/posts/{postId}/comment?nickname={닉네임}&content={댓글}` | 댓글 작성             | `POST http://43.201.186.153:8080/posts/1/comment?nickname=이화연&content=테스트`                                                                                                                                                                                                                                                                                 |
| POST   | `/posts/{postId}/bookmark?nickname={닉네임}` | 북마크 토글 (등록/해제)         | `POST http://43.201.186.153:8080/posts/5/bookmark?nickname=이화연`                                                                                                                                                                                                                                                                                              |
| GET    | `/posts/{postId}/like?nickname={닉네임}`     | 좋아요 토글 (조회 후 토글)      | `GET http://43.201.186.153:8080/posts/5/like?nickname=이화연`                                                                                                                                                                                                                                                                                                    |
| GET    | `/posts/bookmarks?nickname={닉네임}`         | 사용자 북마크 목록 조회         | `GET http://43.201.186.153:8080/posts/bookmarks?nickname=이화연`                                                                                                                                                                                                                                                                                                 |
| POST   | `/predict` (ML 서버)                          | 이미지 업로드 → PDF 도안 생성   | **URL**<br>`POST http://43.201.186.153:8000/predict`<br>**Headers**<br>`Content-Type: multipart/form-data`<br>**Body** (form-data)<br>- `file` (File)                                                                                                                                                                                                            |
| GET    | `/pdfs/{filename}.pdf` (ML 서버)              | 생성된 PDF 다운로드 / 확인      | `GET http://43.201.186.153:8000/pdfs/'PDF파일명.pdf'`                                                                                                                                                                                                                                                                                 |

---

## DB 스키마 및 덤프

현재 MySQL 인스턴스는 이미 EC2(`43.201.186.153`)에 배포되어 있어 따로 실행할 필요가 없습니다.  
필요 시, 아래 **`backend/db/`** 디렉터리에 포함된 dump 파일들을 사용하세요:

- `knitting_girls_users.sql`  
  > 사용자 정보(`users` 테이블) 덤프  
- `knitting_girls_posts.sql`  
  > 게시글 메타데이터(`posts` 테이블) 덤프  
- `knitting_girls_post_hashtags.sql`  
  > 게시글–해시태그 매핑(`post_hashtags` 테이블) 덤프  
- `knitting_girls_post_image.sql`  
  > 게시글별 이미지 연결(`post_image` 테이블) 덤프  
- `knitting_girls_post_likes.sql`  
  > 좋아요 기록(`post_likes` 테이블) 덤프  
- `knitting_girls_comments.sql`  
  > 댓글 정보(`comments` 테이블) 덤프  
- `knitting_girls_images.sql`  
  > 업로드된 이미지 메타데이터(`images` 테이블) 덤프  
- `knitting_girls_bookmarks.sql`  
  > 북마크 기록(`bookmarks` 테이블) 덤프  

1. **스키마 생성** (`schema.sql`)  
   ```sql
   CREATE DATABASE IF NOT EXISTS knitting_girls;
   USE knitting_girls;
   -- users, posts, post_hashtags, post_image, post_likes, comments, images, bookmarks 테이블 DDL…

2. **덤프 Import**
      ```sql
      mysql -u <user> -p knitting_girls < backend/db/knitting_girls_users.sql
      mysql -u <user> -p knitting_girls < backend/db/knitting_girls_posts.sql
      mysql -u <user> -p knitting_girls < backend/db/knitting_girls_post_hashtags.sql
      mysql -u <user> -p knitting_girls < backend/db/knitting_girls_post_image.sql
      mysql -u <user> -p knitting_girls < backend/db/knitting_girls_post_likes.sql
      mysql -u <user> -p knitting_girls < backend/db/knitting_girls_comments.sql
      mysql -u <user> -p knitting_girls < backend/db/knitting_girls_images.sql
      mysql -u <user> -p knitting_girls < backend/db/knitting_girls_bookmarks.sql
    
