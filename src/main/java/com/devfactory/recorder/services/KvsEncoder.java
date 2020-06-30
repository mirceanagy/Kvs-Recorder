package com.devfactory.recorder.services;

import com.github.kokorin.jaffree.ffmpeg.FFmpeg;
import com.github.kokorin.jaffree.ffmpeg.UrlInput;
import com.github.kokorin.jaffree.ffmpeg.UrlOutput;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Paths;

@Service
public class KvsEncoder {

    @Value("${ffmpeg-bin}")
    private String ffmpegBin;

    public InputStream encode(InputStream is, String identifier) {
        try {
            File inputFile = streamToFile(is, identifier);
            File outputFile = File.createTempFile(identifier + "-out", ".mkv");
            outputFile.deleteOnExit();
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                FFmpeg.atPath(Paths.get(ffmpegBin))
                        .addInput(UrlInput.fromPath(inputFile.toPath()))
                        .addOutput(UrlOutput.toPath(outputFile.toPath())/*.setCodec(StreamType.VIDEO, "V_MPEG4/ISO/AVC")*//*.setFormat()*/.copyAllCodecs())
                        .setOverwriteOutput(true)
                        .execute();
                return new FileInputStream(outputFile);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static File streamToFile(InputStream in, String identifier) throws IOException {
        final File tempFile = File.createTempFile(identifier, null);
        tempFile.deleteOnExit();
        try (FileOutputStream out = new FileOutputStream(tempFile)) {
            IOUtils.copy(in, out);
        }
        return tempFile;
    }
}
