package org.mipams.jumbf.entities;

import org.mipams.jumbf.util.MipamsException;

public interface BoxInterface {

    public long getBoxSize() throws MipamsException;

    public int getTypeId();

    public String getType();
}