package operatingSystem;

import java.util.ArrayList;

/**
 * Created by parthgodhani on 10/24/17.(PARTHKUMAR GODHANI - gg2835)
 */
public class DIRECTORY {

    DIRECTORY()
    {
       // System.out.println("DIR BLOCK CREATED");
        BACK=0;
        FREE=0;
        FRWD=0;
        FILLER="DIRS".getBytes();
    }
    int BACK;
    int FRWD;
    int FREE;
    byte[] FILLER=new byte[4];
    ArrayList<DIR> DIRS=new ArrayList<>(31);

}
class DIR{

    byte TYPE;
    byte[] NAME=new byte[9];
    int LINK;
    short SIZE;

    DIR(){
        LINK=0;
        SIZE=0;
    }
}
