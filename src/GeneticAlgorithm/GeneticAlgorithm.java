/********************************************************************
 * Author: Payam Azad  April 2013                                   *
 * This class is an implementation of Generic Genetic Algorithm     *
 * for further uses. It's a generic class to be inherited anywhere  *
 * I need. Just evaluation function needs to be added.              *
 * Developing these series of Algorithm for my MS Thesis            *
 ********************************************************************/
package GeneticAlgorithm;

import java.util.Random;
import java.util.Arrays;

//***this package is based on byte array, I may be able to write a bitwise version
public class GeneticAlgorithm 
{
    //evaluate fitness of each population member,, this function have to be overwritten
    private double evaluate(byte[] solution)
    {
        return 0.0;
    }
    
    //--------------------variables------------------------------------
    private int populationSize;   //size of choromosome numbers
    private int chromosomeLength;   //size of each chormosome
    private double mutuationProbability;
    private double crossOverProbability;
    private int eliteNumber;     //number of elite answers that have to remain
    private int maxGeneration;
    
    //int mode;   //operate in byte mode or bit mode
    
    //Different methods of Selection, Cross Over and Mutation
    //***use enum later
    private int selectionMethod;
    private int crossOverMethod;
    private int mutuationMethod;
    
    //population 
    byte[][] population;
    //int[] bitPopulation;
    double[] fitness;
    byte[][] elites;
    int[] selectedParents;
    
    //process time calculation variables
    long runTime;
    long initializationTime;
    long selectionTime;
    long crossOverTime;
    long mutuationTime;
    long elitismTime;
    long evaluateTime;
    long sortTime;
    long otherTime;
    //---------------------------end of variable-------------------------
    
    //-------------------------constructors------------------------------
    public GeneticAlgorithm(int chromosomeLength)
    {
        this.chromosomeLength = chromosomeLength;
        
        //default values
        this.populationSize = 100;        //***defualt population size I put 20, may change later
        this.mutuationProbability = (double)1/chromosomeLength;  //L*Pm = 1: expecting one mutuation in each chromosome
        this.selectionMethod = 0;       //simplest selection method, choosing best parents
        this.crossOverMethod = 0;       //simplest cross over method, one point cross over from half
        this.crossOverProbability = 1;  //cross over happens in any case (all offspring created sexually)
        this.eliteNumber = 1;           //default number of elite members that have to remain for next generation
        this.maxGeneration = 10000;      //***default maximum number of generations, may change later
        
        //create population
        population = new byte[populationSize][chromosomeLength];
        fitness = new double[populationSize];
        elites = new byte[eliteNumber][chromosomeLength];
    }
    public GeneticAlgorithm(int chromosomeSize, int populationSize)
    {
        this(chromosomeSize);
        this.populationSize = populationSize;
        
        //create population
        population = new byte[populationSize][chromosomeLength];
        fitness = new double[populationSize];
    }
    public GeneticAlgorithm(int chromosomeSize, int populationSize, double mutuationProbability)
    {
        this(chromosomeSize, populationSize);
        this.mutuationProbability = mutuationProbability;
        
        //create population
        population = new byte[populationSize][chromosomeLength];
        fitness = new double[populationSize];
        elites = new byte[eliteNumber][chromosomeLength];
    }
    public GeneticAlgorithm(int chromosomeSize, int populationSize, double mutuationProbability, int selectionMethod, int crossOverMethod)
    {
        this(chromosomeSize, populationSize, mutuationProbability);
        this.selectionMethod = selectionMethod;
        this.crossOverMethod = crossOverMethod;
        
        //create population
        population = new byte[populationSize][chromosomeLength];
        fitness = new double[populationSize];
        elites = new byte[eliteNumber][chromosomeLength];
    }
    public GeneticAlgorithm(int chromosomeSize, int populationSize, double mutuationProbability, int selectionMethod, int crossOverMethod, double crossOverProbability)
    {
        this(chromosomeSize, populationSize, mutuationProbability, selectionMethod, crossOverMethod);
        this.crossOverProbability = crossOverProbability;
        
        //create population
        population = new byte[populationSize][chromosomeLength];
        fitness = new double[populationSize];
        elites = new byte[eliteNumber][chromosomeLength];
    }
    public GeneticAlgorithm(int chromosomeSize, int populationSize, double mutuationProbability, int selectionMethod, int crossOverMethod, double crossOverProbability, int eliteNumber)
    {
        this(chromosomeSize, populationSize, mutuationProbability, selectionMethod, crossOverMethod, crossOverProbability);
        this.eliteNumber = eliteNumber;
        
        //create population
        population = new byte[populationSize][chromosomeLength];
        fitness = new double[populationSize];
        elites = new byte[eliteNumber][chromosomeLength];
    }
    //---------------------end of constructors----------------------------
    
