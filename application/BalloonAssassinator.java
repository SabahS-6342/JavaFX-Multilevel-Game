package application;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class BalloonAssassinator extends Application {
	private final String username;
	private final String password;

	public BalloonAssassinator(String username, String password) {
		super();
		this.username = username;
		this.password = password;
	}
	private String sky = "sky1.jpeg";
	private String[] balloonColors = { "#ff6699", "#993399", "#ffccdd", "#cc99ff" };
	private String chosenColor;
	private int score = 0;
	private int timerSeconds = 30; 
	private Timeline timerTimeline;


	public static void main(String[] args) {
		launch();
	}

	public void start(Stage stage) throws Exception {
		try {
			InputStream inputStream = getClass().getResourceAsStream(sky);
			if (inputStream == null) {
				throw new IllegalArgumentException("Image not found: " + sky);
			}

			Image img = new Image(inputStream);
			BorderPane root = new BorderPane();

			root.setBackground(new javafx.scene.layout.Background(new javafx.scene.layout.BackgroundImage(img,
					javafx.scene.layout.BackgroundRepeat.NO_REPEAT, javafx.scene.layout.BackgroundRepeat.NO_REPEAT,
					javafx.scene.layout.BackgroundPosition.DEFAULT, javafx.scene.layout.BackgroundSize.DEFAULT)));

			VBox v = new VBox();
			Text t = new Text("Choose a color: ");
			t.setStyle("-fx-font-size: 20px;" + "-fx-background-color: #D8BFD8;");
			HBox h = new HBox();
			Circle pink = new Circle(40);
			pink.setFill(Color.web("#ff6699"));
			Circle purple = new Circle(40);
			purple.setFill(Color.web("#993399"));
			Circle lightpink = new Circle(40);
			lightpink.setFill(Color.web("#ffccdd"));
			Circle lightpurple = new Circle(40);
			lightpurple.setFill(Color.web("#cc99ff"));
			h.getChildren().addAll(pink, purple, lightpink, lightpurple);
			v.getChildren().addAll(t, h);
			v.setSpacing(10);
			h.setSpacing(10);
			root.setCenter(v);

			pink.setOnMouseClicked(e -> {
				this.chosenColor = "#ff6699";
				root.getChildren().remove(v);
				startGame(root);
			});

			purple.setOnMouseClicked(e -> {
				this.chosenColor = "#993399";
				root.getChildren().remove(v);
				startGame(root);
			});

			lightpink.setOnMouseClicked(e -> {
				this.chosenColor = "#ffccdd";
				root.getChildren().remove(v);
				startGame(root);
			});

			lightpurple.setOnMouseClicked(e -> {
				this.chosenColor = "#cc99ff";
				root.getChildren().remove(v); 
				startGame(root);
			});
			
			Button back = new Button("Back");
			back.setStyle("-fx-font-size: 16px; -fx-background-color: #ccccff;");

			back.setOnAction(e -> {
				LevelPage s = new LevelPage(username, password);
				try {
					s.start(stage);
				} catch (Exception e11) {
					e11.printStackTrace();
				}
			});

			StackPane bottomRightPane = new StackPane(back);
			bottomRightPane.setAlignment(javafx.geometry.Pos.BOTTOM_RIGHT);

			root.setBottom(bottomRightPane);

			Scene scene = new Scene(root, img.getWidth(), img.getHeight());
			stage.setResizable(false);
			stage.setScene(scene);
			stage.setTitle("Balloon Assassinator");
			stage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void startGame(BorderPane root) {
		startBalloonGeneration(root);
		startTimer(root);
	}

	private void startBalloonGeneration(BorderPane root) {
	    // timeline for continuous balloon generation
	    Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1.5), event -> generateBalloon(root)));

	    //cycle count to the number of seconds in the timer
	    timeline.setCycleCount(timerSeconds);
	    
	    //stop the game when the timeline finishes
	    timeline.setOnFinished(e -> {
	        try {
				stopGame();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
	        timeline.stop();
	    });

	    timeline.play();
	}

	private void stopGame() throws Exception {
	    if (score >= 50)
	        System.out.println("You win");
	    else
	        System.out.println("You lose");
	    timerTimeline.stop(); 
	    stop();
	    System.out.println("Final Score: " + score);
	    try {
	        Class.forName("com.mysql.cj.jdbc.Driver");
	        System.out.println("Driver loaded");
	        // Establish a connection
	        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/hci_project", "root", "");
	        String querypic = "UPDATE users SET level2score = '" + score + "' WHERE username = '" + username
	                + "' AND password='" + password + "';";
	        String querypict = "UPDATE users SET level2time = '30' WHERE username = '" + username
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




	private void generateBalloon(BorderPane root) {
	    if (timerSeconds == 0) {
	        return;
	    }

	    Ellipse balloon = createBalloon();
	    balloon.setLayoutX(Math.random() * (root.getWidth() - balloon.getRadiusX()));
	    balloon.setLayoutY(root.getHeight()); // Start from the bottom

	    balloon.setOnMouseClicked(event -> {
	        if (balloon.getFill().equals(Color.web(chosenColor))) {
	            score += 10;
	        } else {
	            score -= 5;
	        }
	        root.getChildren().remove(balloon);
	        System.out.println("Score: " + score);
	    });

	    root.getChildren().add(balloon);

	    // Set the ToY property to a large negative value to move the balloon above the top of the scene
	    TranslateTransition transition = new TranslateTransition(Duration.seconds(1.5), balloon);
	    transition.setToY(-root.getHeight()); // Set to a value larger than the scene height
	    transition.play();
	}



	private void startTimer(BorderPane root) {
	    Text timerText = new Text("Time Left: " + timerSeconds + "s");
	    timerText.setStyle("-fx-font-size: 20px;" + "-fx-background-color: #D8BFD8;");
	    root.setTop(timerText);

	    timerTimeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
	        timerSeconds--;
	        timerText.setText("Time Left: " + timerSeconds + "s");
	        if (timerSeconds <= 0) {
	            try {
					stopGame();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        }
	    }));
	    timerTimeline.setCycleCount(Timeline.INDEFINITE); // Set cycle count to INDEFINITE
	    timerTimeline.play();
	}


	private Ellipse createBalloon() {
		Ellipse balloon = new Ellipse();
		balloon.setRadiusX(30);
		balloon.setRadiusY(50);

		String color = balloonColors[(int) (Math.random() * balloonColors.length)];
		balloon.setFill(Color.web(color));

		return balloon;
	}
}
