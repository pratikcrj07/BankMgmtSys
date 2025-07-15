package bank.management.system.with.ATM.simulation;

import org.opencv.core.Core;

public class test {
    static { System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }

    public static void main(String[] args) {
        System.out.println(" OpenCV version: " + Core.VERSION);
    }
}
