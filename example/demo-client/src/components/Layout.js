import React from 'react'

import { styled } from '@mui/material/styles';
import { Outlet } from 'react-router-dom'

import Title from './Title';
import Footer from './Footer';

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
            <Outlet />
            <Footer />
        </LayoutStyle>
    )
}