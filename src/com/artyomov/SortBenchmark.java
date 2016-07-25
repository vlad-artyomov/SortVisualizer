package com.artyomov;

import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;

public class SortBenchmark {
    int[] array;

    public SortBenchmark(ObservableList<XYChart.Data<Integer, Integer>> list) {
        array = new int[list.size()];

        for (int i = 0; i < list.size(); i++) {
            array[i] = list.get(i).getYValue();
        }
    }

    public double bubbleSort() {
        long startTime = System.nanoTime();

        int temp;
        for (int i = 0; i < array.length - 1; i++) {
            for (int j = 0; j < array.length - i - 1; j++) {
                if (array[j] > array[j + 1]) {
                    temp = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = temp;
                }
            }
        }

        long endTime = System.nanoTime();
        return (double) (endTime - startTime) / 1000000;
    }

    public double insertionSort() {
        long startTime = System.nanoTime();

        for (int i = 1; i < array.length; i++) {
            int currElem = array[i];
            int prevKey = i - 1;
            while (prevKey >= 0 && array[prevKey] > currElem) {
                array[prevKey + 1] = array[prevKey];
                array[prevKey] = currElem;
                prevKey--;
            }
        }

        long endTime = System.nanoTime();
        return (double) (endTime - startTime) / 1000000;
    }

    public double selectionSort() {
        long startTime = System.nanoTime();

        for (int i = 0; i < array.length - 1; i++) {
            int min = i;
            for (int j = i + 1; j < array.length; j++) {
                if (array[j] < array[min]) min = j;
            }
            int temp = array[i];
            array[i] = array[min];
            array[min] = temp;
        }

        long endTime = System.nanoTime();
        return (double) (endTime - startTime) / 1000000;
    }

    public double cocktailSort() {
        long startTime = System.nanoTime();

        int left = 0; // левая граница
        int right = array.length - 1; // правая граница

        do
        {
            //Сдвигаем к концу массива "тяжелые элементы"
            for (int i = left; i < right; i++)
            {
                if(array[i] > array[i+1])
                {
                    array[i] ^= array[i+1];
                    array[i+1] ^= array[i];
                    array[i] ^= array[i+1];
                }
            }
            right--; // уменьшаем правую границу
            //Сдвигаем к началу массива "легкие элементы"
            for (int i = right; i > left ; i--)
            {
                if(array[i] < array[i-1])
                {
                    array[i] ^= array[i-1];
                    array[i-1] ^= array[i];
                    array[i] ^= array[i-1];
                }
            }
            left++; // увеличиваем левую границу
        } while (left <= right);

        long endTime = System.nanoTime();
        return (double) (endTime - startTime) / 1000000;
    }

    public double mergeSort(int low, int high) {
        long startTime = System.nanoTime();

        if (low + 1 < high) {
            int mid = (low + high) >>> 1;
            mergeSort(low, mid);
            mergeSort(mid, high);

            int size = high - low;
            int[] b = new int[size];
            int i = low;
            int j = mid;
            for (int k = 0; k < size; k++) {
                if (j >= high || i < mid && array[i] < array[j]) {
                    b[k] = array[i++];
                } else {
                    b[k] = array[j++];
                }
            }
            System.arraycopy(b, 0, array, low, size);
        }

        long endTime = System.nanoTime();
        return (double) (endTime - startTime) / 1000000;
    }

    public double quickSort() {
        long startTime = System.nanoTime();

        qSort(0, array.length - 1);

        long endTime = System.nanoTime();
        return (double) (endTime - startTime) / 1000000;
    }

    private int partition(int left, int right)
    {
        int i = left, j = right;
        int tmp;
        int pivot = array[(left + right) / 2];

        while (i <= j) {
            while (array[i] < pivot)
                i++;
            while (array[j] > pivot)
                j--;
            if (i <= j) {
                tmp = array[i];
                array[i] = array[j];
                array[j] = tmp;
                i++;
                j--;
            }
        }

        return i;
    }

    private void qSort(int left, int right) {
        int index = partition(left, right);
        if (left < index - 1)
            qSort(left, index - 1);
        if (index < right)
            qSort(index, right);
    }
}
