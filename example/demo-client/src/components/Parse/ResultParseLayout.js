import { Button, Grid, Paper, Typography } from '@mui/material';
import React from 'react';
import Stack from '@mui/material/Stack'
import styled from '@emotion/styled';
import JumbfStructure from './JumbfStructure';
import { getBaseURL } from '../../utils/api';

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

const ResultParseLayout = (props) => {

    const {
        jumbfStructure,
        addModalContent,
        expandedList,
        handleToggle,
        parsedFileName,
        uploadedFileName,
        loading,
    } = props;

    let extractButton = (uploadedFileName.name.endsWith(".jumbf")) ?
        <Button
            variant="contained"
            disabled
            sx={{
                '& .a': { textDecoration: 'none', color: 'white' },
                height: '100%',
                backgroundColor: '#4DF132',
                width: '50%',
            }}
        >
            Extract JUMBF File
        </Button> : <a
            href={getBaseURL(`demo/extractJumbf?fileName=${parsedFileName}`)}
            download={"jumbf-standalone.jumbf"}
            style={{ textDecoration: 'none', width: "50%" }}
        >
            <Button
                variant="contained"
                disabled={loading || uploadedFileName.name.endsWith(".jumbf")}
                sx={{
                    '& .a': { textDecoration: 'none', color: 'white' },
                    height: '100%',
                    backgroundColor: '#4DF132',
                    width: '100%',
                }}
            >
                Extract JUMBF File
            </Button>
        </a>

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
                    height: '50vh',
                    flex: 'auto',
                    overflowY: 'auto',
                    overflowX: 'auto',
                }}>
                <JumbfStructure
                    jumbfStructure={jumbfStructure}
                    expandedList={expandedList}
                    handleToggle={handleToggle}
                    addModalContent={addModalContent}
                />
            </Paper>

            <Grid container justifyContent="space-between" sx={{ width: "100%" }}>
                <MipamsParseOptionLeft item xs={12} md={6}>
                    <Button
                        variant="contained"
                        disabled={loading}
                        href="/jumbf/parse"
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
                    {extractButton}
                </MipamsParseOptionRight>
            </Grid>
        </MipamsStack>
    )
}

export default ResultParseLayout