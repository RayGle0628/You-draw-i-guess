package client;

import javafx.scene.media.AudioClip;

/**
 * SoundFX is the class that deals with playing sounds to the client to indicate events happening in a game.
 */
public class SoundFX {

    private AudioClip yourTurn;
    private AudioClip correctGuess;
    private AudioClip correctGuess2;
    private AudioClip startRound;

    /**
     * Constructor for the SoundFX class. Points AudioCLip objects to the correct resource path.
     */
    public SoundFX() {
        correctGuess = new AudioClip(this.getClass().getResource("/Resources/SoundEffects/b.wav").toString());
        correctGuess2 = new AudioClip(this.getClass().getResource("/Resources/SoundEffects/a.wav").toString());
        yourTurn = new AudioClip(this.getClass().getResource("/Resources/SoundEffects/c.wav").toString());
        startRound = new AudioClip(this.getClass().getResource("/Resources/SoundEffects/d.wav").toString());
    }

    /**
     * Played when a user other than this one guesses a word correctly.
     */
    public void playGuess() {
        correctGuess.play();
    }

    /**
     * Played when the user guesses a word correctly.
     */
    public void playGuess2() {
        correctGuess2.play();
    }

    /**
     * Played to a single user when it is their turn to draw.
     */
    public void playYouDraw() {
        yourTurn.play();
    }

    /**
     * Played at the start of a new round.
     */
    public void playStartRound() {
        startRound.play();
    }
}
