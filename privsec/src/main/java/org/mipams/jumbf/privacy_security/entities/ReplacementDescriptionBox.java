package org.mipams.jumbf.privacy_security.entities;

import org.mipams.jumbf.core.entities.BmffBox;
import org.mipams.jumbf.core.util.CoreUtils;
import org.mipams.jumbf.core.util.MipamsException;
import org.mipams.jumbf.privacy_security.entities.replacement.ParamHandlerInterface;
import org.mipams.jumbf.privacy_security.util.BoxTypeEnum;

import lombok.Getter;
import lombok.Setter;

public class ReplacementDescriptionBox extends BmffBox {

    protected @Getter @Setter int replacementTypeId;
    protected @Getter @Setter int toggle;
    protected @Getter @Setter ParamHandlerInterface paramHandler;

    @Override
    public int getTypeId() {
        return BoxTypeEnum.ReplacementDescriptionBox.getTypeId();
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
}
