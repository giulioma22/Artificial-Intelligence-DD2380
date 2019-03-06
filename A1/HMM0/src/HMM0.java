import java.util.Scanner;

public class HMM0 {
    public static void main(String args[]){
        Scanner sc = new Scanner(System.in);
        String input1 = sc.nextLine();
        String input2 = sc.nextLine();
        String input3 = sc.nextLine();

        double[][] transition = string2matrix(input1);
        double[][] emission = string2matrix(input2);
        double[][] initial_prob = string2matrix(input3);

        double[][] C = matrixMulti(initial_prob,transition);
        double[][] obs = matrixMulti(C,emission);

        printmatrix2string(obs);

        return;
    }

    public static double[][] string2matrix(String all){
        Scanner scan = new Scanner(all);
        int row = scan.nextInt();
        int col = scan.nextInt();

        double[][] matrix = new double[row][col];

        for (int i=0;i<row;i++){
            for (int j=0;j<col;j++){

                matrix[i][j] = Double.parseDouble(scan.next());

            }
        }

        return matrix;
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
            for (int j = 0; j < mat[i].length; j++)
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
