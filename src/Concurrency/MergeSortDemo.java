package Concurrency;

import java.util.Arrays;
import java.util.Random;

public class MergeSortDemo {
    public static void main(String[] args){
        int size = 5000;
        Random random = new Random(System.currentTimeMillis());
        int[] a = new int[size];
        int[] b = new int[size];

        for (int i=0; i<size; i++) {
            a[i] = random.nextInt(size);
            b[i] = a[i];
        }

        long start = System.currentTimeMillis();
        new MergeSort(a).mergesort(a, 0, a.length-1);
        long end = System.currentTimeMillis();
        System.out.println("Time taken by single thread mergesort: " + (end-start) + " ms");

        start = System.currentTimeMillis();
        new MergeSort(b).mergesortMT(b, 0, b.length-1);
        end = System.currentTimeMillis();
        System.out.println("Time taken by multi thread mergesort: " + (end-start) + " ms");

        //System.out.println(Arrays.toString(a));
        //System.out.println(Arrays.toString(b));
    }
}

class MergeSort {
    int[] a;

    public MergeSort(int[] a) {
        this.a = a;
    }

    void mergesort(int[] a, int l, int r) {
        if (l<r) {
            int mid = l + (r - l) / 2;

            mergesort(a, l, mid);
            mergesort(a, mid+1, r);

            merge(a, l, mid, r);
        }
    }

    void mergesortMT(int[] a, int l, int r) {
        if (l<r) {
            int mid = l + (r - l) / 2;

            Thread t1 = new Thread(() -> mergesortMT(a, l, mid));
            Thread t2 = new Thread(() -> mergesortMT(a, mid+1, r));

            t1.start();
            t2.start();

            try {
                t1.join();
                t2.join();
            } catch (InterruptedException e) {}

            merge(a, l, mid, r);
        }
    }

    private void merge(int[] a, int l, int mid, int r) {
        int[] left = new int[mid-l+1];
        int[] right = new int[r-mid];

        for (int i=0; i<left.length; i++) {
            left[i] = a[i+l];
        }

        for (int i=0; i<right.length; i++) {
            right[i] = a[i+mid+1];
        }

        int i=0, j=0, k=l;
        while (i<left.length && j<right.length) {
            if (left[i] <= right[j]) {
                a[k++] = left[i++];
            } else {
                a[k++] = right[j++];
            }
        }

        while (i<left.length) {
            a[k++] = left[i++];
        }

        while (j<right.length) {
            a[k++] = right[j++];
        }
    }
}