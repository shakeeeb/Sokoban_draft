package sokoban.ui;

import application.Main;
import application.Main.SokobanPropertyType;
import java.io.File;

import java.io.IOException;
import java.net.URL;
import java.time.Clock;
import java.time.Duration;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;
import java.util.TimerTask;
import javafx.application.Platform;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLDocument;
import properties_manager.PropertiesManager;
import sokoban.file.SokobanFileLoader; //fileLoader isnt really needed here. its needed in the event hadler
import sokoban.game.SokobanGameData;
import sokoban.game.SokobanGameStateManager;

public class SokobanUI extends Pane {

    /**
     * The SokobanUIState represents the four screen states that are possible
     * for the Sokoban game application. Depending on which state is in current
     * use, different controls will be visible.
     */
    public enum SokobanUIState {

        SPLASH_SCREEN_STATE, PLAY_GAME_STATE, VIEW_STATS_STATE, VIEW_HELP_STATE,
        LEVEL1_STATE, LEVEL2_STATE, LEVEL3_STATE, LEVEL4_STATE, LEVEL5_STATE, LEVEL6_STATE, LEVEL7_STATE
    }

    // mainStage
    private Stage primaryStage;
    boolean clicked = false;

    // mainPane
    private BorderPane mainPane;
    private BorderPane hmPane;

    // SplashScreen
    private ImageView splashScreenImageView;
    private StackPane splashScreenPane;
    private Label splashScreenImageLabel;
    private FlowPane levelSelectionPane;
    private ArrayList<Button> levelButtons;

    // NorthToolBar
    private HBox northToolbar;
    private Button gameButton;
    private Button statsButton;
    private Button undoButton;
    private Button exitButton;
    private Button backButton;

    // GamePane
    private Label SokobanLabel;
    private Button newGameButton;
    private HBox letterButtonsPane;
    private HashMap<Character, Button> letterButtons;
    private BorderPane gamePanel = new BorderPane();

    //StatsPane
    private ScrollPane statsScrollPane;
    private JEditorPane statsPane;

    //HelpPane
    private BorderPane helpPanel;
    private JScrollPane helpScrollPane;
    private JEditorPane helpPane;
    private Button homeButton;
    private Pane workspace;

    // Padding
    private Insets marginlessInsets;
    String buzzfile = "sounds/buzz.mp3";
    String losefile = "sounds/lose.mp3";
    String movefile = "sounds/move.mp3";
    String pushfile = "sounds/push.mp3";
    String Victoryfile = "sounds/victory.mp3";
    
    //sounds
    private Media buzzr = new Media(getClass().getResource(buzzfile).toString());
    private Media loser = new Media(getClass().getResource(losefile).toString());
    private Media mover = new Media(getClass().getResource(movefile).toString());
    private Media pushr = new Media(getClass().getResource(pushfile).toString());
    private Media victoryr = new Media(getClass().getResource(Victoryfile).toString());
    
    private MediaPlayer buzz = new MediaPlayer(buzzr);
    private MediaPlayer lose = new MediaPlayer(loser);
    private MediaPlayer move = new MediaPlayer(mover);
    private MediaPlayer push = new MediaPlayer(pushr);
    private MediaPlayer victory = new MediaPlayer(victoryr);
    // Image path
    private String ImgPath = "file:images/";
    // mainPane weight && height
    private int paneWidth;
    private int paneHeigth;

    // THIS CLASS WILL HANDLE ALL ACTION EVENTS FOR THIS PROGRAM
    private SokobanEventHandler eventHandler;
    private SokobanErrorHandler errorHandler;
    private SokobanDocumentManager docManager;
    private SokobanFileLoader fileLoader;
        // AND HERE IS THE GRID WE'RE MAKING
    private int gridColumns;
    private int gridRows;
    private int grid[][];
    private int preservedGrid[][];
    private int tempHist[][];
    
    
    GridRenderer gridRenderer;
    private GraphicsContext gc;
    SokobanGameStateManager gsm;
    
    ArrayList<int[]> dots;
    Stack<int[][]> gamehistory = new Stack<int[][]>();
    
    private int[] sokobansHere = new int[2];
    int prevPanel = 0;

