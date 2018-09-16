package com.rohancs.tools.spreadsheet;

import javax.script.ScriptEngine;
import java.util.*;

/**
 * Created by Rohan on 9/14/18.
 */
public class Cell {
    String name,text;
    int row,col;
    Double value;
    List<String> dependencies;
    boolean isResolved=false;

    Cell(Integer cellRow,Integer cellCol, String cellText) {
        String rowName = Utility.getColumnName(cellRow);

        this.name = rowName + cellCol;
        this.row = cellRow;
        this.col = cellCol;
        this.text = cellText.toUpperCase();

        try{
            value = Double.parseDouble(cellText);
            isResolved = true;
        }
        catch (Exception e) {
            if(this.text.startsWith("="))
                parseExpression(getText());
            else
                throw new RuntimeException("Unable to parse cell "+getName()+" with value "+getText());
        }
    }

    private void parseExpression(String expr) {
        isResolved = false;
        dependencies = new ArrayList<>();

        String temp = expr.substring(1)
                            .replace("+"," ")
                            .replace("-"," ")
                            .replace("*"," ")
                            .replace("/"," ")
                            .replace("("," ")
                            .replace(")"," ");

        String[] tokens = temp.split(" ");

        for(String s: tokens) {
            try {
                Integer val = Integer.parseInt(s);
            }
            catch (Exception e) {
                if(!s.equalsIgnoreCase(""))
                    dependencies.add(s);
            }
        }

        /*
        Sort dependencies in decending order of number of characters
        This is to ensure when we replace data with cell name, we replace bigger tokens first to avoid errors.
        Eg: AA0 + A0 ==> should replace AA0 with data and then A0 with data. Reverse would corrupt the string.
        */
        Collections.sort(dependencies, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                if(o1.length() < o2.length()) return  -1;
                else if(o1.length() > o2.length()) return 1;

                return 0;
            }
        });

    }

    boolean evaluateExpression(final Map<String, Cell> data, ScriptEngine engine) {
        //Replace cell references with data
        String expr = this.text.substring(1);

        for(String s: dependencies)
            expr = expr.replace(s,data.get(s).getValue().toString());

        try{
            value = (Double) engine.eval(expr);
        }
        catch (Exception e){
            value = null;
        }

        isResolved = true;

        return isResolved;
    }

    public List<String> getDependencies() {return dependencies;}

    public Double getValue() {
        if(isResolved)
            return value;
        else
            return null;
    }

    public String getName() {return name;}

    int getRow() {return row;}
    int getCol() {return col;}

    public String getText() {return text;}
}
