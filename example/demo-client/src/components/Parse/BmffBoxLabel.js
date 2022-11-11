import React from 'react'

import { Box, Tooltip, Typography } from '@mui/material';
import InfoOutlinedIcon from '@mui/icons-material/InfoOutlined';
import { contentTypeUuidToText, bmffBoxTypeToText } from '../../utils/description'

function isJumbf(bmffBox) {
    return bmffBox['descriptionBox'];
}

const BmffBoxLabel = (props) => {

    const { bmffNode } = props;

    const tooltipInfo = (isJumbf(bmffNode)) ?
        contentTypeUuidToText[bmffNode['descriptionBox']['uuid']] : bmffBoxTypeToText[bmffNode['type']]

    return (
        <Box sx={{ display: 'flex', alignItems: 'flex-end' }}>
            <Typography variant="h6" style={{ fontWeight: 'bold' }}>
                {bmffNode['type'] + " ( " + bmffNode['boxSize'] + " bytes ) "}
            </Typography>
            <Tooltip sx={{ paddingLeft: '4px' }} title={<Typography variant="body1" style={{ fontWeight: 'bold' }}>{tooltipInfo}</Typography>} placement="right">
                <InfoOutlinedIcon fontSize="large" />
            </Tooltip>
        </Box >
    )
}

export default BmffBoxLabel