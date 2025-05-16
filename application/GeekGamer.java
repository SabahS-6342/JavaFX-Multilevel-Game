package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class GeekGamer extends Application {
	private final String username;
	private final String password;

	public GeekGamer(String username, String password) {
		super();
		this.username = username;
		this.password = password;
	}

	String imagePath = "cs.jpeg";
	private int currentQuestionIndex = 0;
	private List<Image> images;
	private List<String> characterList;
	private int score = 0;

	public static void main(String[] args) {
		launch();
	}

	public void start(Stage stage)
			throws FileNotFoundException, InterruptedException, ClassNotFoundException, SQLException {

		try {
			InputStream inputStream = getClass().getResourceAsStream(imagePath);
			if (inputStream == null) {
				throw new IllegalArgumentException("Image not found: " + imagePath);
			}

			Image img = new Image(inputStream);
			BorderPane root = new BorderPane();

			root.setBackground(new javafx.scene.layout.Background(new javafx.scene.layout.BackgroundImage(img,
					javafx.scene.layout.BackgroundRepeat.NO_REPEAT, javafx.scene.layout.BackgroundRepeat.NO_REPEAT,
					javafx.scene.layout.BackgroundPosition.DEFAULT, javafx.scene.layout.BackgroundSize.DEFAULT)));

			HBox h = new HBox();

			answerButtons a = new answerButtons('a');
			answerButtons b = new answerButtons('b');
			answerButtons c = new answerButtons('c');
			answerButtons d = new answerButtons('d');

			h.getChildren().addAll(a.stackPane, b.stackPane, c.stackPane, d.stackPane);
			h.setTranslateX(165);
			h.setTranslateY(435);
			h.setSpacing(50);
			root.getChildren().add(h);

			images = loadImages("C:\\Users\\sabah\\OneDrive\\Desktop\\Games - HCI Project\\qs");
			characterList = readCharactersFromFile("C:\\Users\\sabah\\OneDrive\\Desktop\\Games - HCI Project\\a.txt");

			ImageView questionImageView = new ImageView(images.get(currentQuestionIndex));
			questionImageView.setTranslateX(89);
			questionImageView.setTranslateY(90);
			questionImageView.setFitWidth(460);
			questionImageView.setFitHeight(309);

			root.setTop(questionImageView);

			currentQuestionIndex = nextIndex();
			a.button.setOnAction(event -> checkAnswer(a, 'a'));
			b.button.setOnAction(event -> checkAnswer(b, 'b'));
			c.button.setOnAction(event -> checkAnswer(c, 'c'));
			d.button.setOnAction(event -> checkAnswer(d, 'd'));

			Button back = new Button("Back");
			back.setStyle("-fx-font-size: 16px; -fx-background-color: lightblue;");

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
			stage.setTitle("Geek-Gamer");
			stage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private class answerButtons {
		char title;
		Button button;
		StackPane stackPane;

		public answerButtons(char title) {
			this.title = title;

			this.button = new Button(String.valueOf(title));

			this.stackPane = new StackPane();
			this.stackPane.getChildren().add(this.button);
			this.stackPane.setPrefSize(100, 100);

			this.button.setStyle("-fx-font-size: 16px; -fx-background-color: #F2E5B7;");
		}
	}

	private int nextIndex() {
		int index;
		if (currentQuestionIndex < images.size() - 1) {
			index = currentQuestionIndex + 1;
		} else {
			// Handle when there are no more questions
			System.out.println("No more questions!");
			index = -1; // You can return -1 or any other value to indicate no more questions
		}
		return index;
	}

	private List<File> getImageFiles(String folderPath) {
		File folder = new File(folderPath);
		if (folder.exists() && folder.isDirectory()) {
			File[] files = folder.listFiles();
			List<File> imageFiles = new ArrayList<>();
			for (File file : files) {
				if (file.isFile() && isImageFile(file)) {
					imageFiles.add(file);
				}
			}
			return imageFiles;
		} else {
			System.out.println("Folder not found: " + folderPath);
			return new ArrayList<>();
		}
	}

	private boolean isImageFile(File file) {
		String fileName = file.getName().toLowerCase();
		return fileName.endsWith(".png") || fileName.endsWith(".jpg") || fileName.endsWith(".jpeg");
	}

	private List<Image> loadImages(String folderPath) {
		List<File> imageFiles = getImageFiles(folderPath);
		List<Image> images = new ArrayList<>();
		for (File file : imageFiles) {
			Image image = new Image(file.toURI().toString());
			images.add(image);
		}
		return images;
	}

	private static List<String> readCharactersFromFile(String filePath) {
		List<String> characterList = new ArrayList<>();

		try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
			String line;

			// Read each line (containing one character) from the file
			while ((line = reader.readLine()) != null) {
				// Add the character to the ArrayList
				characterList.add(line.trim()); // Use trim to remove leading/trailing whitespaces
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return characterList;
	}

	private void checkAnswer(answerButtons button, char userAnswer) {
		char correctAnswer = characterList.get(currentQuestionIndex).charAt(0);
		if (userAnswer == correctAnswer) {
			score += 10;
			System.out.println(score);
		} else {
			score = Math.max(0, score - 3);
			System.out.println(score);
		}
		int nextIndex = nextIndex();
		if (nextIndex != -1) {
			currentQuestionIndex = nextIndex;
			updateDisplay((BorderPane) button.stackPane.getScene().getRoot());
		} else {
			System.out.println("Game Over!");
			try {
				gameOver();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void updateDisplay(BorderPane root) {
		HBox h = new HBox();

		answerButtons a = new answerButtons('a');
		answerButtons b = new answerButtons('b');
		answerButtons c = new answerButtons('c');
		answerButtons d = new answerButtons('d');

		h.getChildren().addAll(a.stackPane, b.stackPane, c.stackPane, d.stackPane);
		h.setTranslateX(165);
		h.setTranslateY(435);
		h.setSpacing(50);
		root.setCenter(h);

		ImageView questionImageView = new ImageView(images.get(currentQuestionIndex));
		questionImageView.setTranslateX(89);
		questionImageView.setTranslateY(90);
		questionImageView.setFitWidth(460);
		questionImageView.setFitHeight(309);

		root.setTop(questionImageView);

		a.button.setOnAction(event -> checkAnswer(a, 'a'));
		b.button.setOnAction(event -> checkAnswer(b, 'b'));
		c.button.setOnAction(event -> checkAnswer(c, 'c'));
		d.button.setOnAction(event -> checkAnswer(d, 'd'));

	}

	public void gameOver() throws Exception {
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
			String querypic = "UPDATE users SET level1score = '" + score + "' WHERE username = '" + username
					+ "' AND password='" + password + "';";
			try {
				PreparedStatement p = connection.prepareStatement(querypic);
				p.execute();
				System.out.println("Score updated in the database!");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
