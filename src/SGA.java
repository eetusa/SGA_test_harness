import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class SGA {

    // Crossover and mutation rates should also be parameters...
    public double MUTATION_PROB = 0.05;
    public double CROSSOVER_PROB = 0.8;
    public boolean IS_CROSSOVER_POINT_RANDOM = false;
    public final int POPULATION_SIZE;
    public final int CHROMOSOME_LENGTH;
    private final Chromosome[] pop;
    private final Random gen = new Random();

    // The goal of Chromosome delivered as a parameter.
    public SGA(int popSize, String goal ) {
        POPULATION_SIZE = popSize;
        CHROMOSOME_LENGTH = goal.length();
        pop = new Chromosome[POPULATION_SIZE];
        for ( int i = 0; i < POPULATION_SIZE; i++ )
            pop[i] = new Chromosome ( goal, CHROMOSOME_LENGTH );
    }

    /**
     * Setups SGA parameters
     * @param m_p mutation probability per bit
     * @param c_p crossover probability
     * @param is_cpr is crossover point random
     */
    public void setSettings(double m_p, double c_p, boolean is_cpr){
        MUTATION_PROB = m_p;
        CROSSOVER_PROB = c_p;
        IS_CROSSOVER_POINT_RANDOM = is_cpr;
    }

    public void setMUTATION_PROB(double p){
        MUTATION_PROB = p;
    }

    public void setCROSSOVER_PROB(double p){
        CROSSOVER_PROB = p;
    }

    public void setIS_CROSSOVER_POINT_RANDOM(boolean b){
        IS_CROSSOVER_POINT_RANDOM = b;
    }
    @Override
    /**
     * Returning the whole population as a string.
     */
    public String toString () {
        String retVal = "";
        for ( int i = 0; i < POPULATION_SIZE; i++ )
            retVal += pop[i].toString() + "\n";
        return retVal;
    }
    public String toString ( int popMember ) {
        return pop[popMember].toString();
    }
    public boolean hasSolution () {
        for ( int i = 0; i < pop.length; i++ ) {
            if ( pop[i].isPerfectFitness())
                return true;
        }
        return false;
    }

    /**
     * Crossover is done once every time. This is a very good place to fine tune your algorithm.
     *
     * You could do this more than once or you could prevent it from
     * happening now and then. The literature says 70-90 % of the
     * times you should do it.
     *
     * If you are doing crossover more than once you could prevent/allow
     * a single chromosome do be part of crossover exactly one time or many
     * times.
     *
     * I have coded this in a friendly manner so the code should be clear for everyone.
     *
     * @return the index of the parents (performing mutation to the parents).
     */
    public int[] crossover () {

        // Taking the distribution into account in a very simple (and stupid?) way.
        ArrayList<Integer> distribution = new ArrayList();
        for ( int i = 0; i < POPULATION_SIZE; i++ ) {
            int fit = pop[i].getFitness();
            // Must accept zero fitness - otherwise eternal loop can happen.
            // Adding the value fit times into the distribution-array.
            // The more fit the chromosome is, the more probable it is for it to be a parent.
            for ( int j = 0; j <= fit; j++ )
                distribution.add ( i );
        }

        int parent1 = distribution.get ( gen.nextInt( distribution.size()));
        int parent2;
        do {
            parent2 = distribution.get ( gen.nextInt( distribution.size()));
            //System.out.println ("Eternal");
        } while ( parent1 == parent2 );
        // Choosing the crossover point by random.
        if (Math.random() < CROSSOVER_PROB) {
            int crossover = (CHROMOSOME_LENGTH)/2;
            if (IS_CROSSOVER_POINT_RANDOM){
                crossover = gen.nextInt(CHROMOSOME_LENGTH - 1) + 1;
            }

            String tail1 = pop[parent1].toString(crossover);
            //System.out.println("Tail len: " + tail1.length() + " chrom len: " + CHROMOSOME_LENGTH);
            String tail2 = pop[parent2].toString(crossover);
            pop[parent1].replace(crossover, tail2);
            pop[parent2].replace(crossover, tail1);
        }
        return new int[] {parent1, parent2};


    }

    /**
     * Doing only full mutation, i.e. going through the whole chromosome.
     * Could also just mutate a single bit every time or now and then.
     * @param popMember
     * @param full
     */
    public void mutation ( int popMember, boolean full ) {
        if ( full ) {
            for ( int i = 0; i < CHROMOSOME_LENGTH; i++ ) {
                if ( Math.random() < MUTATION_PROB ) {
                    char c = pop[popMember].getChromo().charAt ( i );
                    if ( c == '0' ) c = '1';
                    else c = '0';
                    String temp = pop[popMember].getChromo().substring ( 0, i );
                    temp += c;
                    temp += pop[popMember].getChromo().substring ( i+1 );
                    pop[popMember].replace ( 0, temp );
                }
            }
        }
    }

    /**
     * Replacing n worst with n best.
     * @param n
     * @throws CloneNotSupportedException
     */
    public void killTheStupid ( int n ) throws CloneNotSupportedException {
        Arrays.sort ( pop );
        for ( int i = 0; i < n; i++ )
            pop[POPULATION_SIZE-1-i] = (Chromosome)pop[i].clone();
    }

    public int getBestFit () {
        int retVal = 0;
        for ( int i = 0; i < pop.length; i++ ) {
            int fit = pop[i].getFitness();
            if (  fit > retVal )
                retVal = fit;
        }
        return retVal;
    }

    public void printSolution () {
        for ( int i = 0; i < pop.length; i++ ) {
            if ( pop[i].isPerfectFitness())
                System.out.printf ("%3d %s\n", i, pop[i] );
        }
    }

}
