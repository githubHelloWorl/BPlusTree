//package com.cmd.BTreeAdd2;
//
//import java.util.Collections;
//
//public class BPlusNode2 <K, V extends Comparable<V>> extends BPlusNode {
//    //非叶子节点查找,定位到需要插入的叶子节点
//    @Override
//    BPlusNode<K,V> search(K key) {
//        var insertIndex= Collections.binarySearch(keys, key);
//        if (this.isLeaf){
//            return  this;
//        }
//
//        insertIndex= insertIndex>=0?insertIndex:-insertIndex-1;
//        if (childList.size() > 0&&childList.getFirst().keys.size() > 0){
//            return childList.get(Math.min(insertIndex, keys.size()-1)).search(key);
//        }
//
//        return  this;
//
//    }
//
////    //对叶子节点的key使用二分查找
////    @Override
////    LeafNode<K, V> search(K key)   {
////
////        var searchIndex = Collections.binarySearch(keys, key);
////        var node=this;
////        if (searchIndex>=0){
////            this.insertIndex=searchIndex;//找到保留检索位置为后序删除做铺垫
////            return this;
////        }
////        else{
////
////            this.insertIndex=-searchIndex-1;
////        }
////
////        return node;
////    }
//
//
//
//}
