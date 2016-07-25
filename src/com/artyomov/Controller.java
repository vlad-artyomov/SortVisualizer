package com.artyomov;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;

public class Controller {
    @FXML
    Button btnSort;
    @FXML
    Button btnMix;
    @FXML
    LineChart chart;
    @FXML
    Slider sliderNumber;
    @FXML
    Slider sliderDelay;
    @FXML
    ComboBox<String> comboBoxSortAlgs;
    @FXML
    TextField tfResult;

    ChartDrawer chartDrawer;

    @FXML
    public void initialize() {
        chartDrawer = new ChartDrawer(chart, tfResult, (int) sliderNumber.getValue());
        comboBoxSortAlgs.setItems(chartDrawer.getSortAlgs());
        comboBoxSortAlgs.setValue(chartDrawer.getSortAlgs().get(0));

        btnMix.setOnAction(event -> chartDrawer.mix());

        btnSort.setOnAction(event -> chartDrawer.sort(chartDrawer.getSortAlgs().indexOf(comboBoxSortAlgs.getValue())));

        sliderNumber.valueProperty().addListener((observable, oldValue, newValue) -> {
            chartDrawer.setN((int) sliderNumber.getValue());
        });

        sliderDelay.valueProperty().addListener((observable, oldValue, newValue) -> {
            chartDrawer.setDelay((long) sliderDelay.getValue());
        });
    }
}
