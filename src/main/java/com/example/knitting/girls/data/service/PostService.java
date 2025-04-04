package com.example.knitting.girls.data.service;

import com.example.knitting.girls.data.dto.CommentDto;
import com.example.knitting.girls.data.dto.PostDetailDto;
import com.example.knitting.girls.data.dto.PostDto;
import com.example.knitting.girls.data.dto.UserDto;
import com.example.knitting.girls.data.entity.*;
import com.example.knitting.girls.data.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import java.util.Base64;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookmarkRepository bookmarkRepository;
    private final PostImageRepository postImageRepository;
    private PostDetailDto convertToPostDetailDto(Post post) {
        List<CommentDto> commentDtos = post.getComments().stream()
                .map(comment -> new CommentDto(comment.getId(), comment.getContent(), comment.getCreatedAt(), new UserDto(comment.getAuthor())))
                .collect(Collectors.toList());

        List<UserDto> likedUsers = post.getLikes().stream()
                .map(UserDto::new)
                .collect(Collectors.toList());

        List<UserDto> bookmarkedUsers = bookmarkRepository.findByPost(post).stream()
                .map(bookmark -> new UserDto(bookmark.getUser()))
                .collect(Collectors.toList());

        return new PostDetailDto(post, commentDtos, likedUsers, bookmarkedUsers);
    }

    // 모든 게시글 조회
    public List<PostDetailDto> getAllPosts() {
        List<Post> posts = postRepository.findAll();
        return posts.stream().map(this::convertToPostDetailDto).collect(Collectors.toList());
    }

    // 특정 게시글 조회
    public PostDetailDto getPostById(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));
        return convertToPostDetailDto(post);
    }
    
    // 게시글 작성
    public Post createPost(PostDto postDto, String nickname, List<MultipartFile> images) {
        User author = userRepository.findByNickname(nickname);
        if (author == null) {
            throw new IllegalArgumentException("사용자가 존재하지 않습니다.");
        }

        Post post = Post.builder()
                .content(postDto.getContent())
                .hashtags(postDto.getHashtags())
                .createdAt(LocalDateTime.now())
                .author(author)
                .build();

        postRepository.save(post);  // 먼저 게시글 저장

        // 이미지 처리
        if (images != null && !images.isEmpty()) {
            for (MultipartFile image : images) {
                try {
                    byte[] imageBytes = image.getBytes();
                    String imageData = Base64.getEncoder().encodeToString(imageBytes);

                    PostImage postImage = PostImage.builder()
                            .post(post) // 게시글과 연결
                            .imageData(imageData)
                            .createdAt(LocalDateTime.now())
                            .build();
                    postImageRepository.save(postImage); // 이미지 저장
                } catch (IOException e) {
                    throw new RuntimeException("파일 읽기 실패", e);
                }
            }
        }

        return post;
    }



    // 게시글 수정
    public Post updatePost(Long postId, PostDto postDto, String nickname) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));
        if (!post.getAuthor().getNickname().equals(nickname)) {
            throw new IllegalArgumentException("자신의 글만 수정할 수 있습니다.");
        }
        post.setContent(postDto.getContent());
        post.setHashtags(postDto.getHashtags());
        return postRepository.save(post);
    }

    // 게시글 삭제
    public String deletePost(Long postId, String nickname) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));
        if (!post.getAuthor().getNickname().equals(nickname)) {
            throw new IllegalArgumentException("자신의 글만 삭제할 수 있습니다.");
        }
        postRepository.delete(post);
        return "게시글 삭제됨";
    }

    // 해시태그로 검색
    public List<PostDetailDto> searchByTag(String tag) {
        return postRepository.findByHashtagsContaining(tag).stream()
                .map(this::convertToPostDetailDto)
                .collect(Collectors.toList());
    }

    // 작성자로 검색
    public List<PostDetailDto> getUserPosts(String nickname) {
        User author = userRepository.findByNickname(nickname);
        if (author == null) {
            throw new IllegalArgumentException("사용자가 존재하지 않습니다.");
        }
        return postRepository.findByAuthor(author).stream()
                .map(this::convertToPostDetailDto)
                .collect(Collectors.toList());
    }

    // 댓글
    public PostDetailDto addComment(Long postId, String nickname, String content) {
        User user = userRepository.findByNickname(nickname);
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));
        Comment comment = Comment.builder()
                .content(content)
                .author(user)
                .post(post)
                .createdAt(LocalDateTime.now())
                .build();
        commentRepository.save(comment);
        return convertToPostDetailDto(post);
    }

    // 좋아요
    public String likePost(Long postId, String nickname) {
        User user = userRepository.findByNickname(nickname);
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));
        if (!post.getLikes().add(user)) {
            post.getLikes().remove(user);
            postRepository.save(post);
            return "좋아요 삭제됨";
        }
        postRepository.save(post);
        return "좋아요 추가됨";
    }

    // 북마크
    public String bookmarkPost(Long postId, String nickname) {
        User user = userRepository.findByNickname(nickname);
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));
        Optional<Bookmark> existingBookmark = bookmarkRepository.findByUser(user).stream()
                .filter(b -> b.getPost().equals(post))
                .findFirst();
        if (existingBookmark.isPresent()) {
            bookmarkRepository.delete(existingBookmark.get());
            return "북마크 삭제됨";
        }
        Bookmark bookmark = Bookmark.builder().user(user).post(post).build();
        bookmarkRepository.save(bookmark);
        return "북마크 추가됨";
    }

    // 북마크 목록 조회
    public List<PostDetailDto> getBookmarkedPosts(String nickname) {
        User user = userRepository.findByNickname(nickname);
        if (user == null) {
            throw new IllegalArgumentException("사용자가 존재하지 않습니다.");
        }
        return bookmarkRepository.findByUser(user).stream()
                .map(Bookmark::getPost)
                .map(this::convertToPostDetailDto)
                .collect(Collectors.toList());
    }
}

