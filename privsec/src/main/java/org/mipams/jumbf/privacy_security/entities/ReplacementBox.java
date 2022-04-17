package org.mipams.jumbf.privacy_security.entities;

import java.util.List;
import java.util.UUID;

import org.mipams.jumbf.core.entities.ContentBox;
import org.mipams.jumbf.core.entities.BmffBox;
import org.mipams.jumbf.core.util.MipamsException;
import org.mipams.jumbf.privacy_security.entities.replacement.ReplacementType;
import org.mipams.jumbf.privacy_security.util.BoxTypeEnum;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@ToString
public final class ReplacementBox implements ContentBox {

    protected @Getter @Setter ReplacementDescriptionBox descriptionBox;
    protected @Getter @Setter List<BmffBox> replacementDataBoxList;

    @Override
    public int getTypeId() {
        return BoxTypeEnum.ReplacementBox.getTypeId();
    }

    @Override
    public UUID getContentTypeUUID() {
        return BoxTypeEnum.ReplacementBox.getContentUuid();
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
