import java.util.*;

public class Player {
    /**
     * Performs a move
     *
     * @param gameState
     *            the current state of the board
     * @param deadline
     *            time before which we must have returned
     * @return the next state the board is in after our move
     */

    public GameState play(final GameState gameState, final Deadline deadline) {
        Vector<GameState> nextStates = new Vector<GameState>();
        gameState.findPossibleMoves(nextStates);

        double minMax = 0;
        double temp = 0;
        GameState best_possible_state = null;

        //PLAYER X is 2, while PLAYER O is 1

        for (int i = 0; i < nextStates.size(); i++) {
            if (i == 0) best_possible_state = nextStates.get(0);
            if (gameState.getNextPlayer() == 1) {     //X turn, next O turn
                minMax = alphaBeta(nextStates.get(i), 0, -100000000, 100000000, 2);
                if (minMax > temp) {
                  temp = minMax;
                  best_possible_state = nextStates.get(i);
                }
            }
            if (gameState.getNextPlayer() == 2) {
                minMax = alphaBeta(nextStates.get(i), 0, -100000000, 100000000, 1);
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
            v = heuristic(player, analysingState);
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


    private double heuristic(int player, GameState state) {

      double[][] rows_at_depth = new double[4][4];
      double[][] columns_at_depth = new double[4][4];
      double[][] side_rows = new double[4][4];
      double[][] diagonals_at_depth = new double[4][2];
      double[][] side_diagonals = new double[4][2];
      double[][] top_diagonals = new double[4][2];
      double[] diagonals_3d = new double[4];
      double pos_utility = 0;

      //CELL = 1 is X, CELL = 2 is O (opposite of PLAYERS)

      for (int k = 0; k < 4; k++) {
        for (int i = 0; i < 4; i++) {
          for (int j = 0; j < 4; j++) {

            //Counting the X marks
            if (state.at(i, j, k) == 1) {
              if ((rows_at_depth[i][k] < 0 && rows_at_depth[i][k] != -3) || rows_at_depth[i][k] == 0.1) rows_at_depth[i][k] = 0.1;
              if (rows_at_depth[i][k] == -3) {
                rows_at_depth[i][k] = 0.1;
                pos_utility += 5000;
              }
              else rows_at_depth[i][k] += 1;
              if ((columns_at_depth[j][k] < 0 && columns_at_depth[j][k] != -3) || columns_at_depth[j][k] == 0.1) columns_at_depth[j][k] = 0.1;
              if (columns_at_depth[j][k] == -3) {
                columns_at_depth[j][k] = 0.1;
                pos_utility += 5000;
              }
              else columns_at_depth[j][k] += 1;
              if ((side_rows[i][j] < 0 && side_rows[i][j] != -3) || side_rows[i][j] == 0.1) side_rows[i][j] = 0.1;
              if (side_rows[i][j] == -3) {
                side_rows[i][j] = 0.1;
                pos_utility += 5000;
              }
              else side_rows[i][j] += 1;

              //Diagonals on the 2D faces (X)
              if (i == j) {
                if ((diagonals_at_depth[k][0] < 0 && diagonals_at_depth[k][0] != -3) || diagonals_at_depth[k][0] == 0.1) diagonals_at_depth[k][0] = 0.1;
                if (diagonals_at_depth[k][0] == -3) {
                  diagonals_at_depth[k][0] = 0.1;
                  pos_utility += 5000;
                }
                else {
                    diagonals_at_depth[k][0] +=1;
                    pos_utility += 10;
                }
              }
              if (i + j == 3){
                if ((diagonals_at_depth[k][1] < 0 && diagonals_at_depth[k][1] != -3) || diagonals_at_depth[k][1] == 0.1) diagonals_at_depth[k][1] = 0.1;
                if (diagonals_at_depth[k][1] == -3) {
                  diagonals_at_depth[k][1] = 0.1;
                  pos_utility += 5000;
                }
                else {
                    diagonals_at_depth[k][1] +=1;
                    pos_utility += 10;
                }
              }

              //Diagonals on the SIDE faces (X)
              if (i == k) {
                if ((side_diagonals[j][0] < 0 && side_diagonals[j][0] != -3) || side_diagonals[j][0] == 0.1) side_diagonals[j][0] = 0.1;
                if (side_diagonals[j][0] == -3) {
                  side_diagonals[j][0] = 0.1;
                  pos_utility += 5000;
                }
                else {
                    side_diagonals[j][0] +=1;
                    pos_utility += 10;
                }
              }
              if (i + k == 3){
                if ((side_diagonals[j][1] < 0 && side_diagonals[j][1] != -3) || side_diagonals[j][1] == 0.1) side_diagonals[j][1] = 0.1;
                if (side_diagonals[j][1] == -3) {
                  side_diagonals[j][1] = 0.1;
                  pos_utility += 5000;
                }
                else {
                    side_diagonals[j][1] +=1;
                    pos_utility += 10;
                }
              }

              //Diagonals on the TOP faces (X)
              if (j == k) {
                if ((top_diagonals[i][0] < 0 && top_diagonals[i][0] != -3) || top_diagonals[i][0] == 0.1) top_diagonals[i][0] = 0.1;
                if (top_diagonals[i][0] == -3) {
                  top_diagonals[i][0] = 0.1;
                  pos_utility += 5000;
                }
                else {
                    top_diagonals[i][0] +=1;
                    pos_utility += 10;
                }
              }
              if (j + k == 3){
                if ((top_diagonals[i][1] < 0 && top_diagonals[i][1] != -3) || top_diagonals[i][1] == 0.1) top_diagonals[i][1] = 0.1;
                if (top_diagonals[i][1] == -3) {
                  top_diagonals[i][1] = 0.1;
                  pos_utility += 5000;
                }
                else {
                    top_diagonals[i][1] +=1;
                    pos_utility += 10;
                }
              }

              //3D diagonals (X)

              if (i == j && j == k) {      //Index 0
                if ((diagonals_3d[0] < 0 && diagonals_3d[0] != -3) || diagonals_3d[0] == 0.1) diagonals_3d[0] = 0.1;
                if (diagonals_3d[0] == -3) {
                  diagonals_3d[0] = 0.1;
                  pos_utility += 5000;
                }
                else {
                    diagonals_3d[0] +=1;
                    pos_utility += 100;
                }
              }
              if (3-i == j && j == k) {      //Index 1
                if ((diagonals_3d[1] < 0 && diagonals_3d[1] != -3) || diagonals_3d[1] == 0.1) diagonals_3d[1] = 0.1;
                if (diagonals_3d[1] == -3) {
                  diagonals_3d[1] = 0.1;
                  pos_utility += 5000;
                }
                else {
                    diagonals_3d[1] +=1;
                    pos_utility += 100;
                }
              }
              if (i == 3-j && 3-j == k) {      //Index 2
                if ((diagonals_3d[2] < 0 && diagonals_3d[2] != -3) || diagonals_3d[2] == 0.1) diagonals_3d[2] = 0.1;
                if (diagonals_3d[2] == -3) {
                  diagonals_3d[2] = 0.1;
                  pos_utility += 5000;
                }
                else {
                    diagonals_3d[2] +=1;
                    pos_utility += 100;
                }
              }
              if (3-i == 3-j && 3-j == k) {      //Index 3
                if ((diagonals_3d[3] < 0 && diagonals_3d[3] != -3) || diagonals_3d[3] == 0.1) diagonals_3d[3] = 0.1;
                if (diagonals_3d[3] == -3) {
                  diagonals_3d[3] = 0.1;
                  pos_utility += 5000;
                }
                else {
                    diagonals_3d[3] +=1;
                    pos_utility += 100;
                }
              }

              //Counting the O marks
              } else if (state.at(i,j,k) == 2) {
                if (rows_at_depth[i][k] > 0) rows_at_depth[i][k] = 0.1;
                else rows_at_depth[i][k] -= 1;
                if (columns_at_depth[j][k] > 0) columns_at_depth[j][k] = 0.1;
                else columns_at_depth[j][k] -= 1;
                if (side_rows[i][j] > 0) side_rows[i][j] = 0.1;
                else side_rows[i][j] -= 1;

                //Diagonals on the 2D faces (O)
                if (i == j) {
                  if (diagonals_at_depth[k][0] > 0) diagonals_at_depth[k][0] = 0.1;
                  else {
                      diagonals_at_depth[k][0] -=1;
                      pos_utility -= 10;
                  }
                }
                if (i + j == 3){
                  if (diagonals_at_depth[k][1] > 0) diagonals_at_depth[k][1] = 0.1;
                  else {
                      diagonals_at_depth[k][1] -=1;
                      pos_utility -= 10;
                  }
                }

                //Diagonals on the SIDE faces (O)
                if (i == k) {
                  if (side_diagonals[j][0] > 0) side_diagonals[j][0] = 0.1;
                  else {
                      side_diagonals[j][0] -=1;
                      pos_utility -= 10;
                  }
                }
                if (i + k == 3){
                  if (side_diagonals[j][1] > 0) side_diagonals[j][1] = 0.1;
                  else {
                      side_diagonals[j][1] -=1;
                      pos_utility -= 10;
                  }
                }

                //Diagonals on the TOP faces (O)
                if (j == k) {
                  if (top_diagonals[i][0] > 0) top_diagonals[i][0] = 0.1;
                  else {
                      top_diagonals[i][0] -=1;
                      pos_utility -= 10;
                  }
                }
                if (j + k == 3){
                  if (top_diagonals[i][1] > 0) top_diagonals[i][1] = 0.1;
                  else {
                      top_diagonals[i][1] -=1;
                      pos_utility -= 10;
                  }
                }

                //3D diagonals (O)
                if (i == j && j == k) {      //Index 0
                  if (diagonals_3d[0] > 0) diagonals_3d[0] = 0.1;
                  else {
                      diagonals_3d[0] -=1;
                      pos_utility -= 100;
                  }
                }
                if (3-i == j && j == k) {      //Index 1
                  if (diagonals_3d[1] > 0) diagonals_3d[1] = 0.1;
                  else {
                      diagonals_3d[1] -=1;
                      pos_utility -= 100;
                  }
                }
                if (i == 3-j && 3-j == k) {      //Index 2
                  if (diagonals_3d[2] > 0) diagonals_3d[2] = 0.1;
                  else {
                      diagonals_3d[2] -=1;
                      pos_utility -= 100;
                  }
                }
                if (3-i == 3-j && 3-j == k) {      //Index 3
                  if (diagonals_3d[3] > 0) diagonals_3d[3] = 0.1;
                  else {
                      diagonals_3d[3] -=1;
                      pos_utility -= 100;
                  }
                }

              }
          }
        }
      }

      //Calculating heuristic based on number of X
      for (int i = 0; i < 4; i++) {
        if (diagonals_3d[i] == 2) pos_utility += 10;
        if (diagonals_3d[i] == 3) pos_utility += 500;
        if (diagonals_3d[i] == 4) pos_utility += 1000000;
        for (int j = 0; j < 4; j++) {
          if (rows_at_depth[i][j] == 2) pos_utility += 10;
          if (rows_at_depth[i][j] == 3) pos_utility += 500;
          if (rows_at_depth[i][j] == 4) pos_utility += 1000000;
          if (columns_at_depth[i][j] == 2) pos_utility += 10;
          if (columns_at_depth[i][j] == 3) pos_utility += 500;
          if (columns_at_depth[i][j] == 4) pos_utility += 1000000;
          if (side_rows[i][j] == 2) pos_utility += 10;
          if (side_rows[i][j] == 3) pos_utility += 500;
          if (side_rows[i][j] == 4) pos_utility += 1000000;
          if (j < 2) {
            if (diagonals_at_depth[i][j] == 2) pos_utility += 10;
            if (diagonals_at_depth[i][j] == 3) pos_utility += 500;
            if (diagonals_at_depth[i][j] == 4) pos_utility += 1000000;
            if (side_diagonals[i][j] == 2) pos_utility += 10;
            if (side_diagonals[i][j] == 3) pos_utility += 500;
            if (side_diagonals[i][j] == 4) pos_utility += 1000000;
            if (top_diagonals[i][j] == 2) pos_utility += 10;
            if (top_diagonals[i][j] == 3) pos_utility += 500;
            if (top_diagonals[i][j] == 4) pos_utility += 1000000;
          }
        }
      }

      return pos_utility;
      }

}