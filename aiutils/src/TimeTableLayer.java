/*
 * Lisensi Aku cinta kamu. Muaaah
                 / \
                / _ \
               | / \ |
               ||   || _______
               ||   || |\     \
               ||   || ||\     \
               ||   || || \    |
               ||   || ||  \__/
               ||   || ||   ||
                \\_/ \_/ \_//
               /   _     _   \
              /               \
              |    O     O    |
              |   \  ___  /   |                           
             /     \ \_/ /     \
            /  -----  |  --\    \
            |     \__/|\__/ \   |
            \       |_|_|       /
             \_____       _____/
                   \     /
                   |     |
 * jelek ya kelinci nya. Peang pletat pletot gitu.
 */
package aiutils;

import java.util.Arrays;
import java.util.Random;

/**
 * Karena desain rumit status ruang kita yang terdiri dari berlapis lapis time table
 * maka kelas ini adalah representasi lapisan time table, dengan semua slot masih kosong.
 * Berapa lapis? ratusan! lebih!
 */
public class TimeTableLayer extends TimeSlot {
    private final static int rowMax = 5; //dihitung senen sampe jumat. Kyut Kyut
    private final static int colMax = 12; //dihitung dari jam 7 sampe jam 18. Meong
    private final static int defaultStartTime = 7;
    private final static int defaultEndTime = 18;
    private TimeSlot[][] layer;
    private boolean[][] eligibleStatus; //false jika ruangan ditutup
    //Tiap ruangan punya layer masing-masing. Okeh...
    
    /**
     *do nothing
     */
    protected TimeTableLayer(){
        super(true);
    }
    
    /**
     * Inisialisasi semua atribut dari sebuah layer dari kubus status 3D ciamik
     * kita yang kena bencana alam itu, tanpa mengambil data dari antrian kelas global
     * Hehe
     * Im fabulous (insert some fabulous cat ascii art here)
     * @param r, ruangan yang direpresentasikan dari layer ini
     */
    protected TimeTableLayer(StudyRoom r){
        //Inisialisasi layer
        layer = new TimeSlot[colMax][rowMax];
        int iter;
        for(iter = 0; iter < colMax; iter++){
            Arrays.fill(layer[iter], new TimeSlot());
        }
        //Inisialisasi tabel konstrain, ya...sebut aja itu lah
        eligibleStatus = new boolean[colMax][rowMax];
        for(iter = 0; iter < colMax; iter++){
            Arrays.fill(eligibleStatus[iter], true);
        }
        int startEligible = r.getConstraint().getTimeConstr()[0].getHour()
                - defaultStartTime; 
                //andaikan ada, misalnya dimulai dari 8.30, 
                //maka slot yang dipake tetep dari slot 8.00
        int endEligible = r.getConstraint().getTimeConstr()[1].getHour()
                - defaultStartTime;
        int endEligibleMinute = r.getConstraint().getTimeConstr()[1].getMinute();
                //andaikan ada, misalnya selesai jam 10.30, maka slot jam yang
                //10.00 ikut kepake. Moga aja ga ada aamiin
        if(endEligibleMinute > 0)
            endEligible += 1;
        //isi semua sel yang ga menuhi konstrain ruangan dengan false 
        for(iter = 0; iter < startEligible; iter++){
            Arrays.fill(eligibleStatus[iter], false);
        }
        for(iter = endEligible; iter < colMax ; iter++){
            Arrays.fill(eligibleStatus[iter], false);            
        }
        for(iter = 0; iter < rowMax; iter++){
            if(!r.getConstraint().getDayConstr()[iter]){
                int iterSlot;
                for(iterSlot = 0; iterSlot < colMax; iterSlot++)
                    eligibleStatus[iterSlot][iter] = false;
            }
        }
    }
    
    /**
     * Kasarnya: indeks maksimum baris 
     * @return banyaknya hari (baris = hari)
     */
    public static int getDayCount(){
        return rowMax;
    }
    
    /**
     * Indeks maksimum kolom
     * @return banyaknya slot maksimal dalam satu hari (kolom = slot)
     */
    public static int getSlotInDayCount(){
        return colMax;
    }
    
    /*
     * Dapetin posisi slot random yang dapat diisi 
     * @return Posisi slot random yang dijamin memenuhi konstrain ruangan/layer ini. Null jika tidak ada
     */
    protected short[] getRandomValidSlot(){
        Random randomizer = new Random(System.currentTimeMillis());
        int rouletteOut = randomizer.nextInt() % 1000;
        short iterSlot = 0;
        short iterDay = 0;
        while(rouletteOut > 0){           
            if(iterDay < rowMax){
                iterDay++;
                rouletteOut--;
            }
            else{
                iterDay = 0;
                iterSlot++;
                if(iterSlot == colMax)
                    iterSlot = 0;
                rouletteOut--;
            }
        }
        //jika berhenti di nilai yang false, cari sel paling dekat yang punya
        //nilai true. Atau, kalo semuanya false, maka return null
        int step = 0;
        int limit = rowMax * colMax; //avoid infinit loop
        while(!eligibleStatus[iterSlot][iterDay] && step <= limit){
            if(iterDay < rowMax){
                iterDay++;
                step++;
            }
            else{
                iterDay = 0;
                iterSlot++;
                if(iterSlot == colMax)
                    iterSlot = 0;
                step++;
            }
        }
        if(step <= limit){
            short[] ret = new short[2];
            ret[0] = iterSlot;
            ret[1] = iterDay;
            return ret;
        }
        else return null;
    }
    
    /*
     * Isi slot spesifik dari sebuah slot yang ada di ruangan/layer ini
     * @param day hari
     * @param timeStart waktu mulai
     * @param slotId id slot 
     * @param slotLen panjang slot
     * @param classInternId kode kelas internal yang ada di antrian GlobalUtils.
     */
    protected void FillSpecificSlot(short day, short timeStart, 
            int slotId, short slotLen, int classInternId){
        layer[timeStart][day].SetSlotValue(slotId, slotLen, classInternId);
        for(int i = 1; i < slotLen; i++)
            layer[timeStart + 1][day].SetSlotValue(slotId, slotLen, classInternId);
    }
    
    /*
     * 
     * @param day
     * @param timeStart 
     */
    protected void EmptySpecificSlot(short day, short timeStart){
        layer[timeStart][day].EmptySlotValue();
    }
    
    /**
     * Hitung banyaknya jadwal yang konflik dalam layer ini.
     * @return 
     */
    public int CountConflictThisLayer(){
        int sum = 0;
        int i, j;
        for(i = 0; i < colMax; i++){
            for(j = 0; j < rowMax; j++){
                int retSize = layer[i][j].getSlotLengthSize();
                if(retSize > 1)
                    sum += GlobalUtils.Combinatorial(2, retSize);
            }
        }
        return sum;
    }
}
