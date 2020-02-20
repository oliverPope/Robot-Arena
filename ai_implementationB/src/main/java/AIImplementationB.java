/******************************************************************
 *Name: Oliver Pope 18344822
 *Completion Date: 30/10/2019
 *ASSERTION: AIImplementationB
 ******************************************************************/
public class AIImplementationB implements RobotAI{

    private Thread AIBThread;
    private String direction;
    private RobotInfo curRobot;

    public AIImplementationB()
    {
        direction = "south";
    }


    @Override
    public void runAI(RobotControl rc) {

        curRobot = rc.getRobot();


        Runnable AIBTask = () ->
        {
            try {
                while (true) {

                    /*relevant GUi fields, essentiall resets the robot back to default so that
                   the GUi changes are only tempoary eg reacting to notification is a flash not a set change*/
                    rc.getRobot().setBotHitBot(false);
                    rc.getRobot().resetBotColor();
                    rc.getRobot().setHit(false);
                    rc.checkIfHit(curRobot.getName());

                    //checks if the robot is the last one and or is still alive
                    rc.isWinner();
                    rc.isAlive();

                    curRobot = rc.getRobot();//updates the local reference
                    //iterates through each robot and determines if it can fire at it

                    for (RobotInfo robot : rc.getAllRobots()) {
                        if (rc.acquireTarget(robot,curRobot)) {
                            Thread.sleep(500);
                            //attempts to fire at target location

                            if(rc.fire(robot.getRow(), robot.getColumn()))
                            {
                                //checks the hit notifications
                                rc.checkHits();
                                //assignes a specific AIA notification reaction colour
                                int [] color = new int[3];
                                color[0] = 0;
                                color[1] =102;
                                color[2] = 0;
                                rc.getRobot().setBotColor(color);

                            }
                            break;
                        }
                    }

                    /*switch case for th emovements, checks if it can move in the direction, if it cant
                   then its direciton is changed*/
                    switch (direction) {
                        case "north":
                            if (!rc.moveNorth()) {
                                direction = "east";
                            }
                            break;

                        case "east":
                            if (!rc.moveEast()) {

                                direction = "south";
                            }
                            break;

                        case "south":
                            if (!rc.moveSouth()) {

                                direction = "west";
                            }
                            break;

                        case "west":
                            if (!rc.moveWest()) {

                                direction = "north";
                            }
                            break;
                    }

                    rc.isWinner();
                    rc.isAlive();
                           Thread.sleep(1000);


                }

            }
            catch(InterruptedException e)
            {
                System.out.println(curRobot.getName() + " " + e.getMessage()+ " last location: " + curRobot.getRow() + " " + curRobot.getColumn());
            }

        };

        AIBThread = new Thread(AIBTask, "AIB-Thread");
        AIBThread.start();

    }

    public void end()
    {
        if(AIBThread == null)
        {
            throw new IllegalStateException();
        }

        AIBThread.interrupt();
        AIBThread = null;
    }

}
