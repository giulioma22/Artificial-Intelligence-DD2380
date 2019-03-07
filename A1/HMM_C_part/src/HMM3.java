import java.util.ArrayList;
import java.util.Scanner;

public class HMM3 {
    public static void main(String[] args){
        Scanner sc = new Scanner(System.in);
        String input1 = sc.nextLine();
        String input2 = sc.nextLine();
        String input3 = sc.nextLine();
        String input4 = sc.nextLine();
        sc.close();

        double[][] transition = string2matrix(input1);
        double[][] emission = string2matrix(input2);
        double[][] initial_prob = string2matrix(input3);
        int[] obs_sequence = string2array(input4);

        int N = transition.length;
        int M = emission[0].length;
        int T = obs_sequence.length; //Number of emissions

        double[][] conv = new double[1][T];

        double[][] alpha = new double[T][N];
        double[][] beta = new double[T][N];
        double[][] digamma;     //Initialized later to avoid the static problem when adding to matrix_3D
        double[][] gamma = new double[T][N];

        ArrayList<double[][]> matrix_3D = new ArrayList<>();

        double iters = 1;
        double numerator;
        double denominator;
        double sum;
        double old_log = -1000000;
        double log_prob = 0;
        double c0;
        double ct;

        while (iters < T && log_prob > old_log) {

            //Alpha

            for (int t = 0; t < T; t++) {
                if (t == 0) {
                    c0 = 0;
                    for (int i = 0; i < N; i++) {
                        alpha[0][i] = initial_prob[0][i] * emission[i][obs_sequence[0]];
                    c0 += alpha[0][i];
                    }
                    c0 = 1/c0;
                    for (int i = 0; i < N; i++) {
                        alpha[0][i] = c0 * alpha[0][i];
                    }
                    conv[0][0] = c0;

                } else {
                    ct = 0;
                    for (int i = 0; i < N; i++) {
                        sum = 0;
                        for (int j = 0; j < N; j++) {
                            sum += alpha[t - 1][j] * transition[j][i];
                        }
                        alpha[t][i] = sum * emission[i][obs_sequence[t]];
                        ct += alpha[t][i];
                    }
                    ct = 1/ct;
                    for (int i = 0; i < N; i++) {
                        alpha[t][i] = ct * alpha[t][i];
                    }
                    conv[0][t] = ct;
                }
            }

            //print2D(alpha);

            //Beta

            for (int i = 0; i < N; i++) {
                beta[T-1][i] = conv[0][T-1];
            }

            for (int t = T-2; t >= 0; t--) {
                for (int i = 0; i < N; i++) {
                    sum = 0;
                    for (int j = 0; j < N; j++) {
                        sum += beta[t+1][j] * emission[j][obs_sequence[t+1]] * transition[i][j];
                    }
                    sum = conv[0][t] * sum;
                    beta[t][i] = sum;
                }
            }

            //print2D(beta);

            //Di-Gamma and Gamma

            matrix_3D.clear();

            for (int t = 0; t < T-1; t++) {
                digamma = new double[N][N];
                for (int i = 0; i < N; i++) {
                    sum = 0;
                    for (int j = 0; j < N; j++) {
                        digamma[i][j] = alpha[t][i] * transition[i][j] * emission[j][obs_sequence[t+1]] * beta[t+1][j];
                        sum += digamma[i][j];
                    }
                    gamma[t][i] = sum;
                }
                matrix_3D.add(digamma);
            }

            //print2D(gamma);

            //Initial probability

            for (int i = 0; i < N; i++) {
                initial_prob[0][i] = gamma[0][i];
            }

            //print2D(initial_prob);

            //A Matrix

            for (int i = 0; i < N; i++) {
                denominator = 0;
                for (int t = 0; t < T - 1; t++) {
                    denominator += gamma[t][i];
                }
                for (int j = 0; j < N; j++) {
                    numerator = 0;
                    for (int t = 0; t < T - 1; t++) {
                        numerator += matrix_3D.get(t)[i][j];
                    }
                    transition[i][j] = numerator/denominator;
                }
            }

            //print2D(transition);

            //B Matrix

            for (int i = 0; i < N; i++) {
                denominator = 0;
                for (int t = 0; t < T; t++) {
                    denominator += gamma[t][i];
                }
                for (int j = 0; j < M; j++) {
                    numerator = 0;
                    for (int t = 0; t < T; t++) {
                        if (obs_sequence[t] == j){
                            numerator += gamma[t][i];
                        }
                    }
                    emission[i][j] = numerator/denominator;
                }
            }

            //print2D(emission);

            //Calculating convergence

            log_prob = 0;
            for (int t = 0; t < T; t++) {
                log_prob += Math.log(conv[0][t]);
            }
            log_prob = -log_prob;


            iters += 1;
            if (iters < T && log_prob > old_log){
                old_log = log_prob;
                log_prob = 100;
            }
        }

        System.err.println("Number of iterations: " + iters);
        printmatrix2string(transition);
        System.err.println();
        printmatrix2string(emission);

    }




    // - - - - - - FUNCTIONS - - - - - -


    public static double[][] string2matrix(String all){
        Scanner scan = new Scanner(all);
        int row = scan.nextInt();
        int col = scan.nextInt();

        double[][] matrix = new double[row][col];

        for (int i = 0; i < row; i++){
            for (int j = 0; j < col; j++){

                matrix[i][j] = Double.parseDouble(scan.next());
            }
        }

        return matrix;
    }

    public static int[] string2array(String all){
        Scanner scan = new Scanner(all);
        int row = scan.nextInt();

        int[] array = new int[row];

        for (int i = 0; i < row; i++){
            array[i] = scan.nextInt();
        }

        return array;
    }

    public static void printmatrix2string(double mat[][])
    {
        double[][] matrix =  mat;
        int row = matrix.length;
        int col = matrix[0].length;

        System.out.print(row + " " + col + " ");
        for (int i = 0; i < mat.length; i++) {

            // Loop through all elements of current row
            for (int j = 0; j < mat[i].length; j++)
                System.out.print(mat[i][j] + " ");
        }
        return;
    }

    public static void print2D(double mat[][])
    {
        // Loop through all rows
        for (int i = 0; i < mat.length; i++) {

            // Loop through all elements of current row
            for (int j = 0; j < mat[0].length; j++)
                System.out.print(mat[i][j] + " ");

            System.out.println();
        }
    }

    public static double[][] matrixMulti(double mat1[][], double mat2[][]) {

        double[][] product = new double[mat1.length][mat2[0].length];

        for (int i = 0; i < mat1.length; i++) {
            for (int j = 0; j < mat2[0].length; j++) {
                for (int k = 0; k < mat1[0].length; k++) {
                    product[i][j] += mat1[i][k] * mat2[k][j];
                }
            }
        }

        return product;
    }

}