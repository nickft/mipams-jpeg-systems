package org.mipams.jumbf.core;

import org.mipams.jumbf.core.util.MipamsException;
import org.mipams.jumbf.core.entities.BmffBox;
import org.mipams.jumbf.core.services.boxes.BmffBoxService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class BmffBoxServiceDiscoveryManager {

    private static final Logger logger = LoggerFactory.getLogger(BmffBoxServiceDiscoveryManager.class);

    @Autowired
    protected List<BmffBoxService<? extends BmffBox>> bmffBoxServices;

    public BmffBoxService<? extends BmffBox> getBmffBoxServiceBasedOnTbox(int tBox) throws MipamsException {

        for (BmffBoxService<? extends BmffBox> service : bmffBoxServices) {

            logger.debug(service.getClass().getName());

            if (service.getServiceMetadata().getBoxTypeId() == tBox) {
                return service;
            }
        }

        throw new MipamsException("Box with tBox " + tBox + " is not registered");
    }
}