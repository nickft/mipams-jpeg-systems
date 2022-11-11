import React from 'react'
import AppBar from '@mui/material/AppBar';
import Box from '@mui/material/Box';
import Toolbar from '@mui/material/Toolbar';
import Typography from '@mui/material/Typography';
import IconButton from '@mui/material/IconButton';
import styled from '@emotion/styled';
import { Avatar } from '@mui/material';
import { Outlet } from 'react-router';

const MipamsAppBar = styled(AppBar)(({
    backgroundColor: '#295264',
}));

const MipamsToolbar = styled(Toolbar)(({ theme }) => ({
    justifyContent: 'space-between',
    [theme.breakpoints.up('lg')]: {
        maxWidth: `${theme.breakpoints.values['lg']}px`,
        marginLeft: 'auto',
        marginRight: 'auto',
        width: '100%',
    },
}));

const MipamsContent = styled('div')(({ theme }) => ({
    [theme.breakpoints.up('lg')]: {
        maxWidth: `${theme.breakpoints.values['lg']}px`,
        marginLeft: 'auto',
        marginRight: 'auto',
        width: '100%',
    },
}));

export default function Layout() {
    return (
        <Box sx={{ flexGrow: 1 }}>
            <MipamsAppBar position="static">
                <MipamsToolbar>
                    <Typography
                        variant="h3"
                        href="/jumbf"
                        component="a"
                        sx={{
                            color: 'inherit',
                            textDecoration: 'none',
                        }}
                    >
                        MIPAMS
                    </Typography>
                    <IconButton disabled size="large">
                        <Avatar alt="UPC Logo" src="/jumbf/upc.png" sx={{
                            width: '64px',
                            height: '64px',
                        }} />
                    </IconButton>
                </MipamsToolbar>
            </MipamsAppBar>
            <MipamsContent>
                <Outlet />
            </MipamsContent>
        </Box>
    )
}