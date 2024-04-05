import java.util.*;
import java.io.*;

public class AlgorithmsPhase2 
{
    public static void main(String [] args)
    {
      Scanner key = new Scanner(System.in);
      String name[] = new String[3]; //stores asset's name
      double eReturn[] = new double[3]; //stores asset's expected return
      double riskLevel[] = new double[3]; //stores asset's risk level
      int quantity[] = new int[3]; //stores asset's maximum quantity
      double tolerance = 0;
      int investment = 0;
              
      try (BufferedReader br = new BufferedReader(new FileReader("Profile1.txt"))) //reads the text file containing the profile
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
      Allocation best = new Allocation();
      dynamicProgramming(eReturn, riskLevel, quantity, tolerance, investment, best);
      
      /**System.out.println("Optimal Allocation: ");
      System.out.println(name[0] + ": " + best.units1 + " units");    
      System.out.println(name[1] + ": " + best.units2 + " units");    
      System.out.println(name[2] + ": " + best.units3 + " units"); 
      System.out.printf("   Expected Portfolio Return: %.3f\n", best.aReturn);   
      System.out.printf("Portfolio Risk Level: %.3f\n", best.risk);  */ 
    }
    
   /** public static void dynamicProgramming(double[] eReturn, double[] riskLevel, int[] quantity, double tolerance, int investment, Allocation best) 
    {
      int assets = eReturn.length;
      double[][][] dp = new double[assets + 1][investment + 1][investment + 1];

    
      for(int i=0; i<=assets; i++) 
         for(int j=0; j<=investment; j++) 
            for(int k=0; k<=investment; k++) 
                  dp[i][j][k] = 0;
            
        
  
      for(int i=1; i<=assets; i++) 
      {
        for(int j=1; j<=investment; j++) 
        {
            for(int k=0; k<=Math.min(j, quantity[i - 1]); k++) 
            {
                double portfolioReturn = k/j * eReturn[i - 1] + dp[i - 1][j - k][k];
                double portfolioRisk = Math.sqrt(Variance3(riskLevel, computeWeights(i, k, j - k)));
                if(portfolioRisk <= tolerance && portfolioReturn > dp[i][j][k]) 
                    dp[i][j][k] = portfolioReturn;
            }
        }
      }

    // Find the maximum portfolio return
    double maxReturn = 0;
    int maxJ = 0, maxK = 0;
    for(int j=0; j<=investment; j++) 
    {
        for(int k=0; k<=investment; k++) 
        {
            maxReturn = Math.max(maxReturn, dp[assets][j][k]);
            if (dp[assets][j][k] == maxReturn) 
            {
                maxJ = j;
                maxK = k;
            }
        }
    }

    // Update the best allocation
    best.setAllocation(maxJ - maxK, maxK, investment - maxJ, maxReturn, Math.sqrt(Variance3(riskLevel, computeWeights(assets, maxK, maxJ - maxK))));
}

// Helper method to compute weights
private static double[] computeWeights(int assetIndex, int quantity1, int quantity2) {
    double totalQuantity = quantity1 + quantity2;
    double weight1 = quantity1 / totalQuantity;
    double weight2 = quantity2 / totalQuantity;
    return new double[]{weight1, weight2};
}*/
    
    public static void dynamicProgramming(double[] eReturn, double[] riskLevel, int[] quantity, double tolerance, int investment, Allocation best)
    {
        int assets = eReturn.length;
        double [][] dp = new double[assets+1][investment + 1];
        //Allocation[][] allocation = new Allocation[assets + 1][investment + 1];
 
        for(int i=1; i<=assets; i++) 
        {       
            for(int j=1; j<=investment; j++) 
            {
               dp[i][j] = dp[i-1][j]; //initialize it to previous row
              
               double [] weights = new double[3];
               if(i == 1 && j <= quantity[0])
               { 
                   //if(riskLevel[0] <= tolerance)
                     dp[i][j] = Math.max(dp[0][j], eReturn[0]);
                    // allocation[i][j].setAllocation(j,0,0, dp[i][j],0);
               }
               
               else if(i == 2 && j <= quantity[0] + quantity[1])
               {  
                  weights[0] = ((j-1)/(j*1.0));
                  weights[1] = (1/(j*1.0));
                  double prevRisk = 0;
                  prevRisk = Math.sqrt(Variance2(prevRisk, weights[0], riskLevel[1], weights[1]));
                  //if(Math.sqrt(Variance2(prevRisk, weights[0], riskLevel[1], weights[1])) <= tolerance)
                     dp[i][j] = Math.max(dp[2][j-1]*((j-1)/j) + eReturn[1]*(1/j), Math.max(dp[1][j], dp[2][j-1]));
                   // allocation[i][j].setAllocation(j-1,1,0, dp[i][j],0);

               }
               else if(i == 3 && j < quantity[0] + quantity[1] + quantity[2])
               {
                  weights[0] = ((j-1)/(j*1.0));
                  weights[1] = (1/(j*1.0));
                  double prevRisk = 0;
                  prevRisk = Math.sqrt(Variance2(prevRisk, weights[0], riskLevel[1], weights[1]));
                  //if(Math.sqrt(Variance2(prevRisk, weights[0], riskLevel[1], weights[1])) <= tolerance)
                  {
                      dp[i][j] = Math.max(dp[3][j-1]*((j-1)/j) + eReturn[2]*(1/j), Math.max(dp[2][j], dp[3][j-1]));
                  } 
               }
         
              }
            }
     
    
    double maxReturn = findMaxValue(dp);      
        System.out.println(dp[3][investment]);
        
                         // displayGrid(dp, investment);
                   
    }
    
    public static double findMaxValue(double[][] dp)
    {
      double max = 0;
        for (int i = 1; i <= 3; i++){ 
        for (int j = 1; j <= 900; j++) {
           if(dp[i][j]> max)
               max = dp[i][j];
        }
    }
       return max;
    }
    
    
public static double Variance3(double[] riskLevel, double[] weights) {
   

    double variance = 0;
    for (int i = 0; i < weights.length; i++) {
        variance += Math.pow(riskLevel[i] * weights[i], 2);
    }
    return variance;
}


 
    
    public static double Variance(double[] riskLevels, double[] weights) //calculates portfolio's variance
    {
      double variance = 0; 
      for(int i=0; i<3; i++) 
        for(int j=0; j<3; j++) 
          variance += weights[i] * weights[j] * riskLevels[i] * riskLevels[j];
      
      return variance;
    }
    
    public static double Variance2(double riskLevel1, double weight1, double riskLevel2, double weight2) 
    {
        double variance =  (Math.pow(weight1, 2) * Math.pow(riskLevel1, 2)) + (Math.pow(weight2, 2) * Math.pow(riskLevel2, 2)) + 2 * weight1 * weight2* riskLevel1 * riskLevel2;
        return variance;
    }
    
    public static void displayGrid(double[][] g, int investment)
    {
        for(int i=0; i<4; i++)
        { 
          for(int j=0; j<investment; j++) 
          { 
              System.out.printf("%.2f |",g[i][j]);
          
          }
           System.out.println();
        }

         System.out.println();
    }
    
}