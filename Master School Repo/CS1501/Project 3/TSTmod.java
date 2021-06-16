/**
 ** Name: Brent Dudley (btd27)
 ** Java Version: JDK-11
 ** CS1501 - Dr. Khattab
 ** Assignment 3
 ** Mod TST to use StringBuilder
 **/

public class TSTmod<Value> {
    private int N;                      // size
    private Node root;

    private class Node {
        private char c;
        private Node left, mid, right;
        private Value val;
    }

    public int size() {
        return N;
    }

    public boolean contains(StringBuilder key) {
        return get(key) != null;
    }

    public Value get(StringBuilder key) {
        if (key == null || key.length() == 0) {
            throw new RuntimeException("illegal key");
        }
        Node x = get(root, key, 0);
        if (x == null) {
            return null;
        }
        return (Value) x.val;
    }

    private Node get(Node x, StringBuilder key, int d) {
        if (key == null || key.length() == 0) {
            throw new RuntimeException("illegal key");
        }
        if (x == null) {
            return null;
        }
        char c = key.charAt(d);
        if (c < x.c) {
            return get(x.left,  key, d);
        } else if (c > x.c) {
            return get(x.right, key, d);
        } else if (d < key.length() - 1) {
            return get(x.mid,   key, d+1);
        } else {
            return x;
        }
    }

    public void put(StringBuilder s, Value val) {
        if (!contains(s)) N++;
        root = put(root, s, val, 0);
    }

    private Node put(Node x, StringBuilder s, Value val, int d) {
        char c = s.charAt(d);
        if (x == null) {
            x = new Node();
            x.c = c;
        }
        if (c < x.c) {
            x.left  = put(x.left,  s, val, d);
        } else if (c > x.c) {
            x.right = put(x.right, s, val, d);
        } else if (d < s.length() - 1) {
            x.mid   = put(x.mid,   s, val, d+1);
        } else {
            x.val = val;
        }
        return x;
    }

    public StringBuilder longestPrefixOf(StringBuilder s) {
        if (s == null || s.length() == 0) {
            return null;
        }
        int length = 0;
        Node x = root;
        int i = 0;
        while (x != null && i < s.length()) {
            char c = s.charAt(i);
            if (c < x.c) {
                x = x.left;
            } else if (c > x.c) {
                x = x.right;
            } else {
                i++;
                if (x.val != null) {
                    length = i;
                }
                x = x.mid;
            }
        }
        return new StringBuilder(s.substring(0, length));
    }

}