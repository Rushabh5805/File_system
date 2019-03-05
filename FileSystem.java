package operatingSystem;

import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import javax.xml.crypto.Data;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;
import java.util.StringTokenizer;

/**
 * Created by parthgodhani on 11/6/17. (PARTHKUMAR GODHANI - gg2835)
 */

public class FileSystem {

    boolean readFlag = false, writeFlag = false, seekFlag = false, openFlag = false, readStartFlag = false;
    short maxDataSize = 504;
    DATA data;
    Scanner scanner = new Scanner(System.in);
    int filePointer = 0, dataPointer = 0, currDirPointer = 0, lastDataBlockSize = 0;
    DIRECTORY directory;
    DIR dir;
    ArrayList<Object> blocks = new ArrayList<>(100);
    ArrayList<DIR> dirs;

    public FileSystem() {
        directory = new DIRECTORY();
        directory.FREE = 1;
        directory.BACK = 0;
        directory.FILLER = new byte[]{'R', 'O', 'O', 'T'};
        directory.FRWD = 0;
        blocks.add(0, directory);
    }

    public static void main(String[] args) {

        FileSystem fileSystem = new FileSystem();

        byte typeD = 'D', typeU = 'U';
/**
        fileSystem.createFile(typeD, "parth".getBytes());
        fileSystem.createFile(typeD, "parth/godhani".getBytes());
        fileSystem.createFile(typeD, "parth/godhani/abc".getBytes());
        fileSystem.createFile(typeD, "parth/kumar".getBytes());
        fileSystem.createFile(typeD, "parth/kumar".getBytes());
        fileSystem.createFile(typeD, "parth/kumar/abc".getBytes());
        fileSystem.createFile(typeD, "parth/kumar/abc3".getBytes());
        fileSystem.createFile(typeU, "parth/kumar2".getBytes());
        //fileSystem.createFile(typeD, "parth/rutuja2/love".getBytes());
        // fileSystem.deleteFile("parth/rutuja2/love".getBytes());
        // fileSystem.createFile(typeD, "parth/rutuja2/love/4".getBytes());
        //fileSystem.openFile('U', "parth/rutuja2".getBytes());
        fileSystem.closeFile();
        fileSystem.openFile('U', "parth/kumar2".getBytes());


        fileSystem.writeData(10, "Parth Godhani".getBytes());
        //fileSystem.seekData((byte) 1, (short) -5);
        //fileSystem.seekData((byte)0,(short)4);
        fileSystem.readData(5);

        fileSystem.closeFile();
        fileSystem.openFile('U', "parth/rutuja2".getBytes());
        fileSystem.createFile(typeD, "parth/godhani3/tana".getBytes());
        //fileSystem.display();

        /**
         fileSystem.createFile(typeD, "parth/godhani4/california".getBytes());
         fileSystem.openFile('U', "parth/godhani3/tana".getBytes());
         fileSystem.closeFile();
         fileSystem.openFile('U', "parth/godhani4/california".getBytes());
         fileSystem.closeFile();
         fileSystem.openFile('U', "parth/godhani/surati".getBytes());
         //fileSystem.openFile('U', "parth/godhani/surat".getBytes());
         fileSystem.closeFile();


         //**/fileSystem.startSystem();


    }

    public void startSystem() {

        System.out.println("Enter Commands (Exit for termination)");
        String command = scanner.nextLine().toUpperCase();

        while (!command.equals("EXIT")) {
            StringTokenizer s = new StringTokenizer(command, " ");
            String name = "";
            byte c;
            switch (s.nextToken().toUpperCase()) {
                case "CREATE":

                    c = (byte)s.nextToken().charAt(0);
                    name = s.nextToken();
                    createFile( c, name.getBytes());
                    break;
                case "OPEN":
                    c = (byte)s.nextToken().charAt(0);
                    name = s.nextToken();
                    openFile((char) c, name.getBytes());
                    break;
                case "CLOSE":
                    closeFile();
                    break;
                case "DELETE":

                    name = s.nextToken();
                    deleteFile(name.getBytes());
                    break;
                case "READ":
                    int a = Integer.parseInt(s.nextToken().toString());
                    readData(a);
                    break;
                case "WRITE":
                    a = Integer.parseInt(s.nextToken());
                    name = command.substring(8,command.length());
                    writeData(a, name.getBytes());
                    break;
                case "SEEK":
                    byte b = (byte) s.nextToken().toString().charAt(0);
                    short offset = Short.decode(s.nextToken());
                    seekData(b, offset);
                    break;
                default:
                    System.out.println("Enetr Commands :");
            }
            System.out.println("Enter Next Command :");
            command = scanner.nextLine();

        }


    }

