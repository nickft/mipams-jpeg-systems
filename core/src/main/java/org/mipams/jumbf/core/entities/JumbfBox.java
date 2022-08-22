package org.mipams.jumbf.core.entities;

import java.util.List;

import org.mipams.jumbf.core.util.MipamsException;

public class JumbfBox extends BmffBox {

    protected DescriptionBox descriptionBox;
    protected List<BmffBox> contentBoxList;
    protected PaddingBox paddingBox;

    @Override
    public int getTypeId() {
        return 0x6A756D62;
    }

    @Override
    public String getType() {
        return "jumb";
    }

    @Override
    protected long calculatePayloadSize() throws MipamsException {
        long sum = getDescriptionBox().getBoxSizeFromBmffHeaders();

        for (BmffBox contentBox : getContentBoxList()) {
            sum += contentBox.getBoxSize();
        }

        sum += (getPaddingBox() != null) ? getPaddingBox().getBoxSize() : 0;
        return sum;
    }

    public long calculateContentBoxListSize(List<BmffBox> contentBoxList) {
        long sum = 0;

        for (BmffBox contentBox : contentBoxList) {
            sum += contentBox.getBoxSize();
        }

        return sum;
    }

    public DescriptionBox getDescriptionBox() {
        return this.descriptionBox;
    }

    public void setDescriptionBox(DescriptionBox descriptionBox) {
        this.descriptionBox = descriptionBox;
    }

    public List<BmffBox> getContentBoxList() {
        return this.contentBoxList;
    }

    public void setContentBoxList(List<BmffBox> contentBoxList) {
        this.contentBoxList = contentBoxList;
    }

    public PaddingBox getPaddingBox() {
        return this.paddingBox;
    }

    public void setPaddingBox(PaddingBox paddingBox) {
        this.paddingBox = paddingBox;
    }

    @Override
    public String toString() {
        final String descriptionBoxString = this.descriptionBox != null ? getDescriptionBox().toString() : "null";
        final String contentString = this.contentBoxList != null ? getContentBoxList().toString() : "null";
        final String paddingString = this.paddingBox != null ? getPaddingBox().toString() : "null";

        return "JumbfBox(" + super.toString() + ", descriptionBox=" + descriptionBoxString + ", content="
                + contentString + ", paddingBox=" + paddingString + ")";
    }

}