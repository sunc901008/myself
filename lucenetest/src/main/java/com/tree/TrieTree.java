package com.tree;

import java.util.HashMap;
import java.util.Map;

/**
 * 字典树的Java实现。实现了插入、查询以及深度优先遍历.
 */
class TrieTree {

    //字典树的插入和构建
    public void createTrie(TrieTreeNode node, String str) {
        if (str == null || str.length() == 0) {
            return;
        }
        char[] letters = str.toCharArray();
        for (char letter : letters) {
            int pos = letter - 'a';
            if (node.child[pos] == null) {
                node.child[pos] = new TrieTreeNode();
            } else {
                node.child[pos].nCount++;
            }
            node.ch = letter;
            node = node.child[pos];
        }
    }

    //字典树的查找
    public int findCount(TrieTreeNode node, String str) {
        if (str == null || str.length() == 0) {
            return -1;
        }
        char[] letters = str.toCharArray();
        for (char letter : letters) {
            int pos = letter - 'a';
            if (node.child[pos] == null) {
                return 0;
            } else {
                node = node.child[pos];
            }
        }
        return node.nCount;
    }

}