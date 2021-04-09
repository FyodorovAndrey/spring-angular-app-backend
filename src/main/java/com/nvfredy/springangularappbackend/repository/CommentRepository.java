package com.nvfredy.springangularappbackend.repository;

import com.nvfredy.springangularappbackend.entity.Comment;
import com.nvfredy.springangularappbackend.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByPost(Post post);

    Optional<Comment> findByIdAndUserId(Long commentId, Long userId);

}
