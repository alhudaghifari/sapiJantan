/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aiutils;

import java.io.File;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;
/**
 *
 * @author Ghifari
 */
public class fileReader {
    private static String path;
    
    String getPath(){
        return path;
    }
    
    public void setPath(String path) {
        this.path = path;
    }
    
    public static void filereader(){
        path = "d:/Testcase.txt";
        GlobalUtils global = new GlobalUtils();
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
                    global.setRoomStudy(temp);
                }else if (schedule && !temp.equals("Jadwal")) {
                    global.setScheduleStudy(temp);
                }
                
            }
            
        }catch(Exception e){
            System.out.println("There is a mistake when reading the file..");
        }
        
    }
    
    public static void main(String[] args) {
        filereader();
        GlobalUtils global = new GlobalUtils();
        global.printRoomStudy();
        global.printScheduleStudy();
    }
    
}
