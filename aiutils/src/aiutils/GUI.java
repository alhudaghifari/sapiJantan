/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aiutils;
import java.io.File;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.swing.JOptionPane;
/**
 *
 * @author Ghifari
 */
public class GUI extends Application {
    BorderPane root = new BorderPane();
    static boolean inputed = false;
    static boolean setTable = false;
    static TimeTable tabel2 = new TimeTable(); 
    static TimeTable tabel = new TimeTable();
    GlobalUtils globalutil = new GlobalUtils();
    static int size=0;
    static Schedule [][] schedule = new Schedule[5][12];
    static String [] studyName;
    static int [] hourSlot;
    static int [] daySlot;
    static String [] roomName;
    
    
    public void setStringValueOfSchedule(int index, String studyName, int hourSlot,
                                         int daySlot, String roomName) {
        this.studyName[index] = studyName;
        this.hourSlot[index] = hourSlot;
        this.daySlot[index] = daySlot;
        this.roomName[index] = roomName;
    }
    
    public void setValueOfSchedule() {
        boolean [][] collision = new boolean[5][12];
        for(int i=0;i<5;i++){
            for(int j=0;j<12;j++){
                //the schedule
                String temp = "";
                int jum = 1;
                collision[i][j]=false;
                for(int index=0;index<size;index++){
                    if (daySlot[index] == i) {
                        if (hourSlot[index] == j && !collision[i][j]) {
                            collision[i][j]=true;
                            temp = roomName[index] + "-" + studyName[index];
                        }else if (hourSlot[index] == j && collision[i][j]) {
                            temp += " | " + roomName[index] + "-" + studyName[index];
                            if (jum>=2) {
                                temp += "\n";
                                jum = 0;
                            }
                            jum++;
                            //System.out.println("temp = "+temp);
                        }
                    }
                }
                schedule[i][j].getSel().setText(temp);
                
            }
        }
    }
    
    private void getCopyTable(TimeTable t){
        tabel2 = t.getCopy();
    }
    
    public void setTableTexted(){
        int x = 162;
        int y = 145;
        for(int i=0;i<5;i++){
            for(int j=0;j<12;j++){
                //the schedule
                schedule[i][j] = new Schedule(x,y,"-----");
                schedule[i][j].setColor(Color.BLACK);
                schedule[i][j].setScaleX(1);
                schedule[i][j].setScaleY(1);
                root.getChildren().add(schedule[i][j].getSel());
                y+=45;
            }
            x+=160;
            y = 145;
        }
    }
    
    public void setClearTable(){
        for(int i=0;i<5;i++){
            for(int j=0;j<12;j++){
                //the schedule
                schedule[i][j].getSel().setText("");
            }
        }
    }
    
