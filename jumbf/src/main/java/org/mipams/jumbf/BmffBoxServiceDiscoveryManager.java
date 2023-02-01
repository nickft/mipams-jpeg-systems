package org.mipams.jumbf;

import org.mipams.jumbf.util.MipamsException;
import org.mipams.jumbf.entities.BmffBox;
import org.mipams.jumbf.services.boxes.BmffBoxService;

import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BmffBoxServiceDiscoveryManager {

    private static final Logger logger = Logger.getLogger(BmffBoxServiceDiscoveryManager.class.getName());

    @Autowired
    protected List<BmffBoxService<? extends BmffBox>> bmffBoxServices;

    public BmffBoxService<? extends BmffBox> getBmffBoxServiceBasedOnTbox(int tBox) throws MipamsException {

        for (BmffBoxService<? extends BmffBox> service : bmffBoxServices) {

            logger.log(Level.FINE,service.getClass().getName());

            if (service.getServiceMetadata().getBoxTypeId() == tBox) {
                return service;
            }
        }

        throw new MipamsException("Box with tBox " + tBox + " is not registered");
    }
}