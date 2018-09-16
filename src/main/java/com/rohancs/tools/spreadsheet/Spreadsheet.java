package com.rohancs.tools.spreadsheet;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.*;

/**
 * Created by Rohan on 9/14/18.
 * This class represents a Spreadsheet
 *
 * Internal Data Structures:
 * =========================
 * Data : (A) Resolved Cells (B) Unresolved Cells
 *
 * Cell Dependencies:
 *          Cell(X) ==> Cell(Y,Z)
 *        (A) Cell Dependency : Value of Cell X depends on value of cell Y,Z
 *        (B) Reverse Cell Dependency: Y can unlock X & Z can unlock X
 *
 * Processing Queue:
 *      Using cell dependencies, identify cells that can be potentially resolved and then process them.
 */
public class Spreadsheet {
    //Status
    boolean processed = false;

    //Evaluation Engine
    ScriptEngineManager mgr = new ScriptEngineManager();
    ScriptEngine engine = mgr.getEngineByName("JavaScript");

    //Keep track of cell dependency and sheet dependency
    Map<String,List<String>> cellDependency;
    Map<String,List<String>> reverseCellDependency;

    //Keep queue of cells that can unlock other cells
    Queue<String> processingQueue;

    //Keep track of data
    Map<String,Cell> unresolvedData;
    Map<String,Cell> resolvedData;

    public Spreadsheet(final File input) throws Exception {
        //Init
        cellDependency = new HashMap<>();
        reverseCellDependency = new HashMap<>();

        unresolvedData = new HashMap<>();
        resolvedData = new TreeMap<>();

        parse(input);
    }

    private void parse(File input) throws Exception{
        FileReader fileReader = new FileReader(input);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        int row = 1;

        String line;
        while ((line = bufferedReader.readLine()) != null) {
            String[] cells = line.split(",");

            Integer col = 0;
            for(String cell: cells){
                //Parse cell
                Cell newCell = new Cell(row,col,cell);

                if(newCell.isResolved)
                    resolvedData.put(newCell.getName(), newCell);
                else {
                    List<String> dependencies = newCell.getDependencies();
                    cellDependency.put(newCell.getName(),dependencies);

                    for(String dep: dependencies) {
                        if(!reverseCellDependency.containsKey(dep))
                            reverseCellDependency.put(dep,new ArrayList<>());

                        reverseCellDependency.get(dep).add(newCell.getName());
                    }

                    unresolvedData.put(newCell.getName(),newCell);
                }

                col++;
            }
            row ++;
        }
        fileReader.close();
    }

    public void process() throws Exception {
        //Initialize Queue
        processingQueue = new LinkedList<>();

        for(String s: reverseCellDependency.keySet()) {
            if (resolvedData.containsKey(s))
                processingQueue.add(s);
        }

        //Process work queue
        while(processingQueue.size() > 0) {
            String resolvedCell = processingQueue.remove();

            //Check if dependent cells can be resolved
            for(String cell: reverseCellDependency.get(resolvedCell)) {

                //skip if cell already resolved
                if(resolvedData.containsKey(cell))
                    continue;

                //Check if cell can be resolved
                boolean resolvable = true;

                for(String dep: cellDependency.get(cell)) {
                    if(!resolvedData.containsKey(dep)) {
                        resolvable = false;
                        break;
                    }
                }

                if(resolvable) {
                    //Resolve Cell
                    Cell temp = unresolvedData.get(cell);
                    temp.evaluateExpression(resolvedData, engine);

                    //Move Cell
                    resolvedData.put(temp.getName(),temp);
                    unresolvedData.remove(cell);

                    //Add to Process Queue
                    if(reverseCellDependency.containsKey(cell))
                        processingQueue.add(cell);
                }
            }

        }

        processed = true;

        if(unresolvedData.size() > 0) {
            StringBuffer buf = new StringBuffer();
            for(String s: unresolvedData.keySet())
                buf.append(","+s);

            throw new RuntimeException("Unable to resolve cells: "+buf.substring(1));
        }
    }

    public Map<String,Cell> getData() { return resolvedData; }

    public void saveFile(String outputFileName) throws Exception {
        try {
            if(!processed) process();

            PrintWriter writer = new PrintWriter(outputFileName, "UTF-8");

            int currentRow = 1;
            boolean newline = true;
            for(String cell: getData().keySet()) {
                Cell temp = getData().get(cell);

                //Check if cell is new Row
                if(temp.getRow() > currentRow) {
                    newline = true;
                    currentRow = temp.getRow();
                }

                //Write Output
                //First Cell of Document
                if(newline && currentRow == 1) {
                    writer.print(temp.getValue());
                    newline = false;
                }
                //New Line Cell
                else if(newline) {
                    writer.print("\n"+temp.getValue());
                    newline = false;
                }
                //New Cell on same line
                else {
                    writer.print(","+temp.getValue());
                }
            }

            writer.close();
        }
        catch (Exception e) {
            throw e;
        }
    }
}
