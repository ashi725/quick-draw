package nz.ac.auckland.se206.util;

import static nz.ac.auckland.se206.ml.DoodlePrediction.printPredictions;

import ai.djl.modality.Classifications;
import ai.djl.translate.TranslateException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.controllers.CanvasController;
import nz.ac.auckland.se206.controllers.ReadyController;
import nz.ac.auckland.se206.controllers.ResultsController;
import nz.ac.auckland.se206.speech.TextToSpeech;

/** Handles the clock and time limit functionality. */
public class NormalOrHiddenModeTask extends TimerTask {
  private final long startTime;
  private long timeElapsed;
  private final Timer timer;
  private final CanvasController canvasController;
  private final ResultsController resultsController;
  private final ReadyController readyController;
  private final JsonParser jsonParser;
  private int wordPos;
  private int prevWordPos;
  private TextToSpeech tts;
  /** Constructs the NormalModeTask object. */
  public NormalOrHiddenModeTask() {
    canvasController = (CanvasController) App.getController("canvas");
    startTime = System.currentTimeMillis(); // Get the current time in milliseconds.
    resultsController = (ResultsController) App.getController("results");
    readyController = (ReadyController) App.getController("ready");
    tts = new TextToSpeech();
    jsonParser = App.getJsonParser();
    wordPos = 100;
    timer = new Timer();
  }

  /** Starts the clock and task with 1-second period. */
  public void scheduleTask() {
    timer.schedule(this, 0, 1000);
  }

  /**
   * Handles the time limit functionality. If the time limit is reached, the program will terminate.
   */
  public void run() {
    int timeLimit =
        Integer.parseInt(
            App.getJsonParser().getProperty(App.getCurrentUser(), "timeAllowed").toString());
    timeElapsed = (System.currentTimeMillis() - startTime) / 1000; // time elapsed in seconds
    if (timeElapsed == timeLimit) { // If time runs out, move to results scene.
      App.getSoundManager().playGameLoss();
      timer.cancel();
      resultsController.setResultLabel(
          "You ran out of time to draw: \"" + readyController.getPrompt() + "\"");
      Platform.runLater(resultsController::setSketchImage);
      jsonParser.incrementLosses(App.getCurrentUser());
      jsonParser.resetWinStreak(App.getCurrentUser());
      canvasController.results();

    } else if (timeElapsed
        < timeLimit) { // If time is still remaining, update the time label and prediction list.
      Platform.runLater(
          () -> {
            canvasController.setTimerLabel(
                String.valueOf(((long) (timeLimit - 1) - timeElapsed) % timeLimit));
            try {
              // Get the predictions as a string list to parse
              List<String> stringPredictions = new ArrayList<>();
              List<Classifications.Classification> predictions =
                  canvasController
                      .getModel()
                      .getPredictions(canvasController.getCurrentSnapshot(), 100);

              for (Classifications.Classification prediction : predictions) {
                stringPredictions.add(prediction.getClassName());
              }

              List<String> topTen = stringPredictions.subList(0, 10); // Get the top 10 predictions
              prevWordPos = wordPos; // Store the new and old word positions
              wordPos = stringPredictions.indexOf(readyController.getPrompt());

              // Run every 3 seconds if word is not in top 10
              if (!topTen.contains(readyController.getPrompt())
                  && prevWordPos != 100
                  && timeElapsed % 3 == 0) {
                if (wordPos < prevWordPos) { // If the word has moved up in the list
                  new Thread(() -> tts.speak("You are getting closer")).start();
                } else if (wordPos > prevWordPos) { // If the word has moved down in the list
                  new Thread(() -> tts.speak("You are getting further away")).start();
                }
              }
              canvasController.setPredictionList(printPredictions(predictions.subList(0, 10)));

              // Check if the prompt is in the top 3 predictions.
              if (canvasController.isCorrect()) {
                App.getSoundManager().playGameWin();
                timer.cancel();
                // If so, move to results scene.
                resultsController.setResultLabel(
                    "Good job! You drew: \""
                        + readyController.getPrompt()
                        + "\" in "
                        + timeElapsed
                        + " seconds!");
                resultsController.setSketchImage();
                jsonParser.incrementWins(App.getCurrentUser());
                final long time = timeElapsed;
                jsonParser.setFastestTime(App.getCurrentUser(), Long.toString(time));
                jsonParser.incrementWinStreak(App.getCurrentUser());
                canvasController.results();
              }
            } catch (TranslateException e) {
              throw new RuntimeException(e);
            }
          });
    }
  }
}
