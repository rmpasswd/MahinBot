package MahinBot;
import battlecode.common.*;

public class Netgun extends Building
{

    public Netgun ( RobotController r ) { super(r) ; }

    public void takeTurn() throws GameActionException
    {
        super.takeTurn();

        Team enemy = rc.getTeam().opponent() ;

        RobotInfo[] enemiesInRange =
                rc.senseNearbyRobots( GameConstants.NET_GUN_SHOOT_RADIUS_SQUARED , enemy) ;

        for (RobotInfo e : enemiesInRange )
        {
            if(e.type==RobotType.DELIVERY_DRONE && rc.canShootUnit(e.ID))
            {
                rc.shootUnit(e.ID);
                break;
            }
        }
    }
}
