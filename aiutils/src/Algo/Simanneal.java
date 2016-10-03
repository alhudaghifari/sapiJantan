package Algo;

import aiutils.GlobalUtils;
import aiutils.TimeTable;
import java.util.Random;

public class Simanneal {
    TimeTable tt;
    double P = 0;
    int T = 1000;
    
    /**
     * membuat timetable baru dengan inputan timetable dan probabilitas
     * @param t inputan timetable
     * @param p inputan probabilitas
     */
    Simanneal(TimeTable t, double p)
    {
        tt = t;
        P = p;
    }
    
    /**
     * methode dengan algoritma simulated annealing
     */
    void Sim()
    {
        boolean finded = false;
        int i=0;
        double prob;
        
        //generate neighborn
        TimeTable tx[] = new TimeTable[tt.getSimplified().getSize()];
        tx = tt.generateNeighboringTimeTable();
        
        //cari konflik yang lebih baik kalo ketemu 1 langsung TT berubah
        while(i<100 && !finded)
        {
            //saat konflik lebih baik langsung pindah
            if(tt.CountTotalConflict()>=tx[i].CountTotalConflict())
            {
                //relace TT
                tt = tx[i].getCopy();
                T--;
                finded = true;
            }
            //saat konflik tidak lebih bai
            else
            {
                //ukur probabilitas
                prob = Math.exp(-1*((tt.CountTotalConflict() - tx[i].CountTotalConflict())/T));
                T--;
                if(prob>=P)
                {
                    //relace TT
                    tt = tx[i].getCopy();
                    finded = true;
                }
                else
                {
                    i++;
                }
            }
        }
    }
    
    /**
     * methode dengan algoritma hillclimbing with random walk
     */
    void greed()
    {
        boolean finded = false;
        int index;
        TimeTable tx[] = new TimeTable[tt.getSimplified().getSize()];
        tx = tt.generateNeighboringTimeTable();
        int r[] = new int[tx.length];
        
        for(int i=0;i<tx.length;i++)
        {
            r[i] = tx[i].CountTotalConflict();
        }
        index = maxarrin(r);
        if(index != -99)
            tt = tx[index].getCopy();
        else
        {
            Random ww = new Random();
            int rand = ww.nextInt(100);
            tt = tx[rand].getCopy();
        }
            
    }
    
    /**
     * mencari index dari array yang nilainya terkecil
     * @param r array yang dicari nilai min
     * @return index  yang min
     */
    int maxarrin(int[] r)
    {
        int hasil=0;
        int i = 0;
        int thatsit = -99;
        int count = 0;
        
        while(i<r.length)
        {
            if(hasil>r[i])
            {
                hasil = r[i];
                thatsit = i;
                count++;
            }
            else
                i++;
        }
        return thatsit;
    }
    
    /**
     * methode untuk run dengan algoritma simulated annealing
     */
    void runSim()
    {
        int i=0;
        while(tt.CountTotalConflict()>0 && i<100)
        {
            if(T>0)
               Sim();
            else
            {
               greed(); 
            }
            i++;
        }
        printPopulation(tt);
    }
    
    
    /**
     * methode untuk run dengan algoritma hillclimbing with random walk
     */
    void runHill()
    {
        int i=0;
        while(tt.CountTotalConflict()>0 && i<100)
        {
            greed(); 
            i++;
        }
        printPopulation(tt);
    }
    
    /**
     * print timetable
     * @param t timetable yang akan di print
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
}
