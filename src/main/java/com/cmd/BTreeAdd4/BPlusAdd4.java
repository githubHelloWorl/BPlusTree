package com.cmd.BTreeAdd4;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class BPlusAdd4<V, K extends Comparable<K>> {
    // 阶数
    private int order;
    // 根节点
    private Node<V, K> root;

    // 构造函数
    public BPlusAdd4() {
        this(3);
    }

    public BPlusAdd4(int order) {
        this.order = order;
        root = new LeafNode<V, K>();
    }

    // 打印
    public void print(){
        printTree(root, 0);
    }

    // 打印树形
    public void print1(){
        printTree1(root);
    }

    private void printTree(Node<V, K> node, int level){
        System.out.println("Level: " + level + ": " + node.printKeys() + ", " + node.printValues());
        for(int i = 0; i < node.number;++i){
            Node<V, K> node1 = node.childs[i];
            if(node1 != null)
                printTree(node1, level+1);
        }
    }

    private void printTree1(Node<V, K> node){
        if(node == null) return;
        Queue<Node<V, K>> queue = new LinkedList<>();
        queue.add(node);
        Node<V, K> nodeFlag = node;
        int level = -1;
        while(queue.size() > 0){
            Node<V, K> nodeTemp = queue.poll();
            if(nodeFlag == nodeTemp){
                ++level;
                if(nodeFlag != root) System.out.println();
                System.out.print("Level: " + level + ": ");
                nodeFlag = null;
            }
            System.out.print(nodeTemp.printKeys() + "," + nodeTemp.printValues() + "  ");
            for(int i = 0; i < nodeTemp.number; ++i){
                Node<V, K> node1 = nodeTemp.childs[i];
                if(node1 != null){
                    if(nodeFlag == null) nodeFlag = node1;
                    queue.add(node1);
                }
            }
        }
    }

    /**
     * 查找
     * @param key
     * @return
     */
    public V find(K key){
        return root.find(key);
    }

    /**
     * 插入
     * @param key
     * @param value
     */
    public void insert(K key, V value){
        // 观察返回值 node,是否为空
        Node<V, K> node = root.insert(key, value);
        root = node != null ? node : root;
    }

    /**
     * 节点
     * @param <V>
     * @param <K>
     */
    abstract class Node<V, K extends Comparable<K>> {
        // 父节点
        protected Node<V, K> parent;
        // 子节点
        protected Node<V, K>[] childs;
        // 键数量
        protected int number;
        // 键
        protected Object[] keys;

        public Node(){
            this.number = 0;
            this.keys = new Object[order + 1];
            this.parent = null;
            this.childs = new Node[order + 1];
        }

        /**
         * 打印键
         * @return
         */
        public String printKeys() {
            return Arrays.toString(Arrays.stream(keys).toList().subList(0, number).toArray());
        }

        /**
         * 打印值
         * @return
         */
        abstract String printValues();

        /**
         * 查找
         * @return
         */
        abstract V find(K key);

        /**
         * 插入
         * @param key
         * @param value
         * @return
         */
        abstract Node<V, K> insert(K key, V value);

        // 更新最大键值
        // 1 首先本节点已更新最大键值
        // 2 根据本节点最大键值,陆续更新父节点的最大键值
        protected void updateMaxKey(Node<V, K> node){
            if(node.parent != null){
                // todo 此处可以使用 Math.max
                node.parent.keys[node.parent.number - 1] = node.keys[node.number - 1];
                updateMaxKey(node.parent);
            }
        }

        // 操作错误,提示
        protected void useError(String msg){
            throw new RuntimeException(msg);
        }
    }

    /**
     * 非叶子节点
     * @param <V>
     * @param <K>
     */
    class BPlusNode<V, K extends Comparable<K>> extends Node<V, K> {

        public BPlusNode(){super();}

        @Override
        String printValues() {
            return "";
        }

        @Override
        V find(K key) {
            int left = 0, right = number - 1;
            while(left <= right){
                int mid = left + (right - left) / 2;
                K temp = (K) keys[mid];
                if(key.compareTo(temp) < 0){
                    right = mid - 1;
                }else if(key.compareTo(temp) > 0){
                    left = mid + 1;
                }else{
                    return childs[mid].find(key);
                }
            }
            for(int i = 0; i < number; ++i)
                if(key.compareTo((K) keys[i]) <= 0)
                    return childs[i].find(key);
            return childs[number - 1].find(key);
        }

        @Override
        Node<V, K> insert(K key, V value) {
            int i = 0;
            Node<V, K> node = null;
            while(i < number){
                if(key.compareTo((K) keys[i]) <= 0){
                    node = childs[i].insert(key, value);
                    break;
                }
                ++i;
            }
            if(i == number)
                node = childs[number - 1].insert(key, value);

            // 判断自身有没有超过度
            if(this.number > order){
                // 设置分割线
                int split = (this.number - 1) / 2;
                // 创建新的非叶子节点
                BPlusNode<V, K> node1 = new BPlusNode<>();
                node1.parent = this.parent;
                for(int j = split + 1; j < this.number; ++j){
                    node1.keys[node1.number] = keys[j];
                    node1.childs[node1.number++] = childs[j];
                    // 更新子节点的父节点
                    childs[j].parent = node1;
                }
                this.number = split + 1;

                // 如果父节点是空
                if(parent == null){
                    BPlusNode<V, K> node2 = new BPlusNode<>();
                    node2.childs[node2.number] = this;
                    node2.keys[node2.number++] = this.keys[this.number - 1];
                    node2.childs[node2.number] = node1;
                    node2.keys[node2.number++] = node1.keys[node1.number - 1];
                    this.parent = node2;
                    node1.parent = node2;
                    return node2;
                }else{
                    // 如果有父节点,则可以在父节点上操作了
                    // 操作小键,从前往后遍历,用于插入数据(因为肯定有数据)
                    for(int k = 0; k < parent.number; ++k){
                        if(((K)this.keys[this.number - 1]).compareTo((K)parent.keys[k]) < 0){
                            for(int j = parent.number; j > k; --j){
                                parent.keys[j] = parent.keys[j - 1];
                                parent.childs[j] = parent.childs[j - 1];
                            }
                            parent.keys[k] = this.keys[this.number - 1];
                            parent.childs[k] = this;
                            ++parent.number;
                            break;
                        }
                    }

                    // 操作大键,从后往前,肯定有相同的键
                    for(int k = parent.number - 1; k >= 0; --k){
                        if(((K)node1.keys[node1.number - 1]).compareTo((K)parent.keys[k]) == 0){
                            parent.childs[k] = node1;
                        }
                    }
                }
            }

            return node;
        }
    }

    /**
     * 叶子节点
     * @param <V>
     * @param <K>
     */
    class LeafNode<V, K extends Comparable<K>> extends Node<V, K> {
        // 保存的值
        private Object[] values;
        // 左侧叶子节点
        private LeafNode<V, K> left;
        // 右侧叶子节点
        private LeafNode<V, K> right;

        public LeafNode(){
            super();
            this.values = new Object[order + 1];
            left = null;
            right = null;
        }

        @Override
        String printValues() {
            return Arrays.toString(Arrays.stream(values).toList().subList(0, number).toArray());
        }

        @Override
        V find(K key) {
            int left = 0, right = number - 1;
            while(left <= right){
                int mid = left + (right - left) / 2;
                K temp = (K) keys[mid];
                if(key.compareTo(temp) < 0){
                    right = mid - 1;
                }else if(key.compareTo(temp) > 0){
                    left = mid + 1;
                }else{
                    return (V) values[mid];
                }
            }
            return null;
        }

        @Override
        Node<V, K> insert(K key, V value) {
            // 查找到对应的位置,并插入
            // 如果是空的,则直接插入,此时注意要修改父节点的最大值
            boolean isMaxKey = false;
            if(number == 0){
                keys[number] = key;
                values[number] = value;
            } else {
                // 从后往前比较
                // 如果最大key值都小于传入的key,则不需要比较了
                if(key.compareTo((K) keys[number - 1]) > 0){
                    keys[number] = key;
                    values[number] = value;
                    // 更新最大值标识
                    isMaxKey = true;
                }else{
                    // 如果不是最大值,则正常比较
                    int i = number - 1;
                    while(i >= 0 && key.compareTo((K) keys[i]) < 0){
                        keys[i + 1] = keys[i];
                        values[i + 1] = values[i];
                        --i;
                    }
                    if(i >= 0 && key.compareTo((K) keys[i]) == 0){
                        useError("不允许有相同的键值");
                    }
                    keys[i + 1] = key;
                    values[i + 1] = value;
                }
            }
            ++number;
            // 逐步更新父节点最大键值,需要在更新number后再更新最大键值
            if(isMaxKey)
                this.updateMaxKey(this);

            // 判断当前节点键数量是否超过阶数,则进行分裂
            if(number > order){
                // 设置分割线
                int split = (this.number - 1) / 2;
                // 创建新的叶子节点
                LeafNode<V, K> node = new LeafNode<>();
                node.parent = this.parent;
                this.right = node;
                node.left = this;
                for(int i = split + 1; i < this.number; ++i){
                    node.keys[node.number] = keys[i];
                    node.values[node.number++] = values[i];
                }
                this.number = split + 1;

                // 如果父节点是空
                if(parent == null){
                    BPlusNode<V, K> node2 = new BPlusNode<>();
                    node2.childs[node2.number] = this;
                    node2.keys[node2.number++] = this.keys[this.number - 1];
                    node2.childs[node2.number] = node;
                    node2.keys[node2.number++] = node.keys[node.number - 1];
                    this.parent = node2;
                    node.parent = node2;
                    return node2;
                }else{
                    // 如果有父节点,则可以在父节点上操作了
                    // 操作小键,从前往后遍历,用于插入数据(因为肯定有数据)
                    for(int i = 0; i < parent.number; ++i){
                        if(((K)this.keys[this.number - 1]).compareTo((K)parent.keys[i]) < 0){
                            for(int j = parent.number; j > i; --j){
                                parent.keys[j] = parent.keys[j - 1];
                                parent.childs[j] = parent.childs[j - 1];
                            }
                            parent.keys[i] = this.keys[this.number - 1];
                            parent.childs[i] = this;
                            ++parent.number;
                            break;
                        }
                    }

                    // 操作大键,从后往前,肯定有相同的键
                    for(int i = parent.number - 1; i >= 0; --i){
                        if(((K)node.keys[node.number - 1]).compareTo((K)parent.keys[i]) == 0){
                            parent.childs[i] = node;
                        }
                    }
                }
            }
            return null;
        }
    }

    public static void main(String[] args) {
        BPlusAdd4<Integer, Integer> b = new BPlusAdd4<>(3);
        b.insert(40, 40);
        b.insert(31, 31);
        b.insert(30, 30);
        b.insert(29, 29);
        System.out.println("查找: " + b.find(1));
        System.out.println("查找: " + b.find(29));
//        for(int i = 100; i > 90; --i){
//            b.insert(i, i);
//        }
//        b.insert(27, 8);
//        b.insert(3, 8);
//        b.insert(9, 8);
//        b.insert(10, 10);
//        b.insert(18, 10);
//        b.insert(17, 10);
//        b.insert(16, 10);
//        b.insert(15, 10);
//        b.insert(1, 1);
//        b.insert(2, 1);
//        b.insert(59, 1);
//        b.insert(58, 58);
//        b.insert(57, 57);
//        b.insert(56, 56);

//        b.print1();

    }
}