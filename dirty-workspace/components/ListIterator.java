package components;

import java.util.Scanner;
import java.io.*;

public class ListIterator {
    Scanner sc;

    ListIterator (File file) {
        try {
            sc = new Scanner(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean hasNext() {
        return sc.hasNextLine();
    }

    public String next() {
        return sc.nextLine();
    }
}
