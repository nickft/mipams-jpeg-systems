package org.mipams.jumbf.entities;

import org.mipams.jumbf.util.CoreUtils;
import org.mipams.jumbf.util.MipamsException;

public class UuidBox extends BmffBox {

    private String uuid;

    private String fileUrl;

    public static int TYPE_ID = 0x75756964;
    public static String TYPE = "uuid";

    @Override
    public int getTypeId() {
        return TYPE_ID;
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public long calculatePayloadSize() throws MipamsException {

        long sum = CoreUtils.UUID_BYTE_SIZE;
        sum += CoreUtils.getFileSizeFromPath(getFileUrl());

        return sum;
    }

    public String getUuid() {
        return this.uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getFileUrl() {
        return this.fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    @Override
    public String toString() {
        final String uuid = this.uuid != null ? getUuid() : "null";
        final String fileUrl = this.fileUrl != null ? getFileUrl() : "null";

        return "UuidBox(" + super.toString() + ", uuid=" + uuid + ", fileUrl=" + fileUrl + ")";
    }

}
