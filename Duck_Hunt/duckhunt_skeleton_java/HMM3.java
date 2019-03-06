import java.util.ArrayList;
import java.util.Random;

public class HMM3{

    double[][] transition;
    double[][] emission;
    double[][] initial_prob;
    int[] obs_sequence;

    double lastdist[][];

    Random random;

    int N, M, T, nRows;
    double multiplier = 0.2;
    double[] info;


    // - - - - - - DUCK HUNT IMPLEMENTATIONS - - - - - -


    public void HMM_algorithm(){

        double[][] conv = new double[1][this.T];

        double[][] alpha = new double[this.T][this.N];
        double[][] beta = new double[this.T][this.N];
        double[][] digamma;
        double[][] gamma = new double[this.T][this.N];

        ArrayList<double[][]> matrix_3D = new ArrayList<>();

        double iters = 1;
        double numerator;
        double denominator;
        double sum;
        double old_log = -1000000;
        double log_prob = 0;
        double c0;
        double ct;

        while (iters < this.T && log_prob > old_log) {

            //Alpha

            for (int t = 0; t < this.T; t++) {
                if (t == 0) {
                    c0 = 0;
                    for (int i = 0; i < this.N; i++) {
                        alpha[0][i] = this.initial_prob[0][i] * this.emission[i][this.obs_sequence[0]];
                    c0 += alpha[0][i];
                    }
                    c0 = 1/c0;
                    for (int i = 0; i < this.N; i++) {
                        alpha[0][i] = c0 * alpha[0][i];
                    }
                    conv[0][0] = c0;

                } else {
                    ct = 0;
                    for (int i = 0; i < this.N; i++) {
                        sum = 0;
                        for (int j = 0; j < this.N; j++) {
                            sum += alpha[t - 1][j] * this.transition[j][i];
                        }
                        alpha[t][i] = sum * this.emission[i][this.obs_sequence[t]];
                        ct += alpha[t][i];
                    }
                    ct = 1/ct;
                    for (int i = 0; i < this.N; i++) {
                        alpha[t][i] = ct * alpha[t][i];
                    }
                    conv[0][t] = ct;
                }
            }

            //print2D(alpha);

            //Beta

            for (int i = 0; i < this.N; i++) {
                beta[this.T-1][i] = conv[0][this.T-1];
            }

            for (int t = this.T-2; t >= 0; t--) {
                for (int i = 0; i < this.N; i++) {
                    sum = 0;
                    for (int j = 0; j < this.N; j++) {
                        sum += beta[t+1][j] * this.emission[j][this.obs_sequence[t+1]] * this.transition[i][j];
                    }
                    sum = conv[0][t] * sum;
                    beta[t][i] = sum;
                }
            }

            //print2D(beta);

            //Di-Gamma and Gamma

            matrix_3D.clear();

            for (int t = 0; t < this.T-1; t++) {
                digamma = new double[this.N][this.N];
                for (int i = 0; i < this.N; i++) {
                    sum = 0;
                    for (int j = 0; j < this.N; j++) {
                        digamma[i][j] = alpha[t][i] * this.transition[i][j] * this.emission[j][this.obs_sequence[t+1]] * beta[t+1][j];
                        sum += digamma[i][j];
                    }
                    gamma[t][i] = sum;
                }
                matrix_3D.add(digamma);
            }

            //print2D(gamma);

            //Initial probability

            for (int i = 0; i < this.N; i++) {
                this.initial_prob[0][i] = gamma[0][i];
            }

            //For the validate function
            this.lastdist = new double [1][this.N];
            for(int i = 0; i < this.N; i++){
                this.lastdist[0][i] = gamma[this.T-1][i];
            }

            //print2D(initial_prob);

            //A Matrix

            for (int i = 0; i < this.N; i++) {
                denominator = 0;
                for (int t = 0; t < this.T - 1; t++) {
                    denominator += gamma[t][i];
                }
                for (int j = 0; j < this.N; j++) {
                    numerator = 0;
                    for (int t = 0; t < this.T - 1; t++) {
                        numerator += matrix_3D.get(t)[i][j];
                    }
                    this.transition[i][j] = numerator/denominator;
                }
            }



            //print2D(transition);

            //B Matrix

            for (int i = 0; i < this.N; i++) {
                denominator = 0;
                for (int t = 0; t < this.T; t++) {
                    denominator += gamma[t][i];
                }
                for (int j = 0; j < this.M; j++) {
                    numerator = 0;
                    for (int t = 0; t < this.T; t++) {
                        if (this.obs_sequence[t] == j){
                            numerator += gamma[t][i];
                        }
                    }
                    this.emission[i][j] = numerator/denominator;
                }
            }

            //print2D(emission);

            //Calculating convergence

            log_prob = 0;
            for (int t = 0; t < this.T; t++) {
                log_prob += Math.log(conv[0][t]);
            }
            log_prob = -log_prob;


            iters += 1;
            if (iters < this.T && log_prob > old_log){
                old_log = log_prob;
                log_prob = 100;
            }
        }
    }


