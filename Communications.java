package MahinBot;
import battlecode.common.*;

import java.util.ArrayList;

public class Communications
{

    RobotController rc ;
    // Robot.hqLoc
    public Communications ( RobotController r ) { this.rc = r ; }

    static final int teamSecret = 477;
    static final int transactionCost = 3 ;

    // public boolean broadcastedCreation = false ;

    void broadcastLoc(MapLocation loc,int ab) throws GameActionException{
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
        System.out.println("starting readBlock fn");
        for(int rn=1;rn<10;rn++){ //rc.getRoundNum()
            System.out.println("INSIDE EACH ROUNDNUM");
            Transaction[] blk=rc.getBlock(rn);
            System.out.println("GOT ALL BLOCK");
            for (Transaction eachblk : blk) {
                System.out.println("searching through each message of each block");
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
