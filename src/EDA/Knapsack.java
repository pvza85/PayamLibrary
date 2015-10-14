/* by: Payam Azad 26-12-2013
 * solving knapsack problem using Dynmic Programming
 */
package knapsack;

import java.io.File;
import java.util.Scanner;

public class Knapsack
{
    public static int itemNumber, capacity;
    public static int[] Values, Weights;
    public static int[] tValues, tWeights;
    public static byte[] Solution;
    public static int best;
    
    public static void main(String[] args) 
    {
        String[] files = {"ks_4_0", "ks_19_0", "ks_30_0", "ks_40_0", "ks_45_0", "ks_50_0", "ks_50_1", "ks_60_0", "ks_100_0", "ks_100_1", "ks_100_2", "ks_200_0", "ks_200_1", "ks_300_0", "ks_400_0", "ks_500_0", "ks_1000_0", "ks_10000_0"}; 
        String fileName;
        if(args.length > 0)
            fileName =  args[0];
        else
            //for(int i = 0; i < files.length; i++)
            //{
               // System.out.println("#" + i);
            fileName = "data/" + files[17];
        
        read(fileName);

        DynamicProgramming3();

        print();
         //   }

    }
    
    private static void read(String fileName)
    {
        Scanner input;
        
        try
        {
            input = new Scanner(new File(fileName));
            
            itemNumber = input.nextInt();
            capacity = input.nextInt();
            
            Values = new int[itemNumber];
            Weights = new int[itemNumber];
            tValues = new int[itemNumber];
            tWeights = new int[itemNumber];
            Solution = new byte[itemNumber];
            
            for(int i = 0; i < itemNumber; i++)
            {
                tValues[i] = Values[i] = input.nextInt();
                tWeights[i] = Weights[i] = input.nextInt();
            }
        }
        catch(Exception e)
        {
            System.out.println("Problem in input!");
        }
    }
    
    private static void DynamicProgramming2()
    {
        int c = capacity;
        int n = itemNumber;
        
        int[] z = new int[c];
        int[] r = new int[c];
        
        
        while(c > 0)
        {         
            for(int d = 0; d < c; d++)
            {
                z[d] = 0;
                r[d] = 0;
            }
            for(int j = 0; j < n; j++)
                for(int d = c-1; d >= Weights[j]; d--)
                {
                    if(z[d - Weights[j]+1] + Values[j] > z[d])
                    {
                        z[d] = z[d - Weights[j]+1] + Values[j];
                        r[d] = j;
                    }
                }
            
            int t = r[c-1];
            Solution[t] = 1;
            n = r[t] - 1;
            c = c - Weights[t];     
        }
        
        
        
        best = z[capacity-1];     
    }
    
    
    private static void DynamicProgramming1()
    {
        int[] prev = new int[capacity];
        int[] current = new int[capacity];
        
        for(int i = 0; i < itemNumber ; i++)
        {
            int j;
            for(j = 0 ; j < Weights[i]; j++)
                current[j] = prev[j];
            for(; j < capacity; j++)
            {
                if(prev[j-Weights[i]] + Values[i] > prev[j])
                    current[j] = prev[j-Weights[i]] + Values[i];
                else
                    current[j] = prev[j];
            }
            
            for(j = 0; j < capacity; j++)
                prev[j] = current[j];
           
        }
        
        best = current[capacity-1];
    }
    
    private static void DynamicProgramming3()
    {
        
        
        
        int tCapacity = capacity;
        int tNumber = itemNumber;
        
        while(tCapacity > 0)
        {
            int[] prev = new int[tCapacity+1];
            int[] current = new int[tCapacity+1];
            int addedItem = -1;
        
            for(int i = 0; i < tNumber ; i++)
            {
                int j;
                for(j = 0 ; j < tWeights[i] && j <= tCapacity; j++)
                    current[j] = prev[j];
                for(j = tWeights[i]; j <= tCapacity; j++)
                {
                    if(prev[j-tWeights[i]] + tValues[i] > prev[j])
                    {
                        current[j] = prev[j-tWeights[i]] + tValues[i];
                        if(j == tCapacity) addedItem = i;
                    }
                    else
                        current[j] = prev[j];
                }

                for(j = 0; j <= tCapacity; j++)
                    prev[j] = current[j];
                
            }
            
            if(addedItem >= 0)
            {
                Solution[addedItem] = 1;
                tCapacity -= tWeights[addedItem];
                tNumber--;
                remove(addedItem);
            }
            else
                break;
            
        }
        
        //best = current[tCapacity-1];
    }
    
    private static void print()
    {
        System.out.println(evaluate(Solution));
        for(int i = 0 ; i < itemNumber-1; i++)
            System.out.print(Solution[i] + " ");
        System.out.println(Solution[itemNumber-1]);
        
    }
    public static int evaluate(byte[] solution)
    {
        int totalValue = 0;
        int totalWeight = 0;
        
        for(int i = 0; i < solution.length; i++)
        {
            totalValue += solution[i] * Values[i];
            totalWeight += solution[i] * Weights[i];
        }
        
        /*if(totalWeight > capacity)
            return ( capacity - totalWeight) * 10000000 + totalValue;   //definetly wrong*/
        return totalValue;
    }
    
    private static void remove(int index)
    {
        int[] tempV = new int[tValues.length - 1];
        int[] tempW = new int[tValues.length - 1];
        
        int i;
        for(i = 0; i < index; i++)
        {
            tempV[i] = tValues[i];
            tempW[i] = tWeights[i];
        }
        i++;
        for(;i < tValues.length; i++)
        {
            tempV[i-1] = tValues[i];
            tempW[i-1] = tWeights[i];
        }
        
        tValues = tempV;
        tWeights = tempW;
    }
    
}
