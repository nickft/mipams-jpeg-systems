package org.mipams.jumbf.core.entities;

import java.util.List;
import java.util.UUID;

public interface ContentBox extends BoxInterface {
    public List<XtBox> getXtBoxes();

    public UUID getContentTypeUUID();
}