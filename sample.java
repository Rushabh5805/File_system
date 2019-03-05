package operatingSystem;

import java.util.StringTokenizer;

/**
 * Created by parthgodhani on 11/6/17.
 */
public class sample {

    public static void main(String[] args) {
        short a='F';
        byte b='F';
        byte type='D';
        byte[] bArray="".getBytes();
        String nameString=new String("parth/godhani/tanjhna");
        String fileName = nameString.substring(nameString.lastIndexOf('/')+1,nameString.length());
        String lastDirPath=nameString.substring(0,nameString.lastIndexOf('/'));
        StringTokenizer filePathTokenizer = new StringTokenizer(new String("parth"), "/");
        fileName=filePathTokenizer.nextToken();
        System.out.println( fileName+" "+ "abc".substring(0,2));


        String data="create a 50";
        StringTokenizer s=new StringTokenizer(data," ");
        String command=s.nextToken();
        byte bit=(byte)s.nextToken().charAt(0);
        System.out.println(Short.decode(s.nextToken()));
    }

}
