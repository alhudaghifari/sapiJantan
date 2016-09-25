/*
ga ada gambar. sori yak
Lisensi tetep... 1 DoNt c4r3
 */
package aiutils;
import java.util.Arrays;
//import java.util.Iterator;
import java.util.Random;
import java.util.LinkedList;
//import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Untuk kalian, maka diriku persembahkan KELAS TIMETABLE <br>
 * Tanpa banyak bacot, inilah representasi status kita, dengan mengandung banyak TIMESLOT sebagai 
 * representasi variabel utama (diisikan dengan kelas kuliah (domain))yang dikelompokkan menjadi 
 * beberapa TIMETABLELAYER (variabel sekunder?) sesuai dengan ruangan yang disediakan (domain juga). <br>
 * Hehe. Im fabulous. Tapi kayaknya banyak yang kurang. Nanti bakal diperbarui, tenang aja ;)<br><br>
 * 
 * TimeTable sejatinya adalah sebuah linked list yang berisi semua layer yang mewakili setiap ruangan
 * yang ada di sRoomList (di GlobalUtils). Setiap layer (disebut TimeTableLayer) adalah
 * matriks yang mewakili struktur tabel waktu buka setiap ruangan dengan perhitungan hari buka
 * default Senin-Jumat dan jam buka default setiap harinya adalah 07.00 - 18.00 (12 jam). Setiap
 * sel matriks adalah sebuah multivalued TimeSlot, sehingga memungkinkan lebih dari satu kelas 
 * mata kuliah diassign ke TimeSlot yang sama.
 */
public class TimeTable{
    
    /**
     * Kelas ini adalah struktur internal dari TimeTable dan dipakai utamanya di 
     * TimeTableLayer (juga struktur internal dari TimeTable). Catatan: Sebisa mungkin
     * jangan gunakan metod-metod ada yang ada di dalam kelas ini di dalam modul selain
     * TimeTable.java, kecuali kalau benar-benar kepepet. Dokumentasi ada supaya kode modul ini
     * lebih gampang dipahami.
     */
    private class TimeSlot{
                private class TimeSlotElement{
                    private int slotId; //harus dijamin unik
                    private int slotLength;
                    private int classInternalId;
                    private boolean isTaken;
                    
                    public TimeSlotElement(){
                        slotId = -99;
                        slotLength = 0;
                        classInternalId = -99;
                        isTaken = false;
                    }
                    
                    public TimeSlotElement(int slot_id, int slotLen, int classIntID){
                        slotId = slot_id;
                        slotLength = slotLen;
                        classInternalId = classIntID;
                        isTaken = true;
                    }
                    
                    public synchronized void ChangeValue(int new_slot_id, int new_slotLen, int new_classID){
                        slotId = new_slot_id;
                        slotLength = new_slotLen;
                        classInternalId = new_classID;
                        isTaken = true;
                    }
                    
                }
                private LinkedList<TimeSlotElement> q_el; 
                
                /**
                * Inisialisasi TimeSlot dengan atribut nonsense (artinya kosong).
                * Lebih detilnya, menciptakan sebuah linked list yang berisi TimeSlotElement dengan
                * atribut-atributnya nonsense seragam (untuk mencerminkan keadaan kosong dari sebuah
                * TimeSlot)
                */
                public TimeSlot(){
                    q_el = new LinkedList<>();
                    q_el.add(new TimeSlotElement());
                }
                
                /**
                 * Inisialisasi TimeSlot baru dengan TimeSlot lain yang sudah ada
                 * @param another TimeSlot lain yang sudah ada. Gak bakal ada kejadian apa-apa
                 * kalau parameter ini adalah TimeSlot yang sama.
                 */
                public TimeSlot(TimeSlot another){
                    if(!this.equals(another)){
                        synchronized(this){
                            q_el.clear();
                            q_el.addAll(another.q_el);
                        }
                    }
                }
                
                /**
                 * Deep copy/Deep clone dari objek TimeSlot ini, sehingga TimeSlot hasil duplikasi
                 * independen terhadap objek TimeSlot ini (gak memiliki referensi apapun ke objek ini). 
                 * @return Sebuah TimeSlot hasil deepClone.
                 */
                public TimeSlot getCopy(){
                   TimeSlot dupli = new TimeSlot();
                   dupli.q_el.clear();
                   dupli.q_el.addAll(this.q_el);
                   return dupli;
                }
                
