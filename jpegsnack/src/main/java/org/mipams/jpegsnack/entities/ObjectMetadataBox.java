package org.mipams.jpegsnack.entities;

import java.util.ArrayList;
import java.util.List;

import org.mipams.jpegsnack.util.JpegSnackException;
import org.mipams.jumbf.entities.BmffBox;
import org.mipams.jumbf.util.CoreUtils;
import org.mipams.jumbf.util.MipamsException;

public class ObjectMetadataBox extends BmffBox {

    int toggle;
    int id;
    String mediaType;
    Integer noOfMedia;
    Float opacity;
    String style;
    List<String> locations = new ArrayList<>();

    public static int TYPE_ID = 0x6F626D62;
    public static String TYPE = "obmb";

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public int getTypeId() {
        return TYPE_ID;
    }

    @Override
    protected long calculatePayloadSize() throws MipamsException {
        long result = 0;

        result += getToggleSize();

        result += getIdSize();

        result += getMediaTypeSize();

        if (numberOfMediaExists()) {
            result += 1;
        }

        if (opacityExists()) {
            result += getOpacitySize();
        }

        if (styleExists()) {
            result += getStyleSize();
        }

        result += getLocationsSize();

        return result;
    }

    public boolean opacityExists() {
        return CoreUtils.isBitAtGivenPositionSet(toggle, 2);
    }

    private long getOpacitySize() {
        return CoreUtils.INT_BYTE_SIZE;
    }

    public boolean styleExists() {
        return CoreUtils.isBitAtGivenPositionSet(toggle, 1);
    }

    private long getStyleSize() {
        return CoreUtils.addEscapeCharacterToText(style).length();
    }

    private long getToggleSize() {
        return 1;
    }

    private long getIdSize() {
        return 1;
    }

    private long getMediaTypeSize() {
        return CoreUtils.addEscapeCharacterToText(mediaType).length();
    }

    public boolean numberOfMediaExists() {
        return CoreUtils.isBitAtGivenPositionSet(toggle, 0);
    }

    private long getLocationsSize() {
        long result = 0;
        for (String location : locations) {
            result += CoreUtils.addEscapeCharacterToText(location).length();
        }

        return result;
    }

    public void setToggle(int toggle) {
        this.toggle = toggle;
    }

    public int getToggle() {
        return toggle;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setNoOfMedia(int noOfMedia) {
        this.noOfMedia = noOfMedia;
    }

    public Integer getNoOfMedia() {
        return noOfMedia;
    }

    public void setOpacity(float opacity) {
        this.opacity = opacity;
    }

    public Float getOpacity() {
        return opacity;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getStyle() {
        return style;
    }

    public void setLocations(List<String> locations) {
        this.locations.clear();
        this.locations.addAll(locations);
    }

    public List<String> getLocations() {
        return locations;
    }

    @Override
    public void applyInternalBoxFieldsBasedOnExistingData() throws MipamsException {
        int toggle = 0;

        if (getNoOfMedia() != null) {
            toggle = toggle | 1;
        }

        if (getStyle() != null) {
            toggle = toggle | 2;
        }

        if (getOpacity() != null) {
            toggle = toggle | 4;
        }

        setToggle(toggle);
    }
}
