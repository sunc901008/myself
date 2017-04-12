package com.tree;


import java.util.ArrayList;
import java.util.List;

public class WordTrie {

    private static final int ARRAY_LENGTH = 26;

    private static final String zeroString = "";

    class TrieNode {
        /**
         * trie tree word count
         */
        int count;

        /**
         * trie tree prefix count
         */
        int prefixCount;

        final List<TrieNode> next;

        ValueInfo valueInfo;

        String nodeName;

        /**
         * 当前TrieNode状态 ,默认 0 , 1表示从根节点到当前节点的路径表示一个词,即叶子节点
         */
        int nodeState = 0;

        final float maxScore;

        TrieNode() {
            count = 0;
            prefixCount = 0;
            next = new ArrayList<>();
            nodeState = 0;
            maxScore = 10.0f;
            valueInfo = null;
            nodeName = "";
        }
    }

    /**
     * trie树根
     */
    private TrieNode root = new TrieNode();

    /**
     * 英文字符串正则匹配
     */
    private static final String englishPattern = "^[A-Za-z]+$";

    /**
     * 中文正则匹配
     */
//    private static String chinesePattern = "[\u4e00-\u9fa5]";
    public void addWord(String word) {
        if (word == null || "".equals(word.trim())) {
            throw new IllegalArgumentException("word can not be null!");
        }
        word = word.toLowerCase();
        addWord(root, word);
    }

    private void addWord(TrieNode node, String word) {
        if (word.length() == 0) { // if all characters of the word has been
            node.count++;
            node.nodeState = 1;
        } else {
            node.prefixCount++;
            String str = word.substring(0, 1);
            List<TrieNode> children = node.next;
            TrieNode trieNode = new TrieNode();
            trieNode.nodeName = str;
            boolean bool = true;
            for (TrieNode t : children) {
                if (t.nodeName.equals(str)) {
                    bool = false;
                    trieNode = t;
                    addWord(trieNode, word.substring(1));
                    break;
                }
            }
            if (bool) {
                children.add(trieNode);
                // go the the next character
                addWord(trieNode, word.substring(1));
            }
        }
    }

