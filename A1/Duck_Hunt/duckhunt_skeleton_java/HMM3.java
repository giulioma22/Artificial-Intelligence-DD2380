import java.util.ArrayList;
import java.util.Random;

public class HMM3{

    double[][] transition;
    double[][] emission;
    double[] initial_prob;
    int[] obs_sequence;

    double lastdist[][];

    Random random;

    int N, M, T;
    double multiplier = 0.2;
    long seed = (long)5000000000.0;
    double[] info;


    // - - - - - - DUCK HUNT IMPLEMENTATIONS - - - - - -


    public HMM3(int N, int M, int T, int[] obs_sequence){
        random = new Random(seed);

        this.N = N;
        this.M = M;
        this.transition = new double[N][N];
        this.emission = new double[N][M];
        this.initial_prob = new double[N];

        //A Matrix - Transition

        for (int i = 0; i < N; i++) {
            double holder = 0;
            for (int t = 0; t < N; t++) {
                this.transition[i][t] = random.nextDouble() * multiplier / N;
                holder += this.transition[i][t];
            }
            for (int j = 0; j < N; j++) {
                this.transition[i][j] /= holder;
            }
        }

        //B Matrix - Emission

        for (int i = 0; i < N; i++) {
            double holder = 0;
            for (int t = 0; t < M; t++) {
                this.emission[i][t] = random.nextDouble() * multiplier / M;
                holder += this.emission[i][t];
            }
            for (int j = 0; j < M; j++){
                this.emission[i][j] /= holder;
            }

        }

        //Initial probability

        double holder = 0;
        for (int i = 0; i < N; i++) {
            initial_prob[i] = random.nextDouble() * multiplier / N;
            holder += initial_prob[i];
        }
        for (int i = 0; i < N; i++){
            initial_prob[i] /= holder;
        }

        //Observation sequence

        this.T = T;
        this.obs_sequence = obs_sequence;

    }


    // - - - - - - - - - - - - - - - - - - -


    public void HMM_algorithm(){

        double iters = 0;
        double numerator;
        double denominator;
        double sum;
        double old_log = -1000000;
        double log_prob = 0;
        double c0;
        double ct;

        ArrayList<double[][]> matrix_3D = new ArrayList<>();

        while (iters < T && log_prob > old_log) {

            double[][] alpha = new double [T][N];
            double[] conv = new double[T];
            double[][] beta = new double[T][N];
            double[][] gamma = new double[T][N];
            double[][] digamma;

            //Alpha

            for (int t = 0; t < T; t++) {
                if (t == 0) {
                    c0 = 0;
                    for (int i = 0; i < N; i++) {
                        alpha[0][i] = this.initial_prob[i] * this.emission[i][this.obs_sequence[0]];
                    c0 += alpha[0][i];
                    }
                    c0 = 1/c0;
                    for (int i = 0; i < N; i++) {
                        alpha[0][i] = c0 * alpha[0][i];
                    }
                    conv[0] = c0;

                } else {
                    ct = 0;
                    for (int i = 0; i < N; i++) {
                        sum = 0;
                        for (int j = 0; j < N; j++) {
                            sum += alpha[t - 1][j] * this.transition[j][i];
                        }
                        alpha[t][i] = sum * this.emission[i][this.obs_sequence[t]];
                        ct += alpha[t][i];
                    }
                    ct = 1/ct;
                    for (int i = 0; i < N; i++) {
                        alpha[t][i] = ct * alpha[t][i];
                    }
                    conv[t] = ct;
                }
            }


            //Beta

            for (int i = 0; i < N; i++) {
                beta[T-1][i] = conv[T-1];
            }

            for (int t = T-2; t >= 0; t--) {
                for (int i = 0; i < N; i++) {
                    sum = 0;
                    for (int j = 0; j < N; j++) {
                        sum += beta[t+1][j] * this.emission[j][this.obs_sequence[t+1]] * this.transition[i][j];
                    }
                    sum = conv[t] * sum;
                    beta[t][i] = sum;
                }
            }

            //Di-Gamma and Gamma

            matrix_3D.clear();

            for (int t = 0; t < T-1; t++) {
                digamma = new double[N][N];
                for (int i = 0; i < N; i++) {
                    sum = 0;
                    for (int j = 0; j < N; j++) {
                        digamma[i][j] = alpha[t][i] * this.transition[i][j] * this.emission[j][this.obs_sequence[t+1]] * beta[t+1][j];
                        sum += digamma[i][j];
                    }
                    gamma[t][i] = sum;
                }
                matrix_3D.add(digamma);
            }

            denominator = 0;
            for(int i = 0; i < N; i++){
                denominator += alpha[T-1][i];
            }
            for(int i = 0; i < N; i++){
                gamma[T-1][i] = (alpha[T-1][i]) / denominator;
            }

            //Initial probability

            for (int i = 0; i < N; i++) {
                this.initial_prob[i] = gamma[0][i];
            }

            //For the validate function
            this.lastdist = new double [1][N];
            for(int i = 0; i < N; i++){
                this.lastdist[0][i] = gamma[T-1][i];
            }

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
                    this.transition[i][j] = numerator/denominator;

                }
            }

