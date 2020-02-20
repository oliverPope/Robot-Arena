/******************************************************************
 *Name: Oliver Pope 18344822
 *Completion Date: 30/10/2019
 *ASSERTION: Notification
 ******************************************************************/
public class Notification {

    private RobotInfo attackerBot;
    private RobotInfo victimBot;

    public Notification(RobotInfo inAttack, RobotInfo inVictim)
    {
        this.attackerBot = inAttack;
        this.victimBot = inVictim;
    }

    public RobotInfo getAttackerBot() {
        return attackerBot;
    }

    public RobotInfo getVictimBot() {
        return victimBot;
    }
}
