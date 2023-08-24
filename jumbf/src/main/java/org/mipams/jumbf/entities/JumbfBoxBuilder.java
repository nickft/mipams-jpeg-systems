package org.mipams.jumbf.entities;

import java.util.ArrayList;
import java.util.List;

import org.mipams.jumbf.services.content_types.ContentTypeService;
import org.mipams.jumbf.util.MipamsException;

public class JumbfBoxBuilder {
    private JumbfBox jumbfBox;

    public JumbfBoxBuilder(ContentTypeService contentTypeService) {
        reset();
        setContentType(contentTypeService);
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

    public JumbfBoxBuilder setJumbfBoxAsRequestable() {
        this.jumbfBox.getDescriptionBox().setAsRequestable();
        return this;
    }

    private JumbfBoxBuilder setContentType(ContentTypeService contentTypeService) {
        this.jumbfBox.getDescriptionBox().setUuid(contentTypeService.getContentTypeUuid());
        return this;
    }

    public JumbfBoxBuilder setLabel(String label) {
        this.jumbfBox.getDescriptionBox().setLabel(label);
        return this;
    }

    public JumbfBoxBuilder setId(int id) {
        this.jumbfBox.getDescriptionBox().setId(id);
        return this;
    }

    public JumbfBoxBuilder setSha256Hash(byte[] digest) {
        this.jumbfBox.getDescriptionBox().setSha256Hash(digest);
        return this;
    }

    public JumbfBoxBuilder setPrivateField(BmffBox privateField) {
        this.jumbfBox.getDescriptionBox().setPrivateField(privateField);
        return this;
    }

    public JumbfBoxBuilder setPaddingSize(long numberOfBytes) throws MipamsException {
        PaddingBox paddingBox = new PaddingBox();
        paddingBox.setPaddingSize(numberOfBytes);
        paddingBox.updateFieldsBasedOnExistingData();
        this.jumbfBox.setPaddingBox(paddingBox);
        return this;
    }

    public JumbfBoxBuilder appendContentBox(BmffBox box) {
        this.jumbfBox.getContentBoxList().add(box);
        return this;
    }

    public JumbfBoxBuilder appendAllContentBoxes(List<? extends BmffBox> boxList) {
        this.jumbfBox.getContentBoxList().addAll(boxList);
        return this;
    }

    public JumbfBoxBuilder removeContentBox(BmffBox box) {
        this.jumbfBox.getContentBoxList().remove(box);
        return this;
    }

    public JumbfBoxBuilder emptyContentBoxList() {
        this.jumbfBox.setContentBoxList(new ArrayList<>());
        return this;
    }

    public JumbfBox getResult() throws MipamsException {
        this.jumbfBox.getDescriptionBox().updateFieldsBasedOnExistingData();

        for (BmffBox contentBox : this.jumbfBox.getContentBoxList()) {
            contentBox.updateFieldsBasedOnExistingData();
        }

        this.jumbfBox.updateFieldsBasedOnExistingData();

        return this.jumbfBox;
    }
}
