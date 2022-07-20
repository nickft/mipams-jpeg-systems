import React, { useEffect, useState } from 'react'

import ParseLayout from '../components/Parse/ParseLayout'

import { api } from '../utils/api';

const Parse = () => {

    const [jumbfStructure, setJumbfStructure] = useState();

    const [loading, setLoading] = useState(false);

    const [errorMessage, setErrorMessage] = useState();
    const [expanded, setExpanded] = useState([]);

    const [modalContent, setModalContent] = useState(null);
    const addModalContent = (content) => { setModalContent(content) };
    const closeModalContent = () => { setModalContent(null) };

    const [uploadedFileName, setUploadedFileName] = useState();

    const handleToggle = (event, nodeIds) => {
        setExpanded(nodeIds);
    };

    useEffect(() => {

        if (uploadedFileName) {
            setLoading(true);
            setExpanded([]);

            const formData = new FormData();

            formData.append(
                "file",
                uploadedFileName,
                uploadedFileName.name
            );

            // Request made to the backend api Send formData object

            api.post("/demo/uploadJumbfFile", formData).then(response => {
                setJumbfStructure(response.data);
                setErrorMessage(null);
                setLoading(false);
            }).catch(error => {
                setJumbfStructure(null);
                setLoading(false);
                if (typeof (error.response.data) === 'string') {
                    setErrorMessage(error.response.data);
                } else {
                    setErrorMessage(error.message);
                }
            });
        }
    }, [uploadedFileName])

    function handleFileUploadChange(event) {
        setUploadedFileName(event.target.files[0]);
    }

    function onFileUploadClick(event) {
        setExpanded([]);
    }

    return (
        <ParseLayout
            jumbfStructure={jumbfStructure}
            errorMessage={errorMessage}
            setErrorMessage={setErrorMessage}
            expandedList={expanded}
            handleToggle={handleToggle}
            loading={loading}
            handleFileUploadChange={handleFileUploadChange}
            onFileUploadClick={onFileUploadClick}
            modalContent={modalContent}
            addModalContent={addModalContent}
            closeModalContent={closeModalContent}
        />
    )
}

export default Parse