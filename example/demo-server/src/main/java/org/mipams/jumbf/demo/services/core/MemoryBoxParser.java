package org.mipams.jumbf.demo.services.core;

import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.mipams.jumbf.core.entities.MemoryBox;
import org.mipams.jumbf.core.util.BadRequestException;
import org.mipams.jumbf.core.util.CoreUtils;
import org.mipams.jumbf.core.util.MipamsException;
import org.springframework.beans.factory.annotation.Value;

public abstract class MemoryBoxParser<T extends MemoryBox> extends BmffBoxParser<T> {

    @Value("${org.mipams.core.image_folder}")
    String ASSET_DIRECTORY;

    @Override
    protected void populateBox(T memoryBox, ObjectNode input) throws MipamsException {

        if (input == null) {
            throw new BadRequestException(
                    "Expected input for box type " + memoryBox.getType() + " but nothing found");
        }

        String fileName = input.get("fileName").asText();

        if (fileName == null) {
            throw new BadRequestException("Path is not specified");
        }

        File f = new File(CoreUtils.getFullPath(ASSET_DIRECTORY, fileName));

        try {
            memoryBox.setContent(Files.readAllBytes(f.toPath()));
        } catch (IOException e) {
            throw new MipamsException(e);
        }
    }
}
