# Contig
Built multiple AI that play the math game Contig and then experimented with them to find an optimal gameplay strategy. The rules of the game can be found below.

<p align="center">
  <img width="430" height="640" src="https://sites.google.com/a/pvlearners.net/sweigand-games/_/rsrc/1375251341279/contig/Contig.png?height=640&width=430">
</p>

AI Players include

1. Contig Player - Plays whichever move has the highest score
2. Wall Player - Same as Contig player but will priortize moves along the wall of the board to reduce the opponents chance to score
3. Random Player - Given all valid moves, selects one at random
4. Lowest Chance Player - Same as Contig Player but will prioritize numbers on the board that have the lowest chance of being valid in the future (for example, if the two valid, highest scoring moves are 216 and 12, Lowest Chance Player will choose 216 since it is less likely that 216 will be valid given that 12 can be made from many different combinations of numbers)
5. Highest Chance Player - The same as Lowest Chance Player except that it selects whichever move has the highest probability of being valid in the future. 


In order to create the Lowest Chance and Highest Chance Player, for each number on the board, I calculated how likely it is that that number could be played on a random roll. Rolling three die, there are 216 possible combinations so I simply computed every valid number for every roll. For example, the roll {1, 1, 1} can compute to 1 (1 * 1 * 1), 2 (1 + 1 * 1), and 3 (1 + 1 + 1). Then I did this with {1, 1, 2}, {1, 1, 3}, etc. 

Once I had all the possible valid numbers for any given roll, the percentage chance of that number being playable given a random roll is (# of rolls where this number is valid / # of possible rolls). If 7 can be made 180 ways, then the number 7 on the board has a (180/216) -> 83.3% chance of being valid given a random roll. 
