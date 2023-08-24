package org.mipams.jpegsnack.services.boxes;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import org.mipams.jpegsnack.entities.ObjectMetadataBox;
import org.mipams.jumbf.entities.ParseMetadata;
import org.mipams.jumbf.entities.ServiceMetadata;
import org.mipams.jumbf.services.boxes.BmffBoxService;
import org.mipams.jumbf.util.CoreUtils;
import org.mipams.jumbf.util.MipamsException;
import org.springframework.stereotype.Service;

@Service
public class ObjectMetadataBoxService extends BmffBoxService<ObjectMetadataBox> {

    ServiceMetadata serviceMetadata = new ServiceMetadata(ObjectMetadataBox.TYPE_ID,
            ObjectMetadataBox.TYPE);

    @Override
    public ServiceMetadata getServiceMetadata() {
        return serviceMetadata;
    }

    @Override
    protected ObjectMetadataBox initializeBox() {
        return new ObjectMetadataBox();
    }

    @Override
    protected void populatePayloadFromJumbfFile(ObjectMetadataBox obmbBox, ParseMetadata metadata, InputStream is)
            throws MipamsException {

        int toggle = CoreUtils.readSingleByteAsIntFromInputStream(is);
        obmbBox.setToggle(toggle);

        int id = CoreUtils.readSingleByteAsIntFromInputStream(is);
        obmbBox.setId(id);

        String mediaType = CoreUtils.readStringFromInputStream(is);
        obmbBox.setMediaType(mediaType);

        if (obmbBox.numberOfMediaExists()) {
            int noOfMedia = CoreUtils.readSingleByteAsIntFromInputStream(is);
            obmbBox.setNoOfMedia(noOfMedia);
        }

        if (obmbBox.opacityExists()) {
            float opacity = CoreUtils.readFloatFromInputStream(is);
            obmbBox.setOpacity(opacity);
        }

        if (obmbBox.styleExists()) {
            String style = CoreUtils.readStringFromInputStream(is);
            obmbBox.setStyle(style);
        }

        int totalLocations = (obmbBox.numberOfMediaExists()) ? obmbBox.getNoOfMedia() : 1;

        for (int i = 0; i < totalLocations; i++) {
            String location = CoreUtils.readStringFromInputStream(is);
            obmbBox.getLocations().add(location);
        }

    }

    @Override
    protected void writeBmffPayloadToJumbfFile(ObjectMetadataBox obmbBox, OutputStream os) throws MipamsException {
        CoreUtils.writeIntAsSingleByteToOutputStream(obmbBox.getToggle(), os);

        CoreUtils.writeIntAsSingleByteToOutputStream(obmbBox.getId(), os);

        CoreUtils.writeTextToOutputStream(CoreUtils.addEscapeCharacterToText(obmbBox.getMediaType()), os);

        if (obmbBox.numberOfMediaExists()) {
            CoreUtils.writeIntAsSingleByteToOutputStream(obmbBox.getNoOfMedia(), os);
        }

        if (obmbBox.opacityExists()) {
            CoreUtils.writeFloatToOutputStream(obmbBox.getOpacity(), os);
        }

        if (obmbBox.styleExists()) {
            CoreUtils.writeTextToOutputStream(obmbBox.getStyle(), os);
        }

        int total = (obmbBox.numberOfMediaExists()) ? obmbBox.getNoOfMedia() : 1;

        for (int i = 0; i < total; i++) {
            CoreUtils.writeTextToOutputStream(CoreUtils.addEscapeCharacterToText(obmbBox.getLocations().get(i)), os);
        }
    }

}
