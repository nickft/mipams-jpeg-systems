package org.mipams.jpegsnack.entities;

import org.mipams.jpegsnack.util.JpegSnackException;
import org.mipams.jumbf.util.CoreUtils;

public class InstructionParameter {

    int toggle = 0;

    public InstructionParameter(int ltyp) {
        this.toggle = ltyp;
    }

    public InstructionParameter() {
    }

    Integer horizontalOffset;
    Integer verticalOffset;
    Integer width;
    Integer height;
    Boolean persist;
    Integer life;
    Integer nextUse;
    Integer horizontalCropOffset;
    Integer verticalCropOffset;
    Integer croppedWidth;
    Integer croppedHeight;
    Integer rotation;

    public long calculateSizeInBytes() {
        long result = 0;
        if (xoAndYoExist()) {
            result += 2 * CoreUtils.INT_BYTE_SIZE;
        }

        if (widthAndHeightExist()) {
            result += 2 * CoreUtils.INT_BYTE_SIZE;
        }

        if (persistAndLifeAndNextUseExist()) {
            result += 2 * CoreUtils.INT_BYTE_SIZE;
        }

        if (xcAndYcAndHcAndWcExist()) {
            result += 4 * CoreUtils.INT_BYTE_SIZE;
        }

        if (rotExists()) {
            result += CoreUtils.INT_BYTE_SIZE;
        }
        return result;
    }

    public boolean xoAndYoExist() {
        return CoreUtils.isBitAtGivenPositionSet(toggle, 0);
    }

    public boolean widthAndHeightExist() {
        return CoreUtils.isBitAtGivenPositionSet(toggle, 1);
    }

    public boolean persistAndLifeAndNextUseExist() {
        return CoreUtils.isBitAtGivenPositionSet(toggle, 3);
    }

    public boolean xcAndYcAndHcAndWcExist() {
        return CoreUtils.isBitAtGivenPositionSet(toggle, 5);
    }

    public boolean rotExists() {
        return CoreUtils.isBitAtGivenPositionSet(toggle, 6);
    }

    public void setHorizontalOffset(Integer horizontalOffset) {
        this.horizontalOffset = horizontalOffset;
    }

    public Integer getHorizontalOffset() {
        return horizontalOffset;
    }

    public void setVerticalOffset(Integer verticalOffset) {
        this.verticalOffset = verticalOffset;
    }

    public Integer getVerticalOffset() {
        return verticalOffset;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getWidth() {
        return width;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getHeight() {
        return height;
    }

    public void setPersist(Boolean persist) {
        this.persist = persist;
    }

    public Boolean getPersist() {
        return persist;
    }

    public void setLife(Integer life) {
        this.life = life;
    }

    public Integer getLife() {
        return life;
    }

    public void setNextUse(Integer nextUse) {
        this.nextUse = nextUse;
    }

    public Integer getNextUse() {
        return nextUse;
    }

    public void setHorizontalCropOffset(Integer horizontalCropOffset) {
        this.horizontalCropOffset = horizontalCropOffset;
    }

    public Integer getHorizontalCropOffset() {
        return horizontalCropOffset;
    }

    public void setVerticalCropOffset(Integer verticalCropOffset) {
        this.verticalCropOffset = verticalCropOffset;
    }

    public Integer getVerticalCropOffset() {
        return verticalCropOffset;
    }

    public void setCroppedWidth(Integer croppedWidth) {
        this.croppedWidth = croppedWidth;
    }

    public Integer getCroppedWidth() {
        return croppedWidth;
    }

    public void setCroppedHeight(Integer croppedHeight) {
        this.croppedHeight = croppedHeight;
    }

    public Integer getCroppedHeight() {
        return croppedHeight;
    }

    public void setRotation(Integer rotation) {
        this.rotation = rotation;
    }

    public Integer getRotation() {
        return rotation;
    }

    public int getPersistAndLifeValue() throws JpegSnackException {
        if (!persist) {
            return life;
        }
        return life |= (1 << 31);
    }

    public int applyAndReturnToggleForInstructionParameter() throws JpegSnackException {
        int toggle = 0;

        boolean xoExists = getHorizontalOffset() != null;
        boolean yoExists = getVerticalOffset() != null;

        if (Boolean.logicalXor(xoExists, yoExists)) {
            throw new JpegSnackException(String.format(
                    "InstructionSetBox structure is corrupted: either Horizontal Offset " +
                            "(XO - exists: [%b]) or Vertical Offset (YO - exists: [%b]) is not set.",
                    xoExists, yoExists));
        }

        if (xoExists) {
            toggle = toggle | 1;
        }

        boolean widthExists = getWidth() != null;
        boolean heightExists = getHeight() != null;

        if (Boolean.logicalXor(widthExists, heightExists)) {
            throw new JpegSnackException(String.format(
                    "InstructionSetBox structure is corrupted: either Width " +
                            "(exists: [%b]) or Height (exists: [%b]) is not set.",
                    widthExists, heightExists));
        }

        if (widthExists) {
            toggle = toggle | 2;
        }

        boolean persistExists = getPersist() != null;
        boolean lifeExists = getLife() != null;
        boolean nextUseExists = getNextUse() != null;

        if (Boolean.logicalOr(Boolean.logicalXor(persistExists, lifeExists),
                Boolean.logicalXor(lifeExists, nextUseExists))) {
            throw new JpegSnackException(String.format(
                    "InstructionSetBox structure is corrupted: Something between Persist "
                            + "(exists: [%b]), Life (exists: [%b]) and Next-Use (exists: [%b]) is not set.",
                    persistExists, lifeExists, nextUseExists));
        }

        if (persistExists) {
            toggle = toggle | 8;
        }

        boolean xcExists = getHorizontalCropOffset() != null;
        boolean ycExists = getVerticalCropOffset() != null;
        boolean hcExists = getCroppedHeight() != null;
        boolean wcExists = getCroppedWidth() != null;

        if (Boolean.logicalOr(Boolean.logicalXor(xcExists, ycExists),
                Boolean.logicalXor(hcExists, wcExists))) {
            throw new JpegSnackException(String.format(
                    "InstructionSetBox structure is corrupted: Something between Horizontal "
                            + "Crop Offset (XC - exists: [%b]), Vertical Crop Offset (YC - exists: [%b]), "
                            + "Cropped Width (WC - exists: [%b]) and Cropped Height (HC - exists: [%b]) is not set.",
                    xcExists, ycExists, wcExists, hcExists));
        }

        if (xcExists) {
            toggle = toggle | 32;
        }

        boolean rotExists = getRotation() != null;
        if (rotExists) {
            toggle = toggle | 64;
        }

        this.toggle = toggle;

        return toggle;
    }
}
