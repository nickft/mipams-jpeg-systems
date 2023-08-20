package org.mipams.jlink.entities;

public class JlinkRegion {
    String shape = "rectangle";

    Double x;

    Double y;

    Double w;

    Double h;

    Double rotation;

    public void setShape(String shape) {
        this.shape = shape;
    }

    public String getShape() {
        return shape;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Double getX() {
        return x;
    }

    public void setY(Double y) {
        this.y = y;
    }

    public Double getY() {
        return y;
    }

    public void setW(Double w) {
        this.w = w;
    }

    public Double getW() {
        return w;
    }

    public void setH(Double h) {
        this.h = h;
    }

    public Double getH() {
        return h;
    }

    public void setRotation(Double rotation) {
        this.rotation = rotation;
    }

    public Double getRotation() {
        return rotation;
    }

    @Override
    public String toString() {
        return String.format("Link shape : %s", getShape());
    }
}