    // - - - - - - - - - - - - - - - - - - -

    public void validate(){

        double[] foo = new double[this.N];
        for (int i = 0; i < this.transition.length; i++) {
            double bar = 0.0;
            for (int j = 0; j < this.initial_prob[0].length; j++) {
                bar += this.lastdist[0][j] * this.transition[j][i];
            }
            foo[i] = bar;
        }

        double[] bar = new double[this.M];
        double temp_sum = 0.0;
        for (int i = 0; i < this.emission[0].length; i++) {
            double baz = 0.0;
            for (int j = 0; j < this.emission.length; j++) {
                baz += foo[j] * this.emission[j][i];
            }
            bar[i] = baz;
            temp_sum += baz;
        }

        for(int i=0; i<bar.length;i++){
            if (temp_sum == 0.0){
                bar[i] = 0.0;
            }
            else{
                bar[i] = bar[i]/temp_sum;
            }

        }

        this.info = bar;
        // System.err.println("Bar: " + Arrays.toString(bar));

    }


    // - - - - - - - - - - - - - - - - - - -


    public void updateObsSeq(int T, int[] obs_sequence){
        this.T = T;
        this.obs_sequence = obs_sequence;
    }


    // - - - - - - - - - - - - - - - - - - -


    public HMM3(int N, int M, int nRows, int T, int[] obs_sequence){
        random = new Random();

        this.N = N;
        this.M = M;
        this.nRows = nRows;
        this.transition = new double[this.N][this.N];
        this.emission = new double[this.N][this.M];
        this.initial_prob = new double[this.nRows][this.N];

        //A Matrix

        for (int i = 0; i < this.N; i++) {
            double holder = 0;
            for (int t = 0; t < this.N; t++) {
                this.transition[i][t] = 1 / this.N + random.nextDouble() * multiplier / this.N;
                holder += this.transition[i][t];
            }
            for (int j = 0; j < this.N; j++) {
                this.transition[i][j] /= holder;
            }
        }

        //B Matrix

        for (int i = 0; i < this.N; i++) {
            double holder = 0;
            for (int t = 0; t < this.M; t++) {
                this.emission[i][t] = 1 / this.M + random.nextDouble() * multiplier / this.M;
                holder += this.emission[i][t];
            }
            for (int j = 0; j < this.M; j++){
                this.emission[i][j] /= holder;
            }

        }

        //Initial probability

        double holder = 0;
        for (int i = 0; i < this.N; i++) {
            initial_prob[0][i] = 1 / this.N + random.nextDouble() * multiplier / this.N;
            holder += initial_prob[0][i];
        }
        for (int i = 0; i < this.N; i++){
        initial_prob[0][i] /=  holder;
        }

        //Observation sequence

        this.T = T;
        this.obs_sequence = obs_sequence;


    }


    // - - - - - - - - - - - - - - - - - - -


    public double updateAlpha(int[] observations){
        double prob = 0;
        double[][] alpha = new double[observations.length][this.N];

        for (int t = 0; t < observations.length; t++){
            if (t == 0){
                for (int i = 0; i < this.N; i++){
                    alpha[0][i] = this.initial_prob[0][i] * this.emission[i][observations[0]];
                }
            } else {
                for (int i = 0; i < this.N; i++){
                    for (int j = 0; j < this.N; j++){
                        alpha[t][i] += alpha[t-1][j] * this.transition[j][i] * this.emission[i][observations[t]];
                    }
                }
            }
        }

        for (int i = 0; i < this.N; i++){
            prob += alpha[observations.length - 1][i];
        }
        return prob;
    }


    // - - - - - - - - - - - - - - - - - - -


    public double ShotProb(){
        double max = 0;
        int maxIdx = 0;
        for (int i = 0; i < this.info.length; i++){
            if (this.info[i] > max){
                max = this.info[i];
                maxIdx = i;
            }
        }
        return max;
    }


    // - - - - - - - - - - - - - - - - - - -


    public int ShootBird(){
        double max = 0;
        int maxIdx = 0;
        for (int i = 0; i < this.info.length; i++){
            if (this.info[i] > max){
                max = this.info[i];
                maxIdx = i;
            }
        }
        return maxIdx;
    }


    // - - - - - - FUNCTIONS - - - - - -


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
