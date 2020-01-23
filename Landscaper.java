package MahinBot;
import battlecode.common.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Landscaper extends Unit {

    ArrayList<Direction> digtiles =new ArrayList<Direction>();
    ArrayList<MapLocation> pos2fill =new ArrayList<MapLocation>();
    ArrayList<Direction> dir2fill = new ArrayList<Direction>();
    Boolean lcapinPos=false;
    public Landscaper(RobotController r) {
        super(r);
    }

    public void takeTurn() throws GameActionException {
        super.takeTurn();
        prepPos();
        while(!lcapinPos){
            checkPos();
        }
        if(lcapinPos && !findNearbyRobots(RobotType.DESIGN_SCHOOL,rc.getTeam())){  //IN POSITION.
            System.out.println(lcapinPos+"and"+!findNearbyRobots(RobotType.DESIGN_SCHOOL,rc.getTeam()));

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
                digtiles.addAll(Arrays.asList(Util.directions));    
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
    boolean tryDig() throws GameActionException {
        Direction dir= Util.randomDirection();
        if (rc.canDigDirt(dir)){
            rc.digDirt(dir);
            return true;
        }
        return false;
    }
    boolean tryDig(Direction dir) throws GameActionException {
        if (rc.canDigDirt(dir)){
            rc.digDirt(dir);
            return true;
        }
        return false;
    }

    void checkPos() throws GameActionException{
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
                        while(!tryMove(Util.randomDirection())){  // POSSIBLE INFINITE LOOP?
                            tryMove(Util.randomDirection());
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
                    if(!tryMove(Util.randomDirection())){
                        System.out.println("opposite also dont work, trying randomDirection");
                        tryMove(currentLocation.directionTo(togo).opposite());

                    }
                }
            }
        }
    }
    void prepPos() throws GameActionException{ // twin in design_school
        if(pos2fill.isEmpty() && hqLoc!=null){
            MapLocation temp= new MapLocation(0,0);
            for (Direction dir : Util.directions) {
                temp=hqLoc.add(dir).add(dir); // (10,10) to (14,7) in range?
                if(rc.onTheMap(temp)){
                    pos2fill.add(temp);
                }
            }
            // pos2fill=pos2fill;          
            System.out.println("pos2fill: "+pos2fill);  
        }else {
            // tryMove(Util.randomDirection()); // lcap get away far !!
        }
    }
}