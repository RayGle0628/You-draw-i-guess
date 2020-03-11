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
        correctGuess = new AudioClip("file:SoundEffects/b.wav");
        correctGuess2 = new AudioClip("file:SoundEffects/a.wav");
        yourTurn = new AudioClip("file:SoundEffects/c.wav");
        startRound = new AudioClip("file:SoundEffects/d.wav");
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
