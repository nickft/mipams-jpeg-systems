import { TextField } from '@mui/material'
import React from 'react'

const JsonEditor = (props) => {

    const { jsonValue, setJsonRequest } = props;
    return (
        <TextField
            multiline
            label="JUMBF Structure"
            sx={{
                width: '100%'
            }}
            value={jsonValue}
            onChange={(e) => setJsonRequest(e.target.value)}
        >
            File Uploading
        </TextField>
    )
}

export default JsonEditor