package org.mipams.jumbf.demo.dto;

import java.util.List;

import org.mipams.jumbf.core.entities.JumbfBox;

public class JumbfResponse {

    String fileName;

    List<JumbfBox> jumbfBoxList;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public List<JumbfBox> getJumbfBoxList() {
        return jumbfBoxList;
    }

    public void setJumbfBoxList(List<JumbfBox> jumbfBoxList) {
        this.jumbfBoxList = jumbfBoxList;
    }
}
