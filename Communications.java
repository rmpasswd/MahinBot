package MahinBot;
import battlecode.common.*;

import java.util.ArrayList;

public class Communications
{

    RobotController rc ;
    // Robot.hqLoc
    public Communications ( RobotController r ) { this.rc=r ; }

    static final int teamSecret = 477;
    static final int transactionCost = 3 ;

    // public boolean broadcastedCreation = false ;

    public void broadcastLoc(MapLocation loc,int ab) throws GameActionException{
        int[] message = new int[7] ;
        message[0] = teamSecret ;
        message[1] = ab ;
        message[2] = loc.x ;
        message[3] = loc.y ;
        if(rc.canSubmitTransaction(message , 3 )) {
            rc.submitTransaction( message , 3 ) ;
        }

    }
    public void readBlock() throws GameActionException {
        for(int rn=1;rn<rc.getRoundNum();rn++){
            Transaction[] blk=rc.getBlock(rn);
            for (Transaction eachblk : blk) {
                int[] ms=eachblk.getMessage();
                if(ms[0]==teamSecret){ // SWITCH STATEMENTS HERE ?
                    if(ms[1]==5){
                        Robot.hqLoc=new MapLocation(ms[2],ms[3]);
                    }else if (ms[1]==7) {
                        Robot.souploc=new MapLocation(ms[2],ms[3]);
                    }
                }
            }
        }
    }
    public void updateSoupLocations(ArrayList<MapLocation> soupLocations) throws GameActionException
    {
        for(Transaction tx : rc.getBlock(rc.getRoundNum() - 1))
        {
            int[] mess = tx.getMessage();
            if(mess[0] == teamSecret && mess[1] == 2)
            {
                // TODO: don't add duplicate locations
                System.out.println("heard about a tasty new soup location");
                soupLocations.add(new MapLocation(mess[2], mess[3]));
            }
        }
    }

}
