package org.mipams.jumbf.privacy_security.entities;

import java.util.ArrayList;
import java.util.List;

import org.mipams.jumbf.core.entities.BinaryDataBox;
import org.mipams.jumbf.core.entities.ContentBox;
import org.mipams.jumbf.core.entities.BmffBox;
import org.mipams.jumbf.core.util.MipamsException;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class ProtectionBox implements ContentBox {

    private @Getter @Setter ProtectionDescriptionBox protectionDescriptionBox;

    private @Getter @Setter BinaryDataBox binaryDataBox;

    @Override
    public int getTypeId() {
        return 0x74B11BBF;
    }

    @Override
    public String getType() {
        return "prtc";
    }

    @Override
    public String getContentTypeUUID() {
        return "74B11BBF-F21D-4EEA-98C1-0BEBF23AEFD3";
    }

    @Override
    public long getBoxSize() throws MipamsException {
        long sum = getProtectionDescriptionBox().getBoxSizeFromBmffHeaders();

        sum += getBinaryDataBox().getBoxSizeFromBmffHeaders();

        return sum;
    }

    @Override
    public List<BmffBox> getBmffBoxes() {
        List<BmffBox> result = new ArrayList<>();

        result.add(getProtectionDescriptionBox());
        result.add(getBinaryDataBox());

        return result;
    }
}
