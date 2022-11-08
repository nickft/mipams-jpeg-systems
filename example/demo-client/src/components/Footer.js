import { Typography } from '@mui/material';
import React from 'react'
import { styled } from '@mui/material/styles';

const FooterWrapper = styled('div')(({ theme }) => ({
    backgroundColor: 'aliceblue',
    width: '100%',
    height: '5vh',
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'center',
    borderTopStyle: 'solid',
    borderTopWidth: 'thin',
    borderTopColor: 'grey',
    paddingTop: theme.spacing(3),
    paddingBottom: theme.spacing(3)
}));


const Footer = () => {
    return (
        <FooterWrapper>
            <Typography variant="p">
                {"For more information visit "}
                <a href="https://github.com/DMAG-UPC/mipams-jumbf" target="_blank" rel="noreferrer">
                    here
                </a>
            </Typography>
        </FooterWrapper>
    )
}

export default Footer