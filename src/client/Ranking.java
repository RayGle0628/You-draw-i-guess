package client;

/**
 * Ranking is an object that contains ranking information. It is used to populate the leader board in the home scene.
 */
public class Ranking {

    private String username;
    private int rank;
    private int score;
    private int wins;

    /**
     * Constructor for the Ranking class populates field variables from the split input string.
     *
     * @param ranking the split components of ranking data from the server.
     */
    public Ranking(String[] ranking) {
        this.rank = Integer.parseInt(ranking[0]);
        this.username = (ranking[1]);
        this.score = Integer.parseInt(ranking[2]);
        this.wins = Integer.parseInt(ranking[3]);
    }

    /**
     * Getter for the username field variable.
     *
     * @return username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Getter for the rank field variable.
     *
     * @return rank
     */
    public int getRank() {
        return rank;
    }

    /**
     * Getter for the score field variable.
     *
     * @return score
     */
    public int getScore() {
        return score;
    }

    /**
     * Getter for the wins field variable.
     *
     * @return wins
     */
    public int getWins() {
        return wins;
    }
}
