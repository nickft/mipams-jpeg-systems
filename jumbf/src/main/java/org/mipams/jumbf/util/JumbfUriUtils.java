package org.mipams.jumbf.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mipams.jumbf.entities.BmffBox;
import org.mipams.jumbf.entities.JumbfBox;

public class JumbfUriUtils {

    public static String SELF_CONTAINED_URI = "self#jumbf=";

    private static final String SELF_JUMBF_URI_REFERENCE_REGEX = "self#jumbf=[\\w\\d\\/][\\w\\d\\.\\/:\\-]+[\\w\\d]";

    public static Optional<JumbfBox> getJumbfBoxFromRelativeUri(String jumbfUri, JumbfBox... jumbfBoxes)
            throws MipamsException {
        if (!isJumbfUriReferenceValid(jumbfUri)) {
            throw new MipamsException(String.format(
                    "Invalid JUMBF URI reference acccording to ISO/IEC 19566-5 specification: [%s]",
                    jumbfUri));
        }

        if (isJumbfUriAbsolute(jumbfUri)) {
            throw new MipamsException("JUMBF URI is absolute. Look up with absolute JUMBF URI is not supported.");
        }

        if (jumbfBoxes == null) {
            throw new MipamsException("Invalid look up JUMBF Box(es). ");
        }

        final String uriPath = jumbfUri.substring("self#jumbf=".length());
        List<String> literals = new ArrayList<>(Arrays.asList(uriPath.split("/")));

        Iterator<? extends BmffBox> jumbfBoxIterator = new ArrayList<>(Arrays.asList(jumbfBoxes)).stream()
                .filter(contentBox -> contentBox.getClass().equals(JumbfBox.class)).iterator();

        Iterator<String> literalIterator = literals.iterator();
        while (literalIterator.hasNext()) {
            String literal = literalIterator.next();
            while (jumbfBoxIterator.hasNext()) {
                JumbfBox box = (JumbfBox) jumbfBoxIterator.next();

                if (literal.equals(box.getDescriptionBox().getLabel())) {
                    if (literalIterator.hasNext()) {
                        jumbfBoxIterator = box.getContentBoxList().stream()
                                .filter(contentBox -> contentBox.getClass().equals(JumbfBox.class))
                                .iterator();
                    } else {
                        return Optional.of(box);
                    }
                    break;
                }
            }
        }

        return (jumbfBoxIterator.hasNext()) ? Optional.of(new JumbfBox()) : Optional.empty();
    }

    public static Optional<JumbfBox> getJumbfBoxFromAbsoluteUri(String jumbfUri, JumbfBox parentJumbfBox)
            throws MipamsException {

        if (!isJumbfUriReferenceValid(jumbfUri)) {
            throw new MipamsException(String.format(
                    "Invalid JUMBF URI reference acccording to ISO/IEC 19566-5 specification: [%s]",
                    jumbfUri));
        }

        if (!isJumbfUriAbsolute(jumbfUri)) {
            throw new MipamsException("JUMBF URI is not absolute. Look up with relative JUMBF URI is not supported.");
        }

        final String uriPath = jumbfUri.substring("self#jumbf=/".length());
        List<String> literals = new ArrayList<>(Arrays.asList(uriPath.split("/")));

        String superBoxDescriptionBoxLabel = literals.removeFirst();

        if (parentJumbfBox.getDescriptionBox() == null || parentJumbfBox.getContentBoxList() == null) {
            throw new MipamsException("Parent Jumbf Box is invalid.");
        }

        if (!superBoxDescriptionBoxLabel.equals(parentJumbfBox.getDescriptionBox().getLabel())) {
            throw new MipamsException(String.format(
                    "Invalid superbox description box label. Searching for [%s] but found %s",
                    jumbfUri, parentJumbfBox.getDescriptionBox().getLabel()));
        }

        if (literals.isEmpty()) {
            return Optional.of(parentJumbfBox);
        }

        Iterator<BmffBox> jumbfBoxIterator = parentJumbfBox.getContentBoxList().stream()
                .filter(contentBox -> contentBox.getClass().equals(JumbfBox.class)).iterator();

        Iterator<String> literalIterator = literals.iterator();
        while (literalIterator.hasNext()) {
            String literal = literalIterator.next();
            while (jumbfBoxIterator.hasNext()) {
                JumbfBox box = (JumbfBox) jumbfBoxIterator.next();

                if (literal.equals(box.getDescriptionBox().getLabel())) {
                    if (literalIterator.hasNext()) {
                        jumbfBoxIterator = box.getContentBoxList().stream()
                                .filter(contentBox -> contentBox.getClass().equals(JumbfBox.class))
                                .iterator();
                    } else {
                        return Optional.of(box);
                    }
                    break;
                }
            }
        }

        return (jumbfBoxIterator.hasNext()) ? Optional.of(new JumbfBox()) : Optional.empty();
    }

    public static boolean isJumbfUriReferenceValid(String uriReference) {
        if (uriReference == null) {
            return false;
        }

        Pattern pattern = Pattern.compile(SELF_JUMBF_URI_REFERENCE_REGEX);
        Matcher matcher = pattern.matcher(uriReference);

        return matcher.matches();
    }

    public static boolean isJumbfUriAbsolute(String url) {
        return url.startsWith("self#jumbf=/");
    }

}
