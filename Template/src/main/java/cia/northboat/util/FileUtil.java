package cia.northboat.util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {

    /**
     * 读取文件并将每行内容存储到 List<String> 中
     *
     * @param filePath 文件的相对路径
     * @return 包含文件每行内容的 List<String>
     * @throws IOException 如果文件读取失败
     */
    public static List<String> readFileToList(String filePath) {
        List<String> lines = new ArrayList<>();

        // 使用 ClassLoader 获取资源流
        try (InputStream inputStream = FileUtil.class.getClassLoader().getResourceAsStream(filePath);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            if (inputStream == null) {
                throw new IOException("文件未找到: " + filePath);
            }

            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line); // 将每行内容添加到 List 中
            }
        } catch (IOException e){
            System.err.println("读取文件失败: " + e.getMessage());
            return null;
        }

        return lines;
    }

    public static void writeCostToLog(String logMessage){
        // 将日志写入文件
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("time.log", true))) { // true 表示追加模式
            writer.write(logMessage);
        } catch (IOException e) {
            System.err.println("写入日志文件失败: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        String filePath = "100.txt"; // 文件的相对路径

        writeCostToLog(filePath);
    }
}