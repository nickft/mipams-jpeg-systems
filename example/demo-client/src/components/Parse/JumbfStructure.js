import React from 'react'

import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import ChevronRightIcon from '@mui/icons-material/ChevronRight';

import { styled } from '@mui/material/styles';

import TreeItem from '@mui/lab/TreeItem';
import TreeView from '@mui/lab/TreeView';

import BmffBoxLabel from './BmffBoxLabel';


function isJumbf(bmffBox) {
    return bmffBox['descriptionBox'];
}

const StyledTreeView = styled(TreeView)(({ theme }) => ({
    margin: theme.spacing(2),
}));

const JumbfStructure = (props) => {

    const { jumbfStructure, expandedList, handleToggle } = props;

    function getLeafBmffNode(id, bmffNode) {

        const arr = ['boxSize', 'requestable', 'lbox', 'tbox', 'xboxEnabled', 'xlbox', 'payloadSizeFromBmffHeaders', 'boxSizeFromBmffHeaders', 'labelWithEscapeCharacter', 'typeId', 'type'];

        id += 1
        const parentId = id;

        const internalFieldLayout = Object.keys(bmffNode).filter(key => !(arr.includes(key))).map(key => {

            id += 1

            const label = key + ": " + bmffNode['' + key + ''];

            return (<TreeItem
                key={id}
                nodeId={id.toString()}
                expanded={expandedList}
                label={label}
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
                console.log(descriptionInfo)

                const contentListInfo = getTreeItemsForBmffList(descriptionInfo.id, box['contentBoxList']);
                lastUsedId = contentListInfo[contentListInfo.length - 1].id;

                console.log(contentListInfo)

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
}

export default JumbfStructure