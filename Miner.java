package MahinBot;
import battlecode.common.*;
import java.util.ArrayList;

public class Miner extends Unit {
    boolean designed=false,been2mid=false,readSoupLoc=false,broadcastedSoupLoc=false;
    // int numDesignSchools = 0;
    // ArrayList<MapLocation> soupLocations = new ArrayList<MapLocation>();

    public Miner(RobotController r) {
        super(r);
    }

    public void takeTurn() throws GameActionException {
        super.takeTurn();

        
       if(designed==false){
            if(findNearbyRobots(RobotType.LANDSCAPER,rc.getTeam())) //found lcap then dschool also exists
                designed=true;
            else if(!findNearbyRobots(RobotType.DESIGN_SCHOOL,rc.getTeam())){
                // int d=rc.getLocation().distanceSquaredTo(hqLoc);
                if(rc.getLocation().distanceSquaredTo(hqLoc)<10){
                    if(tryBuild(RobotType.DESIGN_SCHOOL,Util.randomDirection())){
                         designed=true;
                         // System.out.println("BUILT DESIGN_SCHOOL ");
                     }
                }else {
                    System.out.println("TO FAR TO DESIGN");
                }
            }else{
                designed=true;
                System.out.println("alrdy designed; setting2TRUE");
             }
         }
        for(Direction dir: Util.directions){
            if(tryRefine(dir)){
                System.out.println("refining!_souploc:"+souploc);
            }else{
                // System.out.println("can't refie");
            } // REFINING
        }
        for(Direction dir: Util.directions){
            if(tryMine(dir)){
                if(souploc!=null && broadcastedSoupLoc==false){
                    comms.broadcastLoc(souploc,7);
                    broadcastedSoupLoc=true;
                }

                System.out.println("mining... ");
                souploc=null;
            }else{
                // System.out.println("Not mining");
            }
        }

        if(rc.getSoupCarrying() == rc.getType().soupLimit){//IF BAG FULL GOTO HQ
            // System.out.println("bag_full");
            if(souploc==null)
                souploc=rc.getLocation();
             if(gotonav(hqLoc))
                System.out.println("naving from souploc "+souploc+"to hqLoc: "+hqLoc);
            //vvvvvvvvvvvvvvvvvvvIF BAG NOT FULLvvvvvvvvvvvvvvvv
         }else{
            // IF ABLE TO GO TO SOUP_LOCATION...checking again for bag!full
            if(souploc!= null && rc.getSoupCarrying() != rc.getType().soupLimit){
                if(tryMove(rc.getLocation().directionTo(souploc))){
                    System.out.println("gong to SOUP: " + souploc);
                }else{ //OBSTACLE ON THE WAY TO SOUP ?
                    if(!tryMove(hqLoc.directionTo(souploc))){
                        if(!gotonav(souploc)){
                            System.out.println("trymove/souploc inaccessible! nav FAILED");
                        }
                    }
                }
                if(rc.getLocation().equals(souploc)){
                    if(tryMove(hqLoc.directionTo(souploc))){
                        tryMove(hqLoc.directionTo(souploc));    // just trying to go towards soup!
                    }else {
                        tryMove(Util.randomDirection());
                    }
                    System.out.println("moved BEYOND_towards soup");
                    souploc=null;
                    // boolean canstillmine=false;
                    // for(Direction dir : Util.directions) {
                    //     if(rc.canMineSoup(dir)){
                    //         canstillmine=true; break;
                    //     }
                    // }
                    // if(!canstillmine){
                    //     souploc=search4Soup(rc.getLocation());   // // weakness : goes underwater!!
                    //     System.out.println("GOT A LOC FROM $100 FN");
                    // } 
                    //SENSING SOUP IF ADJACENT TILE CONTAINS NO SOUP;

                }else {
                    //IF SOUPLOC NOT EQ TO GETLOC.
                }
            }else{
                if(!readSoupLoc){
                    System.out.println("going to readBlock");
                    comms.readBlock();
                    System.out.println("came from readBlock");
                    readSoupLoc=true;
                }
                if(souploc==null){
                    if(tryMove(Util.randomDirection())){
                        System.out.println("wandering randomly; BAG NOT FULL;SOUPLOC UNKNOWN");
                        if(been2mid==false && turnCount>15){ // skeptical
                            souploc=search4Soup(rc.getLocation());
                            //SKEPTICAL about search4soup.
                        }else {
                            // System.out.println("been2mid: "+been2mid+"turnCount: "+turnCount);
                        }
                    }
                }
            }
         }
    }

    MapLocation search4Soup(MapLocation curloc) throws GameActionException{
        MapLocation[] souplocs=rc.senseNearbySoup();     // SENSING WITH $100 FUNCTION!
        if(souplocs.length==0){
            MapLocation val=new MapLocation((rc.getMapWidth()/2),(rc.getMapHeight()/2));
            System.out.println("souploc NONE"); // weakness : goes underwater!!
            been2mid=true;
            return val;
        }else{
            int cp=0,temp=0,dst=10000;
            for (int j=0; j<souplocs.length; j++) {
                temp=curloc.distanceSquaredTo(souplocs[j]);
                if(dst>temp){
                    dst=temp;
                    cp=j;
                }
            }
            return souplocs[cp];
        }

    }

    boolean tryMine(Direction dir) throws GameActionException {
        if (rc.isReady() && rc.canMineSoup(dir)) {
            rc.mineSoup(dir);
            return true;
        } else return false;
    }

    /**
     * Attempts to refine soup in a given direction.
     *
     * @param dir The intended direction of refining
     * @return true if a move was performed
     * @throws GameActionException
     */
    boolean tryRefine(Direction dir) throws GameActionException {
        if (rc.isReady() && rc.canDepositSoup(dir)) {
            rc.depositSoup(dir, rc.getSoupCarrying());
            return true;
        } else return false;
    }

    // void checkIfSoupGone() throws GameActionException {
    //     if (soupLocations.size() > 0) {
    //         MapLocation targetSoupLoc = soupLocations.get(0);
    //         if (rc.canSenseLocation(targetSoupLoc)
    //                 && rc.senseSoup(targetSoupLoc) == 0) {
    //             soupLocations.remove(0);
    //         }
    //     }
    // }
}
