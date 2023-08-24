package org.mipams.jpegsnack.services.boxes;

import java.io.InputStream;
import java.io.OutputStream;

import org.mipams.jpegsnack.entities.InstructionParameter;
import org.mipams.jpegsnack.entities.InstructionSetBox;
import org.mipams.jpegsnack.entities.JpegSnackParseMetadata;
import org.mipams.jumbf.entities.ParseMetadata;
import org.mipams.jumbf.entities.ServiceMetadata;
import org.mipams.jumbf.services.boxes.BmffBoxService;
import org.mipams.jumbf.util.CoreUtils;
import org.mipams.jumbf.util.MipamsException;
import org.springframework.stereotype.Service;

@Service
public class InstructionSetBoxService extends BmffBoxService<InstructionSetBox> {

    ServiceMetadata serviceMetadata = new ServiceMetadata(InstructionSetBox.TYPE_ID,
            InstructionSetBox.TYPE);

    @Override
    public ServiceMetadata getServiceMetadata() {
        return serviceMetadata;
    }

    @Override
    protected InstructionSetBox initializeBox() {
        return new InstructionSetBox();
    }

    @Override
    protected void populatePayloadFromJumbfFile(InstructionSetBox instructionSetBox, ParseMetadata metadata,
            InputStream is) throws MipamsException {
        JpegSnackParseMetadata jpegSnackParseMetadata = (JpegSnackParseMetadata) metadata;

        int ltyp = CoreUtils.readTwoByteWordAsInt(is);
        instructionSetBox.setLtyp(ltyp);

        int repetition = CoreUtils.readTwoByteWordAsInt(is);
        instructionSetBox.setRepetition(repetition);

        int tick = CoreUtils.readIntFromInputStream(is);
        instructionSetBox.setTick(tick);

        populateInstructionParameters(instructionSetBox, jpegSnackParseMetadata.getNoOfObjects(), is);
    }

    private void populateInstructionParameters(InstructionSetBox instructionSetBox, int totalNumberOfInstructions,
            InputStream is) throws MipamsException {
        for (int i = 0; i < totalNumberOfInstructions; i++) {
            InstructionParameter parameter = new InstructionParameter(instructionSetBox.getLtyp());
            if (parameter.xoAndYoExist()) {
                int xo = CoreUtils.readIntFromInputStream(is);
                int yo = CoreUtils.readIntFromInputStream(is);

                parameter.setHorizontalOffset(xo);
                parameter.setVerticalOffset(yo);
            }

            if (parameter.widthAndHeightExist()) {
                int width = CoreUtils.readIntFromInputStream(is);
                int height = CoreUtils.readIntFromInputStream(is);
                parameter.setWidth(width);
                parameter.setHeight(height);
            }

            if (parameter.persistAndLifeAndNextUseExist()) {
                int life = CoreUtils.readIntFromInputStream(is);
                int nextUse = CoreUtils.readIntFromInputStream(is);

                boolean persist = life < 0;
                life = life & 0x7F;

                parameter.setPersist(persist);
                parameter.setLife(life);
                parameter.setNextUse(nextUse);
            }

            if (parameter.xcAndYcAndHcAndWcExist()) {
                int xcrop = CoreUtils.readIntFromInputStream(is);
                int ycrop = CoreUtils.readIntFromInputStream(is);
                int wcrop = CoreUtils.readIntFromInputStream(is);
                int hcrop = CoreUtils.readIntFromInputStream(is);

                parameter.setHorizontalCropOffset(xcrop);
                parameter.setVerticalCropOffset(ycrop);
                parameter.setCroppedWidth(wcrop);
                parameter.setCroppedHeight(hcrop);
            }

            if (parameter.rotExists()) {
                int rotation = CoreUtils.readIntFromInputStream(is);
                parameter.setRotation(rotation);
            }
            instructionSetBox.getInstructionParameters().add(parameter);
        }

    }

    @Override
    protected void writeBmffPayloadToJumbfFile(InstructionSetBox instructionSetBox, OutputStream os)
            throws MipamsException {

        CoreUtils.writeIntAsTwoByteToOutputStream(instructionSetBox.getLtyp(), os);

        CoreUtils.writeIntAsTwoByteToOutputStream(instructionSetBox.getRepetition(), os);

        CoreUtils.writeIntToOutputStream(instructionSetBox.getTick(), os);

        for (InstructionParameter parameter : instructionSetBox.getInstructionParameters()) {
            if (parameter.xoAndYoExist()) {
                CoreUtils.writeIntToOutputStream(parameter.getHorizontalOffset(), os);
                CoreUtils.writeIntToOutputStream(parameter.getVerticalOffset(), os);
            }

            if (parameter.widthAndHeightExist()) {
                CoreUtils.writeIntToOutputStream(parameter.getWidth(), os);
                CoreUtils.writeIntToOutputStream(parameter.getHeight(), os);
            }

            if (parameter.persistAndLifeAndNextUseExist()) {
                CoreUtils.writeIntToOutputStream(parameter.getPersistAndLifeValue(), os);
                CoreUtils.writeIntToOutputStream(parameter.getNextUse(), os);
            }

            if (parameter.xcAndYcAndHcAndWcExist()) {
                CoreUtils.writeIntToOutputStream(parameter.getHorizontalCropOffset(), os);
                CoreUtils.writeIntToOutputStream(parameter.getVerticalCropOffset(), os);
                CoreUtils.writeIntToOutputStream(parameter.getCroppedWidth(), os);
                CoreUtils.writeIntToOutputStream(parameter.getCroppedHeight(), os);
            }

            if (parameter.rotExists()) {
                CoreUtils.writeIntToOutputStream(parameter.getRotation(), os);
            }
        }
    }

}
