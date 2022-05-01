import { Typography } from '@mui/material'
import React from 'react'
import { Link } from 'react-router-dom';
import { styled } from '@mui/material/styles';

const TitleWrapper = styled('div')(({ theme }) => ({
    backgroundColor: 'aliceblue',
    width: '100%',
    display: 'flex',
    flexGrow: 0,
    height: '10vh',
    flexDirection: 'column',
    alignItems: 'center',
    color: 'black'
}));


const Title = () => {
    return (
        <TitleWrapper>
            <Link to="/" onClick={() => window.location.reload(false)} style={{ textDecoration: 'none', color: 'black' }}>
                <Typography variant="h3">
                    Welcome to Mipams JUMBF
                </Typography>
            </Link>
        </TitleWrapper>
    )
}

export default Title