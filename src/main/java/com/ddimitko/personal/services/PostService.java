package com.ddimitko.personal.services;

import com.ddimitko.personal.models.Comment;
import com.ddimitko.personal.models.Post;
import com.ddimitko.personal.models.User;
import com.ddimitko.personal.repositories.CommentRepository;
import com.ddimitko.personal.repositories.PostRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserService userService;
    private final CommentRepository commentRepository;

    public PostService(final PostRepository postRepository, final UserService userService, CommentRepository commentRepository) {
        this.postRepository = postRepository;
        this.userService = userService;
        this.commentRepository = commentRepository;
    }

    //Posts
    public List<Post> findAllPosts(){
        return postRepository.findAll();
    }

    public Post findByPostId(Long postId){
        return postRepository.findById(postId).orElseThrow();
    }

    public Post createPost(String content, String userTag) throws Exception {

        User user = userService.findUserByUserTag(userTag);
        Post post = new Post();
        post.setUser(user);
        post.setContent(content);
        //post.setSubmissionDate(LocalDateTime.now());
        return postRepository.save(post);
    }

    public void editPost(Long postId, String content) {
        Post post = postRepository.findById(postId).orElseThrow();
        post.setContent(content);
        postRepository.save(post);
    }

    public void deletePost(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow();
        postRepository.delete(post);
    }

    //Comments
    public Post commentPost(Long postId, String userTag, String content) throws Exception {
        User user = userService.findUserByUserTag(userTag);
        Post post = findByPostId(postId);

        Comment comment = new Comment();
        comment.setContent(content);
        comment.setUser(user);
        comment.setPost(post);
        //post.getComments().add(comment);

        commentRepository.save(comment);
        return post;
    }

    public List<Comment> findAllByPostId(Long postId){
        Post post = findByPostId(postId);
        return commentRepository.findAllByPost(post);
    }

    public Comment findByCommentId(Long commentId){
        return commentRepository.findById(commentId).orElseThrow();
    }

    public Post deleteComment(Long postId, Long commentId) {
        Post post = findByPostId(postId);
        Comment comment = findByCommentId(commentId);

        commentRepository.delete(comment);

        return post;
    }

    public void editComment(Long commentId, String content) {
        Comment comment = findByCommentId(commentId);
        comment.setContent(content);
        commentRepository.save(comment);
    }

}
