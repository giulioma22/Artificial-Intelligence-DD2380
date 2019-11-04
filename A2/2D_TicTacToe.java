import java.util.Random;
import java.util.Vector;
import java.util.stream.IntStream;

public class Player {
    /**
     * Performs a move
     *
     * @param gameState the current state of the board
     * @param deadline  time before which we must have returned
     * @return the next state the board is in after our move
     */
    public GameState play(final GameState gameState, final Deadline deadline) {
        Vector<GameState> nextStates = new Vector<>();
        gameState.findPossibleMoves(nextStates);

        int depth = 9;
        double minMax = 0;
        double temp = 0;
        GameState best_possible_state = null;

        //PLAYER X is 2, while PLAYER O is 1

        for (int i = 0; i < nextStates.size(); i++) {
            if (i == 0) best_possible_state = nextStates.get(0);
            if (gameState.getNextPlayer() == 1) {     //X turn, next O turn
                minMax = alphaBeta(nextStates.get(i), depth, -100000000, 100000, 2);
                if (minMax > temp) {
                  temp = minMax;
                  best_possible_state = nextStates.get(i);
                }
            }
            if (gameState.getNextPlayer() == 2) {
                minMax = alphaBeta(nextStates.get(i), depth, -100000000, 100000, 1);
                if (minMax < temp) {
                  temp = minMax;
                  best_possible_state = nextStates.get(i);
                }
            }
        }
        return best_possible_state;

    }

    private double alphaBeta(GameState analysingState, int depth, double alpha, double beta, int player) {
        Vector<GameState> childStates = new Vector<>();
        analysingState.findPossibleMoves(childStates);
        double v;
        if (depth == 0 || childStates.size() == 0) {
            v = utility(player, analysingState);
        } else if (player == 2) {
            v = -100000000;
            for (GameState childState : childStates) {
                v = Math.max(v, alphaBeta(childState, depth - 1, alpha, beta, 1));
                alpha = Math.max(alpha, v);
                if (beta <= alpha) break;
            }
        } else {
            v = 100000000;
            for (GameState childState : childStates) {
                v = Math.min(v, alphaBeta(childState, depth - 1, alpha, beta, 2));
                beta = Math.min(beta, v);
                if (beta <= alpha) break;
            }
        }
        return v;
    }


    private double utility(int player, GameState state) {
      double[] rows = new double[4];
      double[] columns = new double[4];
      double[] diagonals = new double[2];
      double pos_utility = 0;

      //CELL = 1 is X, CELL = 2 is O (opposite of PLAYERS)

      for (int i = 0; i < 4; i++) {
          for (int j = 0; j < 4; j++) {
            if (state.at(i, j) == 1) {
              //Cheching corners
              if ((i == 0 && j == 0) || (i == 0 && j == 3) ||
              (i == 3 && j == 0) || (i == 3 && j == 3)) pos_utility += 10;

              if ((rows[i] < 0 && rows[i] != -3) || rows[i] == 0.1) rows[i] = 0.1;
              if (rows[i] == -3) {
                rows[i] = 0.1;
                pos_utility += 20000;
              }
              else rows[i] += 1;
              if ((columns[j] < 0 && columns[j] != -3) || columns[j] == 0.1) columns[j] = 0.1;
              if (columns[j] == -3) {
                columns[j] = 0.1;
                pos_utility += 20000;
              }
              else columns[j] += 1;
              if (i == j) {
                if ((diagonals[0] < 0 && diagonals[0] != -3) || diagonals[0] == 0.1) diagonals[0] = 0.1;
                if (diagonals[0] == -3) {
                  diagonals[0] = 0.1;
                  pos_utility += 20000;
                }
                else {
                    diagonals[0] +=1;
                    pos_utility += 10;
                }
              }
              if (i + j == 3){
                if ((diagonals[1] < 0 && diagonals[1] != -3) || diagonals[1] == 0.1) diagonals[1] = 0.1;
                if (diagonals[1] == -3) {
                  diagonals[1] = 0.1;
                  pos_utility += 20000;
                }
                else {
                    diagonals[1] +=1;
                    pos_utility += 10;
                }
              }

            } else if (state.at(i,j) == 2) {
              if (rows[i] > 0) rows[i] = 0.1;
              else rows[i] -= 1;
              if (columns[j] > 0) columns[j] = 0.1;
              else columns[j] -= 1;
              if (i == j) {
                if (diagonals[0] > 0) diagonals[0] = 0.1;
                else {
                    diagonals[0] -=1;
                    pos_utility -= 10;
                }
              }
              if (i + j == 3){
                if (diagonals[1] > 0) diagonals[1] = 0.1;
                else {
                    diagonals[1] -=1;
                    pos_utility -= 10;
                }
              }
            }
          }
        }

        for (int i = 0; i < 4; i++) {
          if (rows[i] == 2) pos_utility += 20;
          if (rows[i] == 3) pos_utility += 500;
          if (rows[i] == 4) pos_utility += 1000000;
          if (columns[i] == 2) pos_utility += 20;
          if (columns[i] == 3) pos_utility += 500;
          if (columns[i] == 4) pos_utility += 1000000;
          if (i < 2) {
            if (diagonals[i] == 2) pos_utility += 20;
            if (diagonals[i] == 3) pos_utility += 500;
            if (diagonals[i] == 4) pos_utility += 1000000;
          }
        }

        return pos_utility;
    }

}