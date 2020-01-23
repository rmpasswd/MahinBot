package MahinBot;
import battlecode.common.*;

public class Navigation
{

    RobotController rc ;
    public Navigation ( RobotController r ) { rc = r ; }

    boolean tryMove ( Direction dir ) throws GameActionException
    {
        if( rc.isReady() && rc.canMove(dir) && !rc.senseFlooding(rc.getLocation().add(dir)) )
        {
            rc.move(dir);
            return true;
        }
        else return false;
    }

    boolean goTo ( Direction dir ) throws GameActionException
    {
        Direction[] toTry =
        {
                dir,
                dir.rotateLeft(),
                dir.rotateRight(),
                dir.rotateLeft().rotateLeft(),
                dir.rotateRight().rotateRight()
        };
        for ( Direction d : toTry )
        {
            if( tryMove(d) ) return true;
        }
        return false;
    }

    boolean goTo ( MapLocation destination ) throws GameActionException
    {
        return goTo(rc.getLocation().directionTo(destination)) ;
    }

}
