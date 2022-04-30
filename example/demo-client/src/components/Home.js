import { Button, Grid } from '@mui/material'
import React from 'react'

const Action = (props) => {

    return (
        <Button
            size="large"
            variant="contained"
            disabled={props.disabled}
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
            justifyContent="center"
            spacing={6}
            sx={{
                flex: 'auto'
            }}
        >
            <Grid item>
                <Action
                    disabled
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
