package org.mipams.jumbf.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Properties {

    @Value("${org.mipams.image_folder}")
    private String IMAGE_FOLDER;

    @Value("${org.mipams.max_file_size_in_bytes}")
    private long MAX_FILE_SIZE;

    public void checkIfFileSizeExceedApplicationLimits(String filePath) throws MipamsException {
        long size = CoreUtils.getFileSizeFromPath(filePath);
        if (size > MAX_FILE_SIZE) {
            throw new MipamsException("File is too large for the application. Check the available limits.");
        }
    }

    public String getFileDirectory() {
        return IMAGE_FOLDER;
    }
}
