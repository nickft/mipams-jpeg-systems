package org.mipams.privsec.entities;

import org.mipams.jumbf.entities.BmffBox;
import org.mipams.jumbf.util.CoreUtils;
import org.mipams.jumbf.util.MipamsException;
import org.mipams.privsec.entities.replacement.ParamHandlerInterface;

public class ReplacementDescriptionBox extends BmffBox {

    protected int replacementTypeId;
    protected int toggle;
    protected ParamHandlerInterface paramHandler;

    public static int TYPE_ID = 0x70737264;
    public static String TYPE = "psrd";

    @Override
    public int getTypeId() {
        return TYPE_ID;
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    protected final long calculatePayloadSize() throws MipamsException {
        long sum = getReplacementTypeIdSize() + getToggleSize() + paramHandler.getParamSize();
        return sum;
    }

    public int getReplacementTypeIdSize() {
        return 1;
    }

    public int getToggleSize() {
        return 1;
    }

    public void setAutoApply(boolean isAutoApply) {
        int val = (isAutoApply) ? 0 : 1;
        int updatedToggle = CoreUtils.setBitValueAtGivenPosition(getToggle(), 0, val);

        setToggle(updatedToggle);
    }

    public int getReplacementTypeId() {
        return this.replacementTypeId;
    }

    public void setReplacementTypeId(int replacementTypeId) {
        this.replacementTypeId = replacementTypeId;
    }

    public int getToggle() {
        return this.toggle;
    }

    public void setToggle(int toggle) {
        this.toggle = toggle;
    }

    public ParamHandlerInterface getParamHandler() {
        return this.paramHandler;
    }

    public void setParamHandler(ParamHandlerInterface paramHandler) {
        this.paramHandler = paramHandler;
    }

    @Override
    public String toString() {

        final String paramHandlerInterface = this.paramHandler != null ? getParamHandler().toString() : "null";

        return "ReplacementDescriptionBox(replacementTypeId=" + getReplacementTypeId() + ", toggle=" + getToggle()
                + ", paramHandler=" + paramHandlerInterface + ")";
    }
}
