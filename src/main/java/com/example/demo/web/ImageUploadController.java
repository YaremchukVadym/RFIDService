package com.example.demo.web;

import com.example.demo.entity.ImageModel;
import com.example.demo.payload.response.MessageResponse;
import com.example.demo.services.ImageUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;

@RestController
@RequestMapping("api/image")
@CrossOrigin
public class ImageUploadController {

    @Autowired
    private ImageUploadService imageUploadService;

    @PostMapping("/upload")
    public ResponseEntity<MessageResponse> uploadImageToUser(@RequestParam("file") MultipartFile file,
                                                             Principal principal) throws IOException {
        imageUploadService.uploadImageToUser(file, principal);
        return ResponseEntity.ok(new MessageResponse("Image for user Uploaded Successfully"));
    }

    @PostMapping("/{itemId}/upload")
    public ResponseEntity<MessageResponse> uploadImageToPost(@PathVariable("itemId") String itemId,
                                                             @RequestParam("file") MultipartFile file,
                                                             Principal principal) throws IOException {
        imageUploadService.uploadImageToItem(file, principal, Long.parseLong(itemId));
        return ResponseEntity.ok(new MessageResponse("Image for item Uploaded Successfully"));
    }

    @GetMapping("/profileImage")
    public ResponseEntity<ImageModel> getImageForUser(Principal principal) {
        ImageModel userImage = imageUploadService.getImageToUser(principal);
        return new ResponseEntity<>(userImage, HttpStatus.OK);
    }

    @GetMapping("/{itemId}/image")
    public ResponseEntity<ImageModel> getImageToPost(@PathVariable("itemId") String itemId) {
        ImageModel itemImage = imageUploadService.getImageToItem(Long.parseLong(itemId));
        return new ResponseEntity<>(itemImage, HttpStatus.OK);
    }


}