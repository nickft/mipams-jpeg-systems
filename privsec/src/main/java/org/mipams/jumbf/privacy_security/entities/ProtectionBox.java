package org.mipams.jumbf.privacy_security.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.mipams.jumbf.core.entities.BinaryDataBox;
import org.mipams.jumbf.core.entities.ContentBox;
import org.mipams.jumbf.core.entities.BmffBox;
import org.mipams.jumbf.core.util.MipamsException;
import org.mipams.jumbf.privacy_security.util.BoxTypeEnum;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@ToString(callSuper = true)
public class ProtectionBox implements ContentBox {

    private @Getter @Setter ProtectionDescriptionBox protectionDescriptionBox;

    private @Getter @Setter BinaryDataBox binaryDataBox;

    @Override
    public int getTypeId() {
        return BoxTypeEnum.ProtectionBox.getTypeId();
    }

    @Override
    public long getBoxSize() throws MipamsException {
        long sum = getProtectionDescriptionBox().getBoxSizeFromBmffHeaders();

        sum += getBinaryDataBox().getBoxSizeFromBmffHeaders();

        return sum;
    }

    @Override
    public UUID getContentTypeUUID() {
        return BoxTypeEnum.ProtectionBox.getContentUuid();
    }

    @Override
    public List<BmffBox> getBmffBoxes() {
        List<BmffBox> result = new ArrayList<>();

        result.add(getProtectionDescriptionBox());
        result.add(getBinaryDataBox());

        return result;
    }
}
