/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aiutils;

import javafx.scene.paint.Paint;
import javafx.scene.text.Text;

/**
 * kelas ini digunakan sebagai element perepresentasi timetable.
 * sepertinya kalian nggak akan butuh kelas ini karena kelas ini
 * hanyalah untuk kepentingan GUI asik.
 * @author Ghifari
 */
public class Schedule {
    private final Text sel;
    
    public Schedule(int x, int y, String text){
        sel = new Text(x, y, text);
    }
    
    public void setColor (Paint color){
        sel.setFill(color);
    }
    
    public void setScaleX (double x){
        sel.setScaleX(x);
    }
    
    public void setScaleY (double y){
        sel.setScaleY(y);
    }
    
    public Text getSel() {
        return sel;
    }
    
}
