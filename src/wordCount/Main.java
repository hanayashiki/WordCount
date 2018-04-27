package wordCount;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

class BadArgumentException extends Exception {
    String message;
    BadArgumentException(String message) {
        this.message = "Bad Arguments: " + message;
    }
}

public class Main {
    static boolean countPhrase = false;
    static int phraseLength = 0;
    static int wordLimit = 10;
    static boolean recursive = false;
    static String fileAddr = "";
    static String dirAddr = "";

    private static void parsePath(String string) throws BadArgumentException {
        fileAddr = string;
        if (!new File(fileAddr).exists()) {
            throw new BadArgumentException("File does not exist.");
        } else {
            if (new File(fileAddr).isDirectory()) {
                throw new BadArgumentException("Unexpected directory path.");
            }
        }
    }

    private static void parseArgs(String args[]) throws BadArgumentException {
        try {
            if (args[0].equals("-m")) {
                countPhrase = true;
                try {
                    phraseLength = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    throw new BadArgumentException("NumberFormatException.");
                }
                parsePath(args[2]);
            } else if (args[0].equals("-n")) {
                try {
                    wordLimit = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    throw new BadArgumentException("NumberFormatException.");
                }
                parsePath(args[2]);
            } else if (args[0].equals("-r")) {
                recursive = true;
                parsePath(args[1]);
            } else {
                parsePath(args[0]);
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new BadArgumentException("too few arguments.");
        }
    }

    private static void printWords(HashMap<String, Integer> wordCountResult) {
        String [] outputs = new String[wordCountResult.size()];
        Iterator<Map.Entry<String, Integer>> iterator = wordCountResult.entrySet().iterator();
        int i = 0;
        while (iterator.hasNext()) {
            Map.Entry<String, Integer> entry = iterator.next();
            outputs[i++] = entry.getKey() + ": " + entry.getValue();
        }
        Arrays.sort(outputs);
        for (int j = 0; j < Math.min(outputs.length, wordLimit); j++) {
            System.out.println(outputs[j]);
        }
    }

    private static void printPhrases(HashMap<Phrase, Integer> phraseCountResult) {
        Phrase [] outputs = new Phrase[phraseCountResult.size()];
        Iterator<Map.Entry<Phrase, Integer>> iterator = phraseCountResult.entrySet().iterator();
        int i = 0;
        while (iterator.hasNext()) {
            Phrase phr = iterator.next().getKey();
            outputs[i++] = phr;
            //System.out.println("copying: " + phr + ": " + phr.getCount());
        }
        Arrays.sort(outputs);
        for (int j = Math.max(outputs.length, outputs.length - wordLimit) - 1; j >= 0; j--) {
            System.out.println(outputs[j] + ": " + outputs[j].getCount());
        }
    }

    public static void main(String args[]) {
        try {
            parseArgs(args);
        } catch (BadArgumentException e) {
            System.out.println(e.message);
            return;
        }
        try {
            WordCount counter = new WordCount(new FileInputStream(fileAddr), phraseLength);
            counter.countWords();
            System.out.println("characters: " + counter.asciiCount);
            System.out.println("lines: " + counter.lineCount);
            System.out.println("words: " + counter.wordCountResult.size());
            if (!countPhrase) {
                printWords(counter.wordCountResult);
            } else {
                printPhrases(counter.phraseCountResult);
               // System.out.println(counter.phraseCountResult);
            }
        } catch (FileNotFoundException e) {
            System.out.println("File is not found " + fileAddr);
            return;
        } catch (IOException e) {
            System.out.println("Exception happened during IO.");
            return;
        }

    }
}
