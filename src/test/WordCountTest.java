package test;

import wordCount.AsciiCount;
import wordCount.WordCount;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

public class WordCountTest {
    WordCountTest() {}
    public static void main(String args[]) throws FileNotFoundException, IOException {
        String [] tests = {
                "7:src/test/textsForWordCount/test1.txt",
                "5:src/test/textsForWordCount/test2chinese.txt",
                "0:src/test/textsForWordCount/test3.txt",
        };
        for (String test : tests) {
            String [] splitted = test.split(":");
            String fileAddr = splitted[1];
            int expect = Integer.parseInt(splitted[0]);
            FileInputStream file = new FileInputStream(fileAddr);
            WordCount counter = new WordCount(file, 2);
            counter.countWords();
            System.out.println(test + ": word: " + counter.wordCountResult);
            System.out.println(test + ": phrase: " + counter.phraseCountResult);
        }
    }
}