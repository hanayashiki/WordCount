package wordCount;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class AsciiCount {
    private final int BUFFER_SIZE = 4096;
    protected static InputStreamReader inputStreamReader;
    private char [] charBuffer = new char[BUFFER_SIZE];
    public AsciiCount(InputStream inputStream) {
        this.inputStreamReader = new InputStreamReader(inputStream);
    }
    protected static boolean isAscii(char c) {
        return c >= 0 && c <= 127;

    }
    public int count() throws IOException {
        char c;
        int count = 0;
        while ((c = (char)inputStreamReader.read()) != (char)-1) {
            if (isAscii(c)) {
                count++;
                System.out.println("get char: " + (int)c);
            }
        }
        return count;
    }
}
