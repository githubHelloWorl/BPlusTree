package com.cmd.BTreeAdd;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Test {
    public static void main(String[] args){

        BPlusTree<Integer, Integer> b = new BPlusTree<>(6);

        long time1 = System.nanoTime();

        for (int i = 0; i < 22; i++) {
            Product p = new Product(i, "test", 1.0 * i);
//            b.insert(p, p.getId());
            b.insert(i, i);
        }

      long time2 = System.nanoTime();
        b.print();
//      Product p1 = b.find(4);

      long time3 = System.nanoTime();

      System.out.println("插入耗时: " + (time2 - time1));
      System.out.println("查询耗时: " + (time3 - time2));
    }
}

