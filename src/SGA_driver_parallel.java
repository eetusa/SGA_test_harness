public class SGA_driver_parallel {

    public double MUTATION_PROB;
    public double CROSSOVER_PROB;
    public boolean IS_CROSSOVER_POINT_RANDOM = false;
    public int POPULATION_SIZE;
    public int CHROMOSOME_LENGTH;
    public int AMOUNT_OF_RUNS;
    public int NEW_BEST_RESILIANCE;
    public String GOAL;

    int rounds = 0;
    int bestOverall = 0;
    int rounds_since_last_new_best = 0;
    boolean end = false;
    SGA_parallel sga;

    int[] ROUND_SOLUTION_FOUND;
    int[] FITNESS_AT_END;
    double SR;
    double AVG_END_FIT;
    double AVG_SOLUTION_FOUND;
    String saved_result;

    public SGA_driver_parallel(double m_p, double c_p, boolean i_cpm, int pop_size, int chrom_len, String goal, int amount_of_runs, int nbr){
        MUTATION_PROB = m_p;
        CROSSOVER_PROB = c_p;
        IS_CROSSOVER_POINT_RANDOM = i_cpm;
        POPULATION_SIZE = pop_size;
        CHROMOSOME_LENGTH = chrom_len;
        AMOUNT_OF_RUNS = amount_of_runs;
        GOAL = goal;
        NEW_BEST_RESILIANCE = nbr;
        ROUND_SOLUTION_FOUND = new int[AMOUNT_OF_RUNS];
        FITNESS_AT_END = new int[AMOUNT_OF_RUNS];


    }

    public void run_once(int run_nro) throws CloneNotSupportedException {
        ROUND_SOLUTION_FOUND[run_nro] = -1;
        FITNESS_AT_END[run_nro] = 0;
        rounds = 0;
        rounds_since_last_new_best = 0;
        bestOverall = 0;
        end = false;

        sga = new SGA_parallel(POPULATION_SIZE, GOAL);
        sga.setSettings(MUTATION_PROB, CROSSOVER_PROB, IS_CROSSOVER_POINT_RANDOM);

        while ( !end ) {
            rounds++;
            int[] parents = sga.crossover();
            if ( !sga.hasSolution()) {
                sga.mutation ( parents[0], true );
                if ( !sga.hasSolution()) {
                    sga.mutation ( parents[1], true );
                    if ( !sga.hasSolution()) {
                        sga.killTheStupid(1);
                    }
                    else {
                        //   System.out.println("Solution found after mutation 2");
                    }
                }
                else {
                    // System.out.println("Solution found after mutation 1");
                }
            }
            else {
                // System.out.println("Solution found after crossover");
            }

            int bestFound = sga.getBestFit();
            rounds_since_last_new_best++;
            if ( bestFound > bestOverall ) {
                bestOverall = bestFound;
                rounds_since_last_new_best = 0;
                //   System.out.printf ("%4d %3d\n", rounds, bestOverall );
            }


            //System.out.println ( sga.toString() + rounds++ );
            if ( sga.hasSolution()) {
                ROUND_SOLUTION_FOUND[run_nro] = rounds;
                end = true;
                FITNESS_AT_END[run_nro] = bestOverall;
                //     System.out.println ("Solution found!");
                //    sga.printSolution();
            }
            if (rounds_since_last_new_best > NEW_BEST_RESILIANCE){
             //   System.out.println("No solution found, round: "+rounds);
                FITNESS_AT_END[run_nro] = bestOverall;
                end = true;
            }
        }
    }

    public String run() throws CloneNotSupportedException {
        // System.out.printf("Running m_p: %f, c_p: %f, pop_size: %d, i_cpm: %B, chrom_len: %d\n", MUTATION_PROB, CROSSOVER_PROB, POPULATION_SIZE, IS_CROSSOVER_POINT_RANDOM, CHROMOSOME_LENGTH);
        for (int i = 0; i < AMOUNT_OF_RUNS; i++) {
            run_once(i);
        }
        calculate_end_values();
        String result = String.format("%f;%f;%d;%B;%d;%f;%f;%f\n", MUTATION_PROB, CROSSOVER_PROB, POPULATION_SIZE, IS_CROSSOVER_POINT_RANDOM, CHROMOSOME_LENGTH, SR, AVG_END_FIT, AVG_SOLUTION_FOUND);
        saved_result = result;
        return result;
    }

    private void calculate_end_values() {
        int sr_temp = 0;
        int solution_round_temp = 0;
        for (int round : ROUND_SOLUTION_FOUND){
            if (round != -1) {
                sr_temp++;
                solution_round_temp += round;
            }
        }

        int fit_temp = 0;
        for (int end_fit : FITNESS_AT_END){
            fit_temp += end_fit;
        }

        AVG_SOLUTION_FOUND = (double) solution_round_temp / AMOUNT_OF_RUNS;
        SR = (double) sr_temp / AMOUNT_OF_RUNS;
        AVG_END_FIT = (double) fit_temp / AMOUNT_OF_RUNS;

        //  System.out.printf("%f;%f;%d;%B;%d;%f;%f;%f\n", MUTATION_PROB, CROSSOVER_PROB, POPULATION_SIZE, IS_CROSSOVER_POINT_RANDOM, CHROMOSOME_LENGTH, SR, AVG_END_FIT, AVG_SOLUTION_FOUND);
        //  System.out.println("Success rate: " + SR + ", Avg fitness at end: " + AVG_END_FIT + ", Avg. solution found in round: " + AVG_SOLUTION_FOUND);
    }

}
