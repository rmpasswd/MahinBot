// TAKEN FROM https://github.com/battlecode/battlecode20-scaffold/tree/master/src
//MAHINS_CODE
package MahinBot;
import battlecode.common.*;
import java.lang.Math;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Random;
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
    static MapLocation hqLoc=null, souploc=null,dsLoc=null;
    static Boolean designed=false,been2mid=false,lcapinPos=false;
    // static ArrayList<MapLocation> loc2fill =new ArrayList<MapLocation>();
    static ArrayList<Direction> digtiles =new ArrayList<Direction>();
    static ArrayList<MapLocation> pos2fill =new ArrayList<MapLocation>();
    // static ArrayList<Direction> dir2fill = new ArrayList<Direction>();
    static Direction[] dir2fill = new Direction[3];
    static Random rndm = new Random();
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
        dturn=0;

        System.out.println("I'm a " + rc.getType() + " and I just got created!");
        while (true) {
            turnCount += 1;
            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
            try {
                // Here, we've separated the controls into a different method for each RobotType.
                // You can add the missing ones or rewrite this into your own control structure.
                System.out.println("I'm a " + rc.getType() + "! Location " + rc.getLocation());
                findhq();
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
        // testing designed value:
        //ALSO BUILD A DESIGN SCHOOL opposite dir that of hqlocation:

       if(designed==false){
            if(findNearbyRobots(RobotType.LANDSCAPER,rc.getTeam()))
                designed=true;
            else if(!findNearbyRobots(RobotType.DESIGN_SCHOOL,rc.getTeam())){
                int d=rc.getLocation().distanceSquaredTo(hqLoc);
                if(d<10){
                    if(tryBuild(RobotType.DESIGN_SCHOOL,randomDirection())){
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
        for(Direction dir: directions)
            if(tryRefine(dir)){
                System.out.println("refining!_souploc:"+souploc);
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
            // System.out.println("bag_full");
            if(souploc==null)
                souploc=rc.getLocation();
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
                    if(tryMove(randomDirection())){
                        // System.out.println("moved randomly cuz trymove/souploc inaccessible!");
                    }
                }
                if(rc.getLocation().equals(souploc)){
                    if(tryMove(hqLoc.directionTo(souploc))){
                        tryMove(hqLoc.directionTo(souploc));    // just trying to go towards soup!
                    }else {
                        tryMove(randomDirection());
                    }
                    System.out.println("moved BEYOND_towards soup");
                    souploc=null;
                    // boolean canstillmine=false;
                    // for(Direction dir : directions) {
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
                if(tryMove(randomDirection())){
                    System.out.println("wandering randomly; BAG NOT FULL;SOUPLOC UNKNOWN");
                    if(been2mid==false && turnCount>15){
                        souploc=search4Soup(rc.getLocation());
                        //SKEPTICAL about search4soup.
                    }else {
                        // System.out.println("been2mid: "+been2mid+"turnCount: "+turnCount);
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
        prepPos();
        if(dturn<pos2fill.size()){
            if(tryBuild(RobotType.LANDSCAPER,randomDirection())){
                // System.out.println("build lscapers at dturn: "+dturn+" and pos2fill: "+pos2fill.size());
                dturn++;
            }            
        }else{
            System.out.println("RIP me");
            rc.disintegrate();
        }


    }

    static void runFulfillmentCenter() throws GameActionException {
        for (Direction dir : directions)
            tryBuild(RobotType.DELIVERY_DRONE, dir);
    }

    static void runLandscaper() throws GameActionException {
        prepPos();
        while(!lcapinPos){
            checkPos();
        }
        if(lcapinPos && !findNearbyRobots(RobotType.DESIGN_SCHOOL,rc.getTeam())){  //IN POSITION.
            System.out.println(lcapinPos+"and"+!findNearbyRobots(RobotType.DESIGN_SCHOOL,rc.getTeam()));

            MapLocation currentLocation= rc.getLocation();
            if(dir2fill[0]==null){
                dir2fill[0]=currentLocation.directionTo(currentLocation);
                Direction dto=currentLocation.directionTo(hqLoc);
                if(dto.toString().length()>5){
                    dir2fill[1]=dto.rotateRight();
                    dir2fill[2]=dto.rotateLeft();
                }else {
                    dir2fill[1]=dto.rotateRight().rotateRight();
                    dir2fill[2]=dto.rotateLeft().rotateLeft();
                }
                System.out.println(dir2fill);
            }
            // if(loc2fill.isEmpty()){
            //     MapLocation currentLocation= rc.getLocation();
            //     Direction dto=currentLocation.directionTo(hqLoc);
            //     loc2fill.add(currentLocation);    
            //     if(dto.toString().length()>5){
            //         loc2fill.add(currentLocation.add(dto.rotateRight()));
            //         loc2fill.add(currentLocation.add(dto.rotateLeft()));
            //     }else {
            //         loc2fill.add(currentLocation.add(dto.opposite()));
            //         loc2fill.add(currentLocation.add(dto));
            //     }
            // }
            if(digtiles.isEmpty()){
                digtiles.addAll(Arrays.asList(directions));    digtiles.remove(dir2fill[1]);   digtiles.remove(dir2fill[2]);
            }
            if(rc.getDirtCarrying()!=0){
                int minEl=9999,curEl=0; 
                Direction runningDir=null; 
                // Direction[] itover = dir2fill.toArray(); 

                for (Direction dir : dir2fill) {
                    curEl=rc.senseElevation(currentLocation.add(dir));
                    if(minEl>curEl){
                        minEl=curEl;
                        runningDir=dir; //////// this caused error so changed dir2fill to array.
                    }
                }
                System.out.println("the tile to dump dirt: "+runningDir);
                if(rc.canDepositDirt(runningDir)){
                    rc.depositDirt(runningDir);
                }
            }else {  // IF NO DIRT CARRYING.
                System.out.println("DIGGIN FROM "+digtiles);
                Direction dir=digtiles.get(rndm.nextInt(digtiles.size()));
                tryDig(dir);
            }
        }else {
            if (turnCount>20) {
                System.out.println("getting old but not in position? :"+lcapinPos);
            }
        }

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
    //888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888
    static void checkPos() throws GameActionException{
        if(!pos2fill.isEmpty()){
            MapLocation currentLocation=rc.getLocation();
            for (MapLocation ml : pos2fill) {
                if(currentLocation.equals(ml)){                
                    // MapLocation todel= new MapLocation();    
                    pos2fill.remove(new MapLocation(ml.x,ml.y));
                    lcapinPos=true; return;
                }
            }
            System.out.println("not ON ANY LOC");
            MapLocation togo=pos2fill.get(rndm.nextInt(pos2fill.size()));
            if(rc.canSenseLocation(togo)){
                // System.out.println("togo_nearby: "+togo);
                RobotInfo rb=rc.senseRobotAtLocation(togo);
                if(rb!=null){
                    if(rb.type==RobotType.LANDSCAPER && rb.team==rc.getTeam()){
                        pos2fill.remove(new MapLocation(togo.x,togo.y));
                        checkPos();// if already occ. by another lscaper
                        return;
                            //dont doooooooooooooooooo this! pos2fill.remove(new MapLocation(togo));
                    }else if(rb.type==RobotType.DESIGN_SCHOOL && pos2fill.size()==1){
                        // System.out.println("DESIGN SCHOOL GETTING IN THE WAY...");
                        
                    }else { //IF TOGO is not a landscaper and design_school.
                        if(!tryMove(currentLocation.directionTo(togo))){
                            tryMove(randomDirection());
                        }
                    }                    
                }else { // IF THE TILE IS EMPTY 
                    if(!tryMove(currentLocation.directionTo(togo))){
                            if(!tryMove(hqLoc.directionTo(togo)))
                                tryMove(randomDirection()); // todo: need to tweak this.// update: how's this?

                            // System.out.println("can't go but will go");
                    }
                }

            }else { // IF TOGO IS NOT WITHIN SENSOR
                if(!tryMove(currentLocation.directionTo(togo))){
                    tryMove(randomDirection());
                }
            }
        }
    }
    static void prepPos() throws GameActionException{
        if(pos2fill.isEmpty()){
            for (Direction dir : directions) {
                MapLocation temp=hqLoc.add(dir).add(dir);
                if(rc.onTheMap(temp)){
                    pos2fill.add(temp);
                }
            }
            // pos2fill=pos2fill;          
            System.out.println("pos2fill: "+pos2fill);  
        }else {
            // System.out.println("hqlocation still unknown or pos2fill full");
        }
    }
    static void findhq() throws GameActionException{
        if(hqLoc == null){
            //getting the miners search for hq location
            RobotInfo[] nearbybots = rc.senseNearbyRobots(rc.getCurrentSensorRadiusSquared(), rc.getTeam());
            for ( RobotInfo bot : nearbybots ){
                if(bot.type == RobotType.HQ){
                    hqLoc=bot.location;
                    return;
                }
            }
        }


    }
    static MapLocation search4Soup(MapLocation curloc) throws GameActionException{
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

    static boolean findNearbyRobots(RobotType targetbot) throws GameActionException {
        RobotInfo[] bots = rc.senseNearbyRobots();
        for (RobotInfo bot : bots) {
            if(bot.type == targetbot){
                // System.out.println("detected!"+targetbot+"!!RESPOND....");
                return true;
            }
        }
        return false;
    }
 static boolean findNearbyRobots(RobotType targetbot,Team side) throws GameActionException {
        RobotInfo[] bots = rc.senseNearbyRobots(-1,side);
        for (RobotInfo bot : bots) {
            if(bot.type == targetbot){
                // System.out.println("bot of INTENDED SIDE detected!"+targetbot);
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
    static boolean tryDig() throws GameActionException {
        Direction dir= randomDirection();
        if (rc.canDigDirt(dir)){
            rc.digDirt(dir);
            return true;
        }
        return false;
    }
    static boolean tryDig(Direction dir) throws GameActionException {
        if (rc.canDigDirt(dir)){
            rc.digDirt(dir);
            return true;
        }
        return false;
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