    public List<String> prefixSearchWord(String word) {
        if (word == null || "".equals(word.trim())) {
            return new ArrayList<>();
        }
        word = word.toLowerCase();
        String str = word.substring(0, 1);

        TrieNode trieNode = new TrieNode();
        List<TrieNode> children = root.next;
        for (TrieNode t : children) {
            if (t.nodeName.equals(str)) {
                trieNode = t;
                break;
            }
        }

        if (!"".equals(trieNode.nodeName)) {
            return depthSearch(trieNode, new ArrayList<>(), word.substring(1), str);
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * @Description: 搜索单词, 以a-z为根,分别向下递归搜索
     */
    public List<String> searchWord(String word) {
        if (word == null || "".equals(word.trim())) {
            return new ArrayList<>();
        }
        char c = word.charAt(0);
        c = Character.toLowerCase(c);
        int index = c - 'a';
        List<String> list = new ArrayList<>();
        for (int i = 0; i < ARRAY_LENGTH; i++) {
            int j = 'a' + i;
            char temp = (char) j;
            if (root.next.get(i) != null) {
                if (index == i) {
                    fullSearch(root.next.get(i), list, word.substring(1),
                            "" + temp, word);
                } else {
                    fullSearch(root.next.get(i), list, word, "" + temp, word);
                }
            }
        }
        return list;
    }

    /**
     * @param list        保存搜索到的字符串
     * @param word        搜索的单词.匹配到第一个则减去一个第一个,连续匹配,直到word为空串.若没有连续匹配,则恢复到原串。
     * @param matchedWord 匹配到的单词
     * @return List
     * @Description: 匹配到对应的字母, 则以该字母为字根, 继续匹配完所有的单词。
     */
    private List<String> fullSearch(TrieNode node, List<String> list, String word, String matchedWord, String inputWord) {
        if (node.nodeState == 1 && word.length() == 0) {
            list.add(matchedWord);
        }
        if (word.length() != 0) {
            char c = word.charAt(0);
            c = Character.toLowerCase(c);
            int index = c - 'a';
            for (int i = 0; i < ARRAY_LENGTH; i++) {
                if (node.next.get(i) != null) {
                    int j = 'a' + i;
                    char temp = (char) j;
                    if (index == i) {
                        // 连续匹配
                        fullSearch(node.next.get(i), list, word.substring(1), matchedWord + temp, inputWord);
                    } else {
                        // 未连续匹配,则重新匹配
                        fullSearch(node.next.get(i), list, inputWord, matchedWord + temp, inputWord);
                    }
                }
            }
        } else {
            if (node.prefixCount > 0) {
                for (int i = 0; i < ARRAY_LENGTH; i++) {
                    if (node.next.get(i) != null) {
                        int j = 'a' + i;
                        char temp = (char) j;
                        fullSearch(node.next.get(i), list, zeroString, matchedWord
                                + temp, inputWord);
                    }
                }
            }
        }
        return list;
    }

    /**
     * @param list        保存搜索到的字符串
     * @param word        搜索的单词.匹配到第一个则减去一个第一个,连续匹配,直到word为空串.若没有连续匹配,则恢复到原串。
     * @param matchedWord 匹配到的单词
     * @return List<String>
     * @Description: 深度遍历子树
     */
    private List<String> depthSearch(TrieNode node, List<String> list, String word, String matchedWord) {
        if (node.nodeState == 1 && word.length() == 0) {
            list.add(matchedWord);
        }
        List<TrieNode> children = node.next;
        if (word.length() != 0) {
            String str = word.substring(0, 1);

            TrieNode trieNode = new TrieNode();
            for (TrieNode t : children) {
                if (t.nodeName.equals(str)) {
                    trieNode = t;
                    break;
                }
            }

            if (!"".equals(trieNode.nodeName)) {
                depthSearch(trieNode, list, word.substring(1), matchedWord + str);
            }
        } else {
            if (node.prefixCount > 0) {// 若匹配单词结束,但是trie中的单词并没有完全找到,需继续找到trie中的单词结束.
                // node.prefixCount>0表示trie中的单词还未结束
//                for (int i = 0; i < ARRAY_LENGTH; i++) {
//                    if (node.next.get(i) != null) {
//                        int j = 'a' + i;
//                        char temp = (char) j;
//                        depthSearch(node.next.get(i), list, zeroString, matchedWord + temp);
//                    }
//                }
                list.addAll(preTraversal(node, matchedWord));
            }
        }
        return list;
    }

    /**
     * 遍历Trie树
     */
    public List<String> getAllWords() {
        return preTraversal(this.root, "");
    }

    /**
     * 前序遍历。。。
     *
     * @param root    子树根节点
     * @param prefixs 查询到该节点前所遍历过的前缀
     */
    public List<String> preTraversal(TrieNode root, String prefixs) {
        List<String> list = new ArrayList<>();
        if (root.nodeState == 1) {
            // //当前即为一个单词
            list.add(prefixs);
        }

        List<TrieNode> children = root.next;

        for (TrieNode trieNode : children) {
            // //递归调用前序遍历
            String tempStr = prefixs + trieNode.nodeName;
            list.addAll(preTraversal(trieNode, tempStr));
        }
        return list;
    }

    /**
     * 判断某字串是否在字典树中
     *
     * @return true if exists ,otherwise false
     */
    public boolean isExist(String word) {
        return search(this.root, word);
    }

    /**
     * 查询某字串是否在字典树中
     */
    private boolean search(TrieNode root, String word) {
        char[] chs = word.toLowerCase().toCharArray();
        for (char ch : chs) {
            int index = ch - 'a';
            if (root.next.get(index) == null) {
                // /如果不存在，则查找失败
                return false;
            }
            root = root.next.get(index);
        }

        return true;
    }
}