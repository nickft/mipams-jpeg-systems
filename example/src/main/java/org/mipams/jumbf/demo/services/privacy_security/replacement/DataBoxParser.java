package org.mipams.jumbf.demo.services.privacy_security.replacement;

import java.util.List;

import com.fasterxml.jackson.databind.node.ObjectNode;

import org.mipams.jumbf.core.entities.BmffBox;
import org.mipams.jumbf.core.util.MipamsException;

public interface DataBoxParser {
    List<BmffBox> discoverDataBoxFromRequest(ObjectNode inputNode) throws MipamsException;
}
