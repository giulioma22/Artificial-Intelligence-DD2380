import java.util.Scanner;

public class HMM2 {
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
        int T = obs_sequence.length; //N of emissions

        double max = 0;
        int max_idx = 1;
        double [][] delta = new double[T][N];
        int [][] delta_idx = new int[T][N];
        int [][] sequence = new int[1][T];

        for (int t = 0; t < T; t++){
            if (t == 0){
                for (int i = 0; i < N; i++){
                    delta[0][i] = initial_prob[0][i] * emission[i][obs_sequence[0]];
                }
            } else {
                for (int i = 0; i < N; i++){
                    max = 0;
                    max_idx = 0;
                    for (int j = 0; j < N; j++){
                        double temp = transition[j][i] * delta[t-1][j] * emission[i][obs_sequence[t]];
                        if (max < temp){
                            max = temp;
                            max_idx = j;
                        }
                    }
                    delta[t][i] = max;
                    delta_idx[t][i] = max_idx;
                }
            }
        }

        for (int t = T-1; t >= 0; t--){
            if (t == T-1){
                max = 0;
                for (int k = 0; k < N; k++){
                    if (max < delta[t][k]){
                        max = delta[t][k];
                        sequence[0][t] = k;
                    }
                }
            } else {
                sequence[0][t] = delta_idx[t+1][sequence[0][t+1]];
            }
        }

        printINTmatrix2string(sequence);

    }

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

    public static int[][] string2INTmatrix(String all){
        Scanner scan = new Scanner(all);
        int row = scan.nextInt();
        int col = scan.nextInt();

        int[][] matrix = new int[row][col];

        for (int i = 0; i < row; i++){
            for (int j = 0; j < col; j++){

                matrix[i][j] = scan.nextInt();
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

    public static void printINTmatrix2string(int mat[][])
    {
        int[][] matrix =  mat;
        int row = matrix.length;
        int col = matrix[0].length;

        //System.out.print(row + " " + col + " ");
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
