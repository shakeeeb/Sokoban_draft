package sokoban.file;

import application.Main.SokobanPropertyType;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import properties_manager.PropertiesManager;
import sokoban.game.SokobanGameStateManager;
import sokoban.ui.SokobanUI;

public class SokobanFileLoader {
    private FileChooser fileChooser;
    private SokobanUI ui;
    private SokobanGameStateManager gsm;
    private Stage primaryStage;
//TODO
    public SokobanFileLoader(SokobanUI ui){
        this.ui = ui;
        this.gsm = ui.getGSM();
        this.primaryStage = ui.GetStage();
        
                
    }
    // so this'll load hte file for the most part
    public void writeFile(){
        try{
        File blank = new File("./data/blank.sok");
        FileOutputStream fos = new FileOutputStream("blank.sok");
        DataOutputStream dos = new DataOutputStream(fos);
        
        
        } catch (FileNotFoundException f){f.printStackTrace();}
    }
    int gridColumns;
    int gridRows;
    public void OpenFile(String levelFile){
        PropertiesManager props = PropertiesManager.getPropertiesManager();
            String fileName = props.getProperty(SokobanPropertyType.DATA_PATH) + levelFile;
            System.out.println(fileName);
            File fileToOpen = new File(fileName);
            
            try {
                if (fileToOpen != null) {
                    // LET'S USE A FAST LOADING TECHNIQUE. WE'LL LOAD ALL OF THE
                    // BYTES AT ONCE INTO A BYTE ARRAY, AND THEN PICK THAT APART.
                    // THIS IS FAST BECAUSE IT ONLY HAS TO DO FILE READING ONCE
                    byte[] bytes = new byte[Long.valueOf(fileToOpen.length()).intValue()];
                    ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
                    FileInputStream fis = new FileInputStream(fileToOpen);
                    BufferedInputStream bis = new BufferedInputStream(fis);

                    // HERE IT IS, THE ONLY READY REQUEST WE NEED
                    bis.read(bytes);
                    bis.close();

                    // NOW WE NEED TO LOAD THE DATA FROM THE BYTE ARRAY
                    DataInputStream dis = new DataInputStream(bais);

                    // NOTE THAT WE NEED TO LOAD THE DATA IN THE SAME
                    // ORDER AND FORMAT AS WE SAVED IT
                    // FIRST READ THE GRID DIMENSIONS
                    int initGridColumns = dis.readInt();
                    int initGridRows = dis.readInt();
                    int[][] newGrid = new int[initGridColumns][initGridRows];

                    // AND NOW ALL THE CELL VALUES
                    for (int i = 0; i < initGridColumns; i++) {
                        for (int j = 0; j < initGridRows; j++) {
                            newGrid[i][j] = dis.readInt();
                        }
                    }

                    ui.SetGrid(newGrid);
                    gridColumns = initGridColumns;
                    gridRows = initGridRows;
                    ui.setGridthings(gridColumns, gridRows);
                    ui.repaint(); //i'll call this from the event handler in ui
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
}
