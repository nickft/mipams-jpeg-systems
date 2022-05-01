import { Button, Grid } from '@mui/material'
import React from 'react'

const Action = (props) => {

    return (
        <Button
            size="large"
            variant="contained"
            href={props.href}
        >{props.title}</Button>
    )
}


export default function Home() {

    return (
        <Grid
            container
            alignItems="center"
            direction="row"
            justifyContent="space-around"
            spacing={6}
            sx={{
                flex: 'auto'
            }}
        >
            <Grid item>
                <Action
                    title="Generate JUMBF"
                    href="/generate" />
            </Grid>
            <Grid item>
                <Action
                    title="Parse JUMBF"
                    href="/parse" />
            </Grid>
        </Grid>
    )
}
