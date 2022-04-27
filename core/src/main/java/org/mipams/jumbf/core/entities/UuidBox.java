package org.mipams.jumbf.core.entities;

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
public class UuidBox extends BmffBox {

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
    public long calculatePayloadSize() throws MipamsException {

        long sum = CoreUtils.UUID_BYTE_SIZE;
        sum += CoreUtils.getFileSizeFromPath(getFileUrl());

        return sum;
    }

}
