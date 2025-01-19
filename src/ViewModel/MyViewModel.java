package ViewModel;

import Model.IModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.input.KeyCode;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class MyViewModel  extends Observable implements Observer {
    private IModel MyModel;

    // Indicates whether the maze has been solved
    private boolean solvedMaze = false;

    // Track the character's position in the maze
    private int characterPositionRowIndex;
    private int characterPositionColumnIndex;

    // Properties bound to UI elements displaying character position
    public StringProperty characterPositionRow = new SimpleStringProperty("");
    public StringProperty characterPositionColumn = new SimpleStringProperty("");

    private ArrayList<Integer> rowsSolution;
    private ArrayList<Integer> columnsSolution;

    // Constructor initializes ViewModel with a Model
    public MyViewModel(IModel myModel) {
        MyModel = myModel;
        MyModel.assignObserver(this);  // ViewModel observes Model for changes - tracks the changes in the model
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o == MyModel) {
            // Retrieve new data from the Model
            solvedMaze = MyModel.isSolvedMaze();
            columnsSolution = MyModel.GetColumnsSolution();
            rowsSolution = MyModel.GetRowsSolution();
            // Update properties for binding
            characterPositionRowIndex = MyModel.GetCharacterPositionRow();
            characterPositionColumnIndex = MyModel.GetCharacterPositionColumn();

            characterPositionRow.set(characterPositionRowIndex + "");
            characterPositionColumn.set(characterPositionColumnIndex + "");

            setChanged(); // Mark ViewModel as changed
            notifyObservers();  // Notify observers (the View)
        }
    }

    public void GenerateMaze(int Width, int Height) {
        MyModel.GenerateMaze(Width, Height);
    }

    public void SolveMaze() {
        MyModel.SolveMaze();
    }

    // Translates key presses into directions and commands Model to move the character
    public void moveCharacter(KeyCode Movement) {
        int direction = -1;
        switch (Movement) {
            case UP:
                direction = 8;
                break;
            case DOWN:
                direction = 2;
                break;
            case RIGHT:
                direction = 6;
                break;
            case LEFT:
                direction = 4;
                break;
            case NUMPAD8:
                direction = 8;
                break;
            case NUMPAD2:
                direction = 2;
                break;
            case NUMPAD6:
                direction = 6;
                break;
            case NUMPAD4:
                direction = 4;
                break;

            // Up Right
            case NUMPAD9:
                direction = 9;
                break;

            // Up Left
            case NUMPAD7:
                direction = 7;
                break;

            // Down Right
            case NUMPAD3:
                direction = 3;
                break;

            // Down Left
            case NUMPAD1:
                direction = 1;
                break;
        }
        MyModel.MoveCharacter(direction);
    }

    public int[][] GetMaze() {
        return MyModel.GetMaze();
    }

    public int[] getGoalPoint() {
        int[] goalPosition = new int[2];

        goalPosition[0] = MyModel.getGoalPosition().getRowIndex();
        goalPosition[1] = MyModel.getGoalPosition().getColumnIndex();

        return goalPosition;
    }

    public IModel getMyModel() {
        return MyModel;
    }

    public int GetCharacterPositionRow() {
        return characterPositionRowIndex;
    }

    public int GetCharacterPositionColumn() {
        return characterPositionColumnIndex;
    }

    public boolean isGameOver() {
        return MyModel.isGameOver();
    }

    public boolean isSolvedMaze() {
        return solvedMaze;
    }

    public boolean isHint() {
        return MyModel.isHint();
    }

    public void SaveFile(File MyFile) throws IOException {
        MyModel.SaveFile(MyFile);
    }

    public void LoadFile(File MyFile) throws IOException, ClassNotFoundException {
        MyModel.LoadFile(MyFile);
    }

    public void SaveProperties(String generateMaze, String solvingMethod, String numOfThreads) throws IOException, ClassNotFoundException {
        MyModel.saveProperties(generateMaze, solvingMethod, numOfThreads);
    }

    public ArrayList<Integer> getRowsSolution() {
        return rowsSolution;
    }

    public ArrayList<Integer> getColumnsSolution() {
        return columnsSolution;
    }

    public void Hint() {
        MyModel.Hint();
    }

    public void setSolvedMaze(boolean solvedMaze) {
        this.solvedMaze = solvedMaze;
        MyModel.setSolvedMaze(solvedMaze);
    }

    public void setHint(boolean hitValue) {
        MyModel.setHint(hitValue);
    }

    public void stopServers() {
        MyModel.stopServers();
    }

    /*public void addToLog(String msg){
        MyModel.addToLog(msg);
    }*/
}