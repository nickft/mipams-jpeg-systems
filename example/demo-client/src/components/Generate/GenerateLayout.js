import { Alert, Button, Card, Grid, IconButton, Link, Modal, Paper, Stack, TextField, Typography } from '@mui/material'
import { styled } from '@mui/material/styles';
import CloseIcon from '@mui/icons-material/Close';
import React from 'react'
import FileList from './FileList';
import JsonEditor from './JsonEditor';
import { getBaseURL } from '../../utils/api';

const ModalStack = styled(Stack)(({ theme }) => ({
    paddingBottom: theme.spacing(2),
    flexGrow: 0,
    width: '100%',
    justifyContent: 'space-between',
}));

const MipamsStack = styled(Stack)(({ theme }) => ({
    margin: theme.spacing(3),
}));

const MipamsParseOption = styled(Grid)(({
    display: 'flex',
    justifyContent: 'center',
    width: "100%",
    padding: "inherit",
}));

const MipamsParseOptionLeft = styled(MipamsParseOption)(({ theme }) => ({
    [theme.breakpoints.up('md')]: {
        paddingRight: theme.spacing(1),
    },
    [theme.breakpoints.down('md')]: {
        paddingBottom: theme.spacing(1),
    },
}));

const MipamsParseOptionRight = styled(MipamsParseOption)(({ theme }) => ({
    [theme.breakpoints.up('md')]: {
        paddingLeft: theme.spacing(1),
    },
    [theme.breakpoints.down('md')]: {
        paddingTop: theme.spacing(1),
    },
}));

const ModalBox = styled(Card)(({ theme }) => ({
    border: 'none',
    overflow: 'auto',
    backgroundColor: 'white',
    padding: theme.spacing(2),
    display: 'flex',
    flexDirection: 'column',
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
        modalContent,
        openModalContent,
        closeModalContent,
        download,
    } = props;

    let errorMessageComponent = (errorMessage) ? <Alert severity="error" onClose={() => setErrorMessage(null)} sx={{ flex: '0' }}>
        Error generating JUMBF: {errorMessage}
    </Alert > : null;

    let buttonsComponent = <Button size="large" disabled={loading} variant="contained" component="span" onClick={openModalContent}>
        {loading ? "Generating..." : "Generate JUMBF File"}
    </Button>

    if (download) {
        buttonsComponent = <Grid container justifyContent="space-between" sx={{ width: "100%" }}>
            <MipamsParseOptionLeft item xs={12} md={6}>
                <Button
                    variant="contained"
                    disabled={loading}
                    href="/jumbf/generate"
                    size="large"
                    sx={{
                        '& .a': { textDecoration: 'none', color: 'white' },
                        height: '100%',
                        backgroundColor: '#EF5858',
                        width: "50%"
                    }}
                >
                    Clear instance
                </Button>
            </MipamsParseOptionLeft>
            <MipamsParseOptionRight item xs={12} md={6}>
                <a
                    href={jumbfFileName ? getBaseURL(`demo/download?targetFile=${jumbfFileName}`) : getBaseURL("/demo/download")}
                    download={jumbfFileName ? jumbfFileName : "test.jumbf"}
                    style={{ textDecoration: 'none', width: "50%" }}
                >
                    <Button
                        variant="contained"
                        color="success"
                        size="large"
                        sx={{
                            '& .a': { textDecoration: 'none', color: 'white' },
                            height: '100%',
                            width: '100%',
                        }}
                    >
                        Download File
                    </Button>

                </a>
            </MipamsParseOptionRight>
        </Grid>
    }

    return (
        <MipamsStack alignItems="center" spacing={2}>
            <Typography
                variant="h3"
                sx={{
                    textAlign: 'center',
                }}
            >
                Generating JUMBF information
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
                <Typography variant="h6" gutterBottom sx={{ fontWeight: 400 }}>
                    The application supports the generation of JUMBF structures by using an intermediate JSON representation that allows a user to describe
                    the contents of a JUMBF Box. Currently, JUMBF Boxes from JPEG Systems Part 5 (i.e., JSON, XML, Embedded File, Contiguous Codestream,
                    UUID Content types) and Part 4 (i.e., Protection and Replacement Content types) are supported.
                </Typography>
                <Typography variant="h6" gutterBottom sx={{ fontWeight: 400 }}>
                    Note: It is possible to define an array of JSON elements in order to store multiple JUMBF boxes in a single file. Visit
                    <Link href="https://github.com/DMAG-UPC/mipams-jumbf/blob/main/example/README.md" target="_blank" rel="noreferrer" sx={{ marginLeft: "6px", marginRight: "6px" }}>
                        here
                    </Link>
                    for a complete list of rules and examples on how to generate a JUMBF file.
                </Typography>
            </Paper>

            <Card sx={{ width: "100%", padding: "8px" }}>
                <Grid
                    container
                    justifyContent="space-between"
                    sx={{
                        width: "100%"
                    }}
                    spacing={2}
                >
                    <Grid item xs={12} md={6}>
                        <Typography variant="h5" gutterBottom sx={{ textAlign: 'center' }}>
                            Step 1. Describe JUMBF Structure
                        </Typography>
                        <JsonEditor
                            readOnly={download || loading}
                            jsonRequest={jsonRequest}
                            setJsonRequest={setJsonRequest}
                        />
                    </Grid>
                    <Grid item xs={12} md={6}>
                        <Typography variant="h5" gutterBottom sx={{ textAlign: 'center' }}>
                            Step 2. Add related content files
                        </Typography>
                        <FileList
                            readOnly={download || loading}
                            fileList={fileList}
                            onAddFileWithId={onAddFileWithId}
                            onDeleteFileWithId={onDeleteFileWithId}
                            onFileFieldChange={onFileFieldChange}
                        />
                    </Grid>
                </Grid>
            </Card>
            {buttonsComponent}
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
                    <MipamsStack direction="column" spacing={4}>
                        <ModalStack direction="row" spacing={3}>
                            <Typography
                                variant="h6"
                                sx={{ textAlign: "center" }}
                            >
                                Optional JUMBF file name
                            </Typography>
                            <IconButton aria-label="delete" size="small" onClick={closeModalContent}>
                                <CloseIcon />
                            </IconButton>
                        </ModalStack>
                        <TextField
                            value={jumbfFileName}
                            label="File name"
                            onChange={(e) => setJumbfFileName(e.target.value)}
                        >
                            File Uploading
                        </TextField>
                        <Button size="large" variant="contained" component="span" onClick={() => { closeModalContent(); generateJumbfFile(); }}>
                            Proceed
                        </Button>
                    </MipamsStack>
                </ModalBox>
            </Modal>
        </MipamsStack >
    )
}

export default GenerateLayout