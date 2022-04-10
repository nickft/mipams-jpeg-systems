package org.mipams.jumbf.privacy_security.entities.replacement;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.fasterxml.jackson.databind.node.ObjectNode;

import org.mipams.jumbf.core.util.CoreUtils;
import org.mipams.jumbf.core.util.MipamsException;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@ToString
public class RoiParamHandler implements ParamHandlerInterface {

    private @Getter @Setter int offsetX;
    private @Getter @Setter int offsetY;

    @Override
    public void populateParamFromRequest(ObjectNode input) throws MipamsException {
        int offsetX = input.get("offset-X").asInt();
        int offsetY = input.get("offset-Y").asInt();

        setOffsetX(offsetX);
        setOffsetY(offsetY);
    }

    @Override
    public void writeParamToBytes(OutputStream outputStream) throws MipamsException {

        try {
            outputStream.write(CoreUtils.convertIntToByteArray(getOffsetX()));
            outputStream.write(CoreUtils.convertIntToByteArray(getOffsetY()));
        } catch (IOException e) {
            throw new MipamsException("Could not write to file.", e);
        }
    }

    @Override
    public void populateParamFromBytes(InputStream inputStream) throws MipamsException {

        int offsetX = CoreUtils.readIntFromInputStream(inputStream);
        int offsetY = CoreUtils.readIntFromInputStream(inputStream);

        setOffsetX(offsetX);
        setOffsetY(offsetY);
    }

    @Override
    public long getParamSize() throws MipamsException {
        return 2 * CoreUtils.INT_BYTE_SIZE;
    }

}
