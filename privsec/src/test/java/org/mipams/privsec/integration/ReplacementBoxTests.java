package org.mipams.privsec.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mipams.jumbf.entities.BinaryDataBox;
import org.mipams.jumbf.entities.BmffBox;
import org.mipams.jumbf.entities.ContiguousCodestreamBox;
import org.mipams.jumbf.entities.JsonBox;
import org.mipams.jumbf.entities.JumbfBox;
import org.mipams.jumbf.services.content_types.JsonContentType;
import org.mipams.jumbf.util.MipamsException;

import org.mipams.privsec.entities.ReplacementDescriptionBox;
import org.mipams.privsec.entities.replacement.AppParamHandler;
import org.mipams.privsec.entities.replacement.BoxParamHandler;
import org.mipams.privsec.entities.replacement.FileParamHandler;
import org.mipams.privsec.entities.replacement.ParamHandlerInterface;
import org.mipams.privsec.entities.replacement.ReplacementType;
import org.mipams.privsec.entities.replacement.RoiParamHandler;
import org.mipams.privsec.services.boxes.replacement.ParamHandlerFactory;
import org.mipams.privsec.services.content_types.ReplacementContentType;

import org.mipams.jumbf.config.JumbfConfig;
import org.mipams.privsec.config.PrivsecConfig;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.ResourceUtils;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { PrivsecConfig.class, JumbfConfig.class })
@ActiveProfiles("test")
public class ReplacementBoxTests extends AbstractIntegrationTests {

        @Test
        void testBoxReplacementBoxWithOffset() throws Exception {

                BoxParamHandler boxParamHandler = new BoxParamHandler();
                boxParamHandler.setOffset(Long.parseLong("123123123123"));

                JsonBox jsonBox = new JsonBox();
                jsonBox.setContent(TEST_CONTENT.getBytes());
                jsonBox.updateFieldsBasedOnExistingData();

                JsonContentType jsonContentType = new JsonContentType();
                String contentTypeUuid = jsonContentType.getContentTypeUuid();

                JumbfBox jsonJumbfBox = MockJumbfBox.generateJumbfBoxWithContent(contentTypeUuid, List.of(jsonBox));

                List<BmffBox> replacementDataBoxList = List.of(jsonJumbfBox);

                JumbfBox givenJumbfBox = getReplacementJumbfBoxBasedOnReplacementDescriptionBox(
                                ReplacementType.BOX.getId(), boxParamHandler, replacementDataBoxList);
                JumbfBox parsedJumbfBox = generateJumbfFileAndParseBox(List.of(givenJumbfBox)).get(0);

                assertEquals(givenJumbfBox, parsedJumbfBox);
        }

        @Test
        void testBoxReplacementBoxWithLabel() throws Exception {
                BoxParamHandler boxParamHandler = new BoxParamHandler();
                boxParamHandler.setLabel("reference-label");
                boxParamHandler.setOffset(boxParamHandler.getMaxLongValue());

                JsonBox jsonBox = new JsonBox();
                jsonBox.setContent(TEST_CONTENT.getBytes());
                jsonBox.updateFieldsBasedOnExistingData();

                JsonContentType jsonContentType = new JsonContentType();
                String contentTypeUuid = jsonContentType.getContentTypeUuid();

                JumbfBox jsonJumbfBox = MockJumbfBox.generateJumbfBoxWithContent(contentTypeUuid, List.of(jsonBox));

                List<BmffBox> replacementDataBoxList = List.of(jsonJumbfBox);

                JumbfBox givenJumbfBox = getReplacementJumbfBoxBasedOnReplacementDescriptionBox(
                                ReplacementType.BOX.getId(), boxParamHandler, replacementDataBoxList);
                JumbfBox parsedJumbfBox = generateJumbfFileAndParseBox(List.of(givenJumbfBox)).get(0);

                assertEquals(givenJumbfBox, parsedJumbfBox);
        }

        @Test
        void testBoxReplacementBoxWithNoAdditionalParam() throws Exception {
                ParamHandlerFactory paramHandlerFactory = new ParamHandlerFactory();
                ParamHandlerInterface paramHandler = paramHandlerFactory.getParamHandler(ReplacementType.BOX);

                JsonBox jsonBox = new JsonBox();
                jsonBox.setContent(TEST_CONTENT.getBytes());
                jsonBox.updateFieldsBasedOnExistingData();

                JsonContentType jsonContentType = new JsonContentType();
                String contentTypeUuid = jsonContentType.getContentTypeUuid();

                JumbfBox jsonJumbfBox = MockJumbfBox.generateJumbfBoxWithContent(contentTypeUuid, List.of(jsonBox));

                List<BmffBox> replacementDataBoxList = List.of(jsonJumbfBox);

                JumbfBox givenJumbfBox = getReplacementJumbfBoxBasedOnReplacementDescriptionBox(
                                ReplacementType.BOX.getId(), paramHandler, replacementDataBoxList);
                JumbfBox parsedJumbfBox = generateJumbfFileAndParseBox(List.of(givenJumbfBox)).get(0);

                assertEquals(givenJumbfBox, parsedJumbfBox);
        }

