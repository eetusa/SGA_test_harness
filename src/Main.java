import java.io.*;
import java.time.Instant;
import java.util.ArrayList;

public class Main {

    public static void main ( String... args ) throws CloneNotSupportedException, IOException {
        //runTests();
        runTests_parallel();
    }

    // Samat testit, mutta pyrkii hyödyntämään rinnakkaislaskentaa.
    // Metodi kirjoittaa tekstitiedostoon tulokset teksteistä.
    public static void runTests_parallel() throws CloneNotSupportedException, IOException {

        // -- Parametrit --
        final double[] mutation_probs = {0.0005};
        final double[] crossover_probs = {0.9};
        final boolean[] is_crossover_point_random = {true};
        final int[] population_sizes = {10};
        int chromosome_length = 50; // kasvaa grow_pacen mukaan chromosome_treshholdiin asti

        final int amount_of_runs = 10; // kuinka monta kierrosta kutakin testiä ajetaan
        final int grow_pace = 50;
        final int chromosome_treshhold = 1001;
        //-- -- -- -- -- --
        ArrayList<SGA_driver_parallel> saved_runs = new ArrayList<>();


        String goal = "";
        for ( int i = 0; i < chromosome_length; i++ ) {
            if ( Math.random() < 0.5 )
                goal += "0";
            else
                goal += "1";
        }
        Writer output = null;
        try {
            long now = Instant.now().toEpochMilli();
            String filename = String.format("output_%d.txt", now);
            File f;
            f = new File(filename);
            f.createNewFile();
            output = new BufferedWriter(new FileWriter(filename, true));
            output.append("MUTATION_PROB;CROSSOVER_PROB;POPULATION_SIZE;IS_CROSSOVER_POINT_RANDOM;CHROMOSOME_LENGTH;SR;AVG_END_FIT;AVG_SOLUTION_FOUND\n");
            System.out.println("MUTATION_PROB;CROSSOVER_PROB;POPULATION_SIZE;IS_CROSSOVER_POINT_RANDOM;CHROMOSOME_LENGTH;SR;AVG_END_FIT;AVG_SOLUTION_FOUND");

            // Muodostetaan eri testit rinnakkaisajettavaksi
            while (chromosome_length < chromosome_treshhold){
                for (double m_p : mutation_probs) {
                    for (double c_p : crossover_probs){
                        for (int pop_size : population_sizes){
                            for (boolean i_cpm : is_crossover_point_random){
                                SGA_driver_parallel runner = new SGA_driver_parallel(m_p, c_p, i_cpm, pop_size, chromosome_length, goal, amount_of_runs, 300000);
                                saved_runs.add(runner);
                            }
                        }
                    }
                }
                goal = "";
                chromosome_length += grow_pace;
                for ( int i = 0; i < chromosome_length; i++ ) {
                    if ( Math.random() < 0.5 )
                        goal += "0";
                    else
                        goal += "1";
                }
            }

            // Ajetaan muodostetut testit rinnakkain sekä kirjoitetaan tuloksia bufferiin
            Writer finalOutput = output;
            saved_runs.parallelStream().forEach(runner -> {
                try {
                    String result = runner.run();
                    System.out.print(result);
                    finalOutput.append(result);

                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            finalOutput.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            output.close();
        }
    }

    // Pitkälti sama, kuin ylempi metodi, mutta suorittaa testit peräkkäisesti
    public static void runTests() throws CloneNotSupportedException, IOException {
       // final double[] mutation_probs = {0.01, 0.02, 0.03, 0.04, 0.05, 0.06, 0.07};
        final double[] mutation_probs = {0.0005};
        // final double[] crossover_probs = {0.7, 0.8, 0.9};
        final double[] crossover_probs = {0.6};
        final boolean[] is_crossover_point_random = {true};
        final int[] population_sizes = {50};
        final int amount_of_runs = 5;
        final int grow_pace = 10;

       ArrayList<SGA_driver> saved_runs = new ArrayList<>();


        final int chromosome_treshhold = 121;
        int chromosome_length = 100; // grows
        String goal = "";
        for ( int i = 0; i < chromosome_length; i++ ) {
            if ( Math.random() < 0.5 )
                goal += "0";
            else
                goal += "1";
        }
        Writer output = null;
        try {
            long now = Instant.now().toEpochMilli();
            String filename = String.format("output_%d.txt", now);
            File f;
            f = new File(filename);
            f.createNewFile();
            output = new BufferedWriter(new FileWriter(filename, true));
            output.append("MUTATION_PROB;CROSSOVER_PROB;POPULATION_SIZE;IS_CROSSOVER_POINT_RANDOM;CHROMOSOME_LENGTH;SR;AVG_END_FIT;AVG_SOLUTION_FOUND\n");
            System.out.println("MUTATION_PROB;CROSSOVER_PROB;POPULATION_SIZE;IS_CROSSOVER_POINT_RANDOM;CHROMOSOME_LENGTH;SR;AVG_END_FIT;AVG_SOLUTION_FOUND");

            while (chromosome_length < chromosome_treshhold){
                for (double m_p : mutation_probs) {
                    for (double c_p : crossover_probs){
                        for (int pop_size : population_sizes){
                            for (boolean i_cpm : is_crossover_point_random){
                                SGA_driver runner = new SGA_driver(m_p, c_p, i_cpm, pop_size, chromosome_length, goal, amount_of_runs, 300000);
                                saved_runs.add(runner);
                                String result = runner.run();

                                System.out.print(result);
                                output.append(result);
                            }
                        }
                    }
                }
                goal = "";
                chromosome_length += grow_pace;
                for ( int i = 0; i < chromosome_length; i++ ) {
                    if ( Math.random() < 0.5 )
                        goal += "0";
                    else
                        goal += "1";
                }
                output.close();
                output = new BufferedWriter(new FileWriter(filename, true));

            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            output.close();
        }


    }

}
