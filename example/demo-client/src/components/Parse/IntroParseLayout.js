import { Button, Paper, Skeleton, Typography } from '@mui/material';
import React from 'react';
import Stack from '@mui/material/Stack'
import styled from '@emotion/styled';

const Input = styled('input')({
    display: 'none',
});

const MipamsStack = styled(Stack)(({ theme }) => ({
    margin: theme.spacing(3),
}));

const IntroParseLayout = (props) => {

    const {
        handleFileUploadChange,
        onFileUploadClick,
        loading,
    } = props;

    let placeholder = <React.Fragment>
        <Typography variant="h6" gutterBottom sx={{ fontWeight: 400 }}>
            The application supports the inspection of JUMBF structures that are either
            stored as a standalone file or embedded in a JPEG encoded image.
        </Typography>
        <Typography variant="h6" gutterBottom sx={{ fontWeight: 400 }}>
            Click the button below and upload a file to begin.
        </Typography>
    </React.Fragment>

    if (loading) {
        placeholder = <Skeleton variant="rounded" />;
    }

    return (
        <MipamsStack alignItems="center" spacing={2}>
            <Typography
                variant="h3"
                sx={{
                    textAlign: 'center',
                }}
            >
                Inspecting JUMBF information
            </Typography>

            <Paper
                elevation={1}
                sx={{
                    width: '100%',
                    padding: '8px',
                    flex: 'auto',
                    overflowY: 'auto',
                    overflowX: 'auto',
                }}>
                {placeholder}
            </Paper>

            <div
                sx={{
                    display: 'flex',
                    justifyContent: 'center',
                    height: '5vh'
                }}

            >
                <label htmlFor="contained-button-file">
                    <Input accept="*" id="contained-button-file" multiple type="file" onChange={handleFileUploadChange} />
                    <Button size="large" disabled={loading} variant="contained" component="span" onClick={onFileUploadClick} sx={{ backgroundColor: "#91E1BB" }}>
                        Upload File
                    </Button>
                </label>
            </div>
        </MipamsStack>
    )
}

export default IntroParseLayout