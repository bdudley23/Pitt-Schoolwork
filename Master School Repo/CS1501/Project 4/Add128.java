/************************************************************************
**  AUTHOR:         Brent Dudley (btd27)
**  CLASS:          CS1501, M/W 3:00-4:15PM
**  PROFESSOR:      Dr. Khattab
**  ASSIGNMENT 4:   Primitive SecureChatServer/Client with RSA Encryption
************************************************************************/

import java.util.Random;

/*
**  Add128 cipher implements SymCipher
**
**  Contents:
**  1)  2 Constructors: one with/one without parameters
**          -With parameters takes a byte array, use byte array as key
**          -Without parameters create random 128 byte additive key, store as array of bytes
**          **NOTE: WITH called by Server, WITHOUT called by Client
**  2)  Encode() - convert String to array of bytes, add corresponding bytes of key to corresponding bytes of index
**           in String's array of bytes
**          **Note: If message shorter than key, ignore remaining bytes
**                  If message longer than key, cycle through key as many times as necessary
**          -Return array of bytes
**  3)  Decode() - subtract corresponding byte of key from corresponding index in array of bytes
**          **Note: If message shorter than key, ignore remaining bytes
**                  If message longer than key, cycle through key as many times as necessary
**          -Convert resulting byte array to string
**          -Return String
*/

public class Add128 implements SymCipher {
    
    private final byte[] key;
    
    public Add128() {
        Random randByte = new Random();
        key = new byte[128];
        
        // Fill key with random bytes
        randByte.nextBytes(key);
    }
    
    public Add128(byte[] bytes) {
        // Check bytes length is 128
        assert (bytes.length == 128);
        
        // Copy bytes to key
        key = new byte[128];
        System.arraycopy(bytes, 0, key, 0, 128);
    }
    
    public byte[] getKey() {
        return key;
    }
    
    public byte[] encode(String s) {
        byte[] coded = s.getBytes();
        for (int i = 0; i < coded.length; i++) {
            // To "wrap": add key byte at char index, mod key length
            coded[i] += key[i % key.length];
        }
        return coded;
    }
    
    public String decode(byte[] bytes) {
        byte[] msgBytes = new byte[bytes.length];
        System.arraycopy(bytes, 0, msgBytes, 0, bytes.length);
        for (int i = 0; i < msgBytes.length; i++) {
            // To "wrap": Subtract byte at byte index, mod key length
            msgBytes[i] -= key[i % key.length];
        }
        return new String(msgBytes);
    }
    
}