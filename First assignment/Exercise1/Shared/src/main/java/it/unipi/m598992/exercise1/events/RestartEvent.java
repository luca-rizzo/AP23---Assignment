package it.unipi.m598992.exercise1.events;

import java.util.*;

public class RestartEvent {

    private final Integer[] permutation;

    public RestartEvent(Integer[] permutation) {
        this.permutation = permutation;
    }

    public RestartEvent() {
        permutation = createPermutation();
    }

    private Integer[] createPermutation() {
        Integer[] order = { 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        List<Integer> orderList = Arrays.asList(order);
        Collections.shuffle(orderList);
        orderList.toArray(order);
        return order;
    }

    public Integer[] getPermutation() {
        return permutation;
    }
}
