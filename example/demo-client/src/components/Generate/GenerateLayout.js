import { Alert, Box, Button, Paper, TextField, } from '@mui/material'
import { styled } from '@mui/material/styles';
import React from 'react'
import FileList from './FileList';
import JsonEditor from './JsonEditor';

const StyledBox = styled(Box)(({ theme }) => ({
    margin: theme.spacing(1),
}));

const StyledInnerBox = styled(Box)(({ theme }) => ({
    margin: theme.spacing(2),
}));

const GenerateLayout = (props) => {

    const {
        fileList,
        onAddFileWithId,
        onDeleteFileWithId,
        onFileFieldChange,
        generateJumbfFile,
        loading,
        errorMessage,
        setErrorMessage,
        setJsonRequest,
        jsonRequest,
        jumbfFileName,
        setJumbfFileName,
        download,
        setDownload
    } = props;

    return (
        <StyledBox
            sx={{
                flexGrow: 1,
                display: 'flex',
                flexDirection: 'column',
                height: '80vh'
            }}
        >
            <Paper
                elevation={3}
                sx={{
                    flexGrow: 1,
                    display: 'flex',
                    flexDirection: 'column',
                    height: '70vh'
                }}
            >
                <Box
                    id='generator-id'
                    flexGrow={1}
                    display="flex"
                    flexDirection="column"
                    height='100%'
                >
                    <StyledInnerBox
                        flexGrow={0}
                        sx={{
                            overflowY: 'auto',
                            minHeight: '10px',
                            display: 'flex',
                            flexDirection: 'column',
                            maxHeight: '20vh',
                            border: 'solid thin #009900'
                        }}
                    >
                        <FileList
                            fileList={fileList}
                            onAddFileWithId={onAddFileWithId}
                            onDeleteFileWithId={onDeleteFileWithId}
                            onFileFieldChange={onFileFieldChange}
                        />
                    </StyledInnerBox>
                    <StyledInnerBox
                        flexGrow={4}
                        sx={{
                            overflowY: 'auto',
                            height: '45vh',
                            paddingTop: '5px'
                        }}
                    >
                        <JsonEditor
                            jsonRequest={jsonRequest}
                            setJsonRequest={setJsonRequest}
                        />
                    </StyledInnerBox>
                    <StyledInnerBox>
                        <TextField
                            label="JUMBF File name"
                            sx={{
                                width: '100%'
                            }}
                            value={jumbfFileName}
                            onChange={(e) => setJumbfFileName(e.target.value)}
                        >
                        </TextField>
                    </StyledInnerBox>

                    {errorMessage &&
                        <Alert severity="error" onClose={() => setErrorMessage(null)} sx={{ flex: '0' }}>
                            Error generating the file: {errorMessage}
                        </Alert >
                    }
                </Box>
            </Paper>
            <StyledBox
                flexGrow={0}
                display='flex'
                flexDirection='row'
                justifyContent='center'
                height='5vh'
            >
                {download ?

                    <a
                        href={jumbfFileName ? "http://localhost:8080/demo/download?targetFile=" + jumbfFileName : "http://localhost:8080/demo/download"}
                        download={jumbfFileName ? jumbfFileName : "test.jumbf"}
                        style={{ textDecoration: 'none' }}
                    >
                        <Button
                            variant="contained"
                            color="success"
                            sx={{
                                '& .a': { textDecoration: 'none', color: 'white' },
                                height: '100%'
                            }}
                            onClick={() => { setDownload(false) }}
                        >
                            Download File
                        </Button>

                    </a>
                    :
                    <Button disabled={loading} variant="contained" component="span" onClick={() => generateJumbfFile()}>
                        {loading ? "Generating..." : "Generate JUMBF File"}
                    </Button>
                }
            </StyledBox>
        </StyledBox >
    )
}

export default GenerateLayout