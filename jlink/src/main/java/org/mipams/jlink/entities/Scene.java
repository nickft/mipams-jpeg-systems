package org.mipams.jlink.entities;

import java.util.ArrayList;
import java.util.List;

public class Scene {

    String version = "1.0.0";

    String title = "";

    String note = "";

    Image image;

    List<Viewport> viewports = new ArrayList<>();

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getNote() {
        return note;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public Image getImage() {
        return image;
    }

    public void setViewports(List<Viewport> viewport) {
        this.viewports.clear();
        this.viewports.addAll(viewport);
    }

    public void addViewport(Viewport viewport) {
        this.viewports.add(viewport);
    }

    public List<Viewport> getViewports() {
        return viewports;
    }

    @Override
    public String toString() {
        return String.format("Scene: %s", getTitle());
    }
}
