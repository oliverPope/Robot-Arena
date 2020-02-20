/******************************************************************
 *Name: Oliver Pope 18344822
 *Completion Date: 30/10/2019
 *ASSERTION: RobotControl interface
 ******************************************************************/

import java.util.List;

public interface RobotControl {
    public boolean fire(int row, int column) throws InterruptedException;
    public void displayMessage(String str);
    public void checkHits() throws InterruptedException;
    public void checkIfHit(String botName) throws InterruptedException;
    public RobotInfo getRobot();
    public List<RobotInfo> getAllRobots();
    public boolean acquireTarget(RobotInfo robot, RobotInfo curRobot);
    public void isWinner()throws InterruptedException;
    public void isAlive() throws InterruptedException;
    public void updateRobotInfo();
    public void updateRobotGUI();
    public boolean moveNorth();
    public boolean moveEast();
    public boolean moveSouth();
    public boolean moveWest();
}
