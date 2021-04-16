package com.nvfredy.springangularappbackend.facade;

import com.nvfredy.springangularappbackend.dto.PostDTO;
import com.nvfredy.springangularappbackend.entity.Post;
import org.springframework.stereotype.Component;

@Component
public class PostFacade {

    public PostDTO postToPostDTO(Post post) {
        PostDTO postDTO = new PostDTO();

        postDTO.setId(post.getId());
        postDTO.setTitle(post.getTitle());
        postDTO.setCaption(post.getCaption());
        postDTO.setUsername(post.getUser().getUsername());
        postDTO.setLocation(post.getLocation());
        postDTO.setLikes(post.getLikes());
        postDTO.setUsersLiked(post.getLikedUsers());

        return postDTO;
    }

}
