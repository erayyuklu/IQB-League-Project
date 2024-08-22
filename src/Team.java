import lombok.Data;

@Data
public class Team {
    private String name;
    private short foundationYear;
    private String[] colors;
    private int overallScore; //overallScore can be very high while we have a lot of matches. It is not a good idea to keep it as a byte or short type.
    // Constructor
    public Team(String name, short foundationYear, String[] colors) {
        this.name = name;
        this.foundationYear = foundationYear;
        this.colors = colors;
        this.overallScore = 0;
    }
}