    //CREATE
    public void createFile(byte type, byte[] name) {
        String nameString = new String(name);
        String currentDirPath = getCurrDirPath(name);

        /**
         * check dir or file exist
         * **/
        if (getFileBlockNobyName((byte) 'U', name) > -1) {
            int blockNo = getFileBlockNobyName((byte) 'A', name);
            if (blocks.get(blockNo).getClass() == DATA.class) {
                blocks.set(blockNo, new DATA());
            }
            if (blocks.get(blockNo).getClass() == DIRECTORY.class) {
                blocks.set(blockNo, new DIRECTORY());
            }
        }
        /**
         * If dir or file not exist
         */
        else {


            //System.out.println("\nIN CREATE | Current Dir Path is: "+currentDirPath+" | status "+ getFileBlockNobyName(currentDirPath.getBytes()));
            int currentDirPointer = getFileBlockNobyName((byte) 'D', currentDirPath.getBytes());

            if (currentDirPointer > -1 || currentDirPath.equals("")) {
                String fileName = nameString.substring(nameString.lastIndexOf('/') + 1, nameString.length());

                int allocatedBlock = getNextFreeBlock();
                dir = new DIR();
                dir.TYPE = type;
                dir.NAME = fileName.getBytes();
                dir.LINK = allocatedBlock;

                if (((DIRECTORY) blocks.get(currentDirPointer)).DIRS.size() > 30) {
                    directory = ((DIRECTORY) blocks.get(currentDirPointer));
                    directory.FRWD = getNextFreeBlock();

                    DIRECTORY newDirectory = new DIRECTORY();
                    newDirectory.BACK = directory.FRWD;
                    blocks.add(directory.FRWD, newDirectory);
                    currentDirPointer = directory.FRWD;

                }
                dirTableEntry(currentDirPointer, dir);
                //System.out.println("IN CREATE | Directory Table Entry_____ TYPE: " + (char) dir.TYPE + "| Name: " + new String( dir.NAME) + "| LINK: " + dir.LINK + " in block number : " + currentDirPointer);

                if (type == 'D') {
                    directory = new DIRECTORY();
                    blocks.add(allocatedBlock, directory);
                }
                if (type == 'U') {
                    data = new DATA();
                    blocks.add(allocatedBlock, data);
                }
            } else {
                System.out.println("Directory Path does not exist!!! ");
            }
        }
        filePointer = 0;
        openFile('O',name);
    }

    public void openFile(char mode, byte[] name) {

        int fileBlockNo = getFileBlockNobyName((byte) 'A', name);
        currDirPointer = getFileBlockNobyName((byte) 'A', getCurrDirPath(name).getBytes());

        if (filePointer == 0) {
            if (fileBlockNo > -1 && blocks.get(fileBlockNo).getClass() == DATA.class) {
                openFlag = true;
                filePointer = fileBlockNo;

                if (mode == 'I' || mode == 'U') {
                    readFlag = true;
                    seekFlag = true;
                }
                if (mode == 'O' || mode == 'U') {
                    writeFlag = true;
                }
                System.out.println("File Opened :  " + new String(name) + " at " + filePointer);

            } else {
                System.out.println("Warning ! Invalid Filename. Try again ");
            }
        } else {
            System.out.println("Please close file!!!");
        }
    }


    public void closeFile() {
        openFlag = false;
        filePointer = 0;
        dataPointer = 0;
        readFlag = false;
        seekFlag = false;
        writeFlag = false;
        readStartFlag = false;
    }

