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
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Time Slot
 * Deskripsi lengkap di kemudian hari. 
 */
public class TimeSlot {
    private int slotId;
    private ConcurrentLinkedQueue<Short> slotLength;
    private ConcurrentLinkedQueue<Integer> classInternalId;
    private boolean isTaken;
    /**
     * Inisialisasi TimeSlot dengan atribut nonsense (artinya kosong)
     */
    protected TimeSlot(){
        slotId = -99;
        slotLength = new ConcurrentLinkedQueue<>();
        classInternalId = new ConcurrentLinkedQueue<>();
        isTaken = false;
    }
    
    protected TimeSlot(boolean donothhing){
        //do nothing
    }
    
    /**
     * Inisialisasi TimeSlot dengan atribut makes sense
     * @param slotID id slot. udah ga usah mikirin ini, kerjaannya si TimeTable
     * @param slotLen panjang blok slot
     * @param classInternID kode rahasia kelas program kita. Pssst! Malu ah
     */
    protected synchronized void SetSlotValue(int slotID, short slotLen, int classInternID){
        this.slotId = slotID;
        slotLength.add(slotLen);
        classInternalId.add(classInternID);
        isTaken = true;
    }
    
    /**
     * Dapatkan Id Slot. Penawaran terbatas. Ayo buruan!!
     * @return identifier dari slot ini. Kelas TimeTable yang menjamin semua slot yang terisi
     * memiliki id unik.
     */
    public int getSlotId(){
        return slotId;
    }
    
    /**
     * Dapatkan panjang slot. 
     * @return negatif jika ada lebih dari satu kelas pada slot yang sama, panjang slot jika tidak
     */
    public short getSlotLength(){
        int size = slotLength.size();
        if (size > 1)
            return -99;
        else
            return slotLength.peek();
    }
    
    protected int getSlotLengthSize(){
        return slotLength.size();
    }
    /**
     * Dapatkan internal id kelas yang digunakan sebagai kodifikasi kelas internal dalam penyimpanan di dalam antrian.
     * @return -99 jika ada lebih dari satu kelas pada satu slot yang sama, jika tidak maka internal id kelas
     */
    public int getClassInternalId(){
        int size = classInternalId.size();
        if(size > 1)
            return -99;
        else
            return classInternalId.peek();
    }
    
    /**
     * Isi lagi slot dengan atribut non sense
     */
    protected synchronized void EmptySlotValue(){
        slotId = -99;
        slotLength.clear();
        classInternalId.clear();
        isTaken = false;
    }
    
    /**
     * Periksa kekosongan slot.
     * @return true jika kosong.
     */
    public boolean isEmpty(){
        return slotId < 0 || slotLength.isEmpty() || classInternalId.isEmpty(); 
    }
}