    public SokobanUI() {
        gridRenderer = new GridRenderer();
        gsm = new SokobanGameStateManager(this);
        eventHandler = new SokobanEventHandler(this);
        errorHandler = new SokobanErrorHandler(primaryStage);
        docManager = new SokobanDocumentManager(this);
        fileLoader = new SokobanFileLoader(this);
        initMainPane();
        //initSokobanUI();
        initSplashScreen();
    }
    
    public void SetGrid(int[][] grid){
        this.grid = grid;
        gridRows = grid.length;
        gridColumns = grid[0].length;
    }
    public void repaint() {
        initSokobanUI();
         this.gridRenderer.repaint();
    }
    public void setGridthings(int cols, int rows){
        this.gridColumns = cols;
        this.gridRows = rows;
    }

    public int[][] GetGrid(){
        return this.grid;
    }

    public void SetStage(Stage stage) {
        primaryStage = stage;
    }

    public BorderPane GetMainPane() {
        return this.mainPane;
    }

    public SokobanGameStateManager getGSM() {
        return gsm;
    }
    
    public Stage GetStage(){
        return primaryStage;
    }

    public SokobanDocumentManager getDocManager() {
        return docManager;
    }

    public SokobanErrorHandler getErrorHandler() {
        return errorHandler;
    }

    public JEditorPane getHelpPane() {
        return helpPane;
    }
    
    

    public void initMainPane() {
        marginlessInsets = new Insets(5, 5, 5, 5);
        mainPane = new BorderPane();

        PropertiesManager props = PropertiesManager.getPropertiesManager();
        paneWidth = Integer.parseInt(props
                .getProperty(SokobanPropertyType.WINDOW_WIDTH));
        paneHeigth = Integer.parseInt(props
                .getProperty(SokobanPropertyType.WINDOW_HEIGHT));
        mainPane.resize(paneWidth, paneHeigth);
        mainPane.setPadding(marginlessInsets);
    }

    public void initSplashScreen() {

        // INIT THE SPLASH SCREEN CONTROLS
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        String splashScreenImagePath = props
                .getProperty(SokobanPropertyType.SPLASH_SCREEN_IMAGE_NAME);
        props.addProperty(SokobanPropertyType.INSETS, "5");
        String str = props.getProperty(SokobanPropertyType.INSETS);
        // work on this shit later, more important shit to do
        Media welcome = new Media(getClass().getResource("sounds/Final Fantasy VII - Opening Theme, Bombing Mission [HQ].mp3").toString());
        MediaPlayer player = new MediaPlayer(welcome);
        player.play();
        

        splashScreenPane = new StackPane();
        //splashScreenPane.setStyle("-fx-background-image: url(" + splashScreenImagePath +")");

        Image splashScreenImage = loadImage(splashScreenImagePath);
        splashScreenImageView = new ImageView(splashScreenImage);

        splashScreenImageLabel = new Label();
        splashScreenImageLabel.setGraphic(splashScreenImageView);
        // move the label position to fix the pane
        splashScreenImageLabel.setLayoutX(-45);
        splashScreenPane.getChildren().add(splashScreenImageLabel);

        // GET THE LIST OF LEVEL OPTIONS
        ArrayList<String> levels = props
                .getPropertyOptionsList(SokobanPropertyType.LEVEL_OPTIONS);
        ArrayList<String> levelImages = props
                .getPropertyOptionsList(SokobanPropertyType.LEVEL_IMAGE_NAMES);
        ArrayList<String> levelFiles = props
                .getPropertyOptionsList(SokobanPropertyType.LEVEL_FILES);
        gamehistory = new Stack<int[][]>();
        

        levelSelectionPane = new FlowPane(); //change this to flowpane, and set it on top of the other pane
        levelSelectionPane.setVgap(10.0);
        levelSelectionPane.setHgap(10.0);
        levelSelectionPane.setAlignment(Pos.CENTER);
        
        // add key listener
        levelButtons = new ArrayList<Button>();
        for (int i = 0; i < levels.size(); i++) {

            // GET THE LIST OF LEVEL OPTIONS
            String level = levels.get(i);
            String levelFile = levelFiles.get(i);
            String levelImageName = levelImages.get(i);
            Image levelImage = loadButtonImage(levelImageName);
            ImageView levelImageView = new ImageView(levelImage);

            // AND BUILD THE BUTTON
            Button levelButton = new Button();
            levelButton.setGraphic(levelImageView);
            
            // CONNECT THE BUTTON TO THE EVENT HANDLER
            levelButton.setOnAction((ActionEvent event) -> {
                eventHandler.respondToSelectLevelRequest(levelFile);
            });
            // TODO
            levelSelectionPane.getChildren().add(levelButton);
            // TODO: enable only the first level
            levelButton.setDisable(false);
        }
        
        splashScreenPane.getChildren().add(levelSelectionPane);
        levelSelectionPane.toFront();
        mainPane.setCenter(splashScreenPane);
    }
    public void haveWon(){
        int number = 0;
        for(int i=0;i <dots.size();i++){
            if (testWin(dots.get(i))){
                number++;
        }else {
                number = 0;
        }
        if(number == dots.size()){ //this is the win
        gsm.win();
        Stage winStage = new Stage();
        winStage.initModality(Modality.WINDOW_MODAL);
        BorderPane border = new BorderPane();
        System.out.println("YOU WON");
        Button okButton = new Button("aww yeeeee");
        border.setBottom(okButton);
        Label messagelabel = new Label("Dude, you just WON! Congrats");
        border.setCenter(messagelabel);
        Scene scene = new Scene(border, 200, 100);
        winStage.setScene(scene);
        winStage.show();
        victory.play();
        okButton.setOnAction(e -> {
            changeWorkspace(SokobanUIState.SPLASH_SCREEN_STATE);
            winStage.close();
        });
        }
        }
    }
    public boolean boxOnDot(int[] arr){//pass in the box coordinates   
        for(int[] win: dots){
            if((arr[0]== win[0])&&(arr[1]==win[1]))
                return true;
        } return false;
    }
    
