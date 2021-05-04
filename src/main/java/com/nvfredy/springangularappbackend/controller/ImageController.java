package com.nvfredy.springangularappbackend.controller;

import com.nvfredy.springangularappbackend.entity.Image;
import com.nvfredy.springangularappbackend.payload.response.MessageResponse;
import com.nvfredy.springangularappbackend.service.ImageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;

@RestController
@CrossOrigin
@RequestMapping("api/image")
public class ImageController {

    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @PostMapping("/upload")
    public ResponseEntity<MessageResponse> uploadProfileImage(@RequestParam("file") MultipartFile file,
                                                              Principal principal) throws IOException {
        imageService.uploadUserProfileImage(file, principal);

        return new ResponseEntity<>(new MessageResponse("Image was upload to profile"), HttpStatus.OK);
    }

    @PostMapping("/{postId}/upload")
    public ResponseEntity<MessageResponse> uploadPostImage(@RequestParam("file") MultipartFile file,
                                                           @PathVariable("postId") String postId,
                                                           Principal principal) throws IOException {
        imageService.uploadPostImage(file, principal, Long.parseLong(postId));

        return new ResponseEntity<>(new MessageResponse("Image was upload to post"), HttpStatus.OK);
    }

    @GetMapping("/profileImage")
    public ResponseEntity<Image> getProfileImage(Principal principal) {
        Image image = imageService.getImageUserProfile(principal);

        return new ResponseEntity<>(image, HttpStatus.OK);
    }

    @GetMapping("/{postId}/image")
    public ResponseEntity<Image> getPostImage(@PathVariable("postId") String postId) {
        Image image = imageService.getImagePost(Long.parseLong(postId));

        return new ResponseEntity<>(image, HttpStatus.OK);
    }
}
