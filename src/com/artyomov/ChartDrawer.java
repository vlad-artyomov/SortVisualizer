package com.artyomov;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TextField;

import java.util.Random;

public class ChartDrawer {
    private ObservableList<XYChart.Data<Integer, Integer>> data;
    private ObservableList<String> sortAlgs;
    private int n;
    private long delay = 0;
    private TextField result;
    private Thread thread;

    Random random = new Random();

    public ChartDrawer(LineChart chart, TextField result, int n) {
        this.n = n;
        this.result = result;
        XYChart.Series series = new XYChart.Series();

        data = FXCollections.observableArrayList();
        series.setName("Array");
        series.setData(data);
        chart.getData().add(series);

        sortAlgs = FXCollections.observableArrayList(
                "Bubble sort",      //Пузырьковая сортировка (обменом)
                "Insertion sort",   //Простыми вставками
                "Selection sort",   //Выбором
                "Cocktail sort",    //Перемешиванием (разновидность пузырьковой)
                "Quick sort"        //Быстрая сортировка
        );
    }

    public void mix() {
        if (thread != null) {
            if (thread.isAlive()) thread.interrupt();
        }

        if (data.isEmpty())
            for (int i = 0; i < n; i++)
                data.add(new XYChart.Data(i, random.nextInt(101)));

        else if (data.size() < n) {
            for (int i = 0; i < data.size(); i++)
                data.get(i).setYValue(random.nextInt(101));
            for (int i = data.size(); i < n; i++)
                data.add(new XYChart.Data(i, random.nextInt(101)));
        } else {
            data.remove(n, data.size() - 1);
            for (int i = 0; i < n; i++)
                data.get(i).setYValue(random.nextInt(101));
        }
    }

    public void sort(int algorithmIndex) {
        if (thread != null) {
            if (thread.isAlive()) thread.interrupt();
        }

        switch (algorithmIndex) {
            case 0: //Bubble sort
                result.setText(String.valueOf(new SortBenchmark(data).bubbleSort()) + " ms");
                thread = new Thread(bubbleSort());
                thread.setDaemon(true);
                thread.start();
                break;
            case 1: //Insertion sort
                result.setText(String.valueOf(new SortBenchmark(data).insertionSort()) + " ms");
                thread = new Thread(insertionSort());
                thread.setDaemon(true);
                thread.start();
                break;

            case 2: //Selection sort
                result.setText(String.valueOf(new SortBenchmark(data).selectionSort()) + " ms");
                thread = new Thread(selectionSort());
                thread.setDaemon(true);
                thread.start();
                break;

            case 3: //Cocktail sort
                result.setText(String.valueOf(new SortBenchmark(data).cocktailSort()) + " ms");
                thread = new Thread(cocktailSort());
                thread.setDaemon(true);
                thread.start();
                break;

            case 4: //Quick sort
                result.setText(String.valueOf(new SortBenchmark(data).quickSort()) + " ms");
                thread = new Thread(quickSort());
                thread.setDaemon(true);
                thread.start();
                break;

            default:
                System.err.println("Invalid algorithm");
                break;
        }
    }

