import lombok.Data;

@Data
public class Match {
    private Team homeTeam;
    private Team awayTeam;
    private byte homeScore;
    private byte awayScore;
    // Constructor
    public Match(Team homeTeam, Team awayTeam) {
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.homeScore = 0;
        this.awayScore = 0;
    }

    // Method to simulate a match and determine the winner
    public void simulateMatch() {
        homeScore = (byte)(Math.random() * 11); // 0-10 range for home team
        awayScore = (byte)(Math.random() * 10); // 0-9 range for away team
        //for these ranges, home team have an advantage over away team %11.1 . It is an acceptable and possible advantage.
        if(homeScore > awayScore) {
            System.out.println(homeTeam.getName() + " (home) won the match " + awayTeam.getName() + " (away) lost the match!");
        } else if(awayScore > homeScore) {
            System.out.println(homeTeam.getName() + " (home) lost the match " +awayTeam.getName() + " (away) won the match!" );
        } else {
            System.out.println(homeTeam.getName() + " (home) -" +awayTeam.getName()+" (away) The match ended in a draw.");
        }
    }
}