/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aiutils;

import java.util.LinkedList;

/**
 *
 * @author Ghifari
 */
public class Additional {
    static GlobalUtils.fileReader f = new GlobalUtils.fileReader();
    static TimeTable tabel = new TimeTable();
    
    public String getNameHourFromInteger(int hour) {
            String jam = "" ;
            switch(hour) {
                case 0  :
                    jam = "07.00-08.00";
                    break;
                case 1  :
                    jam = "08.00-09.00";
                    break;
                case 2  :
                    jam = "09.00-10.00";
                    break;
                case 3  :
                    jam = "10.00-11.00";
                    break;
                case 4  :
                    jam = "11.00-12.00";
                    break;
                case 5  :
                    jam = "12.00-13.00";
                    break;
                case 6  :
                    jam = "13.00-14.00";
                    break;
                case 7  :
                    jam = "14.00-15.00";
                    break;
                case 8  :
                    jam = "15.00-16.00";
                    break;
                case 9  :
                    jam = "16.00-17.00";
                    break;
                case 10  :
                    jam = "17.00-18.00";
                    break;
                case 11  :
                    jam = "18.00-19.00";
                    break;
            }
            return jam;
        }
        
        public String getNameDayFromInteger(int day){
            String hari = "";
            switch(day) {
                case 0  :
                    hari = "Senin";
                    break;
                case 1  :
                    hari = "Selasa";
                    break;
                case 2  :
                    hari = "Rabu";
                    break;
                case 3  :
                    hari = "Kamis";
                    break;
                case 4  :
                    hari = "Jumat";
                    break;
            }
            return hari;
        }
    
    static public void printTimeTable() {
        int size = tabel.getSimplified().getSize();
        for(int index=0; index<size;index++){
            //mendapatkan nama
            System.out.println(GlobalUtils.searchClassById(tabel.getSimplified()
                    .getStudyClassInternalId(index)).getClassName()
            );
            // 0 menyatakan slot
            System.out.println("jam = " + tabel.getSimplified().getStudyClassPosition(index, false)[0]);
            // 1 menyatakan day
            int day = tabel.getSimplified().getStudyClassPosition(index, false)[1];
            String hari;
            System.out.print("day " + day + " = ");
            switch(day) {
                case 0  :
                    System.out.println("senin");
                    break;
                case 1  :
                    System.out.println("selasa");
                    break;
                case 2  :
                    System.out.println("rabu");
                    break;
                case 3  :
                    System.out.println("kamis");
                    break;
                case 4  :
                    System.out.println("jumat");
                    break;
            }
            // 2 menyatakan room
            int room = tabel.getSimplified().getStudyClassPosition(index, false)[2];
            System.out.println("room =  "+GlobalUtils.getStudyRoomListCopy()[room].getRoomId());
            System.out.println("==========================================================");
        }
    }
    
    public static void main(String[] args) {
        printTimeTable();
        String [] a = new String[2];
        a[0]="2\noke";
        a[1]="1\noke";
        System.out.println(a[0]);
        System.out.println(a[1]);
        a = new String[2];
        System.out.println(a[0]);
    }
}
