package client;


public class Ranking {
    private String username;
    private int rank;
    private int score;
    private int wins;

    public Ranking(String[] ranking) {
        this.rank = Integer.parseInt(ranking[0]);
        this.username = (ranking[1]);
        this.score = Integer.parseInt(ranking[2]);
        this.wins = Integer.parseInt(ranking[3]);
    }

    public String getUsername() {
        return username;
    }

    public int getRank() {
        return rank;
    }

    public int getScore() {
        return score;
    }

    public int getWins() {
        return wins;
    }
}
