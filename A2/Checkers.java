import java.util.Random;
import java.util.Vector;

public class Player {

    public int center = 3;
    public int defense = 1;
    public int back_row = 7;
    public int king_val = 4;
    public int depth = 7;

    /**
     * @param pState
     * the current state of the board
     * @param pDue
     * time before which we must have returned
     * @return the next state the board is in after our move
     */

    int stat_count;

    public GameState play(final GameState pState, final Deadline pDue) {

        Vector<GameState> lNextStates = new Vector<>();
        pState.findPossibleMoves(lNextStates);

        //WHITE is 2, RED is 1

        double minMax;
        double temp = 0;
        GameState best_possible_state = null;

        Random r = new Random();

        for (int i = 0; i < lNextStates.size(); i++) {
            if (i == 0)
                best_possible_state = lNextStates.get(r.nextInt(lNextStates.size()));

            //if (pDue.timeUntil() < 50e7) break;

            if (pState.getNextPlayer() == 2) {     //WHITE turn
                minMax = alphaBeta(lNextStates.get(i), depth, -100000000, 100000000, 1);
                if (minMax >= temp) {
                    temp = minMax;
                    best_possible_state = lNextStates.get(i);
                }
            }
            if (pState.getNextPlayer() == 1) {      //RED turn
                minMax = alphaBeta(lNextStates.get(i), depth, -100000000, 100000000, 2);
                if (minMax <= temp) {
                    temp = minMax;
                    best_possible_state = lNextStates.get(i);
                }
            }
        }
        stat_count++;
        System.err.println("Round no: " + stat_count);

        return best_possible_state;
    }

    private double alphaBeta(GameState analysingState, int depth, double alpha, double beta, int player) {
        Vector<GameState> childStates = new Vector<>();
        analysingState.findPossibleMoves(childStates);
        double v;
        // is EOG
//        if(isEog)
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


//     private double iddfs(GameState root) {
//         for (int depth = 0; depth < 1000; depth++) {
//             int remaining = dls(root, depth);
//             int found = dls(root, depth);
//             if (found != 0) {
//                 return found;
//             } else if (0 != remaining) {
//                 return 0;
//             }
//         }
//         return 0;
//     }
//
//     private int dls(GameState state, int depth) {
//
//         if (depth == 0) {
// //            if (state ==)
//         }
//         return 3;
//     }


    private double heuristic(int player, GameState state) {

        int tot_utility = 0;

        int red_pieces = 0;
        int white_pieces = 0;
        int red_kings = 0;
        int white_kings = 0;

        if (player == 1 && state.isRedWin()) return -100000;
        if (player == 2 && state.isWhiteWin()) return 1000000;
        if (state.getMovesUntilDraw() < 5 && state.isDraw()) return 0;

        // Counting number of RED and WHITE pieces & kings
        for (int i = 0; i < 32; i++) {
            if (0 != (state.get(i) & Constants.CELL_RED)) {
                if (i == 0 || i == 1 || i == 2 || i == 3)
                    tot_utility -= back_row;
                if (i == 13 || i == 14 || i == 17 || i == 18)
                    tot_utility -= center;
                ++red_pieces;
                if (0 != (state.get(i) & Constants.CELL_KING)) {
                    ++red_kings;
                }
//                Checking for protection
                if (i > 4) {
                    if (0 != (state.get(i - 5) & Constants.CELL_RED))
                        tot_utility -= defense;
                    if (0 != (state.get(i - 4) & Constants.CELL_RED))
                        tot_utility -= defense;
                }
            }
            if (0 != (state.get(i) & Constants.CELL_WHITE)) {
                ++white_pieces;
                if (i == 28 || i == 29 || i == 30 || i == 31)
                    tot_utility += back_row;
                if (i == 13 || i == 14 || i == 17 || i == 18)
                    tot_utility += center;
                if (0 != (state.get(i) & Constants.CELL_KING)) {
                    ++white_kings;
                }
                if (i < 32 - 5) {
                    if (0 != (state.get(i + 5) & Constants.CELL_WHITE))
                        tot_utility += defense;
                    if (0 != (state.get(i + 4) & Constants.CELL_WHITE))
                        tot_utility += defense;
                }
            }
        }

        tot_utility += (white_pieces + king_val * white_kings) - (red_pieces + king_val * red_kings);
        return tot_utility;

    }
}