import java.io.*;
import java.util.*;

public class AlgorithmsPhase1
{
    static class Allocation //object that will hold a given allocation
    {
      int units1, units2, units3; //each represents the amount of units allocation for each asset 
      double aReturn, risk; //represent the return and risk of each unit allocation
   
      public void setAllocation(int units1, int units2, int units3, double aReturn, double risk)
      { 
        this.units1 = units1;
        this.units2 = units2;
        this.units3 = units3;
        this.aReturn = aReturn;
        this.risk = risk;
      }
   
    }
    
    public static void main(String [] args)
    {
      Scanner key = new Scanner(System.in);
      String name[] = new String[3]; //stores asset's name
      double eReturn[] = new double[3]; //stores asset's expected return
      double riskLevel[] = new double[3]; //stores asset's risk level
      int quantity[] = new int[3]; //stores asset's maximum quantity
      double tolerance = 0;
      int investment = 0;
              
      try (BufferedReader br = new BufferedReader(new FileReader("Profile2.txt"))) //reads the text file containing the profile
      {
        String line;
        int i = 0;
        while((line = br.readLine()) != null) 
        {
          String[] parts = line.split(" : "); //per iteration, parts will store each asset's information as separate parts; parts[0] is the name, parts[1] is the expected return...
          if(parts.length == 4) 
          { 
              name[i] = parts[0];
              eReturn[i] = Double.parseDouble(parts[1]);
              riskLevel[i] = Double.parseDouble(parts[2]);
              quantity[i]= Integer.parseInt(parts[3]);
              i++;
          } 
          else if(i > 2) 
          { // Total investment and risk tolerance
             if(line.startsWith("Total investment:")) 
                investment = Integer.parseInt(line.split(":")[1].trim());
             else if(line.startsWith("Risk tolerance level:")) 
                 tolerance = Double.parseDouble(line.split(":")[1].trim());
          }
        } 
      }
      catch (IOException e) 
      {
        e.printStackTrace();
      }
      
      Allocation best = new Allocation(); //when the algorithm terminates, will contain the best asset unit allocation
      
      bruteForce(eReturn, riskLevel, quantity, tolerance, investment, best); //calls brute-force algorithm

      System.out.println("Optimal Allocation: ");
      System.out.println(name[0] + ": " + best.units1 + " units");    
      System.out.println(name[1] + ": " + best.units2 + " units");    
      System.out.println(name[2] + ": " + best.units3 + " units"); 
      System.out.printf("   Expected Portfolio Return: %.3f\n", best.aReturn);   
      System.out.printf("Portfolio Risk Level: %.3f\n", best.risk);   
    }
    
    
    public static void bruteForce(double[] eReturn, double[] riskLevel, int[] quantity, double tolerance, int investment, Allocation best)
    {
      int totalQuantity; //total number of units allocated
      double maxReturn = 0, portfolioReturn, portfolioRisk, weights[] = new double[3]; //weights is the weight of each asset in a given allocation
      
      for(int i=0; i<=quantity[0]; i++)
      {
        for(int j=0; j<=quantity[1]; j++)
        {
          for(int k=0; k<=quantity[2]; k++)
          {
             totalQuantity = i+j+k;
             if(totalQuantity > investment)
                 continue;

             weights[0] = (i/(investment*1.0));
             weights[1] = (j/(investment*1.0));
             weights[2] = (k/(investment*1.0));
             
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
    
