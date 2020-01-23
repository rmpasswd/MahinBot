package MahinBot;
import battlecode.common.* ;

public class HQ extends Netgun
{

    static  int numMiners = 0 ;

    public HQ ( RobotController r ) throws GameActionException
    {
        super(r);
        // comms.broadcastLoc(rc.getLocation());
    }

    public void takeTurn() throws GameActionException
    {
        if(turnCount==1){
            comms.broadcastLoc(hqLoc,5);
        }
        if (turnCount < 20 || turnCount%100<5) {
            for (Direction dir : Util.directions)
                tryBuild(RobotType.MINER, dir);
        }
    }
}
