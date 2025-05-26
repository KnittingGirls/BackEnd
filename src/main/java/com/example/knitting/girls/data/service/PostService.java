package com.example.knitting.girls.data.service;

import com.example.knitting.girls.data.dto.CommentDto;
import com.example.knitting.girls.data.dto.PostDetailDto;
import com.example.knitting.girls.data.dto.PostDto;
import com.example.knitting.girls.data.dto.UserDto;
import com.example.knitting.girls.data.entity.Bookmark;
import com.example.knitting.girls.data.entity.Comment;
import com.example.knitting.girls.data.entity.Post;
import com.example.knitting.girls.data.entity.PostImage;
import com.example.knitting.girls.data.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
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
        // 이미지 엔티티 DB에서 직접 가져와서 post에 박아넣기
        List<PostImage> images = postImageRepository.findByPost(post);
        post.setImages(images);

        // 댓글 null-safe
        List<CommentDto> commentDtos = null;
        if (post.getComments() != null) {
            commentDtos = post.getComments().stream()
                    .map(c -> new CommentDto(
                            c.getId(),
                            c.getContent(),
                            c.getCreatedAt(),
                            new UserDto(c.getAuthor())
                    ))
                    .collect(Collectors.toList());
        }

        // 좋아요 null-safe
        List<UserDto> likedUsers = null;
        if (post.getLikes() != null) {
            likedUsers = post.getLikes().stream()
                    .map(UserDto::new)
                    .collect(Collectors.toList());
        }

        List<UserDto> bookmarkedUsers = bookmarkRepository.findByPost(post).stream()
                .map(b -> new UserDto(b.getUser()))
                .collect(Collectors.toList());

        return new PostDetailDto(post, commentDtos, likedUsers, bookmarkedUsers);
    }

    // 모든 게시글 조회
    public List<PostDetailDto> getAllPosts() {
        return postRepository.findAll().stream()
                .map(this::convertToPostDetailDto)
                .collect(Collectors.toList());
    }

    // 특정 게시글 조회
    public PostDetailDto getPostById(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));
        return convertToPostDetailDto(post);
    }

    // 게시글 작성
    public Post createPost(PostDto postDto, String nickname, List<MultipartFile> images) {
        var author = userRepository.findByNickname(nickname);
        if (author == null) throw new IllegalArgumentException("사용자가 존재하지 않습니다.");

        // Post 엔티티 생성+저장
        Post post = Post.builder()
                .content(postDto.getContent())
                .hashtags(postDto.getHashtags())
                .createdAt(LocalDateTime.now())
                .author(author)
                .build();
        postRepository.save(post);

        // 이미지 파일+엔티티 저장
        if (images != null && !images.isEmpty()) {
            images.forEach(file -> {
                try {
                    String ext = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf('.'));
                    String filename = UUID.randomUUID() + ext;
                    String dir = System.getProperty("user.dir") + "/uploads/";
                    File uploadDir = new File(dir);
                    if (!uploadDir.exists()) uploadDir.mkdirs();
                    File dest = new File(dir + filename);
                    file.transferTo(dest);
                    postImageRepository.save(PostImage.builder()
                            .post(post)
                            .imagePath(filename)
                            .createdAt(LocalDateTime.now())
                            .build());
                } catch (IOException e) {
                    throw new RuntimeException("이미지 저장 실패", e);
                }
            });
        }

        return post;
    }

    // 게시글 수정
    public Post updatePost(Long postId, PostDto postDto, String nickname) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));
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
        return "게시글이 삭제되었습니다.";
    }

    // 해시태그 검색
    public List<PostDetailDto> searchByTag(String tag) {
        return postRepository.findByHashtagsContaining(tag).stream()
                .map(this::convertToPostDetailDto)
                .collect(Collectors.toList());
    }

    // 작성자별 게시글 조회
    public List<PostDetailDto> getUserPosts(String nickname) {
        var author = userRepository.findByNickname(nickname);
        if (author == null) throw new IllegalArgumentException("사용자가 존재하지 않습니다.");
        return postRepository.findByAuthor(author).stream()
                .map(this::convertToPostDetailDto)
                .collect(Collectors.toList());
    }

    // 댓글 작성
    public PostDetailDto addComment(Long postId, String nickname, String content) {
        var user = userRepository.findByNickname(nickname);
        var post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));
        commentRepository.save(Comment.builder()
                .content(content)
                .author(user)
                .post(post)
                .createdAt(LocalDateTime.now())
                .build());
        return convertToPostDetailDto(post);
    }

    // 좋아요 토글
    public String likePost(Long postId, String nickname) {
        var user = userRepository.findByNickname(nickname);
        var post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));
        if (!post.getLikes().add(user)) {
            post.getLikes().remove(user);
            postRepository.save(post);
            return "좋아요가 취소되었습니다.";
        }
        postRepository.save(post);
        return "좋아요가 등록되었습니다.";
    }

    // 북마크 토글
    public String bookmarkPost(Long postId, String nickname) {
        var user = userRepository.findByNickname(nickname);
        var post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));
        var existing = bookmarkRepository.findByUser(user).stream()
                .filter(b -> b.getPost().equals(post))
                .findFirst();
        if (existing.isPresent()) {
            bookmarkRepository.delete(existing.get());
            return "북마크가 해제되었습니다.";
        }
        bookmarkRepository.save(Bookmark.builder().user(user).post(post).build());
        return "북마크가 등록되었습니다.";
    }

    // 북마크 목록 조회
    public List<PostDetailDto> getBookmarkedPosts(String nickname) {
        var user = userRepository.findByNickname(nickname);
        if (user == null) throw new IllegalArgumentException("사용자가 존재하지 않습니다.");
        return bookmarkRepository.findByUser(user).stream()
                .map(Bookmark::getPost)
                .map(this::convertToPostDetailDto)
                .collect(Collectors.toList());
    }
}
