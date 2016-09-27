/*
 * I dont care license. 
 *
 *░▄▀▄▀▀▀▀▄▀▄░░░░░░░░░ ░▄▀▄▀▀▀▀▄▀▄░░░░░░░░░
 *░█░░░░░░░░▀▄░░░░░░▄░ ░█░░░░░░░░▀▄░░░░░░▄░
 *█░░▀░░▀░░░░░▀▄▄░░█░█ █░░▀░░▀░░░░░▀▄▄░░█░█
 *█░▄░█▀░▄░░░░░░░▀▀░░█ █░▄░█▀░▄░░░░░░░▀▀░░█
 *█░░▀▀▀▀░░░░░░░░░░░░█ █░░▀▀▀▀░░░░░░░░░░░░█
 *█░░░░░░░░░░░░░░░░░░█ █░░░░░░░░░░░░░░░░░░█
 *█░░░░░░░░░░░░░░░░░░█ █░░░░░░░░░░░░░░░░░░█
 *░█░░▄▄░░▄▄▄▄░░▄▄░░█░░█░░▄▄░░▄▄▄▄░░▄▄░░█░
 *░█░▄▀█░▄▀░░█░▄▀█░▄▀░░█░▄▀█░▄▀░░█░▄▀█░▄▀░
 *░░▀░░░▀░░░░░▀░░░▀░░░░░▀░░░▀░░░░░▀░░░▀░░░
 * 
 *   ANNOYING DOG MENGINVASI KODE PROGRAM
 */
package aiutils;

/**
 * Kelas kuliah. Atau mata kuliah. Atau matkul. Apapun itulah sebutannya
 * 
 */
public class StudyClass {
    private final int internalId;
    private final int classNum;
    private final String className;
    private final String [] roomConstraint;
    private final TimeConstraint tConstraint;
    private final short sks;
    
    /**
     * Buat sebuah kelas kuliah baru
     * @param internID kodifikasi internal kelas yang kita tahu. attribut ini sebisa mungkin harus unik. (bukan id kelas sesungguhnya) 
     * @param classNum nomor kelas (satu mata kuliah punya banya nomor kelas mungkin, misalnya IF3170 ada classNum 1 dan 2)
     * @param name nama mata kuliahnya
     * @param sks banyaknya sks mata kuliahnya / panjang slot yang harus dialokasi
     * @param roomConstraint konstrain ruangan (boleh di ruangan mana aja kelas kuliah ini?). Yang diisi id ruangannya aja ya~
     * @param timeConstraint konstrain waktu (boleh jam berapa sampe jam berapa kuliah ini? BTW masukkannya TimeConstraint ya, yang komponennya ada MyTime[] dan boolean[]) 
     */
    public StudyClass(int internID, int classNum, String name, short sks, 
            String [] roomConstraint, TimeConstraint timeConstraint){
        internalId = internID;
        this.classNum = classNum;
        className = name;
        this.sks = sks;
        this.roomConstraint = roomConstraint; 
        this.tConstraint = timeConstraint;
    }
    
    /**
     * Get Internal id. 
     * @return Internal id. Lihat konstruktor
     */
    public int getInternalID(){
        return internalId;
    }
    
    /**
     * Get Class id
     * @return class id. Lihat konstruktor
     */
    public int getClassId(){
        return classNum;
    }
    
    /**
     * Get Class name
     * @return class name. Lihat konstruktor
     */
    public String getClassName(){
        return className;
    }
    
    /**
     * Get id ruangan konstrain
     * @return duplikat list id ruangan konstrain. Lihat konstruktor
     */
    public String [] getRoomConstraint(){
        return roomConstraint.clone();
    }
    
    /**
     * Get Time Constraint
     * @return duplikat time constrain. lihat konstruktor.
     */
    public TimeConstraint getTimeConstraint(){
        return tConstraint.getCopy();
    }
    
    /**
     * Get Sks
     * @return sks. Ga usah lihat konstruktor. 
     */
    public short getLength(){
        return sks;
    }
    ///Ahhh coba semua modul kayak gini doang
}
