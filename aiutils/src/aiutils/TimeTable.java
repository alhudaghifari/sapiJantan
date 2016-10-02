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
 * Untuk kalian, maka diriku persembahkan KELAS TIMETABLE. <br>
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
    private Random randomizer;
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
                    
                    private TimeSlotElement(){
                        slotId = -99;
                        slotLength = 0;
                        classInternalId = -99;
                        isTaken = false;
                    }
                    
                    private TimeSlotElement(int slot_id, int slotLen, int classIntID){
                        slotId = slot_id;
                        slotLength = slotLen;
                        classInternalId = classIntID;
                        isTaken = true;
                    }
                    
                    private synchronized void ChangeValue(int new_slot_id, int new_slotLen, int new_classID){
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
                            for(int i = 0; i < q_el.size(); i++){
                                q_el.add(
                                        new TimeSlotElement(another.q_el.get(i).slotId,
                                                another.q_el.get(i).slotLength, 
                                                another.q_el.get(i).classInternalId));
                            }
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
                   //dupli.q_el.addAll(this.q_el);
                   for(int i = 0; i < q_el.size(); i++){
                       dupli.q_el.add(
                               new TimeSlotElement(q_el.get(i).slotId,
                                       q_el.get(i).slotLength, 
                                       q_el.get(i).classInternalId));
                   }
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
                    if(isEmpty()){
                        q_el.peek().ChangeValue(slotID, slotLen, classInternID);
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
                    TimeSlotElement[] dupli = q_el.toArray(new TimeSlotElement[q_el.size()]);
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
                        //mungkin bakal jadi kosong...
                        if(q_el.isEmpty()){
                            q_el.add(new TimeSlotElement());
                        }
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
           private final int maxEffectiveSlot;
           private final int endSlot;
           private final int startSlot;
           //Tiap ruangan punya layer masing-masing. Okeh...
           
           public TimeTableLayer(){
               layer = null;
               eligibleStatus = null;
               maxEffectiveSlot = 0;
               endSlot = 0;
               startSlot = 0;
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
                   int iter1;
                   for(iter1 = 0; iter1 < rowMax; iter1++){
                       layer[iter][iter1] = new TimeSlot();
                   }
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
               maxEffectiveSlot = endEligible - startEligible +1;
               startSlot = startEligible;
               endSlot = endEligible;
           }
           
           public int getMaxEffectiveSlot(){
               return maxEffectiveSlot;
           }
           /**
            * 
            * @return 
            */
           public TimeTableLayer getCopy(){
               TimeTableLayer dupli = new TimeTableLayer();
               dupli.layer = new TimeSlot[colMax][rowMax];
               dupli.eligibleStatus = new boolean[colMax][rowMax];
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
           public int[] getRandomValidSlot(int sks, int constrBegin, int constrEnd, boolean[] constrDay){
               //Random randomizer = new Random(System.currentTimeMillis());
               
               int begin_c = constrBegin - defaultStartTime;
               int end_c = constrEnd - defaultStartTime;
               if(!(sks > maxEffectiveSlot || begin_c >= endSlot || end_c <= startSlot)){
                    begin_c = Math.max(begin_c, startSlot);
                    int iterSlotLimit = Math.min(end_c, endSlot) - sks;
                    if(iterSlotLimit < begin_c)
                        return null;
                    else{
                        boolean[] day_c = constrDay.clone();
                        int rouletteOut = randomizer.nextInt();
                        if(rouletteOut < 0)
                            rouletteOut = -1*rouletteOut;
                        if(rouletteOut > 10000)
                            rouletteOut %= 10000;
                        int iterSlot = begin_c;
                        int iterDay = 0;
                        while(rouletteOut > 0){
                            //skip iterDay yang jatuh pada konstrain kelas = false
                            if(iterDay < rowMax-1){
                                iterDay++;
                                rouletteOut--;
                            }
                            else{
                                iterDay = 0;
                                iterSlot++;
                                if(iterSlot == iterSlotLimit || iterSlot == colMax || iterSlot == end_c)
                                    iterSlot = begin_c;
                                rouletteOut--;
                            }
                        }
                        //jika berhenti di nilai yang false, cari sel paling dekat yang punya
                        //nilai true. Atau, kalo semuanya false, maka return null
                        int step = 0;
                        int limit = rowMax * colMax; //avoid infinit loop
                        while((!eligibleStatus[iterSlot][iterDay] || !day_c[iterDay]) && step <= limit){
                            if(iterDay < rowMax-1 ){
                                iterDay++;
                                step++;
                            }
                            else{
                                iterDay = 0;
                                iterSlot++;
                                if(iterSlot == iterSlotLimit || iterSlot == colMax || iterSlot == end_c)
                                    iterSlot = begin_c;
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
                    
               }
               else return null;
           }

           /**
            * Isi slot spesifik dari sebuah slot yang ada di ruangan/layer ini. Method ini akan 
            * mengecek bahwa semua slot adalah valid (dapat diisi oleh kelas). Valid dalam artian
            * mematuhi TimeConstraint dari kelas dan semua slot berada di ruangan yang terbuka (artinya
            * eligiblestatus = true)
            * @param day hari
            * @param timeStart waktu mulai
            * @param slotId id slot 
            * @param slotLen panjang slot
            * @param classInternId kode kelas internal yang ada di antrian GlobalUtils.
            */
           public boolean FillSpecificSlot(int day, int timeStart, 
                   int slotId, int slotLen, int classInternId){
               boolean valid = true;
               //cek semua bakal slot yang diisi memenuhi konstrain waktu dan ruangan terbuka
               StudyClass sc = GlobalUtils.searchClassById(classInternId);
               if(sc != null){
                   int timeLimit_s = sc.getTimeConstraint().getTimeConstr()[0].getHour() - defaultStartTime;
                   int timeLimit_e = sc.getTimeConstraint().getTimeConstr()[1].getHour() - defaultStartTime;
                   int timeLimit_e_m = sc.getTimeConstraint().getTimeConstr()[1].getMinute();
                   if(timeLimit_e_m > 0)
                       timeLimit_e++;
                   boolean dayLimit = sc.getTimeConstraint().getDayConstr()[day];
                   int limit = timeStart + slotLen - 1;
                   if(timeStart >= timeLimit_s && limit < timeLimit_e && dayLimit 
                           && eligibleStatus[timeStart][day]){
                       //cek semua bakal slot yang diisi berada di ruangan yang terbuka
                       if(limit  < colMax){
                            for(int i = 0; i < slotLen; i++){
                                TimeSlot ts  = layer[timeStart + i][day];
                                ts.SetSlotValue(slotId, slotLen, classInternId);                   
                             }                
                        }
                       else{
                             valid = false;
                        }
                   }
                   else valid = false;
               }
               else valid = false;
               return valid;
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
                           sum += GlobalUtils.Combinatorial(retSize, 2);
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
               if(coordinate.length == 1){
                   int i = 0;
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
                   //dan harus dihapus juga yang bawah-bawahnya sepanjang slot length
                   int len = ret.getSlotLength();
                   int iter = 0;
                   while(iter < len && pos[0]+iter < colMax){
                       layer[pos[0]+iter][pos[1]].RemoveSlot(slotId);
                       iter++;
                   }
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
                   j = 0;
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
        randomizer = new Random(System.currentTimeMillis());
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
        //for debuggin
        for(iterSCQueue = 0; iterSCQueue < countSC; iterSCQueue++){
            //Random roomRandomizer = new Random(System.currentTimeMillis());
            int rR = randomizer.nextInt();
                if(rR < 0)
                    rR = -1*rR;
            rR %= layers.size();
            //cek konstrain ruangan dari kelas yang diperiksa, jika rR tidak memenuhi konstrain ruangan
            //maka cari ruangan terdekat yang dapat memenuhi konstrain
            boolean repeatFindingRoom = true;
            int rFR = 0;
            while(repeatFindingRoom && rFR <= layers.size()){
                int roomConL = aCopySCQueue[iterSCQueue].getRoomConstraint().length;
                if(roomConL > 0){
                    boolean roomValid = false;
                    int step = 0;
                    while(!roomValid && step < layers.size()){
                        int k = 0;
                        while(k < roomConL && !roomValid){
                            //cek apakah ada di antara sekian banyak konstrain ruangan, ruangan rR
                            //memenuhi konstrain ruangan kelas & cukup untuk menampung sks kelas
                             if(aCopySRList[rR].getRoomId().equals(
                                     aCopySCQueue[iterSCQueue].getRoomConstraint()[k])
                                     && layers.get(rR).getMaxEffectiveSlot() >= aCopySCQueue[iterSCQueue].getLength())
                                 roomValid = true;
                             else
                                 k++;
                        }
                        if(!roomValid){
                            rR = (rR+1)%layers.size();
                            step++;
                        }
                    }
                }
                //cari slot random
                TimeConstraint tc_iterated= aCopySCQueue[iterSCQueue].getTimeConstraint().getCopy();
                TimeTableLayer layerThisRoom = layers.get(rR);
                boolean valid = false;
                while(!valid){
                    int[] tr = 
                            layerThisRoom.getRandomValidSlot(aCopySCQueue[iterSCQueue].getLength(),
                                    tc_iterated.getTimeConstr()[0].getHour(), 
                                    tc_iterated.getTimeConstr()[1].getHour(),
                                    tc_iterated.getDayConstr());
                    if(tr != null){
                        valid = layerThisRoom.FillSpecificSlot(tr[1], tr[0], slotId, 
                        aCopySCQueue[iterSCQueue].getLength(), 
                        aCopySCQueue[iterSCQueue].getInternalID());
                        repeatFindingRoom = false;
                    }
                    else{
                        rR = (rR+1)%layers.size();
                        rFR++;
                        break;
                    }
                }
            }        
            slotId++; 
        }
    }
    
    /**
     * Ini digunakan untuk TimeTable yang berisi null. Hati-hati.
     * @param arbitrary berapapun angkanya, tetep hasilnya timetable yang berisi null.
     */
    public TimeTable(int arbitrary){
        layers = null;
    }
    
    /**
     * Dapatkan hasil dari deep clone TimeTable ini. Artinya hasil duplikat dari TimeTable ini
     * dijamin tidak mengandung referensi apapun ke TimeTable yang aslinya (dengan kata lain:
     * alokasi resource baru)
     * @return yaa.... deep clone. Kan tadi udah dibilang
     */
    public TimeTable getCopy(){
        TimeTable dupli = new TimeTable(0);
        dupli.layers = new LinkedList<>();
        for(int i = 0; i < layers.size(); i++){
            dupli.layers.add(layers.get(i).getCopy());
        }
         return dupli;
    }
    
    /**
     * Kelas TimeTable yang lebih simpel.
     * Pada intinya, kelas ini berisi list dengan elemen berupa tipe bentukan (IdInternalKelas, arrayPosisiKelas),
     * dengan arrayPosisiKelas menyatakan dimana kelas tersebut dijadwal. arrayPosisiKelas adalah sebuah array dengan
     * tiga elemen (slotdimulai, hari, indeksruangan), dengan indeksruangan menyatakan indeks ruangan di kontainer list
     * ruangan, sRoomList, di GlobalUtils.<br><br>
     * Catatan: kelas ini belum tentu menjamin validitas IdInternalKelas, slotdimulai, hari, dan indeksruangan. (dengan kata lain,
     * tanggung jawab si pengguna kelas yang memasukkan data-data tersebut sebagai atribut kelas ini).<br><br>
     * Catatan:  TimeTable.Simplified me-list semua entri (timeslot) yang ada dalam TimeTable. Artinya, jika ada satu 
     * mata kuliah yang punya 4 sks, maka kuliah tersebut akan muncul 4 kali di dalam list TimeTable (dengan koordinat yang berbeda, 
     * misal [0,0,0],[1,0,0],[2,0,0],[3,0,0] (format: slot, day, room), namun koordinat slot dipastikan selalu berurutan), 
     * dan mungkin tersebar di seluruh list.
     */
    public class Simplified{
        private class SimplifiedElement{
            private int sC;
            private int[] coordinate;
            private SimplifiedElement(int _classInternalId, int _slot, int _day, int _room){
                sC = _classInternalId;
                coordinate = new int[3];
                coordinate[0] = _slot;
                coordinate[1] = _day;
                coordinate[2] = _room;
            }
        }
        private LinkedList<SimplifiedElement> list;
        
        /* Konstruk bentuk yang lebih simpel.*/
        private Simplified(){
            //iterasi tiap layer, slot, dan day untuk mendapat masing-masing kelas
            list = new LinkedList<>();
            int itLayer = 0, itSlot = 0, itDay = 0;
            int maxSlot = getLayerMaxSlot()[0];
            int maxDay = getLayerMaxSlot()[1];;
            while(itLayer < layers.size()){
                itSlot = 0;
                while(itSlot < maxSlot){
                    itDay = 0;
                    while(itDay < maxDay){
                        if(!layers.get(itLayer).layer[itSlot][itDay].isEmpty()){
                            int maxEl = layers.get(itLayer).layer[itSlot][itDay].q_el.size();
                            int i;
                            for(i = 0; i < maxEl; i++)
                                list.add(new SimplifiedElement(layers.get(itLayer).layer[itSlot][itDay].q_el.get(i).classInternalId, 
                                    itSlot, itDay, itLayer));
                        }
                        itDay++;
                    }
                    itSlot++;
                }
                itLayer++;
            }
        }
        
        /**
         * Buat sebuah TimeTable versi yang tersimpelkan dari parameter-parameter yang ada.
         * Catatan: Urutan elemen-elemen dari StudyClassInternalID, positionSlot, positionDay, dan positionRow sangat diperhatikan
         * karena metod ini akan mengkonstruk sebuah TimeTable Simplified baru berdasarkan indeks elemen-elemen tersebut. Misalnya,
         * indeks pertama dari positionSlot akan dianggap sebagai lokasi slot dari StudyClassInternalID indeks pertama, begitupun
         * dengan positionDay dan positionRoom, dan seterusnya.<br><br> 
         * Catatan: Metod ini tidak akan memasukkan StudyClassInternalID yang tidak
         * memiliki positionSlot atau positionDay atau positionRoom yang berkorelasi (terjadi apabila panjang 
         * positionSlot/positionDay/positionRoom lebih kecil daripada panjang StudyClassInternalID)<br><br>
         * Catatan: metod ini juga tidak akan mengecek validitas isi dari masing-masing parameter. 
         * @param StudyClassInternalID array dari StudyClassInternalID.
         * @param positionSlot array dari posisi slot
         * @param positionDay array dari posisi hari
         * @param positionRoom array dari posisi ruangan
         */
        public Simplified(int[] StudyClassInternalID, int[] positionSlot, int[] positionDay, int[] positionRoom){
            int iter;
            list = new LinkedList<>();
            //cari panjang minimum di antara keempat parameter
            int maxIter = Math.min(StudyClassInternalID.length, 
                    Math.min(positionRoom.length, 
                            Math.min(positionSlot.length, positionDay.length)));
            //iterasi dari awal hingga maxiter
            for(iter = 0; iter < maxIter; iter++){
                list.add(new SimplifiedElement(StudyClassInternalID[iter],
                    positionSlot[iter], positionDay[iter], positionRoom[iter]));
            }
        }
        
        /**
         * Buat sebuah TimeTable baru berdasarkan TimeTable versi yang lebih simpel ini. 
         * Belum diimplementasikan. (Namun definisi metod udah final).
         * @return TimeTable baru
         */
        public TimeTable makeTimeTable(){
            TimeTable.Simplified dupli = stripDown();
            int[] duplimiss = dupli.getMissingClass(); //debug
            //buat sebuah TimeTable kosong, lalu inisialisasi semua layernya.
            TimeTable ret = new TimeTable(0);
            StudyRoom[] aCopySRRoom = GlobalUtils.getStudyRoomListCopy();
            ret.layers = new LinkedList<>();
            ret.randomizer = new Random(System.currentTimeMillis());
            for(int i = 0; i < aCopySRRoom.length; i++){
                ret.layers.add(new TimeTableLayer(aCopySRRoom[i]));
            }
            //iteratif baca satu-satu list dupli yang ada, masukkan ke ret
            int slotID = 1;
            for(int i = 0; i < dupli.getSize(); i++){
                StudyClass sc = GlobalUtils.searchClassById(dupli.getStudyClassInternalId(i));
                int scSks = sc.getLength();
                int[] scpos = dupli.getStudyClassPosition(i, true);
                boolean he = ret.layers.get(scpos[2]).FillSpecificSlot(scpos[1], scpos[0], slotID, scSks, sc.getInternalID());
                slotID++;
                TimeTable.Simplified T = ret.getSimplified().stripDown();
                int[] Tmiss = T.getMissingClass();
                int ik = 0;
            }
            return ret;
        }
        /**
         * Dapatkan banyaknya elemen yang ada dalam TimeTable.Simplified ini
         * @return banyaknya elemen yang ada dalam TimeTable.Simplified ini
         */
        public int getSize(){
            return list.size();
        }
        
        
        /**
         * Dapatkan Study Class Internal id (id yang kita bikin sendiri) di index tertentu.
         * Karena pada dasarnya kelas ini adalah list yang berisi (StudyClass internal id, slot, day, room).
         * @param index suatu index
         * @return Id internal kelas yang ada di index tersebut.
         */
        public int getStudyClassInternalId(int index){
            return list.get(index).sC;
        }
        
        /**
         * Dapatkan sebuah "posisi" yang berbentuk array tiga elemen (slot, day, room) berdasarkan index tertentu.
         * @param index suatu index.
         * @param clone jika parameter ini di set true maka yang didapatkan adalah hasi clone dari array yang aslinya. 
         * Jika tidak, maka gak dilakukan clone terlebih dahulu.
         * @return sebuah array tiga elemen dengan indeks 0 menyatakan slot, 1 menyatakan day, dan 2 menyatakan room.
         * Room disini maksudnya adalah no urut / indeks ruangan yang ada di kontainer sRoomList di GlobalUtils. Misalnya,
         * jika di sRoomList indeks no 5 terdapat ruangan 2222 dan room = 5, maka artinya room tersebut adalah ruangan 2222.
         */
        public int[] getStudyClassPosition(int index, boolean clone){
            if(clone)
                return list.get(index).coordinate.clone();
            else
                return list.get(index).coordinate;
        }
        
        /**
         * Dapatkan sebuah "posisi" yang berbentuk array tiga elemen (slot, day, room) berdasarkan sebuah id internal kelas.
         * Pada dasarnya, metod ini melakukan searching biasa yang primitif (iteratif dari awal list).  
         * @param classInternalID suatu index.
         * @param clone jika parameter ini di set true maka yang didapatkan adalah hasi clone dari array yang aslinya. 
         * Jika tidak, maka gak dilakukan clone terlebih dahulu.
         * @return sebuah array tiga elemen dengan indeks 0 menyatakan slot, 1 menyatakan day, dan 2 menyatakan room.
         * Room disini maksudnya adalah no urut / indeks ruangan yang ada di kontainer sRoomList di GlobalUtils. Misalnya,
         * jika di sRoomList indeks no 5 terdapat ruangan 2222 dan room = 5, maka artinya room tersebut adalah ruangan 2222.
         * Namun, jika ternyata tidak ada classInternalID yang dimaksud, maka kembaliannya adalah array satu elemen nonsense,
         * yaitu [-1]. Jika ada lebih dari satu elemen yang punya id internal kelas classInternalID, maka yang dikembalikan
         * adalah elemen yang ditemukan pertama kali.
         */
        public int[] getStudyClassPositionByID(int classInternalID, boolean clone){
            int index = 0; boolean found = false;
            while(index < list.size() && !found){
                if(list.get(index).sC != classInternalID)
                    index++;
                else
                    found = true;
            }
            if(found){
                return getStudyClassPosition(index, clone);
            }
            else{
                int[] hoho = new int[1];
                hoho[0] = -1;
                return hoho;
            }
        }
        
        /**
         * Method untuk menyingkirkan entri-entri dengan id internal kelas yang sama dalam sebuah TimeTable.Simplified, lalu
         * memberikan sebuah TimeTable.Simplified baru yang berisi semua entri id internal kelas yang berbeda dan koordinat
         * entri yang paling rendah.
         * TimeTable.Simplified me-list semua entri (timeslot) yang ada dalam TimeTable. Artinya, jika ada satu 
         * mata kuliah yang punya 4 sks, maka kuliah tersebut akan muncul 4 kali di dalam list TimeTable (dengan koordinat yang berbeda, 
         * misal [0,0,0],[1,0,0],[2,0,0],[3,0,0] (format: slot, day, room), namun koordinat slot dipastikan selalu berurutan), 
         * dan mungkin tersebar di seluruh list. Method ini mempertahankan entri mata kuliah tersebut yang memiliki koordinat slot 
         * yang paling rendah (dalam contoh diatas yang dipertahankan adalah yang punya koordinat [0,0,0]), dan membuang 3 kemunculan
         * yang lainnya dalam sebuah TimeTable.Simplified yang baru.
         * @return TimeTable.Simplified baru yang gak mengandung referensi ke TimeTable.Simplified lama.
         */
        public TimeTable.Simplified stripDown(){
            LinkedList<Integer> l_internID = new LinkedList<>();
            LinkedList<Integer> l_start = new LinkedList<>();
            LinkedList<Integer> l_positionDay = new LinkedList<>();
            LinkedList<Integer> l_positionRoom = new LinkedList<>();
            //baca iteratif dari awal list sampai akhir untuk menggolongkan data.
            for(int i = 0; i < list.size(); i++){
                int id_iterated = list.get(i).sC;
                int ts_iterated = list.get(i).coordinate[0];
                if(!l_internID.contains(id_iterated)){
                    l_internID.add(id_iterated);
                    l_start.add(ts_iterated);
                    l_positionDay.add(list.get(i).coordinate[1]);
                    l_positionRoom.add(list.get(i).coordinate[2]);
                }
                else{
                    int idx = l_internID.indexOf(id_iterated);
                    l_start.set(idx, Math.min(ts_iterated, l_start.get(idx)));
                }
            }
            //convert ke array int.
            Integer[] idWrap = l_internID.toArray(new Integer[l_internID.size()]);
            int[] arrID = new int[idWrap.length];
            for(int i = 0; i < arrID.length; i++){
                arrID[i] = idWrap[i];
            }
            Integer[] PosSlotWrap = l_start.toArray(new Integer[l_start.size()]);
            int[] arrPosSlot = new int[PosSlotWrap.length];
            for(int i = 0; i < arrPosSlot.length; i++){
                arrPosSlot[i] = PosSlotWrap[i];
            }
            Integer[] PosDayWrap = l_positionDay.toArray(new Integer[l_positionDay.size()]);
            int[] arrPosDay = new int[PosDayWrap.length];
            for(int i = 0; i < arrPosDay.length; i++){
                arrPosDay[i] = PosDayWrap[i];
            }
            Integer[] PosRoomWrap = l_positionRoom.toArray(new Integer[l_positionRoom.size()]);
            int[] arrPosRoom = new int[PosRoomWrap.length];
            for(int i = 0; i < arrPosRoom.length; i++){
                arrPosRoom[i] = PosRoomWrap[i];
            }
            //return sebuah TimeTable.Simplified baru
            return new TimeTable.Simplified(arrID, arrPosSlot, arrPosDay, arrPosRoom);
        }
        
        /**
         * Dapatkan semua id internal kelas yang mungkin tidak terjadwal.
         * Ada kemungkinan dari data yang diinput user, ada kelas yang terpaksa tidak terjadwal. Gunakan
         * method ini untuk mendapatkan array dengan elemen-elemennya adalah id internal kelas yang tidak
         * terjadwal
         * @return semua id internal kelas yang tidak terjadwal. Jika semua kelas berhasil terjadwal, maka yang dikembalikan
         * adalah array kosong.
         */
        public int[] getMissingClass(){
            //dapatkan dulu copy dari hasil stripdown tt ini
            TimeTable.Simplified copyOfThis = this.stripDown();
            StudyClass[] scCopy = GlobalUtils.getStudyClassQueueCopy();
            int[] scCopyIntId = new int[scCopy.length];
            for(int i = 0; i < scCopyIntId.length; i++){
                scCopyIntId[i] = scCopy[i].getInternalID();
            }
            Arrays.sort(scCopyIntId);
            int[] arrOfId = new int[copyOfThis.list.size()];
            for(int i = 0; i< arrOfId.length; i++){
                arrOfId[i] = copyOfThis.list.get(i).sC;
            }
            Arrays.sort(arrOfId);
            //sekarang, secara iteratif bandingkan tiap elemen dari array yang punya semua
            //internal id dalam kontainer global dengan array yang punya semua id dalam timetable ini
            int sizeOfRet = scCopyIntId.length - arrOfId.length;
            int[] ret = new int[sizeOfRet];
            int iterRet = 0;
            int iterArrOId = 0;
            for(int i = 0; i < scCopyIntId.length; i++){
                if(scCopyIntId[i] != arrOfId[iterArrOId]){
                    ret[iterRet] = scCopyIntId[i];
                    if(iterArrOId < arrOfId.length -1)
                        i++;
                    iterRet++;
                }
                if(iterArrOId < arrOfId.length - 1){
                        iterArrOId++;
                }
            }
            return ret;
        }
        
    }
    
    /**
     * Dapatkan bentuk yang lebih simpel dari TimeTable partikular ini yang ngejelimet.
     * Bentuknya seperti ini: <br>
     * 1. (StudyClassInternalID) (slotwaktumulai) (hari) (ruangan) <br>
     * 2. (StudyClassInternalID) (slotwaktumulai) (hari) (ruangan) <br>
     * dan seterusnya
     * @return sebuah TimeTable yang bentuknya seperti deskripsi (bukan yang berlayer-layer)
     */
    public TimeTable.Simplified getSimplified(){
        return new Simplified();
    }

    /*
     * 
     * @param timeslotID
     * @param layernum
     * @param new_pos_slot
     * @param new_pos_day
     * @param new_layernum
     * @return 
     *
    private TimeTable move(int timeslotID, int layernum, int new_pos_slot,
            int new_pos_day, int new_layernum){
        //search for position
        TimeTable dupli = getCopy();
        return dupli;
    }*/
    
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
     * Dapatkan semua internal id kelas yang menempati posisi tertentu dalam TimeTable.
     * @param slot posisi slot (dari 0 - 11, 0 menyatakan jam 7 pagi)
     * @param day posisi hari (dari 0 - 4, 0 menyatakan senin)
     * @param room posisi ruangan (urutan ruangan sama dengan yang ada di kontainer sRoomList di GlobalUtils)
     * @return semua internal id kelas yang menempati slot tersebut. Jika posisi tersebut kosong / ga ada, yang
     * diberikan adalah array satu elemen [-99].
     */
    public int[] getClassInternalIDByPosition(int slot, int day, int room){
       int roommax = layers.size();
       int[] slotmax = getLayerMaxSlot();
       if(slot < slotmax[0] && day < slotmax[1] && room < roommax ){
            int retSize = layers.get(room).layer[slot][day].q_el.size();
            int[] ret = new int[retSize];
            for(int i = 0; i < retSize; i++){
                ret[i] = layers.get(room).layer[slot][day].q_el.get(i).classInternalId;
            }
            return ret;
       }
       else return new int[]{-99};
    }
    /**
     * Sama kayak getSpesificSlotLocationBySlotId, tapi parameternya adalah TimeSlot !!!Jangan dipakai!!!.
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
        private int info_id; //informasi time slot yang di"sayang" oleh engine partikular ini.
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
        
        /*
         * TimeSlot ts HARUS VALID.
         * @param ts 
         */
        private gnttEngine(TimeSlot ts){
            //dapetin semua info dari ts dari timetable ini.
            int[] aArray = TimeTable.this.getSpesificSlotLocation(ts);
            info_id = ts.getSlotId();
            if(aArray.length == 3)
                fillAttribute(aArray);
            else{
                int why = 0;
            }
        }
        
        /*
         * 
         * @param timeSlotId 
         */
        private gnttEngine(int timeSlotId){
            int[] aArray = TimeTable.this.getSpesificSlotLocationById(timeSlotId);
            info_id = timeSlotId;
            if(aArray.length == 3)
                fillAttribute(aArray);
            else{
                int why = 0;
            }
        }
        
        /*
         * 
         * @param info_t
         * @param maxCol
         * @param maxRow
         * @return 
         */
        private LinkedList<TimeTable> moveToAllPosThisLayer(TimeTable info_t, int maxCol, int maxRow, int level){
            LinkedList<TimeTable> res = new LinkedList<>();
            TimeTable dupli = null;
            int icol = 0, irow;
            while(icol < maxCol){
                irow = 0;
                while(irow < maxRow){
                    if(!(icol == info_pos_slot && irow == info_pos_day && level == info_pos_layer)){
                        //buat TimeTable baru yang merupakan hasil modif dari info_t
                        dupli = info_t.getCopy();
                        //pindahkan slot dari posisi (info_pos_slot, info_pos_day) ke (icol,irow)
                        int timeslotID = info_id;
                        int new_pos_slot = icol;
                        int new_pos_day = irow;
                        int new_layernum = level;
                        int layernum = info_pos_layer;
                        //artifak: method TimeTable.move.
                        //**int[] posSlot = dupli.getSpesificSlotLocationById(timeslotID);
                        //abis itu pindahin: remove dulu baru add
                        //**if(posSlot.length == 3){
                            //**if(new_pos_slot != posSlot[0] && new_pos_day != posSlot[1] && new_layernum != posSlot[2]){
                        TimeSlot ret = dupli.layers.get(layernum).removeSlotById(timeslotID);
                        boolean valid = dupli.layers.get(new_layernum).FillSpecificSlot(new_pos_day, 
                             new_pos_slot, ret.getSlotId(), ret.getSlotLength(), ret.getClassInternalId());  
                            //**}
                        //**}
                        //jika berhasil mindahin slot maka append ke list result
                        if(valid)
                            res.add(dupli);
                        dupli = null;
                    }
                    irow++;
                }
                icol++;
            }
            return res;
        }
        
        
        /*
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
                result.addAll(moveToAllPosThisLayer(dupliOri, layerCol, layerRow, level));
                level++;
            }
            level = info_pos_layer-1;
            while(level >= 0){
                result.addAll(moveToAllPosThisLayer(dupliOri, layerCol, layerRow, level));
                level--;
            }
        }
        
        private LinkedList<TimeTable> getResult(){
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
        LinkedList<gnttEngine> engineCollection = new LinkedList<>();
        //TimeTable.Simplified sim = getSimplified().stripDown();
        for(int i = 0; i < countClass; i++){
            int id = findTimeSlotIdByClassInternalId(aSCCopy[i].getInternalID());
            if(id != -99)
                engineCollection.add(new gnttEngine(id));
        }
        //start gnttEngine
        int eSize = engineCollection.size();
        Thread[] eCThread = new Thread[eSize];
        for(int i = 0; i < eSize; i++){
            eCThread[i] = new Thread(engineCollection.get(i));
        }
        for(int i = 0; i < eSize; i++){
            eCThread[i].start();
        }
        for(int i = 0; i < eSize; i++){
            try {
                eCThread[i].join();
            } catch (InterruptedException ex) {
                System.out.println(ex.toString());
                break;
            }
        }
        //koleksi semua hasilnya
        LinkedList<TimeTable> result = new LinkedList<>();
        for(int i = 0; i < eSize; i++){
                result.addAll(engineCollection.get(i).getResult());
        }
        TimeTable[] resultArr = result.toArray(new TimeTable[result.size()]);
        return resultArr;
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
Kok jadi panjang gini :v
*/

