package com.nvfredy.springangularappbackend.dto;

import lombok.Data;

@Data
public class CommentDTO {

    private Long id;
//    @NotEmpty
    private String message;
//    @NotEmpty
    private String username;

}
