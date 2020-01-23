package MahinBot;
import battlecode.common.*;
import java.util.ArrayList;

public class Unit extends Robot
{

    Navigation nav ;
    public Unit( RobotController r )
    {
        super(r);
        nav = new Navigation( r ) ;
    }

    public void takeTurn () throws GameActionException
    {
        super.takeTurn();
        super.findhq();
    }

    boolean gotonav(Direction dir) throws GameActionException {
        Direction[] toTry = {dir, dir.rotateLeft(), dir.rotateRight(), dir.rotateLeft().rotateLeft(), dir.rotateRight().rotateRight()};
        for (Direction d : toTry){
            System.out.println("trying: "+d);
            if(tryMove(d))
                return true;
        }
        return false;
    } 
    boolean gotonav(MapLocation ml)throws GameActionException{
        return gotonav(rc.getLocation().directionTo(ml));
    }
    boolean tryMove() throws GameActionException {
        for (Direction dir : Util.directions)
            if (tryMove(dir))
                return true;
        return false;
        // MapLocation loc = rc.getLocation();
        // if (loc.x < 10 && loc.x < loc.y)
        //     return tryMove(Direction.EAST);
        // else if (loc.x < 10)
        //     return tryMove(Direction.SOUTH);
        // else if (loc.x > loc.y)
        //     return tryMove(Direction.WEST);
        // else
        //     return tryMove(Direction.NORTH);
    }

    boolean tryMove(Direction dir) throws GameActionException {
        // System.out.println("I am trying to move " + dir + "; " + rc.isReady() + " " + rc.getCooldownTurns() + " " + rc.canMove(dir));
        if (rc.isReady() && rc.canMove(dir) && !rc.senseFlooding(rc.getLocation().add(dir))) {
            rc.move(dir);
            return true;
        } else return false;
    }

}
