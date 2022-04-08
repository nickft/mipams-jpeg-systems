package org.mipams.jumbf.core.entities;

import org.mipams.jumbf.core.util.BoxTypeEnum;
import org.mipams.jumbf.core.util.MipamsException;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@ToString
public class EmbeddedFileBox extends XTBox {

    private @Getter @Setter EmbeddedFileDescriptionBox descriptionBox;

    private @Getter @Setter BinaryDataBox binaryDataBox;

    @Override
    public int getTypeId() {
        return BoxTypeEnum.EmbeddedFileBox.getTypeId();
    }

    @Override
    public long calculatePayloadSize() throws MipamsException {

        long sum = getDescriptionBox().getBoxSizeFromXTBoxHeaders();

        sum += getBinaryDataBox().getBoxSizeFromXTBoxHeaders();

        return sum;
    }

    public String getFileName() {
        return descriptionBox.getFileName();
    }
}