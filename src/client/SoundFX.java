package client;

import javafx.scene.media.AudioClip;

public class SoundFX {
    AudioClip yourTurn;
    AudioClip correctGuess;
    AudioClip correctGuess2;
    AudioClip startRound;
    AudioClip endRound;
    AudioClip startGame;
    AudioClip endGame;

    public SoundFX() {
        correctGuess = new AudioClip("file:b.wav");
        correctGuess2 = new AudioClip("file:a.wav");
        yourTurn = new AudioClip("file:c.wav");
        startRound = new AudioClip("file:d.wav");
    }


    public void playGuess(){
      correctGuess.play();
    }
  public void playGuess2(){
    correctGuess2.play();
  }
  public void playYouDraw(){
    yourTurn.play();
  }
  public void playStartRound(){
    startRound.play();
  }
}
