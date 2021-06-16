/*********************************
* ASSIGNMENT 1, TASK 1
* Author: Brent Dudley
* Username: btd27
* Class: CS1501/Khattab/MW3-4:15PM
**********************************/

import java.util.*;
import java.io.*;


public class CrosswordB {
    
    public static DictInterface dictionary;
    public static int boardSize;
    public static char[][] board;
    public static int numSolution;
    public static boolean spaceAvail = false;
    public static char[] alphabet = "abcdefghijklmnopqrstuvwxyz".toCharArray();
    public static StringBuilder[] colStr;
    public static StringBuilder[] rowStr;

    
    public static void main(String[] args) throws IOException {
        // Read in given dictionary file
        Scanner inputFile = new Scanner(new FileInputStream("dict8.txt")); 
        
        // Determine user input for DLB vs MyDictionary
        try {
            if (args[1].equals("dlb")) {
                dictionary = new DLB();
            } else {  
                dictionary = new MyDictionary();
            }
        } catch(IndexOutOfBoundsException e) {
            System.out.println("Invalid dictInterface argument");
            System.out.println("For DLB, try: java CrosswordB <test.txt> dlb");
            System.out.println("For MyDictionary, try: java CrosswordB <test.txt> mydict");
            System.exit(0);
        }

        File inputBoard = new File(args[0]);
        Scanner fileScan = new Scanner(inputBoard);
        boardSize = fileScan.nextInt();

        colStr = new StringBuilder [boardSize];
        rowStr = new StringBuilder [boardSize];

        for (int i = 0; i < boardSize; i++) {
            rowStr[i]= new StringBuilder("");
            colStr[i] = new StringBuilder("");
        }

        board = new char[boardSize][boardSize];

        //reads in from file and creates gameboard
        for (int row = 0; row < boardSize; row++) {
            String ch = fileScan.next();
            for (int col = 0; col < boardSize; col++) {
                board [row][col]= ch.charAt(col); 
            }
        }

        String word;
        //store everything in the inputfile to the DictInterface object D
        while (inputFile.hasNext()) {
            word = inputFile.nextLine();
            dictionary.add(word);
        }   

        if (args[1].equals("dlb")) {
            recurseMany(0);
            System.out.println("Number solutions: " + numSolution);
        } else {
            recurseOne(0);
            for (int i = 0; i < boardSize; i++) {
                System.out.println(rowStr[i]);
            }
        }

    }  // END MAIN
        
    // Method checks for valid word/prefix at given row/column
    public static boolean wordPrefix(int row, int col) {
        int colSP = dictionary.searchPrefix(colStr[col]);
        int rowSP = dictionary.searchPrefix(rowStr[row]);  

        if (    row<boardSize-1 && 
                col<boardSize-1 && 
                (colSP == 1 || colSP == 3) && 
                (rowSP == 1 || rowSP ==3) ) { return true; }

        if (    col == boardSize-1 && 
                row<boardSize-1 && 
                (colSP == 1 || colSP ==3) && 
                (rowSP == 2 || rowSP == 3) ) { return true; }

        if (    row == boardSize-1 && 
                col<boardSize-1 && 
                (colSP == 2 || colSP == 3) && 
                (rowSP == 1 || rowSP == 3) ) { return true; }

        if (    row == boardSize-1 && 
                col == boardSize-1 && 
                (colSP == 2 || colSP == 3) && 
                (rowSP == 2 || rowSP == 3) ) { return true; }
        else
            return false;
    } // END WORDPREFIX

