package org.example.photoeditorapp.controller;


import org.example.photoeditorapp.model.ImageModel;
import org.example.photoeditorapp.service.PhotoEditorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;

@Controller
public class PhotoEditorController {

    private static final Logger logger = LoggerFactory.getLogger(PhotoEditorController.class);


    @Autowired
    private PhotoEditorService photoEditorService;

    @GetMapping("/")
    public String showEditor(Model model) {
        model.addAttribute("imageModel", new ImageModel());
        return "editor";
    }

    @PostMapping("/upload")
    public String uploadImage(@RequestParam("file") MultipartFile file, Model model) {
        try {
            photoEditorService.saveImage(file);
            model.addAttribute("successMessage", "Image uploaded successfully!");
            logger.info("Image uploaded successfully!");
        } catch (IOException e) {
            model.addAttribute("errorMessage", "Failed to upload image: " + e.getMessage());
            logger.error("Failed to upload image", e);
        }
        return "redirect:/";
    }

    @PostMapping("/crop")
    @ResponseBody
    public ResponseEntity<Resource> cropImage(@ModelAttribute @Valid ImageModel imageModel, BindingResult result) {
        if (result.hasErrors()) {
            logger.error("Validation error in cropImage method: {}", result.getAllErrors());
            return ResponseEntity.badRequest().body(null);
        }
        try {
            Resource croppedImageResource = photoEditorService.getCroppedImageResource(imageModel);

            logger.info("Image cropped successfully");
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);

            return ResponseEntity.ok().headers(headers).body(croppedImageResource);
        } catch (IOException e) {
            logger.error("Failed to crop image", e);
            return ResponseEntity.badRequest().body(null);
        }
    }


    @PostMapping("/rotate")
    @ResponseBody
    public ResponseEntity<Resource> rotateImage(@ModelAttribute @Valid ImageModel imageModel, BindingResult result) {
        if (result.hasErrors()) {
            logger.error("Validation error in rotateImage method: {}", result.getAllErrors());
            return ResponseEntity.badRequest().body(null);
        }
        try {
            Resource rotatedImage = photoEditorService.rotateImage(imageModel);
            logger.info("Image rotated successfully");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);

            return ResponseEntity.ok().headers(headers).body(rotatedImage);
        } catch (IOException e) {
            logger.error("Failed to rotate image", e);
            return ResponseEntity.badRequest().body(null);
        }
    }

}
