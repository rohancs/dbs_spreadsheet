# Spreadsheet Resolver

This application takes a spreadsheet in CSV format as input and returns the spreadsheet with cells resolved in CSV format.

For	example:<br /> 
The	following CSV input:<br />
2,4,1,=A0+A1*A2<br />
=A3*(A0+1),=B2,0,=A0+1

Produces output	file:<br />
2.00000,4.00000,1.00000,6.00000<br />
18.00000,0.00000,0.00000,3.00000

####<u>Classes:</u> 

#####Spreadsheet<br />
Encapsulates spreadsheet cells and maintains cell dependencies. It also exposes methods to "process"
spreadsheet i.e. resolve cell dependencies and calculate values of expressions.

*Data Structures:*
* Data: 
    * Resolved Cells: Cells with resolved values
    * Unresolved Cells: Cells with expressions i.e. unresolved values

* Dependencies:
    * *Cell Dependency:* <br/>
        Map of cells with expressions and List of Cells they depend on. This is used to resolve value of a cell.
    * *Reverse Cell Dependency:*<br />
        Map of Cells that are referenced by other Cells. This is used to detect other potential cells that can be resolved
    
* Expression Resolution Engine:<br />
    Using Javascript script engine to evaluate expressions to get values

* Processing Queue:<br />
    This queue is used to keep track of cells that can potentially be resolved to values


#####Cell<br />
Encapsulates cell level data & operations like:
* Parsing of cell text
* Compute cell dependencies
* Evaluate cell value using resolution engine


####System Design
1. *Spreadsheet Parsing*: Parse input file and create "Cell" objects
2. *Cell Parsing*: Each Cell object computes cell dependencies and classifies it as "resolved" or "unresolved"
3. *Spreadsheet State*: The spreadsheet maintains cells in resolved/unresolved maps and maintains cell dependencies in two data structures.
4. *Process Spreadsheet*: 
    * *Bootstrap:* Iterate over unresolved cells and the cell's dependencies to see if any cell (X) can be resolved : 
        * if Yes -- then resolve X & add X to processing queue. Move cell from Unresolved to Resolved Map.
        * If No -- Then check next unresolved cell
    * *Process Queue:* Loop over processing queue until empty:
        * If resolvable then resolve & add to queue
    * At the end of loop:
        * Unresolved Map should be empty, if not then return exception
        * If Unresolved Map is empty -- then processing is complete

####Test Cases

1. Sample Input (Coding Challenge Example)
2. Sample Input (Base Case)
3. Error Case: Referencing invalid cell
4. Error Case: Cyclic dependency
5. Error Case: Blank Input

####Build Steps

./gradlew clean build jar

Jar Location: ~/build/libs/spreadsheet.jar

####Execution Steps

java -jar ~/build/libs/spreadsheet.jar -i InputFile -o OutputFile