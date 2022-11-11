import React from 'react';
import { Button, Grid, Paper, Stack, Typography } from '@mui/material';
import styled from '@emotion/styled';
import { Link } from "react-router-dom";
import { Link as MuiLink } from '@mui/material';


const Action = (props) => {
    return (
        <Link
            to={props.href}
            style={{
                textDecoration: 'none',
                width: "100%",
            }}
        >
            <Button
                variant="contained"
                sx={{
                    backgroundColor: "#E38F8F",
                    height: "12vh",
                    width: "100%",
                }}
            >
                <Typography
                    variant="h6"
                    sx={{
                        color: "white",
                    }}
                >
                    {props.title}
                </Typography>
            </Button >
        </Link >
    )
}

const MipamsHomeOption = styled(Grid)(({
    display: 'flex',
    justifyContent: 'center',
    width: "100%",
    padding: "inherit",
}));

const MipamsHomeOptionLeft = styled(MipamsHomeOption)(({ theme }) => ({
    [theme.breakpoints.up('md')]: {
        paddingRight: theme.spacing(3),
    },
    [theme.breakpoints.down('md')]: {
        paddingBottom: theme.spacing(3),
    },
}));

const MipamsHomeOptionRight = styled(MipamsHomeOption)(({ theme }) => ({
    [theme.breakpoints.up('md')]: {
        paddingLeft: theme.spacing(3),
    },
    [theme.breakpoints.down('md')]: {
        paddingTop: theme.spacing(3),
    },
}));


const MipamsStack = styled(Stack)(({ theme }) => ({
    margin: theme.spacing(3),
}));

export default function Home() {

    return (
        <MipamsStack
            spacing={4}
            sx={{
                margin: 'auto',
            }}
        >
            <Paper
                elevation={1}
                sx={{
                    padding: '8px',
                    flex: 'auto',
                    overflowY: 'auto',
                    overflowX: 'auto',
                }}>
                <Typography variant="body1" gutterBottom>
                    Welcome to MIPAMS JUMBF, an application that allows a user to interact with data expressed in JPEG Universal Multimedia Box Format (JUMBF).
                    This application has been developed in scope of the JPEG Systems Part 10 activities in order to showcase the applicability of UPC's proposal
                    for JUMBF reference software. The mipams-jumbf library is open-source and is located
                    <MuiLink href="https://github.com/DMAG-UPC/mipams-jumbf" target="_blank" rel="noreferrer" sx={{ marginLeft: "6px" }}>
                        here
                    </MuiLink>
                    .
                </Typography>

                <Typography variant="body1" gutterBottom>
                    Click on one of the two following options to proceed:
                </Typography>
            </Paper>
            <Grid container justifyContent="space-between" sx={{ width: "100%" }}>
                <MipamsHomeOptionLeft item xs={12} md={6}>
                    <Action
                        title="Generate JUMBF"
                        href="/generate" />
                </MipamsHomeOptionLeft>
                <MipamsHomeOptionRight item xs={12} md={6}>
                    <Action
                        title="Inspect JUMBF"
                        href="/parse" />
                </MipamsHomeOptionRight>
            </Grid>
        </MipamsStack>
    )
}
