package MahinBot;
import battlecode.common.*;

@SuppressWarnings("ALL")

public strictfp class RobotPlayer
{

    public static void run ( RobotController rc ) throws GameActionException
    {
        Robot me = null ;

        switch(rc.getType())
        {
            case HQ:                 me = new HQ(rc);           break;
            case MINER:              me = new Miner(rc);        break;
            case REFINERY:           me = new Refinery(rc);     break;
            case VAPORATOR:          me = new Vaporator(rc);    break;
            case DESIGN_SCHOOL:      me = new DesignSchool(rc); break;
            case FULFILLMENT_CENTER: me = new Fulfill(rc);     break;
            case LANDSCAPER:         me = new Landscaper(rc);   break;
            case DELIVERY_DRONE:     me = new Drone(rc);         break;
            case NET_GUN:            me = new Netgun(rc);      break;
        }

        while(true)
        {
            try
            {
                me.takeTurn();
                Clock.yield();
            }
            catch (Exception e)
            {
                System.out.println(rc.getType() + " Exception");
                e.printStackTrace();
            }
        }
    }

}

