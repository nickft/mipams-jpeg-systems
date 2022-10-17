package org.mipams.jumbf.core.services.boxes;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.mipams.jumbf.core.entities.BmffBox;
import org.mipams.jumbf.core.entities.JumbfBox;
import org.mipams.jumbf.core.entities.PaddingBox;
import org.mipams.jumbf.core.entities.ParseMetadata;
import org.mipams.jumbf.core.entities.ServiceMetadata;
import org.mipams.jumbf.core.services.content_types.ContentTypeService;
import org.mipams.jumbf.core.ContentTypeDiscoveryManager;
import org.mipams.jumbf.core.util.CoreUtils;
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
    protected void writeBmffPayloadToJumbfFile(JumbfBox jumbfBox, OutputStream outputStream)
            throws MipamsException {

        descriptionBoxService.writeToJumbfFile(jumbfBox.getDescriptionBox(), outputStream);

        String contentTypeUuid = jumbfBox.getDescriptionBox().getUuid();

        ContentTypeService contentTypeService = contentTypeDiscoveryManager
                .getContentBoxServiceBasedOnContentUuid(contentTypeUuid);

        contentTypeService.writeContentBoxesToJumbfFile(jumbfBox.getContentBoxList(), outputStream);

        if (jumbfBox.getPaddingBox() != null) {
            paddingBoxService.writeToJumbfFile(jumbfBox.getPaddingBox(), outputStream);
        }

    }

    @Override
    protected void populatePayloadFromJumbfFile(JumbfBox jumbfBox, ParseMetadata parseMetadata, InputStream input)
            throws MipamsException {

        logger.debug("Jumbf box");

        final long nominalPayloadSize = jumbfBox.getPayloadSizeFromBmffHeaders();

        ParseMetadata descriptionParseMetadata = new ParseMetadata();
        descriptionParseMetadata.setAvailableBytesForBox(nominalPayloadSize);
        descriptionParseMetadata.setParentDirectory(parseMetadata.getParentDirectory());

        jumbfBox.setDescriptionBox(descriptionBoxService.parseFromJumbfFile(input, descriptionParseMetadata));

        long actualSize = jumbfBox.getDescriptionBox().getBoxSizeFromBmffHeaders();

        String contentTypeUuid = jumbfBox.getDescriptionBox().getUuid();

        ContentTypeService contentTypeService = contentTypeDiscoveryManager
                .getContentBoxServiceBasedOnContentUuid(contentTypeUuid);

        ParseMetadata contentParseMetadata = new ParseMetadata();
        contentParseMetadata.setAvailableBytesForBox(nominalPayloadSize != 0 ? nominalPayloadSize - actualSize : 0);
        contentParseMetadata.setParentDirectory(parseMetadata.getParentDirectory());

        List<BmffBox> contentBoxList = contentTypeService.parseContentBoxesFromJumbfFile(input, contentParseMetadata);
        jumbfBox.setContentBoxList(contentBoxList);

        actualSize += jumbfBox.calculateContentBoxListSize(contentBoxList);

        try {
            if (input.available() > 0 && CoreUtils.isPaddingBoxNext(input)) {

                ParseMetadata paddingParseMetadata = new ParseMetadata();
                paddingParseMetadata
                        .setAvailableBytesForBox(nominalPayloadSize != 0 ? nominalPayloadSize - actualSize : 0);
                paddingParseMetadata.setParentDirectory(parseMetadata.getParentDirectory());

                PaddingBox paddingBox = paddingBoxService.parseFromJumbfFile(input, paddingParseMetadata);
                jumbfBox.setPaddingBox(paddingBox);
            }
        } catch (IOException e) {
            throw new MipamsException(e);
        }

        logger.debug("Discovered box: " + jumbfBox.toString());
    }

    public JumbfBox parseSuperBox(InputStream input, ParseMetadata parseMetadata) throws MipamsException {
        return super.parseFromJumbfFile(input, parseMetadata);
    }
}