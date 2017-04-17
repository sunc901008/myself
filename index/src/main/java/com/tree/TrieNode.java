package com.tree;

import java.util.ArrayList;
import java.util.List;

/**
 * creator: sunc
 * date: 2017/4/13
 * description:
 */

public class TrieNode {

    List<TrieNode> next;

    List<ValueInfo> valueInfo;

    String nodeName;

    //  节点id
//    String nodeId;
    //  父节点id
//    String parentId;

    /**
     * 当前TrieNode状态 ,默认 0 , 1表示从根节点到当前节点的路径为一个词,即叶子节点
     */
    int nodeState = 0;

    final List<Float> maxScore;

    TrieNode() {
        this("");
//        nodeId = UUID.randomUUID().toString().replaceAll("-", "");
//        parentId = "";
    }

    TrieNode(String nodeName) {
        this.next = null;
        this.nodeState = 0;
        this.maxScore = new ArrayList<>();
        this.valueInfo = null;
        this.nodeName = nodeName;
//        nodeId = UUID.randomUUID().toString().replaceAll("-", "");
//        parentId = "";
    }

}