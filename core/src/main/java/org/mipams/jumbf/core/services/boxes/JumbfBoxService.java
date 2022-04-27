package org.mipams.jumbf.core.services.boxes;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.mipams.jumbf.core.entities.BmffBox;
import org.mipams.jumbf.core.entities.JumbfBox;
import org.mipams.jumbf.core.entities.PaddingBox;
import org.mipams.jumbf.core.entities.ServiceMetadata;
import org.mipams.jumbf.core.services.content_types.ContentTypeService;
import org.mipams.jumbf.core.ContentTypeDiscoveryManager;
import org.mipams.jumbf.core.util.MipamsException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public final class JumbfBoxService extends BmffBoxService<JumbfBox> {

    private static final Logger logger = LoggerFactory.getLogger(JumbfBoxService.class);

    @Autowired
    DescriptionBoxService descriptionBoxService;

    @Autowired
    ContentTypeDiscoveryManager contentTypeDiscoveryManager;

    @Autowired
    PaddingBoxService paddingBoxService;

    ServiceMetadata serviceMetadata;

    @PostConstruct
    void init() {
        JumbfBox box = initializeBox();
        serviceMetadata = new ServiceMetadata(box.getTypeId(), box.getType());
    }

    @Override
    protected JumbfBox initializeBox() {
        return new JumbfBox();
    }

    @Override
    public ServiceMetadata getServiceMetadata() {
        return serviceMetadata;
    }

    @Override
    protected void writeBmffPayloadToJumbfFile(JumbfBox jumbfBox, FileOutputStream fileOutputStream)
            throws MipamsException {

        descriptionBoxService.writeToJumbfFile(jumbfBox.getDescriptionBox(), fileOutputStream);

        String contentTypeUuid = jumbfBox.getDescriptionBox().getUuid();

        ContentTypeService contentTypeService = contentTypeDiscoveryManager
                .getContentBoxServiceBasedOnContentUuid(contentTypeUuid);

        contentTypeService.writeContentBoxesToJumbfFile(jumbfBox.getContentBoxList(), fileOutputStream);

        if (jumbfBox.getPaddingBox() != null) {
            paddingBoxService.writeToJumbfFile(jumbfBox.getPaddingBox(), fileOutputStream);
        }

    }

    @Override
    protected void populatePayloadFromJumbfFile(JumbfBox jumbfBox, long availableBytesForBox, InputStream input)
            throws MipamsException {

        logger.debug("Jumbf box");

        final long nominalPayloadSize = jumbfBox.getPayloadSizeFromBmffHeaders();

        jumbfBox.setDescriptionBox(descriptionBoxService.parseFromJumbfFile(input, nominalPayloadSize));

        long actualSize = jumbfBox.getDescriptionBox().getBoxSizeFromBmffHeaders();

        String contentTypeUuid = jumbfBox.getDescriptionBox().getUuid();

        ContentTypeService contentTypeService = contentTypeDiscoveryManager
                .getContentBoxServiceBasedOnContentUuid(contentTypeUuid);

        long remainingBytes = nominalPayloadSize - actualSize;

        List<BmffBox> contentBoxList = contentTypeService.parseContentBoxesFromJumbfFile(input, remainingBytes);
        jumbfBox.setContentBoxList(contentBoxList);

        actualSize += jumbfBox.calculateContentBoxListSize(contentBoxList);

        if (!actualBoxSizeEqualsToSizeSpecifiedInBmffHeaders(jumbfBox)) {
            PaddingBox paddingBox = paddingBoxService.parseFromJumbfFile(input, nominalPayloadSize - actualSize);
            jumbfBox.setPaddingBox(paddingBox);
        }

        logger.debug("Discovered box: " + jumbfBox.toString());
    }

    public JumbfBox parseSuperBox(InputStream input) throws MipamsException {
        return super.parseFromJumbfFile(input, -1);
    }
}