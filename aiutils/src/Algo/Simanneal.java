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
            int rand = ww.nextInt(100);
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
