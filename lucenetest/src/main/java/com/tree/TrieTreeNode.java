package com.tree;

/**
 * 字典树的Java实现。实现了插入、查询以及深度优先遍历.
 */
class TrieTreeNode {
    final int MAX_SIZE = 26;

    char ch; //记录该字符
    int nCount;//记录该字符出现次数
    TrieTreeNode[] child;

    public TrieTreeNode() {
        nCount = 1;
        child = new TrieTreeNode[MAX_SIZE];
    }

}