    public boolean testWin(int[] arr){//pass in the array expecting dots, pass in dot coordinates
        return (grid[arr[0]][arr[1]] == 2);
    }
    
    public void storeTargets(){
        dots = new ArrayList<int[]>();
        int i = 0;
        int j = 0;
        for(;i < grid.length; i++){
            for(j=0; j<grid[0].length;j++){
                if(preservedGrid[i][j] == 3){
                 int[] arr = new int[2];
                 arr[0] = i;
                 arr[1] = j;
                 dots.add(arr);
                }    
            }
        }
    }
    
    public int[][] cloneGrid(){
        int i = 0;//grid.length; columns
        int j = 0;//grid[0].length; rows
        int[][] destination = new int[grid.length][grid[0].length];
        for(;i < grid.length;i++){
            for(j=0;j < grid[0].length;j++){
                destination[i][j] = grid[i][j];
            }
        }
        return destination;
        
    }

    /**
     * This method initializes the language-specific game controls, which
     * includes the three primary game screens.
     */
    public void initSokobanUI() { // this is where we go from the splash screen
        //should probably use this method in the constructor.
        // FIRST REMOVE THE SPLASH SCREEN
        mainPane.getChildren().clear();
        gamePanel.setCenter(gridRenderer);
        preservedGrid = preserveGrid();
        storeTargets();
        // add key listeners to the gamepanel, or to teh mainpane
        mainPane.setOnKeyPressed((KeyEvent ke) -> {
            eventHandler.respondToKeyEvent(ke);
        });
        gridRenderer.setOnMouseClicked(mouseEvent -> {
            
            double w = gridRenderer.getWidth() / gridColumns;
            double col = mouseEvent.getX() / w;
            double h = gridRenderer.getHeight() / gridRows;
            double row = mouseEvent.getY() / h;
            int intcol = (int)col;
            int introw = (int)row;
            int sokX = sokobansHere[0];
            int sokY = sokobansHere[1];
            if((intcol == sokX) && (introw == sokY)){
                clicked = true;
            } else {
                if((isAdjacentToSokoban(intcol, introw))){
                    if(intcol == sokX-1){//move left
                        moveSokoban(KeyCode.LEFT);
                    } else if(intcol == sokX+1){//right
                        moveSokoban(KeyCode.RIGHT);
                    } else if(introw == sokY+1){//down
                        moveSokoban(KeyCode.DOWN);
                    } else if (introw == sokY-1){//up
                        moveSokoban(KeyCode.UP);
                    }
                }
                // you see its a valid move, column row, and  is it adjacent and open
                //intcol introw, check if it's adjacent to sokoban, and check if its valid
                //move sokobans position 1 unit  
            }
        });
        gridRenderer.setOnMouseDragged(mouseEvent -> {
            
            double w = gridRenderer.getWidth() / gridColumns;
            double col = mouseEvent.getX() / w;
            double h = gridRenderer.getHeight() / gridRows;
            double row = mouseEvent.getY() / h;
            int intcol = (int)col;
            int introw = (int)row;
            int sokX = sokobansHere[0];
            int sokY = sokobansHere[1];
            System.out.println("sok coordinates"+ sokX +" "+sokY);
            //grid[intcol][introw]; 
            System.out.println("columnsrows"+ intcol +" "+ introw);
            if((intcol == sokX) && (introw == sokY)){
                clicked = true;
            } else {
                //if he's not clicked, second click
                if((isAdjacentToSokoban(intcol, introw))){
                    if(intcol == sokX-1){//move left
                        moveSokoban(KeyCode.LEFT);
                    } else if(intcol == sokX+1){
                        moveSokoban(KeyCode.RIGHT);
                    } else if(introw == sokY+1){
                        moveSokoban(KeyCode.DOWN);
                    } else if (introw == sokY-1){
                        moveSokoban(KeyCode.UP);
                    }
                }
                // you see its a valid move, column row, and  is it adjacent and open
                //intcol introw, check if it's adjacent to sokoban, and check if its valid
                //move sokobans position 1 unit  
            }
        });

        // GET THE UPDATED TITLE
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        String title = props.getProperty(SokobanPropertyType.GAME_TITLE_TEXT);
        primaryStage.setTitle(title);

        // THEN ADD ALL THE STUFF WE MIGHT NOW USE
        gsm.makeNewGame();
        initNorthToolbar();

        // OUR WORKSPACE WILL STORE EITHER THE GAME, STATS,
        // OR HELP UI AT ANY ONE TIME
        initWorkspace();
        //initGameScreen();
        //initStatsPane();
        //initHelpPane();

        // WE'LL START OUT WITH THE GAME SCREEN
        changeWorkspace(SokobanUIState.PLAY_GAME_STATE);

    }
    public void getTime(Label timelabel){
        timelabel.setText(gsm.getTime());
    }
    
