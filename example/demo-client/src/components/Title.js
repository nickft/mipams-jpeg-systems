import { Typography } from '@mui/material'
import React from 'react'
import { Link } from 'react-router-dom';
import { styled } from '@mui/material/styles';

const TitleWrapper = styled('div')(({ theme }) => ({
    backgroundColor: 'aliceblue',
    width: '100%',
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'center',
    marginBottom: theme.spacing(10),
    color: 'black'
}));


const Title = () => {
    return (
        <TitleWrapper>
            <Link to="/" style={{ textDecoration: 'none', color: 'black' }}>
                <Typography variant="h3">
                    Welcome to Mipams JUMBF
                </Typography>
            </Link>
        </TitleWrapper>
    )
}

export default Title