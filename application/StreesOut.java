package application;

import static java.util.concurrent.TimeUnit.SECONDS;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;
import java.util.Timer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class StreesOut extends Application {
	private final String username;
	private final String password;

	public StreesOut(String username, String password) {

		super();
		this.username = username;
		this.password = password;// TODO Auto-generated constructor stub
	}

	private static final int WIDTH = 400;
	private static final int HEIGHT = 400;

	private Circle circle;
	private static Circle dot;
	private static int score = 0;

	public void start(Stage primaryStage) throws FileNotFoundException, SQLException, ClassNotFoundException {
		Class.forName("com.mysql.cj.jdbc.Driver");
		System.out.println("Driver loaded");
		// Establish a connection
		Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/hci_project", "root", "");

		Group g = new Group();
		String bgpath = "C:\\Users\\sabah\\OneDrive\\Desktop\\Games - HCI Project\\Games\\src\\application\\rotate.jpg";
		Image bgdimg = new Image(new FileInputStream(bgpath));
		ImageView bgdview = new ImageView(bgdimg);
		g.getChildren().add(bgdview);
		Scene endscene = new Scene(g);

		Pane root = new Pane();
		root.getChildren().add(bgdview);
		circle = new Circle(WIDTH / 2, HEIGHT / 2, 100);
		circle.setStroke(Color.BLACK);

		root.getChildren().add(circle);

		dot = new Circle((WIDTH / 2) + 100, HEIGHT / 2, 5);
		dot.setFill(Color.RED);
		root.getChildren().add(dot);
		String sp = "C:\\Users\\sabah\\OneDrive\\Desktop\\Games - HCI Project\\Games\\src\\application\\spin.jpg";
		Image wheel = new Image(new FileInputStream(sp));

		ImageView wheelview = new ImageView(wheel);
		wheelview.setFitWidth(150);
		wheelview.setFitHeight(150);
		circle.setFill(new ImagePattern(wheel));
		// root.getChildren().add(wheelview);
		Timeline animation = new Timeline(new KeyFrame(Duration.millis(16), e -> rotateCircle()));
		animation.setCycleCount(Animation.INDEFINITE);
		animation.play();

		Scene scene = new Scene(root, WIDTH, HEIGHT);

		scene.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.G) {

				if (dot.getFill().equals(Color.GREEN)) {
					score += 10;
				} else {
					score = Math.max(0, score - 5);
				}

				System.out.println("Score: " + score);
			}
		});
		double delay = (2 + new Random().nextInt(5)) * 1000;
		Timeline changeColor = new Timeline(new KeyFrame(Duration.millis(delay), e -> {
			toggleDotColor();
		}));
		changeColor.setCycleCount(1000);
		changeColor.play();

		final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

		final Runnable runnable = new Runnable() {
			int countdown = 30;

			public void run() {
				System.out.println(countdown);
				countdown--;

				if (countdown == 0) {
					System.out.println("Timer Over!");
					changeColor.pause();
					animation.pause();
					String querypict = "UPDATE users SET Level4Time = '30' WHERE username = '" + username
							+ "' AND password='" + password + "';";

					String querypic = "UPDATE users SET Level4Score = '" + score + "' WHERE username = '" + username
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
					String s = "GAME OVER! YOUR SCORE IS: " + score;

					root.getChildren().add(new Text(100, 100, s));
					primaryStage.close();

					scheduler.shutdown();

				}
			}
		};
		scheduler.scheduleAtFixedRate(runnable, 0, 1, SECONDS);

		primaryStage.setResizable(false);
		primaryStage.setTitle("Rotating Circle Game");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	private void rotateCircle() {
		circle.setRotate(circle.getRotate() + 1);

		if (circle.getRotate() % 360 == 0) {
			// toggleDotColor();
		}
	}

	static Timer timer = new Timer();

	// new Task().run();

	private static void toggleDotColor() {
		if (dot.getFill() == Color.GREEN) {
			dot.setFill(Color.RED);
		} else {
			dot.setFill(Color.GREEN);
		}

		// greenDot = !greenDot;

		// Revert color to red after 2 seconds
		Timeline revertColor = new Timeline(new KeyFrame(Duration.seconds(2), e -> {
			dot.setFill(Color.RED);
			if (dot.getFill() == Color.GREEN) {
				// If 'g' wasn't pressed during the green phase, decrement score
				score = Math.max(0, score - 5);
				System.out.println("Miss! Score: " + score);
			}
		}));
		revertColor.play();

	}

//	@Override
//	public void run() {
//		int delay = (2 + new Random().nextInt(5)) * 1000;
//        timer.schedule(toggleDotColor(), delay);
//       // toggleDotColor();
//       System.out.println(new Date());
//		
//	}
	public static void main(String[] args) {

		launch(args);

	}

}