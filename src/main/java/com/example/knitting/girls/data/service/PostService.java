package com.example.knitting.girls.data.service;

import com.example.knitting.girls.data.dto.CommentDto;
import com.example.knitting.girls.data.dto.PostDetailDto;
import com.example.knitting.girls.data.dto.PostDto;
import com.example.knitting.girls.data.dto.UserDto;
import com.example.knitting.girls.data.entity.Bookmark;
import com.example.knitting.girls.data.entity.Comment;
import com.example.knitting.girls.data.entity.Post;
import com.example.knitting.girls.data.entity.User;
import com.example.knitting.girls.data.repository.BookmarkRepository;
import com.example.knitting.girls.data.repository.CommentRepository;
import com.example.knitting.girls.data.repository.PostRepository;
import com.example.knitting.girls.data.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import java.util.Base64;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookmarkRepository bookmarkRepository;

    public Post createPost(PostDto postDto, String nickname, MultipartFile image) {
        User author = userRepository.findByNickname(nickname);
        if (author == null) {
            throw new IllegalArgumentException("사용자가 존재하지 않습니다.");
        }

        String imageData = null;
        if (image != null && !image.isEmpty()) {
            try {
                byte[] imageBytes = image.getBytes();
                imageData = Base64.getEncoder().encodeToString(imageBytes); // Base64 사용
            } catch (IOException e) {
                throw new RuntimeException("파일 읽기 실패", e);
            }
        }

        Post post = Post.builder()
                .content(postDto.getContent())
                .hashtags(postDto.getHashtags())
                .createdAt(LocalDateTime.now())
                .imageData(imageData)
                .author(author)
                .build();
        return postRepository.save(post);
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

    private PostDetailDto convertToPostDetailDto(Post post) {
        List<CommentDto> commentDtos = post.getComments().stream()
                .map(comment -> new CommentDto(comment.getId(), comment.getContent(), comment.getCreatedAt(), new UserDto(comment.getAuthor())))
                .collect(Collectors.toList());

        List<UserDto> likedUsers = post.getLikes().stream()
                .map(UserDto::new)
                .collect(Collectors.toList());

        // 북마크한 사용자 리스트
        List<UserDto> bookmarkedUsers = bookmarkRepository.findByPost(post).stream()
                .map(bookmark -> new UserDto(bookmark.getUser()))
                .collect(Collectors.toList());

        return new PostDetailDto(post, commentDtos, likedUsers, bookmarkedUsers);
    }


    // 해시태그 검색
    public List<Post> searchByTag(String tag) {
        return postRepository.findByHashtagsContaining(tag);
    }
    // 유저가 쓴 게시글 조회
    public List<Post> getUserPosts(String nickname) {
        User author = userRepository.findByNickname(nickname);
        if (author == null) {
            throw new IllegalArgumentException("사용자가 존재하지 않습니다.");
        }
        return postRepository.findByAuthor(author);
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
    public void deletePost(Long postId, String nickname) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));
        if (!post.getAuthor().getNickname().equals(nickname)) {
            throw new IllegalArgumentException("자신의 글만 삭제할 수 있습니다.");
        }
        postRepository.delete(post);
    }

    // 좋아요 기능
    public void likePost(Long postId, String nickname) {
        User user = userRepository.findByNickname(nickname);
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));
        if (!post.getLikes().add(user)) {
            post.getLikes().remove(user); // 좋아요 취소
        }
        postRepository.save(post);
    }

    // 댓글
    public Comment addComment(Long postId, String nickname, String content) {
        User user = userRepository.findByNickname(nickname);
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));
        Comment comment = Comment.builder()
                .content(content)
                .author(user)
                .post(post)
                .createdAt(LocalDateTime.now())
                .build();
        return commentRepository.save(comment);
    }

    // 스크랩(북마크)
    public void bookmarkPost(Long postId, String nickname) {
        User user = userRepository.findByNickname(nickname);
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));
        Bookmark bookmark = Bookmark.builder().user(user).post(post).build();
        bookmarkRepository.save(bookmark);
    }

    // 북마크 조회
    public List<Post> getBookmarkedPosts(String nickname) {
        User user = userRepository.findByNickname(nickname);
        if (user == null) {
            throw new IllegalArgumentException("사용자가 존재하지 않습니다.");
        }

        List<Bookmark> bookmarks = bookmarkRepository.findByUser(user);
        return bookmarks.stream().map(Bookmark::getPost).collect(Collectors.toList());
    }
}

