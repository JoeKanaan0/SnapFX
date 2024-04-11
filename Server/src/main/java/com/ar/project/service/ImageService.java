package com.ar.project.service;

import com.ar.project.exception.ResourceNotFoundException;
import com.ar.project.model.Image;
import com.ar.project.model.User;
import com.ar.project.repository.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ImageService {

    @Autowired
    private ImageRepository imageRepository;

    public List<Image> getAllUserImages(Long userId) {
        return imageRepository.findByUserId(userId);
    }

    public Image getImageById(Long id) {
        return imageRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Image Not Found"));
    }

    public Image getImageByPath(String path) {
        return imageRepository.findByPath(path);
    }

    public Image addImage(User user, String path) {
        Image image = new Image(user, path);
        return imageRepository.save(image);
    }

    public void deleteImage(Long id) {
        imageRepository.deleteById(id);
    }
}
