import { Box, Modal, Paper } from '@mui/material'
import React from 'react'

import { styled } from '@mui/material/styles';
import { Button, Alert } from '@mui/material';

import JumbfStructure from './JumbfStructure'


const StyledBox = styled(Box)(({ theme }) => ({
    padding: theme.spacing(1),
}));

const Input = styled('input')({
    display: 'none',
});

const ParseLayout = React.forwardRef((props, ref) => {

    const {
        jumbfStructure,
        errorMessage,
        setErrorMessage,
        expandedList,
        handleToggle,
        loading,
        handleFileUploadChange,
        onFileUploadClick,
        modalContent,
        addModalContent,
        closeModalContent
    } = props;

    let output = null;

    if (errorMessage) {
        output = <Alert severity="error" onClose={() => setErrorMessage(null)} sx={{ flex: '0' }}>
            Error parsing the file: {errorMessage}
        </Alert >
    } else if (jumbfStructure) {
        output =
            <Paper
                elevation={3}
                sx={{
                    flex: 'auto',
                    overflowY: 'auto',
                    overflowX: 'auto',
                }}
            >
                <JumbfStructure
                    jumbfStructure={jumbfStructure}
                    expandedList={expandedList}
                    handleToggle={handleToggle}
                    addModalContent={addModalContent}
                />
            </Paper>
    }

    return (
        <StyledBox
            sx={{
                flexGrow: 1,
                display: 'flex',
                flexDirection: 'column',
                height: '80vh',
                alignContent: 'center',
                justifyContent: 'center'
            }}
        >
            {output}
            <StyledBox
                sx={{
                    display: 'flex',
                    justifyContent: 'center',
                    height: '5vh'
                }}

            >
                <label htmlFor="contained-button-file">
                    <Input accept="*" id="contained-button-file" multiple type="file" onChange={handleFileUploadChange} />
                    <Button disabled={loading} variant="contained" component="span" onClick={onFileUploadClick}>
                        Upload JUMBF File
                    </Button>
                </label>
            </StyledBox>
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
                <div readonly="true" style={{ border: 'none', width: '70%', height: '70vh', overflow: 'auto', backgroundColor: 'white' }}>
                    {modalContent}
                </div>
            </Modal>
        </StyledBox >
    )
});

export default ParseLayout