package com.tree;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

class IndexTrie {

    // 是否锁定(重建和更新index的时候会锁定，操作完毕后解锁)
    private boolean lock;

    // trie树根
    TrieNode root = new TrieNode();

    //英文字符串正则匹配
//    private static final String englishPattern = "^[A-Za-z]+$";

    //中文正则匹配
//    private static String chinesePattern = "[\u4e00-\u9fa5]";

    IndexTrie() {
        this.lock = false;
    }

    void addWord(ValueInfo valueInfo) {
        String word = valueInfo.getContent();
        if (word == null || "".equals(word.trim())) {
            throw new IllegalArgumentException("word can not be null!");
        }
        word = word.toLowerCase();
        addWord(root, word, valueInfo);
    }

    private void addWord(TrieNode node, String word, ValueInfo valueInfo) {
        if (word.length() == 0) {
            node.nodeState = 1;//添加完毕，设置为叶子节点
            String table = valueInfo.getTable();
            String column = valueInfo.getColumn();
            String content = valueInfo.getContent();
            String type = valueInfo.getType();
            boolean bool = false;//表示是否已经存了该信息. false 表示尚未存储
            if (node.valueInfo != null) {
                for (ValueInfo v : node.valueInfo) {
                    if (v.getTable().equals(table)
                            && v.getColumn().equals(column)
                            && v.getContent().equals(content)
                            && v.getType().equals(type)) {
                        bool = true;
                    }
                }
            }
            if (!bool) {
                if (node.valueInfo == null)
                    node.valueInfo = new ArrayList<>();
                node.valueInfo.add(valueInfo);
            }
        } else {
            String str = word.substring(0, 1);
            boolean bool = true;
            if (node.next != null) {
                for (TrieNode t : node.next) {
                    if (t.nodeName.equals(str)) {
                        bool = false;
                        addWord(t, word.substring(1), valueInfo);
                        break;
                    }
                }
            }
            if (bool) {
//                trieNode.parentId = node.nodeId;
                TrieNode trieNode = new TrieNode(str);
                if (node.next == null) {
                    node.next = new ArrayList<>();
                }
                node.next.add(trieNode);
                // 添加下一个字符
                addWord(trieNode, word.substring(1), valueInfo);
            }
        }
    }

    // 前缀搜索
//    public List<String> prefixSearchWord(String word) {
//        if (word == null || "".equals(word.trim())) {
//            return new ArrayList<>();
//        }
//        word = word.toLowerCase();
//        String str = word.substring(0, 1);
//
//        TrieNode trieNode = new TrieNode();
//        List<TrieNode> children = root.next;
//        for (TrieNode t : children) {
//            if (t.nodeName.equals(str)) {
//                trieNode = t;
//                break;
//            }
//        }
//
//        if ("".equals(trieNode.nodeName)) {
//            return new ArrayList<>();
//        } else {
//            return depthSearch(trieNode, new ArrayList<>(), word.substring(1), str);
//        }
//    }


