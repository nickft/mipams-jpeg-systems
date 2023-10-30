package org.mipams.jpeg360.entities;

public class Jpeg360ImageMetadata {

    Integer id;
    Integer version = 1;
    String mediaType = "Image/jpg";
    String projectionType = "Equirectangular";
    String stereoscopicFormat;
    Double phiMin = -180.0;
    Double phiMax = 180.0;;
    Double thetaMin = -90.0;
    Double thetaMax = 90.0;
    Double phiGravity = 270.0;
    Double thetaGravity = -90.0;
    Double compassPhi = 0.0;
    Double compassTheta = 0.0;
    String boxReference = "conventional";

    public void setUmfId(Integer id) {
        this.id = id;
    }

    public Integer getUmdId() {
        return id;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Integer getVersion() {
        return version;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setProjectionType(String projectionType) {
        this.projectionType = projectionType;
    }

    public String getStereoscopicFormat() {
        return stereoscopicFormat;
    }

    public void setStereoscopicFormat(String stereoscopicFormat) {
        this.stereoscopicFormat = stereoscopicFormat;
    }

    public String getProjectionType() {
        return projectionType;
    }

    public void setPhiMin(Double phiMin) {
        this.phiMin = phiMin;
    }

    public Double getPhiMin() {
        return phiMin;
    }

    public void setPhiMax(Double phiMax) {
        this.phiMax = phiMax;
    }

    public Double getPhiMax() {
        return phiMax;
    }

    public void setThetaMin(Double thetaMin) {
        this.thetaMin = thetaMin;
    }

    public Double getThetaMin() {
        return thetaMin;
    }

    public void setThetaMax(Double thetaMax) {
        this.thetaMax = thetaMax;
    }

    public Double getThetaMax() {
        return thetaMax;
    }

    public void setPhiGravity(Double phiGravity) {
        this.phiGravity = phiGravity;
    }

    public Double getPhiGravity() {
        return phiGravity;
    }

    public void setThetaGravity(Double thetaGravity) {
        this.thetaGravity = thetaGravity;
    }

    public Double getThetaGravity() {
        return thetaGravity;
    }

    public void setCompassPhi(Double compassPhi) {
        this.compassPhi = compassPhi;
    }

    public Double getCompassPhi() {
        return compassPhi;
    }

    public void setCompassTheta(Double compassTheta) {
        this.compassTheta = compassTheta;
    }

    public Double getCompassTheta() {
        return compassTheta;
    }

    public void setBoxReference(String boxReference) {
        this.boxReference = boxReference;
    }

    public String getBoxReference() {
        return boxReference;
    }

}
