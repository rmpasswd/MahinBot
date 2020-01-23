package MahinBot;
import battlecode.common.* ;

public class Fulfill extends Building
{

    public Fulfill ( RobotController r )  { super(r); }

    public void takeTurn() throws GameActionException
    {
        super.takeTurn();
        
        for (Direction dir : Util.directions)
            tryBuild(RobotType.DELIVERY_DRONE, dir);        
	}
}