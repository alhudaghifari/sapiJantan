/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aiutils;
import java.util.Arrays;
import java.util.Random;

/**
 *
 * @author CXXXV
 */
class Mutator{
  private TimeTable prime; //yang solusi teroptimal
  private TimeTable[] candidate; //timetable yang masih diprose
  private int population; //banyaknya table yang di-generate
  private int mutationComplexity; //banyak yang dimutasikan
  private int mutationRate; //kemungkinan terjadi mutasi {0-100}
  private int maxFitness; //fitness paling optimal
  private int maxGeneration; //berapa banyak loop genetic algorithm dilakukan
  //private int[] variation; //constraint yang perlu dimasukkan
  //private int variation_length;
  int rand;
  
  //population, mutationRate, maxGeneration
  public Mutator(int p, int m, int g){
    population = p;
    mutationRate = m;
    maxGeneration = g;
    mutationComplexity = 1;
    //variation = target;
    //variation_length = variation.length;
    maxFitness = 0;
    rand = (int) System.currentTimeMillis();
    
    generateCandidates();
    System.out.println("-----initial--------------------------");
    //printPopulation();
  }
  
  public void setPopulation(int pd){
    population = pd;
  }
  
  public void generateCandidates(){
    candidate = new TimeTable[population];
    for(int i=0; i<population; i++)
    {
      candidate[i] = new TimeTable();
    }
    
  }
  
  public void setMutationRate(int m){
    mutationRate = m;
  }
  
  public void setMutationComplexity(int m){
    mutationComplexity = m;
  }
  
  public void setCandidates(TimeTable[] n){
    candidate = n;
  }
  
  public int fitnessFunction(TimeTable n){
    int point = 0;
    point = n.CountTotalConflict();
    
    return point;
  }
  
  public int fitnessCheck(int[] fitness){
    for(int i=0; i<population; i++){
      if(fitness[i] == maxFitness) return i; 
    }
    
    return -99;
  }
  
  public int bestFitness(int[] fitness){
    int max = 99999;
    int maxi = 0;
    for(int i=0; i<population; i++){
      if(fitness[i] < max){
        max = fitness[i];
        maxi = i;
      }
      
    }
    
    return maxi;
  }
  
  public void generatePrime(){
    int[] fitness = new int[population];
    
    for(int i=0; i<maxGeneration; i++){
      //calculate fitness
      for(int j=0; j<population; j++)
      {
        fitness[j] = fitnessFunction(candidate[j]);
        //System.out.println("fitness:"+fitness[j]);
      }
      
      //apply natural selection
      geneticSelect(fitness);
      //geneticBreed(fitness);
      geneticMutate(fitness);
      
      //check prime found of not
      if(fitnessCheck(fitness) == maxFitness) break;
      //break;
      //printPopulation();
    }
    
    prime = candidate[bestFitness(fitness)];
    printPopulation(prime);
  }
  
  public int averageFitness(int[] fitness){
    int sum = 0;
    for(int i=0; i<population; i++){
      sum += fitness[i];
    }
    
    int result = sum/population;
    //System.out.println("fitness"+(result));
    return result;
  }
  
  public void geneticSelect(int[] fitness){
    int failFitness = averageFitness(fitness);
    
    
    int best = 99999;
    int besti = 0;
    for(int i=0; i<population; i++){
      //mark with best genes
      if(fitness[i] < best){
        best = fitness[i];
        besti = i;
      }
      //mark those with bad genes
      if(fitness[i] > failFitness){
        fitness[i] = -1;
      }    
    }
    
    //replace bad genes with best
    for(int i=0; i<population; i++){
      if(fitness[i] == -1){
        fitness[i] = new Integer(fitness[besti]);
        //kopi isi tabel buruk dengan yang bagus
        candidate[i] = candidate[besti].getCopy();
      }
    }
  }
  
  public void geneticBreed(int[] fitness){
    Random rnd = new Random(rand);
    rand++;
    
    //generate tabel kosong sebanyak generasi
    TimeTable[] sucessor = new TimeTable[population];
    
    int crossPoint = 0;
    int endPoint = 0;
    
    for(int i=0; i<population; i++){
      //pick mate
      int mate = rnd.nextInt(population);
      
      //determine which gene to use
      
      //use parents gene to make sucessor
      
      //check if parent is better than successor, if so use parent(the best one)
      if(fitnessFunction(candidate[i]) > fitnessFunction(sucessor[i])){
        if(fitnessFunction(candidate[i]) > fitnessFunction(candidate[mate])){
          //tetap gunakan nilai parent-i
        }
        else{
          System.arraycopy( candidate[mate], 0, sucessor[i], 0, crossPoint);
        }
      }
      else if(fitnessFunction(sucessor[i]) >= fitnessFunction(candidate[i])){
        if(fitnessFunction(sucessor[i]) > fitnessFunction(candidate[mate])){
          //do nothing, gunakan nilai sucessor
        }
        else{
          //gunakan nilai parent-mate
        }
      }
      
      //println
    }
    
    //replace old candidates
    //kopi nilai suksesor ke kandidat lama
  }
  
  public void geneticMutate(int[] fitness){
    Random rnd = new Random(rand);
    rand++;
    int startPoint = 0;
    
    for(int i=0; i<population; i++){
      if(mutationRate > rnd.nextInt(100)){
        System.out.println("sup");
        candidate[i] = new TimeTable();
      }
    }
  }
  
  public void printPopulation(TimeTable t){
    for(int i=0; i<t.getSimplified().getSize(); i++)
    {
       	System.out.println(t.getSimplified().getStudyClassPosition(i,false)[0]);
        System.out.println(t.getSimplified().getStudyClassPosition(i,false)[1]);
        System.out.println(t.getSimplified().getStudyClassPosition(i,false)[2]);
        System.out.println("========================================");
    }
  }
  
  public void printPrime(){

//==========versi-lama==========================================================
//    System.out.println(Arrays.toString(prime));
  }
  
  
  public static void main(String[] args){
    System.out.println("hello world");
    GlobalUtils global = new GlobalUtils();
    GlobalUtils.fileReader f = new GlobalUtils.fileReader();
    TimeTable t = new TimeTable();
    //System.out.println(t.getSimplified().getStudyClassPosition(0, true)[0]);
    
    Mutator m = new Mutator(50, 1, 1000);
    m.generatePrime();
    //m.printPopulation();
  }
}
