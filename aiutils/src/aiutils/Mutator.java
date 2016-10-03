package aiutils;
import java.util.Arrays;
import java.util.Random;


class Mutator{
  private TimeTable prime;
  private TimeTable[] candidate; 
  private int population; 
  private int mutationRate; 
  private int maxFitness; 
  private int maxGeneration; 
  private int primeConflict;
  private int primeGeneration;
  private int rand;
  
  /**
   * Construktor.
   * @param p populasi yang akan digunakan
   * @param m kemungkinan mutasi(dalam persen) yang mungkin
   * @param g banyak generasi akan berlangsung
   */
  public Mutator(int p, int m, int g){
    population = p;
    mutationRate = m;
    maxGeneration = g;
    //variation = target;
    //variation_length = variation.length;
    maxFitness = 0;
    rand = (int) System.currentTimeMillis();
    
    generateCandidates();
    System.out.println("-----initial--------------------------");
    //printPopulation();
  }
  /**
   * program-utama, panggil untuk cari solusi.
   */
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
      geneticBreed(fitness);
      geneticMutate(fitness);
      
      //check prime found of not
      primeGeneration = i;
      if(fitnessCheck(fitness) == maxFitness) break;
      //break;
      //printPopulation();
    }
    
    prime = candidate[bestFitness(fitness)];
    primeConflict = fitness[bestFitness(fitness)];
    printPopulation(prime);
  }
  /**
   * mengeset populasi
   * @param pd populasi baru
   */
  public void setPopulation(int pd){
    population = pd;
  } 
  /**
   * inisialisasi anggota populasi
   */
  public void generateCandidates(){
    candidate = new TimeTable[population];
    for(int i=0; i<population; i++)
    {
      candidate[i] = new TimeTable();
    }
    
  }
  /**
   * mengeset kemungkinan mutasi
   * @param m nilai kemungkinan mutasi baru
   */
  public void setMutationRate(int m){
    mutationRate = m;
  }
  /**
   * mengeset anggota populasi
   * @param n himpunan anggota populasi 
   */
  public void setCandidates(TimeTable[] n){
    candidate = n;
  }
  /**
   * menguji kebugaran anggota populasi
   * @param n anggota populasi yang akan diuji
   * @return nilai kebugaran anggota populasi
   */
  public int fitnessFunction(TimeTable n){
    int point = 0;
    point = n.CountTotalConflict();
    
    return point;
  }
  /**
   * mengecek apakah sudah ada kebugaran optimal
   * @param fitness kebugaran seluruh anggota populasi
   * @return index anggota populasi dengan kebugaran optimal
   */
  public int fitnessCheck(int[] fitness){
    for(int i=0; i<population; i++){
      if(fitness[i] == maxFitness) return i; 
    }
    
    return -99;
  }
  /**
   * mencari anggota dengan kebugaran paling baik
   * @param fitness kebugaran seluruh anggota populasi
   * @return index anggota dengan kebugaran terbaik
   */
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
  /**
   * mencari kebugaran populasi rata-rata
   * @param fitness kebugaran seluruh anggota populasi
   * @return kebugaran populasi rata-rata
   */
  public int averageFitness(int[] fitness){
    int sum = 0;
    for(int i=0; i<population; i++){
      sum += fitness[i];
    }
    
    int result = sum/population;
    //System.out.println("fitness"+(result));
    return result;
  }
  /**
   * melakukan operasi selection, yakni mengeliminasi anggota dengan
   * kebugaran rendah
   * @param fitness kebugaran seluruh anggota populasi
   */
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
  /**
   * melakukan operasi kawin, yakni menyampur dua anggota populasi untuk 
   * menciptakan anggota populasi dengan nilai yang mirip dengan kedua
   * orang tuanya
   * @param fitness kebugaran seluruh anggota populasi
   */
  public void geneticBreed(int[] fitness){
    Random rnd = new Random(rand);
    rand++;
    
    //generate tabel kosong sebanyak generasi
    TimeTable[] sucessor = new TimeTable[population];
    
   
    
    
    for(int i=0; i<population; i++){
      //inisialisasi
      int mate = rnd.nextInt(population);
      int size = candidate[i].getSimplified().getSize();
      int[] e0 = new int[size];
      int[] e1 = new int[size];
      int[] e2 = new int[size];
      int[] eid = new int[size];
      int crossPoint = rnd.nextInt(size);
      
      
      //use parents gene to make sucessor
      for(int j=0; j<crossPoint; j++){
        eid[j] = candidate[i].getSimplified().getStudyClassInternalId(j);
        e0[j] = candidate[i].getSimplified().getStudyClassPosition(j, false)[0];
        e1[j] = candidate[i].getSimplified().getStudyClassPosition(j, false)[1];
        e2[j] = candidate[i].getSimplified().getStudyClassPosition(j, false)[2];
      }
      //use mate gene for the rest
      for(int j=crossPoint; j<size; j++){
        eid[j] = candidate[mate].getSimplified().getStudyClassInternalId(j);
        e0[j] = candidate[mate].getSimplified().getStudyClassPosition(j, false)[0];
        e1[j] = candidate[mate].getSimplified().getStudyClassPosition(j, false)[1];
        e2[j] = candidate[mate].getSimplified().getStudyClassPosition(j, false)[2];
      }
      
      TimeTable t = new TimeTable(0);
      TimeTable.Simplified ta = t.makeNewTimeTableSimplified(eid, e0, e1, e2);
      sucessor[i] = ta.makeTimeTable();

      //check if parent is better than successor, if so use parent(the best one)
      if(fitnessFunction(candidate[i]) > fitnessFunction(sucessor[i])){
        if(fitnessFunction(candidate[i]) > fitnessFunction(candidate[mate])){
          sucessor[i] = candidate[i];
        }
        else{
          sucessor[i] = candidate[mate];
        }
      }
      else if(fitnessFunction(sucessor[i]) >= fitnessFunction(candidate[i])){
        if(fitnessFunction(sucessor[i]) > fitnessFunction(candidate[mate])){
          //do nothing, gunakan nilai sucessor
        }
        else{
          sucessor[i] = candidate[mate];
        }
      }
      
      //println
    }
    
    //replace old candidates
    candidate = sucessor;
  }
  /**
   * Melakukan mutasi, yakni mengganti nilai anggota populasi secara acak jika
   * memenangkan lotere
   * @param kebugaran seluruh anggota populasi 
   */
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
  /**
   * menampilkan nilai salah satu anggota populasi
   * @param t anggota populasi yang ingin ditampilkan
   */
  public void printPopulation(TimeTable t){
    TimeTable.Simplified ta = t.getSimplified();
    TimeTable.Simplified ts = ta.stripDown();
    int size = ts.getSize();
    for(int i=0; i<size; i++)
    {
        System.out.println(GlobalUtils.searchClassById(ts.getStudyClassInternalId(i)).getClassName());
        System.out.println(GlobalUtils.searchClassById(ts.getStudyClassInternalId(i)).getLength());
       	System.out.println("slot: "+ts.getStudyClassPosition(i,false)[0]);
        System.out.println("ruang: "+ts.getStudyClassPosition(i,false)[1]);
        System.out.println("hari: "+ts.getStudyClassPosition(i,false)[2]);
        System.out.println("========================================");
    }
  }
  /**
   * Mengembalikan solusi optimal, perlu dilakukan generatePrime terlebih dahulu
   * @return solusi optimal
   */
  public TimeTable getPrime(){
    return prime;
  }
  /**
   * mengembalikan konflik yang ada pada solusi optimal.
   * Perlu melakukan generatePrime terlebih dahulu.
   * @return konflik dari solusi optimal
   */
  public int getPrimeConflict(){
    return primeConflict;
  }  

  public int getPrimeGeneration(){
    return primeGeneration;
  }
}
