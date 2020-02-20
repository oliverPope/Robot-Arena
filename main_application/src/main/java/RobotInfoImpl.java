/******************************************************************
 *Name: Oliver Pope 18344822
 *Completion Date: 30/10/2019
 *ASSERTION: RobotInfoImpl
 ******************************************************************/

public class RobotInfoImpl implements RobotInfo {
    private String name;
    private int row;
    private int column;
    private double health;
    private boolean alive;

    private boolean firing; //if robot is firing a laser
    private int targetX; //target location for laser
    private int targetY;
    private boolean hit; //if robot has been hit
    private boolean botHitBot; //if robot has hit another bot

    private int[] botColor;

    public RobotInfoImpl(String inName, int inRow, int inCol, double inHealth)
    {
        this.name = inName;
        this.row = inRow;
        this.column = inCol;
        this.health = inHealth;
        this.alive = true;
        this.firing = false;
        this.targetX = -1;
        this.targetY = -1;
        this.hit = false;
        this.botHitBot = false;
        this.botColor = new int[3];
        botColor[0] = 0;
        botColor[1] =0;
        botColor[2] = 255;
    }

    @Override
    public String toString() {
        return "RobotInfo{" +
                "name='" + name + '\'' +
                ", row=" + row +
                ", column=" + column +
                ", health=" + health +
                '}';
    }

    public String getName() {
        return name;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public double getHealth() {
        return health;
    }

    public boolean isAlive(){
        return alive;
    }

    public void setRow(int inRow) {
        this.row = inRow;
    }

    public void setColumn(int inCol) {
        this.column = inCol;
    }

    public void setHealth(double health) {
        this.health = health;
    }

    public void setAlive(boolean inStatus)
    {
        this.alive = inStatus;
    }

    public boolean isFiring() {
        return firing;
    }

    public void setFiring(boolean firing) {
        this.firing = firing;
    }

    public int getTargetRow() {
        return targetX;
    }

    public void setTargetRow(int targetX) {
        this.targetX = targetX;
    }

    public int getTargetColumn() {
        return targetY;
    }

    public void setTargetColumn(int targetY) {
        this.targetY = targetY;
    }

    public void setHit(boolean inHit)
    {
        this.hit = inHit;
    }

    public boolean getHit()
    {
        return hit;
    }

    public boolean isBotHitBot() {
        return botHitBot;
    }

    public void setBotHitBot(boolean botHitBot) {
        this.botHitBot = botHitBot;
    }

    public int[] getBotColor() {
        return botColor;
    }

    public void setBotColor(int[] botColor) {
        this.botColor = botColor;
    }

    //resets bot colour back to defualt BLUE colour
    public void resetBotColor()
    {
        this.botColor = new int[3];
        botColor[0] = 0;
        botColor[1] =0;
        botColor[2] = 255;
    }
}

