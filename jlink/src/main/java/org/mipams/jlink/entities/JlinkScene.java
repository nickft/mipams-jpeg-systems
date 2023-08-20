package org.mipams.jlink.entities;

import java.util.ArrayList;
import java.util.List;

public class JlinkScene {

    String version = "1.0.0";

    String title = "";

    String note = "";

    JlinkImage image;

    List<JlinkViewport> viewports = new ArrayList<>();

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

    public void setImage(JlinkImage image) {
        this.image = image;
    }

    public JlinkImage getImage() {
        return image;
    }

    public void setViewports(List<JlinkViewport> viewport) {
        this.viewports.clear();
        this.viewports.addAll(viewport);
    }

    public void addViewport(JlinkViewport viewport) {
        this.viewports.add(viewport);
    }

    public List<JlinkViewport> getViewports() {
        return viewports;
    }

    @Override
    public String toString() {
        return String.format("Scene: %s", getTitle());
    }
}
