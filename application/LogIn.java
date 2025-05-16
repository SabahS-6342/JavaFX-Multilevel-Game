package application;

import java.beans.EventHandler;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Border;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.sql.*;

public class LogIn extends Application {
	private Statement stmt;
	static boolean signupflag = false;

	public int findMyUser(String username, String password, Group pane)
			throws SQLException, FileNotFoundException, ClassNotFoundException {
		Class.forName("com.mysql.cj.jdbc.Driver");
		System.out.println("Driver loaded");
		// Establish a connection
		Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/hci_project", "root", "");

		String query = "SELECT userid FROM users WHERE username = '" + username + "' AND password='" + password + "';";
		ResultSet rset = stmt.executeQuery(query);
		int t;
		if (rset.next()) {
			t = Integer.parseInt(rset.getString(1));
			System.out.println(rset.toString());
			String queryimg = "SELECT profilepic FROM `users` WHERE username = '" + username + "' AND password='"
					+ password + "';";
			ResultSet rsetimg = stmt.executeQuery(queryimg);
			if (rsetimg.next() && rsetimg.getString(1) != null) {
				System.out.println(rsetimg.getString(1));
				Image tb = new Image(new FileInputStream(rsetimg.getString(1)));
				// ImageView tbview = new ImageView(tb);
				Rectangle ppframe = new Rectangle(320, 5, 150, 150);
				ppframe.setFill(new ImagePattern(tb));
				pane.getChildren().add(ppframe);
			} else {
				Text ppic = new Text("Profile Pic");
				ppic.setFont(Font.font("Led Font", 30));
				ppic.setFill(Color.GREEN);
				ppic.setLayoutX(335);
				ppic.setLayoutY(65);
				TextField lb3 = new TextField();
				lb3.setLayoutY(90);
				lb3.setLayoutX(320);
				lb3.setBlendMode(BlendMode.MULTIPLY);
				lb3.setPromptText("filepath.");
				Button b = new Button("validate");
				b.setLayoutY(90);
				b.setLayoutX(476);
				Button bs = new Button("set");
				bs.setLayoutY(90);
				bs.setLayoutX(536);
				bs.setDisable(true);
				pane.getChildren().addAll(ppic, lb3, b, bs);

				b.setOnAction(ex -> {
					try {
						String picpath = lb3.getText();
						File file = new File(picpath);
						if (!file.exists()) {
							throw new FileNotFoundException("file not found. try to enter VALID IMAGE PATH PLEASEE");
						}
						Image pp = new Image(new FileInputStream(file));
						Rectangle ppframe = new Rectangle(320, 5, 150, 150);
						ppframe.setFill(new ImagePattern(pp));
						pane.getChildren().removeAll(ppic, b, lb3);

						pane.getChildren().addAll(ppframe);
						bs.setDisable(false);
						bs.setOnAction(f -> {
							String querypic = "UPDATE users SET profilepic = '" + picpath + "' WHERE username = '"
									+ username + "' AND password='" + password + "';";
							try {
								PreparedStatement p = connection.prepareStatement(querypic);
								p.execute();
								System.out.println("SUCEESSS");
							} catch (SQLException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						});
					} catch (FileNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				});

			}
			return t;

		} else {
			return -1;
		}
	}

	@Override
	public void start(Stage primaryStage)
			throws FileNotFoundException, InterruptedException, SQLException, ClassNotFoundException {
		Class.forName("com.mysql.cj.jdbc.Driver");
		System.out.println("Driver loaded");
		// Establish a connection
		Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/hci_project", "root", "");
		System.out.println("Database connected");

		this.stmt = connection.createStatement();
		Group pane = new Group();
		// Group panesignup = new Group();
		// Scene scenesignup = new Scene(panesignup,800,500);

		String bgpath = "C:\\Users\\sabah\\OneDrive\\Desktop\\Games - HCI Project\\Games\\src\\application\\backLog.jpg";
		String textbox = "C:\\Users\\sabah\\OneDrive\\Desktop\\Games - HCI Project\\Games\\src\\application\\textbox.png";
		String rando = "C:\\Users\\sabah\\OneDrive\\Desktop\\Games - HCI Project\\Games\\src\\application\\cloudexpandingshrinking.gif";
		String signup = "C:\\Users\\sabah\\OneDrive\\Desktop\\Games - HCI Project\\Games\\src\\application\\signup.png";
		String login = "C:\\Users\\sabah\\OneDrive\\Desktop\\Games - HCI Project\\Games\\src\\application\\login.png";
		String start = "C:\\Users\\sabah\\OneDrive\\Desktop\\Games - HCI Project\\Games\\src\\application\\start.png";
		Image st = new Image(new FileInputStream(start));
		ImageView stview = new ImageView(st);
		Image bgdimg = new Image(new FileInputStream(bgpath));
		ImageView bgdview = new ImageView(bgdimg);
		Image tb = new Image(new FileInputStream(textbox));
		ImageView tbview = new ImageView(tb);
		ImageView tbview2 = new ImageView(tb);
		ImageView tbview3 = new ImageView(tb);
		stview.setFitWidth(200);
		stview.setFitHeight(120);
		stview.setX(293);
		stview.setY(305);
		bgdview.setFitWidth(800);
		bgdview.setFitHeight(500);
		tbview.setFitWidth(150);
		tbview.setFitHeight(50);
		tbview.setX(320);
		tbview.setY(185);
		tbview2.setFitWidth(150);
		tbview2.setFitHeight(50);
		tbview2.setX(320);
		tbview2.setY(270);
		tbview3.setFitWidth(150);
		tbview3.setFitHeight(50);
		tbview3.setX(320);
		tbview3.setY(75);
		Text name = new Text("Name");
		name.setFont(Font.font("Led Font", 30));
		name.setFill(Color.GREEN);
		name.setLayoutX(355);
		name.setLayoutY(180);
		TextField lb1 = new TextField();
		lb1.setLayoutY(195);
		lb1.setLayoutX(320);
		lb1.setBlendMode(BlendMode.MULTIPLY);
		lb1.setPromptText("8 chars.");
		Text pass = new Text("Password");
		pass.setFont(Font.font("Britannic", 30));
		pass.setFill(Color.GREEN);
		pass.setLayoutX(335);
		pass.setLayoutY(265);
		TextField lb2 = new TextField();
		lb2.setLayoutY(275);
		lb2.setLayoutX(320);
		lb2.setPromptText("8 chars");
		lb2.setBlendMode(BlendMode.MULTIPLY);
		Image su = new Image(new FileInputStream(signup));
		ImageView suview = new ImageView(su);
		Image lo = new Image(new FileInputStream(login));
		ImageView loview = new ImageView(lo);
		suview.setFitWidth(110);
		suview.setFitHeight(85);
		suview.setX(280);
		suview.setY(320);
		loview.setFitWidth(110);
		loview.setFitHeight(90);
		loview.setX(400);
		loview.setY(320);
		Text ppic = new Text("Profile Pic");
		ppic.setFont(Font.font("Led Font", 30));
		ppic.setFill(Color.GREEN);
		ppic.setLayoutX(335);
		ppic.setLayoutY(65);
		TextField lb3 = new TextField();
		lb3.setLayoutY(90);
		lb3.setLayoutX(320);
		lb3.setBlendMode(BlendMode.MULTIPLY);
		lb3.setPromptText("filepath.");
		loview.setOnMouseClicked(e -> {
			try {

				System.out.println(this.findMyUser(lb1.getText(), lb2.getText(), pane));
				if (this.findMyUser(lb1.getText(), lb2.getText(), pane) != -1) {
					pane.getChildren().removeAll(loview, suview);
					pane.getChildren().add(stview);
					stview.setOnMouseClicked(f -> {
						LevelPage s = new LevelPage(lb1.getText(), lb2.getText());
						try {
							s.start(primaryStage);
						} catch (Exception e11) {
							e11.printStackTrace();
						}
					});
					System.out.println("bravo");

				}

			} catch (SQLException | FileNotFoundException | ClassNotFoundException e1) {
				e1.printStackTrace();
			}
		});

		suview.setOnMouseClicked(e -> {
			suview.setX(330);
			pane.getChildren().remove(loview);
			Button b = new Button("validate");
			b.setLayoutY(90);
			b.setLayoutX(476);
			pane.getChildren().addAll(ppic, lb3, b);

			b.setOnAction(ex -> {
				try {
					String picpath = lb3.getText();
					File file = new File(picpath);
					if (!file.exists()) {
						throw new FileNotFoundException("file not found. try to enter VALID IMAGE PATH PLEASEE");
					}
					Image pp = new Image(new FileInputStream(file));

					ImageView ppview = new ImageView(pp);
					suview.setX(280);
					pane.getChildren().add(ppview);
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				}
			});
			suview.setOnMouseClicked(f -> {
				try {
					if (this.addUser(lb1.getText(), lb2.getText(), lb3.getText()) == true) {
						pane.getChildren().removeAll(ppic, lb3, b);
						pane.getChildren().add(loview);
					}
				} catch (ClassNotFoundException e1) {
					e1.printStackTrace();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				;

			});

		});

		pane.getChildren().addAll(bgdview, tbview, tbview2, suview, loview, lb1, lb2, name, pass);
		primaryStage.setTitle("MP3PLAYER");
		Scene scenelogin = new Scene(pane, 800, 500);
		primaryStage.setScene(scenelogin);
		primaryStage.show();

	}

	public static void main(String[] args) {
		String s = "janamana";
		System.out.println(s.substring(0, 8));
		launch(args);

	}

	private boolean addUser(String username, String password, String email)
			throws SQLException, ClassNotFoundException {
		Class.forName("com.mysql.cj.jdbc.Driver");
		System.out.println("Driver loaded");
		// Establish a connection
		Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/hci_project", "root", "");
		// TODO Auto-generated method stub
		if (username.equals("") || password.equals("")) {
			return false;
		} else if (!username.equals("") && !password.equals("") && !email.equals("")) {
			String query = "insert into users (username, password, profilepic) values ('" + username.substring(0, 8)
					+ "', '" + password.substring(0, 8) + "', '" + email + "')";
			PreparedStatement pstmt = connection.prepareStatement(query);
			int affectedRows = pstmt.executeUpdate();

			if (affectedRows > 0) {
				return true;
			} else {
				return false;
			}
		} else if (!username.equals("") && !password.equals("") && email.equals("")) {
			String query = "insert into users (username, password) values ('" + username + "', '" + password + "')";
			PreparedStatement pstmt = connection.prepareStatement(query);
			int affectedRows = pstmt.executeUpdate();

			if (affectedRows > 0) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

}
