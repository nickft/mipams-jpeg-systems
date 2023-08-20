package org.mipams.jlink.entities;

import java.util.ArrayList;
import java.util.List;

public class JlinkElement {

    Integer nextId;

    JlinkScene scene;

    List<JlinkLink> links = new ArrayList<>();

    public void setNextId(Integer nextId) {
        this.nextId = nextId;
    }

    public Integer getNextId() {
        return this.nextId;
    }

    public void setScene(JlinkScene scene) {
        this.scene = scene;
    }

    public JlinkScene getScene() {
        return scene;
    }

    public void addLink(JlinkLink link) {
        this.links.add(link);
    }

    public void setLinks(List<JlinkLink> links) {
        this.links.clear();
        this.links.addAll(links);
    }

    public List<JlinkLink> getLinks() {
        return links;
    }
}
