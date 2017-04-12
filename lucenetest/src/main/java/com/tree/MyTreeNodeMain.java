package com.tree;

/**
 * creator: sunc
 * date: 2017/4/12
 * description:
 */
public class MyTreeNodeMain {

    public static void main(String []args){
        MyTreeNode myTreeNode = new MyTreeNode();
        myTreeNode.addChildNode(new MyTreeNode("name1", 10));
        myTreeNode.addChildNode(new MyTreeNode("name2", 20));

        myTreeNode.traverse();

    }

}
