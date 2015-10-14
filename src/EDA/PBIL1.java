/* Author: Payam Azad    May 2013
 * It's an implementation of Population Based Incremental Learning Algorithm (PBIL1)
 * offered by S. Baluja at 1994
 */
package EDA;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

public class PBIL1 
{
    private int chromosomeLength; 
    private int populationSize;
    private int maxGeneration;
    private int eliteNumber;
    
    public double alpha;   //alpha variable in PBIL1
    
    Random rand;
    
    private Individual[] population;
    private double[] probabilities;
    
    enum Type{minimize, maximize};
    SimpleEDA.Type type;
    
    public PBIL1(int chromosomeLength, int populationSize, SimpleEDA.Type type, int eliteNumber)
    {
        this.chromosomeLength = chromosomeLength;
        this.populationSize = populationSize;
        this.type = type;
        this.eliteNumber = eliteNumber;
        this.alpha = 0.7;
    }
    public PBIL1(int chromosomeLength, int populationSize, SimpleEDA.Type type)
    {
        this(chromosomeLength, populationSize, type, 1);
    }
    public PBIL1(int chromosomeLength, int populationSize)
    {
        //default Type = maximize
        this(chromosomeLength, populationSize, SimpleEDA.Type.maximize);
    }
    public PBIL1(int chromosomeLength)
    {
        //default Type = maximize     populationSize = 100
        this(chromosomeLength, 100, SimpleEDA.Type.maximize);
    }
    
    public Individual run()
    {
        return run(100);
    }
    
    public Individual run(int maxGeneration)
    {
        this.maxGeneration = maxGeneration;
        initialize();
        
        for(int i = 0; i < maxGeneration; i++)
        {
            findProbabilities();
            createNewGeneration();
            evaluateAll();
            sort();
            if(stoppingCriteria())
                break;
        }
        return population[0];
    }
    
    private void initialize()
    {
        probabilities = new double[chromosomeLength];
        rand = new Random(System.currentTimeMillis());
        
        population = new Individual[populationSize];
        for(int i = 0; i < populationSize; i++)
        {
            population[i] = new Individual(chromosomeLength);
            population[i].random(rand);
        }
        
        evaluateAll();
        sort();
        
        for(int i = 0; i < chromosomeLength; i++)
        {
            int count = 0;
            for(int j = 0; j < populationSize / 2; j++)
                count += population[j].chromosome[i];
            
            probabilities[i] = (double) count / populationSize * 2;
        }
    }
    
    private void sort()
    {
        //create an object of: a class that impelements Comparator<Individuals>
        Comparator<Individual> myComparator = new Comparator<Individual>()
                { 
                    @Override
                    public int compare(Individual i1, Individual i2)
                    {
                        int res = -1;
                        if(i1.fitness < i2.fitness) 
                            res = 1;
                        if(i1.fitness == i2.fitness)
                            res = 0;

                        if(type == SimpleEDA.Type.minimize)
                        {
                            res *= -1;
                        }

                        return res;
                    }
                };
        Arrays.sort(population, myComparator);
    }
    
    private void evaluateAll()
    {
        for(int i = 0; i < populationSize; i++)
            population[i].evaluate();
    }
    
    private boolean stoppingCriteria()
    {
        return false;
    }
    private void findProbabilities()
    {
        
        for(int i = 0; i < chromosomeLength; i++)
        {
            int count = 0;
            for(int j = 0; j < populationSize / 2; j++)
                count += population[j].chromosome[i];
            
            double current = alpha * ((double) count / populationSize * 2);
            double previous = (double) ((double)1.0-alpha) * (double)probabilities[i];
            probabilities[i] = current + previous;
        }
        
    }
    private void createNewGeneration()
    {       
        //from eliteNumber because I want to preserve previous generations best memeber
        for(int i = eliteNumber; i < populationSize; i++)
            for(int j = 0; j < chromosomeLength; j++)
            {
                double r = rand.nextDouble();
                if(r < probabilities[j])
                    population[i].chromosome[j] = 1;
                else
                    population[i].chromosome[j] = 0;
            }
    }
    
    public String printResults()
    {
        String res = "Best Result is: \n" + population[0].print();
        
        return res;
    }
}
