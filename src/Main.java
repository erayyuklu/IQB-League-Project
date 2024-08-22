import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");
        Team Galatasaray =new Team("Galatasaray",(short)1905, new String[]{"red", "blue"});
        Team Fenerbahce =new Team("Fenerbahce",(short)1907, new String[]{"yellow", "blue"});
        Team Besiktas =new Team("Besiktas",(short)1903, new String[]{"black", "white"});
        System.out.println(Galatasaray.toString());

        League firstLeague = new League(List.of(Galatasaray, Fenerbahce, Besiktas));
        for(int i =0; i<firstLeague.getFixtures().size();i++){
            firstLeague.getFixtures().get(i).simulateMatch();
        }

    }
}

