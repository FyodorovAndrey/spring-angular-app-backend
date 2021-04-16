package com.nvfredy.springangularappbackend.controller;

import com.nvfredy.springangularappbackend.dto.CommentDTO;
import com.nvfredy.springangularappbackend.entity.Comment;
import com.nvfredy.springangularappbackend.facade.CommentFacade;
import com.nvfredy.springangularappbackend.payload.response.MessageResponse;
import com.nvfredy.springangularappbackend.service.CommentService;
import com.nvfredy.springangularappbackend.validators.ResponseErrorValidation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
@RequestMapping("api/comment")
public class CommentController {

    private final CommentService commentService;
    private final CommentFacade commentFacade;
    private final ResponseErrorValidation errorValidation;

    public CommentController(CommentService commentService, CommentFacade commentFacade, ResponseErrorValidation errorValidation) {
        this.commentService = commentService;
        this.commentFacade = commentFacade;
        this.errorValidation = errorValidation;
    }

    @PostMapping("/{postId}/create")
    public ResponseEntity<Object> createComment(@Valid @RequestBody CommentDTO commentDTO,
                                                @PathVariable("postId") String postId,
                                                Principal principal,
                                                BindingResult bindingResult) {

        ResponseEntity<Object> errors = errorValidation.mapValidationService(bindingResult);
        if (!ObjectUtils.isEmpty(errors)) return errors;

        Comment comment = commentService.saveComment(commentDTO, Long.parseLong(postId), principal);
        CommentDTO createdComment = commentFacade.commentToCommentDTO(comment);

        return new ResponseEntity<>(createdComment, HttpStatus.OK);
    }

    @GetMapping("{postId}/all")
    public ResponseEntity<List<CommentDTO>> getAllCommentsToPost(@PathVariable("postId") String postId) {
        List<CommentDTO> commentDTOList = commentService.findAllCommentsForPost(Long.parseLong(postId))
                .stream()
                .map(commentFacade::commentToCommentDTO)
                .collect(Collectors.toList());

        return new ResponseEntity<>(commentDTOList, HttpStatus.OK);
    }

    @DeleteMapping("/{commentId}/delete")
    public ResponseEntity<MessageResponse> deleteComment(@PathVariable("commentId") String commentId) {
        commentService.deleteCommentById(Long.parseLong(commentId));

        return new ResponseEntity<>(new MessageResponse("Comment was deleted"), HttpStatus.OK);
    }
}
