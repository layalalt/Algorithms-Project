import java.util.*;
import java.io.*;

public class AlgorithmsPhase2 
{
    static class Asset 
    {
         String name;
         double eReturn;
         double risk;
         int quantity;

        public Asset(String name, double eReturn, double risk, int quantity) 
        {
           this.name = name;
           this.eReturn = eReturn;
           this.risk = risk;
           this.quantity = quantity;
        }
    }
    
    static class Allocation
    {
       List<Integer> units = new ArrayList<Integer>(); // Store units allocated for each asset
       double aReturn; // Represent the return of the allocation
       double risk; // Represent the risk of the allocation

       public Allocation(int numAssets) 
       {
         for(int i=0; i<numAssets; i++) 
            units.add(0);
       }

       public void setAllocation(List<Integer> units, double aReturn, double risk) 
       {     
         this.units = new ArrayList<>(units); 
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
              
      try(BufferedReader br = new BufferedReader(new FileReader("Profile1.txt"))) //reads the text file containing the profile
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
      
      ArrayList<Asset> assets = new ArrayList<Asset>();
      
      assets.add(new Asset(name[0], eReturn[0], riskLevel[0], quantity[0]));
      assets.add(new Asset(name[1], eReturn[1], riskLevel[1], quantity[1]));
      assets.add(new Asset(name[2], eReturn[2], riskLevel[2], quantity[2]));
  
      Allocation best = new Allocation(3);
      
      dynamicProgramming(assets, tolerance, investment, best);
      
      System.out.println("Optimal Allocation: ");
      System.out.println(name[0] + ": " + best.units.get(0) + " units");    
      System.out.println(name[1] + ": " + best.units.get(1) + " units");    
      System.out.println(name[2] + ": " + best.units.get(2) + " units"); 
      System.out.printf("   Expected Portfolio Return: %.3f\n", best.aReturn);   
      System.out.printf("Portfolio Risk Level: %.3f\n", best.risk); 
    }
    
  
    public static void dynamicProgramming(ArrayList<Asset> assets, double tolerance, int investment, Allocation best)
    {
        int assetsSize = assets.size();
        double[][] returns = new double[assetsSize][investment + 1];
        double[][] weights = new double[assetsSize][investment + 1];
        double[][] riskLevels = new double[assetsSize][investment + 1];
        
        for(int i=0; i<assetsSize; i++) 
        {
           Asset currentAsset = assets.get(i);
           for(int j=0; j<=Math.min(currentAsset.quantity, investment); j++) 
           {    
               weights[i][j] = (j/(investment*1.0));
               returns[i][j] = (j/(investment*1.0))*currentAsset.eReturn; 
               riskLevels[i][j] = currentAsset.risk;
           }
        }

        double[][] dp = new double[assetsSize + 1][investment + 1];
        int[][] allocations = new int[assetsSize + 1][investment + 1];

        for(int i=1; i<=assetsSize; i++) 
        {
          Asset currentAsset = assets.get(i - 1);
          for(int j=1; j<=investment; j++) 
          {
            for(int k=0; k<=Math.min(currentAsset.quantity, j); k++) 
            {
                
                if(Math.sqrt(Variance(riskLevels[i-1], weights[i-1] , k)) <= tolerance)
                {   
                    //System.out.println(Math.sqrt(Variance(riskLevels[i-1], weights[i-1] , k)));
                    dp[i][j] = Math.max(dp[i-1][j], returns[i-1][k] + dp[i-1][j-k]);
                    allocations[i][j] = k;
                }
                else
                    dp[i][j] = dp[i-1][j];
            }
          }
        }

       List<Integer> bestUnits = new ArrayList<Integer>();
       int remainingInvestment = investment;
       for(int i=assetsSize; i>0; i--) 
       {
         int units = allocations[i][remainingInvestment];
         bestUnits.add(units);
         remainingInvestment -= units;
       }
        displayGrid(dp, investment);
        best.setAllocation(bestUnits, dp[assetsSize][investment], Math.sqrt(Variance(riskLevels[assetsSize - 1], weights[assetsSize - 1], allocations[assetsSize][investment])));
    }

    public static double Variance(double[] riskLevels, double[] weights, int length) 
    {
       double variance = 0;

    
       return 0;
    }
    
    public static void displayGrid(double[][] g, int investment)
    {
        for(int i=0; i<4; i++)
        { 
          for(int j=0; j<investment; j++) 
          { 
              System.out.printf("%.4f |",g[i][j]);
          
          }
           System.out.println();
        }

         System.out.println();
    }
    
   
 }

