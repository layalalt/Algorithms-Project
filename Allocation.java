public class Allocation //object that will hold a given allocation
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
