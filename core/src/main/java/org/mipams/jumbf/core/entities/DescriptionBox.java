package org.mipams.jumbf.core.entities;

import org.mipams.jumbf.core.util.CoreUtils;
import org.mipams.jumbf.core.util.MipamsException;

public class DescriptionBox extends BmffBox {

    protected String uuid;

    protected int toggle;

    protected String label;

    protected Integer id;

    protected byte[] sha256Hash;

    protected BmffBox privateField;

    @Override
    public int getTypeId() {
        return 0x6A756D64;
    }

    @Override
    public String getType() {
        return "jumd";
    }

    @Override
    public long calculatePayloadSize() throws MipamsException {
        long sum = getUuidSize();

        sum += getToggleSize();

        if (labelExists()) {
            sum += getLabelSize();
        }

        if (idExists()) {
            sum += getIdSize();
        }

        if (sha256HashExists()) {
            sum += getSignatureSize();
        }

        if (privateFieldExists()) {
            sum += getPrivateFieldSize();
        }

        return sum;
    }

    int getUuidSize() {
        return CoreUtils.UUID_BYTE_SIZE;
    }

    int getToggleSize() {
        return 1;
    }

    long getLabelSize() {
        return CoreUtils.addEscapeCharacterToText(getLabel()).length();
    }

    int getIdSize() {
        return CoreUtils.INT_BYTE_SIZE;
    }

    int getSignatureSize() {
        return 32;
    }

    long getPrivateFieldSize() throws MipamsException {
        return getPrivateField().calculateSizeFromBox();
    }

    public void setAsRequestable() {
        CoreUtils.setBitValueAtGivenPosition(toggle, 0, 1);
    }

    public boolean isRequestable() {
        return CoreUtils.isBitAtGivenPositionSet(toggle, 0);
    }

    public boolean labelExists() {
        return CoreUtils.isBitAtGivenPositionSet(toggle, 1);
    }

    public boolean idExists() {
        return CoreUtils.isBitAtGivenPositionSet(toggle, 2);
    }

    public boolean sha256HashExists() {
        return CoreUtils.isBitAtGivenPositionSet(toggle, 3);
    }

    public boolean privateFieldExists() {
        return CoreUtils.isBitAtGivenPositionSet(toggle, 4);
    }

    public void computeAndSetToggleBasedOnFields() {

        int toggle = 0;

        if (getLabel() != null) {
            toggle = 3;
        }

        if (getId() != null) {
            toggle = toggle | 4;
        }

        if (getSha256Hash() != null) {
            toggle = toggle | 8;
        }

        if (getPrivateField() != null) {
            toggle = toggle | 16;
        }

        setToggle(toggle);
    }

    public String getUuid() {
        return this.uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getToggle() {
        return this.toggle;
    }

    public void setToggle(int toggle) {
        this.toggle = toggle;
    }

    public String getLabel() {
        return this.label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public byte[] getSha256Hash() {
        return this.sha256Hash;
    }

    public void setSha256Hash(byte[] sha256Hash) {
        this.sha256Hash = sha256Hash;
    }

    public BmffBox getPrivateField() {
        return this.privateField;
    }

    public void setPrivateField(BmffBox privateField) {
        this.privateField = privateField;
    }

    @Override
    public String toString() {
        final String uuid = this.uuid != null ? getUuid() : "null";
        final String label = this.label != null ? getLabel() : "null";
        final String id = this.id != null ? getId().toString() : "null";
        final String sha256Hash = this.sha256Hash != null ? getSha256Hash().toString() : "null";
        final String privateField = this.privateField != null ? getPrivateField().toString() : "null";

        StringBuilder builder = new StringBuilder("DescriptionBox(");

        builder.append(super.toString()).append(", ").append("uuid=").append(uuid).append(", ").append("toggle=")
                .append(toggle).append(", ").append("label=").append(label).append(", ").append("id=").append(id)
                .append(", ").append("sha256Hash=").append(sha256Hash).append(", ").append("privateField=")
                .append(privateField.toString()).append(")");

        return builder.toString();
    }
}