package application;

import java.awt.Desktop;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.imageio.ImageIO;

import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class MusicController {

	@FXML
	Button prevButton;

	@FXML
	Button playPauseButton;

	@FXML
	Button nextButton;

	@FXML
	Button shuffleButton;

	@FXML
	Label songInfo;
	
	@FXML
	Label playlistSongInfo;

	@FXML
	Slider timeline;

	@FXML
	ImageView albumArt;

	@FXML
	ListView<String> playlist;
	
	@FXML
	MenuBar menuBar;
	
	@FXML
	MenuItem fileOpenFiles;

	List<File> songs = new ArrayList<File>();
	ObservableList<String> sList = FXCollections.observableArrayList();
	ArrayList<File> shuffledSongs = new ArrayList<File>();
	FileChooser fc = new FileChooser();
	Mp3File mp3;
	static MediaPlayer mp;

	int currentSong;
	Boolean isPlaying = false;

	public void prev() throws UnsupportedTagException, InvalidDataException, IOException {
		if (mp == null) {
			System.out.println("[NOISEY] No media loaded.");
		} else if (currentSong - 1 < 0) {
			currentSong = songs.size();
			decrementAction();
		} else {
			decrementAction();
		}
	}

	public void playPause() throws IOException, UnsupportedTagException, InvalidDataException {
		if (songs == null) {
			System.out.println("[NOISEY] No media loaded.");
		} else {
			if (mp == null) {
				incrementAction();
			} else {
				if (isPlaying) {
					mp.pause();
					playPauseButton.setGraphic(new ImageView("application/playButton.png"));
					isPlaying = false;
				} else {
					mp.play();
					playPauseButton.setGraphic(new ImageView("application/pauseButton.png"));
					isPlaying = true;
				}
			}
		}
	}

	public void next() throws UnsupportedTagException, InvalidDataException, IOException {
		if (mp == null) {
			System.out.println("[NOISEY] No media loaded.");
		} else if (currentSong + 1 >= songs.size()) {
			currentSong = -1;
			incrementAction();
		} else {
			incrementAction();
		}
	}
	
	public void shuffle() {
		for (int i = 0; i < songs.size(); i ++) {
			shuffledSongs.add(songs.get(i));
		}
		System.out.println(shuffledSongs.toString());
		Collections.shuffle(shuffledSongs);
		System.out.println(shuffledSongs.toString());
		
	}

	public void showAbout() {
		Alert alert = new Alert(AlertType.INFORMATION);
		// alert.initStyle(StageStyle.UTILITY);
		alert.setTitle("Noisey");
		alert.setHeaderText("About Noisey");
		alert.setContentText(
				"Noisey is a music player application written in JavaFX that utilizes the mp3agic library for audio file metadata consumption.\n\n"
						+ "Noisey was created by Charles Arnaudo and Andrew DeChristopher for their WIT CS2 final project.");

		System.out.println("[NOISEY] Showing about dialog.");

		alert.showAndWait();
	}

	public void openFiles() throws UnsupportedTagException, InvalidDataException, IOException {
		fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("MP3s", "*.mp3"));
		fc.setTitle("Select music files...");
		List<File> tempSongs = fc.showOpenMultipleDialog(new Stage());
		if (tempSongs != null) {
			songs = tempSongs;
			sList = FXCollections.observableArrayList();
			for (int i = 0; i < tempSongs.size(); i++) {
				sList.add(tempSongs.get(i).getName().substring(0, tempSongs.get(i).getName().length() - 4));
			}
			playlist.setItems(sList);
			playlistSongInfo.setVisible(false);
			currentSong = -1;
			incrementAction();
		}
	}

	public void openFolder() throws UnsupportedTagException, InvalidDataException, IOException {
		if (songs != null) {
			songs = new ArrayList<File>();
		}
		DirectoryChooser dc = new DirectoryChooser();
		dc.setTitle("Select a music folder...");
		File dir = dc.showDialog(new Stage());
		if (dir != null) {
			File[] dirFiles = dir.listFiles();
			sList = FXCollections.observableArrayList();
			openFolderRecursive(dirFiles);
			playlistSongInfo.setVisible(false);
			currentSong = -1;
			incrementAction();
		}else{
			System.out.println("[NOISEY] Invalid folder.");
		}
	}

	public void openFolderRecursive(File[] dirFiles) {
		if (dirFiles.length > 0) {
			for (int i = 0; i < dirFiles.length; i++) {

				// Prints file extension
				// System.out.println(dirFiles[i].getName().substring(dirFiles[i].getName().length()
				// - 3, dirFiles[i].getName().length()));

				// Adds to list if extension is mp3
				if (!dirFiles[i].isDirectory() && dirFiles[i].getName()
						.substring(dirFiles[i].getName().length() - 3, dirFiles[i].getName().length()).equals("mp3")) {
					System.out.println("[NOISEY] Added :: " + dirFiles[i].getName().toString());
					songs.add(dirFiles[i]);

					sList.add(dirFiles[i].getName().substring(0, dirFiles[i].getName().length() - 4));
					playlist.setItems(sList);

				} else if (dirFiles[i].isDirectory()) {
					File direc = dirFiles[i];
					File[] direcFiles = direc.listFiles();
					openFolderRecursive(direcFiles);
				}
			}
		}
	}

	public void exitApp() {
		Platform.exit();
	}

	public void openLocation() throws IOException {
		if (mp != null) {
			String path = mp.getMedia().getSource().trim().toString();
			String[] loc = path.split("/");
			int use = loc.length - 1;

			String filepath = "";

			for (int i = 1; i < use; i++) {
				filepath += "/" + loc[i];
			}

			filepath = URLDecoder.decode(filepath, "UTF-8");

			System.out.println("[NOISEY] Opening folder: " + filepath + " in system file browser.");

			File file = new File(filepath);
			Desktop desktop = Desktop.getDesktop();
			desktop.open(file);
		} else {
			System.out.println("[NOISEY] No media loaded.");
		}
	}

	public void openLyrics() throws URISyntaxException, SocketException, IOException {
		if (songs != null && mp != null) {
			String query = songInfo.getText();
			query = query.replaceAll(" ", "%20");
			URI lyrics = new URI("https://www.musixmatch.com/search/" + query);
			if (Desktop.isDesktopSupported()) {
				Desktop.getDesktop().browse(lyrics);
			}
		} else {
			System.out.println("[NOISEY] No media loaded.");
		}
	}
	
	public void skipToSong() throws UnsupportedTagException, InvalidDataException, IOException{
		int i = playlist.getSelectionModel().getSelectedIndex();
		currentSong = i-1;
		//System.out.println("Selected listItem: " + currentSong);
		incrementAction();
	}

	public void incrementAction() throws UnsupportedTagException, InvalidDataException, IOException {
		if (mp != null) {
			mp.dispose();
		}
		if (songs != null) {
			if (songs.size() != 0) {
				currentSong++;
				mp3 = new Mp3File(songs.get(currentSong));
				mp = new MediaPlayer(new Media(songs.get(currentSong).toURI().toString()));
				setAlbumArt();
				try {
					songInfo.setText(mp3.getId3v1Tag().getArtist() + " - " + mp3.getId3v1Tag().getTitle());
				} catch (NullPointerException ex) {
					songInfo.setText("Pokemon - Pokemon Theme Song");
					albumArt.setImage(new Image("application/pikachu.png"));
				}
				mp.play();
				playPauseButton.setGraphic(new ImageView("application/pauseButton.png"));
				isPlaying = true;
			}
		} else {
			System.out.println("[NOISEY] No media loaded.");
		}
	}

	public void decrementAction() throws UnsupportedTagException, InvalidDataException, IOException {
		if (mp != null) {
			mp.dispose();
		}
		if (songs != null) {
			if (songs.size() != 0) {
				currentSong--;
				mp3 = new Mp3File(songs.get(currentSong));
				mp = new MediaPlayer(new Media(songs.get(currentSong).toURI().toString()));
				setAlbumArt();
				try {
					songInfo.setText(mp3.getId3v1Tag().getArtist() + " - " + mp3.getId3v1Tag().getTitle());
				} catch (NullPointerException ex) {
					songInfo.setText("No artist or song info found");
					albumArt.setImage(new Image("application/pikachu.png"));
				}
				mp.play();
				playPauseButton.setGraphic(new ImageView("application/pauseButton.png"));
				isPlaying = true;
			}
		} else {
			System.out.println("[NOISEY] No media loaded.");
		}
	}

	public void setAlbumArt() {
		try {
			ID3v2 id3v2tag = mp3.getId3v2Tag();
			byte[] imageData = id3v2tag.getAlbumImage();
			BufferedImage img = ImageIO.read(new ByteArrayInputStream(imageData));
			Image art = SwingFXUtils.toFXImage(img, null);
			albumArt.setImage(art);
		} catch (Exception e) {
			albumArt.setImage(new Image("application/placeHolder.png"));
		}
	}

	public void nextSongThread() {
		mp.setOnEndOfMedia(new Runnable() {
			@Override
			public void run() {
				if (currentSong + 1 >= songs.size()) {
					currentSong = -1;
					try {
						incrementAction();
					} catch (UnsupportedTagException | InvalidDataException | IOException e) {
						e.printStackTrace();
					}
				} else {
					try {
						incrementAction();
					} catch (UnsupportedTagException | InvalidDataException | IOException e) {
						e.printStackTrace();
					}
				}
			}
		});
	}

}
