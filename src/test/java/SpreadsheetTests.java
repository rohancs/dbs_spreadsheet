import org.junit.Before;
import org.junit.Test;

import java.io.File;

public class SpreadsheetTests {
    public String[] getArgs(String testFile) {
        ClassLoader classLoader = getClass().getClassLoader();

        //Input
        File file = new File(classLoader.getResource(testFile).getFile());
        String inputFilename = file.getAbsolutePath();

        //Output
        file = new File(classLoader.getResource("output_"+testFile).getFile());
        String outputFileName = file.getAbsolutePath();

        String[] args = {"-i",inputFilename,"-o",outputFileName};

        return args;
    }

    @Test
    public void Test1() {
        //Sample Input in Problem Statement
        try {
            String[] args = getArgs("test1.csv");
            Main.main(args);
        }
        catch (Exception e){
            assert(false);
            throw new RuntimeException(e);
        }
    }

    @Test
    public void Test2() {
        //Bigger and complex Spreadsheet Input
        try {
            String[] args = getArgs("test2.csv");
            Main.main(args);
        }
        catch (Exception e){
            assert(false);
            throw new RuntimeException(e);
        }
    }

    @Test
    public void Test3() {
        //Invalid Cell Reference
        try {
            String[] args = getArgs("test3.csv");
            Main.main(args);
            assert (false);
        }
        catch (Exception e){
            System.out.println("Test3 ~ Invalid Cell Reference :"+e.getMessage());
            assert(true);
        }
    }

    @Test
    public void Test4() {
        //Cyclic Dependency
        try {
            String[] args = getArgs("test4.csv");
            Main.main(args);
            assert (false);
        }
        catch (Exception e){
            System.out.println("Test4 ~ Cyclic Dependency :"+e.getMessage());
            assert(true);
        }
    }

    @Test
    public void Test5() {
        //Blank Input
        try {
            String[] args = getArgs("test5.csv");
            Main.main(args);
            assert (true);
        }
        catch (Exception e){
            assert(false);
            throw new RuntimeException(e);
        }
    }
}
