package org.mipams.jumbf.privacy_security.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import org.mipams.jumbf.core.entities.BinaryDataBox;
import org.mipams.jumbf.core.entities.BmffBox;
import org.mipams.jumbf.core.entities.ContiguousCodestreamBox;
import org.mipams.jumbf.core.entities.JsonBox;
import org.mipams.jumbf.core.entities.JumbfBox;
import org.mipams.jumbf.core.services.content_types.JsonContentType;
import org.mipams.jumbf.core.util.MipamsException;

import org.mipams.jumbf.privacy_security.entities.ReplacementDescriptionBox;
import org.mipams.jumbf.privacy_security.entities.replacement.AppParamHandler;
import org.mipams.jumbf.privacy_security.entities.replacement.BoxParamHandler;
import org.mipams.jumbf.privacy_security.entities.replacement.FileParamHandler;
import org.mipams.jumbf.privacy_security.entities.replacement.ParamHandlerInterface;
import org.mipams.jumbf.privacy_security.entities.replacement.ReplacementType;
import org.mipams.jumbf.privacy_security.entities.replacement.RoiParamHandler;
import org.mipams.jumbf.privacy_security.services.boxes.replacement.ParamHandlerFactory;
import org.mipams.jumbf.privacy_security.services.content_types.ReplacementContentType;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class ReplacementBoxTests extends AbstractIntegrationTests {

        @BeforeAll
        static void initRequest() throws IOException {
                generateFile();
        }

        @AfterAll
        public static void cleanUp() throws IOException {
                fileCleanUp();
        }

        @Test
        void testBoxReplacementBoxWithOffset() throws Exception {

                BoxParamHandler boxParamHandler = new BoxParamHandler();
                boxParamHandler.setOffset(Long.parseLong("123123123123"));

                JsonBox jsonBox = new JsonBox();
                jsonBox.setContent(TEST_CONTENT.getBytes());
                jsonBox.updateBmffHeadersBasedOnBox();

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
                jsonBox.updateBmffHeadersBasedOnBox();

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
                jsonBox.updateBmffHeadersBasedOnBox();

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
                jsonBox.updateBmffHeadersBasedOnBox();

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
                AppParamHandler appParamHandler = new AppParamHandler();
                appParamHandler.setOffset(Long.parseLong("123123123123"));

                BinaryDataBox binaryDataBox = new BinaryDataBox();
                binaryDataBox.setFileUrl(TEST_FILE_PATH);
                binaryDataBox.updateBmffHeadersBasedOnBox();

                List<BmffBox> replacementDataBoxList = List.of(binaryDataBox);

                JumbfBox givenJumbfBox = getReplacementJumbfBoxBasedOnReplacementDescriptionBox(
                                ReplacementType.APP.getId(),
                                appParamHandler, replacementDataBoxList);
                JumbfBox parsedJumbfBox = generateJumbfFileAndParseBox(List.of(givenJumbfBox)).get(0);

                assertEquals(givenJumbfBox, parsedJumbfBox);
        }

        @Test
        void testFileReplacementBox() throws Exception {
                FileParamHandler fileParamHandler = new FileParamHandler();

                ContiguousCodestreamBox jp2cBox = new ContiguousCodestreamBox();
                jp2cBox.setFileUrl(TEST_FILE_PATH);
                jp2cBox.updateBmffHeadersBasedOnBox();

                List<BmffBox> replacementDataBoxList = List.of(jp2cBox);

                JumbfBox givenJumbfBox = getReplacementJumbfBoxBasedOnReplacementDescriptionBox(
                                ReplacementType.FILE.getId(), fileParamHandler, replacementDataBoxList);
                JumbfBox parsedJumbfBox = generateJumbfFileAndParseBox(List.of(givenJumbfBox)).get(0);

                assertEquals(givenJumbfBox, parsedJumbfBox);
        }

        @Test
        void testRoiReplacementBox() throws Exception {
                RoiParamHandler roiParamHandler = new RoiParamHandler();

                ContiguousCodestreamBox jp2cBox = new ContiguousCodestreamBox();
                jp2cBox.setFileUrl(TEST_FILE_PATH);
                jp2cBox.updateBmffHeadersBasedOnBox();

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
                replacementDescriptionBox.updateBmffHeadersBasedOnBox();

                ReplacementContentType replacementContentType = new ReplacementContentType();
                String contentTypeUuid = replacementContentType.getContentTypeUuid();

                List<BmffBox> contentBoxList = new ArrayList<>();
                contentBoxList.add(replacementDescriptionBox);
                contentBoxList.addAll(replacementDataBoxList);

                return MockJumbfBox.generateJumbfBoxWithContent(contentTypeUuid, contentBoxList);
        }
}
