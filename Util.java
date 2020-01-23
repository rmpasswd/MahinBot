package MahinBot;
import battlecode.common.*;

public class Util
{

    static Direction[] directions =
    {
            Direction.NORTH,
            Direction.NORTHEAST,
            Direction.EAST,
            Direction.SOUTHEAST,
            Direction.SOUTH,
            Direction.SOUTHWEST,
            Direction.WEST,
            Direction.NORTHWEST
    };

    static Direction randomDirection()
    {
        return directions[(int)(Math.random()*directions.length)] ;
    }

}
