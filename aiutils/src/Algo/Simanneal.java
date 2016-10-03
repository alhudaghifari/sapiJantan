/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Algo;

import aiutils.TimeTable;
import java.util.Random;

public class Simanneal {
    TimeTable tt;
    float P = 0;
    int T = 1000;
    
    Simanneal(TimeTable t, float p)
    {
        tt = t;
        P = p;
    }
    
    TimeTable Sim()
    {
        boolean finded = false;
        int i=0;
        double prob;
        int l = tt.getSimplified().getSize();
        //generate neighborn
        TimeTable tx[] = new TimeTable[l];
        TimeTable xx = tt.getCopy();
        tx = tt.generateNeighboringTimeTable();
        
        //cari konflik yang lebih baik kalo ketemu 1 langsung TT berubah
        while(i<l && !finded)
        {
            //saat konflik lebih baik langsung pindah
            if(xx.CountTotalConflict()>=tx[i].CountTotalConflict())
            {
                //relace TT
                xx = tx[i].getCopy();
                T--;
                finded = true;
            }
            //saat konflik tidak lebih bai
            else
            {
                //ukur probabilitas
                prob = Math.exp(-1*((xx.CountTotalConflict() - tx[i].CountTotalConflict())/T));
                T--;
                if(prob>=P)
                {
                    //relace TT
                    xx = tx[i].getCopy();
                    finded = true;
                }
                else
                {
                    i++;
                }
            }
        }
        return xx;
    }
    
    TimeTable greed()
    {
        boolean finded = false;
        int index;
        TimeTable tx[] = new TimeTable[tt.getSimplified().getSize()];
        tx = tt.generateNeighboringTimeTable();
        TimeTable xx = tt.getCopy();
        int r[] = new int[tx.length];
        
        //pindah nilai conflict ke array
        for(int i=0;i<tx.length;i++)
        {
            r[i] = tx[i].CountTotalConflict();
        }
        //cari index punya nilai min
        index = maxarrin(xx,r);
        //kalo index tidak -99
        if(index != -99)
            //replace TT
            xx = tx[index].getCopy();
        else
        {
            //kalo tidak ada maka random walk
            Random ww = new Random();
            int rand = ww.nextInt(tx.length);
            //replace TT
            xx = tx[rand].getCopy();
        }
        
        return xx;
    }
    
    int maxarrin(TimeTable xx, int[] r)
    {
        int hasil = xx.CountTotalConflict();
        int i = 0;
        int thatsit = -99;
        
        while(i<r.length)
        {
            if(hasil>r[i])
            {
                hasil = r[i];
                thatsit = i;
            }
            else
                i++;
        }
        return thatsit;
    }
    
    void run()
    {
        boolean br = false;
        while(tt.CountTotalConflict()>0 && !br)
        {
            if(T>0)
               Sim();
            else
            {
               greed(); 
            }
        }
    }
}
