/**
 ** Name: Brent Dudley (btd27)
 ** Java Version: JDK-11
 ** CS1501 - Dr. Khattab
 ** Assignment 3
 **/

/***************************************************************
 **  Compress:       java LZWMod - r/n < input.txt > input.lzw
 **  Expand:         java LZWMod + < input.lzw > input.txt
 ***************************************************************/

import java.util.ArrayList;

public class LZWmod {
    private static final int R = 256;           // number of input chars
    private static int L = 512;                 // number of codewords = 2^W
    private static int W = 9;                   // codeword width
    private static int MAX;
    private static boolean reset;
    
    public static void main(String[] args) {
        MAX = (int) Math.pow(2, 16) + 1;
        if (args[0].equals("-")) {
            if (args[1].equals("r")) {
                reset = true;
                BinaryStdOut.write('1');
            } else{
                BinaryStdOut.write('0');
                reset = false;
            }
            compress();
        } else if (args[0].equals("+")){
            char c = BinaryStdIn.readChar();
            if (c == '1') {
                reset = true;
            } else {
                reset = false;
            }
            expand();
        }
        else throw new RuntimeException("Illegal command line argument");
    }
    
        //Reads in the next 8 bits.
    public static StringBuilder readByte() {
        StringBuilder readIn = new StringBuilder();

        try {
            readIn.append( (char) BinaryStdIn.readChar());
        } catch (Exception e) {  
            readIn = null;
        }

        return readIn;
    }

}

    public static void compress() { 
        StringBuilder input;
        TSTmod<Integer> tst = new TSTmod<Integer>();          // Code Book
        StringBuilder next = new StringBuilder();
        
        //Add ASCII character set to dictionary
        for (int i = 0; i < R; i++){
            tst.put(new StringBuilder().append((char) i), i);
        }
        
        int code = R+1;  // R is codeword for EOF

        input = readByte();

        while (input != null ) {
            StringBuilder sb = tst.longestPrefixOf(input);  // Find max prefix match s.

            // Keeps adding bytes until it does not match a codeword
            while (sb.length() == input.length()) {
                next = readByte();
                input.append(next);
                sb = tst.longestPrefixOf(input);
            }
            BinaryStdOut.write(tst.get(sb), W);      // Write s's code

            if (next != null && code < L) {    // Add s to symbol table.
                tst.put(new StringBuilder(input.substring(0, sb.length() + 1)), code++);
                if (code == L) {
                    if (W < 16) {
                        W++;
                        L = (int) Math.pow(2, W);
                    } else if (W == 16 && reset) {
                        System.err.println("Resetting dictionary...");
                        //reset the dictionary
                        tst = new TSTmod<Integer>();
                        //re-add ASCII charaters
                        for (int i = 0; i < R; i++){
                            tst.put(new StringBuilder().append((char) i), i);
                        }
                        code = R + 1;
                        W = 9;
                        L = 512;
                    }
                }
            }
            input = next;
        }
        BinaryStdOut.write(R, W);
        BinaryStdOut.close();
    } 

    public static void expand() {
        String[] tst = new String[MAX];
        int i;                                  // next available codeword value

        // initialize symbol table with all 1-character strings
        for (i = 0; i < R; i++) {
            tst[i] = "" + (char) i;
        }
        tst[i++] = "";

        int codeword = BinaryStdIn.readInt(W);
        String val = tst[codeword];

        while (true) {
            BinaryStdOut.write(val);

            codeword = BinaryStdIn.readInt(W);
            if (codeword == R) {
                break;
            }
            String str;
            if (i == codeword) {
                str = val + val.charAt(0);
            } else {
                str = tst[codeword];
            }
            if (i < L) {
                tst[i++] = val + str.charAt(0);
            }
            val = str;
            if (i + 1 == L && W < 16) {
                W++;
                L = (int) Math.pow(2, W);
            }

            if (i + 1 == L) {
                tst[i++] = val + str.charAt(0);
                if (reset) {
                    BinaryStdOut.write(val);
                    System.err.println("Resetting the dictionary...");
                    tst = new String[MAX];

                    // initialize symbol table with all 1-character strings
                    for (i = 0; i < R; i++) {
                        tst[i] = "" + (char) i;
                    }
                    tst[i++] = "";

                    W = 9;
                    L = 512;
                    codeword = BinaryStdIn.readInt(W);
                    val = tst[codeword];
                } else {
                    BinaryStdOut.write(val);
                    codeword = BinaryStdIn.readInt(W);
                    while (codeword != R) {
                        BinaryStdOut.write(tst[codeword]);
                        codeword = BinaryStdIn.readInt(W);
                    }
                    break;
                }
            }
        }
        BinaryStdOut.close();
    }