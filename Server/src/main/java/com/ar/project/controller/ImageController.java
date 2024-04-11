package com.ar.project.controller;

import com.ar.project.dto.ImageRequestDTO;
import com.ar.project.model.Image;
import com.ar.project.model.User;
import com.ar.project.service.ImageService;
import com.ar.project.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/images")
public class ImageController {

    @Autowired
    private UserService userService;

    @Autowired
    private ImageService imageService;

    @GetMapping("/user/{id}")
    public ResponseEntity<List<Image>> getAllUserImages(@PathVariable Long id) {
        List<Image> images = imageService.getAllUserImages(id);
        return ResponseEntity.ok(images);
    }

    @GetMapping("/image/{id}")
    public ResponseEntity<Image> getImageById(@PathVariable Long id) {
        return ResponseEntity.ok(imageService.getImageById(id));
    }

    @GetMapping("/imagePath/{path}")
    public ResponseEntity<Image> getImageByPath(@PathVariable String path) {
        return ResponseEntity.ok(imageService.getImageByPath(path));
    }

    @PostMapping
    public ResponseEntity<Image> addImage(@RequestBody ImageRequestDTO imageRequest) {
        User user = userService.getUserById(imageRequest.userId());
        Image image = imageService.addImage(user, imageRequest.path());
        return new ResponseEntity<Image>(image, HttpStatus.CREATED);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteImage(@PathVariable Long id) {
        imageService.deleteImage(id);
        return ResponseEntity.noContent().build();
    }

}

