package org.mipams.jumbf.core.entities;

import java.util.List;

import org.mipams.jumbf.core.util.CoreUtils;
import org.mipams.jumbf.core.util.MipamsException;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = false)
public class UuidBox extends BmffBox implements ContentBox {

    private @Getter @Setter String uuid;

    @EqualsAndHashCode.Exclude
    private @Getter @Setter String fileUrl;

    @Override
    public int getTypeId() {
        return 0x75756964;
    }

    @Override
    public String getType() {
        return "uuid";
    }

    @Override
    public String getContentTypeUUID() {
        return "75756964-0011-0010-8000-00AA00389B71";
    }

    @Override
    public long calculatePayloadSize() throws MipamsException {

        long sum = CoreUtils.UUID_BYTE_SIZE;
        sum += CoreUtils.getFileSizeFromPath(getFileUrl());

        return sum;
    }

    @Override
    public List<BmffBox> getBmffBoxes() {
        return List.of(this);
    }

}