    @Override public void start(Stage stage) {
        Scene scene = new Scene(root, 1366, 689);
        int x;
        int y;
//        scene.getStylesheets().add(this.getClass().getResource("style.css").toExternalForm());
        //** set menu bar **//
        GridPane gridpane = new GridPane();
        gridpane.setPadding(new Insets(0));
        gridpane.setHgap(0);
        gridpane.setVgap(0);
        
        final ImageView imv = new ImageView();
        final Image image2 = new Image(GUI.class.getResourceAsStream("img/bg.jpg"));
        imv.setImage(image2);

        final HBox pictureRegion = new HBox();
        
        pictureRegion.getChildren().add(imv);
        gridpane.add(pictureRegion, 0, 0);
        root.getChildren().add(gridpane);
        
        //Membuat menu bar (yang di rooftop)
        MenuBar menuBar = new MenuBar();
        menuBar.prefWidthProperty().bind(stage.widthProperty());
        root.setTop(menuBar); 
        
        // File menu - new, save, exit
        Menu fileMenu = new Menu("File");
        MenuItem openMenuItem = new MenuItem("Open");
        MenuItem saveMenuItem = new MenuItem("Save");
        MenuItem exitMenuItem = new MenuItem("Exit");
        exitMenuItem.setOnAction(actionEvent -> Platform.exit());
        openMenuItem.setOnAction(new EventHandler<ActionEvent>(){
             @Override
            public void handle(ActionEvent arg0) {
                FileChooser fileChooser = new FileChooser();
                FileChooser.ExtensionFilter extFilter = 
                        new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
                fileChooser.getExtensionFilters().add(extFilter);
                File file = fileChooser.showOpenDialog(stage);
                GlobalUtils.fileReader filereader = new GlobalUtils.fileReader(file.toString());
                tabel = new TimeTable();
                
                System.out.println(file.toString());
                size = tabel.getSimplified().getSize();
                studyName = new String[size];
                hourSlot = new int[size];
                daySlot = new int[size];
                roomName = new String[size];
                 System.out.println("size = "+size);
                for(int index=0; index<size;index++){
                    int room = tabel.getSimplified().getStudyClassPosition(index, false)[2];
                    setStringValueOfSchedule(index, GlobalUtils.searchClassById(tabel.getSimplified()
                            .getStudyClassInternalId(index)).getClassName(),
                            tabel.getSimplified().getStudyClassPosition(index, false)[0],
                            tabel.getSimplified().getStudyClassPosition(index, false)[1],
                            GlobalUtils.getStudyRoomListCopy()[room].getRoomId());
                }
                
                setValueOfSchedule();
                
                inputed = true;
            }
        });
        
        // menambahkan menu->open,save,exit ke layar
        fileMenu.getItems().addAll(openMenuItem, saveMenuItem,
        new SeparatorMenuItem(), exitMenuItem);
        menuBar.getMenus().addAll(fileMenu);
        //** end of menu bar **//
        
        //** make text to textfield **//
        Text popu = new Text(1020, 210, "Populasi");
        Text muta = new Text(1195, 210, "Mutation");
        Text gene = new Text(1020, 270, "Generation");
        Text sima = new Text(1020, 350, "Probabilitas");
        root.getChildren().add(popu);
        root.getChildren().add(muta);
        root.getChildren().add(gene);
        root.getChildren().add(sima);
        //** end of line **//
        
        
        //** make textfield //
        //populasi, mutation chance, generation
        GridPane gridpane4 = new GridPane();
        gridpane4.setPadding(new Insets(0));
        gridpane4.setLayoutX(1020);
        gridpane4.setLayoutY(220);
        GridPane gridpane5 = new GridPane();
        gridpane5.setPadding(new Insets(0));
        gridpane5.setLayoutX(1195);
        gridpane5.setLayoutY(220);
        GridPane gridpane6 = new GridPane();
        gridpane6.setPadding(new Insets(0));
        gridpane6.setLayoutX(1020);
        gridpane6.setLayoutY(280);
        GridPane gridpane7 = new GridPane();
        gridpane7.setPadding(new Insets(0));
        gridpane7.setLayoutX(1020);
        gridpane7.setLayoutY(360);
        
        TextField populasi = new TextField("");
        TextField mutation = new TextField("");
        TextField generation = new TextField("");
        TextField simaneal = new TextField("");
        
        populasi.setMinWidth(135);
        mutation.setMinWidth(135);
        generation.setMinWidth(135);
        simaneal.setMinWidth(135);
        
        gridpane4.getChildren().add(populasi);
        gridpane5.getChildren().add(mutation);
        gridpane6.getChildren().add(generation);
        gridpane7.getChildren().add(simaneal);
        
        root.getChildren().add(gridpane4);
        root.getChildren().add(gridpane5);
        root.getChildren().add(gridpane6);
        root.getChildren().add(gridpane7);
        //** end of make text field **//
        
        
        //** make button Genetic **//
        GridPane gridpane1 = new GridPane();
        gridpane1.setPadding(new Insets(0));
        gridpane1.setLayoutX(1260);
        gridpane1.setLayoutY(280);
        
        Image imageOk = new Image(getClass().getResourceAsStream("img/genetics.png"));
        Button btn = new Button("", new ImageView(imageOk));
        btn.setPadding(new Insets(1));
        
        btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (inputed) {
                    
//                    if (!popu.getText().isEmpty() ||
//                        !muta.getText().isEmpty() ||
//                        !gene.getText().isEmpty()) {
//                        JOptionPane.showMessageDialog(null, 
//                            "Field Population, Mutation, Generation tidak boleh kosong",
//                            "Perhatian",JOptionPane.ERROR_MESSAGE);
//                    } else {
                        setClearTable();
                        int popup = Integer.valueOf(populasi.getText());
                        int mutat = Integer.valueOf(mutation.getText());
                        int gener = Integer.valueOf(generation.getText());

                        Mutator m = new Mutator(popup, mutat, gener);
                        m.generatePrime();
                        tabel = m.getPrime();


                        size = tabel.getSimplified().getSize();
                        studyName = new String[size];
                        hourSlot = new int[size];
                        daySlot = new int[size];
                        roomName = new String[size];
                         System.out.println("size2 = "+size);
                        for(int index=0; index<size;index++){
                            //isi gui
                            int room = tabel.getSimplified().getStudyClassPosition(index, false)[2];
                            setStringValueOfSchedule(index, GlobalUtils
                                    .searchClassById(tabel.getSimplified().getStudyClassInternalId(index))
                                    .getClassName(),
                                    tabel.getSimplified().getStudyClassPosition(index, false)[0],
                                    tabel.getSimplified().getStudyClassPosition(index, false)[1],
                                    GlobalUtils.getStudyRoomListCopy()[room].getRoomId());
                        }

                        setValueOfSchedule();
//                    }
                } else {
                    JOptionPane.showMessageDialog(null, 
                            "Mohon input file terlebih dahulu (tekan menu bar file -> open)",
                            "Perhatian",JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
        
        
        gridpane1.getChildren().add(btn);
        root.getChildren().add(gridpane1);        
        //** end of button genetics **//
        
        //** make button Hill Climb **//
        GridPane gridpane2 = new GridPane();
        gridpane2.setPadding(new Insets(0));
        gridpane2.setLayoutX(1260);
        gridpane2.setLayoutY(430);
        
        Image imageOk2 = new Image(getClass().getResourceAsStream("img/hillCLimb.png"));
        Button btn2 = new Button("", new ImageView(imageOk2));
        btn2.setPadding(new Insets(1));
        
        btn2.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (inputed) {
                    setClearTable();
                    Simanneal simaneal = new Simanneal(tabel, 0);
                    simaneal.runHill();
                    tabel = simaneal.tt;
                    
                    
                    size = tabel.getSimplified().getSize();
                    studyName = new String[size];
                    hourSlot = new int[size];
                    daySlot = new int[size];
                    roomName = new String[size];
                     System.out.println("size2 = "+size);
                    for(int index=0; index<size;index++){
                        //isi gui
                        int room = tabel.getSimplified().getStudyClassPosition(index, false)[2];
                        setStringValueOfSchedule(index, GlobalUtils
                                .searchClassById(tabel.getSimplified().getStudyClassInternalId(index))
                                .getClassName(),
                                tabel.getSimplified().getStudyClassPosition(index, false)[0],
                                tabel.getSimplified().getStudyClassPosition(index, false)[1],
                                GlobalUtils.getStudyRoomListCopy()[room].getRoomId());
                    }

                    setValueOfSchedule();
                } else {
                    JOptionPane.showMessageDialog(null, 
                            "Mohon input file terlebih dahulu (tekan menu bar file -> open)",
                            "Perhatian",JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
        
        
        gridpane2.getChildren().add(btn2);
        root.getChildren().add(gridpane2);        
        //** end of button Hill Climb **//
        
        //** make button Simaneal **//
        GridPane gridpane3 = new GridPane();
        gridpane3.setPadding(new Insets(0));
        gridpane3.setLayoutX(1260);
        gridpane3.setLayoutY(360);
        
        Image imageOk3 = new Image(getClass().getResourceAsStream("img/simaneal.png"));
        Button btn3 = new Button("", new ImageView(imageOk3));
        btn3.setPadding(new Insets(1));
        
        btn3.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (inputed) {
//                    if (sima.getText().isEmpty()) {
//                        JOptionPane.showMessageDialog(null, 
//                            "Field Probabilitas tidak boleh kosong",
//                            "Perhatian",JOptionPane.ERROR_MESSAGE);
//                    } else {
                        setClearTable();
                        double a = Double.valueOf(simaneal.getText());
                        Simanneal simaneals = new Simanneal(tabel, a);
                        simaneals.runSim();
                        tabel = simaneals.tt;


                        size = tabel.getSimplified().getSize();
                        studyName = new String[size];
                        hourSlot = new int[size];
                        daySlot = new int[size];
                        roomName = new String[size];
                         System.out.println("size2 = "+size);
                        for(int index=0; index<size;index++){
                            //isi gui
                            int room = tabel.getSimplified().getStudyClassPosition(index, false)[2];
                            setStringValueOfSchedule(index, GlobalUtils
                                    .searchClassById(tabel.getSimplified().getStudyClassInternalId(index))
                                    .getClassName(),
                                    tabel.getSimplified().getStudyClassPosition(index, false)[0],
                                    tabel.getSimplified().getStudyClassPosition(index, false)[1],
                                    GlobalUtils.getStudyRoomListCopy()[room].getRoomId());
                        }

                        setValueOfSchedule();
//                    }
                } else {
                    JOptionPane.showMessageDialog(null, 
                            "Mohon input file terlebih dahulu (tekan menu bar file -> open)",
                            "Be Careful",JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
        
        
        gridpane3.getChildren().add(btn3);
        root.getChildren().add(gridpane3);        
        //** end of button Simaneal **//
        
        
        if(!setTable)
            setTableTexted();
        setTable = true;
        
        //this is in Schedule Table
        x = 190;
        y = 145;
        Schedule [][] scheduleTarget = new Schedule[5][12];
        //Schedule [][] scheduleSource = new Schedule[5][12];
        for (int i=0;i<5;i++) {
            for (int j=0;j<12;j++) {
                // Target Schedule Table
                scheduleTarget[i][j] = new Schedule(x, y, "           ");
                scheduleTarget[i][j].setScaleX(1.3);
                scheduleTarget[i][j].setScaleY(1.3);
                scheduleTarget[i][j].setColor(Color.BLACK);
                root.getChildren().add(scheduleTarget[i][j].getSel());
                
                // Source Schedule Table
//                scheduleSource[i][j] = new Schedule(x, y, "");
//                scheduleSource[i][j].setScaleX(1.3);
//                scheduleSource[i][j].setScaleY(1.3);
//                scheduleSource[i][j].setColor(Color.BLACK);
//                
                y+=45;
            }
            x+=163;
            y = 145;
        }
        
        //this is in Slot Pengubahan
        x = 1072;
        y = 569;
        Schedule [][] slotTarget = new Schedule[2][2];
        Schedule [][] slotSource = new Schedule[2][2];
        for (int i=0;i<2;i++) {
            for (int j=0;j<2;j++) {
                //slot target
                slotTarget[i][j] = new Schedule(x, y, "          ");
                slotTarget[i][j].setScaleX(2.5);
                slotTarget[i][j].setScaleY(2.5);
                slotTarget[i][j].setColor(Color.DARKGREY);
                root.getChildren().add(slotTarget[i][j].getSel());
                
                //slot source
                slotSource[i][j] = new Schedule(x, y, "");
                slotSource[i][j].setScaleX(1.3);
                slotSource[i][j].setScaleY(1.3);
                slotSource[i][j].setColor(Color.BLACK);
                y+=49;
            }
            x+= 139;
            y = 569;
        }
        
        
        // this is source in Slot Pengubahan
       
//        final Text source = new Text(1052, 204, "DRAG ME");
//        source.setScaleX(1.3);
//        source.setScaleY(1.3);
        
        //untuk percobaan
//        source.setOnDragDetected(new EventHandler <MouseEvent>() {
//            @Override
//            public void handle(MouseEvent event) {
//                /* allow MOVE transfer mode */
//                Dragboard db = source.startDragAndDrop(TransferMode.MOVE);
//                
//                /* put a string on dragboard */
//                ClipboardContent content = new ClipboardContent();
//                content.putString(source.getText());
//                db.setContent(content);
//                
//                event.consume();
//            }
//        });
        
        //******* Customize slot source in Slot Pengubahan When Drag Detected *******//
        //=============================== [0][y] ===================================//
        //=============================== [0][y] ===================================//
        //=============================== [0][y] ===================================//
        slotSource[0][0].getSel().setOnDragDetected(new EventHandler <MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                /* allow MOVE transfer mode */
                Dragboard db = slotSource[0][0].getSel().startDragAndDrop(TransferMode.MOVE);
                
                /* put a string on dragboard */
                ClipboardContent content = new ClipboardContent();
                content.putString(slotSource[0][0].getSel().getText());
                db.setContent(content);
                
                event.consume();
            }
        });
        
        slotSource[0][1].getSel().setOnDragDetected(new EventHandler <MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                /* allow MOVE transfer mode */
                Dragboard db = slotSource[0][1].getSel().startDragAndDrop(TransferMode.MOVE);
                
                /* put a string on dragboard */
                ClipboardContent content = new ClipboardContent();
                content.putString(slotSource[0][1].getSel().getText());
                db.setContent(content);
                
                event.consume();
            }
        });
        
        slotSource[1][0].getSel().setOnDragDetected(new EventHandler <MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                /* allow MOVE transfer mode */
                Dragboard db = slotSource[1][0].getSel().startDragAndDrop(TransferMode.MOVE);
                
                /* put a string on dragboard */
                ClipboardContent content = new ClipboardContent();
                content.putString(slotSource[1][0].getSel().getText());
                db.setContent(content);
                
                event.consume();
            }
        });
        
        slotSource[1][1].getSel().setOnDragDetected(new EventHandler <MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                /* allow MOVE transfer mode */
                Dragboard db = slotSource[1][1].getSel().startDragAndDrop(TransferMode.MOVE);
                
                /* put a string on dragboard */
                ClipboardContent content = new ClipboardContent();
                content.putString(slotSource[1][1].getSel().getText());
                db.setContent(content);
                
                event.consume();
            }
        });

        
        //***************** Customize slot SOURCE in Schedule Table  **************//
        //=============================== [0][y] ===================================//
        //=============================== [0][y] ===================================//
        //=============================== [0][y] ===================================//
        schedule[0][0].getSel().setOnDragDetected(new EventHandler <MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                /* allow MOVE transfer mode */
                Dragboard db = schedule[0][0].getSel().startDragAndDrop(TransferMode.MOVE);
                
                /* put a string on dragboard */
                ClipboardContent content = new ClipboardContent();
                content.putString(schedule[0][0].getSel().getText());
                db.setContent(content);
                
                event.consume();
            }
        });
        
        schedule[0][1].getSel().setOnDragDetected(new EventHandler <MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                /* allow MOVE transfer mode */
                Dragboard db = schedule[0][1].getSel().startDragAndDrop(TransferMode.MOVE);
                
                /* put a string on dragboard */
                ClipboardContent content = new ClipboardContent();
                content.putString(schedule[0][1].getSel().getText());
                db.setContent(content);
                
                event.consume();
            }
        });
        
        schedule[0][2].getSel().setOnDragDetected(new EventHandler <MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                /* allow MOVE transfer mode */
                Dragboard db = schedule[0][2].getSel().startDragAndDrop(TransferMode.MOVE);
                
                /* put a string on dragboard */
                ClipboardContent content = new ClipboardContent();
                content.putString(schedule[0][2].getSel().getText());
                db.setContent(content);
                
                event.consume();
            }
        });
        
        schedule[0][3].getSel().setOnDragDetected(new EventHandler <MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                /* allow MOVE transfer mode */
                Dragboard db = schedule[0][3].getSel().startDragAndDrop(TransferMode.MOVE);
                
                /* put a string on dragboard */
                ClipboardContent content = new ClipboardContent();
                content.putString(schedule[0][3].getSel().getText());
                db.setContent(content);
                
                event.consume();
            }
        });
        
        schedule[0][4].getSel().setOnDragDetected(new EventHandler <MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                /* allow MOVE transfer mode */
                Dragboard db = schedule[0][4].getSel().startDragAndDrop(TransferMode.MOVE);
                
                /* put a string on dragboard */
                ClipboardContent content = new ClipboardContent();
                content.putString(schedule[0][4].getSel().getText());
                db.setContent(content);
                
                event.consume();
            }
        });
        
        schedule[0][5].getSel().setOnDragDetected(new EventHandler <MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                /* allow MOVE transfer mode */
                Dragboard db = schedule[0][5].getSel().startDragAndDrop(TransferMode.MOVE);
                
                /* put a string on dragboard */
                ClipboardContent content = new ClipboardContent();
                content.putString(schedule[0][5].getSel().getText());
                db.setContent(content);
                
                event.consume();
            }
        });
        
        schedule[0][6].getSel().setOnDragDetected(new EventHandler <MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                /* allow MOVE transfer mode */
                Dragboard db = schedule[0][6].getSel().startDragAndDrop(TransferMode.MOVE);
                
                /* put a string on dragboard */
                ClipboardContent content = new ClipboardContent();
                content.putString(schedule[0][6].getSel().getText());
                db.setContent(content);
                
                event.consume();
            }
        });
        
        schedule[0][7].getSel().setOnDragDetected(new EventHandler <MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                /* allow MOVE transfer mode */
                Dragboard db = schedule[0][7].getSel().startDragAndDrop(TransferMode.MOVE);
                
                /* put a string on dragboard */
                ClipboardContent content = new ClipboardContent();
                content.putString(schedule[0][7].getSel().getText());
                db.setContent(content);
                
                event.consume();
            }
        });
        
        schedule[0][8].getSel().setOnDragDetected(new EventHandler <MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                /* allow MOVE transfer mode */
                Dragboard db = schedule[0][8].getSel().startDragAndDrop(TransferMode.MOVE);
                
                /* put a string on dragboard */
                ClipboardContent content = new ClipboardContent();
                content.putString(schedule[0][8].getSel().getText());
                db.setContent(content);
                
                event.consume();
            }
        });
        
        schedule[0][9].getSel().setOnDragDetected(new EventHandler <MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                /* allow MOVE transfer mode */
                Dragboard db = schedule[0][9].getSel().startDragAndDrop(TransferMode.MOVE);
                
                /* put a string on dragboard */
                ClipboardContent content = new ClipboardContent();
                content.putString(schedule[0][9].getSel().getText());
                db.setContent(content);
                
                event.consume();
            }
        });
        
        schedule[0][10].getSel().setOnDragDetected(new EventHandler <MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                /* allow MOVE transfer mode */
                Dragboard db = schedule[0][10].getSel().startDragAndDrop(TransferMode.MOVE);
                
                /* put a string on dragboard */
                ClipboardContent content = new ClipboardContent();
                content.putString(schedule[0][10].getSel().getText());
                db.setContent(content);
                
                event.consume();
            }
        });
        
        schedule[0][11].getSel().setOnDragDetected(new EventHandler <MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                /* allow MOVE transfer mode */
                Dragboard db = schedule[0][11].getSel().startDragAndDrop(TransferMode.MOVE);
                
                /* put a string on dragboard */
                ClipboardContent content = new ClipboardContent();
                content.putString(schedule[0][11].getSel().getText());
                db.setContent(content);
                
                event.consume();
            }
        });
        //*************************************************************************//
        schedule[1][0].getSel().setOnDragDetected(new EventHandler <MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                /* allow MOVE transfer mode */
                Dragboard db = schedule[1][0].getSel().startDragAndDrop(TransferMode.MOVE);
                
                /* put a string on dragboard */
                ClipboardContent content = new ClipboardContent();
                content.putString(schedule[1][0].getSel().getText());
                db.setContent(content);
                
                event.consume();
            }
        });
        
        schedule[1][1].getSel().setOnDragDetected(new EventHandler <MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                /* allow MOVE transfer mode */
                Dragboard db = schedule[1][1].getSel().startDragAndDrop(TransferMode.MOVE);
                
                /* put a string on dragboard */
                ClipboardContent content = new ClipboardContent();
                content.putString(schedule[1][1].getSel().getText());
                db.setContent(content);
                
                event.consume();
            }
        });
        
        schedule[1][2].getSel().setOnDragDetected(new EventHandler <MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                /* allow MOVE transfer mode */
                Dragboard db = schedule[1][2].getSel().startDragAndDrop(TransferMode.MOVE);
                
                /* put a string on dragboard */
                ClipboardContent content = new ClipboardContent();
                content.putString(schedule[1][2].getSel().getText());
                db.setContent(content);
                
                event.consume();
            }
        });
        
        schedule[1][3].getSel().setOnDragDetected(new EventHandler <MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                /* allow MOVE transfer mode */
                Dragboard db = schedule[1][3].getSel().startDragAndDrop(TransferMode.MOVE);
                
                /* put a string on dragboard */
                ClipboardContent content = new ClipboardContent();
                content.putString(schedule[1][3].getSel().getText());
                db.setContent(content);
                
                event.consume();
            }
        });
        
        schedule[1][4].getSel().setOnDragDetected(new EventHandler <MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                /* allow MOVE transfer mode */
                Dragboard db = schedule[1][4].getSel().startDragAndDrop(TransferMode.MOVE);
                
                /* put a string on dragboard */
                ClipboardContent content = new ClipboardContent();
                content.putString(schedule[1][4].getSel().getText());
                db.setContent(content);
                
                event.consume();
            }
        });
        
        schedule[1][5].getSel().setOnDragDetected(new EventHandler <MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                /* allow MOVE transfer mode */
                Dragboard db = schedule[1][5].getSel().startDragAndDrop(TransferMode.MOVE);
                
                /* put a string on dragboard */
                ClipboardContent content = new ClipboardContent();
                content.putString(schedule[1][5].getSel().getText());
                db.setContent(content);
                
                event.consume();
            }
        });
        
        schedule[1][6].getSel().setOnDragDetected(new EventHandler <MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                /* allow MOVE transfer mode */
                Dragboard db = schedule[1][6].getSel().startDragAndDrop(TransferMode.MOVE);
                
                /* put a string on dragboard */
                ClipboardContent content = new ClipboardContent();
                content.putString(schedule[1][6].getSel().getText());
                db.setContent(content);
                
                event.consume();
            }
        });
        
        schedule[1][7].getSel().setOnDragDetected(new EventHandler <MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                /* allow MOVE transfer mode */
                Dragboard db = schedule[1][7].getSel().startDragAndDrop(TransferMode.MOVE);
                
                /* put a string on dragboard */
                ClipboardContent content = new ClipboardContent();
                content.putString(schedule[1][7].getSel().getText());
                db.setContent(content);
                
                event.consume();
            }
        });
        
        schedule[1][8].getSel().setOnDragDetected(new EventHandler <MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                /* allow MOVE transfer mode */
                Dragboard db = schedule[1][8].getSel().startDragAndDrop(TransferMode.MOVE);
                
                /* put a string on dragboard */
                ClipboardContent content = new ClipboardContent();
                content.putString(schedule[1][8].getSel().getText());
                db.setContent(content);
                
                event.consume();
            }
        });
        
        schedule[1][9].getSel().setOnDragDetected(new EventHandler <MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                /* allow MOVE transfer mode */
                Dragboard db = schedule[1][9].getSel().startDragAndDrop(TransferMode.MOVE);
                
                /* put a string on dragboard */
                ClipboardContent content = new ClipboardContent();
                content.putString(schedule[1][9].getSel().getText());
                db.setContent(content);
                
                event.consume();
            }
        });
        
        schedule[1][10].getSel().setOnDragDetected(new EventHandler <MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                /* allow MOVE transfer mode */
                Dragboard db = schedule[1][10].getSel().startDragAndDrop(TransferMode.MOVE);
                
                /* put a string on dragboard */
                ClipboardContent content = new ClipboardContent();
                content.putString(schedule[1][10].getSel().getText());
                db.setContent(content);
                
                event.consume();
            }
        });
        
        schedule[1][11].getSel().setOnDragDetected(new EventHandler <MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                /* allow MOVE transfer mode */
                Dragboard db = schedule[1][11].getSel().startDragAndDrop(TransferMode.MOVE);
                
                /* put a string on dragboard */
                ClipboardContent content = new ClipboardContent();
                content.putString(schedule[1][11].getSel().getText());
                db.setContent(content);
                
                event.consume();
            }
        });
        //*************************************************************************//
        schedule[2][0].getSel().setOnDragDetected(new EventHandler <MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                /* allow MOVE transfer mode */
                Dragboard db = schedule[2][0].getSel().startDragAndDrop(TransferMode.MOVE);
                
                /* put a string on dragboard */
                ClipboardContent content = new ClipboardContent();
                content.putString(schedule[2][0].getSel().getText());
                db.setContent(content);
                
                event.consume();
            }
        });
        
        schedule[2][1].getSel().setOnDragDetected(new EventHandler <MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                /* allow MOVE transfer mode */
                Dragboard db = schedule[2][1].getSel().startDragAndDrop(TransferMode.MOVE);
                
                /* put a string on dragboard */
                ClipboardContent content = new ClipboardContent();
                content.putString(schedule[2][1].getSel().getText());
                db.setContent(content);
                
                event.consume();
            }
        });
        
        schedule[2][2].getSel().setOnDragDetected(new EventHandler <MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                /* allow MOVE transfer mode */
                Dragboard db = schedule[2][2].getSel().startDragAndDrop(TransferMode.MOVE);
                
                /* put a string on dragboard */
                ClipboardContent content = new ClipboardContent();
                content.putString(schedule[2][2].getSel().getText());
                db.setContent(content);
                
                event.consume();
            }
        });
        
        schedule[2][3].getSel().setOnDragDetected(new EventHandler <MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                /* allow MOVE transfer mode */
                Dragboard db = schedule[2][3].getSel().startDragAndDrop(TransferMode.MOVE);
                
                /* put a string on dragboard */
                ClipboardContent content = new ClipboardContent();
                content.putString(schedule[2][3].getSel().getText());
                db.setContent(content);
                
                event.consume();
            }
        });
        
        schedule[2][4].getSel().setOnDragDetected(new EventHandler <MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                /* allow MOVE transfer mode */
                Dragboard db = schedule[2][4].getSel().startDragAndDrop(TransferMode.MOVE);
                
                /* put a string on dragboard */
                ClipboardContent content = new ClipboardContent();
                content.putString(schedule[2][4].getSel().getText());
                db.setContent(content);
                
                event.consume();
            }
        });
        
        schedule[2][5].getSel().setOnDragDetected(new EventHandler <MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                /* allow MOVE transfer mode */
                Dragboard db = schedule[2][5].getSel().startDragAndDrop(TransferMode.MOVE);
                
                /* put a string on dragboard */
                ClipboardContent content = new ClipboardContent();
                content.putString(schedule[2][5].getSel().getText());
                db.setContent(content);
                
                event.consume();
            }
        });
        
        schedule[2][6].getSel().setOnDragDetected(new EventHandler <MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                /* allow MOVE transfer mode */
                Dragboard db = schedule[2][6].getSel().startDragAndDrop(TransferMode.MOVE);
                
                /* put a string on dragboard */
                ClipboardContent content = new ClipboardContent();
                content.putString(schedule[2][6].getSel().getText());
                db.setContent(content);
                
                event.consume();
            }
        });
        
        schedule[2][7].getSel().setOnDragDetected(new EventHandler <MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                /* allow MOVE transfer mode */
                Dragboard db = schedule[2][7].getSel().startDragAndDrop(TransferMode.MOVE);
                
                /* put a string on dragboard */
                ClipboardContent content = new ClipboardContent();
                content.putString(schedule[2][7].getSel().getText());
                db.setContent(content);
                
                event.consume();
            }
        });
        
        schedule[2][8].getSel().setOnDragDetected(new EventHandler <MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                /* allow MOVE transfer mode */
                Dragboard db = schedule[2][8].getSel().startDragAndDrop(TransferMode.MOVE);
                
                /* put a string on dragboard */
                ClipboardContent content = new ClipboardContent();
                content.putString(schedule[2][8].getSel().getText());
                db.setContent(content);
                
                event.consume();
            }
        });
        
        schedule[2][9].getSel().setOnDragDetected(new EventHandler <MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                /* allow MOVE transfer mode */
                Dragboard db = schedule[2][9].getSel().startDragAndDrop(TransferMode.MOVE);
                
                /* put a string on dragboard */
                ClipboardContent content = new ClipboardContent();
                content.putString(schedule[2][9].getSel().getText());
                db.setContent(content);
                
                event.consume();
            }
        });
        
        schedule[2][10].getSel().setOnDragDetected(new EventHandler <MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                /* allow MOVE transfer mode */
                Dragboard db = schedule[2][10].getSel().startDragAndDrop(TransferMode.MOVE);
                
                /* put a string on dragboard */
                ClipboardContent content = new ClipboardContent();
                content.putString(schedule[2][10].getSel().getText());
                db.setContent(content);
                
                event.consume();
            }
        });
        
        schedule[2][11].getSel().setOnDragDetected(new EventHandler <MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                /* allow MOVE transfer mode */
                Dragboard db = schedule[2][11].getSel().startDragAndDrop(TransferMode.MOVE);
                
                /* put a string on dragboard */
                ClipboardContent content = new ClipboardContent();
                content.putString(schedule[2][11].getSel().getText());
                db.setContent(content);
                
                event.consume();
            }
        });
        //*************************************************************************//
        schedule[3][0].getSel().setOnDragDetected(new EventHandler <MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                /* allow MOVE transfer mode */
                Dragboard db = schedule[3][0].getSel().startDragAndDrop(TransferMode.MOVE);
                
                /* put a string on dragboard */
                ClipboardContent content = new ClipboardContent();
                content.putString(schedule[3][0].getSel().getText());
                db.setContent(content);
                
                event.consume();
            }
        });
        
        schedule[3][1].getSel().setOnDragDetected(new EventHandler <MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                /* allow MOVE transfer mode */
                Dragboard db = schedule[3][1].getSel().startDragAndDrop(TransferMode.MOVE);
                
                /* put a string on dragboard */
                ClipboardContent content = new ClipboardContent();
                content.putString(schedule[3][1].getSel().getText());
                db.setContent(content);
                
                event.consume();
            }
        });
        
        schedule[3][2].getSel().setOnDragDetected(new EventHandler <MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                /* allow MOVE transfer mode */
                Dragboard db = schedule[3][2].getSel().startDragAndDrop(TransferMode.MOVE);
                
                /* put a string on dragboard */
                ClipboardContent content = new ClipboardContent();
                content.putString(schedule[3][2].getSel().getText());
                db.setContent(content);
                
                event.consume();
            }
        });
        
        schedule[3][3].getSel().setOnDragDetected(new EventHandler <MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                /* allow MOVE transfer mode */
                Dragboard db = schedule[3][3].getSel().startDragAndDrop(TransferMode.MOVE);
                
                /* put a string on dragboard */
                ClipboardContent content = new ClipboardContent();
                content.putString(schedule[3][3].getSel().getText());
                db.setContent(content);
                
                event.consume();
            }
        });
        
        schedule[3][4].getSel().setOnDragDetected(new EventHandler <MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                /* allow MOVE transfer mode */
                Dragboard db = schedule[3][4].getSel().startDragAndDrop(TransferMode.MOVE);
                
                /* put a string on dragboard */
                ClipboardContent content = new ClipboardContent();
                content.putString(schedule[3][4].getSel().getText());
                db.setContent(content);
                
                event.consume();
            }
        });
        
        schedule[3][5].getSel().setOnDragDetected(new EventHandler <MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                /* allow MOVE transfer mode */
                Dragboard db = schedule[3][5].getSel().startDragAndDrop(TransferMode.MOVE);
                
                /* put a string on dragboard */
                ClipboardContent content = new ClipboardContent();
                content.putString(schedule[3][5].getSel().getText());
                db.setContent(content);
                
                event.consume();
            }
        });
        
        schedule[3][6].getSel().setOnDragDetected(new EventHandler <MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                /* allow MOVE transfer mode */
                Dragboard db = schedule[3][6].getSel().startDragAndDrop(TransferMode.MOVE);
                
                /* put a string on dragboard */
                ClipboardContent content = new ClipboardContent();
                content.putString(schedule[3][6].getSel().getText());
                db.setContent(content);
                
                event.consume();
            }
        });
        
        schedule[3][7].getSel().setOnDragDetected(new EventHandler <MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                /* allow MOVE transfer mode */
                Dragboard db = schedule[3][7].getSel().startDragAndDrop(TransferMode.MOVE);
                
                /* put a string on dragboard */
                ClipboardContent content = new ClipboardContent();
                content.putString(schedule[3][7].getSel().getText());
                db.setContent(content);
                
                event.consume();
            }
        });
        
        schedule[3][8].getSel().setOnDragDetected(new EventHandler <MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                /* allow MOVE transfer mode */
                Dragboard db = schedule[3][8].getSel().startDragAndDrop(TransferMode.MOVE);
                
                /* put a string on dragboard */
                ClipboardContent content = new ClipboardContent();
                content.putString(schedule[3][8].getSel().getText());
                db.setContent(content);
                
                event.consume();
            }
        });
        
        schedule[3][9].getSel().setOnDragDetected(new EventHandler <MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                /* allow MOVE transfer mode */
                Dragboard db = schedule[3][9].getSel().startDragAndDrop(TransferMode.MOVE);
                
                /* put a string on dragboard */
                ClipboardContent content = new ClipboardContent();
                content.putString(schedule[3][9].getSel().getText());
                db.setContent(content);
                
                event.consume();
            }
        });
        
        schedule[3][10].getSel().setOnDragDetected(new EventHandler <MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                /* allow MOVE transfer mode */
                Dragboard db = schedule[3][10].getSel().startDragAndDrop(TransferMode.MOVE);
                
                /* put a string on dragboard */
                ClipboardContent content = new ClipboardContent();
                content.putString(schedule[3][10].getSel().getText());
                db.setContent(content);
                
                event.consume();
            }
        });
        
        schedule[3][11].getSel().setOnDragDetected(new EventHandler <MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                /* allow MOVE transfer mode */
                Dragboard db = schedule[3][11].getSel().startDragAndDrop(TransferMode.MOVE);
                
                /* put a string on dragboard */
                ClipboardContent content = new ClipboardContent();
                content.putString(schedule[3][11].getSel().getText());
                db.setContent(content);
                
                event.consume();
            }
        });
        //*************************************************************************//
        schedule[4][0].getSel().setOnDragDetected(new EventHandler <MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                /* allow MOVE transfer mode */
                Dragboard db = schedule[4][0].getSel().startDragAndDrop(TransferMode.MOVE);
                
                /* put a string on dragboard */
                ClipboardContent content = new ClipboardContent();
                content.putString(schedule[4][0].getSel().getText());
                db.setContent(content);
                
                event.consume();
            }
        });
        
        schedule[4][1].getSel().setOnDragDetected(new EventHandler <MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                /* allow MOVE transfer mode */
                Dragboard db = schedule[4][1].getSel().startDragAndDrop(TransferMode.MOVE);
                
                /* put a string on dragboard */
                ClipboardContent content = new ClipboardContent();
                content.putString(schedule[4][1].getSel().getText());
                db.setContent(content);
                
                event.consume();
            }
        });
        
        schedule[4][2].getSel().setOnDragDetected(new EventHandler <MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                /* allow MOVE transfer mode */
                Dragboard db = schedule[4][2].getSel().startDragAndDrop(TransferMode.MOVE);
                
                /* put a string on dragboard */
                ClipboardContent content = new ClipboardContent();
                content.putString(schedule[4][2].getSel().getText());
                db.setContent(content);
                
                event.consume();
            }
        });
        
        schedule[4][3].getSel().setOnDragDetected(new EventHandler <MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                /* allow MOVE transfer mode */
                Dragboard db = schedule[4][3].getSel().startDragAndDrop(TransferMode.MOVE);
                
                /* put a string on dragboard */
                ClipboardContent content = new ClipboardContent();
                content.putString(schedule[4][3].getSel().getText());
                db.setContent(content);
                
                event.consume();
            }
        });
        
        schedule[4][4].getSel().setOnDragDetected(new EventHandler <MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                /* allow MOVE transfer mode */
                Dragboard db = schedule[4][4].getSel().startDragAndDrop(TransferMode.MOVE);
                
                /* put a string on dragboard */
                ClipboardContent content = new ClipboardContent();
                content.putString(schedule[4][4].getSel().getText());
                db.setContent(content);
                
                event.consume();
            }
        });
        
        schedule[4][5].getSel().setOnDragDetected(new EventHandler <MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                /* allow MOVE transfer mode */
                Dragboard db = schedule[4][5].getSel().startDragAndDrop(TransferMode.MOVE);
                
                /* put a string on dragboard */
                ClipboardContent content = new ClipboardContent();
                content.putString(schedule[4][5].getSel().getText());
                db.setContent(content);
                
                event.consume();
            }
        });
        
        schedule[4][6].getSel().setOnDragDetected(new EventHandler <MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                /* allow MOVE transfer mode */
                Dragboard db = schedule[4][6].getSel().startDragAndDrop(TransferMode.MOVE);
                
                /* put a string on dragboard */
                ClipboardContent content = new ClipboardContent();
                content.putString(schedule[4][6].getSel().getText());
                db.setContent(content);
                
                event.consume();
            }
        });
        
        schedule[4][7].getSel().setOnDragDetected(new EventHandler <MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                /* allow MOVE transfer mode */
                Dragboard db = schedule[4][7].getSel().startDragAndDrop(TransferMode.MOVE);
                
                /* put a string on dragboard */
                ClipboardContent content = new ClipboardContent();
                content.putString(schedule[4][7].getSel().getText());
                db.setContent(content);
                
                event.consume();
            }
        });
        
        schedule[4][8].getSel().setOnDragDetected(new EventHandler <MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                /* allow MOVE transfer mode */
                Dragboard db = schedule[3][8].getSel().startDragAndDrop(TransferMode.MOVE);
                
                /* put a string on dragboard */
                ClipboardContent content = new ClipboardContent();
                content.putString(schedule[4][8].getSel().getText());
                db.setContent(content);
                
                event.consume();
            }
        });
        
        schedule[4][9].getSel().setOnDragDetected(new EventHandler <MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                /* allow MOVE transfer mode */
                Dragboard db = schedule[4][9].getSel().startDragAndDrop(TransferMode.MOVE);
                
                /* put a string on dragboard */
                ClipboardContent content = new ClipboardContent();
                content.putString(schedule[4][9].getSel().getText());
                db.setContent(content);
                
                event.consume();
            }
        });
        
        schedule[4][10].getSel().setOnDragDetected(new EventHandler <MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                /* allow MOVE transfer mode */
                Dragboard db = schedule[4][10].getSel().startDragAndDrop(TransferMode.MOVE);
                
                /* put a string on dragboard */
                ClipboardContent content = new ClipboardContent();
                content.putString(schedule[4][10].getSel().getText());
                db.setContent(content);
                
                event.consume();
            }
        });
        
        schedule[4][11].getSel().setOnDragDetected(new EventHandler <MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                /* allow MOVE transfer mode */
                Dragboard db = schedule[4][11].getSel().startDragAndDrop(TransferMode.MOVE);
                
                /* put a string on dragboard */
                ClipboardContent content = new ClipboardContent();
                content.putString(schedule[4][11].getSel().getText());
                db.setContent(content);
                
                event.consume();
            }
        });
        
        //=============================== [0][y] ===================================//
        //=============================== [0][y] ===================================//
        //=============================== [0][y] ===================================//
        //******************  End of slot SOURCE in Schedule Table  ****************//
        
        //***************** Customize slot TARGET in Slot Pengubahan  **************//
        //=============================== [0][y] ===================================//
        //=============================== [0][y] ===================================//
        //=============================== [0][y] ===================================//
        slotTarget[0][0].getSel().setOnDragOver(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* accept it only if it is  not dragged from the same node 
                 * and if it has a string data */
                if (event.getGestureSource() != slotTarget[0][0].getSel() &&
                        event.getDragboard().hasString()) {
                    /* allow for moving */
                    event.acceptTransferModes(TransferMode.MOVE);
                }
                
                event.consume();
            }
        });
        
        slotTarget[0][0].getSel().setOnDragDropped(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if there is a string data on dragboard, read it and use it */
                Dragboard db = event.getDragboard();
                boolean success = false;
                
                if (db.hasString()) {
                    slotSource[0][0].getSel().setText(db.getString());
                    success = true;
                }
                /* let the source know whether the string was successfully 
                 * transferred and used */
                event.setDropCompleted(success);
                
                event.consume();
            }
        });
        //*****************************************************//
        //*****************************************************//
        //*****************************************************//
        //*****************************************************//
        slotTarget[0][1].getSel().setOnDragOver(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* accept it only if it is  not dragged from the same node 
                 * and if it has a string data */
                if (event.getGestureSource() != slotTarget[0][1].getSel() &&
                        event.getDragboard().hasString()) {
                    /* allow for moving */
                    event.acceptTransferModes(TransferMode.MOVE);
                }
                
                event.consume();
            }
        });
        
        slotTarget[0][1].getSel().setOnDragDropped(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if there is a string data on dragboard, read it and use it */
                Dragboard db = event.getDragboard();
                boolean success = false;
                
                if (db.hasString()) {
//                    slotTarget[0][1].getSel().setText("");
                    slotSource[0][1].getSel().setText(db.getString());
                    success = true;
                }
                /* let the source know whether the string was successfully 
                 * transferred and used */
                event.setDropCompleted(success);
                
                event.consume();
            }
        });
        //*****************************************************//
        //*****************************************************//
        //*****************************************************//
        //*****************************************************//
        slotTarget[1][0].getSel().setOnDragOver(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* accept it only if it is  not dragged from the same node 
                 * and if it has a string data */
                if (event.getGestureSource() != slotTarget[1][0].getSel() &&
                        event.getDragboard().hasString()) {
                    /* allow for moving */
                    event.acceptTransferModes(TransferMode.MOVE);
                }
                
                event.consume();
            }
        });
        
        slotTarget[1][0].getSel().setOnDragDropped(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if there is a string data on dragboard, read it and use it */
                Dragboard db = event.getDragboard();
                boolean success = false;
                
                if (db.hasString()) {
//                    slotTarget[0][1].getSel().setText("");
                    slotSource[1][0].getSel().setText(db.getString());
                    success = true;
                }
                /* let the source know whether the string was successfully 
                 * transferred and used */
                event.setDropCompleted(success);
                
                event.consume();
            }
        });
        //*****************************************************//
        //*****************************************************//
        //*****************************************************//
        //*****************************************************//
        slotTarget[1][1].getSel().setOnDragOver(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* accept it only if it is  not dragged from the same node 
                 * and if it has a string data */
                if (event.getGestureSource() != slotTarget[1][1].getSel() &&
                        event.getDragboard().hasString()) {
                    /* allow for moving */
                    event.acceptTransferModes(TransferMode.MOVE);
                }
                
                event.consume();
            }
        });
        
        slotTarget[1][1].getSel().setOnDragDropped(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if there is a string data on dragboard, read it and use it */
                Dragboard db = event.getDragboard();
                boolean success = false;
                
                if (db.hasString()) {
//                    slotTarget[0][1].getSel().setText("");
                    slotSource[1][1].getSel().setText(db.getString());
                    success = true;
                }
                /* let the source know whether the string was successfully 
                 * transferred and used */
                event.setDropCompleted(success);
                
                event.consume();
            }
        });

        
        //******** Customize Schedule Target in Schedule When Drag Done ***********//
        //============================== [0][y] ===================================//
        //============================== [0][y] ===================================//
        //============================== [0][y] ===================================//
        //*************************************************************************//
        scheduleTarget[0][0].getSel().setOnDragOver(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* accept it only if it is  not dragged from the same node 
                 * and if it has a string data */
                if (event.getGestureSource() != scheduleTarget[0][0].getSel() &&
                        event.getDragboard().hasString()) {
                    /* allow for moving */
                    event.acceptTransferModes(TransferMode.MOVE);
                }
                
                event.consume();
            }
        });
        
        scheduleTarget[0][0].getSel().setOnDragDropped(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if there is a string data on dragboard, read it and use it */
                Dragboard db = event.getDragboard();
                boolean success = false;
                
                if (db.hasString()) {
//                    slotTarget[0][1].getSel().setText("");
                    schedule[0][0].getSel().setText(db.getString());
                    success = true;
                }
                /* let the source know whether the string was successfully 
                 * transferred and used */
                event.setDropCompleted(success);
                
                event.consume();
            }
        });
        
        //*************************************************************************//
        scheduleTarget[0][1].getSel().setOnDragOver(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* accept it only if it is  not dragged from the same node 
                 * and if it has a string data */
                if (event.getGestureSource() != scheduleTarget[0][1].getSel() &&
                        event.getDragboard().hasString()) {
                    /* allow for moving */
                    event.acceptTransferModes(TransferMode.MOVE);
                }
                
                event.consume();
            }
        });
        
        scheduleTarget[0][1].getSel().setOnDragDropped(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if there is a string data on dragboard, read it and use it */
                Dragboard db = event.getDragboard();
                boolean success = false;
                
                if (db.hasString()) {
//                    slotTarget[0][1].getSel().setText("");
                    schedule[0][1].getSel().setText(db.getString());
                    success = true;
                }
                /* let the source know whether the string was successfully 
                 * transferred and used */
                event.setDropCompleted(success);
                
                event.consume();
            }
        });
        
        //*************************************************************************//
        scheduleTarget[0][2].getSel().setOnDragOver(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* accept it only if it is  not dragged from the same node 
                 * and if it has a string data */
                if (event.getGestureSource() != scheduleTarget[0][2].getSel() &&
                        event.getDragboard().hasString()) {
                    /* allow for moving */
                    event.acceptTransferModes(TransferMode.MOVE);
                }
                
                event.consume();
            }
        });
        
        scheduleTarget[0][2].getSel().setOnDragDropped(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if there is a string data on dragboard, read it and use it */
                Dragboard db = event.getDragboard();
                boolean success = false;
                
                if (db.hasString()) {
//                    slotTarget[0][1].getSel().setText("");
                    schedule[0][2].getSel().setText(db.getString());
                    success = true;
                }
                /* let the source know whether the string was successfully 
                 * transferred and used */
                event.setDropCompleted(success);
                
                event.consume();
            }
        });
        
        //*************************************************************************//
        scheduleTarget[0][3].getSel().setOnDragOver(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* accept it only if it is  not dragged from the same node 
                 * and if it has a string data */
                if (event.getGestureSource() != scheduleTarget[0][3].getSel() &&
                        event.getDragboard().hasString()) {
                    /* allow for moving */
                    event.acceptTransferModes(TransferMode.MOVE);
                }
                
                event.consume();
            }
        });
        
        scheduleTarget[0][3].getSel().setOnDragDropped(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if there is a string data on dragboard, read it and use it */
                Dragboard db = event.getDragboard();
                boolean success = false;
                
                if (db.hasString()) {
//                    slotTarget[0][1].getSel().setText("");
                    schedule[0][3].getSel().setText(db.getString());
                    success = true;
                }
                /* let the source know whether the string was successfully 
                 * transferred and used */
                event.setDropCompleted(success);
                
                event.consume();
            }
        });
        
        //*************************************************************************//
        scheduleTarget[0][4].getSel().setOnDragOver(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* accept it only if it is  not dragged from the same node 
                 * and if it has a string data */
                if (event.getGestureSource() != scheduleTarget[0][4].getSel() &&
                        event.getDragboard().hasString()) {
                    /* allow for moving */
                    event.acceptTransferModes(TransferMode.MOVE);
                }
                
                event.consume();
            }
        });
        
        scheduleTarget[0][4].getSel().setOnDragDropped(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if there is a string data on dragboard, read it and use it */
                Dragboard db = event.getDragboard();
                boolean success = false;
                
                if (db.hasString()) {
//                    slotTarget[0][1].getSel().setText("");
                    schedule[0][4].getSel().setText(db.getString());
                    success = true;
                }
                /* let the source know whether the string was successfully 
                 * transferred and used */
                event.setDropCompleted(success);
                
                event.consume();
            }
        });
        
        //*************************************************************************//
        scheduleTarget[0][5].getSel().setOnDragOver(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* accept it only if it is  not dragged from the same node 
                 * and if it has a string data */
                if (event.getGestureSource() != scheduleTarget[0][5].getSel() &&
                        event.getDragboard().hasString()) {
                    /* allow for moving */
                    event.acceptTransferModes(TransferMode.MOVE);
                }
                
                event.consume();
            }
        });
        
        scheduleTarget[0][5].getSel().setOnDragDropped(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if there is a string data on dragboard, read it and use it */
                Dragboard db = event.getDragboard();
                boolean success = false;
                
                if (db.hasString()) {
//                    slotTarget[0][1].getSel().setText("");
                    schedule[0][5].getSel().setText(db.getString());
                    success = true;
                }
                /* let the source know whether the string was successfully 
                 * transferred and used */
                event.setDropCompleted(success);
                
                event.consume();
            }
        });
        
        //*************************************************************************//
        scheduleTarget[0][6].getSel().setOnDragOver(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* accept it only if it is  not dragged from the same node 
                 * and if it has a string data */
                if (event.getGestureSource() != scheduleTarget[0][6].getSel() &&
                        event.getDragboard().hasString()) {
                    /* allow for moving */
                    event.acceptTransferModes(TransferMode.MOVE);
                }
                
                event.consume();
            }
        });
        
        scheduleTarget[0][6].getSel().setOnDragDropped(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if there is a string data on dragboard, read it and use it */
                Dragboard db = event.getDragboard();
                boolean success = false;
                
                if (db.hasString()) {
//                    slotTarget[0][1].getSel().setText("");
                    schedule[0][6].getSel().setText(db.getString());
                    success = true;
                }
                /* let the source know whether the string was successfully 
                 * transferred and used */
                event.setDropCompleted(success);
                
                event.consume();
            }
        });
        
        //*************************************************************************//
        scheduleTarget[0][7].getSel().setOnDragOver(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* accept it only if it is  not dragged from the same node 
                 * and if it has a string data */
                if (event.getGestureSource() != scheduleTarget[0][7].getSel() &&
                        event.getDragboard().hasString()) {
                    /* allow for moving */
                    event.acceptTransferModes(TransferMode.MOVE);
                }
                
                event.consume();
            }
        });
        
        scheduleTarget[0][7].getSel().setOnDragDropped(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if there is a string data on dragboard, read it and use it */
                Dragboard db = event.getDragboard();
                boolean success = false;
                
                if (db.hasString()) {
//                    slotTarget[0][1].getSel().setText("");
                    schedule[0][7].getSel().setText(db.getString());
                    success = true;
                }
                /* let the source know whether the string was successfully 
                 * transferred and used */
                event.setDropCompleted(success);
                
                event.consume();
            }
        });
        
        
        //*************************************************************************//
        scheduleTarget[0][8].getSel().setOnDragOver(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* accept it only if it is  not dragged from the same node 
                 * and if it has a string data */
                if (event.getGestureSource() != scheduleTarget[0][8].getSel() &&
                        event.getDragboard().hasString()) {
                    /* allow for moving */
                    event.acceptTransferModes(TransferMode.MOVE);
                }
                
                event.consume();
            }
        });
        
        scheduleTarget[0][8].getSel().setOnDragDropped(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if there is a string data on dragboard, read it and use it */
                Dragboard db = event.getDragboard();
                boolean success = false;
                
                if (db.hasString()) {
//                    slotTarget[0][1].getSel().setText("");
                    schedule[0][8].getSel().setText(db.getString());
                    success = true;
                }
                /* let the source know whether the string was successfully 
                 * transferred and used */
                event.setDropCompleted(success);
                
                event.consume();
            }
        });
        
        
        //*************************************************************************//
        scheduleTarget[0][9].getSel().setOnDragOver(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* accept it only if it is  not dragged from the same node 
                 * and if it has a string data */
                if (event.getGestureSource() != scheduleTarget[0][9].getSel() &&
                        event.getDragboard().hasString()) {
                    /* allow for moving */
                    event.acceptTransferModes(TransferMode.MOVE);
                }
                
                event.consume();
            }
        });
        
        scheduleTarget[0][9].getSel().setOnDragDropped(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if there is a string data on dragboard, read it and use it */
                Dragboard db = event.getDragboard();
                boolean success = false;
                
                if (db.hasString()) {
//                    slotTarget[0][1].getSel().setText("");
                    schedule[0][9].getSel().setText(db.getString());
                    success = true;
                }
                /* let the source know whether the string was successfully 
                 * transferred and used */
                event.setDropCompleted(success);
                
                event.consume();
            }
        });
        
        
        //*************************************************************************//
        scheduleTarget[0][10].getSel().setOnDragOver(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* accept it only if it is  not dragged from the same node 
                 * and if it has a string data */
                if (event.getGestureSource() != scheduleTarget[0][10].getSel() &&
                        event.getDragboard().hasString()) {
                    /* allow for moving */
                    event.acceptTransferModes(TransferMode.MOVE);
                }
                
                event.consume();
            }
        });
        
        scheduleTarget[0][10].getSel().setOnDragDropped(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if there is a string data on dragboard, read it and use it */
                Dragboard db = event.getDragboard();
                boolean success = false;
                
                if (db.hasString()) {
//                    slotTarget[0][1].getSel().setText("");
                    schedule[0][10].getSel().setText(db.getString());
                    success = true;
                }
                /* let the source know whether the string was successfully 
                 * transferred and used */
                event.setDropCompleted(success);
                
                event.consume();
            }
        });
        
        
        //*************************************************************************//
        scheduleTarget[0][11].getSel().setOnDragOver(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* accept it only if it is  not dragged from the same node 
                 * and if it has a string data */
                if (event.getGestureSource() != scheduleTarget[0][11].getSel() &&
                        event.getDragboard().hasString()) {
                    /* allow for moving */
                    event.acceptTransferModes(TransferMode.MOVE);
                }
                
                event.consume();
            }
        });
        
        scheduleTarget[0][11].getSel().setOnDragDropped(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if there is a string data on dragboard, read it and use it */
                Dragboard db = event.getDragboard();
                boolean success = false;
                
                if (db.hasString()) {
//                    slotTarget[0][1].getSel().setText("");
                    schedule[0][11].getSel().setText(db.getString());
                    success = true;
                }
                /* let the source know whether the string was successfully 
                 * transferred and used */
                event.setDropCompleted(success);
                
                event.consume();
            }
        });
        
        //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++//
        //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++//
        
        //*************************************************************************//
        scheduleTarget[1][0].getSel().setOnDragOver(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* accept it only if it is  not dragged from the same node 
                 * and if it has a string data */
                if (event.getGestureSource() != scheduleTarget[1][0].getSel() &&
                        event.getDragboard().hasString()) {
                    /* allow for moving */
                    event.acceptTransferModes(TransferMode.MOVE);
                }
                
                event.consume();
            }
        });
        
        scheduleTarget[1][0].getSel().setOnDragDropped(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if there is a string data on dragboard, read it and use it */
                Dragboard db = event.getDragboard();
                boolean success = false;
                
                if (db.hasString()) {
//                    slotTarget[0][1].getSel().setText("");
                    schedule[1][0].getSel().setText(db.getString());
                    success = true;
                }
                /* let the source know whether the string was successfully 
                 * transferred and used */
                event.setDropCompleted(success);
                
                event.consume();
            }
        });
        
        //*************************************************************************//
        scheduleTarget[1][1].getSel().setOnDragOver(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* accept it only if it is  not dragged from the same node 
                 * and if it has a string data */
                if (event.getGestureSource() != scheduleTarget[1][1].getSel() &&
                        event.getDragboard().hasString()) {
                    /* allow for moving */
                    event.acceptTransferModes(TransferMode.MOVE);
                }
                
                event.consume();
            }
        });
        
        scheduleTarget[1][1].getSel().setOnDragDropped(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if there is a string data on dragboard, read it and use it */
                Dragboard db = event.getDragboard();
                boolean success = false;
                
                if (db.hasString()) {
//                    slotTarget[0][1].getSel().setText("");
                    schedule[1][1].getSel().setText(db.getString());
                    success = true;
                }
                /* let the source know whether the string was successfully 
                 * transferred and used */
                event.setDropCompleted(success);
                
                event.consume();
            }
        });
        
        //*************************************************************************//
        scheduleTarget[1][2].getSel().setOnDragOver(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* accept it only if it is  not dragged from the same node 
                 * and if it has a string data */
                if (event.getGestureSource() != scheduleTarget[1][2].getSel() &&
                        event.getDragboard().hasString()) {
                    /* allow for moving */
                    event.acceptTransferModes(TransferMode.MOVE);
                }
                
                event.consume();
            }
        });
        
        scheduleTarget[1][2].getSel().setOnDragDropped(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if there is a string data on dragboard, read it and use it */
                Dragboard db = event.getDragboard();
                boolean success = false;
                
                if (db.hasString()) {
//                    slotTarget[0][1].getSel().setText("");
                    schedule[1][2].getSel().setText(db.getString());
                    success = true;
                }
                /* let the source know whether the string was successfully 
                 * transferred and used */
                event.setDropCompleted(success);
                
                event.consume();
            }
        });
        
        //*************************************************************************//
        scheduleTarget[1][3].getSel().setOnDragOver(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* accept it only if it is  not dragged from the same node 
                 * and if it has a string data */
                if (event.getGestureSource() != scheduleTarget[1][3].getSel() &&
                        event.getDragboard().hasString()) {
                    /* allow for moving */
                    event.acceptTransferModes(TransferMode.MOVE);
                }
                
                event.consume();
            }
        });
        
        scheduleTarget[1][3].getSel().setOnDragDropped(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if there is a string data on dragboard, read it and use it */
                Dragboard db = event.getDragboard();
                boolean success = false;
                
                if (db.hasString()) {
//                    slotTarget[0][1].getSel().setText("");
                    schedule[1][3].getSel().setText(db.getString());
                    success = true;
                }
                /* let the source know whether the string was successfully 
                 * transferred and used */
                event.setDropCompleted(success);
                
                event.consume();
            }
        });
        
        //*************************************************************************//
        scheduleTarget[1][4].getSel().setOnDragOver(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* accept it only if it is  not dragged from the same node 
                 * and if it has a string data */
                if (event.getGestureSource() != scheduleTarget[1][4].getSel() &&
                        event.getDragboard().hasString()) {
                    /* allow for moving */
                    event.acceptTransferModes(TransferMode.MOVE);
                }
                
                event.consume();
            }
        });
        
        scheduleTarget[1][4].getSel().setOnDragDropped(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if there is a string data on dragboard, read it and use it */
                Dragboard db = event.getDragboard();
                boolean success = false;
                
                if (db.hasString()) {
//                    slotTarget[0][1].getSel().setText("");
                    schedule[1][4].getSel().setText(db.getString());
                    success = true;
                }
                /* let the source know whether the string was successfully 
                 * transferred and used */
                event.setDropCompleted(success);
                
                event.consume();
            }
        });
        
        //*************************************************************************//
        scheduleTarget[1][5].getSel().setOnDragOver(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* accept it only if it is  not dragged from the same node 
                 * and if it has a string data */
                if (event.getGestureSource() != scheduleTarget[1][5].getSel() &&
                        event.getDragboard().hasString()) {
                    /* allow for moving */
                    event.acceptTransferModes(TransferMode.MOVE);
                }
                
                event.consume();
            }
        });
        
        scheduleTarget[1][5].getSel().setOnDragDropped(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if there is a string data on dragboard, read it and use it */
                Dragboard db = event.getDragboard();
                boolean success = false;
                
                if (db.hasString()) {
//                    slotTarget[0][1].getSel().setText("");
                    schedule[1][5].getSel().setText(db.getString());
                    success = true;
                }
                /* let the source know whether the string was successfully 
                 * transferred and used */
                event.setDropCompleted(success);
                
                event.consume();
            }
        });
        
        //*************************************************************************//
        scheduleTarget[1][6].getSel().setOnDragOver(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* accept it only if it is  not dragged from the same node 
                 * and if it has a string data */
                if (event.getGestureSource() != scheduleTarget[1][6].getSel() &&
                        event.getDragboard().hasString()) {
                    /* allow for moving */
                    event.acceptTransferModes(TransferMode.MOVE);
                }
                
                event.consume();
            }
        });
        
        scheduleTarget[1][6].getSel().setOnDragDropped(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if there is a string data on dragboard, read it and use it */
                Dragboard db = event.getDragboard();
                boolean success = false;
                
                if (db.hasString()) {
//                    slotTarget[0][1].getSel().setText("");
                    schedule[1][6].getSel().setText(db.getString());
                    success = true;
                }
                /* let the source know whether the string was successfully 
                 * transferred and used */
                event.setDropCompleted(success);
                
                event.consume();
            }
        });
        
        //*************************************************************************//
        scheduleTarget[1][7].getSel().setOnDragOver(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* accept it only if it is  not dragged from the same node 
                 * and if it has a string data */
                if (event.getGestureSource() != scheduleTarget[1][7].getSel() &&
                        event.getDragboard().hasString()) {
                    /* allow for moving */
                    event.acceptTransferModes(TransferMode.MOVE);
                }
                
                event.consume();
            }
        });
        
        scheduleTarget[1][7].getSel().setOnDragDropped(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if there is a string data on dragboard, read it and use it */
                Dragboard db = event.getDragboard();
                boolean success = false;
                
                if (db.hasString()) {
//                    slotTarget[0][1].getSel().setText("");
                    schedule[1][7].getSel().setText(db.getString());
                    success = true;
                }
                /* let the source know whether the string was successfully 
                 * transferred and used */
                event.setDropCompleted(success);
                
                event.consume();
            }
        });
        
        
        //*************************************************************************//
        scheduleTarget[1][8].getSel().setOnDragOver(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* accept it only if it is  not dragged from the same node 
                 * and if it has a string data */
                if (event.getGestureSource() != scheduleTarget[1][8].getSel() &&
                        event.getDragboard().hasString()) {
                    /* allow for moving */
                    event.acceptTransferModes(TransferMode.MOVE);
                }
                
                event.consume();
            }
        });
        
        scheduleTarget[1][8].getSel().setOnDragDropped(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if there is a string data on dragboard, read it and use it */
                Dragboard db = event.getDragboard();
                boolean success = false;
                
                if (db.hasString()) {
//                    slotTarget[0][1].getSel().setText("");
                    schedule[1][8].getSel().setText(db.getString());
                    success = true;
                }
                /* let the source know whether the string was successfully 
                 * transferred and used */
                event.setDropCompleted(success);
                
                event.consume();
            }
        });
        
        
        //*************************************************************************//
        scheduleTarget[1][9].getSel().setOnDragOver(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* accept it only if it is  not dragged from the same node 
                 * and if it has a string data */
                if (event.getGestureSource() != scheduleTarget[1][9].getSel() &&
                        event.getDragboard().hasString()) {
                    /* allow for moving */
                    event.acceptTransferModes(TransferMode.MOVE);
                }
                
                event.consume();
            }
        });
        
        scheduleTarget[1][9].getSel().setOnDragDropped(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if there is a string data on dragboard, read it and use it */
                Dragboard db = event.getDragboard();
                boolean success = false;
                
                if (db.hasString()) {
//                    slotTarget[0][1].getSel().setText("");
                    schedule[1][9].getSel().setText(db.getString());
                    success = true;
                }
                /* let the source know whether the string was successfully 
                 * transferred and used */
                event.setDropCompleted(success);
                
                event.consume();
            }
        });
        
        
        //*************************************************************************//
        scheduleTarget[1][10].getSel().setOnDragOver(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* accept it only if it is  not dragged from the same node 
                 * and if it has a string data */
                if (event.getGestureSource() != scheduleTarget[1][10].getSel() &&
                        event.getDragboard().hasString()) {
                    /* allow for moving */
                    event.acceptTransferModes(TransferMode.MOVE);
                }
                
                event.consume();
            }
        });
        
        scheduleTarget[1][10].getSel().setOnDragDropped(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if there is a string data on dragboard, read it and use it */
                Dragboard db = event.getDragboard();
                boolean success = false;
                
                if (db.hasString()) {
//                    slotTarget[0][1].getSel().setText("");
                    schedule[1][10].getSel().setText(db.getString());
                    success = true;
                }
                /* let the source know whether the string was successfully 
                 * transferred and used */
                event.setDropCompleted(success);
                
                event.consume();
            }
        });
        
        
        //*************************************************************************//
        scheduleTarget[1][11].getSel().setOnDragOver(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* accept it only if it is  not dragged from the same node 
                 * and if it has a string data */
                if (event.getGestureSource() != scheduleTarget[1][11].getSel() &&
                        event.getDragboard().hasString()) {
                    /* allow for moving */
                    event.acceptTransferModes(TransferMode.MOVE);
                }
                
                event.consume();
            }
        });
        
        scheduleTarget[1][11].getSel().setOnDragDropped(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if there is a string data on dragboard, read it and use it */
                Dragboard db = event.getDragboard();
                boolean success = false;
                
                if (db.hasString()) {
//                    slotTarget[0][1].getSel().setText("");
                    schedule[1][11].getSel().setText(db.getString());
                    success = true;
                }
                /* let the source know whether the string was successfully 
                 * transferred and used */
                event.setDropCompleted(success);
                
                event.consume();
            }
        });
        
        //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++//
        //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++//
        
        //*************************************************************************//
        scheduleTarget[2][0].getSel().setOnDragOver(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* accept it only if it is  not dragged from the same node 
                 * and if it has a string data */
                if (event.getGestureSource() != scheduleTarget[2][0].getSel() &&
                        event.getDragboard().hasString()) {
                    /* allow for moving */
                    event.acceptTransferModes(TransferMode.MOVE);
                }
                
                event.consume();
            }
        });
        
        scheduleTarget[2][0].getSel().setOnDragDropped(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if there is a string data on dragboard, read it and use it */
                Dragboard db = event.getDragboard();
                boolean success = false;
                
                if (db.hasString()) {
//                    slotTarget[0][1].getSel().setText("");
                    schedule[2][0].getSel().setText(db.getString());
                    success = true;
                }
                /* let the source know whether the string was successfully 
                 * transferred and used */
                event.setDropCompleted(success);
                
                event.consume();
            }
        });
        
        //*************************************************************************//
        scheduleTarget[2][1].getSel().setOnDragOver(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* accept it only if it is  not dragged from the same node 
                 * and if it has a string data */
                if (event.getGestureSource() != scheduleTarget[2][1].getSel() &&
                        event.getDragboard().hasString()) {
                    /* allow for moving */
                    event.acceptTransferModes(TransferMode.MOVE);
                }
                
                event.consume();
            }
        });
        
        scheduleTarget[2][1].getSel().setOnDragDropped(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if there is a string data on dragboard, read it and use it */
                Dragboard db = event.getDragboard();
                boolean success = false;
                
                if (db.hasString()) {
//                    slotTarget[0][1].getSel().setText("");
                    schedule[2][1].getSel().setText(db.getString());
                    success = true;
                }
                /* let the source know whether the string was successfully 
                 * transferred and used */
                event.setDropCompleted(success);
                
                event.consume();
            }
        });
        
        //*************************************************************************//
        scheduleTarget[2][2].getSel().setOnDragOver(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* accept it only if it is  not dragged from the same node 
                 * and if it has a string data */
                if (event.getGestureSource() != scheduleTarget[2][2].getSel() &&
                        event.getDragboard().hasString()) {
                    /* allow for moving */
                    event.acceptTransferModes(TransferMode.MOVE);
                }
                
                event.consume();
            }
        });
        
        scheduleTarget[2][2].getSel().setOnDragDropped(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if there is a string data on dragboard, read it and use it */
                Dragboard db = event.getDragboard();
                boolean success = false;
                
                if (db.hasString()) {
//                    slotTarget[0][1].getSel().setText("");
                    schedule[2][2].getSel().setText(db.getString());
                    success = true;
                }
                /* let the source know whether the string was successfully 
                 * transferred and used */
                event.setDropCompleted(success);
                
                event.consume();
            }
        });
        
        //*************************************************************************//
        scheduleTarget[2][3].getSel().setOnDragOver(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* accept it only if it is  not dragged from the same node 
                 * and if it has a string data */
                if (event.getGestureSource() != scheduleTarget[2][3].getSel() &&
                        event.getDragboard().hasString()) {
                    /* allow for moving */
                    event.acceptTransferModes(TransferMode.MOVE);
                }
                
                event.consume();
            }
        });
        
        scheduleTarget[2][3].getSel().setOnDragDropped(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if there is a string data on dragboard, read it and use it */
                Dragboard db = event.getDragboard();
                boolean success = false;
                
                if (db.hasString()) {
//                    slotTarget[0][1].getSel().setText("");
                    schedule[2][3].getSel().setText(db.getString());
                    success = true;
                }
                /* let the source know whether the string was successfully 
                 * transferred and used */
                event.setDropCompleted(success);
                
                event.consume();
            }
        });
        
        //*************************************************************************//
        scheduleTarget[2][4].getSel().setOnDragOver(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* accept it only if it is  not dragged from the same node 
                 * and if it has a string data */
                if (event.getGestureSource() != scheduleTarget[2][4].getSel() &&
                        event.getDragboard().hasString()) {
                    /* allow for moving */
                    event.acceptTransferModes(TransferMode.MOVE);
                }
                
                event.consume();
            }
        });
        
        scheduleTarget[2][4].getSel().setOnDragDropped(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if there is a string data on dragboard, read it and use it */
                Dragboard db = event.getDragboard();
                boolean success = false;
                
                if (db.hasString()) {
//                    slotTarget[0][1].getSel().setText("");
                    schedule[2][4].getSel().setText(db.getString());
                    success = true;
                }
                /* let the source know whether the string was successfully 
                 * transferred and used */
                event.setDropCompleted(success);
                
                event.consume();
            }
        });
        
        //*************************************************************************//
        scheduleTarget[2][5].getSel().setOnDragOver(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* accept it only if it is  not dragged from the same node 
                 * and if it has a string data */
                if (event.getGestureSource() != scheduleTarget[2][5].getSel() &&
                        event.getDragboard().hasString()) {
                    /* allow for moving */
                    event.acceptTransferModes(TransferMode.MOVE);
                }
                
                event.consume();
            }
        });
        
        scheduleTarget[2][5].getSel().setOnDragDropped(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if there is a string data on dragboard, read it and use it */
                Dragboard db = event.getDragboard();
                boolean success = false;
                
                if (db.hasString()) {
//                    slotTarget[0][1].getSel().setText("");
                    schedule[2][5].getSel().setText(db.getString());
                    success = true;
                }
                /* let the source know whether the string was successfully 
                 * transferred and used */
                event.setDropCompleted(success);
                
                event.consume();
            }
        });
        
        //*************************************************************************//
        scheduleTarget[2][6].getSel().setOnDragOver(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* accept it only if it is  not dragged from the same node 
                 * and if it has a string data */
                if (event.getGestureSource() != scheduleTarget[2][6].getSel() &&
                        event.getDragboard().hasString()) {
                    /* allow for moving */
                    event.acceptTransferModes(TransferMode.MOVE);
                }
                
                event.consume();
            }
        });
        
        scheduleTarget[2][6].getSel().setOnDragDropped(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if there is a string data on dragboard, read it and use it */
                Dragboard db = event.getDragboard();
                boolean success = false;
                
                if (db.hasString()) {
//                    slotTarget[0][1].getSel().setText("");
                    schedule[2][6].getSel().setText(db.getString());
                    success = true;
                }
                /* let the source know whether the string was successfully 
                 * transferred and used */
                event.setDropCompleted(success);
                
                event.consume();
            }
        });
        
        //*************************************************************************//
        scheduleTarget[2][7].getSel().setOnDragOver(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* accept it only if it is  not dragged from the same node 
                 * and if it has a string data */
                if (event.getGestureSource() != scheduleTarget[2][7].getSel() &&
                        event.getDragboard().hasString()) {
                    /* allow for moving */
                    event.acceptTransferModes(TransferMode.MOVE);
                }
                
                event.consume();
            }
        });
        
        scheduleTarget[2][7].getSel().setOnDragDropped(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if there is a string data on dragboard, read it and use it */
                Dragboard db = event.getDragboard();
                boolean success = false;
                
                if (db.hasString()) {
//                    slotTarget[0][1].getSel().setText("");
                    schedule[2][7].getSel().setText(db.getString());
                    success = true;
                }
                /* let the source know whether the string was successfully 
                 * transferred and used */
                event.setDropCompleted(success);
                
                event.consume();
            }
        });
        
        
        //*************************************************************************//
        scheduleTarget[2][8].getSel().setOnDragOver(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* accept it only if it is  not dragged from the same node 
                 * and if it has a string data */
                if (event.getGestureSource() != scheduleTarget[2][8].getSel() &&
                        event.getDragboard().hasString()) {
                    /* allow for moving */
                    event.acceptTransferModes(TransferMode.MOVE);
                }
                
                event.consume();
            }
        });
        
        scheduleTarget[2][8].getSel().setOnDragDropped(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if there is a string data on dragboard, read it and use it */
                Dragboard db = event.getDragboard();
                boolean success = false;
                
                if (db.hasString()) {
//                    slotTarget[0][1].getSel().setText("");
                    schedule[2][8].getSel().setText(db.getString());
                    success = true;
                }
                /* let the source know whether the string was successfully 
                 * transferred and used */
                event.setDropCompleted(success);
                
                event.consume();
            }
        });
        
        
        //*************************************************************************//
        scheduleTarget[2][9].getSel().setOnDragOver(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* accept it only if it is  not dragged from the same node 
                 * and if it has a string data */
                if (event.getGestureSource() != scheduleTarget[2][9].getSel() &&
                        event.getDragboard().hasString()) {
                    /* allow for moving */
                    event.acceptTransferModes(TransferMode.MOVE);
                }
                
                event.consume();
            }
        });
        
        scheduleTarget[2][9].getSel().setOnDragDropped(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if there is a string data on dragboard, read it and use it */
                Dragboard db = event.getDragboard();
                boolean success = false;
                
                if (db.hasString()) {
//                    slotTarget[0][1].getSel().setText("");
                    schedule[2][9].getSel().setText(db.getString());
                    success = true;
                }
                /* let the source know whether the string was successfully 
                 * transferred and used */
                event.setDropCompleted(success);
                
                event.consume();
            }
        });
        
        
        //*************************************************************************//
        scheduleTarget[2][10].getSel().setOnDragOver(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* accept it only if it is  not dragged from the same node 
                 * and if it has a string data */
                if (event.getGestureSource() != scheduleTarget[2][10].getSel() &&
                        event.getDragboard().hasString()) {
                    /* allow for moving */
                    event.acceptTransferModes(TransferMode.MOVE);
                }
                
                event.consume();
            }
        });
        
        scheduleTarget[2][10].getSel().setOnDragDropped(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if there is a string data on dragboard, read it and use it */
                Dragboard db = event.getDragboard();
                boolean success = false;
                
                if (db.hasString()) {
//                    slotTarget[0][1].getSel().setText("");
                    schedule[2][10].getSel().setText(db.getString());
                    success = true;
                }
                /* let the source know whether the string was successfully 
                 * transferred and used */
                event.setDropCompleted(success);
                
                event.consume();
            }
        });
        
        
        //*************************************************************************//
        scheduleTarget[2][11].getSel().setOnDragOver(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* accept it only if it is  not dragged from the same node 
                 * and if it has a string data */
                if (event.getGestureSource() != scheduleTarget[2][11].getSel() &&
                        event.getDragboard().hasString()) {
                    /* allow for moving */
                    event.acceptTransferModes(TransferMode.MOVE);
                }
                
                event.consume();
            }
        });
        
        scheduleTarget[2][11].getSel().setOnDragDropped(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if there is a string data on dragboard, read it and use it */
                Dragboard db = event.getDragboard();
                boolean success = false;
                
                if (db.hasString()) {
//                    slotTarget[0][1].getSel().setText("");
                    schedule[2][11].getSel().setText(db.getString());
                    success = true;
                }
                /* let the source know whether the string was successfully 
                 * transferred and used */
                event.setDropCompleted(success);
                
                event.consume();
            }
        });
        
        //*************************************************************************//
        scheduleTarget[3][0].getSel().setOnDragOver(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* accept it only if it is  not dragged from the same node 
                 * and if it has a string data */
                if (event.getGestureSource() != scheduleTarget[3][0].getSel() &&
                        event.getDragboard().hasString()) {
                    /* allow for moving */
                    event.acceptTransferModes(TransferMode.MOVE);
                }
                
                event.consume();
            }
        });
        
        scheduleTarget[3][0].getSel().setOnDragDropped(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if there is a string data on dragboard, read it and use it */
                Dragboard db = event.getDragboard();
                boolean success = false;
                
                if (db.hasString()) {
//                    slotTarget[0][1].getSel().setText("");
                    schedule[3][0].getSel().setText(db.getString());
                    success = true;
                }
                /* let the source know whether the string was successfully 
                 * transferred and used */
                event.setDropCompleted(success);
                
                event.consume();
            }
        });
        
        //*************************************************************************//
        scheduleTarget[3][1].getSel().setOnDragOver(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* accept it only if it is  not dragged from the same node 
                 * and if it has a string data */
                if (event.getGestureSource() != scheduleTarget[3][1].getSel() &&
                        event.getDragboard().hasString()) {
                    /* allow for moving */
                    event.acceptTransferModes(TransferMode.MOVE);
                }
                
                event.consume();
            }
        });
        
        scheduleTarget[3][1].getSel().setOnDragDropped(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if there is a string data on dragboard, read it and use it */
                Dragboard db = event.getDragboard();
                boolean success = false;
                
                if (db.hasString()) {
//                    slotTarget[0][1].getSel().setText("");
                    schedule[3][1].getSel().setText(db.getString());
                    success = true;
                }
                /* let the source know whether the string was successfully 
                 * transferred and used */
                event.setDropCompleted(success);
                
                event.consume();
            }
        });
        
        //*************************************************************************//
        scheduleTarget[3][2].getSel().setOnDragOver(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* accept it only if it is  not dragged from the same node 
                 * and if it has a string data */
                if (event.getGestureSource() != scheduleTarget[3][2].getSel() &&
                        event.getDragboard().hasString()) {
                    /* allow for moving */
                    event.acceptTransferModes(TransferMode.MOVE);
                }
                
                event.consume();
            }
        });
        
        scheduleTarget[3][2].getSel().setOnDragDropped(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if there is a string data on dragboard, read it and use it */
                Dragboard db = event.getDragboard();
                boolean success = false;
                
                if (db.hasString()) {
//                    slotTarget[0][1].getSel().setText("");
                    schedule[3][2].getSel().setText(db.getString());
                    success = true;
                }
                /* let the source know whether the string was successfully 
                 * transferred and used */
                event.setDropCompleted(success);
                
                event.consume();
            }
        });
        
        //*************************************************************************//
        scheduleTarget[3][3].getSel().setOnDragOver(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* accept it only if it is  not dragged from the same node 
                 * and if it has a string data */
                if (event.getGestureSource() != scheduleTarget[3][3].getSel() &&
                        event.getDragboard().hasString()) {
                    /* allow for moving */
                    event.acceptTransferModes(TransferMode.MOVE);
                }
                
                event.consume();
            }
        });
        
        scheduleTarget[3][3].getSel().setOnDragDropped(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if there is a string data on dragboard, read it and use it */
                Dragboard db = event.getDragboard();
                boolean success = false;
                
                if (db.hasString()) {
//                    slotTarget[0][1].getSel().setText("");
                    schedule[3][3].getSel().setText(db.getString());
                    success = true;
                }
                /* let the source know whether the string was successfully 
                 * transferred and used */
                event.setDropCompleted(success);
                
                event.consume();
            }
        });
        
        //*************************************************************************//
        scheduleTarget[3][4].getSel().setOnDragOver(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* accept it only if it is  not dragged from the same node 
                 * and if it has a string data */
                if (event.getGestureSource() != scheduleTarget[3][4].getSel() &&
                        event.getDragboard().hasString()) {
                    /* allow for moving */
                    event.acceptTransferModes(TransferMode.MOVE);
                }
                
                event.consume();
            }
        });
        
        scheduleTarget[3][4].getSel().setOnDragDropped(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if there is a string data on dragboard, read it and use it */
                Dragboard db = event.getDragboard();
                boolean success = false;
                
                if (db.hasString()) {
//                    slotTarget[0][1].getSel().setText("");
                    schedule[3][4].getSel().setText(db.getString());
                    success = true;
                }
                /* let the source know whether the string was successfully 
                 * transferred and used */
                event.setDropCompleted(success);
                
                event.consume();
            }
        });
        
        //*************************************************************************//
        scheduleTarget[3][5].getSel().setOnDragOver(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* accept it only if it is  not dragged from the same node 
                 * and if it has a string data */
                if (event.getGestureSource() != scheduleTarget[3][5].getSel() &&
                        event.getDragboard().hasString()) {
                    /* allow for moving */
                    event.acceptTransferModes(TransferMode.MOVE);
                }
                
                event.consume();
            }
        });
        
        scheduleTarget[3][5].getSel().setOnDragDropped(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if there is a string data on dragboard, read it and use it */
                Dragboard db = event.getDragboard();
                boolean success = false;
                
                if (db.hasString()) {
//                    slotTarget[0][1].getSel().setText("");
                    schedule[3][5].getSel().setText(db.getString());
                    success = true;
                }
                /* let the source know whether the string was successfully 
                 * transferred and used */
                event.setDropCompleted(success);
                
                event.consume();
            }
        });
        
        //*************************************************************************//
        scheduleTarget[3][6].getSel().setOnDragOver(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* accept it only if it is  not dragged from the same node 
                 * and if it has a string data */
                if (event.getGestureSource() != scheduleTarget[3][6].getSel() &&
                        event.getDragboard().hasString()) {
                    /* allow for moving */
                    event.acceptTransferModes(TransferMode.MOVE);
                }
                
                event.consume();
            }
        });
        
        scheduleTarget[3][6].getSel().setOnDragDropped(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if there is a string data on dragboard, read it and use it */
                Dragboard db = event.getDragboard();
                boolean success = false;
                
                if (db.hasString()) {
//                    slotTarget[0][1].getSel().setText("");
                    schedule[3][6].getSel().setText(db.getString());
                    success = true;
                }
                /* let the source know whether the string was successfully 
                 * transferred and used */
                event.setDropCompleted(success);
                
                event.consume();
            }
        });
        
        //*************************************************************************//
        scheduleTarget[3][7].getSel().setOnDragOver(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* accept it only if it is  not dragged from the same node 
                 * and if it has a string data */
                if (event.getGestureSource() != scheduleTarget[3][7].getSel() &&
                        event.getDragboard().hasString()) {
                    /* allow for moving */
                    event.acceptTransferModes(TransferMode.MOVE);
                }
                
                event.consume();
            }
        });
        
        scheduleTarget[3][7].getSel().setOnDragDropped(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if there is a string data on dragboard, read it and use it */
                Dragboard db = event.getDragboard();
                boolean success = false;
                
                if (db.hasString()) {
//                    slotTarget[0][1].getSel().setText("");
                    schedule[3][7].getSel().setText(db.getString());
                    success = true;
                }
                /* let the source know whether the string was successfully 
                 * transferred and used */
                event.setDropCompleted(success);
                
                event.consume();
            }
        });
        
        
        //*************************************************************************//
        scheduleTarget[3][8].getSel().setOnDragOver(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* accept it only if it is  not dragged from the same node 
                 * and if it has a string data */
                if (event.getGestureSource() != scheduleTarget[3][8].getSel() &&
                        event.getDragboard().hasString()) {
                    /* allow for moving */
                    event.acceptTransferModes(TransferMode.MOVE);
                }
                
                event.consume();
            }
        });
        
        scheduleTarget[3][8].getSel().setOnDragDropped(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if there is a string data on dragboard, read it and use it */
                Dragboard db = event.getDragboard();
                boolean success = false;
                
                if (db.hasString()) {
//                    slotTarget[0][1].getSel().setText("");
                    schedule[3][8].getSel().setText(db.getString());
                    success = true;
                }
                /* let the source know whether the string was successfully 
                 * transferred and used */
                event.setDropCompleted(success);
                
                event.consume();
            }
        });
        
        
        //*************************************************************************//
        scheduleTarget[3][9].getSel().setOnDragOver(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* accept it only if it is  not dragged from the same node 
                 * and if it has a string data */
                if (event.getGestureSource() != scheduleTarget[3][9].getSel() &&
                        event.getDragboard().hasString()) {
                    /* allow for moving */
                    event.acceptTransferModes(TransferMode.MOVE);
                }
                
                event.consume();
            }
        });
        
        scheduleTarget[3][9].getSel().setOnDragDropped(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if there is a string data on dragboard, read it and use it */
                Dragboard db = event.getDragboard();
                boolean success = false;
                
                if (db.hasString()) {
//                    slotTarget[0][1].getSel().setText("");
                    schedule[3][9].getSel().setText(db.getString());
                    success = true;
                }
                /* let the source know whether the string was successfully 
                 * transferred and used */
                event.setDropCompleted(success);
                
                event.consume();
            }
        });
        
        
        //*************************************************************************//
        scheduleTarget[3][10].getSel().setOnDragOver(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* accept it only if it is  not dragged from the same node 
                 * and if it has a string data */
                if (event.getGestureSource() != scheduleTarget[3][10].getSel() &&
                        event.getDragboard().hasString()) {
                    /* allow for moving */
                    event.acceptTransferModes(TransferMode.MOVE);
                }
                
                event.consume();
            }
        });
        
        scheduleTarget[3][10].getSel().setOnDragDropped(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if there is a string data on dragboard, read it and use it */
                Dragboard db = event.getDragboard();
                boolean success = false;
                
                if (db.hasString()) {
//                    slotTarget[0][1].getSel().setText("");
                    schedule[3][10].getSel().setText(db.getString());
                    success = true;
                }
                /* let the source know whether the string was successfully 
                 * transferred and used */
                event.setDropCompleted(success);
                
                event.consume();
            }
        });
        
        //*************************************************************************//
        scheduleTarget[3][11].getSel().setOnDragOver(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* accept it only if it is  not dragged from the same node 
                 * and if it has a string data */
                if (event.getGestureSource() != scheduleTarget[3][11].getSel() &&
                        event.getDragboard().hasString()) {
                    /* allow for moving */
                    event.acceptTransferModes(TransferMode.MOVE);
                }
                
                event.consume();
            }
        });
        
        scheduleTarget[3][11].getSel().setOnDragDropped(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if there is a string data on dragboard, read it and use it */
                Dragboard db = event.getDragboard();
                boolean success = false;
                
                if (db.hasString()) {
//                    slotTarget[0][1].getSel().setText("");
                    schedule[3][11].getSel().setText(db.getString());
                    success = true;
                }
                /* let the source know whether the string was successfully 
                 * transferred and used */
                event.setDropCompleted(success);
                
                event.consume();
            }
        });
        
        //*************************************************************************//
        scheduleTarget[4][0].getSel().setOnDragOver(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* accept it only if it is  not dragged from the same node 
                 * and if it has a string data */
                if (event.getGestureSource() != scheduleTarget[4][0].getSel() &&
                        event.getDragboard().hasString()) {
                    /* allow for moving */
                    event.acceptTransferModes(TransferMode.MOVE);
                }
                
                event.consume();
            }
        });
        
        scheduleTarget[4][0].getSel().setOnDragDropped(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if there is a string data on dragboard, read it and use it */
                Dragboard db = event.getDragboard();
                boolean success = false;
                
                if (db.hasString()) {
//                    slotTarget[0][1].getSel().setText("");
                    schedule[4][0].getSel().setText(db.getString());
                    success = true;
                }
                /* let the source know whether the string was successfully 
                 * transferred and used */
                event.setDropCompleted(success);
                
                event.consume();
            }
        });
        
        //*************************************************************************//
        scheduleTarget[4][1].getSel().setOnDragOver(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* accept it only if it is  not dragged from the same node 
                 * and if it has a string data */
                if (event.getGestureSource() != scheduleTarget[4][1].getSel() &&
                        event.getDragboard().hasString()) {
                    /* allow for moving */
                    event.acceptTransferModes(TransferMode.MOVE);
                }
                
                event.consume();
            }
        });
        
        scheduleTarget[4][1].getSel().setOnDragDropped(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if there is a string data on dragboard, read it and use it */
                Dragboard db = event.getDragboard();
                boolean success = false;
                
                if (db.hasString()) {
//                    slotTarget[0][1].getSel().setText("");
                    schedule[4][1].getSel().setText(db.getString());
                    success = true;
                }
                /* let the source know whether the string was successfully 
                 * transferred and used */
                event.setDropCompleted(success);
                
                event.consume();
            }
        });
        
        //*************************************************************************//
        scheduleTarget[4][2].getSel().setOnDragOver(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* accept it only if it is  not dragged from the same node 
                 * and if it has a string data */
                if (event.getGestureSource() != scheduleTarget[4][2].getSel() &&
                        event.getDragboard().hasString()) {
                    /* allow for moving */
                    event.acceptTransferModes(TransferMode.MOVE);
                }
                
                event.consume();
            }
        });
        
        scheduleTarget[4][2].getSel().setOnDragDropped(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if there is a string data on dragboard, read it and use it */
                Dragboard db = event.getDragboard();
                boolean success = false;
                
                if (db.hasString()) {
//                    slotTarget[0][1].getSel().setText("");
                    schedule[4][2].getSel().setText(db.getString());
                    success = true;
                }
                /* let the source know whether the string was successfully 
                 * transferred and used */
                event.setDropCompleted(success);
                
                event.consume();
            }
        });
        
        //*************************************************************************//
        scheduleTarget[4][3].getSel().setOnDragOver(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* accept it only if it is  not dragged from the same node 
                 * and if it has a string data */
                if (event.getGestureSource() != scheduleTarget[4][3].getSel() &&
                        event.getDragboard().hasString()) {
                    /* allow for moving */
                    event.acceptTransferModes(TransferMode.MOVE);
                }
                
                event.consume();
            }
        });
        
        scheduleTarget[4][3].getSel().setOnDragDropped(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if there is a string data on dragboard, read it and use it */
                Dragboard db = event.getDragboard();
                boolean success = false;
                
                if (db.hasString()) {
//                    slotTarget[0][1].getSel().setText("");
                    schedule[4][3].getSel().setText(db.getString());
                    success = true;
                }
                /* let the source know whether the string was successfully 
                 * transferred and used */
                event.setDropCompleted(success);
                
                event.consume();
            }
        });
        
        //*************************************************************************//
        scheduleTarget[4][4].getSel().setOnDragOver(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* accept it only if it is  not dragged from the same node 
                 * and if it has a string data */
                if (event.getGestureSource() != scheduleTarget[4][4].getSel() &&
                        event.getDragboard().hasString()) {
                    /* allow for moving */
                    event.acceptTransferModes(TransferMode.MOVE);
                }
                
                event.consume();
            }
        });
        
        scheduleTarget[4][4].getSel().setOnDragDropped(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if there is a string data on dragboard, read it and use it */
                Dragboard db = event.getDragboard();
                boolean success = false;
                
                if (db.hasString()) {
//                    slotTarget[0][1].getSel().setText("");
                    schedule[4][4].getSel().setText(db.getString());
                    success = true;
                }
                /* let the source know whether the string was successfully 
                 * transferred and used */
                event.setDropCompleted(success);
                
                event.consume();
            }
        });
        
        //*************************************************************************//
        scheduleTarget[4][5].getSel().setOnDragOver(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* accept it only if it is  not dragged from the same node 
                 * and if it has a string data */
                if (event.getGestureSource() != scheduleTarget[4][5].getSel() &&
                        event.getDragboard().hasString()) {
                    /* allow for moving */
                    event.acceptTransferModes(TransferMode.MOVE);
                }
                
                event.consume();
            }
        });
        
        scheduleTarget[4][5].getSel().setOnDragDropped(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if there is a string data on dragboard, read it and use it */
                Dragboard db = event.getDragboard();
                boolean success = false;
                
                if (db.hasString()) {
//                    slotTarget[0][1].getSel().setText("");
                    schedule[4][5].getSel().setText(db.getString());
                    success = true;
                }
                /* let the source know whether the string was successfully 
                 * transferred and used */
                event.setDropCompleted(success);
                
                event.consume();
            }
        });
        
        //*************************************************************************//
        scheduleTarget[4][6].getSel().setOnDragOver(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* accept it only if it is  not dragged from the same node 
                 * and if it has a string data */
                if (event.getGestureSource() != scheduleTarget[4][6].getSel() &&
                        event.getDragboard().hasString()) {
                    /* allow for moving */
                    event.acceptTransferModes(TransferMode.MOVE);
                }
                
                event.consume();
            }
        });
        
        scheduleTarget[4][6].getSel().setOnDragDropped(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if there is a string data on dragboard, read it and use it */
                Dragboard db = event.getDragboard();
                boolean success = false;
                
                if (db.hasString()) {
//                    slotTarget[0][1].getSel().setText("");
                    schedule[4][6].getSel().setText(db.getString());
                    success = true;
                }
                /* let the source know whether the string was successfully 
                 * transferred and used */
                event.setDropCompleted(success);
                
                event.consume();
            }
        });
        
        //*************************************************************************//
        scheduleTarget[4][7].getSel().setOnDragOver(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* accept it only if it is  not dragged from the same node 
                 * and if it has a string data */
                if (event.getGestureSource() != scheduleTarget[4][7].getSel() &&
                        event.getDragboard().hasString()) {
                    /* allow for moving */
                    event.acceptTransferModes(TransferMode.MOVE);
                }
                
                event.consume();
            }
        });
        
        scheduleTarget[4][7].getSel().setOnDragDropped(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if there is a string data on dragboard, read it and use it */
                Dragboard db = event.getDragboard();
                boolean success = false;
                
                if (db.hasString()) {
//                    slotTarget[0][1].getSel().setText("");
                    schedule[4][7].getSel().setText(db.getString());
                    success = true;
                }
                /* let the source know whether the string was successfully 
                 * transferred and used */
                event.setDropCompleted(success);
                
                event.consume();
            }
        });
        
        
        //*************************************************************************//
        scheduleTarget[4][8].getSel().setOnDragOver(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* accept it only if it is  not dragged from the same node 
                 * and if it has a string data */
                if (event.getGestureSource() != scheduleTarget[4][8].getSel() &&
                        event.getDragboard().hasString()) {
                    /* allow for moving */
                    event.acceptTransferModes(TransferMode.MOVE);
                }
                
                event.consume();
            }
        });
        
        scheduleTarget[4][8].getSel().setOnDragDropped(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if there is a string data on dragboard, read it and use it */
                Dragboard db = event.getDragboard();
                boolean success = false;
                
                if (db.hasString()) {
//                    slotTarget[0][1].getSel().setText("");
                    schedule[4][8].getSel().setText(db.getString());
                    success = true;
                }
                /* let the source know whether the string was successfully 
                 * transferred and used */
                event.setDropCompleted(success);
                
                event.consume();
            }
        });
        
        
        //*************************************************************************//
        scheduleTarget[4][9].getSel().setOnDragOver(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* accept it only if it is  not dragged from the same node 
                 * and if it has a string data */
                if (event.getGestureSource() != scheduleTarget[4][9].getSel() &&
                        event.getDragboard().hasString()) {
                    /* allow for moving */
                    event.acceptTransferModes(TransferMode.MOVE);
                }
                
                event.consume();
            }
        });
        
        scheduleTarget[4][9].getSel().setOnDragDropped(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if there is a string data on dragboard, read it and use it */
                Dragboard db = event.getDragboard();
                boolean success = false;
                
                if (db.hasString()) {
//                    slotTarget[0][1].getSel().setText("");
                    schedule[4][9].getSel().setText(db.getString());
                    success = true;
                }
                /* let the source know whether the string was successfully 
                 * transferred and used */
                event.setDropCompleted(success);
                
                event.consume();
            }
        });
        
        
        //*************************************************************************//
        scheduleTarget[4][10].getSel().setOnDragOver(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* accept it only if it is  not dragged from the same node 
                 * and if it has a string data */
                if (event.getGestureSource() != scheduleTarget[4][10].getSel() &&
                        event.getDragboard().hasString()) {
                    /* allow for moving */
                    event.acceptTransferModes(TransferMode.MOVE);
                }
                
                event.consume();
            }
        });
        
        
        
        scheduleTarget[4][10].getSel().setOnDragDropped(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if there is a string data on dragboard, read it and use it */
                Dragboard db = event.getDragboard();
                boolean success = false;
                
                if (db.hasString()) {
//                    slotTarget[0][1].getSel().setText("");
                    schedule[4][10].getSel().setText(db.getString());
                    success = true;
                }
                /* let the source know whether the string was successfully 
                 * transferred and used */
                event.setDropCompleted(success);
                
                event.consume();
            }
        });
        
        
        //*************************************************************************//
        scheduleTarget[4][11].getSel().setOnDragOver(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* accept it only if it is  not dragged from the same node 
                 * and if it has a string data */
                if (event.getGestureSource() != scheduleTarget[4][11].getSel() &&
                        event.getDragboard().hasString()) {
                    /* allow for moving */
                    event.acceptTransferModes(TransferMode.MOVE);
                }
                
                event.consume();
            }
        });
        
        scheduleTarget[4][11].getSel().setOnDragDropped(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if there is a string data on dragboard, read it and use it */
                Dragboard db = event.getDragboard();
                boolean success = false;
                
                if (db.hasString()) {
//                    slotTarget[0][1].getSel().setText("");
                    schedule[4][11].getSel().setText(db.getString());
                    success = true;
                }
                /* let the source know whether the string was successfully 
                 * transferred and used */
                event.setDropCompleted(success);
                
                event.consume();
            }
        });
        
        //============================== [0][y] ===================================//
        //============================== [0][y] ===================================//
        //============================== [0][y] ===================================//
        //******** Customize Schedule Target in Schedule When Drag Done ***********//
        
        
        
        //******** Customize slot source in Slot Pengubahan When Drag Done ********//
        //============================== [0][y] ===================================//
        //============================== [0][y] ===================================//
        //============================== [0][y] ===================================//
        slotSource[0][0].getSel().setOnDragDone(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if the data was successfully moved, clear it */
                if (event.getTransferMode() == TransferMode.MOVE) {
                    slotSource[0][0].getSel().setText("");
                }
                
                event.consume();
            }
        });
        
        slotSource[0][1].getSel().setOnDragDone(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if the data was successfully moved, clear it */
                if (event.getTransferMode() == TransferMode.MOVE) {
                    slotSource[0][1].getSel().setText("");
                }
                
                event.consume();
            }
        });
        
        slotSource[1][0].getSel().setOnDragDone(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if the data was successfully moved, clear it */
                if (event.getTransferMode() == TransferMode.MOVE) {
                    slotSource[1][0].getSel().setText("");
                }
                
                event.consume();
            }
        });
        
        slotSource[1][1].getSel().setOnDragDone(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if the data was successfully moved, clear it */
                if (event.getTransferMode() == TransferMode.MOVE) {
                    slotSource[1][1].getSel().setText("");
                }
                
                event.consume();
            }
        });
        
        //============================== [0][y] ===================================//
        //============================== [0][y] ===================================//
        //============================== [0][y] ===================================//
        //******** End of slot source in Slot Pengubahan When Drag Done ********//
        
        //******** Customize schedule source in Schedule Table When Drag Done ********//
        //============================== [0][y] ===================================//
        //============================== [0][y] ===================================//
        //============================== [0][y] ===================================//
        
        schedule[0][0].getSel().setOnDragDone(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if the data was successfully moved, clear it */
                if (event.getTransferMode() == TransferMode.MOVE) {
                    schedule[0][0].getSel().setText("");
                }
                
                event.consume();
            }
        });
        
        //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%//
        schedule[0][1].getSel().setOnDragDone(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if the data was successfully moved, clear it */
                if (event.getTransferMode() == TransferMode.MOVE) {
                    schedule[0][1].getSel().setText("");
                }
                
                event.consume();
            }
        });
        
        schedule[0][2].getSel().setOnDragDone(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if the data was successfully moved, clear it */
                if (event.getTransferMode() == TransferMode.MOVE) {
                    schedule[0][2].getSel().setText("");
                }
                
                event.consume();
            }
        });
        
        schedule[0][3].getSel().setOnDragDone(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if the data was successfully moved, clear it */
                if (event.getTransferMode() == TransferMode.MOVE) {
                    schedule[0][3].getSel().setText("");
                }
                
                event.consume();
            }
        });
        
        schedule[0][4].getSel().setOnDragDone(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if the data was successfully moved, clear it */
                if (event.getTransferMode() == TransferMode.MOVE) {
                    schedule[0][4].getSel().setText("");
                }
                
                event.consume();
            }
        });
        
        schedule[0][5].getSel().setOnDragDone(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if the data was successfully moved, clear it */
                if (event.getTransferMode() == TransferMode.MOVE) {
                    schedule[0][5].getSel().setText("");
                }
                
                event.consume();
            }
        });
        
        schedule[0][6].getSel().setOnDragDone(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if the data was successfully moved, clear it */
                if (event.getTransferMode() == TransferMode.MOVE) {
                    schedule[0][6].getSel().setText("");
                }
                
                event.consume();
            }
        });
        
        schedule[0][7].getSel().setOnDragDone(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if the data was successfully moved, clear it */
                if (event.getTransferMode() == TransferMode.MOVE) {
                    schedule[0][7].getSel().setText("");
                }
                
                event.consume();
            }
        });
        
        schedule[0][8].getSel().setOnDragDone(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if the data was successfully moved, clear it */
                if (event.getTransferMode() == TransferMode.MOVE) {
                    schedule[0][8].getSel().setText("");
                }
                
                event.consume();
            }
        });
        
        schedule[0][9].getSel().setOnDragDone(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if the data was successfully moved, clear it */
                if (event.getTransferMode() == TransferMode.MOVE) {
                    schedule[0][9].getSel().setText("");
                }
                
                event.consume();
            }
        });
        
        schedule[0][10].getSel().setOnDragDone(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if the data was successfully moved, clear it */
                if (event.getTransferMode() == TransferMode.MOVE) {
                    schedule[0][10].getSel().setText("");
                }
                
                event.consume();
            }
        });
        
        schedule[0][11].getSel().setOnDragDone(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if the data was successfully moved, clear it */
                if (event.getTransferMode() == TransferMode.MOVE) {
                    schedule[0][11].getSel().setText("");
                }
                
                event.consume();
            }
        });
        
        
        //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%//
        schedule[1][1].getSel().setOnDragDone(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if the data was successfully moved, clear it */
                if (event.getTransferMode() == TransferMode.MOVE) {
                    schedule[1][1].getSel().setText("");
                }
                
                event.consume();
            }
        });
        
        schedule[1][2].getSel().setOnDragDone(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if the data was successfully moved, clear it */
                if (event.getTransferMode() == TransferMode.MOVE) {
                    schedule[1][2].getSel().setText("");
                }
                
                event.consume();
            }
        });
        
        schedule[1][3].getSel().setOnDragDone(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if the data was successfully moved, clear it */
                if (event.getTransferMode() == TransferMode.MOVE) {
                    schedule[1][3].getSel().setText("");
                }
                
                event.consume();
            }
        });
        
        schedule[1][4].getSel().setOnDragDone(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if the data was successfully moved, clear it */
                if (event.getTransferMode() == TransferMode.MOVE) {
                    schedule[1][4].getSel().setText("");
                }
                
                event.consume();
            }
        });
        
        schedule[1][5].getSel().setOnDragDone(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if the data was successfully moved, clear it */
                if (event.getTransferMode() == TransferMode.MOVE) {
                    schedule[1][5].getSel().setText("");
                }
                
                event.consume();
            }
        });
        
        schedule[1][6].getSel().setOnDragDone(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if the data was successfully moved, clear it */
                if (event.getTransferMode() == TransferMode.MOVE) {
                    schedule[1][6].getSel().setText("");
                }
                
                event.consume();
            }
        });
        
        schedule[1][7].getSel().setOnDragDone(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if the data was successfully moved, clear it */
                if (event.getTransferMode() == TransferMode.MOVE) {
                    schedule[1][7].getSel().setText("");
                }
                
                event.consume();
            }
        });
        
        schedule[1][8].getSel().setOnDragDone(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if the data was successfully moved, clear it */
                if (event.getTransferMode() == TransferMode.MOVE) {
                    schedule[1][8].getSel().setText("");
                }
                
                event.consume();
            }
        });
        
        schedule[1][9].getSel().setOnDragDone(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if the data was successfully moved, clear it */
                if (event.getTransferMode() == TransferMode.MOVE) {
                    schedule[1][9].getSel().setText("");
                }
                
                event.consume();
            }
        });
        
        schedule[1][10].getSel().setOnDragDone(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if the data was successfully moved, clear it */
                if (event.getTransferMode() == TransferMode.MOVE) {
                    schedule[1][10].getSel().setText("");
                }
                
                event.consume();
            }
        });
        
        schedule[1][11].getSel().setOnDragDone(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if the data was successfully moved, clear it */
                if (event.getTransferMode() == TransferMode.MOVE) {
                    schedule[1][11].getSel().setText("");
                }
                
                event.consume();
            }
        });
        
        //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%//
        schedule[2][1].getSel().setOnDragDone(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if the data was successfully moved, clear it */
                if (event.getTransferMode() == TransferMode.MOVE) {
                    schedule[2][1].getSel().setText("");
                }
                
                event.consume();
            }
        });
        
        schedule[2][2].getSel().setOnDragDone(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if the data was successfully moved, clear it */
                if (event.getTransferMode() == TransferMode.MOVE) {
                    schedule[2][2].getSel().setText("");
                }
                
                event.consume();
            }
        });
        
        schedule[2][3].getSel().setOnDragDone(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if the data was successfully moved, clear it */
                if (event.getTransferMode() == TransferMode.MOVE) {
                    schedule[2][3].getSel().setText("");
                }
                
                event.consume();
            }
        });
        
        schedule[2][4].getSel().setOnDragDone(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if the data was successfully moved, clear it */
                if (event.getTransferMode() == TransferMode.MOVE) {
                    schedule[2][4].getSel().setText("");
                }
                
                event.consume();
            }
        });
        
        schedule[2][5].getSel().setOnDragDone(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if the data was successfully moved, clear it */
                if (event.getTransferMode() == TransferMode.MOVE) {
                    schedule[2][5].getSel().setText("");
                }
                
                event.consume();
            }
        });
        
        schedule[2][6].getSel().setOnDragDone(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if the data was successfully moved, clear it */
                if (event.getTransferMode() == TransferMode.MOVE) {
                    schedule[2][6].getSel().setText("");
                }
                
                event.consume();
            }
        });
        
        schedule[2][7].getSel().setOnDragDone(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if the data was successfully moved, clear it */
                if (event.getTransferMode() == TransferMode.MOVE) {
                    schedule[2][7].getSel().setText("");
                }
                
                event.consume();
            }
        });
        
        schedule[2][8].getSel().setOnDragDone(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if the data was successfully moved, clear it */
                if (event.getTransferMode() == TransferMode.MOVE) {
                    schedule[2][8].getSel().setText("");
                }
                
                event.consume();
            }
        });
        
        schedule[2][9].getSel().setOnDragDone(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if the data was successfully moved, clear it */
                if (event.getTransferMode() == TransferMode.MOVE) {
                    schedule[2][9].getSel().setText("");
                }
                
                event.consume();
            }
        });
        
        schedule[2][10].getSel().setOnDragDone(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if the data was successfully moved, clear it */
                if (event.getTransferMode() == TransferMode.MOVE) {
                    schedule[2][10].getSel().setText("");
                }
                
                event.consume();
            }
        });
        
        schedule[2][11].getSel().setOnDragDone(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if the data was successfully moved, clear it */
                if (event.getTransferMode() == TransferMode.MOVE) {
                    schedule[2][11].getSel().setText("");
                }
                
                event.consume();
            }
        });
        
        //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%//
        schedule[3][1].getSel().setOnDragDone(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if the data was successfully moved, clear it */
                if (event.getTransferMode() == TransferMode.MOVE) {
                    schedule[3][1].getSel().setText("");
                }
                
                event.consume();
            }
        });
        
        schedule[3][2].getSel().setOnDragDone(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if the data was successfully moved, clear it */
                if (event.getTransferMode() == TransferMode.MOVE) {
                    schedule[3][2].getSel().setText("");
                }
                
                event.consume();
            }
        });
        
        schedule[3][3].getSel().setOnDragDone(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if the data was successfully moved, clear it */
                if (event.getTransferMode() == TransferMode.MOVE) {
                    schedule[3][3].getSel().setText("");
                }
                
                event.consume();
            }
        });
        
        schedule[3][4].getSel().setOnDragDone(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if the data was successfully moved, clear it */
                if (event.getTransferMode() == TransferMode.MOVE) {
                    schedule[3][4].getSel().setText("");
                }
                
                event.consume();
            }
        });
        
        schedule[3][5].getSel().setOnDragDone(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if the data was successfully moved, clear it */
                if (event.getTransferMode() == TransferMode.MOVE) {
                    schedule[3][5].getSel().setText("");
                }
                
                event.consume();
            }
        });
        
        schedule[3][6].getSel().setOnDragDone(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if the data was successfully moved, clear it */
                if (event.getTransferMode() == TransferMode.MOVE) {
                    schedule[3][6].getSel().setText("");
                }
                
                event.consume();
            }
        });
        
        schedule[3][7].getSel().setOnDragDone(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if the data was successfully moved, clear it */
                if (event.getTransferMode() == TransferMode.MOVE) {
                    schedule[3][7].getSel().setText("");
                }
                
                event.consume();
            }
        });
        
        schedule[3][8].getSel().setOnDragDone(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if the data was successfully moved, clear it */
                if (event.getTransferMode() == TransferMode.MOVE) {
                    schedule[3][8].getSel().setText("");
                }
                
                event.consume();
            }
        });
        
        schedule[3][9].getSel().setOnDragDone(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if the data was successfully moved, clear it */
                if (event.getTransferMode() == TransferMode.MOVE) {
                    schedule[3][9].getSel().setText("");
                }
                
                event.consume();
            }
        });
        
        schedule[3][10].getSel().setOnDragDone(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if the data was successfully moved, clear it */
                if (event.getTransferMode() == TransferMode.MOVE) {
                    schedule[3][10].getSel().setText("");
                }
                
                event.consume();
            }
        });
        
        schedule[3][11].getSel().setOnDragDone(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if the data was successfully moved, clear it */
                if (event.getTransferMode() == TransferMode.MOVE) {
                    schedule[3][11].getSel().setText("");
                }
                
                event.consume();
            }
        });
        
        
        //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%//
        schedule[4][1].getSel().setOnDragDone(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if the data was successfully moved, clear it */
                if (event.getTransferMode() == TransferMode.MOVE) {
                    schedule[4][1].getSel().setText("");
                }
                
                event.consume();
            }
        });
        
        schedule[4][2].getSel().setOnDragDone(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if the data was successfully moved, clear it */
                if (event.getTransferMode() == TransferMode.MOVE) {
                    schedule[4][2].getSel().setText("");
                }
                
                event.consume();
            }
        });
        
        schedule[4][3].getSel().setOnDragDone(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if the data was successfully moved, clear it */
                if (event.getTransferMode() == TransferMode.MOVE) {
                    schedule[4][3].getSel().setText("");
                }
                
                event.consume();
            }
        });
        
        schedule[4][4].getSel().setOnDragDone(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if the data was successfully moved, clear it */
                if (event.getTransferMode() == TransferMode.MOVE) {
                    schedule[4][4].getSel().setText("");
                }
                
                event.consume();
            }
        });
        
        schedule[4][5].getSel().setOnDragDone(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if the data was successfully moved, clear it */
                if (event.getTransferMode() == TransferMode.MOVE) {
                    schedule[4][5].getSel().setText("");
                }
                
                event.consume();
            }
        });
        
        schedule[4][6].getSel().setOnDragDone(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if the data was successfully moved, clear it */
                if (event.getTransferMode() == TransferMode.MOVE) {
                    schedule[4][6].getSel().setText("");
                }
                
                event.consume();
            }
        });
        
        schedule[4][7].getSel().setOnDragDone(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if the data was successfully moved, clear it */
                if (event.getTransferMode() == TransferMode.MOVE) {
                    schedule[4][7].getSel().setText("");
                }
                
                event.consume();
            }
        });
        
        schedule[4][8].getSel().setOnDragDone(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if the data was successfully moved, clear it */
                if (event.getTransferMode() == TransferMode.MOVE) {
                    schedule[4][8].getSel().setText("");
                }
                
                event.consume();
            }
        });
        
        schedule[4][9].getSel().setOnDragDone(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if the data was successfully moved, clear it */
                if (event.getTransferMode() == TransferMode.MOVE) {
                    schedule[4][9].getSel().setText("");
                }
                
                event.consume();
            }
        });
        
        schedule[4][10].getSel().setOnDragDone(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if the data was successfully moved, clear it */
                if (event.getTransferMode() == TransferMode.MOVE) {
                    schedule[4][10].getSel().setText("");
                }
                
                event.consume();
            }
        });
        
        schedule[4][11].getSel().setOnDragDone(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* if the data was successfully moved, clear it */
                if (event.getTransferMode() == TransferMode.MOVE) {
                    schedule[4][11].getSel().setText("");
                }
                
                event.consume();
            }
        });
        
        
        //============================== [0][y] ===================================//
        //============================== [0][y] ===================================//
        //============================== [0][y] ===================================//
        //******** End of schedule source in Slot Pengubahan When Drag Done ********//
        
        
//        source.setOnDragDone(new EventHandler <DragEvent>() {
//            @Override
//            public void handle(DragEvent event) {
//                System.out.println("source");
//                /* if the data was successfully moved, clear it */
//                if (event.getTransferMode() == TransferMode.MOVE) {
//                    source.setText("");
//                }
//                
//                event.consume();
//            }
//        });
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        root.getChildren().add(slotSource[0][0].getSel());
        root.getChildren().add(slotSource[0][1].getSel());
        root.getChildren().add(slotSource[1][0].getSel());
        root.getChildren().add(slotSource[1][1].getSel());
        
//        root.getChildren().add(source);
        stage.setScene(scene);
        stage.show();
    }
    
    
    public static void main(String[] args) {
        Application.launch(args);
        System.out.println("any");
    }
}
