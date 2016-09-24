/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aiutils;

/**
 * Time Constraint <br>
 * terdiri dari array mytime dan array boolean hari <br>
 * dan sedikit cinta
 */
public class TimeConstraint {
    private MyTime tConstraint[];
    private boolean dConstraint[];
    
    /**░▄▀▄▀▀▀▀▄▀▄░░░░░░░░░ ░▄▀▄▀▀▀▀▄▀▄░░░░░░░░░ ANNOYING DOG<br>
     * ░█░░░░░░░░▀▄░░░░░░▄░ ░█░░░░░░░░▀▄░░░░░░▄░ MENGINVASI<br>
     * █░░▀░░▀░░░░░▀▄▄░░█░█ █░░▀░░▀░░░░░▀▄▄░░█░█ JAVADOG <br>
     * █░▄░█▀░▄░░░░░░░▀▀░░█ █░▄░█▀░▄░░░░░░░▀▀░░█ <br>
     * █░░▀▀▀▀░░░░░░░░░░░░█ █░░▀▀▀▀░░░░░░░░░░░░█ <br>
     * █░░░░░░░░░░░░░░░░░░█ █░░░░░░░░░░░░░░░░░░█ <br>
     * █░░░░░░░░░░░░░░░░░░█ █░░░░░░░░░░░░░░░░░░█ <br>
     * ░█░░▄▄░░▄▄▄▄░░▄▄░░█░ ░█░░▄▄░░▄▄▄▄░░▄▄░░█░ <br>
     * ░█░▄▀█░▄▀░░█░▄▀█░▄▀░ ░█░▄▀█░▄▀░░█░▄▀█░▄▀░ <br>
     * ░░▀░░░▀░░░░░▀░░░▀░░░ ░░▀░░░▀░░░░░▀░░░▀░░░ <br>
     * Konstruktor biasa.
     * @param el_start konstrain awal
     * @param el_end konstrain akhir
     * @param mon kebukaan (?) di hari senin. True jika buka
     * @param tue kebukaan (?) di hari slasa. True jika buka
     * @param wed kebukaan (?) di hari raabu. True jika buka
     * @param thu kebukaan (?) di hari kamis. True jika buka
     * @param fri kebukaan (?) di hari jumat. True jika buka
     */
    public TimeConstraint(MyTime el_start, MyTime el_end,
            boolean mon, boolean tue, boolean wed, boolean thu, boolean fri){
        tConstraint = new MyTime[2];
        dConstraint = new boolean[5];
        ////////////////////////////
        tConstraint[0] = el_start;
        tConstraint[1] = el_end;
        dConstraint[0] = mon;
        dConstraint[1] = tue;
        dConstraint[2] = wed;
        dConstraint[3] = thu;
        dConstraint[4] = fri;
    }
    
    /**
     * Getter komponen MyTime dari Time Constrain.
     * @return MyTime yang merupakan duplikat dari aslinya
     */
    public MyTime[] getTimeConstr(){
        MyTime[] copyTime = tConstraint.clone();
        return copyTime;
    }
    
    /*
     * Getter komponen hari dari konstrain waktu
     * @return array boolean biasa yang merepresentasikan konstrain hari
     * senen, selasa, rabu, kamis, jumat.
     */
    public boolean[] getDayConstr(){
        boolean[] copyDay = dConstraint.clone();
        return copyDay;
    }
    
    /**
     * Duplikat segar dari objek ini, langsung dari kulkas.
     * @return duplikatnya. Udah dibilangin kan tadi.
     */
    public TimeConstraint getCopy(){
        MyTime[] tdata = tConstraint.clone();
        boolean[] bdata = dConstraint.clone();
        TimeConstraint datacopy = new TimeConstraint(tdata[0], tdata[1], 
                bdata[0], bdata[1], bdata[2], bdata[3], bdata[4]);
        return datacopy;
    }
}
            