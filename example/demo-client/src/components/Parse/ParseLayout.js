import { Alert, Box, Card, IconButton, Modal, Stack, Typography } from '@mui/material'
import React from 'react'
import CloseIcon from '@mui/icons-material/Close';
import { styled } from '@mui/material/styles';
import IntroParseLayout from './IntroParseLayout';
import ResultParseLayout from './ResultParseLayout';

const ModalBox = styled(Box)(({ theme }) => ({
    border: 'none',
    width: '70%',
    height: '70vh',
    overflow: 'auto',
    backgroundColor: 'white',
    padding: theme.spacing(2),
    display: 'flex',
    flexDirection: 'column',
}));

const ModalStack = styled(Stack)(({ theme }) => ({
    paddingBottom: theme.spacing(2),
    flexGrow: 0,
    width: '100%',
    justifyContent: 'space-between',
}));


const ParseLayout = React.forwardRef((props, ref) => {

    const {
        jumbfStructure,
        uploadedFileName,
        parsedFileName,
        loading,
        handleFileUploadChange,
        onFileUploadClick,
        modalContent,
        addModalContent,
        closeModalContent,
        expandedList,
        handleToggle,
        errorMessage,
        setErrorMessage,
    } = props;

    let errorMessageComponent = (errorMessage) ? <Alert severity="error" onClose={() => setErrorMessage(null)} sx={{ flex: '0' }}>
        Error parsing the file: {errorMessage}
    </Alert > : null;

    let output = <IntroParseLayout
        handleFileUploadChange={handleFileUploadChange}
        onFileUploadClick={onFileUploadClick}
        loading={loading}
    />;

    if (jumbfStructure) {
        output = <ResultParseLayout
            jumbfStructure={jumbfStructure}
            addModalContent={addModalContent}
            expandedList={expandedList}
            handleToggle={handleToggle}
            uploadedFileName={uploadedFileName}
            parsedFileName={parsedFileName}
            loading={loading}
        />;
    }

    return (
        <React.Fragment>
            {output}
            {errorMessageComponent}
            <Modal
                open={modalContent !== null}
                onClose={closeModalContent}
                aria-labelledby="modal-modal-title"
                aria-describedby="modal-modal-description"
                sx={{
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center'
                }}
            >
                <ModalBox>
                    <ModalStack direction="row">
                        <Typography variant="h6">Content (Note: Use a prettifier application -e.g., for JSON/XML format- to better inspect the data)</Typography>
                        <IconButton aria-label="delete" size="small" onClick={closeModalContent}>
                            <CloseIcon />
                        </IconButton>
                    </ModalStack>
                    <Card readOnly={true} variant="outlined" sx={{ flexGrow: 1, overflowY: 'auto', overflowX: 'auto', }}>
                        {modalContent}
                    </Card>
                </ModalBox>
            </Modal>
        </React.Fragment >
    )
});

export default ParseLayout