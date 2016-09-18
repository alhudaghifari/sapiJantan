/*
ga ada gambar. sori yak
Lisensi tetep... 1 DoNt c4r3
 */
package aiutils;
import java.util.Random;
import java.util.LinkedList;

/**
 * Untuk kalian, maka diriku persembahkan KELAS TIMETABLE <br>
 * Tanpa banyak bacot, inilah representasi status kita, dengan mengandung banyak TIMESLOT sebagai 
 * representasi variabel utama (diisikan dengan kelas kuliah (domain))yang dikelompokkan menjadi 
 * beberapa TIMETABLELAYER (variabel sekunder?) sesuai dengan ruangan yang disediakan (domain juga). <br>
 * Hehe. Im fabulous. Tapi kayaknya banyak yang kurang. Nanti bakal diperbarui, tenang aja ;)
 * 
 */
public class TimeTable extends TimeTableLayer {
    //lapisan layer ruangan ditumpuk berdasar urutan dari list ruangan yang ada
    //di global utils
    private LinkedList<TimeTableLayer> layers;
   
    /**
     * Konstruk TimeTable dengan semua kelas kuliah telah terassign ke timetable secara random.
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
        //Lah jadi ane yang bikin random
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
    
    /* duh ini susah. nanti dulu ya*/
    public TimeTable[] generateNeighboringTimeTable(){
        return new TimeTable[1]; //dummy
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

