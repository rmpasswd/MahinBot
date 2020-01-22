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

    static int turnCount,dturn=0,halturn=0;
    static MapLocation hqLoc=null, souploc=null,dsLoc=null;
    static Boolean designed=false,been2mid=false,lcapinPos=false,broadcastedSoupLoc=false,readSoupLoc=false;
    // static ArrayList<MapLocation> loc2fill =new ArrayList<MapLocation>();
    static ArrayList<Direction> digtiles =new ArrayList<Direction>();
    static ArrayList<MapLocation> pos2fill =new ArrayList<MapLocation>();
    static ArrayList<Direction> dir2fill = new ArrayList<Direction>();
    // static Direction[] dir2fill = new Direction[3];
    static Random rndm = new Random();
    static final int teamSecret = 47;  // dont like it final;
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
        if(turnCount==1){
            broadcastLoc(hqLoc,5);
        }
        if (turnCount < 20 || turnCount%100<5) {
            for (Direction dir : directions)
                tryBuild(RobotType.MINER, dir);
        }

    }

    static void runMiner() throws GameActionException {
        //ALSO BUILD A DESIGN SCHOOL opposite dir that of hqlocation:
        
       if(designed==false){
            if(findNearbyRobots(RobotType.LANDSCAPER,rc.getTeam())) //found lcap then dschool also exists
                designed=true;
            else if(!findNearbyRobots(RobotType.DESIGN_SCHOOL,rc.getTeam())){
                // int d=rc.getLocation().distanceSquaredTo(hqLoc);
                if(rc.getLocation().distanceSquaredTo(hqLoc)<10){
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
        for(Direction dir: directions){
            if(tryRefine(dir)){
                System.out.println("refining!_souploc:"+souploc);
            }else{
                // System.out.println("can't refie");
            } // REFINING
        }
        for(Direction dir: directions){
            if(tryMine(dir)){
                if(souploc!=null && broadcastedSoupLoc==false){
                    broadcastLoc(souploc,7);
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
                if(!readSoupLoc){
                    readBlock();
                    readSoupLoc=true;
                }
                if(souploc==null){
                    if(tryMove(randomDirection())){
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

         //IF SOUP LOCATION KNOWN AND BAG NOT FULL =\/

    }//END OF RUNMINER()

    static void runRefinery() throws GameActionException {
        // System.out.println("Pollution: " + rc.sensePollution(rc.getLocation()));
    }

    static void runVaporator() throws GameActionException {

    }

    static void runDesignSchool() throws GameActionException {
        prepPos();
        //DONT LOOK DOWN!! JUST ADDING VARIETY TO BUILD DIRECTIONS!!
        if(dturn<pos2fill.size()){
            if(!tryBuild(RobotType.LANDSCAPER,directions[dturn%8])){
                // System.out.println("build lscapers at dturn: "+dturn+" and pos2fill: "+pos2fill.size());
                if(!tryBuild(RobotType.LANDSCAPER,rc.getLocation().directionTo(hqLoc).rotateRight()) &&
                    !tryBuild(RobotType.LANDSCAPER,rc.getLocation().directionTo(hqLoc).rotateRight())){
                        if(tryBuild(RobotType.LANDSCAPER,randomDirection())){
                            dturn++; halturn=turnCount;
                        }
                }else {
                dturn++; halturn=turnCount;    
                    
                }
            }else {
                dturn++; halturn=turnCount;    
            }
        }else{
            if(halturn+10==turnCount){
                rc.disintegrate();
            }
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
            // System.out.println(lcapinPos+"and"+!findNearbyRobots(RobotType.DESIGN_SCHOOL,rc.getTeam()));

            MapLocation currentLocation= rc.getLocation();
            if(dir2fill.isEmpty()){
                dir2fill.add(currentLocation.directionTo(currentLocation));    

                Direction dto=currentLocation.directionTo(hqLoc);
                if(dto.toString().length()>5){
                    dir2fill.add(dto.rotateRight());
                    dir2fill.add(dto.rotateLeft());
                }else {
                    Direction tempd=dto.rotateRight().rotateRight();
                    if(rc.onTheMap(currentLocation.add(tempd)))
                        dir2fill.add(tempd);
                    tempd=dto.rotateLeft().rotateLeft();
                    if(rc.onTheMap(currentLocation.add(tempd)))
                        dir2fill.add(tempd);
                }
            }
            if(digtiles.isEmpty()){
                digtiles.addAll(Arrays.asList(directions));    
                digtiles.removeAll(dir2fill);
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
            MapLocation togo=pos2fill.get(rndm.nextInt(pos2fill.size()));
            int min=999;
            for (MapLocation ml : pos2fill) {
                if(currentLocation.equals(ml)){                
                    // MapLocation todel= new MapLocation();    
                    pos2fill.remove(new MapLocation(ml.x,ml.y));
                    lcapinPos=true; break;
                }else {
                    int tempd=currentLocation.distanceSquaredTo(ml);
                    if(min>tempd){
                        min=tempd;
                        togo=ml;
                    }
                }
            }
            if(lcapinPos) return;
            // System.out.println("not ON ANY LOC");
            if(rc.canSenseLocation(togo)){
                System.out.println("togo_nearby: "+togo+" cd: "+rc.getCooldownTurns());
                RobotInfo rb=rc.senseRobotAtLocation(togo);

                if(rb!=null){
                    if(rb.type==RobotType.LANDSCAPER && rb.team==rc.getTeam()){
                        pos2fill.remove(new MapLocation(togo.x,togo.y));
                        checkPos();// if already occ. by another lscaper
                        return;
                    }else if(rb.type==RobotType.DESIGN_SCHOOL){
                        while(!tryMove(randomDirection())){  // POSSIBLE INFINITE LOOP?
                            tryMove(randomDirection());
                        }
                        return;
                        
                    }else { //IF TOGO is not a landscaper and design_school.
                        if(!gotonav(togo)){
                            System.out.println("naving failed!");
                                if(!tryMove(currentLocation.directionTo(togo).opposite())){
                                    System.out.println("opposite also dont work");
                                }                            
                        }
                    }                    
                }else { // IF THE TILE IS EMPTY 
                    System.out.println("trying to nav to: "+togo);
                    if(!gotonav(togo)){
                        System.out.println("naving failed!");
                        if(!tryMove(currentLocation.directionTo(togo).opposite())){
                            System.out.println("opposite also dont work");
                        }                        
                    }
                }

            }else { // IF TOGO IS NOT WITHIN SENSOR
                if(!gotonav(togo)){
                    System.out.println("naving failed!");
                    if(!tryMove(randomDirection())){
                        System.out.println("opposite also dont work, trying randomDirection");
                        tryMove(currentLocation.directionTo(togo).opposite());

                    }
                }
            }
        }
    }
    static void prepPos() throws GameActionException{
        if(pos2fill.isEmpty() && hqLoc!=null){
            MapLocation temp= new MapLocation(0,0);
            for (Direction dir : directions) {
                temp=hqLoc.add(dir).add(dir); // (10,10) to (14,7) in range?
                if(rc.onTheMap(temp)){
                    pos2fill.add(temp);
                }
            }
            // pos2fill=pos2fill;          
            System.out.println("pos2fill: "+pos2fill);  
        }else {
            // tryMove(randomDirection()); // lcap get away far !!
        }
    }
    static void findhq() throws GameActionException{
        if(hqLoc == null){
            if(rc.getType()==RobotType.HQ){
                hqLoc=rc.getLocation(); return;
            }
            //getting the miners search for hq location
            RobotInfo[] nearbybots = rc.senseNearbyRobots(rc.getCurrentSensorRadiusSquared(), rc.getTeam());
            System.out.println("for hq nearbybots:! "+nearbybots);
            for ( RobotInfo bot : nearbybots ){
                if(bot.type == RobotType.HQ){
                    hqLoc=bot.location;
                    return;
                }
            }
        }
        if(hqLoc==null){
            readBlock();
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
        RobotInfo[] bots = rc.senseNearbyRobots(); ///  yet another 100$ fn
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
    static boolean gotonav(Direction dir) throws GameActionException {
        Direction[] toTry = {dir, dir.rotateLeft(), dir.rotateRight(), dir.rotateLeft().rotateLeft(), dir.rotateRight().rotateRight()};
        for (Direction d : toTry){
            System.out.println("trying: "+d);
            if(tryMove(d))
                return true;
        }
        return false;
    } 
    static boolean gotonav(MapLocation ml)throws GameActionException{
        return gotonav(rc.getLocation().directionTo(ml));
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
        if (rc.isReady() && rc.canMove(dir) && !rc.senseFlooding(rc.getLocation().add(dir))) {
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
    static void broadcastLoc(MapLocation loc,int ab) throws GameActionException{
        int[] message = new int[7] ;
        message[0] = teamSecret ;
        message[1] = ab ;
        message[2] = loc.x ;
        message[3] = loc.y ;
        if(rc.canSubmitTransaction(message , 3 )) {
            rc.submitTransaction( message , 3 ) ;
        }

    }
    static void readBlock() throws GameActionException {
        for(int rn=1;rn<rc.getRoundNum();rn++){
            Transaction[] blk=rc.getBlock(rn);
            for (Transaction eachblk : blk) {
                int[] ms=eachblk.getMessage();
                if(ms[0]==teamSecret){ // SWITCH STATEMENTS HERE ?
                    if(ms[1]==5){
                        hqLoc=new MapLocation(ms[2],ms[3]);                    
                    }else if (ms[1]==7) {
                        souploc=new MapLocation(ms[2],ms[3]);
                    }
                }
            }
        }
    }
    static void tryBlockchain() throws GameActionException { // dummy/sample function
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
