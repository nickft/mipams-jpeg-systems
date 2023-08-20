package org.mipams.jlink.entities;

public class JlinkImage {
    String format;

    String href;

    public void setFormat(String format) {
        this.format = format;
    }

    public String getFormat() {
        return format;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getHref() {
        return href;
    }

    @Override
    public String toString() {
        return String.format("Scene: %s", getHref());
    }
}
