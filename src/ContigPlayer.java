import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Random;

/**
 * Created by jonathanevans on 10/13/17.
 */
public class ContigPlayer {

    protected int playerScore;
    protected Random dice;
    protected String name;


    public ContigPlayer() {
        int playerScore = 0;
        dice = new Random();
        name = "Contig Player";
    }

    /**
     * Each round, the computer will play a turn using this method which
     * first rolls the 3 die, then checks if it is the first turn that has a
     * required move, and then finally determines which moves is the best to play
     */
    public void playTurn() {
        double roll1 = (double)(dice.nextInt(6) + 1);
        double roll2 = (double)(dice.nextInt(6) + 1);
        double roll3 = (double)(dice.nextInt(6) + 1);

        //System.out.println("Rolls: " + roll1 + " " + roll2 + " " + roll3);

        if (Contig.getIsFirstTurn()) {
            playFirstTurn(roll1, roll2, roll3);
        } else {

            ArrayList<IndexPair> bestMovesList = getBestMoves();
            HashSet<Integer> validMovesList = cleanUpListAndConvertToInt(getValidMoves(roll1, roll2, roll3));

            IndexPair play = findBestValidMove(bestMovesList, validMovesList);
            playerScore += play.curScorePotential;
            if (play.row != -1) {
                Contig.gameBoard[play.row][play.column] = -1;
            }
        }
    }

    /*
    The first turn simply plays the addition of the three rolls
        Since this player moves first, they get two points as the other player
        gets the advantage of playing the last turn with the most chips on the board
     */
    private void playFirstTurn(double roll1, double roll2, double roll3) {
        double rollTotal = (roll1 + roll2 + roll3) * 3;

        //51 is the only number such that 3 dice rolls * 3 cannot be found on the board
        //So this simply sets it to 50. Otherwise, every number is on the board.
        if (rollTotal == 51) {
            rollTotal = 50;
        }
        IndexPair firstPair = findIndexByNumber((int)rollTotal);
        Contig.gameBoard[firstPair.row][firstPair.column] = -1;
        playerScore+=2;
        Contig.setIsFirstTurn(false);
    }

