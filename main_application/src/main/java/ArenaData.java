/******************************************************************
 *Name: Oliver Pope 18344822
 *Completion Date: 30/10/2019
 *ASSERTION: ArenaData
 ******************************************************************/

import java.util.HashMap;
import java.util.Map;

public class ArenaData {

    private RobotInfo arena[][];
    private Object mutex;
    private int arenaHeight;
    private int arenaWidth;
    private int aliveRobotCount; //total num robots left in game
    private Map<String, RobotInfo> robotDataMap;


    public ArenaData(int inarenaHeight, int inarenaWidth, Map<String, RobotInfo> inInfoMap, int inNumRobots)
    {
        this.arenaHeight = inarenaHeight;
        this.arenaWidth = inarenaWidth;
        mutex = new Object();
        arena = new RobotInfo[arenaHeight+1][arenaWidth+1];
        robotDataMap = new HashMap<String, RobotInfo>();
        this.aliveRobotCount = inNumRobots;
        this.robotDataMap = inInfoMap;

        //iterates through each robots and initialised them into the arena array
        for(RobotInfo robot: robotDataMap.values())
        {
            int row = robot.getRow();
            int column = robot.getColumn();
            arena[row][column] = robot;
        }
    }

    public RobotInfo[][] getArena()
    {
        synchronized (mutex)
        {
            return arena;
        }
    }

    /******************************************************************
     *SUBMODULE: checkLocation
     *ASSERTION: takes in current location and target locations and
     * determines if the target grid is aavialble, if so will update
     ******************************************************************/
    public boolean checkLocation(int curRow, int curColumn, int targetRow, int targetCol, RobotInfo bot)
    {
        boolean validMove = false;
        synchronized (mutex)
        {
            //checks if its wihtin the arena bounds
            if(targetCol <= arenaWidth && targetRow <= arenaHeight)
            {
                if(targetCol >= 0 && targetRow >= 0)
                {
                    //checks if a robot is set to that location or not
                    if(arena[targetRow][targetCol] == null)
                    {
                        //updates the robot to the new location
                        bot.setRow(targetRow);
                        bot.setColumn(targetCol);
                        robotDataMap.put(bot.getName(), bot);
                        arena[targetRow][targetCol] = bot;
                        //resets the old location so its vailable for other robots
                        arena[curRow][curColumn] = null;
                        validMove = true;
                    }
                }
            }
        }
        return validMove;
    }

    /******************************************************************
     *SUBMODULE: updateSingleRobot
     *ASSERTION: updates a single robot info within the map and arena array
     ******************************************************************/
    public void updateSingleRobot(RobotInfo robot)
    {
        synchronized (mutex)
        {
            robotDataMap.put(robot.getName(), robot);
            arena[robot.getRow()][robot.getColumn()] = robot;
        }
    }

    /******************************************************************
     *SUBMODULE: getAllData
     *ASSERTION: returns the all data map
     ******************************************************************/
    public Map<String, RobotInfo> getAllData()
    {
        synchronized (mutex)
        {
            return robotDataMap;
        }
    }


    /******************************************************************
     *SUBMODULE: getRobotData
     *ASSERTION: returns a specific robot info
     ******************************************************************/
    public RobotInfo getRobotData(String name)
    {
        synchronized (mutex)
        {
            return robotDataMap.get(name);
        }
    }



    public void notifyDeadRobot() {
        synchronized (mutex) {
            aliveRobotCount--;
        }

    }

    public int getAliveCount() {
        synchronized (mutex)
        {
            return aliveRobotCount;

        }
    }

}