    private int partition(final int left, final int right) {
        int i = left, j = right;
        final int pivot = data.get((left + right) / 2).getYValue();
        final int index = (left + right) / 2;

        Platform.runLater(() -> {
            data.get(index).getNode().setStyle("-fx-background-color: dodgerblue");
            data.get(index).getNode().setScaleX(3.0);
            data.get(index).getNode().setScaleY(3.0);
            data.get(index).getNode().setScaleZ(3.0);

            data.get(left).getNode().setStyle("-fx-background-color: limegreen");
            data.get(left).getNode().setScaleX(2.0);
            data.get(left).getNode().setScaleY(2.0);
            data.get(left).getNode().setScaleZ(2.0);

            data.get(right).getNode().setStyle("-fx-background-color: limegreen");
            data.get(right).getNode().setScaleX(2.0);
            data.get(right).getNode().setScaleY(2.0);
            data.get(right).getNode().setScaleZ(2.0);
        });

        while (i <= j) {
            while (data.get(i).getYValue() < pivot)
                i++;

            while (data.get(j).getYValue() > pivot)
                j--;

            if (i <= j) {
                final int i1 = i;
                final int j1 = j;

                Platform.runLater(() -> {
                    int tmp = data.get(i1).getYValue();
                    data.get(i1).setYValue(data.get(j1).getYValue());
                    data.get(j1).setYValue(tmp);
                });
                i++;
                j--;

                try {
                    Thread.sleep(20 + delay * 6);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        Platform.runLater(() -> {
            data.get(index).getNode().setStyle(null);
            data.get(index).getNode().setScaleX(1.0);
            data.get(index).getNode().setScaleY(1.0);
            data.get(index).getNode().setScaleZ(1.0);

            data.get(left).getNode().setStyle(null);
            data.get(left).getNode().setScaleX(1.0);
            data.get(left).getNode().setScaleY(1.0);
            data.get(left).getNode().setScaleZ(1.0);

            data.get(right).getNode().setStyle(null);
            data.get(right).getNode().setScaleX(1.0);
            data.get(right).getNode().setScaleY(1.0);
            data.get(right).getNode().setScaleZ(1.0);
        });

        return i;
    }

    private void qSort(int left, int right) {
        int index = partition(left, right);
        if (left < index - 1)
            qSort(left, index - 1);
        if (index < right)
            qSort(index, right);

    }

    private Task quickSort() {
        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                qSort(0, data.size() - 1);
                return null;
            }
        };
        return task;
    }

    private Task cocktailSort() {
        Task task = new Task<Void>() {
            @Override
            public Void call() throws Exception {
                int left = 0; // левая граница
                int right = data.size() - 1; // правая граница

                do {
                    //Сдвигаем к концу массива "тяжелые элементы"
                    for (int i = left; i < right; i++) {
                        final int i1 = i;
                        Platform.runLater(() -> {
                            if (data.get(i1).getYValue() > data.get(i1 + 1).getYValue()) {
                                int temp = data.get(i1).getYValue();
                                data.get(i1).setYValue(data.get(i1 + 1).getYValue());
                                data.get(i1 + 1).setYValue(temp);
                            }
                        });
                        Thread.sleep(delay);
                    }
                    right--; // уменьшаем правую границу

                    //Сдвигаем к началу массива "легкие элементы"
                    for (int i = right; i > left; i--) {
                        final int i1 = i;
                        Platform.runLater(() -> {
                            if (data.get(i1).getYValue() < data.get(i1 - 1).getYValue()) {
                                int temp = data.get(i1).getYValue();
                                data.get(i1).setYValue(data.get(i1 - 1).getYValue());
                                data.get(i1 - 1).setYValue(temp);
                            }
                        });
                        Thread.sleep(delay);
                    }
                    left++; // увеличиваем левую границу

                } while (left <= right);
                return null;
            }
        };
        return task;
    }

    private Task selectionSort() {
        Task task = new Task<Void>() {
            @Override
            public Void call() throws Exception {
                for (int i = 0; i < data.size() - 1; i++) {
                    final int i1 = i;
                    Platform.runLater(() -> {
                        int min = i1;
                        for (int j = i1 + 1; j < data.size(); j++) {
                            if (data.get(j).getYValue() < data.get(min).getYValue()) min = j;
                        }
                        int temp = data.get(i1).getYValue();
                        data.get(i1).setYValue(data.get(min).getYValue());
                        data.get(min).setYValue(temp);
                    });
                    Thread.sleep(delay * 10);
                }
                return null;
            }
        };
        return task;
    }

    private Task insertionSort() {
        Task task = new Task<Void>() {
            @Override
            public Void call() throws Exception {
                for (int i = 1; i < data.size(); i++) {
                    final int currElem = data.get(i).getYValue();
                    int prevKey = i - 1;
                    while (prevKey >= 0) {
                        final int prevKey1 = prevKey;
                        Platform.runLater(() -> {
                            if (data.get(prevKey1).getYValue() > currElem) {
                                data.get(prevKey1 + 1).setYValue(data.get(prevKey1).getYValue());
                                data.get(prevKey1).setYValue(currElem);
                            }
                        });
                        prevKey--;
                        Thread.sleep(delay);
                    }
                }
                return null;
            }
        };
        return task;
    }

    private Task bubbleSort() {
        Task task = new Task<Void>() {
            @Override
            public Void call() throws Exception {
                for (int i = 0; i < data.size() - 1; i++)
                    for (int j = 0; j < data.size() - i - 1; j++) {
                        final int j1 = j;
                        Platform.runLater(() -> {
                            if (data.get(j1).getYValue() > data.get(j1 + 1).getYValue()) {
                                int temp = data.get(j1).getYValue();
                                data.get(j1).setYValue(data.get(j1 + 1).getYValue());
                                data.get(j1 + 1).setYValue(temp);
                            }
                        });
                        Thread.sleep(delay);
                    }
                return null;
            }
        };
        return task;
    }


    public int getN() {
        return n;
    }

    public void setN(int n) {
        this.n = n;
    }

    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    public ObservableList<String> getSortAlgs() {
        return sortAlgs;
    }
}
