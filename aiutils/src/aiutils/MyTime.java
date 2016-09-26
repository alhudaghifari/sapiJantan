/*
 * Custom pretty simple time definition
 * Lisensi bodo amat.
 */
package aiutils;

/**
 *Kelas waktu yang amat simpel yang sebenernya ga layak jadi kelas, tapi dipake dimana-mana
 * 
 */
public class MyTime {
    private final short myHour;
    private final short myMinute;
    
    /**
     * Konstruk jam yang gak peduli sama jam sehari-hari. Ga ada pengecekan 
     * @param hour jelas lah ya
     * @param minute obvius man
     */
    public MyTime(short hour, short minute){
        myHour = hour;
        myMinute = minute;
    }
    
    /**
     * jelas lah ya
     * @return komponen jam. 
     */
    public int getHour(){
        return myHour;
    }
    
    /**
     * jelas lah ya
     * @return komponen menit
     */
    public int getMinute(){
        return myMinute;
    }
}
