import { Box, IconButton, List, ListItem, styled, Tooltip } from '@mui/material'
import React from 'react'
import CloseIcon from '@mui/icons-material/Close';
import AddCircleRoundedIcon from '@mui/icons-material/AddCircleRounded';

const StyledBox = styled(Box)(({ theme }) => ({
    padding: theme.spacing(1),
}));

const FileList = (props) => {

    const { fileList, onAddFileWithId, onDeleteFileWithId, onFileFieldChange } = props;


    const fileListItems = fileList.map(fileField => (
        <ListItem key={fileField.id} >
            <Box sx={{ display: 'flex', width: '90%', borderBottom: 'solid #E0E0E0 thin' }}>
                <StyledBox sx={{ flexGrow: 1 }}>
                    <input type="file" onChange={(event) => onFileFieldChange(fileField.id, event)} />
                </StyledBox>
                <Box sx={{ flexGrow: 0 }}>
                    <IconButton
                        aria-label="close"
                        color="inherit"
                        size="small"
                        onClick={() => onDeleteFileWithId(fileField.id)}
                    >
                        <CloseIcon fontSize="inherit" />
                    </IconButton>
                </Box>
            </Box>
        </ListItem>
    ))


    return (
        <React.Fragment>
            <List>
                {fileListItems}
            </List>
            <Box flexGrow={1} display="flex" justifyContent="center">
                <Tooltip title="Add file">
                    <IconButton
                        aria-label="close"
                        color="inherit"
                        size="small"
                        onClick={() => onAddFileWithId()}
                    >
                        <AddCircleRoundedIcon sx={{ color: 'green' }} fontSize='medium' />
                    </IconButton>
                </Tooltip>
            </Box>
        </React.Fragment>
    )
}

export default FileList