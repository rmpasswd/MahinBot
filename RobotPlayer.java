// TAKEN FROM https://github.com/battlecode/battlecode20-scaffold/tree/master/src
//MAHINS_CODE
package MahinBot;
import battlecode.common.*;
import java.lang.Math;
// import java.util.Arrays;
// import java.util.IntSummaryStatistics;
public strictfp class RobotPlayer {
    static RobotController rc;
    static Direction[] directions = {
        Direction.NORTH,
        Direction.NORTHEAST,
        Direction.EAST,
        Direction.SOUTHEAST,
        Direction.SOUTH,
        Direction.SOUTHWEST,
        Direction.WEST,
        Direction.NORTHWEST
    };
    static RobotType[] spawnedByMiner = {RobotType.REFINERY, 
            RobotType.VAPORATOR, RobotType.DESIGN_SCHOOL,
            RobotType.FULFILLMENT_CENTER, RobotType.NET_GUN};

    static int turnCount,dturn=0;
    static MapLocation hqLoc=null, souploc=null;
    static Boolean designed=false,been2mid=false;

    /**
     * run() is the method that is called when a robot is instantiated in the Battlecode world.
     * If this method returns, the robot dies!
     **/
    @SuppressWarnings("unused")
    public static void run(RobotController rc) throws GameActionException {

        // This is the RobotController object. You use it to perform actions from this robot,
        // and to get information on its current status.
        RobotPlayer.rc = rc;
        turnCount = 0;

        System.out.println("I'm a " + rc.getType() + " and I just got created!");
        while (true) {
            turnCount += 1;
            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
            try {
                // Here, we've separated the controls into a different method for each RobotType.
                // You can add the missing ones or rewrite this into your own control structure.
                System.out.println("I'm a " + rc.getType() + "! Location " + rc.getLocation());
                switch (rc.getType()) {
                    case HQ:                 runHQ();                break;
                    case MINER:              runMiner();             break;
                    case REFINERY:           runRefinery();          break;
                    case VAPORATOR:          runVaporator();         break;
                    case DESIGN_SCHOOL:      runDesignSchool();      break;
                    case FULFILLMENT_CENTER: runFulfillmentCenter(); break;
                    case LANDSCAPER:         runLandscaper();        break;
                    case DELIVERY_DRONE:     runDeliveryDrone();     break;
                    case NET_GUN:            runNetGun();            break;
                }

                // Clock.yield() makes the robot wait until the next turn, then it will perform this loop again
                Clock.yield();

            } catch (Exception e) {
                System.out.println(rc.getType() + " Exception");
                e.printStackTrace();
            }
        }
    }

    static void runHQ() throws GameActionException {
        
        if (turnCount < 20 || turnCount%100==0) {
            for (Direction dir : directions)
                tryBuild(RobotType.MINER, dir);
        }


    }

    static void runMiner() throws GameActionException {
        if(hqLoc == null){
            //getting the miners search for hq location
            RobotInfo[] nearbybots = rc.senseNearbyRobots(rc.getCurrentSensorRadiusSquared(), rc.getTeam());
            for ( RobotInfo bot : nearbybots ){
                if(bot.type == RobotType.HQ){
                    hqLoc=bot.location;
                    // System.out.println("got the loc:_"+hqLoc);
                }
            }
            System.out.println("cd: "+rc.getCooldownTurns());
        }else{
            // System.out.println("ALREADY CALCULATED LOC: "+ hqLoc);
        }
        // testing designed value:
        //ALSO BUILD A DESIGN SCHOOL opposite dir that of hqloc:
       if(designed==false){
            if(!findNearbyRobots(RobotType.DESIGN_SCHOOL,rc.getTeam())){
                int d=rc.getLocation().distanceSquaredTo(hqLoc);
                if(d>2 && d<=10){
                    if(tryBuild(RobotType.DESIGN_SCHOOL,rc.getLocation().directionTo(hqLoc).opposite())){
                        designed=true;
                        System.out.println("BUILT DESIGN_SCHOOL ");
                    }
                }else {
                    System.out.println("DISTANCE MISMATCH TO DESIGN");
                }  
            }else {
                designed=true;
                System.out.println("somebody designed; setting_to_true ");
            }
       }else{
        // System.out.println("ALREADY TRUE");
       }

        for(Direction dir: directions)
            if(tryRefine(dir)){
                // System.out.println("refining!");
            }else{
                // System.out.println("can't refie");
            } // REFINING
        for(Direction dir: directions)
            if(tryMine(dir)){
                System.out.println("mining... ");
                souploc=null;
            }else{
                // System.out.println("Not mining");
            }
        if(rc.getSoupCarrying() == rc.getType().soupLimit){//IF BAG FULL GOTO HQ
            if(souploc==null)
                souploc=rc.getLocation();
            // System.out.println("free? :"+rc.canBuildRobot(RobotType.DESIGN_SCHOOL,rc.getLocation().directionTo(hqLoc)));
             if(tryMove(rc.getLocation().directionTo(hqLoc)))
                System.out.println("GOING FROM souploc "+souploc+"to hqLoc: "+hqLoc);
            else{
                tryMove(randomDirection()); // if CAN'T GO TO HQ DESPITE BAG FULL.\
                                            // PLACE TO IMPLEMENT PATH FINDING TO HQ 
                                            // OR CLEARING THE PATH USING LANDSCAPERS.
            }
            //vvvvvvvvvvvvvvvvvvvIF BAG NOT FULLvvvvvvvvvvvvvvvv
         }else{
            // IF ABLE TO GO TO SOUP_LOCATION
            if(souploc!= null && rc.getSoupCarrying() != rc.getType().soupLimit){
                if(tryMove(rc.getLocation().directionTo(souploc))){
                    System.out.println("gong to SOUP: " + souploc);
                }else{ //OBSTACLE ON THE WAY TO SOUP ?
                    tryMove(randomDirection());
                    System.out.println("OBSTACLE TO SOUP");
                    souploc=null;
                }
                if(rc.getLocation().equals(souploc)){
                    tryMove(hqLoc.directionTo(souploc));
                    System.out.println("moved BEYOND_towards soup");
                    if(rc.senseSoup(rc.getLocation().add(hqLoc.directionTo(souploc)))==0){
                        souploc=search4Soup(rc.getLocation());
                        System.out.println("GOT A LOC FROM $100 FN");
                    } //SENSING SOUP IF ADJACENT TILE CONTAINS NO SOUP;
                }else {
                    //IF SOUPLOC NOT EQ TO GETLOC.
                }
            }else{
                if(tryMove(randomDirection())){
                    System.out.println("wandering randomly; BAG NOT FULL;SOUPLOC UNKNOWN");
                    if(been2mid==false turnCount>120){
                        souploc=search4Soup(rc.getLocation());
                        //SKEPTICAL about search4soup.
                    }

                }
            }
         }

         //IF SOUP LOCATION KNOWN AND BAG NOT FULL =\/

    }//END OF RUNMINER()

    static void runRefinery() throws GameActionException {
        // System.out.println("Pollution: " + rc.sensePollution(rc.getLocation()));
    }

    static void runVaporator() throws GameActionException {

    }

    static void runDesignSchool() throws GameActionException {
        if(dturn<5){
            if(tryBuild(RobotType.LANDSCAPER,randomDirection())){
                System.out.println("build lscapers at dturn: "+dturn);
                dturn++;
            }            
        }else{
            System.out.println("enough designing");
        }
    }

    static void runFulfillmentCenter() throws GameActionException {
        for (Direction dir : directions)
            tryBuild(RobotType.DELIVERY_DRONE, dir);
    }

    static void runLandscaper() throws GameActionException {

    }

    static void runDeliveryDrone() throws GameActionException {
        Team enemy = rc.getTeam().opponent();
        if (!rc.isCurrentlyHoldingUnit()) {
            // See if there are any enemy robots within striking range (distance 1 from lumberjack's radius)
            RobotInfo[] robots = rc.senseNearbyRobots(GameConstants.DELIVERY_DRONE_PICKUP_RADIUS_SQUARED, enemy);

            if (robots.length > 0) {
                // Pick up a first robot within range
                rc.pickUpUnit(robots[0].getID());
                System.out.println("I picked up " + robots[0].getID() + "!");
            }
        } else {
            // No close robots, so search for robots within sight radius
            tryMove(randomDirection());
        }
    }

    static void runNetGun() throws GameActionException {

    }

    static MapLocation search4Soup(MapLocation curloc) throws GameActionException{
        MapLocation[] souplocs=rc.senseNearbySoup();     // SENSING WITH $100 FUNCTION!
        if(souplocs.length==0){
            MapLocation val=new MapLocation((rc.getMapWidth()/2),(rc.getMapHeight()/2));
            System.out.println("souploc NONE"); 
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

    static boolean findNearbyRobots(RobotType targetbot) throws GameActionException {
        RobotInfo[] bots = rc.senseNearbyRobots();
        for (RobotInfo bot : bots) {
            if(bot.type == targetbot){
                System.out.println("detected!"+targetbot+"!!RESPOND....");
                return true;
            }
        }
        return false;
    }
 static boolean findNearbyRobots(RobotType targetbot,Team side) throws GameActionException {
        RobotInfo[] bots = rc.senseNearbyRobots(-1,side);
        for (RobotInfo bot : bots) {
            if(bot.type == targetbot){
                System.out.println("bot of INTENDED SIDE detected!"+targetbot);
                return true;

            }
        }
        return false;
    }
      /**
     * Returns a random Direction.
     *
     * @return a random Direction
     */
    static Direction randomDirection() {
        return directions[(int) (Math.random() * directions.length)];
    }

    /**
     * Returns a random RobotType spawned by miners.
     *
     * @return a random RobotType
     */
    static RobotType randomSpawnedByMiner() {
        return spawnedByMiner[(int) (Math.random() * spawnedByMiner.length)];
    }

    static boolean tryMove() throws GameActionException {
        for (Direction dir : directions)
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

    /**
     * Attempts to move in a given direction.
     *
     * @param dir The intended direction of movement
     * @return true if a move was performed
     * @throws GameActionException
     */
    static boolean tryMove(Direction dir) throws GameActionException {
        // System.out.println("I am trying to move " + dir + "; " + rc.isReady() + " " + rc.getCooldownTurns() + " " + rc.canMove(dir));
        if (rc.isReady() && rc.canMove(dir)) {
            rc.move(dir);
            return true;
        } else return false;
    }

    /**
     * Attempts to build a given robot in a given direction.
     *
     * @param type The type of the robot to build
     * @param dir The intended direction of movement
     * @return true if a move was performed
     * @throws GameActionException
     */
    static boolean tryBuild(RobotType type, Direction dir) throws GameActionException {
        if (rc.isReady() && rc.canBuildRobot(type, dir)) {
            rc.buildRobot(type, dir);
            // System.out.println("built the thing!!");
            return true;
        } else return false;
    }

    /**
     * Attempts to mine soup in a given direction.
     *
     * @param dir The intended direction of mining
     * @return true if a move was performed
     * @throws GameActionException
     */
    static boolean tryMine(Direction dir) throws GameActionException {
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
    static boolean tryRefine(Direction dir) throws GameActionException {
        if (rc.isReady() && rc.canDepositSoup(dir)) {
            rc.depositSoup(dir, rc.getSoupCarrying());
            return true;
        } else return false;
    }
 

    static void tryBlockchain() throws GameActionException {
        if (turnCount < 3) {
            int[] message = new int[7];
            for (int i = 0; i < 7; i++) {
                message[i] = 123;
            }
            if (rc.canSubmitTransaction(message, 10))
                rc.submitTransaction(message, 10);
        }
        // System.out.println(rc.getRoundMessages(turnCount-1));
    }
}
