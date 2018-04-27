import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class asciiCount {
    private final int BUFFER_SIZE = 4096;
    private static InputStreamReader inputStreamReader;
    private char [] charBuffer = new char[BUFFER_SIZE];
    asciiCount(InputStream inputStream) {
        this.inputStreamReader = new InputStreamReader(inputStream);
    }
    private static boolean isAscii(char c) {
        return c >= 0 && c <= 127;

    }
    public static int count() throws IOException {
        char c;
        int count = 0;
        while ((c = (char)inputStreamReader.read()) != (char)-1) {
            if (isAscii(c)) {
                count++;
            }
        }
        return count;
    }
}
