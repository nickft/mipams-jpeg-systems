package org.mipams.jpegsnack.entities;

import java.util.ArrayList;
import java.util.List;

import org.mipams.jumbf.entities.BmffBox;
import org.mipams.jumbf.util.CoreUtils;
import org.mipams.jumbf.util.MipamsException;

public class JpegSnackDescriptionBox extends BmffBox {

    int version;
    long startTime;
    Integer noOfCompositions;
    List<Composition> compositions = new ArrayList<>();

    public static int TYPE_ID = 0x6A736462;
    public static String TYPE = "jsdb";

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

        result += getVersionSize();

        result += getStartTimeSize();

        if (hasNoOfCompositions()) {
            result += getNoOfCompositionsSize();
        }

        for (Composition composition : compositions) {
            result += composition.computeSizeInBytes();
        }

        return result;
    }

    private long getVersionSize() {
        return 1;
    }

    private long getStartTimeSize() {
        return CoreUtils.LONG_BYTE_SIZE;
    }

    private boolean hasNoOfCompositions() {
        return noOfCompositions != null;
    }

    private long getNoOfCompositionsSize() {
        return 1;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getVersion() {
        return version;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setNoOfCompositions(Integer noOfCompositions) {
        this.noOfCompositions = noOfCompositions;
    }

    public Integer getNoOfCompositions() {
        return noOfCompositions;
    }

    public void setCompositions(List<Composition> compositions) {
        this.compositions.clear();
        this.compositions.addAll(compositions);
    }

    public List<Composition> getCompositions() {
        return compositions;
    }
}
