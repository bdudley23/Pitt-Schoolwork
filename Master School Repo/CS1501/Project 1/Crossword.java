/*********************************
* ASSIGNMENT 1, TASK 1
* Author: Brent Dudley
* Username: btd27
* Class: CS1501/Khattab/MW3-4:15PM
**********************************/

import java.util.*;
import java.io.*;


public class Crossword {
    
    public static DictInterface dictionary;
    public static int boardSize;
    public static char[][] board;
    public static int numSolution;
    public static boolean spaceAvail = false;
    public static char[] alphabet = "abcdefghijklmnopqrstuvwxyz".toCharArray();
    public static StringBuilder[] colStr;
    public static StringBuilder[] rowStr;

    
    public static void main(String[] args) throws IOException {
        // New line for neatness
        System.out.println("");
        
        // Determine user input for DLB vs MyDictionary
        try {
            dictionary = new MyDictionary();
        } catch(IndexOutOfBoundsException e) {
            System.out.println("Invalid dictInterface argument");
            System.out.println("For MyDictionary, try: java CrosswordB <test.txt>");
            System.exit(0);
        }
        
        // Read the board being solved
        File inputBoard = new File(args[0]);
        Scanner fileScan = new Scanner(inputBoard);
        boardSize = fileScan.nextInt();
        
        // Initialize StringBuilder arrays/StringBuilders
        colStr = new StringBuilder [boardSize];
        rowStr = new StringBuilder [boardSize];
        for (int i = 0; i < boardSize; i++) {
            rowStr[i]= new StringBuilder("");
            colStr[i] = new StringBuilder("");
        }
        
        // Create the board to solve
        board = new char[boardSize][boardSize];
        for (int row = 0; row < boardSize; row++) {
            String ch = fileScan.next();
            for (int col = 0; col < boardSize; col++) {
                board[row][col]= ch.charAt(col); 
            }
        }
        
        // Read in given dictionary file
        Scanner inputFile = new Scanner(new FileInputStream("dict8.txt")); 
        String word;
        while (inputFile.hasNext()) {
            word = inputFile.nextLine();
            dictionary.add(word);
        }   

        // Show the board before it's solved
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                System.out.print(board[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println("");
        System.out.println("");
        
        recurseOne(0);
        for (int i = 0; i < boardSize; i++) {
            System.out.println(rowStr[i]);
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