    //main function to run genetic algorithm for default numbers of iteration
    public byte[] run()
    {
        //***max Generation is 1000 for default
        return run(maxGeneration);
    }
    //main function to run genetic algorithm
    public byte[] run(int maxGeneration)
    {
        long startTime, t; //for process time calculation
        
        startTime = System.currentTimeMillis();
        
        t = System.currentTimeMillis();
        initialize();   //initialize first gereneration, sort and select elites
        initializationTime = System.currentTimeMillis() - t;
        
        for(int i = 0; i < maxGeneration; i++)
        {
            t = System.currentTimeMillis();
            selection();
            selectionTime += System.currentTimeMillis() - t;
            
            t = System.currentTimeMillis();
            crossOver();
            crossOverTime += System.currentTimeMillis() - t;
            
            t = System.currentTimeMillis();
            mutuation();
            mutuationTime += System.currentTimeMillis() - t;
            
            t = System.currentTimeMillis();
            elitism();
            elitismTime += System.currentTimeMillis() - t;
            
            if(stoppingCriteria())
                break;
        }
        
        runTime = System.currentTimeMillis() - startTime;
        
        return population[0];  //return the best of population, that is after sorting the first one in population
    }
    public String printResults()
    {
        elitismTime -= (evaluateTime + sortTime);
        
        String res = "Best Result with fitness of " + fitness[1] + "\n created in: "
        + maxGeneration + " Generations and "
        + (double)runTime/1000 + " seconds, \n In details: \n"
        + "   Initialization: " + (double)initializationTime/1000 + " seconds (" + (100 * initializationTime / runTime) + "%) \n"
        + "   Selection: " + (double)selectionTime/1000 + " seconds (" + (100 * selectionTime / runTime) + "%) \n"
        + "   Cross Over: " + (double)crossOverTime/1000 + " seconds (" + (100 * crossOverTime / runTime) + "%) \n"
        + "   Mutuation: " + (double)mutuationTime/1000 + " seconds (" + (100 * mutuationTime / runTime) + "%) \n"
        + "   Evaluation: " + (double)evaluateTime/1000 + " seconds (" + (100 * evaluateTime / runTime) + "%) \n"
        + "   Sort: " + (double)sortTime/1000 + " seconds (" + (100 * sortTime / runTime) + "%) \n"
        + "   Elitism: " + (double)elitismTime/1000 + " seconds (" + (100 * elitismTime / runTime) + "%) \n";

        return res;
    }
    //initilize first population and sort them and select 
    private void initialize()
    { 
        Random rand = new Random(System.currentTimeMillis());  //*** enhance it by faster better random creation methods 
        
        //create random solutions
        for(int i = 0; i < populationSize; i++)  //*** enhance it by checking repeated solutions
        {
            for(int j = 0; j < chromosomeLength; j++)
                population[i][j] = (byte)rand.nextInt(2);  //*** make it faster by random creation methods
            
            //finding fitness of each random solution
            fitness[i] = evaluate(population[i]);
        }
        
        sort();            //sort popultation members for easier selection and elitism
        selectElites();    //select the elite members and keep them for next generation
    }
    private void selection()
    {
        switch(selectionMethod)
        {
            case 0:
                selectBest();
                break;
            default:
                selectBest();
                break;
        }
    }
    private void crossOver()
    {
        switch(crossOverMethod)
        {
            case 0:
                simpleOnePointCrossOver();
                break;
            default:
                simpleOnePointCrossOver();
                break;              
        }
    }
    private void mutuation()
    {
        //*** too much random needs to be created
        Random rand = new Random(System.currentTimeMillis());
        for(int i = 0; i < populationSize; i++)
            for(int j = 0; j < chromosomeLength; j++)
            {
                double t = rand.nextDouble();
                if(t < mutuationProbability)
                    population[i][j] = (byte)((byte)(population[i][j] + 1) % 2);
            }
    }
    private void elitism()
    {
        insertElites();
        
        double t = System.currentTimeMillis();
        evaluateAll();
        evaluateTime += System.currentTimeMillis() - t;
        
        t = System.currentTimeMillis();
        sort();
        sortTime += System.currentTimeMillis() - t;
        
        selectElites();
    }
    //*** later enhance 
    private boolean stoppingCriteria()
    {
        return false;
    }
    
