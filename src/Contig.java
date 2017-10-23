import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;
import java.util.Scanner;

/**
 * Created by jonathanevans on 8/31/17.
 *
 * This is for the algorithms project and this program is an AI that can win the game "Contig"
 */
public class Contig {

    protected static int[][] gameBoard;
    private Scanner scr;

    //Reads the gameboard numbers from a text file. Then creates the gameboard, printing out the pre-game setup
    public Contig() {
        try {
            scr = new Scanner(new FileReader("ContigNumbers.txt"));
        } catch (FileNotFoundException fnfe){
            System.out.println("SHit don't work bro.. It's not up to me, relax bro, I'm just tellin it how it is.");
        }

        gameBoard = createGameBoard();
    }

    /**
     * Creates the game board by parsing the text file.
     * @return the 2d int array gameboard
     */
    public int[][] createGameBoard() {

        int[][] setupBoard = new int[8][8];
        int counter = 1;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                setupBoard[i][j] = scr.nextInt();
            }
        }
        return setupBoard;
    }

    /**
     * This simply prints out the board and replaces all the
     * "-1"'s with an X for clarity
     */
    public void printBoard() {
        for (int i = 0; i < 8; i++) {
            System.out.println();
            for (int j = 0; j < 8; j++) {
                String s = "";
                if (gameBoard[i][j] / 10 == 0 || gameBoard[i][j] == - 1) {
                    s = "  ";
                } else if (gameBoard[i][j] / 100 == 0) {
                    s = " ";
                }
                if (gameBoard[i][j] > 0) {
                    System.out.print(gameBoard[i][j] + " " + s);
//                    System.out.print(getScorePotential(i, j) + "   " );
                } else {
                    System.out.print("X " + s);
                }
            }
        }
        System.out.println();
    }

    /**
     * This method checks all eight directions if possible from any particular spot
     * to determine how many adjacent chips there are, which tallys the score.
     * @param row the row of the particular position
     * @param col the column of the particular position
     * @return int indicating the scoring potential
     */
    public static int getScorePotential(int row, int col) {
        int scoreCounter = 0;
        scoreCounter += checkTop(row, col);
        scoreCounter += checkBottom(row, col);
        if (col - 1 >= 0 && gameBoard[row][col-1] < 0) {
            scoreCounter++;
        }
        if (col + 1 < 8 && gameBoard[row][col+1] < 0) {
            scoreCounter++;
        }
        return scoreCounter;
    }

    //This simply checks the top row for adjacent chips
    private static int checkTop(int row, int col) {
        int topCounter = 0;
        if (row - 1 >= 0) {
            if (col - 1 >= 0 && gameBoard[row-1][col-1] < 0) {
                topCounter++;
            }
            if (gameBoard[row-1][col] < 0) {
                topCounter++;
            }
            if (col + 1 < 8 && gameBoard[row-1][col+1] < 0) {
                topCounter++;
            }
        }
        return topCounter;
    }

    //This simply checks the bottom row for adjacent chips
    private static int checkBottom(int row, int col) {
        int bottomCounter = 0;
        if (row + 1 < 8) {
            if (col - 1 >= 0 && gameBoard[row+1][col-1] < 0) {
                bottomCounter++;
            }
            if (gameBoard[row+1][col] < 0) {
                bottomCounter++;
            }
            if (col + 1 < 8 && gameBoard[row+1][col+1] < 0) {
                bottomCounter++;
            }
        }
        return bottomCounter;
    }

    public static int getScorePotentialDebug(int row, int col) {
        int scoreCounter = 0;
        if (col - 1 >= 0 && gameBoard[row][col-1] < 0) {
            scoreCounter++;
            System.out.println("Mid Left");
        }
        if (col + 1 < 8 && gameBoard[row][col+1] < 0) {
            scoreCounter++;
            System.out.println("Mid Right");
        }

        int topCounter = 0;
        if (row - 1 >= 0) {
            if (col - 1 >= 0 && gameBoard[row-1][col-1] < 0) {
                topCounter++;
                System.out.println("Top Left");
            }
            if (row - 1 >= 0 && gameBoard[row-1][col] < 0) {
                topCounter++;
                System.out.println("Top Mid");
            }
            if (col + 1 < 8 && gameBoard[row-1][col+1] < 0) {
                topCounter++;
                System.out.println("Top Right");
            }
        }

        int bottomCounter = 0;
        if (row + 1 < 8) {
            if (col - 1 >= 0 && gameBoard[row+1][col-1] < 0) {
                bottomCounter++;
                System.out.println("Bottom Left");
            }
            if (row + 1 < 8 && gameBoard[row+1][col] < 0) {
                bottomCounter++;
                System.out.println("Bottom Mid");
            }
            if (col + 1 < 8 && gameBoard[row+1][col+1] < 0) {
                bottomCounter++;
                System.out.println("Bottom Right");
            }
        }


        return scoreCounter + topCounter + bottomCounter;
    }

    public static void main (String[] args) {
        Contig c = new Contig();
        ContigPlayer cp = new ContigWallPlayer();
        for (int i = 0; i < 14; i++) {
            cp.playTurn();
            c.printBoard();
        }
    }
}
