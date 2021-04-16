package com.nvfredy.springangularappbackend.service;

import com.nvfredy.springangularappbackend.dto.CommentDTO;
import com.nvfredy.springangularappbackend.entity.Comment;
import com.nvfredy.springangularappbackend.entity.Post;
import com.nvfredy.springangularappbackend.entity.User;
import com.nvfredy.springangularappbackend.exceptions.PostNotFoundException;
import com.nvfredy.springangularappbackend.repository.CommentRepository;
import com.nvfredy.springangularappbackend.repository.PostRepository;
import com.nvfredy.springangularappbackend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Service
public class CommentService {
    public static final Logger LOGGER = LoggerFactory.getLogger(CommentService.class);

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public CommentService(CommentRepository commentRepository, PostRepository postRepository, UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    public Comment saveComment(CommentDTO commentDTO, Long postId, Principal principal) {
        User user = findUserByPrincipal(principal);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(
                        "Post cannot be found for username: " + user.getEmail()));

        Comment comment = new Comment();
        comment.setUserId(user.getId());
        comment.setUsername(user.getUsername());
        comment.setMessage(commentDTO.getMessage());
        comment.setPost(post);

        LOGGER.info("Saving post for User: {}", user.getEmail());

        return commentRepository.save(comment);
    }

    public List<Comment> findAllCommentsForPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post cannot be found"));

        return commentRepository.findAllByPost(post);
    }

    public void deleteCommentById(Long commentId) {
        Optional<Comment> comment = commentRepository.findById(commentId);
        comment.ifPresent(commentRepository::delete);
    }


    private User findUserByPrincipal(Principal principal) {
        String username = principal.getName();
        return userRepository.findUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User with username: " + username + " not found."));
    }
}
