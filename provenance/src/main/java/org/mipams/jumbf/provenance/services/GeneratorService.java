package org.mipams.jumbf.provenance.services;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import org.mipams.jumbf.core.BoxServiceManager;
import org.mipams.jumbf.core.entities.XTBox;
import org.mipams.jumbf.core.services.GeneratorInterface;
import org.mipams.jumbf.core.services.XTBoxService;
import org.mipams.jumbf.core.util.MipamsException;
import org.mipams.jumbf.provenance.util.BoxTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@SuppressWarnings("unchecked")
public class GeneratorService implements GeneratorInterface {

    private static final Logger logger = LoggerFactory.getLogger(GeneratorService.class);

    @Autowired
    BoxServiceManager boxServiceManager;

    @Override
    public List<XTBox> generateBoxFromRequest(JsonNode inputNode) throws MipamsException {
        return null;
    }

    @Override
    public String generateJumbfFileFromBox(List<XTBox> xtBoxList) throws MipamsException {
        XTBoxService<XTBox> result = boxServiceManager
                .generateServiceBasedOnBoxWithId(BoxTypeEnum.AssertionBox.getTypeId());

        logger.debug("Connected properly");
        if (result == null) {
            return "failed to connect the two packages";
        } else {
            return "I connected one package with another. The box with id: "
                    + Integer.toString(result.serviceIsResponsibleForBoxTypeId()) + " is discovered properly.";
        }
    }
}