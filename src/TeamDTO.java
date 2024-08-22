import lombok.Data;

@Data
public class TeamDTO {
    private String name;
    private short foundationYear;
    private String[] colors;
    private int overallScore;

    // Constructor
    public TeamDTO(String name, short foundationYear, String[] colors) {
        this.name = name;
        this.foundationYear = foundationYear;
        this.colors = colors;
        this.overallScore = 0;
    }
}