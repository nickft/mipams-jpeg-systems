package org.mipams.jumbf.core.entities;

import org.mipams.jumbf.core.util.MipamsException;

public interface BoxInterface {

    public long getBoxSize() throws MipamsException;

    public int getTypeId();
}