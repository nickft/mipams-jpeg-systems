package org.mipams.jumbf.core.entities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.mipams.jumbf.core.util.BoxTypeEnum;
import org.mipams.jumbf.core.util.MipamsException;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@ToString
public class JsonBox extends XTBox {

    private @Getter @Setter ObjectNode jsonContent;

    @Override
    public int getTypeId() {
        return BoxTypeEnum.JsonBox.getTypeId();
    }

    @Override
    public long calculatePayloadSize() throws MipamsException {

        ObjectMapper om = new ObjectMapper();
        final ObjectWriter writer = om.writer();

        byte[] bytes;

        try {
            bytes = writer.writeValueAsBytes(jsonContent);
            return bytes.length;
        } catch (JsonProcessingException e) {
            throw new MipamsException("Coulnd not convert json to byte array", e);
        }
    }
}