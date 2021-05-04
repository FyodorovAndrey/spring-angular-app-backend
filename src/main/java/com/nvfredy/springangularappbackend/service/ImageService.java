package com.nvfredy.springangularappbackend.service;

import com.nvfredy.springangularappbackend.entity.Image;
import com.nvfredy.springangularappbackend.entity.Post;
import com.nvfredy.springangularappbackend.entity.User;
import com.nvfredy.springangularappbackend.exceptions.ImageNotFoundException;
import com.nvfredy.springangularappbackend.repository.ImageRepository;
import com.nvfredy.springangularappbackend.repository.PostRepository;
import com.nvfredy.springangularappbackend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Principal;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

@Service
public class ImageService {

    public static final Logger LOGGER = LoggerFactory.getLogger(ImageService.class);


    private final ImageRepository imageRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public ImageService(ImageRepository imageRepository, UserRepository userRepository, PostRepository postRepository) {
        this.imageRepository = imageRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }

    public Image uploadUserProfileImage(MultipartFile file, Principal principal) {
        User user = findUserByPrincipal(principal);
        LOGGER.info("Upload image for user: {}", user.getUsername());

        Image profileImage = imageRepository.findByUserId(user.getId()).orElse(null);
        if (!ObjectUtils.isEmpty(profileImage)) {
            imageRepository.delete(profileImage);
        }

        Image image = null;
        try {
            image = new Image();
            image.setUserId(user.getId());
            image.setBytes(compressBytes(file.getBytes()));
            image.setName(file.getOriginalFilename());
        } catch (IOException e) {
            LOGGER.error("Error in uploadUserProfileImage method");
        }

        return imageRepository.save(image);
    }

    public Image uploadPostImage(MultipartFile file, Principal principal, Long postId) throws IOException {
        User user = findUserByPrincipal(principal);
        Post post = user.getPosts()
                .stream()
                .filter(p -> p.getId().equals(postId))
                .collect(toSinglePostCollector());

        Image image = new Image();
//        image.setUserId(user.getId());
        image.setBytes(compressBytes(file.getBytes()));
        image.setName(file.getOriginalFilename());
        image.setPostId(post.getId());
        LOGGER.info("Uploading image for post: {}", post.getId());

        return imageRepository.save(image);
    }

    public Image getImageUserProfile(Principal principal) {
        User user = findUserByPrincipal(principal);
        Image image = imageRepository.findByUserId(user.getId()).orElse(null);

        if (!ObjectUtils.isEmpty(image)) {
            image.setBytes(decompressBytes(image.getBytes()));
        }

        return image;
    }

    public Image getImagePost(Long postId) {
        Image image = imageRepository.findByPostId(postId)
                .orElseThrow(() -> new ImageNotFoundException("Image not found for post: " + postId));
        if (!ObjectUtils.isEmpty(image)) {
            image.setBytes(decompressBytes(image.getBytes()));
        }
        return image;
    }

    private byte[] compressBytes(byte[] data) {
        Deflater deflater = new Deflater();
        deflater.setInput(data);
        deflater.finish();

        ByteArrayOutputStream out = new ByteArrayOutputStream(data.length);
        byte[] buffer = new byte[1024];

        try {
            while (!deflater.finished()) {
                int count = deflater.deflate(buffer);
                out.write(buffer, 0, count);
            }
            out.close();
        } catch (IOException e) {
            LOGGER.error("Compress error");
        }
        System.out.println("Compressed file size: " + out.toByteArray().length);
        return out.toByteArray();
    }

    private byte[] decompressBytes(byte[] data) {
        Inflater inflater = new Inflater();
        inflater.setInput(data);
        ByteArrayOutputStream out = new ByteArrayOutputStream(data.length);
        byte[] buffer = new byte[1024];

        try {
            while (!inflater.finished()) {
                int count = inflater.inflate(buffer);
                out.write(buffer, 0, count);
            }
            out.close();
        } catch (DataFormatException | IOException e) {
            LOGGER.error("Decompress error");
        }
        return out.toByteArray();
    }

    private User findUserByPrincipal(Principal principal) {
        String username = principal.getName();
        return userRepository.findUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User with username: " + username + " not found."));
    }

    private <T> Collector<T, ?, T> toSinglePostCollector() {
        return Collectors.collectingAndThen(
                Collectors.toList(),
                list -> {
                    if (list.size() != 1) {
                        throw new IllegalStateException();
                    }
                    return list.get(0);
                }
        );
    }
}
