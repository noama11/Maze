package View;

import Model.IModel;
import ViewModel.MyViewModel;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Observable;
import java.util.Observer;
import java.util.Optional;

// controller is not the right name for this class, this class sappouse to be the code behind of the mazeWindow
public class MyViewController implements IView, Observer {
    public static int counter = 1;
    private MediaPlayer mediaPlayer;
    @FXML
    private MazeDisplayer mazeDisplayer;
    private boolean ctrlKey = false;
    private int hintNum = 1;
    @FXML
    private javafx.scene.control.TextField textField_mazeRows;
    @FXML
    private javafx.scene.control.TextField textField_mazeColumns;
    @FXML
    private javafx.scene.control.MenuItem save;
    @FXML
    private javafx.scene.control.Button generateMazeButton;
    @FXML
    private javafx.scene.control.Button solveMazeButton;
    @FXML
    private javafx.scene.control.Button HintButton;
    @FXML
    private javafx.scene.control.Label selectCharacterLabel;
    @FXML
    private ComboBox<String> selectCharacterComboBox;
    private ObservableList<String> characters = FXCollections.observableArrayList("Jessi", "Bunny1", "Bunny2", "Bunny3");
    private MyViewModel viewModel;

    public void setViewModel(MyViewModel viewModel) {
        this.viewModel = viewModel;
    }

    public void initialize() {
        selectCharacterComboBox.setItems(characters);
        selectCharacterComboBox.setValue("Jessi");
        selectCharacterLabel = new Label();
        generateMazeButton = new Button();
    }

    public void setResizeEvent(Scene scene) {
        // Variables to track mouse movement
        final double[] mouseX = new double[1];
        final double[] mouseY = new double[1];
        final double[] oldLayoutX = new double[1];
        final double[] oldLayoutY = new double[1];

        // Mouse pressed event handler
        this.mazeDisplayer.setOnMousePressed(event -> {
            // Store the initial mouse coordinates and mazeDisplayer's layout position
            mouseX[0] = event.getSceneX();
            mouseY[0] = event.getSceneY();
            oldLayoutX[0] = mazeDisplayer.getLayoutX();
            oldLayoutY[0] = mazeDisplayer.getLayoutY();
        });

        // Mouse dragged event handler
        mazeDisplayer.setOnMouseDragged(event -> {
            // Calculate the distance moved by the mouse
            double deltaX = event.getSceneX() - mouseX[0];
            double deltaY = event.getSceneY() - mouseY[0];

            // Calculate the new layout position based on the distance moved
            double newLayoutX = oldLayoutX[0] + deltaX;
            double newLayoutY = oldLayoutY[0] + deltaY;

            // Set the new layout position for mazeDisplayer
            mazeDisplayer.setLayoutX(newLayoutX);
            mazeDisplayer.setLayoutY(newLayoutY);

            // Redraw the maze
            mazeDisplayer.draw();
        });

        // Resize listeners
        scene.widthProperty().addListener((observableValue, oldSceneWidth, newSceneWidth) -> {
            mazeDisplayer.setLayoutX(calculateNewLayoutX(scene.getWidth(), newSceneWidth.doubleValue()));
            mazeDisplayer.draw();
        });
        scene.heightProperty().addListener((observableValue, oldSceneHeight, newSceneHeight) -> {
            mazeDisplayer.setLayoutY(calculateNewLayoutY(scene.getHeight(), newSceneHeight.doubleValue()));
            mazeDisplayer.draw();
        });
    }

    private double calculateNewLayoutX(double sceneWidth, double newSceneWidth) {
        // Calculate the new X coordinate for mazeDisplayer based on its current position and the resize ratio
        // For example, if mazeDisplayer was at 100 pixels from the left side, and the scene width doubles,
        // the new X coordinate would be 200 pixels from the left side.
        // Adjust the calculation according to your specific requirements.
        double currentX = mazeDisplayer.getLayoutX();
        double resizeRatio = newSceneWidth / sceneWidth;
        return currentX * resizeRatio;
    }

