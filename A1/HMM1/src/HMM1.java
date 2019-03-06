import java.util.Scanner;

public class HMM1 {
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
        int T = obs_sequence.length; //Number of emissions


        //Alpha

        double[][] alpha = new double[T][N];
        double temp = 0;
        double total = 0;

        for (int t = 0; t < T; t++){
            if (t == 0){
                for (int i = 0; i < N; i++){
                    alpha[0][i] = initial_prob[0][i] * emission[i][obs_sequence[0]];
                }
            } else {
                for (int i = 0; i < N; i++){
                    temp = 0;
                    for (int j = 0; j < N; j++){
                        temp += alpha[t-1][j] * transition[j][i];
                    }
                    alpha[t][i] = temp * emission[i][obs_sequence[t]];
                }
            }
        }

        for (int i = 0; i < N; i++){
            total += alpha[T-1][i];
        }

        System.out.println(total);

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