                /*protected TimeSlot(boolean donothhing){
                    //do nothing
                }*/

                /**
                 * Buat TimeSlot menjadi makes sense dengan mengisi atribut yang makes sense. 
                 * Jika TimeSlot tepat memiliki satu elemen, maka elemen tersebut diubah isinya dengan
                 * parameter-parameter yang ada, namun jika tidak (kosong atau lebih dari satu), maka yang
                 * dilakukan adalah mengappend elemen tersebut di ekor list.
                 * @param slotID id slot yang unik. Yang ngebuat id slot unik adalah kerjaannya si TimeTable
                 * @param slotLen panjang blok slot
                 * @param classInternID kode rahasia kelas program kita. Pssst! Malu ah
                 */
                public void SetSlotValue(int slotID, int slotLen, int classInternID){
                    if(q_el.size() == 1){
                        q_el.peek().ChangeValue(slotID, slotLen, slotID);
                    }
                    else
                        q_el.add(new TimeSlotElement(slotID, slotLen, classInternID));
                }

                /**
                 * Dapatkan Id Slot. Catatan: Karena TimeSlot adalah multivalue, maka bisa jadi TimeSlot
                 * memiliki banyak IdSlot di dalamnya (ini menandakan bentrok).
                 * @return identifier dari slot ini. Kelas TimeTable yang menjamin semua slot yang terisi
                 * memiliki id unik. Namun, jika slot punya lebih dari satu kelas, maka yang dikembalikan adalah -1
                 */
                public int getSlotId(){
                    if(q_el.size() > 1)
                        return -1;
                    else
                        return q_el.peek().slotId;
                }

                /**
                 * Dapatkan panjang slot. 
                 * @return negatif jika ada lebih dari satu kelas pada slot yang sama, panjang slot jika tidak
                 */
                public int getSlotLength(){
                    if (q_el.size() > 1)
                        return -99;
                    else
                        return q_el.peek().slotLength;
                }
                
                /**
                 * Hitung banyaknya TimeSlotElement yang mengisi slot ini.
                 * Bisa digunakan untuk menentukan bentrok.
                 * @return banyaknya TimeSlotElement yang mengisi slot ini
                 */
                public int getSlotLengthSize(){
                    return q_el.size();
                }
                /**
                 * Dapatkan internal id kelas yang digunakan sebagai kodifikasi
                 * kelas internal dalam penyimpanan di dalam antrian kelas. 
                 * @return -99 jika ada lebih dari satu kelas pada satu slot yang sama, 
                 * jika tidak maka internal id kelas yang dikembalikan
                 */
                public int getClassInternalId(){
                    if(q_el.size() > 1)
                        return -99;
                    else
                        return q_el.peek().classInternalId;
                }

                /**
                 * Kosongkan slot.
                 */
                public void EmptySlotValue(){
                    q_el.clear();
                    q_el.add(new TimeSlotElement());
                }

                /**
                 * Periksa kekosongan slot.
                 * @return true jika kosong.
                 */
                public boolean isEmpty(){
                    return q_el.size() == 1 && q_el.peek().slotId == -99;
                }
                
                /**
                 * Periksa apakah sebuah Slot (TimeSlotElement lebih tepatnya) yang memiliki id sId
                 * ada dalam TimeSlot ini. Metod ini ada karena sebuah TimeSlot bisa memiliki banyak slot id.
                 * @param sId TimeSlot Id yang ingin diperiksa keberadaannya.
                 * @return true jika ketemu.
                 */
                public synchronized boolean isContainedHere(int sId){
                    TimeSlotElement[] dupli = (TimeSlotElement[])q_el.toArray();
                    int i = 0; boolean found = false;
                    while(i < dupli.length && !found){
                        if(dupli[i].slotId == sId)
                            found = true;
                        else
                            i++;
                    }
                    return found;
                }
                
