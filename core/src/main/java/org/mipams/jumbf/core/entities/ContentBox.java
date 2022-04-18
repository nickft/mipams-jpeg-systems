package org.mipams.jumbf.core.entities;

import java.util.List;

public interface ContentBox extends BoxInterface {

    public String getContentTypeUUID();

    public List<BmffBox> getBmffBoxes();
}