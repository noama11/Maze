package View;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;
import java.awt.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;



public class MazeDisplayer extends Canvas {
    // Custom control that handles maze visualization
    // Implements properties for wall/player images
    // Handles drawing and character positioning
    private int[][] maze;

    private int row_player;
    private int col_player;

    private int goalPositionRow;
    private int goalPositionColumn;
    // The user now can choose the image he wants.
    StringProperty imageFileNameWall = new SimpleStringProperty(); // wall image location
    StringProperty imageFileNamePlayer = new SimpleStringProperty(); // player image location
    //These properties are used to bind the values specified in the FXML file ->
    // to the corresponding properties in the controller class.

    public String getImageFileNameWall() {
        return imageFileNameWall.get();
    }


    public void setImageFileNameWall(String imageFileNameWall) {
        this.imageFileNameWall.set(imageFileNameWall);
    }

    public String getImageFileNamePlayer() {
        return imageFileNamePlayer.get();
    }

    public void setGoalPoint (int row, int column) {
        goalPositionRow = row;
        goalPositionColumn = column;
    }


    public void setImageFileNamePlayer(String imageFileNamePlayer) {
        this.imageFileNamePlayer.set(imageFileNamePlayer);
    }

    public void setRow_player(int row_player) {
        this.row_player = row_player;
        draw();
    }
    public void setCharacterPosition(int row, int column) {
        row_player = row;
        col_player = column;
        draw();
    }
    public void setCol_player(int col_player) {
        this.col_player = col_player;
        draw();
    }

    public int getRow_player() {
        return row_player;
    }

    public int getCol_player() {
        return col_player;
    }

    public void drawMaze(int[][] maze) {
        this.maze = maze;
        draw();
    }

    public void SetCharacter(String CharacterName) {
        if(CharacterName.equals("Jessi"))
            imageFileNamePlayer.set("./Resources/Images/Jessi.png");
        else if(CharacterName.equals("Bunny1"))
            imageFileNamePlayer.set("./resources/Images/Bunny1.png");
        else if(CharacterName.equals("Bunny2"))
            imageFileNamePlayer.set("./resources/Images/Bunny2.png");
        else if(CharacterName.equals("Bunny3"))
            imageFileNamePlayer.set("./resources/Images/Bunny3.png");
        else
            imageFileNamePlayer.set("./resources/Images/Jessi.png");
    }

    public void draw(){
        if(maze != null){
            double canvasHeight = getHeight();
            double canvasWidth = getWidth();
            int rows = maze.length;
            int cols = maze[0].length;

            double cellHeight = canvasHeight / rows;
            double cellWidth = canvasWidth / cols;

            GraphicsContext graphicsContext = getGraphicsContext2D();
            //clear the canvas:
            graphicsContext.clearRect(0, 0, getWidth(), getHeight());
            graphicsContext.setFill(Color.TRANSPARENT);
            Image wallImage = null;
            try {
                wallImage = new Image(new FileInputStream(getImageFileNameWall()));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                System.out.println("image player not good...");
            }

            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    if(maze[i][j] == 1){
                        //if it is a wall:
                        double x = i * cellHeight;
                        double y = j * cellWidth;
                        graphicsContext.fillRect(y, x, cellWidth, cellHeight);
                        graphicsContext.drawImage(wallImage,y, x, cellWidth, cellHeight);
                    }
                }
            }
            Image goalPointImage;
            try {
                goalPointImage = new Image(new FileInputStream("./resources/Images/Carrot.png"));
                graphicsContext.drawImage(goalPointImage, goalPositionColumn * cellWidth, goalPositionRow * cellHeight, cellWidth, cellHeight);
            }
            catch (FileNotFoundException e) {
            }
            double h_player = getRow_player() * cellHeight;
            double w_player = getCol_player() * cellWidth;
            Image playerImage = null;
            try {
                playerImage = new Image(new FileInputStream(getImageFileNamePlayer()));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                System.out.println("image player not good...");
            }
            graphicsContext.drawImage(playerImage,w_player, h_player, cellWidth, cellHeight );
        }

    }
    public void drawSolution(ArrayList<Integer> rowsSolution, ArrayList<Integer> columnsSolution)  {
        if (maze != null) {
            double canvasHeight = getWidth();
            double canvasWidth = getHeight();
            double cellHeight = canvasHeight / maze[0].length;
            double cellWidth = canvasWidth / maze.length;

            draw();

            try {
                Image roadImage = new Image(new FileInputStream("./resources/Images/Carrot.png"));

                GraphicsContext gc = getGraphicsContext2D();

                for(int i = 1; i < rowsSolution.size(); i++){
                    int rowValue = rowsSolution.get(i);
                    int columnValue = columnsSolution.get(i);

                    if(rowValue == row_player && columnValue ==col_player){
                        rowsSolution.remove(i);
                        columnsSolution.remove(i);
                    }
                }

                for (int i = 1; i < rowsSolution.size() - 1; i++)
                    gc.drawImage(roadImage, columnsSolution.get(i)* cellHeight, rowsSolution.get(i) * cellWidth, cellHeight, cellWidth);
                Image playerImage = new Image(new FileInputStream(getImageFileNamePlayer()));
                gc.drawImage(playerImage, col_player * cellHeight, row_player * cellWidth, cellHeight, cellWidth);

            }
            catch (FileNotFoundException e) {
            }
        }
    }
    public void drawHint(int rowSolution, int columnSolution){
        if (maze != null) {
            double canvasHeight = getWidth();
            double canvasWidth = getHeight();
            double cellHeight = canvasHeight / maze[0].length;
            double cellWidth = canvasWidth / maze.length;

            draw();

            try {
                Image roadImage =new Image(new FileInputStream("./resources/Images/Carrot.png"));

                GraphicsContext gc = getGraphicsContext2D();

                gc.drawImage(roadImage, columnSolution * cellHeight, rowSolution * cellWidth, cellHeight, cellWidth);
            }
            catch (FileNotFoundException e) {
                System.out.println("Not work hint!");
            }
        }
    }


}
