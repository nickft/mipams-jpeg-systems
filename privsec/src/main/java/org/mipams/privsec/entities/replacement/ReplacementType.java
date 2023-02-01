package org.mipams.privsec.entities.replacement;

import org.mipams.jumbf.util.MipamsException;

public enum ReplacementType {

    BOX("box", 0),
    APP("app", 1),
    ROI("roi", 2),
    FILE("file", 3);

    private String type;
    private int id;

    private ReplacementType(String type, int id) {
        setType(type);
        setId(id);
    }

    public static ReplacementType getTypeFromString(String type) throws MipamsException {
        for (ReplacementType val : values()) {
            if (val.getType().equals(type)) {
                return val;
            }
        }
        throw new MipamsException(getErrorMessage());
    }

    public static ReplacementType getTypeFromId(int id) throws MipamsException {
        for (ReplacementType val : values()) {
            if (val.getId() == id) {
                return val;
            }
        }
        throw new MipamsException(getErrorMessage());
    }

    static String getErrorMessage() {
        return String.format("Method is not supported. Supported methods are: %s, %s, %s, %s",
                BOX.getType(), APP.getType(), ROI.getType(), FILE.getType());
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }
}