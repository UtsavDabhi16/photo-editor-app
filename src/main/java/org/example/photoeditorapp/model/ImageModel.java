package org.example.photoeditorapp.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Min;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ImageModel {

    private MultipartFile fileName;

    @Min(value = 0, message = "X coordinate must be non-negative")
    private int x;

    @Min(value = 0, message = "Y coordinate must be non-negative")
    private int y;

    @Min(value = 1, message = "Width must be at least 1")
    private int width;

    @Min(value = 1, message = "Height must be at least 1")
    private int height;

    private double rotationAngle;
}
