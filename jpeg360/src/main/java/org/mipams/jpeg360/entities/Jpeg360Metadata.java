package org.mipams.jpeg360.entities;

import java.util.ArrayList;
import java.util.List;

public class Jpeg360Metadata {

    Integer nextId;
    Jpeg360ImageMetadata imageMetadata;
    List<Jpeg360Viewport> viewports = new ArrayList<>();
    List<Jpeg360AcceleratedRoi> acceleratedROIs = new ArrayList<>();

    public void setNextId(Integer nextId) {
        this.nextId = nextId;
    }

    public Integer getNextId() {
        return nextId;
    }

    public void setImageMetadata(Jpeg360ImageMetadata imageMetadata) {
        this.imageMetadata = imageMetadata;
    }

    public Jpeg360ImageMetadata getImageMetadata() {
        return imageMetadata;
    }

    public void addViewport(Jpeg360Viewport viewport) {
        this.viewports.add(viewport);
    }

    public void setViewports(List<Jpeg360Viewport> viewports) {
        this.viewports.clear();
        this.viewports.addAll(viewports);
    }

    public List<Jpeg360Viewport> getViewports() {
        return viewports;
    }

    public void addAcceleratedRois(Jpeg360AcceleratedRoi jpeg360AcceleratedROI) {
        this.acceleratedROIs.add(jpeg360AcceleratedROI);
    }

    public void setAcceleratedRois(List<Jpeg360AcceleratedRoi> acceleratedROIs) {
        this.acceleratedROIs.clear();
        this.acceleratedROIs.addAll(acceleratedROIs);
    }

    public List<Jpeg360AcceleratedRoi> getAcceleratedROIs() {
        return acceleratedROIs;
    }
}
