package MahinBot;
import battlecode.common.*;
import java.util.Random;

public class Robot
{
	public static MapLocation hqLoc=null,souploc=null;
	RobotController rc;
	Communications comms;
    Random rndm = new Random();
	public Robot ( RobotController r )
	{
		this.rc = r ;
		// comms = new Communications( rc ) ;
	}

	int turnCount = 0 ;

	public void takeTurn() throws GameActionException
	{
		turnCount++;
	}

	boolean tryBuild ( RobotType type , Direction dir ) throws GameActionException
	{
		if(rc.isReady() && rc.canBuildRobot(type, dir))
		{
			rc.buildRobot(type, dir);
			return true;
		}
		return false;
	}

    void findhq() throws GameActionException
    {
        if(hqLoc == null){
            if(rc.getType()==RobotType.HQ){
                hqLoc=rc.getLocation(); return;
            }
            //getting the miners search for hq location
            RobotInfo[] nearbybots = rc.senseNearbyRobots(rc.getCurrentSensorRadiusSquared(), rc.getTeam());
            System.out.println("for hq-nearby bots:! "+nearbybots);
            for ( RobotInfo bot : nearbybots ){
                if(bot.type == RobotType.HQ){
                    hqLoc=bot.location;
                    return;
                }
            }
        }
        if(hqLoc==null){
            comms.readBlock();
        }
    }
    boolean findNearbyRobots(RobotType targetbot) throws GameActionException {
        RobotInfo[] bots = rc.senseNearbyRobots(); ///  yet another 100$ fn
        for (RobotInfo bot : bots) { 
            if(bot.type == targetbot){
                // System.out.println("detected!"+targetbot+"!!RESPOND....");
                return true;
            }
        }
        return false;
    }
	boolean findNearbyRobots(RobotType targetbot,Team side) throws GameActionException {
        RobotInfo[] bots = rc.senseNearbyRobots(-1,side);
        for (RobotInfo bot : bots) {
            if(bot.type == targetbot){
                // System.out.println("bot of INTENDED SIDE detected!"+targetbot);
                return true;

            }
        }
        return false;
    }	
}