    public boolean isAdjacentToSokoban(int x, int y){//col, row
        int sokx = sokobansHere[0];
        int soky = sokobansHere[1];
        if((x == sokx-1)||(x-1==sokx)){
            if(y == soky)
                return true; 
        }else if((y == soky-1)||(y-1==soky)){
            if (x == sokx)
                return true;
        } else {
            return false;
        } return false;      
        }
    

    /**
     * This function initializes all the controls that go in the north toolbar.
     */
    private void initNorthToolbar() {
        // MAKE THE NORTH TOOLBAR, WHICH WILL HAVE FOUR BUTTONS
        northToolbar = new HBox();
        northToolbar.setStyle("-fx-background-color:lightgray");
        northToolbar.setAlignment(Pos.CENTER);
        northToolbar.setPadding(marginlessInsets);
        northToolbar.setSpacing(10.0);

        // MAKE AND INIT THE GAME BUTTON
        gameButton = initToolbarButton(northToolbar,
                SokobanPropertyType.GAME_IMG_NAME);
        gameButton.setOnAction((ActionEvent event) -> {
            eventHandler
                    .respondToSwitchScreenRequest(SokobanUIState.PLAY_GAME_STATE);
        });
        //MAKE AND INIT THE BACK BUTTON, WHICH GOES BACK TO SPLASHSCREEn
        backButton = initToolbarButton(northToolbar,
                SokobanPropertyType.BACK_IMG_NAME);
        backButton.setOnAction((ActionEvent event) -> { 
            eventHandler
                    .respondToSwitchScreenRequest(SokobanUIState.SPLASH_SCREEN_STATE);
        });

        // MAKE AND INIT THE STATS BUTTON
        statsButton = initToolbarButton(northToolbar,
                SokobanPropertyType.STATS_IMG_NAME);
        statsButton.setOnAction((ActionEvent event) -> {
            eventHandler
                    .respondToSwitchScreenRequest(SokobanUIState.VIEW_STATS_STATE);
        });
        //make the timelabel
        Label timelabel = new Label("time"); //i have to have something listening
        Thread timethread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (gsm.isGameInProgress()) {
                        //getTime(timelabel);
                        Platform.runLater(() -> {
                            
                                getTime(timelabel);
                        
                        });
                        Thread.sleep(1000);
                    }
                } catch (InterruptedException ex) { System.out.println("FUCKING INTERRUPTED");
                }
            }
        });//.start();
        timethread.start();
        northToolbar.getChildren().add(timelabel);
        // MAKE AND INIT THE undo BUTTON
        undoButton = initToolbarButton(northToolbar,
                SokobanPropertyType.UNDO_IMG_NAME);
        undoButton.setOnAction((ActionEvent event) -> {
            eventHandler.respondToUndoRequest();
        });

        // MAKE AND INIT THE EXIT BUTTON
        exitButton = initToolbarButton(northToolbar,
                SokobanPropertyType.EXIT_IMG_NAME);
        //setTooltip(exitButton, SokobanPropertyType.EXIT_TOOLTIP);
        exitButton.setOnAction((ActionEvent event) -> {
            eventHandler.respondToExitRequest(primaryStage);
        });

        // AND NOW PUT THE NORTH TOOLBAR IN THE FRAME
        mainPane.setTop(northToolbar);
        //mainPane.getChildren().add(northToolbar);
    }
    
    public void undoMove(){
        this.grid = gamehistory.pop();
        this.gridRenderer.repaint();
    }

    /**
     * This method helps to initialize buttons for a simple toolbar.
     *
     * @param toolbar The toolbar for which to add the button.
     *
     * @param prop The property for the button we are building. This will
     * dictate which image to use for the button.
     *
     * @return A constructed button initialized and added to the toolbar.
     */
    private Button initToolbarButton(HBox toolbar, SokobanPropertyType prop) {
        // GET THE NAME OF THE IMAGE, WE DO THIS BECAUSE THE
        // IMAGES WILL BE NAMED DIFFERENT THINGS FOR DIFFERENT LANGUAGES
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        String imageName = props.getProperty(prop);
        System.out.println(imageName);

        // LOAD THE IMAGE
        Image image = loadImage(imageName);
        ImageView imageIcon = new ImageView(image);

        // MAKE THE BUTTON
        Button button = new Button();
        button.setGraphic(imageIcon);
        button.setPadding(marginlessInsets);

        // PUT IT IN THE TOOLBAR
        toolbar.getChildren().add(button);

        // AND SEND BACK THE BUTTON
        return button;
    }
    public int isOpen(int value){
        // pass in a grid value
        // 0 is open, 1 is box, 2 is blocked
        int r = 0;
        switch(value){
            case 3: //if it's a space or a goal, it's open, or even sokoban
            case 4:
            case 0: r = 0;
                break;
            case 1: // if it's a wall
                r = 2;
                break;
            case 2: // if it's a box
                r = 1;
                break;   
        }
        return r;
    }
    
    public void lose(){ // BLEGHH YOU LOST MY NIGGA
        // tell gsm to end the game, record stats and shit
        //mainPane.
        gsm.GameOver();
        //mainpane.setDisable(true);
        //northtoolbar.setDisable(false);
        Stage loseStage = new Stage();
        loseStage.initModality(Modality.WINDOW_MODAL);
        BorderPane border = new BorderPane();
        System.out.println("LOST");
        Button okButton = new Button("Aww man..");
        border.setBottom(okButton);
        Label messagelabel = new Label("Dude, You just lost.");
        border.setCenter(messagelabel);
        Scene scene = new Scene(border, 200, 100);
        loseStage.setScene(scene);
        loseStage.show();
        lose.play();
        //add a sound effect
        okButton.setOnAction(e -> {
            changeWorkspace(SokobanUIState.SPLASH_SCREEN_STATE);
            loseStage.close();
        });
        
        
    }
    
    public void isBoxMovable(KeyCode direction, int boxX, int boxY){ //pass in box coordinates
        // check adjacent squares to see if they are open or blocked off
        boolean isLost = false;
        int[] arr = new int[2];
            arr[0] = boxX;
            arr[1] = boxY;
        if(boxOnDot(arr)){
            System.out.println("placed Box"+boxX+" "+boxY);
            return;
        }
        switch(direction){
            case LEFT: // box is blocked off on the left side, check top and bottom
                if(!(isOpen(grid[boxX-1][boxY])==0)){ // check that it's actually blocked off
                    if ((isOpen(grid[boxX][boxY-1])!= 0)||(isOpen(grid[boxX][boxY+1])!=0))
                        isLost = true;
                }
                break;
            case RIGHT: // box is blocked off on the right side, check top and bottom
                if(!(isOpen(grid[boxX+1][boxY])==0)){
                    if ((isOpen(grid[boxX][boxY-1])!= 0)||(isOpen(grid[boxX][boxY+1])!=0)) 
                        isLost = true;
                }
                break;
            case UP: // box is blocked off from the top, check right and left
                if(!(isOpen(grid[boxX][boxY-1])==0)){
                    if ((isOpen(grid[boxX-1][boxY])!= 0)||(isOpen(grid[boxX+1][boxY])!=0))
                    isLost = true;
                }
                break;
            case DOWN: // box is blocked ohh from the bottom, check right and left 
                if(!(isOpen(grid[boxX][boxY+1])==0)){
                    if ((isOpen(grid[boxX-1][boxY])!= 0)||(isOpen(grid[boxX+1][boxY])!=0)) 
                        isLost = true;
                }
                break;
        } 
        if (isLost)
            lose();
    }
    
    public int moveBox(KeyCode direction, int boxX, int boxY){
        // this does exactly the same thing as moveSokoban, excecpt it moves the box
        // pretty inefficeint, but I DONT GIVE A FLYING FUCK
        int canimove = 0; // 0 is true 1 is false
        switch(direction){
            case LEFT: //check if the left side of sokoban on the grid is free, move him if it is.
                    if (isOpen(grid[boxX-1][boxY])==0){ // if the left side is open
                       grid[boxX][boxY] = preservedGrid[boxX][boxY];
                       grid[boxX-1][boxY] = 2; // put box in the new panel
                       isBoxMovable(direction, boxX-1, boxY);
                       push.play();
                       haveWon();
                    } else { //if it's closed
                       canimove=1;
                       buzz.play();
                       //isBoxMovable(direction, boxX, boxY);
                    }     
                break;
            case UP:
                if (isOpen(grid[boxX][boxY-1])== 0){ // it's open swagg swagg
                       grid[boxX][boxY] = preservedGrid[boxX][boxY];
                       grid[boxX][boxY-1] = 2; // put box in the new panel
                       isBoxMovable(direction, boxX, boxY-1);
                       push.play();
                       haveWon();
                } else { // its not open
                    canimove=1;
                    buzz.play();
                    //isBoxMovable(direction, boxX, boxY-1);
                }
                break;
            case RIGHT:
                if (isOpen(grid[boxX+1][boxY])== 0){ // it's open swagg swagg
                       grid[boxX][boxY] = preservedGrid[boxX][boxY];
                       grid[boxX+1][boxY] = 2; // put box in the new panel
                       isBoxMovable(direction, boxX+1, boxY);
                       push.play();
                       haveWon();
                } else { // its not open
                    canimove=1;
                    buzz.play();
                    //isBoxMovable(direction, boxX+1, boxY);
                }
                break;
            case DOWN:
                if (isOpen(grid[boxX][boxY+1])== 0){ // it's open swagg swagg
                       grid[boxX][boxY] = preservedGrid[boxX][boxY];
                       grid[boxX][boxY+1] = 2; // put sokoban in the new panel
                       isBoxMovable(direction, boxX, boxY+1);
                       push.play();
                       haveWon();
                } else { // its not open
                    canimove = 1;
                    buzz.play();
                    //isBoxMovable(direction, boxX, boxY+1);
                }
                break;
        }
        return canimove;
    }
    
    public void moveSokoban(KeyCode direction){
        // using direction as well as sokoban's location
        // will check location, and then move sokoban
        // if there is abox, move the box as well
        // prior to that, we're going to put the grid into history
        tempHist = cloneGrid();
        gamehistory.push(tempHist);
        
        
        int sokX = sokobansHere[0]; //pass over sokoban's coordinates
        int sokY = sokobansHere[1];
        int canimove=0;
        switch(direction){
            case LEFT: //check if the left side of sokoban on the grid is free, move him if it is.
                    if (isOpen(grid[sokX-1][sokY])==0){ // if the left side is open
                       grid[sokX][sokY] = preservedGrid[sokX][sokY];
                       grid[sokX-1][sokY] = 4; // put sokoban in the new panel
                       move.play();
                    } else if(isOpen(grid[sokX-1][sokY])==2){ //if it's closed
                        //don't  move sokoban
                        buzz.play();
                        //play a farting sound
                    } else { // it's gotta be  a box. have like a check box 
                        // function that does exactly the same thing
                       canimove = moveBox(direction,sokX-1,sokY);
                       if(canimove == 0){
                       grid[sokX][sokY] = preservedGrid[sokX][sokY];
                       grid[sokX-1][sokY] = 4;
                       }
                    }     
                break;
            case UP:
                if (isOpen(grid[sokX][sokY-1])== 0){ // it's open swagg swagg
                       grid[sokX][sokY] = preservedGrid[sokX][sokY];
                       grid[sokX][sokY-1] = 4; // put sokoban in the new panel
                       move.play();
                } else if(isOpen(grid[sokX][sokY-1])==2){ // its not open
                    buzz.play();
                } else { // it's gotta be a box
                    canimove = moveBox(direction,sokX,sokY-1);
                    if (canimove == 0){
                    grid[sokX][sokY] = preservedGrid[sokX][sokY];
                    grid[sokX][sokY-1] = 4;
                    }
                }
                break;
            case RIGHT:
                if (isOpen(grid[sokX+1][sokY])== 0){ // it's open swagg swagg
                       grid[sokX][sokY] = preservedGrid[sokX][sokY];
                       grid[sokX+1][sokY] = 4; // put sokoban in the new panel
                       move.play();
                } else if(isOpen(grid[sokX+1][sokY])==2){ // its not open
                    buzz.play();
                } else { // it's gotta be a box
                    
                    canimove = moveBox(direction,sokX+1,sokY);
                    if (canimove == 0){
                    grid[sokX][sokY] = preservedGrid[sokX][sokY];
                    grid[sokX+1][sokY] = 4;
                    }
                }
                break;
            case DOWN:
                if (isOpen(grid[sokX][sokY+1])== 0){ // it's open swagg swagg
                       grid[sokX][sokY] = preservedGrid[sokX][sokY];
                       grid[sokX][sokY+1] = 4; // put sokoban in the new panel
                       move.play();
                } else if(isOpen(grid[sokX][sokY+1])==2){ // its not open
                    buzz.play();
                } else { // it's gotta be a box
                    canimove = moveBox(direction,sokX,sokY+1);
                    if (canimove == 0){
                    grid[sokX][sokY] = preservedGrid[sokX][sokY];
                    grid[sokX][sokY+1] = 4; // put sokoban in the new panel
                    }
                }
                break;
        }//after finish, always call repaint
        this.gridRenderer.repaint();
    }
    
    public int[][] preserveGrid(){
        //call this whenever initSokobanUI is called, and it will save the original grid
        // ito a copy that saves all the original grid values
        int i = 0;//grid.length; columns
        int j = 0;//grid[0].length; rows
        int[][] destination = new int[grid.length][grid[0].length];
        for(;i < grid.length;i++){
            for(j=0;j < grid[0].length;j++){
                if ((grid[i][j]==2) || (grid[i][j]==4)) // if it's sokoban or a wall, it should save blanks
                    destination[i][j] = 0;
                 else 
                    destination[i][j] = grid[i][j];
                
            }
        }
        return destination;
                
    }
    
    class GridRenderer extends Canvas {

        // PIXEL DIMENSIONS OF EACH CELL
        int cellWidth;
        int cellHeight;

        // images
        Image wallImage = new Image("file:images/wall.png");
        Image boxImage = new Image("file:images/box.png");
        Image placeImage = new Image("file:images/place.png");
        Image sokobanImage = new Image("file:images/Sokoban.png");

        /**
         * Default constructor.
         */
        public GridRenderer() {
            this.setWidth(500);
            this.setHeight(500);
            repaint();
        }

        public void repaint() {
            gc = this.getGraphicsContext2D();
            gc.clearRect(0, 0, this.getWidth(), this.getHeight());

            // CALCULATE THE GRID CELL DIMENSIONS
            double w = this.getWidth() / gridColumns;
            double h = this.getHeight() / gridRows;

            gc = this.getGraphicsContext2D();
            //boolean inside = false; //toggle with inside = !inside

            // NOW RENDER EACH CELL
            int x = 0, y = 0;
            for (int i = 0; i < gridColumns; i++) {
                y = 0;
                for (int j = 0; j < gridRows; j++) {
                    // DRAW THE CELL
                    gc.setFill(Color.WHITE);
                    gc.strokeRoundRect(x, y, w, h, 10, 10);

                    switch (grid[i][j]) {
                        case 0:
                            gc.fillRect(x, y, w, h); 
                            gc.strokeRect(x, y, w, h);//gc.strokeRoundRect(x, y, w, h, 10, 10)
                            // no, it goes up to down
                            break;
                        case 1:
                            gc.drawImage(wallImage, x, y, w, h);
                            break;
                        case 2:
                            gc.drawImage(boxImage, x, y, w, h);
                            break;
                        case 3:
                            gc.drawImage(placeImage, x, y, w, h);
                            break;
                        case 4:
                            gc.drawImage(sokobanImage, x, y, w, h); //sokoban
                            //save sokoban's coordinates, give these coordinates to
                            // an array that holds sokoban's coordinates
                            sokobansHere[0] = i;
                            sokobansHere[1] = j;
                            System.out.println("sokoban is now "+ i + " " + j);
                            break;
                        case 5: //outside; change the fill and then change it back
                            gc.setFill(Color.LIGHTBLUE);
                            gc.fillRect(x, y, w, h);
                            gc.setFill(Color.WHITE);
                    }


                    // ON TO THE NEXT ROW
                    y += h;
                } 
                // ON TO THE NEXT COLUMN
                x += w;
            }
        }

    }

    /**
     * The workspace is a panel that will show different screens depending on
     * the user's requests.
     */
    private void initWorkspace() {
        // THE WORKSPACE WILL GO IN THE CENTER OF THE WINDOW, UNDER THE NORTH
        // TOOLBAR
        workspace = new Pane();
        mainPane.setCenter(workspace);
        //mainPane.getChildren().add(workspace);
        System.out.println("in the initWorkspace");
    }
    public Image loadImage(String imageName, int height, int width){//typically square anyway
        String imgInfo = ImgPath + imageName;
        Image img = new Image(imgInfo, height, width, true, false);
        return img;
    }
    
    public Image loadButtonImage(String imageName){
        String imgInfo = ImgPath + imageName;
        Image img = new Image(imgInfo, 100, 100, true, false);
        return img;
    }


    public Image loadImage(String imageName) {
        Image img = new Image(ImgPath + imageName);
        return img;
    }
    public void initStatsPane(){
        
    } 

    /**
     * This function selects the UI screen to display based on the uiScreen
     * argument. Note that we have 3 such screens: game, stats, and help.
     *
     * @param uiScreen The screen to be switched to.
     */
    public void changeWorkspace(SokobanUIState uiScreen) {
        switch (uiScreen) {
            case VIEW_HELP_STATE:
                mainPane.setCenter(helpPanel);
                break;
            case PLAY_GAME_STATE:
                mainPane.setCenter(gamePanel);
                break;
            case VIEW_STATS_STATE:
                mainPane.setCenter(statsScrollPane);
                break;
            case SPLASH_SCREEN_STATE:
                mainPane.setCenter(splashScreenPane);
                mainPane.setTop(null);
            default:
        }

    }


}
