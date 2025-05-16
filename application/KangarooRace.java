package application;

import javafx.scene.input.KeyCode;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.input.KeyEvent;
import javafx.application.Application;
import javafx.scene.image.ImageView;
import javafx.beans.value.ChangeListener;
import javafx.animation.PathTransition;
import javafx.animation.PathTransition.OrientationType;
import javafx.animation.RotateTransition;
import javafx.beans.value.ObservableValue;
import static java.util.concurrent.TimeUnit.SECONDS;
import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.BreakIterator;
import java.util.ArrayList;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcTo;
import javafx.animation.PathTransition;
import javafx.animation.PathTransition.OrientationType;
import javafx.scene.image.Image;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Circle;
import javafx.scene.shape.ClosePath;
import javafx.scene.text.Text;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.animation.Timeline;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.event.ActionEvent;
import javafx.scene.shape.Line;
import javafx.scene.shape.LineTo;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import javafx.collections.FXCollections;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Slider;
import javafx.animation.PathTransition;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.QuadCurveTo;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Path;
import javafx.animation.TranslateTransition;
import javafx.scene.shape.Rectangle;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class KangarooRace extends Application {

	private final String username;
	private final String password;

	public KangarooRace(String username, String password) {

		super();
		this.username = username;
		this.password = password;// TODO Auto-generated constructor stub
	}

	private static int score = 0;
	final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

	@Override // Override the start method in the Application class
	public void start(Stage primaryStage)
			throws FileNotFoundException, InterruptedException, ClassNotFoundException, SQLException {
		Class.forName("com.mysql.cj.jdbc.Driver");
		System.out.println("Driver loaded");
		// Establish a connection
		Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/hci_project", "root", "");

		Group pane = new Group();

		String imgpath = "C:\\Users\\sabah\\OneDrive\\Desktop\\Games - HCI Project\\Games\\src\\application\\kangaroocropped.gif";
		String bgpath = "C:\\Users\\sabah\\OneDrive\\Desktop\\Games - HCI Project\\Games\\src\\application\\backLog.jpg";
		String cac = "C:\\Users\\sabah\\OneDrive\\Desktop\\Games - HCI Project\\Games\\src\\application\\cactus.png";
		String bir = "C:\\Users\\sabah\\OneDrive\\Desktop\\Games - HCI Project\\Games\\src\\application\\flappy.gif";
		String cl = "C:\\Users\\sabah\\OneDrive\\Desktop\\Games - HCI Project\\Games\\src\\application\\cloudexpandingshrinking.gif";
		Image bgdimg = new Image(new FileInputStream(bgpath));
		ImageView bgdview = new ImageView(bgdimg);
		Image climg = new Image(new FileInputStream(cl));
		ImageView climgview = new ImageView(climg);
		climgview.setX(700);
		climgview.setY(100);
		climgview.setFitHeight(100);
		climgview.setFitWidth(170);
		Duration duration = Duration.millis(30000);

		TranslateTransition transition = new TranslateTransition(duration, climgview);
		transition.setByX(-800);
		transition.setAutoReverse(true);
		transition.setCycleCount(100);
		transition.play();
		bgdview.setFitWidth(800);
		bgdview.setFitHeight(500);
		pane.getChildren().add(bgdview);
		// below: kangaroo img
		Image image = new Image(new FileInputStream(imgpath));
		Image cactus = new Image(new FileInputStream(cac));
		Image bird = new Image(new FileInputStream(bir));
		ImageView kang = new ImageView(image);
		ImageView cactuss = new ImageView(cactus);

		kang.setFitWidth(150.0);

		Rectangle imgview = new Rectangle(270, 366, 100, 150);
		imgview.setArcHeight(50);
		imgview.setArcWidth(50);
		imgview.setRotationAxis(Rotate.Y_AXIS); // added this cuz without the image is only flipped upside down
		imgview.setRotate(180);
		imgview.setFill(new ImagePattern(image));
		pane.getChildren().add(imgview);
		String path = "C:\\Users\\sabah\\OneDrive\\Desktop\\Games - HCI Project\\Games\\src\\application\\idea22.mp3";
		Media media = new Media(new File(path).toURI().toString());
		final Runnable runnable = new Runnable() {
			static int countdown = 30;

			public void run() {
				System.out.println(countdown);
				countdown--;

				if (countdown == 0) {
					System.out.println("Timer Over!");
					pane.getChildren().remove(imgview);
					primaryStage.close();
					String s = "GAME OVER! YOUR SCORE IS: " + score;

					pane.getChildren().add(new Text(100, 100, s));
					String querypic = "UPDATE users SET Level5Score = '" + score + "' WHERE username = '" + username
							+ "' AND password='" + password + "';";
					String querypict = "UPDATE users SET Level5Time = '30' WHERE username = '" + username
							+ "' AND password='" + password + "';";

					try {
						PreparedStatement p = connection.prepareStatement(querypic);
						p.execute();
						PreparedStatement pt = connection.prepareStatement(querypict);
						pt.execute();
						System.out.println("SUCEESSS");
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					scheduler.shutdown();
				}
			}
		};
		scheduler.scheduleAtFixedRate(runnable, 3, 1, SECONDS);

		MediaPlayer mediaPlayer = new MediaPlayer(media);
		MediaView mediaView = new MediaView(mediaPlayer);
		imgview.setX(50);
		imgview.setY(300);
		Button playButton = new Button("🔇");
		playButton.setShape(new Circle(15));
		playButton.setStyle("-fx-background-color: gold;-fx-border-width:2px;-fx-border-color:red;");
		playButton.setLayoutX(600);
		playButton.setLayoutY(10);
		playButton.setOnMouseClicked(e -> {
			if (playButton.getText().equals("🔇")) {
				mediaPlayer.play();
				playButton.setText("🔊");
			} else {
				mediaPlayer.pause();
				playButton.setText("🔇");
			}
		});
		for (int i = 1; i < 15; i++) {
			int choice = (int) (1 + Math.random() * 2);
			System.out.println(i + " choice: " + choice);
			makeobstacle(pane, imgview, choice, i);
		}

		pane.getChildren().addAll(climgview, playButton);
		Scene scene = new Scene(pane, 650, 500);

		scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
			if (event.getCode() == KeyCode.SPACE) {
				Path newpath = new Path();

				newpath.getElements().add(new MoveTo(100, 190));
				// newpath.getElements().add(new LineTo(imgview.getX(),imgview.getY()-150));
				newpath.getElements().add(new QuadCurveTo(100, 180, 100, 366));
				PathTransition pathTransition = new PathTransition();

				pathTransition.setDuration(Duration.millis(1500));
				pathTransition.setNode(imgview);
				pathTransition.setPath(newpath);
				pathTransition.setCycleCount(1);

				pathTransition.setAutoReverse(true);

				pathTransition.play();

			}
		});

		scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
			if (event.getCode() == KeyCode.D) {
				Path newpath = new Path();

				newpath.getElements().add(new MoveTo(100, 420));
				// newpath.getElements().add(new LineTo(imgview.getX(),imgview.getY()-150));
				newpath.getElements().add(new QuadCurveTo(100, 470, 100, 366));
				PathTransition pathTransition = new PathTransition();

				pathTransition.setDuration(Duration.millis(1500));
				pathTransition.setNode(imgview);
				pathTransition.setPath(newpath);
				pathTransition.setCycleCount(1);

				pathTransition.setAutoReverse(true);

				pathTransition.play();

			}

		});

		primaryStage.setResizable(false);
		primaryStage.setTitle("MP3PLAYER");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	/**
	 * The main method is only needed for the IDE with limited JavaFX support. Not
	 * needed for running from the command line.
	 */
	public static void main(String[] args) {

		launch(args);

	}

	private void checkBoundsObstacle(Shape obstacle, Group pane, Rectangle rec) {
		boolean collisionDetected = false;
		// block.getBoundsInParent().intersects(static_bloc. getBoundsInParent())
		if (rec.getBoundsInParent().intersects(obstacle.getBoundsInParent())) {
			collisionDetected = true;
		}

		if (collisionDetected) {
			score -= 5;
			scheduler.shutdown();
			pane.getChildren().remove(rec);
			Text t = new Text("GAME OVER");
			t.setX(100);
			t.setY(100);

			// t.setFont(Font.font("Times New Roman", FontWeight.EXTRA_BOLD,
			// FontPosture.ITALIC, 42));
			t.setStyle("-fx-text-fill: green; -fx-font-size: 46px;");
			pane.getChildren().add(t);
			// System.out.println("DETECTED");

		} else {
			// System.out.println("no");
		}
	}

	public void makeobstacle(Group pane, Rectangle imgview, int choice, int i)
			throws FileNotFoundException, InterruptedException {
		// java.util.concurrent.TimeUnit.SECONDS.sleep(2);
		String cac = "C:\\Users\\sabah\\OneDrive\\Desktop\\Games - HCI Project\\Games\\src\\application\\cactus.png";
		String bir = "C:\\Users\\sabah\\OneDrive\\Desktop\\Games - HCI Project\\Games\\src\\application\\flappy.gif";

		Image cactus = new Image(new FileInputStream(cac));
		Image bird = new Image(new FileInputStream(bir));
		String s = "Congrats! Your Score is: " + score;
		Text t = new Text(s);
		t.setX(100);
		t.setY(100);

		t.setFont(Font.font("Times New Roman", FontWeight.EXTRA_BOLD, FontPosture.ITALIC, 42));

		if (choice == 1) {

			Rectangle rec = new Rectangle(800, 375, 42, 100);
			rec.setArcHeight(50);
			rec.setArcWidth(50);

			rec.setFill(new ImagePattern(cactus));
			TranslateTransition tt = new TranslateTransition(Duration.millis(8000), rec);
			tt.setNode(rec);
			tt.setFromX(rec.getX());
			tt.setToX(-900);
			tt.setDelay(Duration.millis(i * 2000));
			tt.setCycleCount(1);
			tt.setAutoReverse(true);
			tt.play();
			tt.setOnFinished(e -> {
				pane.getChildren().remove(rec);
				if (pane.getChildren().contains(imgview)) {
					score += 10;
					System.out.println(score);
				}
				if (i == 14 && pane.getChildren().contains(imgview)) {
					t.setText(String.valueOf(score));
					pane.getChildren().add(t);
				}
			});

			pane.getChildren().add(rec);
			rec.translateXProperty().addListener((observable, oldValue, newValue) -> {
				checkBoundsObstacle(rec, pane, imgview);
			});
		} else {
			// Circle c = new Circle(800, 305, 15);

			Rectangle c = new Rectangle(800, 285, 35, 30);
			c.setArcHeight(10);
			c.setArcWidth(12);
			c.setFill(new ImagePattern(bird));
			// return rec;
			pane.getChildren().add(c);
			TranslateTransition tt = new TranslateTransition(Duration.millis(7000), c);
			tt.setNode(c);
			tt.setFromX(c.getX());
			// tt.setFromX(c.getCenterX());
			tt.setToX(-900);
			tt.setDelay(Duration.millis(i * 2000));
			tt.setCycleCount(1);
			tt.setAutoReverse(true);
			tt.play();
			tt.setOnFinished(e -> {
				pane.getChildren().remove(c);
				if (pane.getChildren().contains(imgview)) {
					score += 10;
					System.out.println(score);
				}
				if (i == 14 && pane.getChildren().contains(imgview)) {
					t.setText(String.valueOf(score));
					pane.getChildren().add(t);
				}
			});
			c.translateXProperty().addListener((observable, oldValue, newValue) -> {
				checkBoundsObstacle(c, pane, imgview);
			});

		}

	}

}