package org.mipams.jlink.entities;

public class JlinkLink {
    JlinkRegion region;

    int duration = 600;

    int vpid = 0;

    String sprite;

    String to;

    public void setRegion(JlinkRegion region) {
        this.region = region;
    }

    public JlinkRegion getRegion() {
        return region;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getDuration() {
        return duration;
    }

    public void setVpid(int vpid) {
        this.vpid = vpid;
    }

    public int getVpid() {
        return vpid;
    }

    public void setSprite(String sprite) {
        this.sprite = sprite;
    }

    public String getSprite() {
        return sprite;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getTo() {
        return to;
    }

    @Override
    public String toString() {
        return String.format("Link to : %s", getTo());
    }
}