    //evaluate all current population fitness
    private void evaluateAll()
    {
        for(int i = 0; i < populationSize; i++) 
            fitness[i] = evaluate(population[i]);
    }
    //sort population Descendary
    private void sort()
    {
        double[] tempFitness = new double[populationSize];
        System.arraycopy( fitness, 0, tempFitness, 0, populationSize );
        byte[][] tempPopulation = new byte[populationSize][chromosomeLength];
        //System.arraycopy( population, 0, tempPopulation, 0, population.length );
        
        Arrays.sort(tempFitness);
        
        //place population in order in tempPopulation
        for(int i = populationSize-1;  i >= 0; i--)
            for(int j = 0; j < populationSize; j++)
                if(tempFitness[i] == fitness[j])
                {
                    fitness[j] = -1;
                    System.arraycopy(population[j], 0, tempPopulation[populationSize - i - 1], 0, chromosomeLength);
                    break;
                }
        
        population = tempPopulation;
        evaluateAll();
    }
    //select select "eliteNum" best members of population
    private void selectElites()
    {
        elites = new byte[eliteNumber][chromosomeLength];
        System.arraycopy(population, 0, elites, 0, eliteNumber);
    }
    //insert "eliteNum" of previous generations best members instead of worst members of current population
    private void insertElites()
    {
        /*for(int i = 0; i < eliteNumber; i++)
            population[populationSize - eliteNumber + i] = elites[i];  */
        System.arraycopy(elites, 0, population, populationSize - eliteNumber, eliteNumber);  
    }
    //selection function that call different selection methods
    //simplest, naive selection method, it select the best parents
    private void selectBest()
    {
        selectedParents = new int[populationSize];
        //*** check for odd population
        for(int i = 0, j = populationSize/2-1; i < populationSize/2; i++, j--)
        {
            selectedParents[i*2] = i;
            selectedParents[i*2+1] = j;
        }
    } 
    //simplest cross over possible, one point from middle of each answer
    private void simpleOnePointCrossOver()
    {
        byte[][] offSprings = new byte[populationSize][chromosomeLength];
        //****check of odd chromosomeLength
        for(int i = 0; i < populationSize; i = i+2)
        {
            int j;
            for(j = 0; j < chromosomeLength/2; j++)
            {
                offSprings[i][j] = population[selectedParents[i]][j];
                offSprings[i+1][j] = population[selectedParents[i+1]][j];
            }
            for(;j < chromosomeLength; j++)
            {
                offSprings[i][j] = population[selectedParents[i+1]][j];
                offSprings[i+1][j] = population[selectedParents[i]][j];
            }
        }
    }   
    //return n best members of fitness array
    private int[] nMax(int n)
    {
        //http://en.wikipedia.org/wiki/Selection_algorithm
        int[] res = new int[n];
        
        return res;
    }
    //**** just one max  ****maybe temporary
    private int max()
    {
        int index = 0;
        for(int i = 1; i < populationSize; i++)
            if(fitness[i] > fitness[index])
                index = i;
        
        return index;
    }
    //**** just one min  ****maybe temporary
    private int min()
    {
        int index = 0;
        for(int i = 1; i < populationSize; i++)
            if(fitness[i] < fitness[index])
                index = i;
        
        return index;
    }   
} 
