package MahinBot;
import battlecode.common.* ;
import java.util.ArrayList;

public class DesignSchool extends Building
{
    static int dturn=0,halturn=0;
    static ArrayList<MapLocation> pos2fill =new ArrayList<MapLocation>();
    public DesignSchool ( RobotController r )  { super(r); }

    public void takeTurn() throws GameActionException
    {
        super.takeTurn();

        prepPos();
        //DONT LOOK DOWN!! JUST ADDING VARIETY TO BUILD Util.directions!!
        if(dturn<pos2fill.size()){
            if(!tryBuild(RobotType.LANDSCAPER,Util.directions[dturn%8])){
                // System.out.println("build lscapers at dturn: "+dturn+" and pos2fill: "+pos2fill.size());
                if(!tryBuild(RobotType.LANDSCAPER,rc.getLocation().directionTo(hqLoc).rotateRight()) &&
                    !tryBuild(RobotType.LANDSCAPER,rc.getLocation().directionTo(hqLoc).rotateRight())){
                        if(tryBuild(RobotType.LANDSCAPER,Util.randomDirection())){
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


    void prepPos() throws GameActionException{  // twin in lscaper!
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
