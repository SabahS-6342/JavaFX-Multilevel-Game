package application;

import javafx.animation.ScaleTransition;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import java.io.FileInputStream;
import java.io.InputStream;
import javafx.util.Duration;

public class LevelPage extends Application {

	private final String username;
	private final String password;

	public LevelPage(String username, String password) {
		super();
		this.username = username;
		this.password = password;
	}

	public static void main(String[] args) {
		launch();
	}

	public void start(Stage stage) {

		try {

// background
			String imagePath = "C:\\Users\\sabah\\OneDrive\\Desktop\\Games - HCI Project\\Games\\src\\application\\levels.gif";
			Image bgdimg = new Image(new FileInputStream(imagePath));
			ImageView bgdview = new ImageView(bgdimg);

// levels
			HBox h = new HBox();

			VBox v1 = new VBox();
			LevelButtons l1 = new LevelButtons("Geek Gamer");
			v1.getChildren().add(l1.stackPane);
			v1.setAlignment(Pos.CENTER);

			VBox v2 = new VBox();
			LevelButtons l2 = new LevelButtons("  Baloon\nAssassinator");
			v2.getChildren().add(l2.stackPane);
			v2.setAlignment(Pos.CENTER);

			VBox v3 = new VBox();
			LevelButtons l3 = new LevelButtons("Focus Freak");
			v3.getChildren().add(l3.stackPane);
			v3.setAlignment(Pos.CENTER);

			VBox v4 = new VBox();
			LevelButtons l4 = new LevelButtons("Stress Out");
			v4.getChildren().add(l4.stackPane);
			v4.setAlignment(Pos.CENTER);

			VBox v5 = new VBox();
			LevelButtons l5 = new LevelButtons("Kangaroo\n  Race");
			v5.getChildren().add(l5.stackPane);
			v5.setAlignment(Pos.CENTER);

			h.getChildren().addAll(v1, v2, v3, v4, v5);
			h.setAlignment(Pos.CENTER);
			h.setSpacing(100);

			BorderPane root = new BorderPane();
			root.setCenter(h);
			root.setBackground(new Background(new BackgroundImage(bgdimg, BackgroundRepeat.NO_REPEAT,
					BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
					new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, true, true, true, true))));

// buttons on action
			l1.button.setOnAction(e1 -> {
				GeekGamer s = new GeekGamer(username, password);
				try {
					s.start(stage);
				} catch (Exception e11) {
					e11.printStackTrace();
				}
			});

			l2.button.setOnAction(e2 -> {
				 BalloonAssassinator s = new BalloonAssassinator(username, password);
				try {
					 s.start(stage);
				} catch (Exception e11) {
					e11.printStackTrace();
				}
			});

			l3.button.setOnAction(e3 -> {
				 FocusFreak s = new FocusFreak(username, password);
				try {
					 s.start(stage);
				} catch (Exception e11) {
					e11.printStackTrace();
				}
			});

			l4.button.setOnAction(e4 -> {
				 StreesOut s = new StreesOut(username, password);
				try {
					 s.start(stage);
				} catch (Exception e11) {
					e11.printStackTrace();
				}
			});

			l5.button.setOnAction(e5 -> {
				KangarooRace s = new KangarooRace(username, password);
				try {
					 s.start(stage);
				} catch (Exception e11) {
					e11.printStackTrace();
				}
			});

			Scene scene = new Scene(root, 1100, 600);

			bgdview.fitWidthProperty().bind(scene.widthProperty());
			bgdview.fitHeightProperty().bind(scene.heightProperty());

			stage.setScene(scene);
			stage.setTitle("Levels Application");
			stage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public class LevelButtons {
		String title;
		Button button;
		StackPane stackPane = new StackPane();

		public LevelButtons(String title) {
			this.title = title;

			Circle circle = new Circle(70);
			circle.getStyleClass().add("level-button-circle");
			circle.setFill(Color.web("#D8BFD8"));
			this.button = new Button(title);

			stackPane.getChildren().addAll(circle, this.button);
			stackPane.setPrefSize(100, 100);

			ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(0.2), stackPane);
			scaleTransition.setToX(1.2);
			scaleTransition.setToY(1.2);

			this.button.setOnMousePressed(event -> {
				scaleTransition.playFromStart();

			});

			this.button.setOnMouseReleased(event -> {
				scaleTransition.setToX(1.0);
				scaleTransition.setToY(1.0);
			});

			this.button.setStyle("-fx-font-size: 16px;" + "-fx-background-color: #D8BFD8;");
		}
	}

}
