import lombok.Data;

@Data
public class MatchDTO {
    private String homeTeamName;
    private String awayTeamName;
    private int homeScore;
    private int awayScore;

    // Constructor

    public MatchDTO() {
    }


    public MatchDTO(String homeTeamName, String awayTeamName, int homeScore, int awayScore) {
        this.homeTeamName = homeTeamName;
        this.awayTeamName = awayTeamName;
        this.homeScore = homeScore;
        this.awayScore = awayScore;
    }
}