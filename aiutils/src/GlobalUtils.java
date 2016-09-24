/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package schedulling;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.LinkedList;
/**
 * Not finished yet
 * Not thread safe yet
 * @author HP
 */
public final class GlobalUtils {
        private static int sClassInternalId;
        private static ConcurrentLinkedQueue<StudyClass> sClassQueue;
        //private static ConcurrentLinkedQueue<Boolean> sClassScheduledStatus;
        private static ConcurrentLinkedQueue<StudyRoom> sRoomList;
        private static LinkedList roomStudy = new LinkedList();
        private static LinkedList scheduleStudy = new LinkedList();
        
        /**
         * Digunakan untuk mengeset atau menambahkan ruangan ayng available
         * @param room 
         */
        public void setRoomStudy(Object room) {
            roomStudy.add(room);
        }
        
        /**
         * Digunakan untuk mendapatkan roomStudy dengan return LinkedList
         * @return roomStudy
         */
        public LinkedList getRoomStudy() {
            return roomStudy;
        }
        
        /**
         * digunakan untuk mendapatkan roomStudy pada indeks ke index
         * @param index
         * @return 
         */
        public Object getRoomStudyAtIndex(int index) {
            return roomStudy.get(index);
        }
        
        /**
         * Digunakan untuk mencetak isi dari roomStudy
         */
        public void printRoomStudy() {
            System.out.println("Isi roomStudy = " + roomStudy);
            //System.out.println("iterator = " + roomStudy.get(2));
        }
        
        /**
         * digunakan untuk mengeset atau menambahkan jadwal
         * @param sched 
         */
        public void setScheduleStudy(Object sched) {
            scheduleStudy.add(sched);
        }
        
        /**
         * Digunakan untuk mendapatkan jadwal dengan tipe LinkedList
         * @return scheduleStudy
         */
        public LinkedList getScheduleStudy() {
            return scheduleStudy;
        }
        
        /**
         * digunakan untuk mendapatkan jadwal pada indeks ke index
         * @param index
         * @return objek schele indeks ke index
         */
        public Object getScheduleStudyAtIndex(int index) {
            return scheduleStudy.get(index);
        }
        
        /**
         * digunakan untuk mencetak isi dari schedule
         */
        public void printScheduleStudy() {
            System.out.println("Isi scheduleStudy = " + scheduleStudy);
            //System.out.println("iterator = " + roomStudy.get(2));
        }
        
        /**
         * Faktorial biasa. nothing fancy (ga nemu metod bawaan yang handle faktorial huwaaa)
         * @param n bilangan faktorial
         * @return 1 jika n <=1, n! jika n > 1
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
         * @return hasil kombinasi. 0 jika tidak ada (pair > base)
         */
        public static int Combinatorial(int base, int pair){
            if(pair > base)
                return 0;
            else
                return (Factorial(base))/(Factorial(pair)*Factorial(base-pair));
        }
        
        public static void initStudyClassQueue(StudyClass[] q){
            
        }
        
        public static void initStudyRoomList(){
            
        }
        
        public static int getClassInternalId(short classNum, String className){
            //dummy
            return 0;
        }
        
        public static StudyClass[] getStudyClassQueueCopy(){
            //dummy
            return new StudyClass[1];
        }
        
        /*public static boolean[] getStudyClassScheduledStatus(){
            //dummy
            return new boolean[1];
        }*/
        
        public static StudyRoom[] getStudyRoomListCopy(){
            //dummy
            return new StudyRoom[1];
        }
        
        /*
        public static void setScheduledStatus(boolean[] status){
           
        }*/
}
