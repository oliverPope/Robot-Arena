/******************************************************************
 *Name: Oliver Pope 18344822
 *Completion Date: 30/10/2019
 *ASSERTION: NotificationData
 ******************************************************************/
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class NotificationData {

    private Thread notificationData;
    private BlockingQueue<Notification> notificationQueue;
    private ConcurrentHashMap<String, LinkedList<Notification>> attackedMap;

    public NotificationData()
    {
        notificationQueue = new LinkedBlockingQueue<Notification>(); //linkedBlocking used to minimise block on producer
        attackedMap = new ConcurrentHashMap<>();
    }

    public void start()
    {
        Runnable notificationTask = () ->
        {
            try
            {
                while(true)
                {
                    //takes from the blocking queue
                    Notification temp = notificationQueue.take();
                    String victimName = temp.getVictimBot().getName();
                    LinkedList<Notification> notiList = attackedMap.get(victimName);

                    if(notiList == null) //if the bot doesnt have an assigned list then create a new one
                    {
                        notiList = new LinkedList<>();
                    }
                    notiList.add(temp); //add do the robots notificaiton list
                    attackedMap.put(victimName, notiList);
                }
            }
            catch(InterruptedException e)
            {
                System.out.println(e.getMessage());
            }
        };
        notificationData = new Thread(notificationTask, "notificaiton task");
        notificationData.start();
    }

    //adds a notificaiton to the blocking queue
    public void addHitNotification(Notification innoti) throws InterruptedException {
        notificationQueue.put(innoti);
    }

    public RobotInfo getAttackerNotification(String botVictim)
    {
        RobotInfo attacker = null;
        LinkedList<Notification> notiList = attackedMap.get(botVictim);
        //pulls the robots notificaiton list and returns the next abailable one
        if(notiList != null && notiList.size() > 0)
        {
            attacker = notiList.remove().getAttackerBot();
            //updates the stored bot and its notificaiotn list
            attackedMap.put(botVictim, notiList);
        }

        return attacker;
    }

    public void end()
    {
        if(notificationData == null)
        {
            throw new IllegalStateException("Notification Thread is null");
        }

        notificationData.interrupt();
        notificationData = null;
    }
}