            //B Matrix

            for (int i = 0; i < N; i++) {
                denominator = 0;
                for (int t = 0; t < T; t++) {
                    denominator += gamma[t][i];
                }
                for (int j = 0; j < M; j++) {
                    numerator = 0;
                    for (int t = 0; t < T; t++) {
                        if (this.obs_sequence[t] == j){
                            numerator += gamma[t][i];
                        }
                    }
                    this.emission[i][j] = numerator/denominator;

                }
            }

            //Calculating convergence

            log_prob = 0;
            for (int t = 0; t < T; t++) {
                log_prob += Math.log(1/conv[t]);
            }
            log_prob = -log_prob;


            iters += 1;
            if (iters < T && log_prob > old_log){
                old_log = log_prob;
                log_prob = 100;
            }
        }

    }


    // - - - - - - - - - - - - - - - - - - -

    public void validate(){

        double[] foo = new double[N];
        for (int i = 0; i < this.transition.length; i++) {
            double bar = 0;
            for (int j = 0; j < this.initial_prob.length; j++) {
                bar += this.lastdist[0][j] * this.transition[j][i];
            }
            foo[i] = bar;
        }

        double[] bar = new double[M];
        double temp_sum = 0;
        for (int i = 0; i < this.emission[0].length; i++) {
            double baz = 0;
            for (int j = 0; j < this.emission.length; j++) {
                baz += foo[j] * this.emission[j][i];
            }
            bar[i] = baz;
            temp_sum += baz;
        }

        for(int i = 0; i < bar.length; i++){
            if (temp_sum == 0){
                bar[i] = 0;
            }
            else{
                bar[i] /= temp_sum;
            }

        }

        this.info = bar;

    }


    // - - - - - - - - - - - - - - - - - - -


    public void updateObsSeq(int T, int[] obs_sequence){
        this.T = T;
        this.obs_sequence = obs_sequence;
    }


    // - - - - - - - - - - - - - - - - - - -


    public double alphaProb(int[] observations){
        double prob = 0;
        double[][] alpha = new double[observations.length][this.N];

        for (int t = 0; t < observations.length; t++){
            if (t == 0){
                for (int i = 0; i < N; i++){
                    alpha[0][i] = this.initial_prob[i] * this.emission[i][observations[0]];
                }
            } else {
                for (int i = 0; i < N; i++){
                    for (int j = 0; j < N; j++){
                        alpha[t][i] += alpha[t-1][j] * this.transition[j][i] * this.emission[i][observations[t]];
                    }
                }
            }
        }

        for (int i = 0; i < N; i++){
            prob += alpha[observations.length - 1][i];
        }
        return prob;
    }


    // - - - - - - - - - - - - - - - - - - -


    public double ShotProb(){
        double max = 0;
        int maxIdx = 0;
        for (int i = 0; i < info.length; i++){
            if (info[i] > max){
                max = info[i];
                maxIdx = i;
            }
        }
        return max;
    }


    // - - - - - - - - - - - - - - - - - - -


    public int ShootBird(){
        double max = 0;
        int maxIdx = 0;
        for (int i = 0; i < info.length; i++){
            if (info[i] > max){
                max = info[i];
                maxIdx = i;
            }
        }
        return maxIdx;
    }

}
