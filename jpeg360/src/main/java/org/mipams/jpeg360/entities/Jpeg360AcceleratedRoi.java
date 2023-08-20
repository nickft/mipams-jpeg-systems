package org.mipams.jpeg360.entities;

public class Jpeg360AcceleratedRoi {

    Integer id;
    Integer roiNumber;
    Integer roiPosX = 0;
    Integer roiPosY = 0;
    Integer blockWidth;
    Integer blockHeight;
    Integer widthInBlocks;
    Integer heightInBlocks;
    Integer offsetX = 0;
    Integer offsetY = 0;
    Integer associatedViewport = -1;
    String boxReference;

    public void setUmfId(Integer id) {
        this.id = id;
    }

    public Integer getUmdId() {
        return id;
    }

    public void setRoiNumber(Integer roiNumber) {
        this.roiNumber = roiNumber;
    }

    public Integer getRoiNumber() {
        return this.roiNumber;
    }

    public void setRoiPosX(Integer roiPosX) {
        this.roiPosX = roiPosX;
    }

    public Integer getRoiPosX() {
        return this.roiPosX;
    }

    public void setRoiPosY(Integer roiPosY) {
        this.roiPosY = roiPosY;
    }

    public Integer getRoiPosY() {
        return this.roiPosY;
    }

    public void setBlockWidth(Integer blockWidth) {
        this.blockWidth = blockWidth;
    }

    public Integer getBlockWidth() {
        return blockWidth;
    }

    public void setBlockHeight(Integer blockHeight) {
        this.blockHeight = blockHeight;
    }

    public Integer getBlockHeight() {
        return blockHeight;
    }

    public void setWidthInBlocks(Integer widthInBlocks) {
        this.widthInBlocks = widthInBlocks;
    }

    public Integer getWidthInBlocks() {
        return widthInBlocks;
    }

    public void setHeightInBlocks(Integer heightInBlocks) {
        this.heightInBlocks = heightInBlocks;
    }

    public Integer getHeightInBlocks() {
        return heightInBlocks;
    }

    public void setOffsetX(Integer offsetX) {
        this.offsetX = offsetX;
    }

    public Integer getOffsetX() {
        return offsetX;
    }

    public void setOffsetY(Integer offsetY) {
        this.offsetY = offsetY;
    }

    public Integer getOffsetY() {
        return offsetY;
    }

    public void setAssociatedViewport(Integer associatedViewport) {
        this.associatedViewport = associatedViewport;
    }

    public Integer getAssociatedViewport() {
        return associatedViewport;
    }

    public void setBoxReference(String boxReference) {
        this.boxReference = boxReference;
    }

    public String getBoxReference() {
        return this.boxReference;
    }
}
