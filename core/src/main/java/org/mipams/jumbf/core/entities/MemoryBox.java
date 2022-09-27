package org.mipams.jumbf.core.entities;

import org.mipams.jumbf.core.util.MipamsException;

public abstract class MemoryBox extends BmffBox {
    protected byte[] content;

    @Override
    protected long calculatePayloadSize() throws MipamsException {
        return content != null ? content.length : 0;
    }

    public byte[] getContent() {
        return this.content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    @Override
    public String toString() {

        final String content = this.content != null ? getContent().toString() : "null";
        return "MemoryBox(" + super.toString() + ", content=" + content + ")";
    }
}
