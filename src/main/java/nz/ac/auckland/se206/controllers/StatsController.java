package nz.ac.auckland.se206.controllers;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.util.JsonParser;

public class StatsController {

  @FXML private Label numWinsLabel;
  @FXML private Label numLossesLabel;
  @FXML private Label fastestTimeLabel;
  @FXML private Text txtBadgeDescription;
  @FXML private ImageView imgBadgeOne;
  @FXML private ImageView imgBadgeTwo;
  @FXML private ImageView imgBadgeThree;
  @FXML private ImageView imgBadgeFour;
  @FXML private ImageView imgBadgeFive;
  @FXML private ImageView imgBadgeSix;
  @FXML private ImageView imgBadgeSeven;
  @FXML private ImageView imgBadgeEight;

  /** Initializes the stats controller class. */
  @FXML
  private void initialize() {
    System.out.println("***************** Initialising Stats Controller *****************");
  }

  @FXML
  private void onSwitchToMenu(ActionEvent event) {
    App.getSoundManager().playButtonClick();
    Button button = (Button) event.getSource(); // Get the scene of the button and switch its root.
    Scene buttonScene = button.getScene();
    buttonScene.setRoot(SceneManager.getUiRoot(SceneManager.AppUi.MENU));
  }

  /** Updates the menu scene with the user's stats */
  public void updateStats() {
    JsonParser jsonParser = App.getJsonParser();

    // Set welcome and stats labels
    numWinsLabel.setText(jsonParser.getProperty(App.getCurrentUser(), "gamesWon").toString());
    numLossesLabel.setText(jsonParser.getProperty(App.getCurrentUser(), "gamesLost").toString());

    // Set fastest time label (if games have been played)
    if (jsonParser.getProperty(App.getCurrentUser(), "fastestTime") == "0") {
      fastestTimeLabel.setText("No wins yet");
    } else {
      fastestTimeLabel.setText(
          jsonParser.getProperty(App.getCurrentUser(), "fastestTime").toString() + " seconds");
    }
  }

  @FXML
  private void onBadgeOne() {
    txtBadgeDescription.setText("Win more games that you have lost");
  }

  @FXML
  private void onBadgeTwo() {
    txtBadgeDescription.setText("Encounter 20 easy words");
  }

  @FXML
  private void onBadgeThree() {
    txtBadgeDescription.setText("Encounter 20 medium words");
  }

  @FXML
  private void onBadgeFour() {
    txtBadgeDescription.setText("Encounter 20 hard words");
  }

  @FXML
  private void onBadgeFive() {
    txtBadgeDescription.setText("Win in under 5 seconds");
  }

  @FXML
  private void onBadgeSix() {
    txtBadgeDescription.setText("Win 3 times in a row");
  }

  @FXML
  private void onBadgeSeven() {
    txtBadgeDescription.setText("Win 10 times in a row");
  }

  @FXML
  private void onBadgeEight() {
    txtBadgeDescription.setText("Win in under 30 seconds");
  }

  /**
   * Checks which badges the user has earned and awards them accordingly
   *
   * @throws FileNotFoundException if the image file is not found
   */
  public void checkBadges() throws FileNotFoundException {

    FileInputStream badgeInput =
        new FileInputStream(
            System.getProperty("user.dir") + "/src/main/resources/images/medal.png");
    Image badge = new Image(badgeInput);

    FileInputStream hiddenInput =
        new FileInputStream(
            System.getProperty("user.dir") + "/src/main/resources/images/doubts-button.png");
    Image hidden = new Image(hiddenInput);

    // checking whether a badge has been won or not
    if (isBadgeOne()) {
      imgBadgeOne.setImage(badge);
    } else {
      imgBadgeOne.setImage(hidden);
    }
    if (isBadgeTwo()) {
      imgBadgeTwo.setImage(badge);
    } else {
      imgBadgeTwo.setImage(hidden);
    }
    if (isBadgeThree()) {
      imgBadgeThree.setImage(badge);
    } else {
      imgBadgeThree.setImage(hidden);
    }
    if (isBadgeThree()) {
      imgBadgeThree.setImage(badge);
    } else {
      imgBadgeThree.setImage(hidden);
    }
    if (isBadgeFour()) {
      imgBadgeFour.setImage(badge);
    } else {
      imgBadgeFour.setImage(hidden);
    }
    if (isBadgeFive()) {
      imgBadgeFive.setImage(badge);
    } else {
      imgBadgeFive.setImage(hidden);
    }
    if (isBadgeSix()) {
      imgBadgeSix.setImage(badge);
    } else {
      imgBadgeSix.setImage(hidden);
    }
    if (isBadgeSeven()) {
      imgBadgeSeven.setImage(badge);
    } else {
      imgBadgeSeven.setImage(hidden);
    }
    if (isBadgeEight()) {
      imgBadgeEight.setImage(badge);
    } else {
      imgBadgeEight.setImage(hidden);
    }
  }

