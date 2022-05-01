import React from 'react'

import { styled } from '@mui/material/styles';

import Parse from '../containers/Parse';
import Generate from '../containers/Generate';

import Title from './Title';
import Footer from './Footer';
import { Grid } from '@mui/material';

const LayoutStyle = styled('div')({
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'center',
    width: '100vw',
    height: '100vh'

});

export default function Layout() {
    return (
        <LayoutStyle>
            <Title />
            <Grid
                container
                spacing={4}
                justifyContent="center"
                sx={{
                    marginTop: 0,
                    height: '85vh',
                    '& .MuiGrid-item': {
                        paddingTop: 0
                    }
                }}
            >
                <Grid item xs={5} sx={{ borderRight: 'solid #E0E0E0 thin' }}>
                    <Generate />
                </Grid>

                <Grid item xs={5}>
                    <Parse />
                </Grid>
            </Grid>
            <Footer />
        </LayoutStyle>
    )
}