import React, { useState } from 'react'
import GenerateLayout from '../components/Generate/GenerateLayout'

import { getRandomInt } from '../utils/helpers';

import { api } from '../utils/api';


const Generate = () => {

    const [jsonRequest, setJsonRequest] = useState();

    const [loading, setLoading] = useState(false);
    const [errorMessage, setErrorMessage] = useState();

    const [jumbfFileName, setJumbfFileName] = useState("");

    const [download, setDownload] = useState(false);

    const [modalContent, setModalContent] = useState(null);
    const openModalContent = () => { setModalContent(jumbfFileName) };
    const closeModalContent = () => { setModalContent(null) };

    const [fileList, setFileList] = useState([{ id: getRandomInt(1000), info: null }]);

    const onFileFieldChangeHandler = (fieldId, event) => {

        if (event) {
            const fileField = fileList.filter(field => field.id === fieldId)[0];
            fileField.info = event.target.files[0]

            const filteredList = fileList.filter(field => field.id !== fieldId);
            setFileList([...filteredList, fileField]);
        }
    }

    const onDeleteFileWithIdHandler = (fieldId) => {

        if (fileList.length < 2) {
            return;
        }

        const filteredList = fileList.filter(field => field.id !== fieldId);
        setFileList(filteredList);
    }

    const onAddFileWithIdHandler = () => {
        const newFileField = { id: getRandomInt(1000), info: null }
        setFileList([...fileList, newFileField]);
    }

    function uploadFile(file) {

        console.log("Uploading file: " + file.name);

        const formData = new FormData();

        formData.append(
            "file",
            file,
            file.name
        );

        api.post("/demo/uploadMetadataFile", formData).then(() => {
            console.log("Successfully uploaded file: " + file.name);
        }).catch(error => {
            if (typeof (error.response.data) === 'string') {
                setErrorMessage(error.response.data);
            } else {
                setErrorMessage(error.message);
            }
        });

        return;
    }

    const generateJumbfFileHandler = () => {
        console.log("Initiate Generation Process");
        setErrorMessage(null);

        if (!jsonRequest) {
            setErrorMessage("JUMBF Structure cannot be empty");
            setLoading(false)
            return;
        }

        setLoading(true);

        const filesToUpload = fileList.filter(field => field.info);

        filesToUpload.forEach(fileField => uploadFile(fileField.info));

        const url = (jumbfFileName) ? "/demo/generateBox?targetFile=" + jumbfFileName : "/demo/generateBox";

        api.post(url, JSON.parse(jsonRequest)).then((response) => {
            setLoading(false);
            setDownload(true)
        }).catch(error => {
            if (typeof (error.response.data) === 'string') {
                setErrorMessage(error.response.data);
            } else {
                setErrorMessage(error.message);
            }
            setLoading(false);
        });

        console.log("Finish Generation Process");
    }

    return (
        <GenerateLayout
            loading={loading}
            errorMessage={errorMessage}
            setErrorMessage={setErrorMessage}
            fileList={fileList}
            onAddFileWithId={onAddFileWithIdHandler}
            onDeleteFileWithId={onDeleteFileWithIdHandler}
            onFileFieldChange={onFileFieldChangeHandler}
            jsonRequest={jsonRequest}
            setJsonRequest={setJsonRequest}
            generateJumbfFile={generateJumbfFileHandler}
            jumbfFileName={jumbfFileName}
            setJumbfFileName={setJumbfFileName}
            download={download}
            setDownload={setDownload}
            modalContent={modalContent}
            openModalContent={openModalContent}
            closeModalContent={closeModalContent}
        />
    )
}

export default Generate