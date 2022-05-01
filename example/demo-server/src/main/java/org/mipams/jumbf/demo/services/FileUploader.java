package org.mipams.jumbf.demo.services;

import java.io.File;
import java.io.IOException;

import org.mipams.jumbf.core.util.CoreUtils;
import org.mipams.jumbf.core.util.MipamsException;
import org.mipams.jumbf.demo.entities.UploadRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;

import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@Service
public class FileUploader {

    @Value("${org.mipams.core.image_folder}")
    String ASSET_DIRECTORY;

    public String saveFileToDiskAndGetFileName(UploadRequest request, boolean randomTargetName) throws MipamsException {

        String targetFileName = randomTargetName ? String.format("%s.jumbf", CoreUtils.randomStringGenerator())
                : request.getFile().getOriginalFilename();

        String targetFilePath = CoreUtils.getFullPath(ASSET_DIRECTORY, targetFileName);

        File targetFile = new File(targetFilePath);

        try {
            request.getFile().transferTo(targetFile);
            return targetFileName;
        } catch (IllegalStateException | IOException e) {
            throw new MipamsException("Could not upload file", e);
        }
    }

    public ResponseEntity<?> createOctetResponse(String fileName) throws MipamsException {
        UrlResource urlResource;

        String fileUrl = CoreUtils.getFullPath(ASSET_DIRECTORY, fileName);

        try {
            urlResource = new UrlResource("file:" + fileUrl);
        } catch (MalformedURLException e) {
            throw new MipamsException("The file path is malformed", e);
        }

        StringBuilder headerValue = new StringBuilder("attachment; filename=\"").append(fileUrl).append("\"");
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, headerValue.toString())
                .body(urlResource);
    }

}
