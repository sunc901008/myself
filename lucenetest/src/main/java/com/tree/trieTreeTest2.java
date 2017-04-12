package com.tree;

/**
 * creator: sunc
 * date: 2017/4/12
 * description:
 */
public class trieTreeTest2 {
    public static void trieTreeTest2() {
        String[] strs = {"banana", "band", "bee", "absolute", "acm"};
        String[] prefix = {"ba", "b", "band", "abc"};
        TrieTree tree = new TrieTree();
        TrieTreeNode root = new TrieTreeNode();

        for (String s : strs) {
            tree.createTrie(root, s);
        }
        System.out.println();
//        for (String pre : prefix) {
//            int num = tree.findCount(root, pre);
//            System.out.println(pre + " " + num);
//        }


    }

    public static void main(String[] agrs) {
        trieTreeTest2();
    }
}