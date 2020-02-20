/******************************************************************
 *Name: Oliver Pope 18344822
 *Completion Date: 30/10/2019
 *ASSERTION: RobotControlImpl
 ******************************************************************/

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.*;

public class RobotControlImpl implements RobotControl{

    private RobotInfo botInfo;
    private BlockingQueue<Notification> hitsQueue;
    private NotificationData notiData;
    private RobotSwingApp guiApp;
    private ArenaData arena;


    /******************************************************************
     *SUBMODULE: Constructor
     ******************************************************************/
    public RobotControlImpl(RobotInfo inBot, NotificationData inNoti, RobotSwingApp inApp, ArenaData inArena)
    {
        this.botInfo = inBot;
        this.hitsQueue = new LinkedBlockingQueue<>();
        this.guiApp = inApp;
        this.arena = inArena;
        this.notiData = inNoti;

        //AIrobotJNI aiC = new AIrobotJNI();
    }


    /******************************************************************
     *SUBMODULE: fire
     *ASSERTION: takes in a target row and column and performs a fire at
     * the targeted robot.
     ******************************************************************/
    public boolean fire(int row, int column) throws InterruptedException
    {
        boolean legalFire = false;
        //set booleans for gui applications
        botInfo.setFiring(true);
        botInfo.setTargetRow(row);
        botInfo.setTargetColumn(column);
        guiApp.updateRobotGUI(botInfo);

        //send data to the logger
        displayMessage(botInfo.getName() + " fired!");
        //retrieves the potential robot from the target coordinates
        RobotInfo robot = arena.getArena()[row][column];
        //a robot may not have actually been at the coordiantes at this time
        if (robot != null) {
            //if the robot isnt alive dont decremement health
            if (robot.isAlive()) {
                //send data to the logger
                displayMessage(botInfo.getName() + " hit a robot!");

                double robotHealth = robot.getHealth();

                //decrement the robots health
                if (Double.compare(robotHealth, 35.0) == 0 || Double.compare(robotHealth, 35.0) > 0) {
                    robot.setHealth(robot.getHealth() - 35.0);
                } else {
                    robot.setHealth(0.0);
                }

                //add hit notification to the hit blocking queue
                hitsQueue.put(new Notification(botInfo, robot));
                //add notification also to the attacker blocking queue in the notification thread
                notiData.addHitNotification(new Notification(botInfo, robot));
                legalFire = true;
            }
            Thread.sleep(250);
            //resets gui boolean
            robot.setHit(false);
        }
        //resets firing boolean
        botInfo.setFiring(false);
        return legalFire;
    }


    /******************************************************************
     *SUBMODULE: displayMessage
     *ASSERTION: takes in a string and sends it to the gui via runlater
     ******************************************************************/
    public void displayMessage(String str)
    {
        SwingUtilities.invokeLater(()->{
            guiApp.updateLogger(str);
        });
    }



    /******************************************************************
     *SUBMODULE: checkHits
     *ASSERTION: attempts to take from the hit robot blocking queue, if
     * a notification exists then allow the robot to react and send the
     * notification data to the gui to be displayed
     ******************************************************************/
    public void checkHits() throws InterruptedException
    {
        RobotInfo victim = hitsQueue.take().getVictimBot(); //peek()
        if(victim !=null)
        {
            botInfo.setBotHitBot(true);

            arena.updateSingleRobot(botInfo);
            SwingUtilities.invokeLater(()->{
                guiApp.updateLogger("*"+botInfo.getName() + " HIT THIS ONE: " + victim.getName());
            });
        }
    }

    /******************************************************************
     *SUBMODULE: checkIfHit
     *ASSERTION: accesse the notification blocking queue for "got hit"
     * notifications, if one exists allows the robot to react and then
     * sneds the notification data to the gui to be displayed
     ******************************************************************/
    public void checkIfHit(String botName) throws InterruptedException
    {
        RobotInfo attacker =  notiData.getAttackerNotification(botName);

        if(attacker != null)
        {
            botInfo.setHit(true);
            arena.updateSingleRobot(botInfo);
            updateRobotGUI();
            SwingUtilities.invokeLater(()->{
                guiApp.updateLogger("-"+botInfo.getName() + " GOT HIT BY: " + attacker.getName());
            });
        }
    }

    /******************************************************************
     *SUBMODULE: getRobot
     *ASSERTION: updates the robot info field to the most current stored
     * version then returns
     ******************************************************************/
    public RobotInfo getRobot() {

        updateRobotInfo();
        return botInfo;
    }


