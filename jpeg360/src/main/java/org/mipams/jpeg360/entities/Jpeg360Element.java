package org.mipams.jpeg360.entities;

public class Jpeg360Element {

    Integer nextId;

    Jpeg360Metadata jpeg360Metadata;

    public void setNextId(Integer nextId) {
        this.nextId = nextId;
    }

    public Jpeg360Metadata getJpeg360Metadata() {
        return this.jpeg360Metadata;
    }

    public void setJpeg360Metadata(Jpeg360Metadata jpeg360Metadata) {
        this.jpeg360Metadata = jpeg360Metadata;
    }

    public Integer getNextId() {
        return this.nextId;
    }

}
