/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aiutils;
import java.io.File;
import java.util.LinkedList;
import java.util.Scanner;

/**
 * Kelas GlobalUtils berisi member-member yang dapat diakses seluruh modul tanpa
 * harus diinstansiasi. Intinya adalah kelas ini mengandung kontainer list yang 
 * menampung seluruh kelas kuliah dan ruang kuliah hasil input user, beserta beberapa
 * metode yang berkaitan dengan inisiasi program dan kontainer-kontainer tersebut.
 * <br><br>
 * Catatan: Kontainer-kontainer list yang ada di modul ini ga boleh diubah isinya kecuali
 * pada saat pembacaan isi list dari input user. Oleh karena itu, apabila ingin mendapatkan
 * isi dari kontainer tersebut, maka yang diberikan adalah duplikatnya yang tidak mengandung
 * referensi ke yang aslinya.
 */
public final class GlobalUtils {
        //private static ConcurrentLinkedQueue<Integer> sClassInternalId;
        //private static ConcurrentLinkedQueue<StudyClass> sClassQueue;
        //private static ConcurrentLinkedQueue<Boolean> sClassScheduledStatus;
        //private static ConcurrentLinkedQueue<StudyRoom> sRoomList;
        private static LinkedList<StudyClass> sClassQueue = new LinkedList<>();
        private static LinkedList<StudyRoom> sRoomList = new LinkedList<>();
        private static int factinvoked = 0;
        
        
        /**
         * Kelas FileReader yang membaca file untuk struktur data StudyClass dan StudyRoom. 
         * Karena kelas ini yang membaca file dan memasukkan data ke kontainer sClassQueue dan sRoomList,
         * maka validitas data StudyClass dan StudyRoom (misalnya: id internal kelas yang harus unik) dijamin
         * oleh kelas ini. HAHAHA (evil laugh)
        */ 
        private static class fileReader{
            private String path;
            private String getPath(){
                return path;
            }

            private void setPath(String path) {
                this.path = path;
            }
            
            public fileReader(String path){
                this.path = path;
                read();
            }
            
            /**
             * Konstruk filereader dengan path yang nunjuk ke file default: "d:/Testcase.txt".
             * Ya...suka-suka yang ngeprogram sih file defaultnya kemana.
            */ 
            public fileReader(){
                path = "d:/Testcase.txt";
                read();
            }
            
            private synchronized void read(){
                //GlobalUtils global = new GlobalUtils();
                boolean room = false;
                boolean schedule = false;
                String temp;

                try{
                    Scanner inFile = new Scanner(new File(path)).useDelimiter("[\\r\\n]+");
                    while(inFile.hasNext()){
                        temp = inFile.next();
                        switch (temp) {
                            case "Ruangan":
                                room = true;
                                schedule = false;
                                break;
                            case "Jadwal":
                                room = false;
                                schedule = true;
                                break;
                        }

                        if(room && !temp.equals("Ruangan")) {
                            GlobalUtils.sRoomList.add(temp); //walah tempnya bukan tipe StudyRoom //global.setRoomStudy(temp);
                        }else if (schedule && !temp.equals("Jadwal")) {
                            GlobalUtils.sClassQueue.add(temp);
                        }

                    }

                }catch(Exception e){
                    System.out.println("WOWOW there is a mistake when reading the file..");
                }
            }
        }

        
        /**
         * Faktorial biasa. nothing fancy (ga nemu metod bawaan yang handle faktorial huwaaa)
         * @param n bilangan faktorial
         * @return 1 jika n kurang sama dengan 1, n! jika n lebih besar 1
         */
        public static int Factorial(int n){
            factinvoked++;
            if (n <= 1)
                return 1;
            else
                return n*Factorial(n-1);
        }
        
        /**
         * Kombinatorial biasa.
         * @param base basis. 
         * @param pair pasangan. Sebagai contoh penggunaan, kombinasi 2 dari 5 = Combinatorial(5,2) 
         * @return hasil kombinasi. 0 jika tidak ada (pair lebih besar base)
         */
        public static int Combinatorial(int base, int pair){
            if(pair > base)
                return 0;
            else
                return (Factorial(base))/(Factorial(pair)*Factorial(base-pair));
        }
        
