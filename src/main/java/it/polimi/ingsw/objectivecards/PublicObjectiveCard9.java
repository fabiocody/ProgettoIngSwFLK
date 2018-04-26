package it.polimi.ingsw.objectivecards;


public class PublicObjectiveCard9 extends ObjectiveCard {

    public PublicObjectiveCard9() {
        super("Diagonali Colorate",
                "Numero di dadi dello stesso colore diagonalmente adiacenti");
    }

    public int calcScore() {
        // TODO
        /*
         *  Set<Die> diagonals = new HashSet<>();
         *  for (int i = 0; i < 4; i++) {
         *      for (int j = 0; j < 5; j++) {
         *          if (i > 0 && j > 0 && grid[i-1][j-1].getColor() == grid[i][j].getColor()) {
         *              diagonals.add(grid[i][j]);
         *              diagonals.add(grid[i-1][j-1]);
         *          }
         *          if (i > 0 && j < 4 && grid[i-1][j+1].getColor() == grid[i][j].getColor()) {
         *              diagonals.add(grid[i][j]);
         *              diagonals.add(grid[i-1][j+1]);
         *          }
         *          if (i < 3 && j > 0 && grid[i+1][j-1].getColor() == grid[i][j].getColor()) {
         *              diagonals.add(grid[i][j]);
         *              diagonals.add(grid[i+1][j-1]);
         *          }
         *          if (i < 3 && j < 4 && grid[i+1][j+1].getColor() == grid[i][j].getColor()) {
         *              diagonals.add(grid[i][j]);
         *              diagonals.add(grid[i+1][j+1]);
         *          }
         *      }
         *  }
         *  return diagonals.size();
         */
        return this.getVictoryPoints();
    }

}
