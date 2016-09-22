/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aiutils;
import java.util.concurrent.ConcurrentLinkedQueue;
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