  /**
   * checks if badgeOne has been earned, if so return true else return false
   *
   * @return true if badgeOne has been earned, else false
   */
  public boolean isBadgeOne() {

    JsonParser jsonParser = App.getJsonParser();
    String winString = jsonParser.getProperty(App.getCurrentUser(), "gamesWon").toString();
    String lossString = jsonParser.getProperty(App.getCurrentUser(), "gamesLost").toString();

    int wins = Integer.parseInt(winString);
    int losses = Integer.parseInt(lossString);

    return wins > losses;
  }

  /**
   * checks if badgeTwo has been earned, if so return true else return false
   *
   * @return true if badgeTwo has been earned, else false
   */
  public boolean isBadgeTwo() {

    JsonParser jsonParser = App.getJsonParser();
    List<String> easyWords;
    easyWords = (List<String>) jsonParser.getProperty(App.getCurrentUser(), "easyWordsEncountered");

    return easyWords.size() > 19;
  }

  /**
   * checks if badgeThree has been earned, if so return true else return false
   *
   * @return true if badgeThree has been earned, else false
   */
  public boolean isBadgeThree() {

    JsonParser jsonParser = App.getJsonParser();
    List<String> mediumWords;
    mediumWords =
        (List<String>) jsonParser.getProperty(App.getCurrentUser(), "mediumWordsEncountered");

    return mediumWords.size() > 19;
  }

  /**
   * checks if badgeFour has been earned, if so return true else return false
   *
   * @return true if badgeFour has been earned, else false
   */
  public boolean isBadgeFour() {

    JsonParser jsonParser = App.getJsonParser();
    List<String> hardWords;
    hardWords = (List<String>) jsonParser.getProperty(App.getCurrentUser(), "hardWordsEncountered");

    return hardWords.size() > 19;
  }

  /**
   * checks if badgeFive has been earned, if so return true else return false
   *
   * @return true if badgeFive has been earned, else false
   */
  public boolean isBadgeFive() {

    JsonParser jsonParser = App.getJsonParser();
    String time = jsonParser.getProperty(App.getCurrentUser(), "fastestTime").toString();
    int fastest = Integer.parseInt(time);

    return fastest <= 5 && fastest != 0;
  }

  /**
   * checks if badgeSix has been earned, if so return true else return false
   *
   * @return true if badgeSix has been earned, else false
   */
  public boolean isBadgeSix() {
    JsonParser jsonParser = App.getJsonParser();
    String winStreak = jsonParser.getProperty(App.getCurrentUser(), "winStreak").toString();
    return Integer.parseInt(winStreak) > 2;
  }

  /**
   * checks if badgeSeven has been earned, if so return true else return false
   *
   * @return true if badgeSeven has been earned, else false
   */
  public boolean isBadgeSeven() {
    JsonParser jsonParser = App.getJsonParser();
    String winStreak = jsonParser.getProperty(App.getCurrentUser(), "winStreak").toString();
    return Integer.parseInt(winStreak) > 9;
  }

  /**
   * checks if badgeEight has been earned, if so return true else return false
   *
   * @return true if badgeEight has been earned, else false
   */
  public boolean isBadgeEight() {
    JsonParser jsonParser = App.getJsonParser();
    String time = jsonParser.getProperty(App.getCurrentUser(), "fastestTime").toString();
    int fastest = Integer.parseInt(time);

    return fastest < 30 && fastest != 0;
  }
}
