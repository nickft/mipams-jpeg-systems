package org.mipams.jumbf.demo.entities;

import org.springframework.web.multipart.MultipartFile;

public class UploadRequest {
    private MultipartFile file;

    public MultipartFile getFile() {
        return this.file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    @Override
    public String toString() {
        return "UploadRequest(file=" + file.getName() + ")";
    }
}