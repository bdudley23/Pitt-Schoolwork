/************************************************************************
**  AUTHOR:         Brent Dudley (btd27)
**  CLASS:          CS1501, M/W 3:00-4:15PM
**  PROFESSOR:      Dr. Khattab
**  ASSIGNMENT 4:   Primitive SecureChatServer/Client with RSA Encryption
************************************************************************/

import java.util.ArrayList;
import java.util.Collections;

/*
**  Substitute cipher implements interface SymCipher
**
**  Contents:
**  1)  2 Constructors, one with/one without parameters
**          -With parameters takes a byte array
**          -Without creates random 256 byte array (permutation of 256 possible byte values), will serve as a map
**              from bytes to their substitution values
**          **NOTE: Will need an inverse mapping array for this cipher
**          **NOTE: Will need to handle negative value bytes
**  2)  Encode() - convert string parameter to array of bytes, then substitute appropriate bytes from the key
**          -Returns array of bytes
**          **NOTE: Handle negative bytes
**  3)  Decode() - reverse the substitution using decode byte array, convert resulting bytes back to String
**          -Returns string
**  4) toUnsignedInt() - And's a given byte with 0xFF to ensure we don't receive any negatively signed bytes
*/

public class Substitute implements SymCipher {
    
    private final byte[] key;
    
    public Substitute() {
        // Create 256 byte collection
        ArrayList<Byte> temp = new ArrayList<Byte>();
        for (int i = 0; i < 256; i++) {
            temp.add(new Byte((byte) i));
        }
        
        // Randomize the collection and set to key
        Collections.shuffle(temp);
        key = new byte[256];
        for (int i = 0; i < 256; i++) {
            key[i] = temp.get(i);
        }
    }
    
    public Substitute (byte[] bytes) {
        // Check bytes length is 256
        assert (bytes.length == 256);
        
        // Copy bytes to key
        key = new byte[256];
        System.arraycopy(bytes, 0, key, 0, 256);
    }
    
    public byte[] getKey() {
        return key;
    }
    
    public byte[] encode(String s) {
        byte[] strBytes = s.getBytes();
        byte[] coded = new byte[strBytes.length];
        for (int i = 0; i < strBytes.length; i++) {
            coded[i] = key[toUnsignedInt(strBytes[i])];
        }
        return coded;
    }
    
    public String decode(byte[] bytes) {
        byte[] msgBytes = new byte[bytes.length];
        
        // Long search with nested for's
        for (int i = 0; i < bytes.length; i++) {
            
            // asciiNum initialized to 0, will be the decoded byte
            int asciiNum = 0;
            
            // Find substitution in key
            for (int j = 0; j < key.length; j++) {
                if (key[j] == bytes[i]) {
                    asciiNum = j;
                    break;
                }
            }
            msgBytes[i] = (byte)asciiNum;
        }
        return new String(msgBytes);
    }
    
    // Quick private helper method to ensure we don't receive any negative signed bytes
    private int toUnsignedInt(byte b) {
        return (b & 0xFF);
    }
    
}