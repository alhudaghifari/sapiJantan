/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package schedulling;
import java.io.File;
import java.util.LinkedList;
import java.util.Scanner;
import javax.swing.JOptionPane;

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
        
        /**
         * this main function is for testing the parser.
         * you can make it become a commentar, ayee.
         * SAPI JANTAN
         * @param args 
         */
        public static void main(String[] args) {
            fileReader filee = new fileReader();
            System.out.println("Cobain Ruangan terakhir = "
                    + GlobalUtils.sRoomList.getLast().getRoomId());
            System.out.println("Cobain ruangan di jadwal terakhir = "
            +  GlobalUtils.sClassQueue.getLast().getClassName());
        }
        
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
            
            /**
             * konstruktor dengan path nya setting sendiri aye
             * @param path 
             */
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
            
            /**
             * This is parser
             */
            private synchronized void read(){
                
                boolean room = false;
                boolean schedule = false;
                String temp;
                String kelas = "";
                String awal = "";
                String akhir = "";
                String hari = "";
                String kodeMatkul = "";
                String durasi = "";
                
                StudyRoom roomStudy;
                StudyClass classStudy;
                MyTime startTime;
                MyTime endTime;
                short myHour;
                short myMinute;
                TimeConstraint timeCons;
                boolean senin = false;
                boolean selasa = false;
                boolean rabu = false;
                boolean kamis = false;
                boolean jumat = false;
                
                
                try{
                    Scanner inFile = new Scanner(new File(path)).useDelimiter("[\\r\\n]+");
                    int jj=0;
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
                            String [] kata = temp.split(";");
                            for (int i=0;i<kata.length;i++){
                                switch (i) {
                                    case 0:
                                        kelas = kata[i];
                                        break;
                                    case 1:
                                        awal = kata[i];
                                        break;
                                    case 2:
                                        akhir = kata[i];
                                        break;
                                    case 3:
                                        hari = kata[i];
                                        break;
                                }
                            }
                            /*** Start Time ***/
                            String [] jam = awal.split("\\.");
                            myHour = Short.valueOf(jam[0]);
                            myMinute = Short.valueOf(jam[1]);
                            startTime = new MyTime(myHour, myMinute);
                            /*** End Time ***/
                            String [] jam2 = akhir.split("\\.");
                            myHour = Short.valueOf(jam2[0]);
                            myMinute = Short.valueOf(jam2[1]);
                            endTime = new MyTime(myHour, myMinute);
                            
                            /*** boolean hari ***/
                            String [] hariArray = hari.split(",");
                            for (int i=0;i<hariArray.length;i++){
                                switch (hariArray[i]) {
                                    case "1":
                                        senin = true;
                                        break;
                                    case "2":
                                        selasa = true;
                                        break;
                                    case "3":
                                        rabu = true;
                                        break;
                                    case "4":
                                        kamis = true;
                                        break;
                                    case "5":
                                        jumat = true;
                                        break;
                                }
                            }
                            timeCons = new TimeConstraint(startTime, endTime,
                                    senin, selasa, rabu, kamis, jumat);
                            roomStudy = new StudyRoom(kelas, timeCons);
                            GlobalUtils.sRoomList.add(roomStudy); 
                        }else if (schedule && !temp.equals("Jadwal")) {
                            //**GlobalUtils.sClassQueue.add(temp);
                            String [] kata = temp.split(";");
                            for (int i=0;i<kata.length;i++){
                                switch (i) {
                                    case 0:
                                        kodeMatkul = kata[i];
                                        break;
                                    case 1:
                                        kelas = kata[i];
                                        break;
                                    case 2:
                                        awal = kata[i];
                                        break;
                                    case 3:
                                        akhir = kata[i];
                                        break;
                                    case 4:
                                        durasi = kata[i];
                                        break;
                                    case 5:
                                        hari = kata[i];
                                        break;
                                }
                            }
                            String [] kata2 = kelas.split(",");
                            String [] roomCons = new String[10];
                            for(int i=0; i<kata2.length;i++) {
                                roomCons[i] = kata2[i];
                            }
                            /*** Start Time ***/
                            String [] jam = awal.split("\\.");
                            myHour = Short.valueOf(jam[0]);
                            myMinute = Short.valueOf(jam[1]);
                            startTime = new MyTime(myHour, myMinute);
                            /*** End Time ***/
                            String [] jam2 = akhir.split("\\.");
                            myHour = Short.valueOf(jam2[0]);
                            myMinute = Short.valueOf(jam2[1]);
                            endTime = new MyTime(myHour, myMinute);
                            
                            /*** boolean hari ***/
                            String [] hariArray = hari.split(",");
                            for (int i=0;i<hariArray.length;i++){
                                switch (hariArray[i]) {
                                    case "1":
                                        senin = true;
                                        break;
                                    case "2":
                                        selasa = true;
                                        break;
                                    case "3":
                                        rabu = true;
                                        break;
                                    case "4":
                                        kamis = true;
                                        break;
                                    case "5":
                                        jumat = true;
                                        break;
                                }
                            }
                            timeCons = new TimeConstraint(startTime, endTime,
                                    senin, selasa, rabu, kamis, jumat);
                            
                            classStudy = new StudyClass(jj, 1, kodeMatkul,
                                    Short.valueOf(durasi), roomCons, timeCons);
                            GlobalUtils.sClassQueue.add(classStudy);
                        }
                        jj++;
                    }

                }catch(Exception e){
                    System.out.println("WOWOW there is a mistake when reading the file..");
                    JOptionPane.showMessageDialog(null, 
                            "WOWOW there is a mistake when reading the file..",
                            "Be Careful",JOptionPane.ERROR_MESSAGE);
                }
            }
        }

        
        /**
         * Faktorial biasa. nothing fancy (ga nemu metod bawaan yang handle faktorial huwaaa)
         * @param n bilangan faktorial
         * @return 1 jika n kurang sama dengan 1, n! jika n lebih besar 1
         */
        public static int Factorial(int n){
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
            StudyClass[] dupli = (StudyClass[]) sClassQueue.toArray();
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
            StudyRoom[] dupli = (StudyRoom[]) sRoomList.toArray();
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