    public void deleteFile(byte[] name) {
        if (getFileBlockNobyName((byte) 'A', name) > -1) {
            dir = new DIR();
            dir.TYPE = 'F';
            dir.NAME = "".getBytes();
            dirTableUpdate(getFileBlockNobyName((byte) 'U', getCurrDirPath(name).getBytes()), getFileBlockNobyName((byte) 'A', name), dir);
            System.out.println("Deleted : " + new String(name));
        } else {
            System.out.println("File does not exist!!");
        }

    }

    public void readData(int n) {
        System.out.println("READ DATA: \n\n");
        int readDataLength = n;

        if (readFlag) {

            System.out.println();
            if (!readStartFlag) {
                readStartFlag = true;
                //dataPointer=0;
                filePointer = getStartingPointerOfFile(filePointer);
                directory = (DIRECTORY) blocks.get(currDirPointer);
                for (DIR d : directory.DIRS) {
                    if (d.LINK == filePointer) {
                        lastDataBlockSize = d.SIZE;
                        break;
                    }
                }
                //System.out.println("File Pointer " + filePointer);


            }

            while (readDataLength > 0) {
                if ((dataPointer + readDataLength) > maxDataSize) {
                    System.out.print(dataReader(filePointer, dataPointer, maxDataSize));
                    readDataLength -= (maxDataSize - dataPointer);

                } else {
                    if (readDataLength > lastDataBlockSize) {
                        System.out.print(dataReader(filePointer, dataPointer, lastDataBlockSize));
                        System.out.print(" <<End of data!!!>>");
                        break;
                    } else {

                        System.out.print(dataReader(filePointer, dataPointer, readDataLength));
                    }


                }
                if (((DATA) blocks.get(filePointer)).FRWD == 0) {
                    if (readDataLength > lastDataBlockSize) {
                        System.out.print(dataReader(filePointer, dataPointer, lastDataBlockSize));
                        System.out.print(" <<End of data!!!>>");
                        break;
                    } else {
                        System.out.print(dataReader(filePointer, dataPointer, readDataLength));
                    }


                } else {
                    filePointer = ((DATA) blocks.get(filePointer)).FRWD;
                }

            }

        } else {
            System.out.println("System Warning | data read is not allowed for this file");


        }
    }

    public void writeData(int n, byte[] myData) {

        byte[] userData = new byte[n];

        //append space if n value is more than data size
        int remainingDataLength = n, nextDataPointer = 0, nextFreeBlock;
        if (myData.length < n) {

            for (int i = 0; i < n; i++) {
                if (i >= myData.length)
                    userData[i] = ' ';
                else
                    userData[i] = myData[i];
                // System.out.println("Data :" + userData[i]);
            }


        }


        if (writeFlag) {
            int availLengthInCurrBlock = maxDataSize - dataPointer;
            while (remainingDataLength > 0) {
                if (remainingDataLength > availLengthInCurrBlock) {
                    dataWriter(filePointer, dataPointer, maxDataSize, new String(userData).substring(nextDataPointer, nextDataPointer + availLengthInCurrBlock).getBytes());
                    System.out.print("Data Written In block " + filePointer + " | from " + dataPointer + " to " + maxDataSize + " and data remain :");
                    nextDataPointer += availLengthInCurrBlock;
                    dataPointer = 0;
                    remainingDataLength -= availLengthInCurrBlock;
                    availLengthInCurrBlock = maxDataSize;
                    System.out.println(remainingDataLength);
                    //System.out.println("Data Written In block "+filePointer+" | from "+dataPointer +" to "+ maxDataSize);
                } else {
                    DIR d = new DIR();
                    dataWriter(filePointer, dataPointer, (remainingDataLength + dataPointer), new String(userData).substring(nextDataPointer, nextDataPointer + remainingDataLength).getBytes());
                    System.out.print("Data Written In final block " + filePointer + " | from " + dataPointer + " to " + (remainingDataLength + dataPointer) + " remain ");
                    //dataPointer = remainingDataLength+dataPointer;
                    //nextDataPointer+=availLengthInCurrBlock;
                    d.SIZE = (short) remainingDataLength;
                    availLengthInCurrBlock = maxDataSize - dataPointer;
                    remainingDataLength -= (remainingDataLength + dataPointer);
                    System.out.println(remainingDataLength);

                    d.TYPE = 'A';

                    dirTableUpdate(currDirPointer, getStartingPointerOfFile(filePointer), d);

                }

                if (remainingDataLength > 0) {
                    if (((DATA) blocks.get(filePointer)).FRWD == 0) {
                        /**
                         * if no further linked block available then add data block
                         */
                        nextFreeBlock = getNextFreeBlock();

                        data = (DATA) blocks.get(filePointer);
                        data.FRWD = nextFreeBlock;
                        data = new DATA();
                        data.BACK = filePointer;
                        blocks.add(nextFreeBlock, data);
                        filePointer = nextFreeBlock;
                        System.out.println("Data Block Created " + filePointer);
                    } else {
                        /**
                         * if next data block available in current block
                         */
                        filePointer = ((DATA) blocks.get(filePointer)).FRWD;
                    }
                }

            }


        } else {
            System.out.println("System Warnign | writing is not allowed for this file!!!");
        }

    }

