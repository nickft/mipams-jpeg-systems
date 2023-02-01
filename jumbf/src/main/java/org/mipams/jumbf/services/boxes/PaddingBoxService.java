package org.mipams.jumbf.services.boxes;

import java.util.logging.Logger;
import java.util.logging.Level;

import java.io.OutputStream;
import java.io.InputStream;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.mipams.jumbf.ContentTypeDiscoveryManager;
import org.mipams.jumbf.entities.PaddingBox;
import org.mipams.jumbf.entities.ParseMetadata;
import org.mipams.jumbf.entities.ServiceMetadata;
import org.mipams.jumbf.util.CoreUtils;
import org.mipams.jumbf.util.MipamsException;

@Service
public final class PaddingBoxService extends BmffBoxService<PaddingBox> {

    private static final Logger logger = Logger.getLogger(PaddingBoxService.class.getName());

    @Autowired
    ContentTypeDiscoveryManager contentBoxDiscoveryManager;

    ServiceMetadata serviceMetadata;

    @PostConstruct
    void init() {
        PaddingBox box = initializeBox();
        serviceMetadata = new ServiceMetadata(box.getTypeId(), box.getType());
    }

    @Override
    protected PaddingBox initializeBox() {
        return new PaddingBox();
    }

    @Override
    public ServiceMetadata getServiceMetadata() {
        return serviceMetadata;
    }

    @Override
    protected void writeBmffPayloadToJumbfFile(PaddingBox paddingBox, OutputStream outputStream)
            throws MipamsException {
        CoreUtils.writePaddingToOutputStream(paddingBox.getPaddingSize(), PaddingBox.PADDING_VALUE,
                outputStream);
    }

    @Override
    protected void populatePayloadFromJumbfFile(PaddingBox paddingBox, ParseMetadata parseMetadata, InputStream input)
            throws MipamsException {

        logger.log(Level.FINE,"Padding box");

        long actualSize = CoreUtils.parsePaddingFromInputStream(input, PaddingBox.PADDING_VALUE,
                parseMetadata.getAvailableBytesForBox());
        paddingBox.setPaddingSize(actualSize);

        logger.log(Level.FINE,"Discovered box: " + paddingBox.toString());
    }

}