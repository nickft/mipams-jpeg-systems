import { Box, Grid, Paper, Tooltip, Typography } from '@mui/material'
import React from 'react'

import { styled } from '@mui/material/styles';
import { Button, Alert } from '@mui/material';

import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import ChevronRightIcon from '@mui/icons-material/ChevronRight';
import InfoOutlinedIcon from '@mui/icons-material/InfoOutlined';

import TreeView from '@mui/lab/TreeView';
import TreeItem from '@mui/lab/TreeItem';

import { contentTypeUuidToText, bmffBoxTypeToText } from '../utils/description'

const Input = styled('input')({
    display: 'none',
});

function isJumbf(bmffBox) {
    return bmffBox['descriptionBox'];
}

function getBoxLabel(bmffNode) {

    const tooltipInfo = (isJumbf(bmffNode)) ?
        contentTypeUuidToText[bmffNode['descriptionBox']['uuid']] : bmffBoxTypeToText[bmffNode['type']]

    return (
        <Box sx={{ display: 'flex', alignItems: 'flex-end' }}>
            <Typography style={{ fontWeight: 'bold' }}>
                {bmffNode['type'] + " ( " + bmffNode['boxSize'] + " bytes ) "}
            </Typography>
            <Tooltip sx={{ paddingLeft: '4px' }} title={tooltipInfo} placement="right">
                <InfoOutlinedIcon />
            </Tooltip>
        </Box>
    )
}

function getLeafBmffNode(id, bmffNode) {

    const arr = ['requestable', 'lbox', 'tbox', 'xboxEnabled', 'xbox', 'payloadSizeFromBmffHeaders', 'boxSizeFromBmffHeaders', 'labelWithEscapeCharacter', 'typeId', 'type'];

    id += 1
    const parentId = id;

    const internalFields = Object.keys(bmffNode).filter(key => !(arr.includes(key))).map(key => {

        id += 1

        const label = key + ": " + bmffNode['' + key + ''];

        return (<TreeItem
            key={id}
            nodeId={id.toString()}
            expanded={[]}
            label={label}
        />);

    })

    const output = (
        <TreeItem key={parentId} nodeId={parentId.toString()} expanded={[]} label={getBoxLabel(bmffNode)}>
            {internalFields}
        </TreeItem>
    );

    return { id, output };
}

function getTreeItemsForBmffList(id, bmffList) {

    return bmffList.map(box => {

        let output = null;
        let lastUsedId;

        if (isJumbf(box)) {

            id += 1

            let descriptionInfo = getLeafBmffNode(id, box['descriptionBox']);

            const contentListInfo = getTreeItemsForBmffList(descriptionInfo.id, box['contentBoxList']);
            lastUsedId = contentListInfo[contentListInfo.length - 1].id;

            output = <TreeItem
                key={id}
                nodeId={id.toString()}
                expanded={[]}
                label={getBoxLabel(box)}
                sx={{
                    '& .MuiTreeItem-content': {
                        width: 'inherit',
                        paddingTop: '2px',
                        paddingBottom: '2px'
                    },

                }}
            >
                {descriptionInfo.output}
                {contentListInfo.map(object => (object.output))}
            </TreeItem>

        } else {
            const leafNodeInfo = getLeafBmffNode(id, box)
            output = leafNodeInfo.output;
            lastUsedId = leafNodeInfo.id;
        }

        id += lastUsedId

        return { id, output };

    });
}

const ParseLayout = (props) => {

    const { jumbfStructure, errorMessage, expandedList, handleToggle, loading, handleFileUploadChange, onFileUploadClick } = props;

    let output;

    if (errorMessage) {
        const errorMessageView = <Alert
            severity="error"
            sx={{
                flex: '0'
            }}
        >
            Error parsing the file: {errorMessage}
        </Alert>
        output = errorMessageView
    } else if (jumbfStructure) {
        const jumbfTreeView =
            <Paper
                elevation={3}
                sx={{
                    flex: 'auto',
                    overflowY: 'auto',
                    overflowX: 'auto'
                }}
            >
                <TreeView
                    aria-label="controlled"
                    defaultCollapseIcon={<ExpandMoreIcon />}
                    defaultExpandIcon={<ChevronRightIcon />}
                    expanded={expandedList}
                    onNodeToggle={handleToggle}
                >
                    {getTreeItemsForBmffList(0, jumbfStructure).map(object => (object.output))}
                </TreeView>
            </Paper>
        output = jumbfTreeView
    } else {
        output = null;
    }


    return (
        <Grid
            container
            flexDirection="column"
            alignContent="center"
            justifyContent="flex-start"
            spacing={4}
            sx={{
                flex: 'auto'
            }}
        >
            <Grid
                item
                sx={{
                    display: 'flex',
                    justifyContent: 'center'
                }}
            >
                <label htmlFor="contained-button-file">
                    <Input accept=".jumbf" id="contained-button-file" multiple type="file" onChange={handleFileUploadChange} />
                    <Button disabled={loading} variant="contained" component="span" onClick={onFileUploadClick}>
                        Upload JUMBF File
                    </Button>
                </label>
            </Grid>
            {output &&
                <Grid
                    item
                    flexDirection="column"
                    sx={{
                        flex: 'auto',
                        display: 'flex',
                        width: '50vw',
                        maxHeight: '60vh',
                    }}
                >
                    {output}
                </Grid>}
        </Grid>
    )
}

export default ParseLayout