    // Recursive method to find multiple solutions
    // Iterates through alphabet array
    public static void recurseMany(int pos) {
        int row = pos / boardSize;
        int col = pos % boardSize;

        //if a solution is found 
        if (    pos == (boardSize * boardSize) || 
                (colStr[boardSize - 1].length() == boardSize && 
                rowStr[boardSize - 1].length() == boardSize)) { return; }        
        else {
            //checks whether the spot is a + or not, if so continue through
            if (isAvailable(row, col)) {                   		
                for (int letVal = 0; letVal < alphabet.length; letVal++) {
                    row = pos / boardSize;
                    col = pos % boardSize;
                    colStr[col].append(alphabet[letVal]);                        
                    rowStr[row].append(alphabet[letVal]);

                    // If valid
                    if (wordPrefix(row, col)) {                       
                        recurseMany(pos + 1);   
                        
                        // Allows for multiple solutions to be found
                        if (    colStr[boardSize - 1].length() == boardSize && 
                                rowStr[boardSize - 1].length() == boardSize) {

                            numSolution++;
                            
                            
                            // Prints out first solution found before rest are found
                            if (    numSolution == 1 && 
                                    (rowStr[boardSize-1].length() == boardSize && 
                                    colStr[boardSize-1].length()==boardSize)) {

                                    System.out.println("First Solution is");
                                    for (int i = 0; i < boardSize; i++) {
                                        System.out.println(rowStr[i]);
                                    }
                            }
                            /*
                            // Commented out, left in to prove to me that the program was still running
                            // and my command prompt wasn't just frozen.
                            // Prints every 5000th solution
                            else if (numSolution % 5000 == 0) {
                                System.out.println("\n" + "Solution Number: " + numSolution);    
                                for (int i = 0; i < boardSize; i++) {
                                    System.out.println(rowStr[i]);
                                }
                            }
                            */
                            
                        }
                        colStr[col].deleteCharAt(row);
                        rowStr[row].deleteCharAt(col);
                        
                     // Else delete char, check next letter
                    } else {
                        colStr[col].deleteCharAt(row);
                        rowStr[row].deleteCharAt(col);
                    }
                }
                
            // If spot not available, add letter and go to next position
            }  else {
                colStr[col].append(board[row][col]);
                rowStr[row].append(board[row][col]);   
                if (wordPrefix(row, col)) {
                    recurseMany(pos + 1);
                    colStr[col].deleteCharAt(row);
                    rowStr[row].deleteCharAt(col);
                } else {                                        
                    colStr[col].deleteCharAt(row);
                    rowStr[row].deleteCharAt(col);
                }                   
            }
        }
    }

    // Recursive method to print out a single (the first) solution
    public static void recurseOne(int pos) {
        int row = pos / boardSize;
        int col = pos % boardSize;

        if (    pos == (boardSize * boardSize) || 
                (colStr[boardSize - 1].length() == boardSize && 
                rowStr[boardSize - 1].length() == boardSize)) {
            return;
        } else {
            //checks whether the spot is a + or not, if so continue through
            if (isAvailable(row, col)) {                   		
                for (int letVal = 0; letVal < alphabet.length; letVal++) {
                    row = pos / boardSize;
                    col = pos % boardSize;
                    colStr[col].append(alphabet[letVal]);                        
                    rowStr[row].append(alphabet[letVal]);
                    
                    // If valid
                    if (wordPrefix(row, col)) {                       
                        recurseOne(pos + 1);            
                        if ((colStr[boardSize - 1].length() == boardSize && 
                             rowStr[boardSize - 1].length() == boardSize)) {
                            return;
                        }
                        colStr[col].deleteCharAt(row);
                        rowStr[row].deleteCharAt(col);
                        
                    // Otherwise, delete char, try next char
                    } else {
                        colStr[col].deleteCharAt(row);
                        rowStr[row].deleteCharAt(col);
                    }
                }
            // If spot not available, add letter and move to next position
            } else {
                colStr[col].append(board[row][col]);
                rowStr[row].append(board[row][col]);  
                
                if (wordPrefix(row, col)) {
                    recurseOne(pos + 1);
                    if ((colStr[boardSize - 1].length() == boardSize && 
                         rowStr[boardSize - 1].length() == boardSize)) {
                        return;
                    }
                    colStr[col].deleteCharAt(row);
                    rowStr[row].deleteCharAt(col);
                } else {                                        
                    colStr[col].deleteCharAt(row);
                    rowStr[row].deleteCharAt(col);
                }                   
            }  
        }
    } // END RECURSEONE

    // Method checks availability for spot on the board
    public static boolean isAvailable(int row, int col) {    
        if (board[row][col] == '+') {
            spaceAvail = true;             
        } else {
            spaceAvail = false;                
        }
        return spaceAvail;
    } // END ISAVAILABLE
    
} // END CROSSWORD