/*Author: Payam Azad   May 2013
 * It is simplest form of EDAs 
 */
package EDA;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

public class SimpleEDA 
{
    private int chromosomeLength; 
    private int populationSize;
    private int maxGeneration;
    private int eliteNumber;
    
    Random rand;
    
    private Individual[] population;
    private double[] probabilities;
    
    enum Type{minimize, maximize};
    Type type;
    
    public SimpleEDA(int chromosomeLength, int populationSize, Type type, int eliteNumber)
    {
        this.chromosomeLength = chromosomeLength;
        this.populationSize = populationSize;
        this.type = type;
        this.eliteNumber = eliteNumber;
    }
    public SimpleEDA(int chromosomeLength, int populationSize, Type type)
    {
        this(chromosomeLength, populationSize, type, 1);
    }
    public SimpleEDA(int chromosomeLength, int populationSize)
    {
        //default Type = maximize
        this(chromosomeLength, populationSize, Type.maximize);
    }
    public SimpleEDA(int chromosomeLength)
    {
        //default Type = maximize     populationSize = 100
        this(chromosomeLength, 100, Type.maximize);
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
        probabilities = new double[populationSize];
        rand = new Random(System.currentTimeMillis());
        
        population = new Individual[populationSize];
        for(int i = 0; i < populationSize; i++)
        {
            population[i] = new Individual(chromosomeLength);
            population[i].random(rand);
        }
        
        evaluateAll();
        sort();
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

                        if(type == Type.minimize)
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
        probabilities = new double[chromosomeLength];
        
        for(int i = 0; i < chromosomeLength; i++)
        {
            int count = 0;
            for(int j = 0; j < populationSize / 2; j++)
                count += population[j].chromosome[i];
            probabilities[i] =(double) count / populationSize * 2;
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


