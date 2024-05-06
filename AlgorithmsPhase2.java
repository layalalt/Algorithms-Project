import java.util.*;
import java.io.*;

public class AlgorithmsPhase2 
{

    static class Asset //objects that will hold the assets read from the file
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

        List<Integer> units = new ArrayList<Integer>(); 
        double aReturn; 
        double risk; 

        public Allocation(int numAssets) 
        {
            for(int i= 0; i<numAssets; i++) 
                units.add(0); 
        }

        public void setAllocation(Allocation a)
        {
            this.units = a.units;
            this.aReturn = a.aReturn;
            this.risk = a.risk;
        }

        public void setAllocation(int i, int j, int k, double aReturn, double risk) 
        {
            this.units.add(0, i);
            this.units.add(1, j);
            this.units.add(2, k);
            this.aReturn = aReturn;
            this.risk = risk;
        }

        public void setAllocation(List<Integer> units, double aReturn, double risk) 
        {
            this.units = new ArrayList<>(units);
            this.aReturn = aReturn;
            this.risk = risk;
        }
    }

    public static void main(String[] args) 
    {

        Scanner key = new Scanner(System.in);
        String name[] = new String[3]; //stores asset's name
        double eReturn[] = new double[3]; //stores asset's expected return
        double riskLevel[] = new double[3]; //stores asset's risk level
        int quantity[] = new int[3]; //stores asset's maximum quantity
        double tolerance = 0;
        int investment = 0;

        try(BufferedReader br = new BufferedReader(new FileReader("Profile2.txt"))) //reads the text file containing the profile
        {
            String line;
            int i = 0;
            while ((line = br.readLine()) != null) 
            {
                String[] parts = line.split(" : "); //per iteration, parts will store each asset's information as separate parts; parts[0] is the name, parts[1] is the expected return...
                if(parts.length == 4) 
                {
                    name[i] = parts[0];
                    eReturn[i] = Double.parseDouble(parts[1]);
                    riskLevel[i] = Double.parseDouble(parts[2]);
                    quantity[i] = Integer.parseInt(parts[3]);
                    i++;
                } 
                else if(i > 2) //reads the total investment and risk from the input file
                {
                    if(line.startsWith("Total investment:")) 
                      investment = Integer.parseInt(line.split(":")[1].trim());
                     else if(line.startsWith("Risk tolerance level:")) 
                      tolerance = Double.parseDouble(line.split(":")[1].trim());   
                }
            }
        } 
        catch(IOException e) 
        {
            e.printStackTrace();
        }

        ArrayList<Asset> assets = new ArrayList<Asset>();

        assets.add(new Asset(name[0], eReturn[0], riskLevel[0], quantity[0]));
        assets.add(new Asset(name[1], eReturn[1], riskLevel[1], quantity[1]));
        assets.add(new Asset(name[2], eReturn[2], riskLevel[2], quantity[2]));
    
        Allocation best = new Allocation(3); //best allocation found
        
        //long startTime = System.currentTimeMillis();
        dynamicProgramming(assets, tolerance, investment, best); //calls the dynamic programming algorithm
        //long endTime = System.currentTimeMillis();
        //System.out.println("Time taken by the dynamic programming algorithm: " + (endTime-startTime)/1000.0 + " seconds");
      
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
       double portfolioRisk, eReturn, riskLevels [] = new double[3];
       riskLevels[0] = assets.get(0).risk;
       riskLevels[1] = assets.get(1).risk;
       riskLevels[2] = assets.get(2).risk;
       
       
       double[][] dp = new double[assetsSize+1][investment+1]; //table that will store the best return found so far given the first i assets and j of the investment given it's within the tolerance level
       Allocation[][] allocations = new Allocation[assetsSize+1][investment+1]; //table that will store the composition of the allocation at dp[i][j] 
    
       for(int i=0; i<=assetsSize; i++) //allocations initializer
         for(int j=0; j<=investment; j++) 
            allocations[i][j] = new Allocation(3);
          
       
      for(int i=1; i<=assetsSize; i++) 
      {
         Asset currentAsset = assets.get(i-1); //asset of the current row
         for(int j=1; j<=investment; j++) 
         {
            for(int k=0; k<=Math.min(currentAsset.quantity, j); k++) //combinations possible at the current row
            {
                eReturn = dp[i-1][j-k] + currentAsset.eReturn * (k/(double)investment); //return combination calculator
                
                double[] weights = new double[3]; //weights of each asset
                weights[i-1] = k/(double)investment;
               
                if(i > 1) 
                {
                    weights[0] = allocations[i-1][j-k].units.get(0)/(double)investment;
                    weights[1] = allocations[i-1][j-k].units.get(1)/(double)investment;
                }
                
                portfolioRisk = Math.sqrt(Variance(riskLevels, weights)); //risk calculator
                
                if(portfolioRisk <= tolerance)
                {
                    if(eReturn > dp[i][j]) //if the current combination is greater than a previously found combination
                    {
                        dp[i][j] = eReturn;
                        allocations[i][j] = new Allocation(3);
                        
                        //allocation setters per row
                        if(i == 1) 
                          allocations[i][j].setAllocation(k, 0, 0, eReturn, portfolioRisk);
                        else if(i == 2) 
                          allocations[i][j].setAllocation(allocations[i-1][j-k].units.get(0), k, 0, eReturn, portfolioRisk);
                        else 
                          allocations[i][j].setAllocation( allocations[i-1][j-k].units.get(0), allocations[i-1][j-k].units.get(1), k, eReturn, portfolioRisk);   
                    }
                }

            }
         }
      }
      best.setAllocation(allocations[assetsSize][investment]); //best allocation
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

