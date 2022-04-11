package org.mipams.jumbf.core.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.mipams.jumbf.core.util.BoxTypeEnum;
import org.mipams.jumbf.core.util.MipamsException;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@ToString
public class EmbeddedFileBox implements ContentBox {

    private @Getter @Setter EmbeddedFileDescriptionBox descriptionBox;

    private @Getter @Setter BinaryDataBox binaryDataBox;

    @Override
    public int getTypeId() {
        return BoxTypeEnum.EmbeddedFileBox.getTypeId();
    }

    @Override
    public UUID getContentTypeUUID() {
        return BoxTypeEnum.EmbeddedFileBox.getContentUuid();
    }

    @Override
    public long getBoxSize() throws MipamsException {
        long sum = getDescriptionBox().getBoxSize();

        sum += getBinaryDataBox().getBoxSize();

        return sum;
    }

    @Override
    public List<XtBox> getXtBoxes() {
        List<XtBox> result = new ArrayList<>();

        result.add(getDescriptionBox());
        result.add(getBinaryDataBox());

        return result;
    }

    public String getFileName() {
        return descriptionBox.getFileName();
    }
}