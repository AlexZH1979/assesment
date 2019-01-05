package ru.zhmyd.algorithm.sort.insertion;

import java.util.Arrays;

public class InsertionSortDemo {

    public static void main(String[] args) {
        Character[] characters =   "lalugaluzhbufxjbviurvlv".chars()
                .boxed()
                .map(i-> (char) i.intValue())
                .toArray(Character[]::new);

        sort(characters);
        System.out.println(Arrays.toString(characters));
    }
    private static <T extends Comparable<T>> void sort(T[] arr){
        int n = arr.length;
        for (int i = 0; i < n; i++) {
            int j = i;
            while ((j > 0) && (arr[j].compareTo(arr[j-1])<0)){
                T tmp = arr[j];
                arr[j] = arr[j-1];
                arr[j-1] = tmp;
                j--;
            }
        }
    }
}
