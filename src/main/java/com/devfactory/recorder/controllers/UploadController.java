package com.devfactory.recorder.controllers;

import com.devfactory.recorder.services.FileSystemStorageService;
import com.devfactory.recorder.services.KvsEncoder;
import com.devfactory.recorder.services.KvsStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@RestController
public class UploadController {

    @Autowired
    private FileSystemStorageService storageService;

    @Autowired
    private KvsEncoder encoder;

    @PostMapping("/upload")
    public void handleFileUpload(@RequestParam("filename") String fileName, @RequestParam("blob") MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("Failed to store empty file " + fileName);
        }
        try (InputStream is = file.getInputStream()) {
//            InputStream encodedInput = encoder.encode(file.getInputStream(), fileName);
            storageService.store(fileName, is);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
