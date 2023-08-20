package org.mipams.jpeg360.entities;

public class Jpeg360Viewport {

    Integer id;
    Integer number = 0;
    Double viewportPhi = 0.0;
    Double viewportTheta = 0.0;
    Double viewportPhiFOV = 100.0;
    Double viewportThetaFOV = 75.0;
    Double viewportRoll = 0.0;

    public void setUmfId(Integer id) {
        this.id = id;
    }

    public Integer getUmdId() {
        return id;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Integer getNumber() {
        return number;
    }

    public void setViewportPhi(Double viewportPhi) {
        this.viewportPhi = viewportPhi;
    }

    public Double getViewportPhi() {
        return viewportPhi;
    }

    public void setViewportTheta(Double viewportTheta) {
        this.viewportTheta = viewportTheta;
    }

    public Double getViewportTheta() {
        return viewportTheta;
    }

    public void setViewportPhiFOV(Double viewportPhiFOV) {
        this.viewportPhiFOV = viewportPhiFOV;
    }

    public Double getViewportPhiFOV() {
        return viewportPhiFOV;
    }

    public void setViewportThetaFOV(Double viewportThetaFOV) {
        this.viewportThetaFOV = viewportThetaFOV;
    }

    public Double getViewportThetaFOV() {
        return viewportThetaFOV;
    }

    public void setViewportRoll(Double viewportRoll) {
        this.viewportRoll = viewportRoll;
    }

    public Double getViewportRoll() {
        return viewportRoll;
    }

}
