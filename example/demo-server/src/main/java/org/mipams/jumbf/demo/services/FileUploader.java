package org.mipams.jumbf.demo.services;

import java.io.File;
import java.io.IOException;

import org.mipams.jumbf.core.util.CoreUtils;
import org.mipams.jumbf.core.util.MipamsException;
import org.mipams.jumbf.demo.entities.UploadRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class FileUploader {

    @Value("${org.mipams.core.image_folder}")
    String ASSET_DIRECTORY;

    public String saveFileToDiskAndGetFileName(UploadRequest request) throws MipamsException {

        String targetFileName = String.format("%s.jumbf", CoreUtils.randomStringGenerator());

        String targetFilePath = CoreUtils.getFullPath(ASSET_DIRECTORY, targetFileName);

        File targetFile = new File(targetFilePath);

        try {
            request.getFile().transferTo(targetFile);
            return targetFileName;
        } catch (IllegalStateException | IOException e) {
            throw new MipamsException("Could not upload file", e);
        }

    }

}
