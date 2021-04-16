package com.nvfredy.springangularappbackend.service;

import com.nvfredy.springangularappbackend.dto.PostDTO;
import com.nvfredy.springangularappbackend.entity.Image;
import com.nvfredy.springangularappbackend.entity.Post;
import com.nvfredy.springangularappbackend.entity.User;
import com.nvfredy.springangularappbackend.exceptions.PostNotFoundException;
import com.nvfredy.springangularappbackend.repository.ImageRepository;
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
public class PostService {
    public static final Logger LOGGER = LoggerFactory.getLogger(PostService.class);

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final ImageRepository imageRepository;

    public PostService(PostRepository postRepository, UserRepository userRepository, ImageRepository imageRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.imageRepository = imageRepository;
    }

    public Post createPost(PostDTO postDTO, Principal principal) {
        User user = findUserByPrincipal(principal);
        Post post = new Post();

        post.setUser(user);
        post.setTitle(postDTO.getTitle());
        post.setCaption(postDTO.getCaption());
        post.setLocation(postDTO.getLocation());
        post.setLikes(0);

        LOGGER.info("Saving post for User: {}", user.getEmail());
        return postRepository.save(post);
    }

    public List<Post> getAllPosts() {
        return postRepository.findAllByOrderByCreatedDateDesc();
    }

    public Post findPostById(Long id, Principal principal) {
        User user = findUserByPrincipal(principal);
        return postRepository.findPostByIdAndUser(id, user)
                .orElseThrow(() -> new PostNotFoundException(
                        "Post cannot be found for username: " + user.getEmail()));
    }

    public List<Post> getAllPostsForUser(Principal principal) {
        User user = findUserByPrincipal(principal);
        return postRepository.findAllByUserOrderByCreatedDateDesc(user);
    }

    public Post likePost(Long postId, String username) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(
                        "Post cannot be found"));

        Optional<String> likedUser = post.getLikedUsers()
                .stream()
                .filter(u -> u.equals(username))
                .findAny();

        if (likedUser.isPresent()) {
            post.setLikes(post.getLikes() - 1);
            post.getLikedUsers().remove(username);
        } else {
            post.setLikes(post.getLikes() + 1);
            post.getLikedUsers().add(username);
        }

        return postRepository.save(post);
    }

    public void deletePost(Long postId, Principal principal) {
        Post post = findPostById(postId, principal);
        Optional<Image> postImage = imageRepository.findByPostId(post.getId());
        postRepository.delete(post);
        postImage.ifPresent(imageRepository::delete);
    }

    private User findUserByPrincipal(Principal principal) {
        String username = principal.getName();
        return userRepository.findUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User with username: " + username + " not found."));
    }
}
