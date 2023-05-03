package org.mipams.jumbf.services.boxes;

import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.mipams.jumbf.entities.BmffBox;
import org.mipams.jumbf.entities.JumbfBox;
import org.mipams.jumbf.entities.PaddingBox;
import org.mipams.jumbf.entities.ParseMetadata;
import org.mipams.jumbf.entities.ServiceMetadata;
import org.mipams.jumbf.services.content_types.ContentTypeService;
import org.mipams.jumbf.ContentTypeDiscoveryManager;
import org.mipams.jumbf.util.CoreUtils;
import org.mipams.jumbf.util.MipamsException;

@Service
public final class JumbfBoxService extends BmffBoxService<JumbfBox> {

    private static final Logger logger = Logger.getLogger(JumbfBoxService.class.getName());

    @Autowired
    DescriptionBoxService descriptionBoxService;

    @Autowired
    ContentTypeDiscoveryManager contentTypeDiscoveryManager;

    @Autowired
    PaddingBoxService paddingBoxService;

    ServiceMetadata serviceMetadata = new ServiceMetadata(JumbfBox.TYPE_ID,
            JumbfBox.TYPE);

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

        logger.log(Level.FINE, "Jumbf box");

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

        if (nominalPayloadSize > 0 && nominalPayloadSize - actualSize == 0) {
            return;
        }

        Long remainingBytes = nominalPayloadSize != 0 ? nominalPayloadSize - actualSize : 0;

        try {
            if (input.available() > 0 && CoreUtils.isPaddingBoxNext(input)) {

                ParseMetadata paddingParseMetadata = new ParseMetadata();
                paddingParseMetadata.setAvailableBytesForBox(remainingBytes);
                paddingParseMetadata.setParentDirectory(parseMetadata.getParentDirectory());

                PaddingBox paddingBox = paddingBoxService.parseFromJumbfFile(input, paddingParseMetadata);
                jumbfBox.setPaddingBox(paddingBox);
            }
        } catch (IOException e) {
            throw new MipamsException(e);
        }

        logger.log(Level.FINE, "Discovered box: " + jumbfBox.toString());
    }

    public JumbfBox parseSuperBox(InputStream input, ParseMetadata parseMetadata) throws MipamsException {
        return super.parseFromJumbfFile(input, parseMetadata);
    }
}