package com.ddimitko.personal.repositories;

import com.ddimitko.personal.models.Comment;
import com.ddimitko.personal.models.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByPost(Post post);
    //List<Comment> findAllByUserTag(String userTag);

}