                /**
                 * 
                 * @param sId
                 * @return 
                 */
                public synchronized int elementPos(int sId){
                    int i = 0; boolean found = false;
                    while (i < q_el.size() && !found){
                        if(q_el.get(i).slotId != sId){
                            i++;
                        }
                        else found = true;
                    }
                    if(found)
                        return i;
                    else return -99;
                }
                
                /**
                 * 
                 */
                public synchronized TimeSlot RemoveSlot(int slotid){
                    int pos = elementPos(slotid);
                    TimeSlot ret = new TimeSlot();
                    if(pos != -99){
                        TimeSlotElement rem = q_el.remove(pos);
                        ret.SetSlotValue(rem.slotId, rem.slotLength, rem.classInternalId);
                    }
                    return ret;
                }
                
                /**
                 * 
                 * @param classInId
                 * @return 
                 */
                public int findSlotIdByClassId(int classInId){
                    int i = 0; boolean found = false;
                    int ret = -99;
                    while(i < q_el.size() && !found){
                        if(q_el.get(i).classInternalId == classInId){
                            ret = q_el.get(i).slotId;
                            found = true;
                        }
                        else{
                            i++;
                        }
                    }
                    return ret;
                }
    }
    
    /**
    * Karena desain rumit status ruang kita yang terdiri dari berlapis lapis time table
    * maka kelas ini adalah representasi lapisan time table, dengan semua slot masih kosong.
    * Berapa lapis? ratusan! lebih!
    * Catatan: lebih baik jangan gunakan metod TimeTableLayer apapun di modul selain modul ini.
    * Dokumentasi ada hanya agar kode dapat lebih dipahami. Itu aja.
    */
    private class TimeTableLayer{
           private final static int rowMax = 5; //dihitung senen sampe jumat. Kyut Kyut
           private final static int colMax = 12; //dihitung dari jam 7 sampe jam 18. Meong
           private final static int defaultStartTime = 7;
           private final static int defaultEndTime = 18;
           private TimeSlot[][] layer;
           private boolean[][] eligibleStatus; //false jika ruangan ditutup
           //Tiap ruangan punya layer masing-masing. Okeh...
           
           public TimeTableLayer(){
               layer = null;
               eligibleStatus = null;
           }
           
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
           
           /**
            * 
            * @return 
            */
           public TimeTableLayer getCopy(){
               TimeTableLayer dupli = new TimeTableLayer();
               dupli.layer = new TimeSlot[colMax][rowMax];
               //duplikasi isi layer dan eligible status TimeTableLayer ini ke TimeTableLayer dupli
               for(int i = 0; i < colMax; i++){
                   for(int j = 0; j < rowMax; j++){
                       dupli.layer[i][j] = this.layer[i][j].getCopy();
                       dupli.eligibleStatus[i][j] = this.eligibleStatus[i][j];
                   }
               }
               return dupli;
           }
           
           /**
            * Dapetin posisi slot random yang dapat diisi 
            * @return Posisi slot random yang dijamin memenuhi konstrain ruangan/layer ini. Null jika tidak ada
            */
           public int[] getRandomValidSlot(){
               Random randomizer = new Random(System.currentTimeMillis());
               int rouletteOut = randomizer.nextInt() % 1000;
               int iterSlot = 0;
               int iterDay = 0;
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
                   int[] ret = new int[2];
                   ret[0] = iterSlot;
                   ret[1] = iterDay;
                   return ret;
               }
               else return null;
           }

           /**
            * Isi slot spesifik dari sebuah slot yang ada di ruangan/layer ini
            * @param day hari
            * @param timeStart waktu mulai
            * @param slotId id slot 
            * @param slotLen panjang slot
            * @param classInternId kode kelas internal yang ada di antrian GlobalUtils.
            */
           public void FillSpecificSlot(int day, int timeStart, 
                   int slotId, int slotLen, int classInternId){
               layer[timeStart][day].SetSlotValue(slotId, slotLen, classInternId);
               for(int i = 1; i < slotLen; i++)
                   layer[timeStart + 1][day].SetSlotValue(slotId, slotLen, classInternId);
           }
           
