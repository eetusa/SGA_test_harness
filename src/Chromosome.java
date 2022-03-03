class Chromosome implements Cloneable, Comparable<Chromosome> {
    private String goal;  // Easier to check if inside the class.
    private String chromo;
    private int fitness;
    public Chromosome ( String g, int len ) {
        goal = g;

        // Random chromosome.
        chromo = "";
        for ( int i = 0; i < len; i++ ) {
            if ( Math.random() < 0.5 )
                chromo += "0";
            else
                chromo += "1";
        }
        // This could be a problem if it is public and this class is extended
        // and the fitness would be calculated in some other way.
        setFitness ();
    }
    private void setFitness () {
        int fit = 0;
        for ( int i = 0; i < goal.length(); i++ ) {
            if ( goal.charAt(i) == chromo.charAt(i))
                fit++;
        }
        fitness = fit;
    }
    public int getFitness () {
        return fitness;
    }
    public boolean isPerfectFitness () {
        return ( fitness == chromo.length());
    }
    public Chromosome ( String c ) {
        chromo = c;
        setFitness();
    }
    @Override
    public Object clone () throws CloneNotSupportedException {
        Chromosome temp = (Chromosome)super.clone();
        return temp;
    }
    public String getChromo () {
        return chromo;
    }
    @Override
    public String toString () {
        return chromo;
    }
    public String toString ( int start ) {
        return chromo.substring ( start );
    }
    public void replace ( int start, String genes ) {
        chromo = chromo.substring ( 0, start );
        chromo += genes;
        setFitness();
    }

    @Override
    public int compareTo ( Chromosome c ) {
        return c.fitness - this.fitness;
    }
}