    //搜索单词, 以deep=1的节点为根,分别向下递归搜索
//    public List<String> searchWord(String word) {
//        if (word == null || "".equals(word.trim())) {
//            return new ArrayList<>();
//        }
//        word = word.toLowerCase();
//        List<String> list = new ArrayList<>();
//        List<TrieNode> children = root.next;
//        String temp = word.substring(0, 1);
//        for (TrieNode trieNode : children) {
//            String search = word;
//            String firstMatch = trieNode.nodeName;
//            if (firstMatch.equals(temp)) {
//                search = word.substring(1);
//            }
//            list.addAll(fullSearch(trieNode, search, firstMatch, word));
//        }
//        return list;
//    }

//    /**
//     * @param node        当前搜索节点
//     * @param search      搜索的单词.匹配到第一个则减去第一个字符,连续匹配,直到为空.若没有连续匹配,则恢复到原串。
//     * @param matchedWord 匹配到的单词
//     * @param inputWord   要搜索的原单词,当当前分支上搜索不到时用来恢复初始搜索单词
//     * @return list        保存搜索到的字符串
//     * @Description: 匹配到对应的字母, 则以该字母为字根, 继续匹配完所有的单词。
//     */
//    private List<String> fullSearch(TrieNode node, String search, String matchedWord, String inputWord) {
//        List<String> list = new ArrayList<>();
//        if (node.nodeState == 1 && search.length() == 0) {
//            list.add(matchedWord);
//        }
//        List<TrieNode> children = node.next;
//        if (search.length() > 0) {
//            String temp = search.substring(0, 1);
//            for (TrieNode trieNode : children) {
//                String searchNext = search.substring(1);
//                if (!trieNode.nodeName.equals(temp)) {
//                    // 未连续匹配,则重新匹配
//                    search = inputWord;
//                }
//                // 连续匹配
//                list.addAll(fullSearch(trieNode, searchNext, matchedWord + temp, inputWord));
//            }
//        } else {
//            List<String> preTraversal = preTraversal(node, matchedWord);
//            preTraversal.remove(matchedWord); // depthSearch 里已经包含了全匹配单词，所以去除全词
//            list.addAll(preTraversal);
//        }
//        return list;
//    }

//    /**
//     * @param list        保存搜索到的字符串
//     * @param word        搜索的单词.匹配到第一个则减去一个第一个,连续匹配,直到word为空串.若没有连续匹配,则恢复到原串。
//     * @param matchedWord 匹配到的单词
//     * @return List<String>
//     * @Description: 深度遍历子树
//     */
//    private List<String> depthSearch(TrieNode node, List<String> list, String word, String matchedWord) {
//        if (node.nodeState == 1 && word.length() == 0) {
//            list.add(matchedWord);
//        }
//        List<TrieNode> children = node.next;
//        if (word.length() != 0) {
//            String str = word.substring(0, 1);
//            TrieNode trieNode = new TrieNode();
//            for (TrieNode t : children) {
//                if (t.nodeName.equals(str)) {
//                    trieNode = t;
//                    break;
//                }
//            }
//            if (!"".equals(trieNode.nodeName)) {
//                depthSearch(trieNode, list, word.substring(1), matchedWord + str);
//            }
//        } else {
//            // 若匹配单词结束,但是trie中的单词并没有完全找到,需继续找到trie中的单词结束.
//            List<String> preTraversal = preTraversal(node, matchedWord);
//            preTraversal.remove(matchedWord); // depthSearch 里已经包含了全匹配单词，所以去除全词
//            list.addAll(preTraversal);
//        }
//        return list;
//    }

    /**
     * 遍历Trie树,返回所有index
     */
    List<String> getAllWords() {
        return preTraversal(this.root, "");
    }

    /**
     * 前序遍历
     *
     * @param node    子树根节点
     * @param prefixs 查询到该节点前所遍历过的前缀
     */
    private List<String> preTraversal(TrieNode node, String prefixs) {
        List<String> list = new ArrayList<>();
        if (node.nodeState == 1) {// 当前即为一个单词
            list.add(prefixs);
        }

        if (node.next != null) {
            for (TrieNode trieNode : node.next) {
                // //递归调用前序遍历
                String tempStr = prefixs + trieNode.nodeName;
                list.addAll(preTraversal(trieNode, tempStr));
            }
        }
        return list;
    }

    /**
     * 遍历Trie树,返回所有节点,深度优先(DFS)
     */
    List<TrieNode> getAllNodesDFS() {
        return getAllNodesDFS(this.root);
    }

    /**
     * 深度优先
     *
     * @param node 子树根节点
     */
    private List<TrieNode> getAllNodesDFS(TrieNode node) {
        List<TrieNode> list = new ArrayList<>();
        list.add(node);
        if (node.next != null) {
            for (TrieNode trieNode : node.next) {
                // //递归调用前序遍历
                list.addAll(getAllNodesDFS(trieNode));
            }
        }
        return list;
    }

    /**
     * 遍历Trie树,返回所有节点,层级优先(BFS)
     */
    List<TrieNode> getAllNodesBFS() {
        return getAllNodesBFS(this.root);
    }