    private double calculateNewLayoutY(double sceneHeight, double newSceneHeight) {
        // Calculate the new Y coordinate for mazeDisplayer based on its current position and the resize ratio
        // Similar to calculateNewLayoutX, but for the Y coordinate.
        // Adjust the calculation according to your specific requirements.
        double currentY = mazeDisplayer.getLayoutY();
        double resizeRatio = newSceneHeight / sceneHeight;
        return currentY * resizeRatio;
    }

    private void generateNewMaze() {
        int rows = Integer.valueOf(textField_mazeRows.getText());
        int cols = Integer.valueOf(textField_mazeColumns.getText());

        if (rows <= 1 || cols <= 1) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Invalid Input");
            alert.setContentText("Rows and columns must be greater than one.");
            alert.showAndWait();
            return;
        }
        viewModel.setSolvedMaze(false);
        viewModel.GenerateMaze(cols, rows); // create the maze

        playMusic(true);
        // TODO set other Buttons to be visible = true

        solveMazeButton.setDisable(false);
        HintButton.setDisable(false);
        save.setDisable(false);
        mazeDisplayer.drawMaze(viewModel.GetMaze());
    }

    public void generateMaze(ActionEvent actionEvent) {
        generateNewMaze();
    }

    public void solveMaze(ActionEvent actionEvent) {
        //Alert alert = new Alert(Alert.AlertType.INFORMATION);
        //alert.setContentText("Solving maze...");
        //alert.show();
        viewModel.SolveMaze();
        viewModel.setSolvedMaze(true);
    }

    public void playMusic(boolean flag) {
        if (mediaPlayer != null)
            mediaPlayer.stop();
        String Path = null;
        if (flag)
            Path = "./resources/Clips/bunnySong.mp3";
        else {
            Path = "./resources/Clips/winningSong.mp3";
            mediaPlayer.stop();
        }
        Media media = new Media(new File(Path).toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setAutoPlay(true);
        mediaPlayer.play();
    }

    public void KeyReleasedEvent(KeyEvent keyEvent) {
        if (!keyEvent.isControlDown())
            ctrlKey = false;
    }

    public void keyPressed(KeyEvent keyEvent) {
        if (keyEvent.isControlDown())
            ctrlKey = true;
        else {
            viewModel.moveCharacter(keyEvent.getCode());
            keyEvent.consume();
        }
    }

    // Put the focus on the screen for using the keybord for moving the player on the screen
    public void mouseClicked(MouseEvent mouseEvent) {
        mazeDisplayer.requestFocus();
    }

    public void onScrollEvent(ScrollEvent scrollEvent) {
        if (ctrlKey) {
            double zoomFactor = 1.05;
            double deltaY = scrollEvent.getDeltaY();
            if (deltaY < 0) {
                zoomFactor = 2.0 - zoomFactor;
            }

            double newX = mazeDisplayer.getScaleX() * zoomFactor;
            double newY = mazeDisplayer.getScaleY() * zoomFactor;

            mazeDisplayer.setScaleX(newX);
            mazeDisplayer.setScaleY(newY);
        }
    }

    public void changeCharacter(ActionEvent actionEvent) {
        mazeDisplayer.SetCharacter(selectCharacterComboBox.getValue().toString());
        mazeDisplayer.drawMaze(viewModel.GetMaze());
    }

    public void GiveHelp(ActionEvent actionEvent) {
        try {
            Stage stage = new Stage();
            stage.setTitle("Help");
            Parent root = FXMLLoader.load(getClass().getResource("Help.fxml"));
            Scene scene = new Scene(root, 370, 300);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void GiveHint(ActionEvent actionEvent) {
        viewModel.Hint();
        mazeDisplayer.drawHint(viewModel.getRowsSolution().get(hintNum), viewModel.getColumnsSolution().get(hintNum));
        hintNum++;
    }

    @Override
    public void DisplayMaze(int[][] maze) {
        int characterPositionRow = viewModel.GetCharacterPositionRow();
        int characterPositionColumn = viewModel.GetCharacterPositionColumn();
        mazeDisplayer.SetCharacter(selectCharacterComboBox.getValue().toString());
        mazeDisplayer.setCharacterPosition(characterPositionRow, characterPositionColumn);

        if (viewModel.isSolvedMaze())
            mazeDisplayer.drawSolution(viewModel.getRowsSolution(), viewModel.getColumnsSolution());

        else if (viewModel.isHint()) {
            mazeDisplayer.drawHint(viewModel.getRowsSolution().get(hintNum), viewModel.getColumnsSolution().get(hintNum));
            hintNum++;
            viewModel.setHint(false);
        } else {
            mazeDisplayer.setGoalPoint(viewModel.getGoalPoint()[0], viewModel.getGoalPoint()[1]);
        }
    }

    //watching on ViewModel
    @Override
    public void update(Observable o, Object arg) {
        if (o == viewModel) {
            DisplayMaze(viewModel.GetMaze());
            generateMazeButton.setDisable(false);

            if (viewModel.isGameOver()) {
                playMusic(false);

                try {
                    Stage stage = new Stage();
                    stage.setTitle("Winning");
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Winning.fxml"));
                    Parent root = fxmlLoader.load();
                    Scene scene = new Scene(root, 500, 500);
                    stage.setScene(scene);
                    stage.initModality(Modality.APPLICATION_MODAL);

                    WinningController view = fxmlLoader.getController();
                    view.setViewModel(viewModel);
                    view.setViewController(this);

                    stage.showAndWait();
                    /*if(newGame) {
                        generateNewMaze();
                        newGame = false;
                    }*/

                } catch (Exception e) {
                    e.printStackTrace();
                    //viewModel.addToLog("Scene open failed");
                }
            }
        }
    }

    public void closeSystem() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to exit?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            viewModel.getMyModel().stopServers();
            Platform.exit();
            System.exit(0);
        }
    }

    public void setNewGame(boolean newGame) {
        generateNewMaze();
    }

    public void saveFileSelected(ActionEvent actionEvent) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save the current Maze");
        fileChooser.setInitialFileName("Maze number " + counter);

        counter = counter + 1;

        File file = new File("./Save Mazes/");
        if (!file.exists())
            file.mkdir();

        fileChooser.setInitialDirectory(file);

        File destination = fileChooser.showSaveDialog(mazeDisplayer.getScene().getWindow());
        if (destination != null)
            viewModel.SaveFile(destination);

    }

    public void loadFileSelected(ActionEvent actionEvent) throws IOException, ClassNotFoundException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load a Maze");

        File file = new File("./Save Mazes/");
        if (!file.exists())
            file = new File("./");

        fileChooser.setInitialDirectory(file);
        File destination = fileChooser.showOpenDialog(mazeDisplayer.getScene().getWindow());
        if (destination != null) {
            viewModel.LoadFile(destination);
            mazeDisplayer.drawMaze(viewModel.GetMaze());
        }
    }

    public void newFileSelected(ActionEvent actionEvent) {
        generateNewMaze();
    }
    public void aboutSelected(ActionEvent actionEvent) {
        try {
            Stage stage = new Stage();
            stage.setTitle("About");
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getResource("About.fxml").openStream());
            Scene scene = new Scene(root, 450, 200);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();

            OptionController view = fxmlLoader.getController();
            view.setViewModel(viewModel);
        } catch (Exception e) {

        }
    }

    public void propertiesButtonSelected(ActionEvent actionEvent) {
        try {
            Stage stage = new Stage();
            stage.setTitle("Properties");
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getResource("Options.fxml").openStream());
            Scene scene = new Scene(root, 600, 400);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();

            OptionController view = fxmlLoader.getController();
            view.setViewModel(viewModel);

        } catch (Exception e) {

        }
    }
}

