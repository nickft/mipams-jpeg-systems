package org.mipams.jumbf.core.entities;

import org.mipams.jumbf.core.util.MipamsException;

public interface BoxInterface {

    public long calculateSizeFromBox() throws MipamsException;

    public int getTypeId();
}