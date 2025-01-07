package com.cmd.BTreeAdd3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// B+ 树节点类
class BPlusTreeNode {
    boolean isLeaf; // 标记节点是否是叶子节点
    List<Integer> keys; // 存储键的列表
    List<BPlusTreeNode> children; // 存储子节点的列表
    BPlusTreeNode next; // 仅叶子节点使用，用于指向下一个叶子节点

    // 构造函数，初始化节点
    public BPlusTreeNode(boolean isLeaf) {
        this.isLeaf = isLeaf;
        this.keys = new ArrayList<>();
        this.children = new ArrayList<>();
    }
}

// B+ 树类
public class BPlusTree {
    private BPlusTreeNode root; // 树的根节点
    private final int M; // 每个节点的最大子节点数量
    private final int MIN_KEYS; // 非根节点中的最小键数量

    // 构造函数，初始化 B+ 树
    public BPlusTree(int M) {
        this.M = M;
        this.MIN_KEYS = (M + 1) / 2; // 计算非根节点的最小键数量
        this.root = new BPlusTreeNode(true); // 初始化根节点为叶子节点
    }

    // 插入键值对到 B+ 树
    public void insert(int key) {
        BPlusTreeNode rootNode = root;
        // 如果根节点已满，需进行分裂
        if (rootNode.keys.size() == M - 1) {
            BPlusTreeNode newRoot = new BPlusTreeNode(false);
            newRoot.children.add(rootNode);
            splitChild(newRoot, 0);
            root = newRoot;
            insertNonFull(root, key);
        } else {
            insertNonFull(rootNode, key);
        }
    }

    // 插入键到非满节点
    private void insertNonFull(BPlusTreeNode node, int key) {
        int i = node.keys.size() - 1;

        // 如果是叶子节点
        if (node.isLeaf) {
            // 找到插入位置
            while (i >= 0 && key < node.keys.get(i)) {
                i--;
            }
            node.keys.add(i + 1, key); // 插入键到合适位置
            // 保持叶节点内的有序性
            if (i + 1 > 0) {
                Collections.sort(node.keys);
            }
            // 如果叶节点已满，进行分裂
            if (node.keys.size() > M - 1) {
                splitLeaf(node);
            }
        } else {
            // 查找合适的孩子节点
            while (i >= 0 && key < node.keys.get(i)) {
                i--;
            }
            i++;
            // 如果孩子节点已满，进行分裂
            if (node.children.get(i).keys.size() == M - 1) {
                splitChild(node, i);
                if (key > node.keys.get(i)) {
                    i++;
                }
            }
            // 递归插入到合适的孩子节点
            insertNonFull(node.children.get(i), key);
        }
    }

    // 分裂一个节点的孩子节点
    private void splitChild(BPlusTreeNode parent, int index) {
        BPlusTreeNode fullChild = parent.children.get(index); // 获取已满的孩子节点
        BPlusTreeNode newChild = new BPlusTreeNode(fullChild.isLeaf); // 创建新孩子节点
        int medianIndex = MIN_KEYS - 1; // 计算中位索引

        // 把一半的键和孩子节点移动到新节点
        for (int j = 0; j < MIN_KEYS - 1; j++) {
            newChild.keys.add(fullChild.keys.remove(MIN_KEYS));
        }

        if (!fullChild.isLeaf) {
            for (int j = 0; j < MIN_KEYS; j++) {
                newChild.children.add(fullChild.children.remove(MIN_KEYS));
            }
        } else {
            newChild.next = fullChild.next; // 连接新叶节点
            fullChild.next = newChild;
        }

        parent.keys.add(index, fullChild.keys.remove(medianIndex)); // 把中间键移到父节点
        parent.children.add(index + 1, newChild); // 添加新孩子节点到父节点
    }

    // 分裂叶子节点
    private void splitLeaf(BPlusTreeNode leaf) {
        BPlusTreeNode newLeaf = new BPlusTreeNode(true); // 创建新的叶子节点
        int medianIndex = (M - 1) / 2; // 计算中位索引

        // 把一半的键移动到新叶子节点
        for (int i = medianIndex + 1; i < leaf.keys.size(); i++) {
            newLeaf.keys.add(leaf.keys.get(i));
        }

        // 保持原叶子节点的前半部分
        while (leaf.keys.size() > medianIndex + 1) {
            leaf.keys.remove(leaf.keys.size() - 1);
        }

        // 更新叶子节点之间的链表连接
        newLeaf.next = leaf.next;
        leaf.next = newLeaf;
    }

    // 在 B+ 树中搜索键
    public boolean search(int key) {
        return search(root, key);
    }

    private boolean search(BPlusTreeNode node, int key) {
        int i = 0;
        // 查找键的位置
        while (i < node.keys.size() && key > node.keys.get(i)) {
            i++;
        }

        // 如果找到键，返回 true
        if (i < node.keys.size() && key == node.keys.get(i)) {
            return true;
        }

        // 如果是叶子节点且没找到，返回 false
        if (node.isLeaf) {
            return false;
        } else {
            // 递归在对应的孩子节点中查找
            return search(node.children.get(i), key);
        }
    }

    // 打印 B+ 树，用于调试
    public void print() {
        printTree(root, 0);
    }

    private void printTree(BPlusTreeNode node, int level) {
        System.out.println("Level " + level + ": " + node.keys);
        // 如果不是叶子节点，递归打印孩子节点
        if (!node.isLeaf) {
            for (BPlusTreeNode child : node.children) {
                printTree(child, level + 1);
            }
        }
    }

    public static void main(String[] args) {
//        BPlusTree bPlusTree = new BPlusTree(4); // 构造一个 M = 4 的 B+ 树示例
//
//        // 插入值
//        bPlusTree.insert(10);
//        bPlusTree.insert(20);
//        bPlusTree.insert(5);
//        bPlusTree.insert(6);
//        bPlusTree.insert(12);
//        bPlusTree.insert(30);
//        bPlusTree.insert(7);
//        bPlusTree.insert(17);
//
//        // 打印 B+ 树的结构
//        bPlusTree.print();
//
//        // 搜索键
//        System.out.println("Search 10: " + bPlusTree.search(10)); // 输出 true
//        System.out.println("Search 15: " + bPlusTree.search(15)); // 输出 false

        int[] a = new int[2];
    }
}