package com.tree;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * creator: sunc
 * date: 2017/4/13
 * description:
 */

public class TrieNode {

    final List<TrieNode> next;

    final List<ValueInfo> valueInfo;

    String nodeName;

    //  节点id
    String nodeId;
    //  父节点id
    String parentId;

    /**
     * 当前TrieNode状态 ,默认 0 , 1表示从根节点到当前节点的路径为一个词,即叶子节点
     */
    int nodeState = 0;

    final List<Float> maxScore;

    TrieNode() {
        next = new ArrayList<>();
        nodeState = 0;
        maxScore = new ArrayList<>();
        valueInfo = new ArrayList<>();
        nodeName = "";
        nodeId = UUID.randomUUID().toString().replaceAll("-", "");
        parentId = "";
    }

}