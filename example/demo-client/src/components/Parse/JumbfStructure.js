import React from 'react'

import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import ChevronRightIcon from '@mui/icons-material/ChevronRight';

import { styled } from '@mui/material/styles';

import TreeItem from '@mui/lab/TreeItem';
import TreeView from '@mui/lab/TreeView';

import BmffBoxLabel from './BmffBoxLabel';
import { Box } from '@mui/system';
import { IconButton, Typography } from '@mui/material';
import OpenInNewOutlinedIcon from '@mui/icons-material/OpenInNewOutlined';

import { getRandomInt } from '../../utils/helpers';



function isJumbf(bmffBox) {
    return bmffBox['descriptionBox'];
}

const StyledTreeView = styled(TreeView)(({ theme }) => ({
    margin: theme.spacing(2),
}));

const JumbfStructure = (props) => {

    const { jumbfStructure, expandedList, handleToggle, addModalContent } = props;

    function getLeafBmffNode(id, bmffNode) {

        const arr = ['boxSize', 'requestable', 'lbox', 'tbox', 'xboxEnabled', 'xlBox', 'payloadSizeFromBmffHeaders', 'boxSizeFromBmffHeaders', 'labelWithEscapeCharacter', 'typeId', 'type', 'randomFileName', 'mediaTypeSize', 'fileNameSize', 'toggleSize'];

        id += 1
        const parentId = id;

        const internalFieldLayout = Object.keys(bmffNode).filter(key => !(arr.includes(key))).map(key => {

            id += 1

            let label = null;

            if (key === 'content') {

                // const text = JSON.stringify(atob(bmffNode['' + key + '']));

                const text = atob(bmffNode['' + key + '']);

                label = <Box>
                    {key}
                    : "view here"
                    <IconButton onClick={() => addModalContent(text)}>
                        <OpenInNewOutlinedIcon />
                    </IconButton>
                </Box>
            } else if (key === 'privateField') {
                const objectNode = bmffNode['' + key + ''];
                if (!objectNode) {
                    label = key + ": null";
                }
                else if (objectNode.type === 'priv') {
                    label = key + ": Superbox with type 'priv' and " + objectNode.contentBoxList.length + " contents";
                } else {
                    label = key + ": BMFF Box with type '" + objectNode.type + "'";
                }
            } else if (key === 'mediaType') {
                label = key + ": " + bmffNode['' + key + ''].type + "/" + bmffNode['' + key + ''].subtype;
            } else {
                label = key + ": " + bmffNode['' + key + ''];
            }

            return (<TreeItem
                key={id}
                nodeId={id.toString()}
                expanded={expandedList}
                label={<Typography variant="h6" sx={{ fontWeight: "400 !important" }}>
                    {label}
                </Typography>}
            />);

        })

        const output = (
            <TreeItem
                key={id}
                nodeId={parentId.toString()}
                expanded={expandedList}
                label={<BmffBoxLabel bmffNode={bmffNode} expandedList={expandedList} />}
            >
                {internalFieldLayout}
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

                let paddingBoxNode = null;

                if (box['paddingBox']) {

                    const sId = getRandomInt(1000);

                    paddingBoxNode = <TreeItem
                        expanded={expandedList}
                        key={sId}
                        nodeId={sId.toString()}
                        label={<BmffBoxLabel bmffNode={box['paddingBox']} />}
                    />;
                }



                output = <TreeItem
                    key={id}
                    nodeId={id.toString()}
                    expanded={[]}
                    label={<BmffBoxLabel bmffNode={box} expandedList={expandedList} />}
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
                    {paddingBoxNode}
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


    return (
        <StyledTreeView
            aria-label="controlled"
            defaultCollapseIcon={<ExpandMoreIcon />}
            defaultExpandIcon={<ChevronRightIcon />}
            expanded={expandedList}
            onNodeToggle={handleToggle}
        >
            {getTreeItemsForBmffList(0, jumbfStructure).map(object => (object.output))}
        </StyledTreeView>
    )
};

export default JumbfStructure