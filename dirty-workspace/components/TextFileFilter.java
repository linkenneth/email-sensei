package components;

import java.io.File;
import javax.swing.*;
import javax.swing.filechooser.*;

public class TextFileFilter extends FileFilter {

    public boolean accept (File f) {
        if (f.isDirectory()) {
            return true;
        }
        String extension = Utils.getExtension(f);

        if (extension != null) {
            if (extension.equals(Utils.txt)) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    public String getDescription() {
        return "Only .txt files";
    }
}


