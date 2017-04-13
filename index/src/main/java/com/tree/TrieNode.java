package com.tree;

import java.util.ArrayList;
import java.util.List;

/**
 * creator: sunc
 * date: 2017/4/13
 * description:
 */

class TrieNode {
    /**
     * 子节点个数
     */
    int prefixCount;

    final List<TrieNode> next;

    final List<ValueInfo> valueInfo;

    String nodeName;

    /**
     * 当前TrieNode状态 ,默认 0 , 1表示从根节点到当前节点的路径为一个词,即叶子节点
     */
    int nodeState = 0;

    final List<Float> maxScore;

    TrieNode() {
        prefixCount = 0;
        next = new ArrayList<>();
        nodeState = 0;
        maxScore = new ArrayList<>();
        valueInfo = new ArrayList<>();
        nodeName = "";
    }
}