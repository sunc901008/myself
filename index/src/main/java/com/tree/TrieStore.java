package com.tree;

import com.commons.FileOperate;
import io.vertx.core.json.JsonArray;
import org.xerial.snappy.Snappy;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * creator: sunc
 * date: 2017/4/17
 * description:
 */
class TrieStore {

    static long store(String path) {
        Date start = new Date();
        JsonArray jsonArray = nodeToJsonArray();
        Date end = new Date();
        System.out.println("construct json : " + (end.getTime() - start.getTime()));
        File file = new File(path);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(path);
            fos.write(Snappy.compress(jsonArray.toString()));
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Date end1 = new Date();
        return end1.getTime() - start.getTime();
    }

    private static JsonArray nodeToJsonArray() {
        List<TrieNode> list = IndexTrieMain.getAllNodesBFS();
        JsonArray jsonArray = new JsonArray();
        System.out.println(list.size() + " nodes will be stored.");
        for (TrieNode node : list) {
            jsonArray.add(node.toJsonObject());
        }
        return jsonArray;
    }

    static TrieNode restore(String path) {
        JsonArray jsonArray = null;
        try {
            jsonArray = new JsonArray(new String(Snappy.uncompress(FileOperate.toByteArray(path))));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (jsonArray == null) {
            return new TrieNode();
        }
        return jsonArrayToNode(jsonArray);
    }

    private static TrieNode jsonArrayToNode1(JsonArray jsonArray) {
        TrieNode root = TrieNode.toTrieNode(jsonArray.getJsonObject(0));

        Queue<TrieNode> queue = new LinkedList<>();
        Queue<TrieNode> queueNext = new LinkedList<>();
        queue.offer(root);

        for (int i = 1; i < jsonArray.size(); i++) {
            TrieNode parentNode = queue.poll();
            if (queueNext.isEmpty()) {
                for (; i < jsonArray.size(); i++) {
                    TrieNode node = TrieNode.toTrieNode(jsonArray.getJsonObject(i));
                    if (parentNode.nodeId.equals(node.parentId)) {
                        parentNode.next.add(node);
                        queue.offer(node);
                    } else {
                        parentNode = queue.poll();
                    }
                }
                if (!queueNext.isEmpty()) {
                    i--;
                }
            }
            while (!queueNext.isEmpty()) {
                TrieNode childNode = queueNext.poll();
                if (parentNode.nodeId.equals(childNode.parentId)) {
                    parentNode.next.add(childNode);
                    queue.offer(childNode);
                } else {
                    parentNode = queue.poll();
                }
            }
        }
        return root;
    }

    private static TrieNode jsonArrayToNode(JsonArray jsonArray) {
        TrieNode root = TrieNode.toTrieNode(jsonArray.getJsonObject(0));

        Queue<TrieNode> queue = new LinkedList<>();
        Queue<TrieNode> parentQueue = new LinkedList<>();
        Queue<TrieNode> childQueue = new LinkedList<>();
        parentQueue.offer(root);
        for (int i = 1; i < jsonArray.size(); i++) {
            queue.offer(TrieNode.toTrieNode(jsonArray.getJsonObject(i)));
        }

        TrieNode parent = parentQueue.poll();

        System.out.println(jsonArray.size() + " nodes will be restored.");
        int i = 1;
        while (!queue.isEmpty()) {
            if (i % 10000 == 0) {
                System.out.println(i + " nodes have been restored.");
            }
            TrieNode node = queue.peek();
            if (parent.nodeId.equals(node.parentId)) {
                parent.next.add(node);
                childQueue.offer(node);
                queue.poll();
            } else {
                if (parentQueue.isEmpty()) {
                    while (!childQueue.isEmpty()) {
                        parentQueue.offer(childQueue.poll());
                    }
                }
                parent = parentQueue.poll();
            }
            i++;
        }


        return root;
    }
}
