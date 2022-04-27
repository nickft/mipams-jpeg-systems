package org.mipams.jumbf.demo.services.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.mipams.jumbf.core.entities.BmffBox;
import org.mipams.jumbf.core.util.BadRequestException;
import org.mipams.jumbf.core.util.MipamsException;

public abstract class BmffBoxParser<T extends BmffBox> implements BoxParserInterface {

    @Override
    public final T discoverBoxFromRequest(ObjectNode inputNode) throws MipamsException {
        T bmffBox = initializeBox();

        try {
            validateRequestType(bmffBox, inputNode);
            populateBox(bmffBox, inputNode);
        } catch (NullPointerException e) {
            throw new MipamsException("Error while parsing the request for box: " + bmffBox.getType(), e);
        }

        bmffBox.updateBmffHeadersBasedOnBox();

        return bmffBox;
    }

    protected abstract T initializeBox();

    private void validateRequestType(T box, ObjectNode inputNode) throws BadRequestException {
        JsonNode typeNode = inputNode.get("type");

        if (typeNode == null) {
            throw new BadRequestException("Box 'type' must be specified");
        }

        String expectedType = box.getType();
        String requestedType = typeNode.asText();

        if (!expectedType.equals(requestedType)) {
            String errorMessage = generateErrorMessageForRequestValidation(expectedType, requestedType);
            throw new BadRequestException(errorMessage);
        }
    }

    private String generateErrorMessageForRequestValidation(String expectedType, String requestedType) {

        String errorMessage = String.format(
                "Box type does not match with description type. Expected: %s, Found: %s",
                expectedType, requestedType);

        return errorMessage;
    }

    protected abstract void populateBox(T box, ObjectNode input) throws MipamsException;

}
