/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aiutils;

/**
 *
 * @author HP
 */
public class MainCLI {
    public static void main(String[] args){
        String path = args[0];
        int mode = Integer.valueOf(args[1]);
        GlobalUtils.fileReader f = new GlobalUtils.fileReader(path);
        TimeTable tt = new TimeTable();
        switch(mode){
            case 1 :{
                Simanneal sa = new Simanneal(tt, 0);
                sa.runHill();
                TimeTable ttt = sa.tt;
                sa.printPopulation(ttt);
                System.out.println("Konflik = " + ttt.CountTotalConflict());
                System.out.println("Efektif = " + ttt.CountEffectiveRoom());
                break;
            }
            case 2 :{
                Simanneal sa = new Simanneal(tt, 0.8);
                sa.runSim();
                TimeTable ttt = sa.tt;
                System.out.println("Konflik = " + ttt.CountTotalConflict());
                System.out.println("Efektif = " + ttt.CountEffectiveRoom());
                sa.printPopulation(ttt);
                break;
            }
            case 3 :{
                Mutator sa = new Mutator(50,1,1000);
                sa.generatePrime();
                TimeTable ttt = sa.getPrime();
                sa.printPopulation(ttt);
                System.out.println("Konflik = " + ttt.CountTotalConflict());
                System.out.println("Efektif = " + ttt.CountEffectiveRoom());
                break;
            }
            default :{
                System.out.println("Konflik = " + tt.CountTotalConflict());
                System.out.println("Efektif = " + tt.CountEffectiveRoom());
            }
        }
    }
}
