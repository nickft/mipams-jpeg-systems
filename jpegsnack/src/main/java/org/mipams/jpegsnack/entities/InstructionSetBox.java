package org.mipams.jpegsnack.entities;

import java.util.ArrayList;
import java.util.List;

import org.mipams.jpegsnack.util.JpegSnackException;
import org.mipams.jumbf.entities.BmffBox;
import org.mipams.jumbf.util.CoreUtils;
import org.mipams.jumbf.util.MipamsException;

public class InstructionSetBox extends BmffBox {

    int ltyp;
    int repetition;
    int tick;
    List<InstructionParameter> instructionParameters = new ArrayList<>();

    public static int TYPE_ID = 0x696E7374;
    public static String TYPE = "inst";

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public int getTypeId() {
        return TYPE_ID;
    }

    @Override
    protected long calculatePayloadSize() throws MipamsException {
        long result = 0;

        result += getToggleSize();

        result += getRepetitionSize();

        result += getTickSize();

        if (hasInstructionSets()) {
            for (InstructionParameter instructionParameter : instructionParameters) {
                result += instructionParameter.calculateSizeInBytes();
            }
        }

        return result;
    }

    private long getToggleSize() {
        return CoreUtils.WORD_BYTE_SIZE;
    }

    private long getRepetitionSize() {
        return CoreUtils.WORD_BYTE_SIZE;
    }

    private long getTickSize() {
        return CoreUtils.INT_BYTE_SIZE;
    }

    private boolean hasInstructionSets() {
        return ltyp != 0;
    }

    public int getLtyp() {
        return ltyp;
    }

    public void setLtyp(int ltyp) {
        this.ltyp = ltyp;
    }

    public int getRepetition() {
        return repetition;
    }

    public void setRepetition(int repetition) {
        this.repetition = repetition;
    }

    public int getTick() {
        return tick;
    }

    public void setTick(int tick) {
        this.tick = tick;
    }

    public List<InstructionParameter> getInstructionParameters() {
        return instructionParameters;
    }

    public void setInstructionParameters(List<InstructionParameter> instructionParameters) {
        this.instructionParameters.clear();
        this.instructionParameters.addAll(instructionParameters);
    }

    @Override
    public void applyInternalBoxFieldsBasedOnExistingData() throws MipamsException {

        Integer instructionSetToggle = 0;

        for (InstructionParameter instructionParameter : instructionParameters) {
            int currentToggle = instructionParameter.applyAndReturnToggleForInstructionParameter();
            if (instructionSetToggle == 0) {
                instructionSetToggle = currentToggle;
                continue;
            }

            if (currentToggle != instructionSetToggle) {
                throw new JpegSnackException("Inconsistent instruction set box toggle among the defined instructions");
            }
        }

        setLtyp(instructionSetToggle);
    }
}
