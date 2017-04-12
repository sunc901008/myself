package com.tree;

/**
 * creator: sunc
 * date: 2017/4/12
 * description:
 */

import java.io.*;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;

public class WordTrieMain {

    public static void main(String[] args) {
        test1();
    }

    public static void test1() {
        WordTrie trie = new WordTrie();

        trie.addWord("iii");
        trie.addWord("iiiy");
        trie.addWord("bibe");
        trie.addWord("bibiwer");

        System.out.println("----------------------------------------");
        System.out.println(trie.getAllWords());
        System.out.println(trie.prefixSearchWord("bibi"));
    }

    public static void test() {
        WordTrie trie = new WordTrie();
        trie.addWord("abi");
        trie.addWord("ai");
        trie.addWord("aqi");
        trie.addWord("biiiyou");
        trie.addWord("dqdi");
        trie.addWord("ji");
        trie.addWord("li");
        trie.addWord("li");
        trie.addWord("li");
        trie.addWord("lipi");
        trie.addWord("qi");
        trie.addWord("qibi");
        trie.addWord("i");
        trie.addWord("ibiyzbi");
        List<String> list = trie.prefixSearchWord("li");
        for (String s : list) {
            System.out.println(s);
        }
        System.out.println("----------------------------------------");
        System.out.println(trie.getAllWords());
        System.out.println("----------------------------------------");
        List<String> li = trie.searchWord("i");
        for (String s : li) {
            System.out.println(s);
        }
        System.out.println("----------------------------------------");
        List<String> words = trie.searchWord("bi");
        for (String s : words) {
            System.out.println(s);
        }

        System.out.println("----------------------------------------");
        List<String> lst = trie.searchWord("q");
        for (String s : lst) {
            System.out.println(s);
        }
    }

}