        /**
         * Inisiasi kontainer StudyClass (sClassQueue), kontainer yang menyimpan semua kelas mata kuliah
         * yang akan dijadwalkan berdasarkan input user, dan kontainer StudyRoom (sRoomList), kontainer
         * yang menyimpan semua ruangan kuliah yang akan dijadwalkan berdasarkan input user. Defaultnya, 
         * kontainer-kontainer ini adalah sebuah list kosong sebelum dipanggil metod ini. 
         * Metod ini akan memanggil parser internal yang membaca file berisi input user, 
         * lalu parser tersebut yang memasukkan data ke dalam kontainer-kontainer ini.
         */
        public static void initialize(String path){
            //Some path checking here? Ga usah lah ya~
            sClassQueue.clear();
            sRoomList.clear();
            fileReader fr = new fileReader(path);
            fr.read();
        }
        
        /**
         * Inisiasi kontainer StudyClass (sClassQueue), kontainer yang menyimpan semua kelas mata kuliah
         * yang akan dijadwalkan berdasarkan input user. Defaultnya, kontainer ini adalah sebuah list kosong
         * sebelum dipanggil metod ini. Metod ini akan memanggil parser internal yang membaca file berisi
         * input user, lalu parser tersebut yang memasukkan data ke dalam kontainer ini.
         */
        public static void initStudyClassQueue(){
            sClassQueue.clear();
            fileReader fr = new fileReader();
            fr.read();
            sRoomList.clear(); //I know I know ini ga efisien, tapi mau gimana lagi nama metodnya begitu
        }
        
        /**
         * Inisiasi kontainer StudyRoom (sRoomList), kontainer yang menyimpan semua ruangan 
         * kuliah yang akan dijadwalkan berdasarkan input user. Defaultnya, kontainer ini 
         * adalah sebuah list kosong sebelum dipanggil metod ini. Metod ini akan memanggil 
         * parser internal yang membaca file berisi input user, lalu parser tersebut yang memasukkan
         * data ke dalam kontainer ini.
         */
        public static void initStudyRoomList(){
            sRoomList.clear();
            fileReader fr = new fileReader();
            fr.read();
            sClassQueue.clear(); //I know I know ini ga efisien, tapi mau gimana lagi nama metodnya begitu
        }
        
        /*
         * 
         * @param classNum
         * @param className
         * @return 
         
        public static int getClassInternalId(short classNum, String className){
            //dummy
            return 0;
        }*/
        
        /**
         * Mendapatkan hasil copy dari kontainer penyimpanan StudyClass (sClassQueue) yang gak mengandung
         * referensi apapun ke kontainer aslinya. Dengan begini, list StudyClass tetap bebas
         * dari modifikasi oleh metod lain. Btw, StudyClass mengandung apa hayo? Anak StudyClass :v
         * @return Sebuah duplikat kontainer penyimpanan StudyClass berbentuk array biasa.
         */
        public static StudyClass[] getStudyClassQueueCopy(){
            StudyClass[] dupli = sClassQueue.toArray(new StudyClass[sClassQueue.size()]);
            return dupli;       
        }
        
        /*public static boolean[] getStudyClassScheduledStatus(){
            //dummy
            return new boolean[1];
        }*/
        
        /**
         * Mendapatkan hasil copy dari kontainer penyimpanan StudyRoom (sRoomList) yang gak mengandung
         * referensi apapun ke kontainer aslinya. Dengan begini, list StudyRoom tetap bebas
         * dari modifikasi oleh metod lain.
         * @return Sebuah duplikat kontainer penyimpanan StudyClass berbentuk array biasa.
         */
        public static StudyRoom[] getStudyRoomListCopy(){
            StudyRoom[] dupli = sRoomList.toArray(new StudyRoom[sRoomList.size()]);
            return dupli;
        }
        
        /**
         * Hitung banyaknya kelas yang ada di dalam antrian kelas yang akan dijadwalkan berdasarkan input user.
         * @return banyaknya kelas dalam integer
         */
        public static int CountStudyClass(){
            return sClassQueue.size();
        }
        
        /**
         * Hitung banyaknya ruangan kelas yang tersedia berdasarkan input user.
         * @return banyaknya ruangan kelas dalam integer
         */
        public static int CountStudyRoom(){
            return sRoomList.size();
        }
        
