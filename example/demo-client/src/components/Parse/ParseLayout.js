import { Box, Paper } from '@mui/material'
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

const ParseLayout = (props) => {

    const { jumbfStructure, errorMessage, setErrorMessage, expandedList, handleToggle, loading, handleFileUploadChange, onFileUploadClick } = props;

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
                    <Input accept=".jumbf" id="contained-button-file" multiple type="file" onChange={handleFileUploadChange} />
                    <Button disabled={loading} variant="contained" component="span" onClick={onFileUploadClick}>
                        Upload JUMBF File
                    </Button>
                </label>
            </StyledBox>
        </StyledBox >
    )
}

export default ParseLayout