//package com.cmd.BTreeAdd2;
//
//import com.cmd.BTreeAdd.BPlusTree;
//
//import java.util.LinkedList;
//import java.util.Objects;
//
//public abstract class BPlusNode<K, V extends Comparable<V>>{
//
//
//    protected  int degree;//阶数
//    protected BPlusNode<K,V> parent;//父节点
//    protected  int keyNum;//关键字个数
//    protected LinkedList<K> keys;//存放数据项的list,K为key
//    protected  boolean isLeaf;//是否为叶子节点
//
//    LinkedList<BPlusNode<K,V>> childList;//存放孩子节点list
//
//    public  BPlusNode(int degree){
//        if (degree<3) throw new IllegalArgumentException("The BPlusTree degree must be at least three!");
//        this.degree = degree;
//        parent=null;
//        keys= new LinkedList<>();
//        childList= new LinkedList<>();
//    }
//
//    //查找
//    abstract BPlusNode<K,V> search(K key);
//
//    //删除
//    abstract BPlusNode<K,V> delete(K key);
//    //插入
//    abstract BPlusNode<K,V> insert(K key,V value);
//
//    //上溢
//    protected  boolean isOverFlow(){
//        return  keyNum >degree;
//    }
//    //下溢
//    protected  boolean isUnderFlow(){
//
//        return  keyNum<Math.ceil(degree/2.0);
//    }
//
//    public V searchData(K key){
//        Objects.requireNonNull(key);
//        var search =(BPlusTree.LeafNode<K, V>) root.search(key);
//        if (search.insertIndex<search.entryKey.size()){
//
//            var  searchEntry = search.entryKey.get(search.insertIndex);
//            if (searchEntry.key().equals(key)) return searchEntry.value();
//        }
//
//        return null;
//    }
//
//}
