package org.mipams.jumbf.core.entities;

import java.util.ArrayList;
import java.util.List;

import org.mipams.jumbf.core.services.content_types.ContentTypeService;
import org.mipams.jumbf.core.util.MipamsException;

public class JumbfBoxBuilder {
    private JumbfBox jumbfBox;

    public JumbfBoxBuilder() {
        reset();
    }

    public JumbfBoxBuilder(JumbfBox jumbfBox) {
        this.jumbfBox = jumbfBox;
    }

    void reset() {
        DescriptionBox dBox = new DescriptionBox();
        List<BmffBox> contentBoxList = new ArrayList<>();

        JumbfBox jumbfBox = new JumbfBox();

        jumbfBox.setDescriptionBox(dBox);
        jumbfBox.setContentBoxList(contentBoxList);

        this.jumbfBox = jumbfBox;
    }

    public void setJumbfBoxAsRequestable() {
        this.jumbfBox.getDescriptionBox().setAsRequestable();
    }

    public void setContentType(ContentTypeService contentTypeService) {
        this.jumbfBox.getDescriptionBox().setUuid(contentTypeService.getContentTypeUuid());
    }

    public void setLabel(String label) {
        this.jumbfBox.getDescriptionBox().setLabel(label);
    }

    public void setId(int id) {
        this.jumbfBox.getDescriptionBox().setId(id);
    }

    public void setSha256Hash(byte[] digest) {
        this.jumbfBox.getDescriptionBox().setSha256Hash(digest);
    }

    public void setPrivateField(String privateFieldUrl) {
        this.jumbfBox.getDescriptionBox().setPrivateBmffBoxUrl(privateFieldUrl);
    }

    public void setPaddingSize(long numberOfBytes) throws MipamsException {
        PaddingBox paddingBox = new PaddingBox();
        paddingBox.setPaddingSize(numberOfBytes);
        paddingBox.updateBmffHeadersBasedOnBox();
        this.jumbfBox.setPaddingBox(paddingBox);
    }

    public void appendContentBox(BmffBox box) {
        this.jumbfBox.getContentBoxList().add(box);
    }

    public void appendAllContentBoxes(List<BmffBox> boxList) {
        this.jumbfBox.getContentBoxList().addAll(boxList);
    }

    public JumbfBox getResult() throws MipamsException {

        this.jumbfBox.getDescriptionBox().computeAndSetToggleBasedOnFields();
        this.jumbfBox.getDescriptionBox().updateBmffHeadersBasedOnBox();

        for (BmffBox contentBox : this.jumbfBox.getContentBoxList()) {
            contentBox.updateBmffHeadersBasedOnBox();
        }

        this.jumbfBox.updateBmffHeadersBasedOnBox();

        return this.jumbfBox;
    }
}
