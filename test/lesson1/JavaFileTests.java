package lesson1;

import org.junit.Assert;

import java.io.*;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.Arrays;

public class JavaFileTests {

    public static void assertFilesByLines(String file1Name, String file2Name) {
        Stopwatch.start();

        try (BufferedReader br1 = new BufferedReader(new FileReader(new File(file1Name)))) {
            try (BufferedReader br2 = new BufferedReader(new FileReader(new File(file2Name)))) {
                String str1 = br1.readLine();
                String str2 = br2.readLine();
                while (str1 != null || str2 != null) {
                    Assert.assertEquals(str1, str2);
                    str1 = br1.readLine();
                    str2 = br2.readLine();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Stopwatch.stop("assertFilesByLines");
    }

    public static boolean compareFilesByBytes(String file1Name, String file2Name) {
        Stopwatch.start();

        boolean areIdentical = false;
        try {
            File file1 = new File(file1Name);
            File file2 = new File(file2Name);

            if (!file1.exists() || !file2.exists()) {
                throw new FileNotFoundException();
            }
            if (file1.length() != file2.length()) {
                return false;
            }

            long f1Length = file1.length();
            long f2Length = file2.length();

            FileInputStream fis1 = new FileInputStream(file1);
            DigestInputStream dgStream1 = new DigestInputStream(fis1,
                    MessageDigest.getInstance("MD5"));
            FileInputStream fis2 = new FileInputStream(file2);
            DigestInputStream dgStream2 = new DigestInputStream(fis2,
                    MessageDigest.getInstance("MD5"));
            // most expensive is dgStream1.getMessageDigest() so do it only at last read
            dgStream1.on(false);
            dgStream2.on(false);

            long f1ReadTotal = 0;
            long f2ReadTotal = 0;

            int read;
            byte[] buff = new byte[1024 * 128];
            do {
                if ((f1Length - f1ReadTotal) < (1024 * 128)) {
                    // last read
                    dgStream1.on(true);
                }
                read = dgStream1.read(buff);
                f1ReadTotal += read > 0 ? read : 0;
            } while (read > 0);

            do {
                if ((f2Length - f2ReadTotal) < (1024 * 128)) {
                    // last read
                    dgStream2.on(true);
                }
                read = dgStream2.read(buff);
                f2ReadTotal += read > 0 ? read : 0;
            } while (read > 0);

            if (Arrays.equals(dgStream1.getMessageDigest().digest(), dgStream2
                    .getMessageDigest().digest())) areIdentical = true;

            fis1.close();
            fis2.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        Stopwatch.stop("compareFilesByBytes");

        return areIdentical;
    }
}
