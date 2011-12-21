package de.wehner.mediamagpie.conductor.webapp.util;

import java.util.Random;

public class PassPhrase {

    protected static Random r = new Random();

    /*
     * Set of characters that is valid. Must be printable, memorable, and "won't break HTML" (i.e., not ' <', '>', '&', '=', ...). or break
     * shell commands (i.e., not ' <', '>', '$', '!', ...). I, L and O are good to leave out, as are numeric zero and one.
     */
    protected static char[] goodChar = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'j', 'k', 'm', 'n', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y',
            'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '2', '3', '4', '5',
            '6', '7', '8', '9', '+', '-', '@', };

    /** Minimum length for a decent password */
    private final int _length;

    public PassPhrase() {
        this(6);
    }

    public PassPhrase(int sequenceLength) {
        _length = sequenceLength;
    }

    /* Generate a Password object with a random password. */
    public String getNext() {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < _length; i++) {
            sb.append(goodChar[r.nextInt(goodChar.length)]);
        }
        return sb.toString();
    }

}
