package test;

import wordCount.AsciiCount;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class AsciiCountTest {
    AsciiCountTest() {}
    public static void main(String args[]) throws FileNotFoundException, IOException {
        String [] tests = {
                "7:src/test/textsForAsciiCount/test1.txt",
                "5:src/test/textsForAsciiCount/test2chinese.txt"
        };
        for (String test : tests) {
            String [] splitted = test.split(":");
            String fileAddr = splitted[1];
            int expect = Integer.parseInt(splitted[0]);
            FileInputStream file = new FileInputStream(fileAddr);
            AsciiCount counter = new AsciiCount(file);
            int count  = counter.count();
            System.out.println(fileAddr + ": "  + (expect == count));
            if (count != expect) {
                System.out.println("count == " + count + ", while expected count == " + expect);
            }
        }
    }
}