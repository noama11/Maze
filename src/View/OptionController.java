package View;

import ViewModel.MyViewModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.SnapshotResult;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;


import java.io.*;

public class OptionController {
    private MyViewModel viewModel;

    public javafx.scene.control.ComboBox generateMazeCB;
    public javafx.scene.control.ComboBox solvingMethodCB;
    public javafx.scene.control.ComboBox numberThreadCB;
    public javafx.scene.control.Button saveButton;
    private ObservableList<String> solve = FXCollections.observableArrayList("Best First Search", "Breadth First Search", "Depth First Search");
    private ObservableList<String> number = FXCollections.observableArrayList("1", "2", "3");
    private ObservableList<String> generate = FXCollections.observableArrayList("Best First Search", "Breadth First Search", "Depth First Search");

    public void initialize(){
        generateMazeCB.setItems(generate);
        solvingMethodCB.setItems(solve);
        numberThreadCB.setItems(number);
    }
    public void setViewModel(MyViewModel viewModel){
        this.viewModel = viewModel;
    }

    public void savePropreties(ActionEvent actionEvent) throws IOException, ClassNotFoundException {
        String generateMaze = generateMazeCB.getValue().toString();
        String solvingMethod = solvingMethodCB.getValue().toString();
        String numberOfThreads = numberThreadCB.getValue().toString();

        generateMaze = generateMaze.replaceAll("\\s", "");
        solvingMethod = solvingMethod.replaceAll("\\s", "");

        viewModel.SaveProperties(generateMaze, solvingMethod, numberOfThreads);

        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }
}
