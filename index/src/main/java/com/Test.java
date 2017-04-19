package com;

import com.tree.IndexTrieMain;
import io.vertx.core.json.JsonObject;
import net.jpountz.lz4.LZ4Compressor;
import net.jpountz.lz4.LZ4Factory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Date;

/**
 * creator: sunc
 * date: 2017/4/13
 * description:
 */
public class Test {


    private static final String file = "f:/display1.csv";
    private static final String word = "teacherrayl";

    public static void main(String[] args) throws Exception {
//        createFile();
//        test20();
//        test21();

        Date start = new Date();

        encode("D:/tools/ideaIU-2017.1.exe", 1);

        Date end = new Date();

        System.out.println(end.getTime() - start.getTime());

    }

    public static void test20() {
        JsonObject json = new JsonObject().put("table", "users").put("column", "displayName").put("type", 0).put("path", file);
        System.out.println(IndexTrieMain.buildTrie(json));
        System.out.println(IndexTrieMain.search(word, 10));
//        System.out.println(IndexTrieMain.getAllNodesBFS().size());
        System.out.println(IndexTrieMain.store("g:/lucene/index/indexBackup"));
    }

    public static void test21() {
        System.out.println(IndexTrieMain.restore("g:/lucene/index/indexBackup"));
        System.out.println(IndexTrieMain.search(word, 10));
//        System.out.println(IndexTrieMain.getAllNodesBFS().size());


//        try {
//            BufferedReader br = new BufferedReader(new FileReader("g:/lucene/index/test.txt"));
//            String line;
//            while ((line = br.readLine()) != null) {
//                System.out.println(line);
//                JsonArray jsonArray = new JsonArray(line);
//                System.out.println(jsonArray);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }

    public static void createFile() {
        String[] chars = {"a", "b", "c", "d", "e", "f",
                "g", "h", "i", "g", "k", "l", "m", "n",
                "o", "p", "q", "r", "s", "t", "u", "v",
                "w", "x", "y", "z", "0", "1", "2", "3",
                "4", "5", "6", "7", "8", "9"};
        try {
            FileWriter fw = new FileWriter(new File("f:/test.csv"));
            int count = 0;
            while (count < 1000) {
                int len = new Float(Math.random() * 5 + 5).intValue();// 5<=  <10
                StringBuilder str = new StringBuilder();
                for (int i = 0; i < len; i++) {
                    int index = new Float(Math.random() * chars.length).intValue();
                    str.append(chars[index]);
                }
                fw.write(str.toString() + "\n");
                count++;
                if (count % 10000 == 0)
                    System.out.println("create number : " + count);
            }
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void encode(String file, int i) {
        String outputFile = file + i;
        try {
            byte[] data = Files.readAllBytes(new File(file).toPath());

            LZ4Factory factory = LZ4Factory.safeInstance();

            LZ4Compressor compressor = factory.highCompressor(i);
            File f = new File(outputFile);
            if (!f.exists()) {
                f.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(outputFile);

            byte[] result = compressor.compress(data);

            fos.write(result);
            fos.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
