import java.util.ArrayList;

/**
 * Created by jonathanevans on 10/26/17.
 */
public class ContigExperiment {

    private ContigPlayer player;
    private ContigWallPlayer wallPlayer;
    private ContigRandomPlayer randomPlayer;
    private ContigLowestChancePlayer lowestChancePlayer;
    private ContigHighestChancePlayer highestChancePlayer;
    private ArrayList<ContigPlayer> allPlayers;

    private static final int TRIALS = 100000;

    public ContigExperiment() {
        player = new ContigPlayer();
        wallPlayer = new ContigWallPlayer();
        randomPlayer = new ContigRandomPlayer();
        lowestChancePlayer = new ContigLowestChancePlayer();
        highestChancePlayer = new ContigHighestChancePlayer();

        allPlayers = new ArrayList<ContigPlayer>();
        allPlayers.add(player);
        allPlayers.add(wallPlayer);
        allPlayers.add(randomPlayer);
        allPlayers.add(lowestChancePlayer);
        allPlayers.add(highestChancePlayer);
    }

    public void runExperiment() {
        for (int i = 0; i < allPlayers.size(); i++) {
            for (int j = i + 1; j < allPlayers.size(); j++) {
                runTrial(allPlayers.get(i), allPlayers.get(j));
            }
        }
    }

    public void runTrial(ContigPlayer cp1, ContigPlayer cp2) {
        System.out.println(cp1.name + " vs. " + cp2.name);
        double cp1FinalWinsRatio = 0;
        double cp2FinalWinsRatio = 0;
        for (int i = 0; i < TRIALS; i++) {
            boolean cp1Win = playGame(cp1, cp2);
            if (cp1Win) {
                cp1FinalWinsRatio += 1;
            } else {
                cp2FinalWinsRatio += 1;
            }
            cp1.resetPlayerScore();
            cp2.resetPlayerScore();
        }
        System.out.println(cp1FinalWinsRatio / TRIALS + "      " + cp2FinalWinsRatio / TRIALS + "\n");
    }

    /*
    The great glorious bug of 2017 happened here. On this day, I spent about an hour
    trying to figure out why every single run ended up with both players being equal,
    even random player. Turns out the games were being decided by a random boolean.
    This was due to a curly bracket enclosing the tiebreaker.
     */
    public boolean playGame(ContigPlayer cp1, ContigPlayer cp2) {
        Contig c = new Contig();
        boolean cp1GoesFirst = cp1.dice.nextBoolean();
        for (int i = 0; i < 8; i++) {
            if (cp1GoesFirst) {
                cp1.playTurn();
            }
            cp2.playTurn();
            if (!cp1GoesFirst) {
                cp1.playTurn();
            }
        }
            //Ties are broken by whoever went first getting the win
            if (cp1.getPlayerScore() == cp2.getPlayerScore()) {
                return cp1GoesFirst;
            } else {
                return cp1.getPlayerScore() > cp2.getPlayerScore();
            }
    }

    public static void main(String[] args) {
        ContigExperiment ce = new ContigExperiment();
        ce.runExperiment();
    }
}
