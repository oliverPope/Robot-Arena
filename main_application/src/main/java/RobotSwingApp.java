/******************************************************************
 *Name: Oliver Pope 18344822
 *Completion Date: 30/10/2019
 *ASSERTION: RobotSwingApp
 ******************************************************************/


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class RobotSwingApp
{
    private JTextArea logger;
    private SwingArena arena;
    private int arenaHeight =5;
    private int arenaWidth=5;
    private int numRobots = 8;
    private ConcurrentHashMap<String, RobotInfo> infoMap;
    private List<RobotAI> aiList = new LinkedList<>();
    private NotificationData nd;

    private JButton endBtn;
    public RobotSwingApp()
    {
        arena = new SwingArena(arenaHeight,arenaWidth);
        initialiseGame();

    }

    /******************************************************************
     *SUBMODULE: initialiseGui
     *ASSERTION: passes the arena map through to the JFXArena
     ******************************************************************/
    public void initialiseGui(Map<String, RobotInfo> inMap)
    {
        arena.initaliseRobots(inMap);
    }

    /******************************************************************
     *SUBMODULE: updateRobotGUI
     *ASSERTION: passes robot info object through to be updated in GUI
     ******************************************************************/
    public void updateRobotGUI(RobotInfo inBot)
    {
        arena.updateRobotGUI(inBot);
    }


    public void start()
    {

        SwingUtilities.invokeLater(() ->
        {
            JFrame window = new JFrame("Robot AI ARENA (Swing)");
            logger = new JTextArea();

            JToolBar toolbar = new JToolBar();
            JButton startBtn = new JButton("Start Game");
            endBtn = new JButton("Stop Game");
            toolbar.add(startBtn);
            toolbar.add(endBtn);

            endBtn.setEnabled(false);

            startBtn.addActionListener((event) ->
            {
                logger.append("GAME BEGINS!\n");
                startGame();

                //disabled the start button
                startBtn.setEnabled(false);
                endBtn.setEnabled(true);
            });

            endBtn.addActionListener((event) ->
            {
                logger.append("GAME OVER: no winner :/\n");
                endGame();
                endBtn.setEnabled(false);
            });


            JScrollPane loggerArea = new JScrollPane(logger);
            loggerArea.setBorder(BorderFactory.createEtchedBorder());


            JSplitPane splitPane = new JSplitPane(
                    JSplitPane.HORIZONTAL_SPLIT, arena, loggerArea);

            Container contentPane = window.getContentPane();
            contentPane.setLayout(new BorderLayout());
            contentPane.add(toolbar, BorderLayout.NORTH);
            contentPane.add(splitPane, BorderLayout.CENTER);

            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            window.setPreferredSize(new Dimension(800, 800));
            window.pack();
            window.setVisible(true);

            splitPane.setDividerLocation(0.75);
        });


    }


    /******************************************************************
     *SUBMODULE: updateLogger
     *ASSERTION: Takes in a string and appends it to the logger
     ******************************************************************/
    public void updateLogger(String str)
    {
        if(str!=null)
        {
            logger.append(str+"\n");
        }
    }


    /******************************************************************
     *SUBMODULE: initialisegame
     *ASSERTION: creates a map of robotInfos
     ******************************************************************/
    public void initialiseGame()
    {
        infoMap = new ConcurrentHashMap<>();
        boolean arenaTemplate[][] = new boolean[arenaHeight][arenaWidth];
        Random random = new Random();
        RobotInfo bot;
        //set up robots with randomised grid locations
        for(int i = 0; i < numRobots; i++)
        {
            int row = random.nextInt(arenaHeight);
            int col = random.nextInt(arenaWidth);
            while(arenaTemplate[row][col] == true) //loop while a grid location is already taken
            {
                row = random.nextInt(arenaHeight);
                col = random.nextInt(arenaWidth);
            }
            arenaTemplate[row][col] = true;
            bot = new RobotInfoImpl("Robot" + i, row,col, 100.0);
            infoMap.put(bot.getName(), bot);
            nd = new NotificationData();
        }
        initialiseGui(infoMap);

    }

    /******************************************************************
     *SUBMODULE: startGame
     *ASSERTION: iterates through each robot, assignes a robot control
     * and creatres a new AI implementaiton and starts the thread
     ******************************************************************/
    public void startGame()
    {
        RobotControl rc;
        RobotAI robotA;
        RobotAI robotB;



        nd.start(); //starts the notificaion data thread
        int count = 0;
        ArenaData arenaData = new ArenaData(arenaHeight, arenaWidth,infoMap, infoMap.size());


        /*Couldnt get JNI link properly, kept getting an exception saying UnsatisfiedLinkError, so i have
        jst commented out the JNIC implementation code as it wouldnt work on my mac machine or curtin lab machine.
        I tried various java.library.path exports but didnt have any successs :(
         */

       /* AIrobotJNI aiC = new AIrobotJNI();
        RobotInfo cRobot = new RobotInfoImpl("RobotC" , 1,1, 100.0);
        aiC.startAIC(new RobotControlImpl(cRobot, nd, this, arenaData));*/



        for(RobotInfo robot: infoMap.values())
        {
            if(count<infoMap.size()/2) //splits half the robots into AIA so other half is AIB
            {
                rc = new RobotControlImpl(robot, nd, this, arenaData);
                robotA = new AIImplementationA();
                robotA.runAI(rc);
                aiList.add(robotA);
            }
            else
            {
                rc = new RobotControlImpl(robot, nd, this, arenaData);
                robotB = new AIImplementationB();
                robotB.runAI(rc);
                aiList.add(robotB);
            }
            count++;
        }
    }

    /******************************************************************
     *SUBMODULE: endGame
     *ASSERTION: iterates through each robot AI thread and calls end,
     * also ends the notification data
     ******************************************************************/
    public void endGame()
    {
        for(RobotAI ai: aiList)
        {
            ai.end();
        }

        nd.end();
    }

    public void disableEndButton()
    {
        endBtn.setEnabled(false);
    }

}