    /*
    This method gets rid of all the numbers that are not on the board and all the duplicates.
    It convers the list to a hashset in order to optimize the contains function that is used often
     */
    protected HashSet<Integer> cleanUpListAndConvertToInt(ArrayList<Double> list) {
        HashSet<Integer> cleanList = new HashSet<>();

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) <= 216 && list.get(i) >= 1 && !cleanList.contains(list.get(i))) {
                if (Math.floor(list.get(i)) == list.get(i)) {
                    //For some reason, this worked while just saying list.get(i) did not
                    cleanList.add((int) Math.floor(list.get(i)));
                }
            }
        }

        return cleanList;
    }

    /*
    This is a crucial method that determines, based on the three dice rolls, what numbers can
    be made following the rules of the game. It creates three lists representing all of the possible
    numbers that can be generated from the rolls , the first computing roll1 and roll2,
    then roll1 and roll3, and roll2 and roll3. From there, it takes each computed number and calls the
    compute function on the missing component (so roll1 after we computed the results of roll2 and roll3).
     */
    public ArrayList<Double> getValidMoves(double roll1, double roll2, double roll3) {
        ArrayList<Double> validMovesList = new ArrayList<Double>();
        ArrayList<Double> validMovesList1 = new ArrayList<Double>();
        ArrayList<Double> validMovesList2 = new ArrayList<Double>();
        ArrayList<Double> validMovesList3 = new ArrayList<Double>();

        validMovesList1.addAll(compute(roll1, roll2));
        validMovesList2.addAll(compute(roll1, roll3));
        validMovesList3.addAll(compute(roll2, roll3));

        for (int i = 0; i < validMovesList1.size(); i++) {
            validMovesList.addAll(compute(validMovesList1.get(i), roll3));
        }
        for (int i = 0; i < validMovesList2.size(); i++) {
            validMovesList.addAll(compute(validMovesList2.get(i), roll2));
        }
        for (int i = 0; i < validMovesList3.size(); i++) {
            validMovesList.addAll(compute(validMovesList3.get(i), roll1));
        }

        return validMovesList;
    }

    /*
    This crucial method returns an ArrayList containing all the operations that
    can be done to two dice rolls (or 3 by calling it twice). Added some components
    to avoid exceedingly large computations like checking factorial and pow for original numbers.
     */
    public ArrayList<Double> compute(double num1, double num2) {
        ArrayList<Double> values = new ArrayList<Double>();
        if (num1 > 2 && num1 <= 6) {
            values.addAll(compute(factorial(num1), num2));
        }
        if (num2 > 2 && num2 <= 6) {
            values.addAll(compute(factorial(num2), num1));
        }
        values.add(num1 + num2);
        values.add(num1 - num2);
        values.add(num1 * num2);
        values.add(num2 - num1);
        values.add(num1 / num2);
        values.add(num2 / num1);

        if (num1 <= 6 || num2 <= 3 ) {
            values.add(Math.pow(num1, num2));
        }

        if (num2 <= 6 || num1 <= 3 ) {
            values.add(Math.pow(num2, num1));
        }

        return values;
    }

    /*
This method starts at the beginning of the best moves list and checks to see if the first best
move is a valid move. If it is, we return that pair, otherwise, we check the next best element.
We do this until we have found the best valid move.
 */
    public IndexPair findBestValidMove(ArrayList<IndexPair> bestMoves, HashSet<Integer> validMoves) {
        for (int i = 0; i < bestMoves.size(); i++) {
            int numAtSpot = Contig.gameBoard[bestMoves.get(i).row][bestMoves.get(i).column];
            for (int j = 0; j < validMoves.size(); j++) {
                if (validMoves.contains(numAtSpot)) {
                    return bestMoves.get(i);
                }
            }
        }
        return new IndexPair(-1, -1, 0);
    }

    /**
     * This adds all the "score potentials" of placing a chip on any of the
     * particular positions to an ArrayList that is then sorted by
     * best moves (higher score) to worst (lower score)
     * @return ArrayList of potential scores on each position
     */
    public ArrayList<IndexPair> getBestMoves() {
        ArrayList<IndexPair> bestMovesList = new ArrayList<IndexPair>();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                IndexPair curIndP = new IndexPair(i, j, Contig.getScorePotential(i, j));
                if (Contig.gameBoard[i][j] != -1) {
                    bestMovesList.add(curIndP);
                }
            }
        }

        bestMovesList.sort(new Comparator<IndexPair>() {
            @Override
            public int compare(IndexPair o1, IndexPair o2) {
                if (o1.curScorePotential > o2.curScorePotential) {
                    return -1;
                } else if (o1.curScorePotential == o2.curScorePotential) {
                    return  0;
                } else {
                    return 1;
                }
            }
        });
        return bestMovesList;
    }

    /**
     * This method finds the position of a number using that number
     * @param roll
     * @return
     */
    protected IndexPair findIndexByNumber(int roll) {
        IndexPair indP = new IndexPair(-1, -1, 0);
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (Contig.gameBoard[i][j] == roll) {
                    indP.row = i;
                    indP.column = j;
                    indP.curScorePotential = Contig.getScorePotential(i, j);
                    return indP;
                }
            }
        }
        return indP;
    }

    private double factorial(double num) {
        if (num <= 1) {
            return 1;
        } else {
            return num * factorial(num - 1);
        }
    }

    public int getPlayerScore() {
        return playerScore;
    }

    public void resetPlayerScore() {
        playerScore = 0;
    }

    /**
     * This class plays a few roles and is used to keep track of Index Pairs
     * It has constructors for helping with creating the best moves ArrayList,
     * creating the valid moves ArrayList, and a normal pair
     */
    public class IndexPair {
        protected int row;
        protected int column;
        protected int curScorePotential;

        public IndexPair(int row, int column, int curScorePotential) {
            this.row = row;
            this.column = column;
            this.curScorePotential = curScorePotential;
        }

        public IndexPair(int row, int column) {
            this.row = row;
            this.column = column;
        }
    }

}
