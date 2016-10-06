/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aiutils;

import aiutils.GlobalUtils;
import aiutils.TimeTable;
import java.util.Random;

public class Simanneal {
    TimeTable tt;
    double P = 0;
    int T = 1000;
    
    Simanneal(TimeTable t, double p)
    {
        tt = t;
        P = p;
    }
    
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
            int rand = ww.nextInt()%tx.length;
            if(rand < 0) rand *= -1;
            tt = tx[rand].getCopy();
        }
            
    }
    
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
    
    void runSim()
    {
        int i=0;
        while(tt.CountTotalConflict()>0 && i<1000)
        {
            if(T>0)
               Sim();
            else
            {
               greed(); 
            }
            i++;
        }
        //printPopulation(tt);
    }
    
    void runHill()
    {
        int i=0;
        while(tt.CountTotalConflict()>0 && i<1000)
        {
            greed(); 
            i++;
        }
        //printPopulation(tt);
    }
    
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
