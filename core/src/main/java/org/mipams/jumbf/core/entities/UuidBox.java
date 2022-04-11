package org.mipams.jumbf.core.entities;

import java.util.List;
import java.util.UUID;

import org.mipams.jumbf.core.util.BoxTypeEnum;
import org.mipams.jumbf.core.util.CoreUtils;
import org.mipams.jumbf.core.util.MipamsException;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@ToString
public class UuidBox extends XtBox implements ContentBox {

    private @Getter @Setter UUID uuid;
    private @Getter @Setter String fileUrl;

    @Override
    public long calculatePayloadSize() throws MipamsException {

        long sum = CoreUtils.UUID_BYTE_SIZE;
        sum += CoreUtils.getFileSizeFromPath(getFileUrl());

        return sum;
    }

    @Override
    public int getTypeId() {
        return BoxTypeEnum.UuidBox.getTypeId();
    }

    @Override
    public List<XtBox> getXtBoxes() {
        return List.of(this);
    }

    @Override
    public UUID getContentTypeUUID() {
        return BoxTypeEnum.UuidBox.getContentUuid();
    }

}
