package application;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {

			primaryStage.initStyle(StageStyle.TRANSPARENT);

			Parent root = FXMLLoader.load(getClass().getResource("ui.fxml"));

			Button playPauseButton = (Button) root.lookup("#playPauseButton");
			playPauseButton.setGraphic(new ImageView("application/playButton.png"));

			Button prevButton = (Button) root.lookup("#prevButton");
			prevButton.setGraphic(new ImageView("application/prevButton.png"));

			Button nextButton = (Button) root.lookup("#nextButton");
			nextButton.setGraphic(new ImageView("application/nextButton.png"));

			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

			Parent loaderRoot = FXMLLoader.load(getClass().getResource("ld.fxml"));
			Pane p = (Pane) loaderRoot.lookup("#splash");
			ImageView iv = (ImageView) loaderRoot.lookup("#splashImage");

			p.setStyle("-fx-background-color: rgba(0, 0, 0, 0.0);");
			iv.setStyle("-fx-background-color: rgba(0, 0, 0, 0.0);");

			primaryStage.getIcons().add(new Image("application/splashScreen.png"));
			final Scene ldscene = new Scene(loaderRoot);
			ldscene.setFill(Color.TRANSPARENT);
			primaryStage.setScene(ldscene);
			primaryStage.show();

			Task<Void> sleeper = new Task<Void>() {
				@Override
				protected Void call() throws Exception {
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
					}
					return null;
				}
			};

			sleeper.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
				@Override
				public void handle(WorkerStateEvent event) {
					Stage decorated = new Stage();
					decorated.initStyle(StageStyle.DECORATED);
					decorated.getIcons().add(new Image("application/splashScreen.png"));
					decorated.setScene(null);
					decorated.setScene(scene);
					decorated.setOnCloseRequest(e -> Platform.exit());
					decorated.setScene(scene);
					decorated.setTitle("Noisey");
					decorated.setResizable(false);
					decorated.show();
					primaryStage.hide();
				}
			});

			new Thread(sleeper).start();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args){
		launch(args);
	}

}