    /**
     * 层级优先
     *
     * @param node 子树根节点
     */
    private List<TrieNode> getAllNodesBFS(TrieNode node) {
        Queue<TrieNode> queue = new LinkedList<>();
        queue.offer(node);
        TrieNode tempNode;
        List<TrieNode> list = new ArrayList<>();
        while (!queue.isEmpty()) {
            tempNode = queue.poll();
            list.add(tempNode);
            if (tempNode.next != null) {
                for (TrieNode trieNode : tempNode.next) {
                    queue.offer(trieNode);
                }
            }
        }
        return list;
    }

    // 前缀搜索 重写
    List<ValueInfo> prefixSearchWordRewrite(String word, int count) {
        if (word == null || "".equals(word.trim())) {
            return new ArrayList<>();
        }
        word = word.toLowerCase();
        List<ValueInfo> list = preTraversal1(getNode(word), word, count);
        return list.subList(0, Math.min(count, list.size()));
    }

    /**
     * 前序遍历
     *
     * @param node    子树根节点
     * @param prefixs 查询到该节点前所遍历过的前缀
     */
    private List<ValueInfo> preTraversal1(TrieNode node, String prefixs, int count) {
        List<ValueInfo> list = new ArrayList<>();
        if (node.nodeState == 1) {// 当前即为一个单词
            int valueInfoCount = 0;
            if (node.valueInfo != null) {
                valueInfoCount = node.valueInfo.size();
                int min = Math.min(valueInfoCount, count);
                for (int i = 0; i < min; i++) {
                    list.add(node.valueInfo.get(i));
                }
            }
            if (list.size() >= count)
                return list;
            count = count - list.size();
        }

        if (node.next != null) {
            for (TrieNode trieNode : node.next) {
                // //递归调用前序遍历
                String tempStr = prefixs + trieNode.nodeName;
                list.addAll(preTraversal1(trieNode, tempStr, count));
                if (list.size() >= count)
                    return list;
            }
        }
        return list;
    }

//    /**
//     * 前序遍历
//     *
//     * @param node    子树根节点
//     * @param prefixs 查询到该节点前所遍历过的前缀
//     */
//    private List<ValueInfo> preTraversal2(TrieNode node, String prefixs) {
//        List<ValueInfo> list = new ArrayList<>();
//        if (node.nodeState == 1) {// 当前即为一个单词
//            list.addAll(node.valueInfo);
//        }
//
//        List<TrieNode> children = node.next;
//        for (TrieNode trieNode : children) {
//            // //递归调用前序遍历
//            String tempStr = prefixs + trieNode.nodeName;
//            list.addAll(preTraversal2(trieNode, tempStr));
//        }
//        return list;
//    }

    // 判断某字串是否在字典树中
//    public boolean isExist(String word) {
//        word = word.toLowerCase();
//        TrieNode node = getNode(word);
//        return !node.nodeName.equals("") && node.nodeState == 1;
//    }

    // 获取某字串的节点,存在则返回该节点，不存在则返回空节点
    private TrieNode getNode(String word) {
        TrieNode node = this.root;
        char[] chs = word.toCharArray();
        for (char ch : chs) {
            String str = String.valueOf(ch);
            boolean bool = false;
            if (node.next != null) {
                for (TrieNode trieNode : node.next) {
                    if (trieNode.nodeName.equals(str)) {
                        node = trieNode;
                        bool = true;
                        break;
                    }
                }
            }
            if (!bool)
                return new TrieNode();
        }
        return node;
    }

    // 根据nodeId获取节点
//    private TrieNode getNodeById(String nodeId) {
//        return getNodeById(this.root, nodeId);
//    }

//    private TrieNode getNodeById(TrieNode node, String nodeId) {
//        if (node.nodeId.equals(nodeId)) {
//            return node;
//        }
//        List<TrieNode> children = node.next;
//        for (TrieNode trieNode : children) {
//            if (trieNode.nodeId.equals(nodeId)) {
//                node = trieNode;
//                break;
//            }
//        }
//        return node;
//    }

    boolean isLock() {
        return lock;
    }

    void setLock(boolean lock) {
        this.lock = lock;
    }

}