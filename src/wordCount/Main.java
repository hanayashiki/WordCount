package wordCount;

import java.io.*;
import java.util.*;


class BadArgumentException extends Exception {
    String message;
    BadArgumentException(String message) {
        this.message = "Bad Arguments: " + message;
    }
}

class WordEntry implements Comparable<WordEntry> {
    String word;
    int count;
    static boolean sortOnCount = true;

    WordEntry(String word, int count) {
        this.word = word;
        this.count = count;
    }

    @Override
    public int compareTo(WordEntry o) {
        if (sortOnCount) {
            int countResult = -(new Integer(count).compareTo(o.count));
            if (countResult == 0) {
                return word.compareTo(o.word);
            }
            return countResult;
        } else {
            return word.compareTo(o.word);
        }
    }

    @Override
    public String toString() {
        return word + ": " + count;
    }
}

public class Main {
    private static boolean countPhrase = false;
    private static int phraseLength = 0;
    private static int wordLimit = 10;
    private static boolean recursive = false;
    private static String fileAddr = "";
    private static String dirAddr = "";

    private static ArrayList<String> pathList = new ArrayList<String>();

    private static void parsePath(String string, Boolean isFile) throws BadArgumentException {
        fileAddr = string;
        dirAddr = string;
        if (!new File(fileAddr).exists()) {
            throw new BadArgumentException("File does not exist.");
        } else {
            if (new File(fileAddr).isDirectory() && isFile) {
                throw new BadArgumentException("Unexpected directory path.");
            } else if (new File(fileAddr).isFile() && !isFile) {
                throw new BadArgumentException("Unexpected file path.");
            }
        }
    }

    private static void parseArgs(String args[]) throws BadArgumentException {
        try {
            if (args[0].equals("-m")) {
                countPhrase = true;
                try {
                    phraseLength = Integer.parseInt(args[1]);
                    if (phraseLength <= 0) {
                        throw new BadArgumentException("Invalid phrase length.");
                    }
                } catch (NumberFormatException e) {
                    throw new BadArgumentException("NumberFormatException.");
                }
                parsePath(args[2], true);
            } else if (args[0].equals("-n")) {
                try {
                    wordLimit = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    throw new BadArgumentException("NumberFormatException.");
                }
                parsePath(args[2], true);
            } else if (args[0].equals("-r")) {
                recursive = true;
                parsePath(args[1], false);
            } else {
                parsePath(args[0], true);
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new BadArgumentException("too few arguments.");
        }
    }

    private static void traverseDirectory(String dir) {
        File root = new File(dir);
        File [] files = root.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    pathList.add(file.getAbsolutePath());
                } else if (file.isDirectory()) {
                    traverseDirectory(file.getAbsolutePath());
                }
            }
        }
    }

    private static void printWords(HashMap<String, Integer> wordCountResult) {
        WordEntry [] outputs = new WordEntry[wordCountResult.size()];
        Iterator<Map.Entry<String, Integer>> iterator = wordCountResult.entrySet().iterator();
        // System.out.println(wordCountResult);
        int i = 0;
        while (iterator.hasNext()) {
            Map.Entry<String, Integer> entry = iterator.next();
            outputs[i++] = new WordEntry(entry.getKey(), entry.getValue());
        }
        WordEntry.sortOnCount = true;
        Arrays.sort(outputs);
        ArrayList<WordEntry> wordEntries = new ArrayList<>();
        WordEntry last = null;
        int count = 0;
        for (int k = 0; k < outputs.length; k++) {
            if (last == null || last.count != outputs[k].count) {
                wordEntries.add(outputs[k]);
                last  = outputs[k];
                count++;
                if (count >= wordLimit) {
                    break;
                }
            }
        }
        // System.out.println(wordEntries);
        WordEntry.sortOnCount = false;
        wordEntries.sort(null);
        for (int k = 0; k < Math.min(wordLimit, wordEntries.size()); k++) {
            System.out.println(wordEntries.get(k));
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
        for (int j = outputs.length - 1; j >= 0; j--) {
            System.out.println(outputs[j] + ": " + outputs[j].getCount());
        }
    }

    public static void main(String args[]) throws FileNotFoundException  {
        try {
            parseArgs(args);
        } catch (BadArgumentException e) {
            System.out.println(e.message);
            return;
        }
        try {
            WordCount counter;
            if (recursive == false) {
                counter = new WordCount(new FileInputStream(fileAddr), phraseLength);
            } else {
                traverseDirectory(dirAddr);
                counter = new WordCount(pathList, phraseLength);
            }
            System.setOut(new PrintStream(new FileOutputStream(new File("result.txt"))));
            counter.countWords();
            System.out.println("characters: " + counter.asciiCount);
            System.out.println("lines: " + counter.lineCount);
            System.out.println("words: " + counter.totalWords);
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