    public void seekData(byte base, short offset) {
        if (base == -1) {
            filePointer = getStartingPointerOfFile(filePointer);
            dataPointer = 0;
            while (offset > maxDataSize) {
                data = (DATA) blocks.get(filePointer);
                if (data.FRWD != 0)
                    filePointer = data.FRWD;
                else
                    break;
                offset -= maxDataSize;
            }
            dataPointer = offset;

        }
        if (base == 0) {
            dataPointer += offset;

        }
        if (base == 1) {
            int lastBSize = 0;
            int tempPointer = 0;
            while (((DATA) blocks.get(filePointer)).FRWD != 0) {
                tempPointer = filePointer;
                filePointer = ((DATA) blocks.get(filePointer)).FRWD;
            }
            filePointer=tempPointer;
            tempPointer = getStartingPointerOfFile(tempPointer);
            directory = (DIRECTORY) blocks.get(currDirPointer);
            for (DIR d : directory.DIRS) {
                if (d.LINK == tempPointer) {
                    lastBSize = d.SIZE;
                    break;
                }
            }
            dataPointer = lastBSize;
            dataPointer+=offset;
            System.out.println("Pointer "+ dataPointer +" FP "+filePointer);


        }

    }

    public int dataWriter(int blockNo, int startPosition, int endPosition, byte[] d) {

        /**
         * startposition is inclusive
         * endposition is exclusive
         * return sends the total no of data  bytes written in block
         */
        int dataIndex = 0;
        if (blocks.get(blockNo).getClass() == DATA.class && endPosition < 505) {//pud d.length()==end-start later addition
            data = (DATA) blocks.get(blockNo);
            for (int i = startPosition; i < endPosition; i++) {

                data.DATA[i] = d[dataIndex++];
                //System.out.println("I: " +i + " Index: " +(dataIndex-1)+" value "+(char)data.DATA[i]);

            }
            return endPosition;
        } else {
            return -1;
        }
    }

    public String dataReader(int fPointer, int startPosition, int endPosition) {
        if (blocks.get(fPointer).getClass() == DATA.class) {
            //System.out.println("P  "+ fPointer +" | S "+ startPosition +" | E "+ endPosition);
            String blockData = new String(((DATA) blocks.get(filePointer)).DATA);
            //System.out.println("DATA : " +blockData);
            return blockData.substring(startPosition, endPosition);
        } else {
            return "";
        }

    }


    public void dirTableEntry(int fPointer, DIR d) {


        ((DIRECTORY) blocks.get(fPointer)).DIRS.add(d);


    }

    public boolean dirTableUpdate(int currentDirPointer, int targetDirPoinger, DIR d) {
        int index = 0;
        directory = (DIRECTORY) blocks.get(currentDirPointer);
        for (DIR dir : directory.DIRS) {
            if (dir.LINK == targetDirPoinger) {
                if (new String(d.NAME).equals(new String(new DIR().NAME)))
                    d.NAME = dir.NAME;
                if (d.SIZE == 0)
                    d.SIZE = dir.SIZE;
                if (d.TYPE == 'A')
                    d.TYPE = dir.TYPE;
                d.LINK = targetDirPoinger;
                directory.DIRS.set(index, d);
                return true;
            }
            index++;
        }
        return false;
    }


