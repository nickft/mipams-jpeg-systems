package org.mipams.jumbf.demo.entities;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

@ToString
public class UploadRequest {
    private @Getter @Setter @NonNull MultipartFile file;
}