package com.ddimitko.personal.controllers;

import com.ddimitko.personal.DTOs.PostDto;
import com.ddimitko.personal.models.Post;
import com.ddimitko.personal.services.PostService;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class PostController {

    private final PostService postService;
    private final ModelMapper modelMapper;

    public PostController(final PostService postService, final ModelMapper modelMapper) {
        this.postService = postService;
        this.modelMapper = modelMapper;
    }

    private PostDto convertToDto(Post post) {

        modelMapper.typeMap(Post.class, PostDto.class)
                .addMappings(mapper -> mapper.map(src -> src.getUser().getUserTag(), PostDto::setUserTag));
        PostDto postDto = modelMapper.map(post, PostDto.class);
        //postDto.setSubmissionDate(LocalDateTime.from(post.getSubmissionDate()).format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)));
        return postDto;
    }

    private Post convertToEntity(PostDto postDto) {
        return modelMapper.map(postDto, Post.class);
    }

    //Posts
    @GetMapping("/")
    public ResponseEntity<List<PostDto>> allPosts() {
        if(postService.findAllPosts().isEmpty()){
            return ResponseEntity.noContent().build();
        }
        else{
            List<Post> postList = postService.findAllPosts();
            return ResponseEntity.ok(postList.stream().map(this::convertToDto)
                    .collect(Collectors.toList()));
        }
    }

    @PostMapping("/post")
    public ResponseEntity<PostDto> createPost(@RequestParam String content, @RequestParam String userTag) throws Exception {
        Post post = postService.createPost(content, userTag);
        PostDto postDto = modelMapper.map(post, PostDto.class);
        return ResponseEntity.ok(postDto);
    }

    @PutMapping("/{postId}/editPost")
    public ResponseEntity editPost(@PathVariable Long postId, @RequestParam String content) {
        postService.editPost(postId, content);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{postId}/delete")
    public ResponseEntity deletePost(@PathVariable Long postId) {
        postService.deletePost(postId);
        return ResponseEntity.ok().build();
    }


    //Comments
    @PutMapping("/{postId}/comment")
    public ResponseEntity<PostDto> putComment(@PathVariable Long postId, @RequestParam String comment, @RequestParam String userTag) throws Exception {
        Post post = postService.commentPost(postId, userTag, comment);
        PostDto postDto = modelMapper.map(post, PostDto.class);

        return ResponseEntity.ok(postDto);
    }

    @PutMapping("/{commentId}/editComment")
    public ResponseEntity editComment(@PathVariable Long commentId, @RequestParam String content) {
        postService.editComment(commentId, content);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{postId}/{commentId}/delete")
    public ResponseEntity<PostDto> deleteComment(@PathVariable Long postId, @PathVariable Long commentId) {
        Post post = postService.deleteComment(postId, commentId);
        PostDto postDto = modelMapper.map(post, PostDto.class);
        return ResponseEntity.ok(postDto);
    }

}
