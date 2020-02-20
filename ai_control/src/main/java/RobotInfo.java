/******************************************************************
 *Name: Oliver Pope 18344822
 *Completion Date: 30/10/2019
 *ASSERTION: RobotInfo interface
 ******************************************************************/
public interface RobotInfo {
    public String getName();

    public int getRow();

    public int getColumn();

    public double getHealth();

    public boolean isAlive();

    public void setRow(int inRow);

    public void setColumn(int inCol);

    public void setHealth(double health);

    public void setAlive(boolean inStatus);

    public boolean isFiring();

    public void setFiring(boolean firing);

    public int getTargetRow();

    public void setTargetRow(int targetX);

    public int getTargetColumn();

    public void setTargetColumn(int targetY);

    public void setHit(boolean inHit);

    public boolean getHit();

    public boolean isBotHitBot();

    public void setBotHitBot(boolean botHitBot);

    public void setBotColor(int[] inColor);

    public int[] getBotColor();

    public void resetBotColor();
}
