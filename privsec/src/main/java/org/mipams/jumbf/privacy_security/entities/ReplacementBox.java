package org.mipams.jumbf.privacy_security.entities;

import java.util.List;

import org.mipams.jumbf.core.entities.ContentBox;
import org.mipams.jumbf.core.entities.BmffBox;
import org.mipams.jumbf.core.util.MipamsException;
import org.mipams.jumbf.privacy_security.entities.replacement.ReplacementType;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@ToString
@EqualsAndHashCode
public final class ReplacementBox implements ContentBox {

    protected @Getter @Setter ReplacementDescriptionBox descriptionBox;
    protected @Getter @Setter List<BmffBox> replacementDataBoxList;

    @Override
    public int getTypeId() {
        return 0xDC28B95F;
    }

    @Override
    public String getType() {
        return "rplc";
    }

    @Override
    public String getContentTypeUUID() {
        return "DC28B95F-B68A-498E-8064-0FCA845D6B0E";
    }

    @Override
    public long getBoxSize() throws MipamsException {

        long sum = getDescriptionBox().getBoxSize();

        for (BmffBox contentBox : getReplacementDataBoxList()) {
            sum += contentBox.getBoxSize();
        }

        return sum;
    }

    @Override
    public List<BmffBox> getBmffBoxes() {
        return getReplacementDataBoxList();
    }

    public ReplacementType getReplacementType() throws MipamsException {
        return ReplacementType.getTypeFromId(getDescriptionBox().getReplacementTypeId());
    }
}
