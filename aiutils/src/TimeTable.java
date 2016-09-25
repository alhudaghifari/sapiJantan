/*
ga ada gambar. sori yak
Lisensi tetep... 1 DoNt c4r3
 */
package aiutils;
import java.util.Arrays;
import java.util.Random;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Untuk kalian, maka diriku persembahkan KELAS TIMETABLE <br>
 * Tanpa banyak bacot, inilah representasi status kita, dengan mengandung banyak TIMESLOT sebagai 
 * representasi variabel utama (diisikan dengan kelas kuliah (domain))yang dikelompokkan menjadi 
 * beberapa TIMETABLELAYER (variabel sekunder?) sesuai dengan ruangan yang disediakan (domain juga). <br>
 * Hehe. Im fabulous. Tapi kayaknya banyak yang kurang. Nanti bakal diperbarui, tenang aja ;)
 * 
 */
public class TimeTable{
    
    protected class TimeSlot{
                private int slotId; //harus dijamin unik
                private ConcurrentLinkedQueue<Short> slotLength;
                private ConcurrentLinkedQueue<Integer> classInternalId;
                private boolean isTaken;
                /**
                * Inisialisasi TimeSlot dengan atribut nonsense (artinya kosong)
                */
                public TimeSlot(){
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
                public synchronized void SetSlotValue(int slotID, short slotLen, int classInternID){
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

                public int getSlotLengthSize(){
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
                public synchronized void EmptySlotValue(){
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
    
    /**
    * Karena desain rumit status ruang kita yang terdiri dari berlapis lapis time table
    * maka kelas ini adalah representasi lapisan time table, dengan semua slot masih kosong.
    * Berapa lapis? ratusan! lebih!
    */
    protected class TimeTableLayer{
           private final static int rowMax = 5; //dihitung senen sampe jumat. Kyut Kyut
           private final static int colMax = 12; //dihitung dari jam 7 sampe jam 18. Meong
           private final static int defaultStartTime = 7;
           private final static int defaultEndTime = 18;
           private TimeSlot[][] layer;
           private boolean[][] eligibleStatus; //false jika ruangan ditutup
           //Tiap ruangan punya layer masing-masing. Okeh...
           
           /**
            * Inisialisasi semua atribut dari sebuah layer dari kubus status 3D ciamik
            * kita yang kena bencana alam itu, tanpa mengambil data dari antrian kelas global
            * Hehe
            * Im fabulous (insert some fabulous cat ascii art here)
            * @param r, ruangan yang direpresentasikan dari layer ini
            */
           public TimeTableLayer(StudyRoom r){
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

           /*
            * Dapetin posisi slot random yang dapat diisi 
            * @return Posisi slot random yang dijamin memenuhi konstrain ruangan/layer ini. Null jika tidak ada
            */
           public short[] getRandomValidSlot(){
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
           public void FillSpecificSlot(short day, short timeStart, 
                   int slotId, short slotLen, int classInternId){
               layer[timeStart][day].SetSlotValue(slotId, slotLen, classInternId);
               for(int i = 1; i < slotLen; i++)
                   layer[timeStart + 1][day].SetSlotValue(slotId, slotLen, classInternId);
           }
           
           public void EmptySpecificSlot(short day, short timeStart){
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
           
           /**
            * 
            * @return 
            */
           public int[] geSlotDayMaxValue(){
               int[] stats = new int[2];
               stats[0] = colMax;
               stats[1] = rowMax;
               return stats;
           }
           
           /**
            * 
            * @return [-1] jika tidak ada slot yang dimaksud, [slotwaktu][hari] jika ada 
            */
           public int[] getSpesificSlotPosition(int slotId){
               //searching seluruh slot pada layer untuk mencara slot dengan id slotId
               int[] coordinate = new int[1];
               coordinate[0] = -1;
               if(slotId != -99){
                   boolean found = false;
                   int col = 0; int row = 0;
                   while(col < colMax && !found){
                       int rowIter = 0;
                       while(rowIter < rowMax && !found){
                           if(layer[col][rowIter].getSlotId() == slotId)
                               found = true;
                           else
                               rowIter++;
                       }
                       if(!found)
                           col++;
                       else
                           row = rowIter;
                   }
                   if(found){
                       coordinate = new int[2];
                       coordinate[0] = col;
                       coordinate[1] = row;
                   }
               }
               return coordinate;
           }
       //
    }

    //lapisan layer ruangan ditumpuk berdasar urutan dari list ruangan yang ada
    //di global utils
    private LinkedList<TimeTableLayer> layers;
   
    /**
     * Konstruk TimeTable dengan semua kelas kuliah telah terassign ke timetable secara random.
     * TimeTable membaca salinan data dari sClassQueue dan sRoomList yang ada di GlobalUtils, mengassign
     * kelas yang ada di sClassQueue ke dalam slot yang tersedia dengan memperhatikan konstrain masing-masing 
     * ruangan yang ada di dalama sRoomList. Artinya, semua kelas walaupun ditempatkan di slot secara random,
     * namun ditempatkan di ruangan yang pasti buka di slot tersebut. Selain itu, TimeTable menjamin 
     * keunikan identifier masing-masing slot yang terisi kelas.
     */
    public TimeTable(){
        //Retrieve copy tabel antrian dan daftar ruangan
        StudyClass[] aCopySCQueue = GlobalUtils.getStudyClassQueueCopy();
        //boolean[] aCopySchedStatus = GlobalUtils.getStudyClassScheduledStatus();
        StudyRoom[] aCopySRList = GlobalUtils.getStudyRoomListCopy();
        int countRoom = aCopySRList.length;
        //Inisialisasi bentuk 3D tererosi kita
        int iterSCQueue, /*iterSchedStatus,*/ iterRoom;
        layers = new LinkedList<>();
        for(iterRoom = 0; iterRoom < countRoom; iterRoom++){
            layers.add(new TimeTableLayer(aCopySRList[iterRoom]));
        }
        //Hmm... Waktunya assigning kelas yang mau dijadwal secara random
        //iterSchedStatus = 0;
        int countSC = aCopySCQueue.length;
        int slotId = 1;
        for(iterSCQueue = 0; iterSCQueue < countSC; iterSCQueue++){
            Random roomRandomizer = new Random(System.currentTimeMillis());
            int rR = roomRandomizer.nextInt() % layers.size();
            TimeTableLayer layerThisRoom = layers.get(rR);
            short[] tr = layerThisRoom.getRandomValidSlot();
            layerThisRoom.FillSpecificSlot(tr[1], tr[0], slotId, 
                    aCopySCQueue[iterSCQueue].getLength(), 
                    aCopySCQueue[iterSCQueue].getInternalID());
            //aCopySchedStatus, rasanya ga butuh ini
            slotId++; 
        }
    }
    
    /**
     * Dapatkan hasil dari deep clone TimeTable ini.
     * @return yaa.... deep clone. Kan tadi udah dibilang
     */
    public TimeTable getCopy(){
        ///hiyaaaaah
    }
    
    /**
     * Cari lokasi spesifik dari suatu timeSlotId.
     * Yang dimaksud lokasi spesifik adalah tuple(slotwaktu, hari, ruangan)
     * @param timeSlotId id dari timeSlotId.
     * @return Array integer tiga elemen, yaitu [slotwaktu, hari, ruangan]. Jika tidak ada 
     * timeSlotId yang dimaksud, maka hasilnya adalah sebuah array nonsense, yaitu [-1].
     */
    public int[] getSpesificSlotLocationById(int timeSlotId){
        //searching dari layer ke layer untuk menemukan spesificlocation
        int layernum = 0;
        int[] coordinate = new int[1];
        boolean found = false;
        int layerMax = layers.size();
        while(layernum < layerMax && !found){
            coordinate = layers.get(layernum).getSpesificSlotPosition(timeSlotId);
            if(coordinate.length == 1){
                layernum++;
            }
            else{
                found = true;
            }
        }
        if(found){
            int[] realCoordinate = new int[3];
            realCoordinate[0] = coordinate[0];
            realCoordinate[1] = coordinate[1];
            realCoordinate[2] = layernum;
            return realCoordinate;
        }
        else{
            coordinate = new int[1];
            coordinate[0] = -1;
            return coordinate;
        }
    }
    
    /**
     * Sama kayak getSpesificSlotLocationBySlotId, tapi parameternya adalah TimeSlot
     * Metod ini sebenernya cuma useful di kelas TimeTable, Trollololololol
     * @param ts TimeSlot
     * @return sama kayak gSSLBSI.
     */
    public int[] getSpesificSlotLocation(TimeSlot ts){
        return getSpesificSlotLocationById(ts.getSlotId());
    }
    
    /**
     * Mendapatkan colMax (maksimum banyaknya slot yang ada dalam satu hari) dan 
     * rowMax (maksimum hari). Semua ruangan selalu memiliki tuple(colMax, rowMax) yang sama.
     * @return Array integer dua elemen, [colMax, rowMax]
     */
    public int[] getLayerMaxSlot(){
        if(layers.isEmpty()){
            int[] hehe = new int[1];
            hehe[0] = -1;
            return hehe;
        }
        else{
            return layers.getFirst().geSlotDayMaxValue();
        }
    }
    
    //Sebuah kelas yang digunakan dalam generateNeighboringTimeTable. udah itu aja
    protected class gnttEngine implements Runnable{
        TimeSlot info; //informasi time slot yang di"sayang" oleh engine partikular ini.
        //posisi info di timetable
        int info_pos_slot; 
        int info_pos_day;
        int info_pos_layer; //layer berapa?
        LinkedList<TimeTable> result;
        
        private void fillAttribute(int[] aArray){
            info_pos_slot = aArray[0];
            info_pos_day = aArray[1];
            info_pos_layer = aArray[2];
            result = new LinkedList<>();
        }
        
        /**
         * TimeSlot ts HARUS VALID.
         * @param ts 
         */
        public gnttEngine(TimeSlot ts){
            //dapetin semua info dari ts dari timetable ini.
            int[] aArray = TimeTable.this.getSpesificSlotLocation(ts);
            fillAttribute(aArray);
        }
        
        /**
         * 
         * @param timeSlotId 
         */
        public gnttEngine(int timeSlotId){
            int[] aArray = TimeTable.this.getSpesificSlotLocationById(timeSlotId);
            fillAttribute(aArray);
        }
        
        /**
         * 
         */
        @Override
        public void run() {
            //dapetin fresh deep copy dari timetable, supaya enak dimodif.
            TimeTable dupliOri = TimeTable.this.getCopy();
            //dimulai dari posisi info saat ini, buat semua timetable tetangga dengan urutan
            //pindahkan ke slot yang masih berada dalam satu layer.
            //pindahkan ke slot yang berada dalam layer-layer dibawahnya
            //pindahkan ke slot yang berada dalam layer-layer diatasnya.
            int[] getLayerStat = TimeTable.this.getLayerMaxSlot();
            int layerCol = getLayerStat[0];
            int layerRow = getLayerStat[1];
            int thickness = TimeTable.this.layers.size();
            int level = info_pos_layer;
            while(level < thickness){
                ////hiyaahhh
                level++;
            }
            level = info_pos_layer;
            while(level >= 0){
                ////hiyaaahhhh
                level--;
            }
            throw new UnsupportedOperationException("Not supported yet."); 
        }
        
        public LinkedList<TimeTable> getResult(){
            
        }
    }
    
    /**
     * Dapatkan
     * @param internalClassId
     * @return 
     */
    public int findTimeSlotIdByClassInternalId(int internalClassId){
        
    }
    
    /**Membuat semua kemungkinan state tetangga (TimeTable) dari state (TimeTable) saat ini. <br>
     * Definisi state tetangga: state yang merupakan hasil dari pemindahan sebuah StudyClass ke slot lain yang ada
     * di slot (TimeTable) saat ini. 
     * Semua kemungkinan state tersebut disimpan ke sebuah array, karena itu ukuran array bisa jadi sangat besar.
     * @return sebuah array TimeTable yang berisi semua kemungkinan state tetangga.
     */
    public TimeTable[] generateNeighboringTimeTable(){
        //hitung banyaknya kelas yang ada di list antrian kelas
        int countClass = GlobalUtils.CountStudyClass();
        //berdasarkan banyaknya slot yang terisi, buat gnttEngine
        gnttEngine[] engineCollection = new gnttEngine[countClass];
        for(int i = 0; i < countClass; i++){
            engineCollection[i] = new gnttEngine(findTimeSlotIdByClassInternalId(i));
        }
        //start gnttEngine
        for(int i = 0; i < countClass; i++){
            engineCollection[i].run();
        }
        //tunggu sampai masing-masing thread gnttEngine berhenti
        
        //koleksi semua hasilnya
        LinkedList<TimeTable> result = new LinkedList<>();
        for(int i = 0; i < countClass; i++){
            result.addAll(engineCollection[i].getResult());
        }
        
        return (TimeTable[]) result.toArray();
    }

    /**
     * Hitung total konflik dari time table status ini.
     * @return total konflik
     */
    public int CountTotalConflict(){
        int i;
        int sum = 0;
        int len = layers.size();
        for(i = 0; i < len; i++){
            sum += layers.get(i).CountConflictThisLayer();
        }
        return sum;
    }
}

/*
           ▄▄         ▄██▄          ▄▄                   
         ▀████▀     ▄▄▄▄▄▄▄▄      ▀████▀                 
     ▄▄▄▄    ▀▀▄▄▀           ▀▄▄  ▀▀ ▄▄▄▄                                                   
   ▄██████   ▄▀    ▄▄      ▄▄   ▀▄  ██████▄
  ████▄▄     █   ▄████    ████▄   █  ▄▄▄████
 ████████    █  ███▄█▀    ▀█▄███  █   █████████
    ▀██████ █ █▄▄▄            ▄▄▄█ █   ██████▀
    ▀▀▀▀▀▀   █  ▀██▄▄▄  ▄  ▄▄██▀   █  ▀▀▀▀▀▀
              █    ██████████     █                      
     ▄███      ▀▄  ▀  ▀██▀  ▀    █   ███▄
      █████      ▀▄            ▄▀   █████
       ███▀▄██▄▄   ▀▀▄▄▄▄▄▄▄▄▀▀  ▄▄▄██▄▀███
       ███ ████████▄▄▄▄    ▄▄▄▄█████████  ███
         ▀▀██████▀▀▀▀        ▀▀▀▀██████▀▀
                         ▄▄
                       ████
                        ████
                           ████▄
                            ▀████
                             ████
                         ▄██████
                 █    ▄███████▀   █
                 ▀▄  ▀▀▀▀▀▀      ▄▀
                     ▀▀▀▀▀▀▀▀▀﻿
HIE HE HE HE HE HE HE HE HE HIE HE HE HE HE HIE HE HEH
*/

