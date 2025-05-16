package application;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;

public class FocusFreak extends Application {

	private final String username;
	private final String password;

	public FocusFreak(String username, String password) {
		super();
		this.username = username;
		this.password = password;
	}
	
    private int score = 0;
    private VBox cardBox;
    private VBox rectangleBox;
    private VBox circleBox;
    private Random random = new Random();
    private Timeline timeline;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        StackPane root = new StackPane();
        Scene scene = new Scene(root, 600, 400);
        
        Button back = new Button("Back");
		back.setStyle("-fx-font-size: 16px; -fx-background-color: #ccccff;");

		back.setOnAction(e -> {
			LevelPage s = new LevelPage(username, password);
			try {
				s.start(primaryStage);
			} catch (Exception e11) {
				e11.printStackTrace();
			}
		});
		
		GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.BOTTOM_RIGHT);
        gridPane.add(back, 1, 1); // Add the button to the bottom-right of the grid

        // Add the GridPane to the StackPane
        root.getChildren().add(gridPane);

        // Create the card box
        cardBox = createCardBox();

        // Create the shape boxes
        rectangleBox = createShapeBox("Rectangle");
        circleBox = createShapeBox("Circle");

        // Add the boxes to the root
        HBox gameLayout = new HBox(20, rectangleBox, cardBox, circleBox);
        gameLayout.setAlignment(Pos.CENTER);
        root.getChildren().add(gameLayout);

        // Create and start the game timeline with a 15-second duration
        timeline = createGameTimeline();
        timeline.play();

        primaryStage.setScene(scene);
        primaryStage.setTitle("Card Matching Game");
        primaryStage.show();
    }

    private VBox createCardBox() {
        VBox cardBox = new VBox(10);
        cardBox.setAlignment(Pos.CENTER);

        // Generate a random shape card
        cardBox.getChildren().add(generateRandomShapeCard());

        // Set up drag-and-drop functionality
        cardBox.setOnDragDetected(event -> {
            Dragboard dragboard = cardBox.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString("");
            dragboard.setContent(content);
        });

        cardBox.setOnDragDone(event -> {
            // Check if the card was dropped into the correct box
            boolean isCorrect = checkCardPlacement();
            updateScore(isCorrect);
            // Generate a new random shape card
            cardBox.getChildren().clear();
            cardBox.getChildren().add(generateRandomShapeCard());
        });

        return cardBox;
    }

    private VBox createShapeBox(String shapeName) {
        VBox shapeBox = new VBox();
        shapeBox.setAlignment(Pos.CENTER);

        Label label = new Label(shapeName);

        // Set up drag-and-drop functionality to accept the corresponding shape
        shapeBox.setOnDragOver(event -> {
            if (event.getGestureSource() != shapeBox && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        shapeBox.setOnDragDropped(event -> {
            Dragboard dragboard = event.getDragboard();
            boolean success = false;

            if (dragboard.hasString()) {
                // Check if the dropped shape matches the box's expected shape
                String droppedShape = dragboard.getString();
                success = shapeName.equalsIgnoreCase(droppedShape);
            }

            event.setDropCompleted(success);
            event.consume();
        });

        shapeBox.getChildren().add(label);
        return shapeBox;
    }

    private StackPane generateRandomShapeCard() {
        StackPane card = new StackPane();
        card.setPrefSize(100, 100);

        if (random.nextBoolean()) {
            Rectangle rectangle = new Rectangle(80, 80);
            rectangle.setFill(Color.TRANSPARENT);
            rectangle.setStroke(Color.RED);
            card.getChildren().addAll(rectangle, createDraggableNode("Rectangle"));
        } else {
            Circle circle = new Circle(40);
            circle.setFill(Color.TRANSPARENT);
            circle.setStroke(Color.PINK);
            card.getChildren().addAll(circle, createDraggableNode("Circle"));
        }

        return card;
    }

    private Label createDraggableNode(String shapeName) {
        Label label = new Label(shapeName);
        label.setVisible(false); // To hide the label during the drag-and-drop operation

        label.setOnDragDetected(event -> {
            Dragboard dragboard = label.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString(shapeName);
            dragboard.setContent(content);
            event.consume();
        });

        label.setOnDragDone(event -> {
            // Hide the label after the drag-and-drop operation
            label.setVisible(false);
        });

        return label;
    }

    private Timeline createGameTimeline() {
        Duration duration = Duration.seconds(15);
        KeyFrame keyFrame = new KeyFrame(duration, event -> endGame());
        return new Timeline(keyFrame);
    }

    private void endGame() {
        System.out.println("Game Over! Final Score: " + score);
        timeline.stop();
    }

    private boolean checkCardPlacement() {
        if (cardBox.getChildren().isEmpty()) {
            return false; // No card to check
        }

        StackPane card = (StackPane) cardBox.getChildren().get(0);
        Node shape = card.getChildren().get(0);

        boolean isCorrect = false;

        if (shape instanceof Rectangle && circleBox.getBoundsInParent().intersects(card.getBoundsInParent())) {
            isCorrect = false; // Circle box intersects with rectangle, incorrect
        } else if (shape instanceof Circle && rectangleBox.getBoundsInParent().intersects(card.getBoundsInParent())) {
            isCorrect = false; // Rectangle box intersects with circle, incorrect
        } else if (rectangleBox.getBoundsInParent().intersects(card.getBoundsInParent())) {
            isCorrect = shape instanceof Rectangle;
        } else if (circleBox.getBoundsInParent().intersects(card.getBoundsInParent())) {
            isCorrect = shape instanceof Circle;
        }

        return isCorrect;
    }


    private void updateScore(boolean isCorrect) {
        if (isCorrect) {
            score += 10;
        } else {
            score = Math.max(0, score - 5);
        }
        System.out.println(score);
    }
    
    private void stopGame() throws Exception {
	    if (score >= 50)
	        System.out.println("you win");
	    else
	        System.out.println("You lose");
	    stop();
	    System.out.println("Final Score: " + score);
	    try {
	        Class.forName("com.mysql.cj.jdbc.Driver");
	        System.out.println("Driver loaded");
	        // Establish a connection
	        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/hci_project", "root", "");
	        String querypic = "UPDATE users SET Level2Score = '" + score + "' WHERE username = '" + username
	                + "' AND password='" + password + "';";
	        String querypict = "UPDATE users SET Level2Time = '30' WHERE username = '" + username
	                + "' AND password='" + password + "';";

	        try {
	            PreparedStatement p = connection.prepareStatement(querypic);
	            p.execute();
	            PreparedStatement pt = connection.prepareStatement(querypict);
	            pt.execute();
	            System.out.println("SUCCESS");
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
}