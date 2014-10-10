package sokoban.ui;

import application.Main;
import application.Main.SokobanPropertyType;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
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
    private Button helpButton;
    private Button exitButton;

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
    
    GridRenderer gridRenderer;
    private GraphicsContext gc;
    SokobanGameStateManager gsm;
    
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

    /**
     * This method initializes the language-specific game controls, which
     * includes the three primary game screens.
     */
    public void initSokobanUI() { // this is where we go from the splash screen
        //should probably use this method in the constructor.
        // FIRST REMOVE THE SPLASH SCREEN
        mainPane.getChildren().clear();
        gamePanel.setCenter(gridRenderer);
        preserveGrid();
        // add key listeners tot he gamepanel, or tot eh mainpane
        mainPane.setOnKeyPressed((KeyEvent ke) -> {
            eventHandler.respondToKeyEvent(ke);
        });

        // GET THE UPDATED TITLE
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        String title = props.getProperty(SokobanPropertyType.GAME_TITLE_TEXT);
        primaryStage.setTitle(title);

        // THEN ADD ALL THE STUFF WE MIGHT NOW USE
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
        //setTooltip(gameButton, SokobanPropertyType.GAME_TOOLTIP);
        gameButton.setOnAction((ActionEvent event) -> {
            eventHandler
                    .respondToSwitchScreenRequest(SokobanUIState.PLAY_GAME_STATE);
        });

        // MAKE AND INIT THE STATS BUTTON
        statsButton = initToolbarButton(northToolbar,
                SokobanPropertyType.STATS_IMG_NAME);
        //setTooltip(statsButton, SokobanPropertyType.STATS_TOOLTIP);

        statsButton.setOnAction((ActionEvent event) -> {
            eventHandler
                    .respondToSwitchScreenRequest(SokobanUIState.VIEW_STATS_STATE);
        });
        // MAKE AND INIT THE HELP BUTTON
        helpButton = initToolbarButton(northToolbar,
                SokobanPropertyType.UNDO_IMG_NAME);
        //setTooltip(helpButton, SokobanPropertyType.HELP_TOOLTIP);
        helpButton.setOnAction((ActionEvent event) -> {
            eventHandler
                    .respondToSwitchScreenRequest(SokobanUIState.VIEW_HELP_STATE);
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
            case 3: //if it's a space or a goal, it's open
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
    
    public int moveBox(KeyCode direction, int boxX, int boxY){
        // this does exactly the same thing as moveSokoban, excecpt it moves the box
        // pretty inefficeint, but I DONT GIVE A FLYING FUCK
        int canimove = 0; // 0 is true 1 is false
        switch(direction){
            case LEFT: //check if the left side of sokoban on the grid is free, move him if it is.
                    if (isOpen(grid[boxX-1][boxY])==0){ // if the left side is open
                       grid[boxX][boxY] = preservedGrid[boxX][boxY];
                        System.out.println(preservedGrid[boxX][boxY]);
                       grid[boxX-1][boxY] = 2; // put box in the new panel
                    } else { //if it's closed
                       canimove=1;
                    }     
                break;
            case UP:
                if (isOpen(grid[boxX][boxY-1])== 0){ // it's open swagg swagg
                       grid[boxX][boxY] = preservedGrid[boxX][boxY];
                       System.out.println(preservedGrid[boxX][boxY]);
                       grid[boxX][boxY-1] = 2; // put box in the new panel
                } else { // its not open
                    canimove=1;
                }
                break;
            case RIGHT:
                if (isOpen(grid[boxX+1][boxY])== 0){ // it's open swagg swagg
                       grid[boxX][boxY] = preservedGrid[boxX][boxY];
                       grid[boxX+1][boxY] = 2; // put box in the new panel
                } else { // its not open
                    canimove=1;
                }
                break;
            case DOWN:
                if (isOpen(grid[boxX][boxY+1])== 0){ // it's open swagg swagg
                       grid[boxX][boxY] = preservedGrid[boxX][boxY];
                       grid[boxX][boxY+1] = 2; // put sokoban in the new panel
                } else { // its not open
                    canimove = 1;
                }
                break;
        }
        return canimove;
    }
    
    public void moveSokoban(KeyCode direction){
        // using direction as well as sokoban's location
        // will check location, and then move sokoban
        // if there is abox, move the box as well
        int sokX = sokobansHere[0]; //pass over sokoban's coordinates
        int sokY = sokobansHere[1];
        // no, its ok. im just doig math to the wrong coordinate
        //also, i have to tel sokoban somehow, that he can't walk over blocks
        int canimove=0;
        switch(direction){
            case LEFT: //check if the left side of sokoban on the grid is free, move him if it is.
                    if (isOpen(grid[sokX-1][sokY])==0){ // if the left side is open
                       grid[sokX][sokY] = preservedGrid[sokX][sokY];
                       grid[sokX-1][sokY] = 4; // put sokoban in the new panel
                    } else if(isOpen(grid[sokX-1][sokY])==2){ //if it's closed
                        //don't  move sokoban
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
                } else if(isOpen(grid[sokX][sokY-1])==2){ // its not open
                    
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
                } else if(isOpen(grid[sokX+1][sokY])==2){ // its not open
                    
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
                } else if(isOpen(grid[sokX][sokY+1])==2){ // its not open
                    
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
    
    public void preserveGrid(){
        //call this whenever initSokobanUI is called, and it will save the original grid
        // ito a copy that saves all the original grid values
        int i = 0;//grid.length; columns
        int j = 0;//grid[0].length; rows
        preservedGrid = new int[grid.length][grid[0].length];
        for(;i < grid.length;i++){
            for(j=0;j < grid[0].length;j++){
                if ((grid[i][j]==2) || (grid[i][j]==4)) // if it's sokoban or a wall, it should save blanks
                    preservedGrid[i][j] = 0;
                 else 
                    preservedGrid[i][j] = grid[i][j];
                
            }
        }
                
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
                            System.out.println(i + " " + j);
                            break;
                        case 5: //outside; change the fill and then change it back
                            gc.setFill(Color.LIGHTBLUE);
                            gc.fillRect(x, y, w, h);
                            gc.setFill(Color.WHITE);
                    }

                    // THEN RENDER THE TEXT
//                    String numToDraw = "" + grid[i][j];
//                    double xInc = (w / 2) - (10 / 2);
//                    double yInc = (h / 2) + (10 / 4);
//                    x += xInc;
//                    y += yInc;
//                    gc.setFill(Color.RED);
//                    gc.fillText(numToDraw, x, y);
//                    x -= xInc;
//                    y -= yInc;

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
            default:
        }

    }


}