        @Test
        void testBoxReplacementBoxWithMultipleReplacement() throws Exception {
                BoxParamHandler boxParamHandler = new BoxParamHandler();
                boxParamHandler.setLabel("reference-label");
                boxParamHandler.setOffset(boxParamHandler.getMaxLongValue());

                JsonBox jsonBox = new JsonBox();
                jsonBox.setContent(TEST_CONTENT.getBytes());
                jsonBox.updateFieldsBasedOnExistingData();

                JsonContentType jsonContentType = new JsonContentType();
                String contentTypeUuid = jsonContentType.getContentTypeUuid();

                JumbfBox jsonJumbfBox = MockJumbfBox.generateJumbfBoxWithContent(contentTypeUuid, List.of(jsonBox));

                List<BmffBox> replacementDataBoxList = List.of(jsonJumbfBox, jsonJumbfBox, jsonJumbfBox);

                JumbfBox givenJumbfBox = getReplacementJumbfBoxBasedOnReplacementDescriptionBox(
                                ReplacementType.BOX.getId(), boxParamHandler, replacementDataBoxList);
                JumbfBox parsedJumbfBox = generateJumbfFileAndParseBox(List.of(givenJumbfBox)).get(0);

                assertEquals(givenJumbfBox, parsedJumbfBox);
        }

        @Test
        void testAppReplacementBox() throws Exception {
                String assetFileUrl = ResourceUtils.getFile("classpath:sample.jpeg").getAbsolutePath();

                AppParamHandler appParamHandler = new AppParamHandler();
                appParamHandler.setOffset(Long.parseLong("123123123123"));

                BinaryDataBox binaryDataBox = new BinaryDataBox();
                binaryDataBox.setFileUrl(assetFileUrl);
                binaryDataBox.updateFieldsBasedOnExistingData();

                List<BmffBox> replacementDataBoxList = List.of(binaryDataBox);

                JumbfBox givenJumbfBox = getReplacementJumbfBoxBasedOnReplacementDescriptionBox(
                                ReplacementType.APP.getId(),
                                appParamHandler, replacementDataBoxList);
                JumbfBox parsedJumbfBox = generateJumbfFileAndParseBox(List.of(givenJumbfBox)).get(0);

                assertEquals(givenJumbfBox, parsedJumbfBox);
        }

        @Test
        void testFileReplacementBox() throws Exception {
                String assetFileUrl = ResourceUtils.getFile("classpath:sample.jpeg").getAbsolutePath();

                FileParamHandler fileParamHandler = new FileParamHandler();

                ContiguousCodestreamBox jp2cBox = new ContiguousCodestreamBox();
                jp2cBox.setFileUrl(assetFileUrl);
                jp2cBox.updateFieldsBasedOnExistingData();

                List<BmffBox> replacementDataBoxList = List.of(jp2cBox);

                JumbfBox givenJumbfBox = getReplacementJumbfBoxBasedOnReplacementDescriptionBox(
                                ReplacementType.FILE.getId(), fileParamHandler, replacementDataBoxList);
                JumbfBox parsedJumbfBox = generateJumbfFileAndParseBox(List.of(givenJumbfBox)).get(0);

                assertEquals(givenJumbfBox, parsedJumbfBox);
        }

        @Test
        void testRoiReplacementBox() throws Exception {
                String assetFileUrl = ResourceUtils.getFile("classpath:sample.jpeg").getAbsolutePath();

                RoiParamHandler roiParamHandler = new RoiParamHandler();

                ContiguousCodestreamBox jp2cBox = new ContiguousCodestreamBox();
                jp2cBox.setFileUrl(assetFileUrl);
                jp2cBox.updateFieldsBasedOnExistingData();

                List<BmffBox> replacementDataBoxList = List.of(jp2cBox);

                JumbfBox givenJumbfBox = getReplacementJumbfBoxBasedOnReplacementDescriptionBox(
                                ReplacementType.ROI.getId(), roiParamHandler, replacementDataBoxList);
                JumbfBox parsedJumbfBox = generateJumbfFileAndParseBox(List.of(givenJumbfBox)).get(0);

                assertEquals(givenJumbfBox, parsedJumbfBox);
        }

        private JumbfBox getReplacementJumbfBoxBasedOnReplacementDescriptionBox(int replacementTypeId,
                        ParamHandlerInterface paramHandler, List<BmffBox> replacementDataBoxList)
                        throws MipamsException {

                ReplacementDescriptionBox replacementDescriptionBox = new ReplacementDescriptionBox();
                replacementDescriptionBox.setAutoApply(replacementTypeId > 1);
                replacementDescriptionBox.setReplacementTypeId(replacementTypeId);
                replacementDescriptionBox.setParamHandler(paramHandler);
                replacementDescriptionBox.updateFieldsBasedOnExistingData();

                ReplacementContentType replacementContentType = new ReplacementContentType();
                String contentTypeUuid = replacementContentType.getContentTypeUuid();

                List<BmffBox> contentBoxList = new ArrayList<>();
                contentBoxList.add(replacementDescriptionBox);
                contentBoxList.addAll(replacementDataBoxList);

                return MockJumbfBox.generateJumbfBoxWithContent(contentTypeUuid, contentBoxList);
        }
}
