import java.io.*;
import java.util.*;
public class AlgorithmsPhase1
{
    public static void main(String [] args)
    {
      Scanner key = new Scanner(System.in);
      String name[] = new String[3]; //stores asset's name
      double eReturn[] = new double[3]; //stores asset's expected return
      double riskLevel[] = new double[3]; //stores asset's risk level
      int quantity[] = new int[3]; //stores asset's maximum quantity
      
      try (BufferedReader br = new BufferedReader(new FileReader("Profile1.txt"))) //reads the text file containing the profile
      {
        String line;
        int i = 0;
        while((line = br.readLine()) != null) 
        {
          String[] parts = line.split(" : "); //per iteration, parts will store each asset's information as separate parts; parts[0] is the name, parts[1] is the expected return...
          name[i] = parts[0];
          eReturn[i] = Double.parseDouble(parts[1]);
          riskLevel[i] = Double.parseDouble(parts[2]);
          quantity[i] = Integer.parseInt(parts[3]);
          i++;
        } 
      }
      catch (IOException e) 
      {
        e.printStackTrace();
      }
      
      System.out.print("Enter tolerance level (0.010-0.050): "); //user decides on the tolerance level
      double tolerance = key.nextDouble();  
      Allocation best = new Allocation(); //when the algorithm terminates, will contain the best asset unit allocation
      
      bruteForce(eReturn, riskLevel, quantity, tolerance, best); //calls brute-force algorithm
    
      System.out.println("Optimal Allocation: ");
      System.out.println(name[0] + ": " + best.units1 + " units");    
      System.out.println(name[1] + ": " + best.units2 + " units");    
      System.out.println(name[2] + ": " + best.units3 + " units"); 
      System.out.printf("   Expected Portfolio Return: %.3f\n", best.aReturn);   
      System.out.printf("Portfolio Risk Level: %.3f\n", best.risk);   
    }
    
    
    public static void bruteForce(double[] eReturn, double[] riskLevel, int[] quantity, double tolerance, Allocation best)
    {
      int totalQuantity; //total number of units allocated
      double maxReturn = 0, portfolioReturn, portfolioRisk, weights[] = new double[3]; //weights is the weight of each asset in a given allocation
      
      for(int i=1; i<=quantity[0]; i++)
      {
        for(int j=1; j<=quantity[1]; j++)
        {
          for(int k=1; k<=quantity[2]; k++)
          {
             totalQuantity = i+j+k;
             weights[0] = (i/(totalQuantity*1.0));
             weights[1] = (j/(totalQuantity*1.0));
             weights[2] = (k/(totalQuantity*1.0));
             
             portfolioReturn = weights[0]*eReturn[0] + weights[1]*eReturn[1] + weights[2]*eReturn[2]; 
             portfolioRisk = Math.sqrt(Variance(riskLevel, weights)); //the portfolio's risk is the standard deviation of the portfolio
             
             if(portfolioRisk <= tolerance)
             {
               if(portfolioReturn > maxReturn) //if a given allocation is within the tolerance level and has a greater return than a previously found return
               {
                 maxReturn = portfolioReturn; //maximum possible return is updated
                 best.setAllocation(i, j, k, portfolioReturn, portfolioRisk); //best allocation is updated
               }
             }
           }
         }
       }  
      
    }
    
    
    public static double Variance(double[] riskLevels, double[] weights) //calculates portfolio's variance
    {
      double variance = 0; 
      for(int i=0; i<3; i++) 
        for(int j=0; j<3; j++) 
          variance += weights[i] * weights[j] * riskLevels[i] * riskLevels[j];
      return variance;
    }
} 
    

