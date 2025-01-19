package View;

import ViewModel.MyViewModel;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class WinningController {
    private MyViewModel viewModel;
    private MyViewController viewController;

    public void setViewController(MyViewController viewController) {
        this.viewController = viewController;
    }

    public javafx.scene.control.Button newMazeButton;
    public javafx.scene.control.Button exitButton;

    @FXML
    private ImageView winningImage;

    public void setViewModel(MyViewModel viewModel){
        this.viewModel = viewModel;
    }

    public void newMazeSelected(ActionEvent actionEvent) {
        viewController.setNewGame(true);
        Stage stage = (Stage) newMazeButton.getScene().getWindow();
        stage.close();
    }

    public void exitButtonSelected(ActionEvent actionEvent) {
        viewModel.stopServers();
        Stage stage = (Stage) newMazeButton.getScene().getWindow();
        stage.close();
        Platform.exit();
    }

    public void initialize() {
        try {
            winningImage.setImage(new Image(new FileInputStream("./Resources/Images/goal.jpeg")));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