    public int getNextFreeBlock() {
        int nextBlock = 0;
        DIRECTORY d = (DIRECTORY) blocks.get(0);
        nextBlock = d.FREE;
        d.FREE += 1;
        return nextBlock;
    }




    public int getFileBlockNobyName(byte type, byte[] name) {

        /**
         * Procedure to check directory existence
         * If directory or file not found then created by procedure.
         * noOfDirs counts possible directory in path(name)
         * fileName indicates file/directory is available in particular directory.
         **/

        //Root Directory
        // System.out.println(" trace "+new String(name));
        if (new String(name).equals(""))
            return 0;


        //System.out.println(new String(name));
        StringTokenizer filePathTokenizer = new StringTokenizer(new String(name), "/");
        String fileName = "";
        boolean fileExistFlag;
        int tempPointer = 0;

        int noOfDirs = filePathTokenizer.countTokens() - 1;
        //System.out.println("IN GET_FILE_BLOCK_BY_NAME | no of Dir in  "+new String(name)+" is " +noOfDirs );

        while (noOfDirs > 0) {
            fileName = filePathTokenizer.nextToken();
            //System.out.println("Trace : File Name "+ fileName);

            /**
             *Checking Directory or file exist or not
             * If Exist then move pointer to next directory or file
             **/
            fileExistFlag = false;

            DIRECTORY directory = ((DIRECTORY) blocks.get(tempPointer));
            for (DIR d : directory.DIRS) {
                //If directory Exist
                if ((new String(d.NAME)).equals(fileName)) {

                    if (d.TYPE == 'D') {
                        tempPointer = d.LINK;
                        fileExistFlag = true;

                    }
                }
            }


            /**
             *If Dir Directory Not exist in Table
             */

            if (!fileExistFlag) {

                /**
                 * If file not fount then it will return -1
                 **/
                //System.out.println("Trace  (getFileBlockByName) : " + " return -1"+" with flag");
                return -1;
            }

            noOfDirs--;
        }

        fileName = filePathTokenizer.nextToken();
        DIRECTORY directory = ((DIRECTORY) blocks.get(tempPointer));
        for (DIR d : directory.DIRS) {
            //If directory Exist
            if ((new String(d.NAME)).equals(fileName)) {
                if (type == 'A') {
                    return d.LINK;
                } else {
                    if (type == d.TYPE) {
                        return d.LINK;
                    } else {
                        return -1;
                    }

                }

            }
        }
        //System.out.println("Trace  (getFileBlockByName) : " + " return -1"+" final");
        return -1;

    }

    public String getCurrDirPath(byte[] name) {
        String currentDirPath = "", nameString = new String(name);

        try {
            currentDirPath = nameString.substring(0, nameString.lastIndexOf('/'));
        } catch (IndexOutOfBoundsException iob) {
            currentDirPath = "";
        }
        return currentDirPath;
    }

    public int getStartingPointerOfFile(int filePointer) {
        int targetPointer = filePointer;
        if (blocks.get(filePointer).getClass() == DATA.class) {
            data = (DATA) blocks.get(filePointer);
            if (data.BACK == 0)
                return targetPointer;
            else {
                do {
                    targetPointer = data.BACK;
                    data = (DATA) blocks.get(data.BACK);
                } while (data.BACK != 0);
                return targetPointer;

            }
        }
        if (blocks.get(filePointer).getClass() == DIRECTORY.class) {
            directory = (DIRECTORY) blocks.get(filePointer);
            if (directory.BACK == 0)
                return filePointer;
            else {
                do {
                    targetPointer = directory.BACK;
                    directory = (DIRECTORY) blocks.get(directory.BACK);
                } while (directory.BACK != 0);
                return targetPointer;

            }
        }
        return 0;
    }

}



