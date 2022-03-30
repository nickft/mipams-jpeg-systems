package org.mipams.jumbf.core.services;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.fasterxml.jackson.databind.node.ObjectNode;

import org.mipams.jumbf.core.entities.ContiguousCodestreamBox;
import org.mipams.jumbf.core.util.BadRequestException;
import org.mipams.jumbf.core.util.BoxTypeEnum;
import org.mipams.jumbf.core.util.CoreUtils;
import org.mipams.jumbf.core.util.MipamsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ContiguousCodestreamBoxService extends XTBoxService<ContiguousCodestreamBox> {

    private static final Logger logger = LoggerFactory.getLogger(ContiguousCodestreamBoxService.class);

    @Value("${org.mipams.core.image_folder}")
    private String IMAGE_FOLDER;

    @Value("${org.mipams.core.max_file_size_in_bytes}")
    private long MAX_FILE_SIZE;

    @Override
    protected ContiguousCodestreamBox initializeBox() throws MipamsException {
        return new ContiguousCodestreamBox();
    }

    @Override
    protected void populateBox(ContiguousCodestreamBox contiguousCodeStreamBox, ObjectNode input)
            throws MipamsException {

        String type = input.get("type").asText();

        if (!BoxTypeEnum.ContiguousCodestreamBox.getType().equals(type)) {
            throw new BadRequestException("Box type does not match with description type.");
        }

        String path = input.get("path").asText();

        if (path == null) {
            throw new BadRequestException("Path is not specified");
        }

        contiguousCodeStreamBox.setPathToCodestream(path);

        if (doesFileSizeExceedApplicationLimits(path)) {
            throw new BadRequestException("File is too large for the application. Check the available limits.");
        }
    }

    protected boolean doesFileSizeExceedApplicationLimits(String filePath) throws MipamsException {
        double size = CoreUtils.getFileSizeFromPath(filePath);
        return size > MAX_FILE_SIZE || size > Long.MAX_VALUE;
    }

    @Override
    protected void writeXTBoxPayloadToJumbfFile(ContiguousCodestreamBox contiguousCodeStreamBox,
            FileOutputStream fileOutputStream) throws MipamsException {
        CoreUtils.writeFileContentToOutput(contiguousCodeStreamBox.getPathToCodestream(), fileOutputStream);
    }

    @Override
    protected void populatePayloadFromJumbfFile(ContiguousCodestreamBox contiguousCodeStreamBox, InputStream input)
            throws MipamsException {
        logger.debug("Contiguous Codestream box");

        String fileName = CoreUtils.randomStringGenerator() + ".jpeg";

        String fullPath = CoreUtils.getFullPath(IMAGE_FOLDER, fileName);

        contiguousCodeStreamBox.setPathToCodestream(fullPath);

        try (FileOutputStream fileOutputStream = new FileOutputStream(fullPath)) {

            long nominalTotalSizeInBytes = contiguousCodeStreamBox.getPayloadSizeFromXTBoxHeaders();

            int actualBytes = 0, n;

            while ((actualBytes < nominalTotalSizeInBytes) && ((n = input.read()) != -1)) {
                fileOutputStream.write(n);
                actualBytes++;
            }

            logger.debug("Finished writing file to: " + fullPath);

        } catch (IOException e) {
            throw new MipamsException("Coulnd not read Json content", e);
        }
    }

    @Override
    public int serviceIsResponsibleForBoxTypeId() {
        return BoxTypeEnum.ContiguousCodestreamBox.getTypeId();
    }
}