    /******************************************************************
     *SUBMODULE: getAllRobots
     *ASSERTION: access the hasmap stored in the arena Data, converts
     * to an array list and returns
     ******************************************************************/
    public List<RobotInfo> getAllRobots()
    {
        List<RobotInfo> robotData = new ArrayList<>(arena.getAllData().values());
        return robotData;
    }

    /******************************************************************
     *SUBMODULE: acquireTarget
     *ASSERTION: checking method to determine if a target robot is not
     * itself, and if its within range
     ******************************************************************/
    public boolean acquireTarget(RobotInfo targetBot, RobotInfo curRobot)
    {
        if (!targetBot.getName().equals(curRobot.getName())
                && Math.abs(curRobot.getRow() - targetBot.getRow()) <= 2
                && Math.abs(curRobot.getColumn() - targetBot.getColumn()) <= 2 && targetBot.isAlive())
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    /******************************************************************
     *SUBMODULE: isWinner
     *ASSERTION: checks if robot is alive and what the alive count tall is
     * If condition is true then ends the last remainign robot
     ******************************************************************/
    public void isWinner()throws InterruptedException
    {
        updateRobotInfo();
        if(botInfo.isAlive() && arena.getAliveCount() <= 1)
        {
            displayMessage("GAME OVER: " + botInfo.getName() + " is the Winner!" );
            guiApp.disableEndButton();
            throw new InterruptedException("WINNER WINNER CHICKEN DINNER");
        }
    }

    /******************************************************************
     *SUBMODULE: isAlive
     *ASSERTION: checks if the robots health is 0.0, if it is updates the
     * alive boolean and performs death notificaiton methods
     ******************************************************************/
    public void isAlive() throws InterruptedException
    {
        updateRobotInfo();
        if (Double.compare(botInfo.getHealth(), 0.0) == 0)
        {
            botInfo.setAlive(false);
            arena.updateSingleRobot(botInfo);
            arena.notifyDeadRobot();
            displayMessage(botInfo.getName() + " is dead");
            throw new InterruptedException("This robot is done");
        }
    }

    /******************************************************************
     *SUBMODULE: updateRobotInfo
     *ASSERTION: gets the most up to date version of itself
     ******************************************************************/
    public void updateRobotInfo()
    {
        botInfo = arena.getRobotData(botInfo.getName());
    }

    /******************************************************************
     *SUBMODULE: updateRobotGUI
     *ASSERTION: calls for an update on this particular robot
     ******************************************************************/
    public void updateRobotGUI()
    {
        SwingUtilities.invokeLater(()->{
            guiApp.updateRobotGUI(botInfo);
        });
    }

    /******************************************************************
     *SUBMODULE: moveNorth
     *ASSERTION: checks arena location and updates gui is valie
     ******************************************************************/
    public boolean moveNorth(){
        int curCol = botInfo.getColumn();
        int curRow = botInfo.getRow();
        boolean validMove = false;

        if(arena.checkLocation(curRow, curCol, curRow+1, curCol, botInfo))
        {
            validMove = true;
            updateRobotGUI();
        }
        return validMove;
    }


    /******************************************************************
     *SUBMODULE: moveEast
     *ASSERTION: checks arena location and updates gui is valie
     ******************************************************************/
    public boolean moveEast(){

        int curCol = botInfo.getColumn();
        int curRow = botInfo.getRow();
        boolean validMove = false;

        if(arena.checkLocation(curRow, curCol, curRow, curCol+1, botInfo))
        {
            validMove = true;
            updateRobotGUI();
        }
        return validMove;
    }

    /******************************************************************
     *SUBMODULE: moveSouth
     *ASSERTION: checks arena location and updates gui is valie
     ******************************************************************/
    public boolean moveSouth(){

        int curCol = botInfo.getColumn();
        int curRow = botInfo.getRow();
        boolean validMove = false;

        if(arena.checkLocation(curRow, curCol, curRow-1, curCol, botInfo))
        {
            validMove = true;
            updateRobotGUI();
        }
        return validMove;
    }

    /******************************************************************
     *SUBMODULE: moveWest
     *ASSERTION: checks arena location and updates gui is valie
     ******************************************************************/
    public boolean moveWest(){

        int curCol = botInfo.getColumn();
        int curRow = botInfo.getRow();
        boolean validMove = false;

        if(arena.checkLocation(curRow, curCol, curRow, curCol-1, botInfo))
        {
            validMove = true;
            updateRobotGUI();
        }
        return validMove;
    }

}
