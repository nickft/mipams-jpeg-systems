package org.mipams.jlink.entities;

public class Viewport {
    double x = 50.0;

    double y = 50.0;

    double xfov = 100.0;

    double yfov = 100.0;

    Integer id;

    public void setX(double x) {
        this.x = x;
    }

    public double getX() {
        return x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getY() {
        return y;
    }

    public void setXfov(double xfov) {
        this.xfov = xfov;
    }

    public double getXfov() {
        return xfov;
    }

    public void setYfov(double yfov) {
        this.yfov = yfov;
    }

    public double getYfov() {
        return yfov;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    @Override
    public String toString() {
        return String.format("Scene: %d", getId());
    }
}
