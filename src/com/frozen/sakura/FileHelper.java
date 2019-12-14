package com.frozen.sakura;

import java.io.*;
import java.util.ArrayList;

public class FileHelper {

    private static class SheetMusic {
        public String note;
        public int count;

        public SheetMusic (String note, int count) {
            super();
            this.note = note;
            this.count = count;
        }
    }

    // 计数器
    private int woc = 0;
    // 调性转换
    private final int offset = 3;
    // 储存乐谱结果
    private ArrayList<SheetMusic> sheetMusics = new ArrayList<>();

    public void createFile() {
        String pathname = "src/com/frozen/sakura/input";
        StringBuilder data = new StringBuilder();
        try (FileReader reader = new FileReader(pathname);
             BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.length() == 0)
                    continue;
                transformData(line.replace(" ", ""));
            }
            solveOverlapNote();
            int width = 0;
            for (SheetMusic item :
                    sheetMusics) {
                for (int i = 0; i < item.count; i++, woc++) {
                    data.append("    ")
                            .append(woc)
                            .append(": ")
                            .append(item.note)
                            .append(";\n");
                    if (Integer.parseInt(item.note) > width)
                        width = Integer.parseInt(item.note);
                }
            }
            String stringBuilder = "WIDTH = " +
                    // 计算宽度
                    (int) (Math.log(getNextPow2(width)) / Math.log(2)) +
                    ";\nDEPTH = " +
                    // 计算深度
                    getNextPow2(woc) +
                    ";\n\nADDRESS_RADIX = DEC;\nDATA_RADIX = DEC;\n\nCONTENT BEGIN\n" +
                    data.toString() +
                    "END;";
            writeFile(stringBuilder);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 写入文件
    private void writeFile(String content) {
        try {
            File writeName = new File("src/com/frozen/sakura/output");
            try (FileWriter writer = new FileWriter(writeName);
                 BufferedWriter out = new BufferedWriter(writer)
            ) {
                out.write(content);
                out.flush();
                System.out.println("Successful.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void transformData(String data) {
        // 音符格式错误
        if (data.contains(".") && data.contains("-")) {
            SheetMusic sheetMusic = new SheetMusic("error", 1);
            sheetMusics.add(sheetMusic);
        } else {
            int count;
            // 计算单位个数
            if (data.contains(".."))
                count = 1;
            else if (data.contains("."))
                if (data.length() - data.replace(".", "").length() == 2)
                    count = 3;
                else if (data.charAt(0) == '.')
                    count = 2;
                else
                    count = 5;
            else
                count = 4 * (data.length() - data.replace("-", "").length() + 1);
            // 将音符添加至乐谱中
            SheetMusic sheetMusic = new SheetMusic(number(data.replace(".", "").replace("-", "")), count);
            sheetMusics.add(sheetMusic);
        }
    }

    // 生成对应音高
    private String number(String index) {
        switch (index.length()) {
            case 1:
                if (index.equals("0"))
                    return index;
                // 中音
                return String.valueOf(Integer.parseInt(index) + 7 + offset);
            case 2:
                int note = Integer.parseInt(String.valueOf(index.charAt(1)));
                switch (index.charAt(0)) {
                    case '0':
                        // 低音
                        return String.valueOf(note + offset);
                    case '1':
                        // 高音
                        return String.valueOf(note + 14 + offset);
                    case '2':
                        return String.valueOf(note + 21 + offset);
                }
            default:
                return index;
        }
    }

    // 处理相近音符
    private void solveOverlapNote() {
        for (int i = 1; i < sheetMusics.size(); i++) {
            SheetMusic temp = sheetMusics.get(i - 1);
            if (sheetMusics.get(i).note.equals(temp.note) && temp.count > 1) {
                temp.count -= 1;
                sheetMusics.add(i, new SheetMusic("0", 1));
                i++;
            }
        }
    }

    // 计算最接近的二次幂
    private long getNextPow2(int v) {
        long x = v - 1;
        x |= x >>> 1;
        x |= x >>> 2;
        x |= x >>> 4;
        x |= x >>> 8;
        x |= x >>> 16;
        return x + 1;
    }
}
