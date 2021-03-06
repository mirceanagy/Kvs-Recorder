package com.devfactory.recorder.services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileSystemStorageService {

    private final static String DIR = "C:/files";
    private final Path rootLocation = Paths.get(DIR);

    public void store(String fileName, InputStream is) {
        try {
            Files.createDirectories(rootLocation);
            if (fileName.contains("..")) {
                // This is a security check
                throw new RuntimeException(
                        "Cannot store file with relative path outside current directory "
                                + fileName);
            }
            Files.copy(is, this.rootLocation.resolve(fileName),
                    StandardCopyOption.REPLACE_EXISTING);
        }
        catch (IOException e) {
            throw new RuntimeException("Failed to store file " + fileName, e);
        }
    }

}
