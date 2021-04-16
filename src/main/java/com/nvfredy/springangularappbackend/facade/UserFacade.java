package com.nvfredy.springangularappbackend.facade;

import com.nvfredy.springangularappbackend.dto.UserDTO;
import com.nvfredy.springangularappbackend.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserFacade {

    public UserDTO userToUserDTO(User user) {
        UserDTO userDTO = new UserDTO();

        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());
        userDTO.setBio(user.getBio());

        return userDTO;
    }

}
