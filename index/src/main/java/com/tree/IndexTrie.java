package com.tree;


import com.commons.Commons;

import java.io.*;
import java.util.*;

class IndexTrie {

    // 是否锁定(重建和更新index的时候会锁定，操作完毕后解锁)
    private int lock = Commons.UNLOCK;

    // trie树根
    TrieNode root = new TrieNode();

    //英文字符串正则匹配
//    private static final String englishPattern = "^[A-Za-z]+$";

    //中文正则匹配
//    private static String chinesePattern = "[\u4e00-\u9fa5]";

    IndexTrie() {
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
            int type = valueInfo.getType();
            boolean bool = false;//表示是否已经存了该信息. false 表示尚未存储
            for (ValueInfo v : node.valueInfo) {
                if (v.getTable().equals(table)
                        && v.getColumn().equals(column)
                        && v.getContent().equals(content)
                        && v.getType() == type) {
                    bool = true;
                }
            }
            if (!bool) {
                node.valueInfo.add(valueInfo);
            }
        } else {
            String str = word.substring(0, 1);
            boolean bool = true;
            for (TrieNode t : node.next) {
                if (t.nodeName.equals(str)) {
                    bool = false;
                    addWord(t, word.substring(1), valueInfo);
                    break;
                }
            }
            if (bool) {
                TrieNode trieNode = new TrieNode(str, node.nodeId);
                node.next.add(trieNode);
                // 添加下一个字符
                addWord(trieNode, word.substring(1), valueInfo);
            }
        }
    }

