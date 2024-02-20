import java.io.*;
import java.util.*;
public class AlgorithmsPhase1
{
    public static void main(String [] args)
    {
      Scanner key = new Scanner(System.in);
      String line, name[] = new String[3];
      double eReturn[] = new double[3];
      double riskLevel[] = new double[3];
      int quantity[] = new int[3];
      try (BufferedReader br = new BufferedReader(new FileReader("Profile1.txt"))) 
      {
        int i=0;
        while((line = br.readLine()) != null)
        {
          String[] parts = line.split(" : ");
          name[i] = parts[0].trim();
          eReturn[i] = Double.parseDouble(parts[1].trim());
          riskLevel[i] = Double.parseDouble(parts[2].trim());
          quantity[i] = Integer.parseInt(parts[3].trim());
          i++;
        } 
      }
      catch (IOException e) 
      {
         e.printStackTrace();
      }
      System.out.println( quantity[0] + " " + quantity[1] + " " + quantity[2]); //checks success of file reading (TEMPORARY)
      System.out.print("Enter tolerance level (0.01-0.06): "); //random values
      double max = 0, tolerance = key.nextDouble();  
      int totalQuantity; 
      Allocation best = new Allocation();
      double portfolioReturn, portfolioRisk, weights[] = new double[3];
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
             portfolioRisk = Math.sqrt(Variance(riskLevel, weights));
             System.out.println("Return: " +portfolioReturn + "   Risk:" + portfolioRisk); //Temporary
             if(portfolioRisk <= tolerance)
             {
               if(portfolioReturn > max)
               {
                 max = portfolioReturn;
                 best.setAllocation(i, j, k, portfolioReturn, portfolioRisk);
               }
             }
             
           }
         }
       }  
       System.out.println("Optimal Allocation: ");
       System.out.println(name[0] + ": " + best.asset1);    
       System.out.println(name[1] + ": " + best.asset2);    
       System.out.println(name[2] + ": " + best.asset3); 
       System.out.println("   Expected Portfolio Return: " + best.retrn);   
       System.out.println("Portfolio Risk Level: " + best.risk);   
    }
    
    
    public static double Variance(double[] riskLevels, double[] weights) 
    {
      double portfolioVariance = 0.0; 
      for(int i=0; i<3; i++) 
      {
        for(int j=0; j<3; j++) 
        {
          portfolioVariance += weights[i] * weights[j] * riskLevels[i] * riskLevels[j];
        } 
      }
      return portfolioVariance;
    }
} 
   