           /**
            * 
            * @param day
            * @param timeStart 
            */
           public void EmptySpecificSlot(int day, int timeStart){
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
           public int[] getSlotDayMaxValue(){
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
                           if(layer[col][rowIter].isContainedHere(slotId))
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
           
           /**
            * Hapus slot yang ber-id slotId dari layer ini. Gak ngelakuin apapun jika ternyata di layer ini
            * gak ada slotId.
            * @param slotId 
            */
           public TimeSlot removeSlotById(int slotId){
               TimeSlot ret = new TimeSlot();
               int[] pos = getSpesificSlotPosition(slotId);
               //jika ada, dan koordinatnya ketemu, maka remove.
               if(pos.length == 2){
                   ret = layer[pos[0]][pos[1]].RemoveSlot(slotId);
               }
               return ret;
           }
           
           /**
            * 
            * @param classInternalId
            * @return 
            */
           public int findSlotIdByClassInternalId(int classInternalId){
               //Iterasi setiap elemen dalam atribut layer untuk mencari slotId.
               int i = 0, j = 0; boolean found = false;
               int ret = -99;
               while(i < colMax && !found){
                   while(j < rowMax && !found){
                       if((ret = layer[i][j].findSlotIdByClassId(classInternalId)) == -99)
                            j++;
                       else{
                           found = true;
                       }
                   }
                   if(!found)
                       i++;
               }
               return ret;
           }
       //
    }

    //lapisan layer ruangan ditumpuk berdasar urutan dari list ruangan yang ada
    //di global utils
    private LinkedList<TimeTableLayer> layers;
   
    /**
     * Konstruk TimeTable dengan semua kelas kuliah telah terassign ke 
     * timetable secara random.
     * TimeTable membaca salinan data dari sClassQueue dan sRoomList yang ada di
     * GlobalUtils supaya sClassQueue dan sRoomList tidak "terganggu". 
     * Setelah itu, TimeTable mengassign kelas yang ada di sClassQueue ke dalam 
     * slot yang tersedia dengan memperhatikan konstrain masing-masing 
     * ruangan yang ada di dalama sRoomList. Artinya, semua kelas walaupun 
     * ditempatkan di slot secara random, namun ditempatkan di ruangan yang 
     * pasti buka di slot tersebut. Selain itu, TimeTable menjamin 
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
            int[] tr = layerThisRoom.getRandomValidSlot();
            layerThisRoom.FillSpecificSlot(tr[1], tr[0], slotId, 
                    aCopySCQueue[iterSCQueue].getLength(), 
                    aCopySCQueue[iterSCQueue].getInternalID());
            //aCopySchedStatus, rasanya ga butuh ini
            slotId++; 
        }
    }
    
    private TimeTable(int arbitrary){
        layers = null;
    }
    
    /**
     * Dapatkan hasil dari deep clone TimeTable ini.
     * @return yaa.... deep clone. Kan tadi udah dibilang
     */
    public TimeTable getCopy(){
        TimeTable dupli = new TimeTable(0);
        dupli.layers = new LinkedList<>();
        for(int i = 0; i < layers.size(); i++){
            dupli.layers.add(this.layers.get(i).getCopy());
        }
        return dupli;
    }
    
    /**
     * 
     * @param timeslotID
     * @param layernum
     * @param new_pos_slot
     * @param new_pos_day
     * @param new_layernum
     * @return 
     */
    public TimeTable move(int timeslotID, int layernum, int new_pos_slot,
            int new_pos_day, int new_layernum){
        //search for position
        TimeTable dupli = getCopy();
        int[] posSlot = dupli.getSpesificSlotLocationById(timeslotID);
        //abis itu pindahin: remove dulu baru add
        if(posSlot.length == 3){
            if(new_pos_slot != posSlot[0] && new_pos_day != posSlot[1] && new_layernum != posSlot[2]){
                TimeSlot ret = dupli.layers.get(layernum).removeSlotById(timeslotID);
                dupli.layers.get(layernum).FillSpecificSlot(new_pos_day, 
                        new_pos_slot, ret.getSlotId(), ret.getSlotLength(), ret.getClassInternalId());  
            }
        }
        return dupli;
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
     * Sama kayak getSpesificSlotLocationBySlotId, tapi parameternya adalah TimeSlot.
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
            return layers.getFirst().getSlotDayMaxValue();
        }
    }
    
