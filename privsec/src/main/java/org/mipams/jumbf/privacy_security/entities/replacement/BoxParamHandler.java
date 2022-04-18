package org.mipams.jumbf.privacy_security.entities.replacement;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.mipams.jumbf.core.util.CoreUtils;
import org.mipams.jumbf.core.util.MipamsException;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@ToString
public class BoxParamHandler implements ParamHandlerInterface {

    private @Getter @Setter Long offset;
    private @Getter @Setter String label;

    @Override
    public void populateParamFromRequest(ObjectNode input) throws MipamsException {
        JsonNode offsetNode = input.get("offset");

        if (offsetNode != null) {
            setOffset(offsetNode.asLong());
        }

        JsonNode labelNode = input.get("label");

        if (labelNode != null) {
            setOffset(getMaxLongValue());
            setLabel(labelNode.asText());
        }
    }

    @Override
    public void writeParamToBytes(OutputStream outputStream) throws MipamsException {
        try {
            if (offsetExists()) {
                outputStream.write(CoreUtils.convertLongToByteArray(getOffset()));
            }

            if (labelExists()) {
                outputStream.write(CoreUtils.convertStringToByteArray(getLabelWithEscapeCharacter()));
            }
        } catch (IOException e) {
            throw new MipamsException("Could not write to file.", e);
        }
    }

    @Override
    public void populateParamFromBytes(InputStream inputStream) throws MipamsException {
        try {
            long offset = CoreUtils.readLongFromInputStream(inputStream);
            setOffset(offset);

            if (labelExists()) {
                String label = CoreUtils.readStringFromInputStream(inputStream);
                setLabel(label);
            }
        } catch (IOException e) {
            throw new MipamsException("Failed to read parameter for box replacement type");
        }

    }

    @Override
    public long getParamSize() throws MipamsException {
        long sum = 0;

        if (offsetExists()) {
            sum += getOffsetSize();
        }

        if (labelExists()) {
            sum += getLabelSize();
        }

        return sum;
    }

    public boolean offsetExists() {
        return getOffset() != null;
    }

    private long getOffsetSize() {
        return CoreUtils.LONG_BYTE_SIZE;
    }

    long getLabelSize() {
        return getLabelWithEscapeCharacter().length();
    }

    public boolean labelExists() {
        return getOffset() != null && offsetHasMaxValue();
    }

    private boolean offsetHasMaxValue() {
        return getOffset() == getMaxLongValue();
    }

    public long getMaxLongValue() {
        String maxLongAsHex = "FFFFFFFFFFFFFFFF";
        BigInteger maxValue = new BigInteger(maxLongAsHex, 16);
        return maxValue.longValue();
    }

    public String getLabelWithEscapeCharacter() {
        return CoreUtils.addEscapeCharacterToText(getLabel());
    }
}
