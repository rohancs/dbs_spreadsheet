import com.rohancs.tools.spreadsheet.Cell;
import com.rohancs.tools.spreadsheet.Spreadsheet;
import jdk.nashorn.internal.runtime.options.Options;

import java.io.File;
import java.util.Map;

/**
 * Created by Rohan on 9/14/18.
 */
public class Main {
    public static void main(String[] args) throws Exception{
        //Scan Input
        File f = getInputFile(args);
        if(f==null) System.exit(-1);

        //Parse & Process Spreadsheet
        Spreadsheet spreadsheet = new Spreadsheet(f);
        spreadsheet.process();

        //Write Output
        String outfile = getOutputFileName(args);
        spreadsheet.saveFile(outfile);
    }

    private static File getInputFile(String[] args) {
        boolean exception = false;
        String inputFile = getInputFileName(args);

        if(inputFile != null) {
            File f = new File(inputFile);

            if(f.exists() && !f.isDirectory())
                return f;
            else
                exception = true;
        }
        else
            exception = true;

        if(exception)
            throw new RuntimeException("Input file not found: "+inputFile);

        return null;
    }

    private static String getInputFileName(String[] args) {
        String result = null;

        for(int i=0;i<args.length -1; i++) {
            if(args[i].equalsIgnoreCase("-i")) {
                result = args[i+1];
                break;
            }
        }

        return result;
    }

    private static String getOutputFileName(String[] args) {
        String result = null;

        for(int i=0;i<args.length -1; i++) {
            if(args[i].equalsIgnoreCase("-o")) {
                result = args[i+1];
                break;
            }
        }

        if(result == null)
            result = System.getProperty("user.home") + File.separator + "output.csv";

        return result;
    }
}
