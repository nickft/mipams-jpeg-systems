package org.mipams.jumbf.core.entities;

import java.util.ArrayList;
import java.util.List;

import org.mipams.jumbf.core.util.MipamsException;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class EmbeddedFileBox implements ContentBox {

    private @Getter @Setter EmbeddedFileDescriptionBox descriptionBox;

    private @Getter @Setter BinaryDataBox binaryDataBox;

    @Override
    public int getTypeId() {
        return 0x6266264;
    }

    @Override
    public String getType() {
        return "bfbd";
    }

    @Override
    public String getContentTypeUUID() {
        return "40CB0C32-BB8A-489D-A70B-2AD6F47F4369";
    }

    @Override
    public long getBoxSize() throws MipamsException {
        long sum = getDescriptionBox().getBoxSize();

        sum += getBinaryDataBox().getBoxSize();

        return sum;
    }

    @Override
    public List<BmffBox> getBmffBoxes() {
        List<BmffBox> result = new ArrayList<>();

        result.add(getDescriptionBox());
        result.add(getBinaryDataBox());

        return result;
    }

    public String getFileName() {
        return descriptionBox.getFileName();
    }
}