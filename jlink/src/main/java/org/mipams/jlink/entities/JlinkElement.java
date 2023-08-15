package org.mipams.jlink.entities;

import java.util.ArrayList;
import java.util.List;

public class JlinkElement {

    Integer nextId;

    Scene scene;

    List<Link> links = new ArrayList<>();

    public void setNextId(Integer nextId) {
        this.nextId = nextId;
    }

    public Integer getNextId() {
        return this.nextId;
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    public Scene getScene() {
        return scene;
    }

    public void addLink(Link link) {
        this.links.add(link);
    }

    public void setLinks(List<Link> links) {
        this.links.clear();
        this.links.addAll(links);
    }

    public List<Link> getLinks() {
        return links;
    }
}