        /**
         * Cari sebuah studyClass berdasarkan internalId. Hasil StudyClass yang diberikan metod ini
         * adalah StudyClass yang pertama kali ditemukan dalam kontainer global StudyClass. Jika tidak
         * ada, maka yang dikembalikan adalah null. Hati-hati ya
         * @param classInternId internal id kelas yang ingin dicari.
         * @return Study Class hasil pencarian. Jika kontainer kosong / kelas tidak ditemukan, null 
         */
        public static StudyClass searchClassById(int classInternId){
            boolean found = false; int k = 0;
            while(k < sClassQueue.size() && !found){
                   if(sClassQueue.get(k).getInternalID() == classInternId)
                       found = true;
                   else
                       k++;
            }
            if(found)
                return sClassQueue.get(k).getCopy();
            else
                return null;
        }
        
                
        //for debugging process
        public static void setSRoomList(LinkedList<StudyRoom> sR){
            sRoomList = new LinkedList<>(sR);
        }
        
        public static void setStudyList(LinkedList<StudyClass> sC){
            sClassQueue = new LinkedList<>(sC);
        }
        
        /*
         * Digunakan untuk mengeset atau menambahkan ruangan ayng available
         * @param room 
        
        public void setRoomStudy(Object room) {
            roomStudy.add(room);
        }
        * 
        
        /**
         * Digunakan untuk mendapatkan roomStudy dengan return LinkedList
         * @return roomStudy
         
        public LinkedList getRoomStudy() {
            return roomStudy;
        }
        
        /**
         * digunakan untuk mendapatkan roomStudy pada indeks ke index
         * @param index
         * @return 
         
        public Object getRoomStudyAtIndex(int index) {
            return roomStudy.get(index);
        }
        
        ///Heit, jangan lupa... ANNOYING DOG MENGINVASI CODE
        *░▄▀▄▀▀▀▀▄▀▄░░░░░░░░░ ░▄▀▄▀▀▀▀▄▀▄░░░░░░░░░
        *░█░░░░░░░░▀▄░░░░░░▄░ ░█░░░░░░░░▀▄░░░░░░▄░
        *█░░▀░░▀░░░░░▀▄▄░░█░█ █░░▀░░▀░░░░░▀▄▄░░█░█
        *█░▄░█▀░▄░░░░░░░▀▀░░█ █░▄░█▀░▄░░░░░░░▀▀░░█
        *█░░▀▀▀▀░░░░░░░░░░░░█ █░░▀▀▀▀░░░░░░░░░░░░█
        *█░░░░░░░░░░░░░░░░░░█ █░░░░░░░░░░░░░░░░░░█
        *█░░░░░░░░░░░░░░░░░░█ █░░░░░░░░░░░░░░░░░░█
        *░█░░▄▄░░▄▄▄▄░░▄▄░░█░ ░█░░▄▄░░▄▄▄▄░░▄▄░░█░
        *░█░▄▀█░▄▀░░█░▄▀█░▄▀░ ░█░▄▀█░▄▀░░█░▄▀█░▄▀░
        *░░▀░░░▀░░░░░▀░░░▀░░░ ░░▀░░░▀░░░░░▀░░░▀░░░

        
        
        
        /**
         * Digunakan untuk mencetak isi dari roomStudy
         
        public void printRoomStudy() {
            System.out.println("Isi roomStudy = " + roomStudy);
            //System.out.println("iterator = " + roomStudy.get(2));
        }
        
        /**
         * digunakan untuk mengeset atau menambahkan jadwal
         * @param sched 
         
        public void setScheduleStudy(Object sched) {
            scheduleStudy.add(sched);
        }
        
        /**
         * Digunakan untuk mendapatkan jadwal dengan tipe LinkedList
         * @return scheduleStudy
         
        public LinkedList getScheduleStudy() {
            return scheduleStudy;
        }
        
        /**
         * digunakan untuk mendapatkan jadwal pada indeks ke index
         * @param index
         * @return objek schele indeks ke index
         
        public Object getScheduleStudyAtIndex(int index) {
            return scheduleStudy.get(index);
        }
        
        /**
         * digunakan untuk mencetak isi dari schedule
         
        public void printScheduleStudy() {
            System.out.println("Isi scheduleStudy = " + scheduleStudy);
            //System.out.println("iterator = " + roomStudy.get(2));
        }*/
        
        
        
        
        /*
        public static void setScheduledStatus(boolean[] status){
           
        }*/
}