    //Sebuah kelas yang digunakan dalam generateNeighboringTimeTable. udah itu aja
    protected class gnttEngine implements Runnable{
        private TimeSlot info; //informasi time slot yang di"sayang" oleh engine partikular ini.
        //posisi info di timetable
        private int info_pos_slot; 
        private int info_pos_day;
        private int info_pos_layer; //layer berapa?
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
         * @param info_t
         * @param maxCol
         * @param maxRow
         * @return 
         */
        public LinkedList<TimeTable> moveToAllPosThisLayer(TimeTable info_t, int maxCol, int maxRow){
            LinkedList<TimeTable> result = new LinkedList<>();
            TimeTable dupli = null;
            int icol = 0, irow = 0;
            while(icol < maxCol){
                while(irow < maxRow){
                    if(icol != info_pos_slot && irow != info_pos_day){
                        //buat TimeTable baru yang merupakan hasil modif dari info_t
                        dupli = info_t.getCopy();
                        //pindahkan slot dari posisi (info_pos_slot, info_pos_day) ke (icol,irow)
                        dupli.move(info.getSlotId(), info_pos_layer, icol, irow, info_pos_layer);
                        //append ke list result
                        result.add(dupli);
                        dupli = null;
                    }
                    irow++;
                }
                icol++;
            }
            return result;
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
                result.addAll(moveToAllPosThisLayer(dupliOri, layerCol, layerRow));
                level++;
            }
            level = info_pos_layer-1;
            while(level >= 0){
                result.addAll(moveToAllPosThisLayer(dupliOri, layerCol, layerRow));
                level--;
            }
        }
        
        public LinkedList<TimeTable> getResult(){
            return new LinkedList<>(result);
        }
    }
    
    /**
     * Dapatkan sebuah slot id yang berisi kelas yang memiliki internal id internalClassId.
     * Slot id tersebut bisa di layer manapun dan waktu kapanpun yang terdefinisi di TimeTable ini.
     * @param internalClassId parameter id internal kelas. Lihat modul StudyClass
     * @return true jika ketemu.
     */
    public int findTimeSlotIdByClassInternalId(int internalClassId){
        //Traversal setiap layer untuk mendapatkan slotid.
        int i = 0; boolean found = false;
        int ret = -99;
        while (i < layers.size() && !found){
            if((ret = layers.get(i).findSlotIdByClassInternalId(internalClassId)) == -99)
                i++;
            else
                found = true;
        }
        return ret;
    }
    
    /**Membuat semua kemungkinan state tetangga (TimeTable) dari state (TimeTable) saat ini. <br>
     * Definisi state tetangga: state yang merupakan hasil dari pemindahan sebuah StudyClass ke slot lain yang ada
     * dari slot (TimeTable) saat ini. Slot lain tersebut bisa berada di ruangan (layer) yang sama maupun di
     * ruangan(layer) yang berbeda. <br>
     * Dalam implementasinya, metod ini (lagi-lagi) membaca salinan data dari GlobalUtils, lalu membuat 
     * thread sejumlah banyaknya kelas yang ada di sClassQueue (artinya, satu thread untuk satu kelas). Thread
     * tersebut akan membuat semua kemungkinan state tetangga untuk tiap kelas tersebut. 
     * Semua kemungkinan state tersebut disimpan ke sebuah array, karena itu ukuran array bisa jadi sangat besar.
     * @return sebuah array TimeTable yang berisi semua kemungkinan state tetangga.
     */
    public TimeTable[] generateNeighboringTimeTable(){
        //hitung banyaknya kelas yang ada di list antrian kelas
        int countClass = GlobalUtils.CountStudyClass();
        StudyClass[] aSCCopy = GlobalUtils.getStudyClassQueueCopy();
        //berdasarkan banyaknya slot yang terisi, buat gnttEngine
        gnttEngine[] engineCollection = new gnttEngine[countClass];
        for(int i = 0; i < countClass; i++){
            engineCollection[i] = new gnttEngine(findTimeSlotIdByClassInternalId(aSCCopy[i].getInternalID()));
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