//    private void addWordFor(TrieNode node, String word, ValueInfo valueInfo) {
//        for (char c : word.toCharArray()) {
//            String str = String.valueOf(c);
//            boolean bool = true;
//            for (TrieNode t : node.next) {
//                if (t.nodeName.equals(str)) {
//                    bool = false;
//                    node = t;
//                    break;
//                }
//            }
//            if (bool) {
//                TrieNode trieNode = new TrieNode(str, node.nodeId);
//                node.next.add(trieNode);
//                // 添加下一个字符
//                node = trieNode;
//            }
//        }
//        node.nodeState = 1;//添加完毕，设置为叶子节点
//        String table = valueInfo.getTable();
//        String column = valueInfo.getColumn();
//        String content = valueInfo.getContent();
//        String type = valueInfo.getType();
//        boolean bool = false;//表示是否已经存了该信息. false 表示尚未存储
//        for (ValueInfo v : node.valueInfo) {
//            if (v.getTable().equals(table)
//                    && v.getColumn().equals(column)
//                    && v.getContent().equals(content)
//                    && v.getType().equals(type)) {
//                bool = true;
//            }
//        }
//        if (!bool) {
//            node.valueInfo.add(valueInfo);
//        }
//    }

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
        List<String> list = new ArrayList<>();
        for (ValueInfo v : preTraversal(this.root, "")) {
            list.add(v.getContent());
        }
        return list;
    }

    /**
     * 前序遍历
     *
     * @param node   子树根节点
     * @param prefix 查询到该节点前所遍历过的前缀
     */
    private List<ValueInfo> preTraversal(TrieNode node, String prefix) {
        return preTraversal(node, prefix, Integer.MAX_VALUE);
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
        for (TrieNode trieNode : node.next) {
            // //递归调用前序遍历
            list.addAll(getAllNodesDFS(trieNode));
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
            for (TrieNode trieNode : tempNode.next) {
                queue.offer(trieNode);
            }
            list.add(tempNode);
        }
        return list;
    }

    // 前缀搜索 重写
    List<ValueInfo> prefixSearchWordRewrite(String word, int count) {
        if (word == null || "".equals(word.trim())) {
            return new ArrayList<>();
        }
        word = word.toLowerCase();
        List<ValueInfo> list = preTraversal(getNode(word), word, count);
        return list.subList(0, Math.min(count, list.size()));
    }

    /**
     * 前序遍历
     *
     * @param node   子树根节点
     * @param prefix 查询到该节点前所遍历过的前缀
     */
    private List<ValueInfo> preTraversal(TrieNode node, String prefix, int count) {
        List<ValueInfo> list = new ArrayList<>();
        if (node.nodeState == 1) {// 当前即为一个单词
            int min = Math.min(node.valueInfo.size(), count);
            for (int i = 0; i < min; i++) {
                list.add(node.valueInfo.get(i));
            }
            if (list.size() >= count)
                return list;
            count = count - list.size();
        }

        for (TrieNode trieNode : node.next) {
            // //递归调用前序遍历
            String tempStr = prefix + trieNode.nodeName;
            list.addAll(preTraversal(trieNode, tempStr, count));
            if (list.size() >= count)
                return list;
        }
        return list;
    }

    // 获取某字串的节点,存在则返回该节点，不存在则返回空节点
    private TrieNode getNode(String word) {
        TrieNode node = this.root;
        char[] chs = word.toCharArray();
        for (char ch : chs) {
            String str = String.valueOf(ch);
            boolean bool = false;
            for (TrieNode trieNode : node.next) {
                if (trieNode.nodeName.equals(str)) {
                    node = trieNode;
                    bool = true;
                    break;
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

    int getLock() {
        return lock;
    }

    void setLock(int lock) {
        this.lock = lock;
    }

    long store(String path) {
        Date start = new Date();
        File file = new File(path);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(path);
            Queue<TrieNode> queue = new LinkedList<>();
            queue.offer(this.root);
            TrieNode tempNode;
            int i = 1;
            StringBuilder fileContent = new StringBuilder();
            while (!queue.isEmpty()) {
                tempNode = queue.poll();
                fileContent.append(tempNode.toArray()).append("\n");
                if (i % Commons.LIMIT_SIZE_MAX == 0) {
                    fw.write(fileContent.toString());
                    System.out.println(i + " nodes have been stored.");
                    fileContent.delete(0, fileContent.length());
                }
                for (TrieNode trieNode : tempNode.next) {
                    queue.offer(trieNode);
                }
                i++;
            }
            if (fileContent.length() > 0) {
                fw.write(fileContent.toString());
            }
            System.out.println("all nodes have been stored. nodes count: " + i);
            fw.close();
//            compress(path + ".snappy", path);
        } catch (IOException e) {
            e.printStackTrace();
        }
//        file.delete();
        Date end = new Date();
        return end.getTime() - start.getTime();
    }

    void restore(String path) {
        try {
            File file = new File(path);
            if (!file.exists()) {
                file.createNewFile();
            }
//            uncompress(path + ".snappy", path);
            BufferedReader br = new BufferedReader(new FileReader(path));
            String line;
            TrieNode root = new TrieNode();
            if ((line = br.readLine()) != null) {
                root = TrieNode.toTrieNode(line);
            }

            Queue<TrieNode> queue = new LinkedList<>();
            Queue<TrieNode> parentQueue = new LinkedList<>();
            Queue<TrieNode> childQueue = new LinkedList<>();
            parentQueue.offer(root);

            while ((line = br.readLine()) != null) {
                queue.offer(TrieNode.toTrieNode(line));
                if (queue.size() >= Commons.LIMIT_SIZE_MAX) {
                    break;
                }
            }
            TrieNode parent = parentQueue.poll();

            int i = 1;
            while (!queue.isEmpty()) {
                TrieNode node = queue.peek();
                if (parent.nodeId == node.parentId) {
                    parent.next.add(node);
                    childQueue.offer(node);
                    queue.poll();
                    i++;
                    if (i % Commons.LIMIT_SIZE_MAX == 0) {
                        System.out.println(i + " nodes have been restored.");
                    }

                    if (queue.size() <= Commons.LIMIT_SIZE_MIN) {
                        while ((line = br.readLine()) != null) {
                            queue.offer(TrieNode.toTrieNode(line));
                            if (queue.size() >= Commons.LIMIT_SIZE_MAX) {
                                break;
                            }
                        }
                    }
                } else {
                    if (parentQueue.isEmpty()) {
                        while (!childQueue.isEmpty()) {
                            parentQueue.offer(childQueue.poll());
                        }
                    }
                    parent = parentQueue.poll();
                }
            }
            br.close();
//            file.delete();
            System.out.println("all nodes have been restored. nodes count: " + i);
            this.root = root;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    private static void uncompress(String snappy, String source) throws IOException {
//        File file = new File(source);
//        if (!file.exists()) {
//            file.createNewFile();
//        }
//        String str = new String(Snappy.uncompress(FileOperate.toByteArray(snappy)));
//        FileWriter fw = new FileWriter(source);
//        fw.write(str);
//        fw.close();
//    }
//
//    private static void compress(String snappy, String source) throws IOException {
//        File file = new File(snappy);
//        if (!file.exists()) {
//            file.createNewFile();
//        }
//        FileOutputStream fos = new FileOutputStream(snappy);
//        fos.write(Snappy.compress(FileOperate.readToString(source)));
//        fos.close();
//    }
}