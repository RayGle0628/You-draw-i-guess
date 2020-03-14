package client;

import javafx.scene.media.AudioClip;

public class SoundFX {
    AudioClip yourTurn;
    AudioClip correctGuess;
    AudioClip correctGuess2;
    AudioClip startRound;

    public SoundFX() {


        correctGuess = new AudioClip(this.getClass().getResource("/Resources/SoundEffects/b.wav").toString());
        correctGuess2 = new AudioClip(this.getClass().getResource("/Resources/SoundEffects/a.wav").toString());
        yourTurn = new AudioClip(this.getClass().getResource("/Resources/SoundEffects/c.wav").toString());
        startRound = new AudioClip(this.getClass().getResource("/Resources/SoundEffects/d.wav").toString());
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
