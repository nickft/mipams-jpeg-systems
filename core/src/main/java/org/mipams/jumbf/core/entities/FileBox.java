package org.mipams.jumbf.core.entities;

import org.mipams.jumbf.core.util.CoreUtils;
import org.mipams.jumbf.core.util.MipamsException;

public abstract class FileBox extends BmffBox {
    protected String fileUrl;

    @Override
    protected long calculatePayloadSize() throws MipamsException {
        return CoreUtils.getFileSizeFromPath(fileUrl);
    }

    public String getFileUrl() {
        return this.fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    @Override
    public String toString() {

        final String fileUrl = this.fileUrl != null ? getFileUrl() : "null";
        return "BinaryDataBox(" + super.toString() + ", referencedExternally=" + fileUrl + ")";
    }
}
