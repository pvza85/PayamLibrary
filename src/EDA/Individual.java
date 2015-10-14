/* Author: Payam Azad   May 2013 
 */
package EDA;

import java.util.Random;

public class Individual 
{
    int chromosomeLength;
    
    public double fitness;
    public byte[] chromosome;
    public int value;
    
    public Individual(int chromosomeLenght)
    {
        this.chromosomeLength = chromosomeLength;
        chromosome = new byte[chromosomeLenght];
    }
    
    public void random(Random rand)
    {
        for(int i = 0; i < chromosomeLength; i++)
            chromosome[i] = (byte) rand.nextInt(2);
    }
    
    public int toInt()
    {
        int res = 0;
        
        for(int i = chromosomeLength-1, j = 0; i >= 0; i--, j++)
            res += Math.pow(j, chromosome[i]);
        
        return res;
    }
    
    public void evaluate()
    {
        fitness = 0.0;
    }
    
    public String print()
    {
        String res = "";
        
        res += "fitness = " + fitness + "    ";;
        for(int i = 0; i < chromosomeLength; i++)
            res += chromosome[i];
        res += "\n";
        
        return res;
    }
}

