package ru.vsu.cs.course2.hci.task1;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

public class Util {
    private static final String CHOOSE_OPEN_FILE_MESSAGE = "Выберите файл для открытия";

    public static String browseOpenFile() {
        FileDialog fileDialog = new FileDialog((Frame) null, CHOOSE_OPEN_FILE_MESSAGE);
        fileDialog.setMode(FileDialog.LOAD);
        fileDialog.setVisible(true);

        String dir = fileDialog.getDirectory();
        String file = fileDialog.getFile();
        if (dir != null && file != null) {
            return dir + file;
        }
        return null;
    }

    public static double[] readFile(String filename) {
        try {
            Path path = Paths.get(filename);
            Stream<String> lines = Files.lines(path);
            return lines.map(s -> {
                try {
                    return Double.parseDouble(s);
                } catch (NumberFormatException e) {
                    return null;
                }
            })
                    .filter(Objects::nonNull)
                    .mapToDouble(x -> x)
                    .toArray();
        } catch (IOException e) {
            return null;
        }
    }

    public static Process startProvider(String filename) {
        try {
            Process process = new ProcessBuilder(filename).start();
            InputStream inputStream = process.getInputStream();
            System.setIn(inputStream);
            return process;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getTitle(String filename) {
        int startIndex = filename.lastIndexOf(File.separatorChar) + 1;
        int endIndex = filename.lastIndexOf('.');
        if (endIndex < 0)
            endIndex = filename.length();
        return filename.substring(startIndex, endIndex);
    }

    public static double[] generateXAxis(int length) {
        return DoubleStream.iterate(1, n -> n + 1).limit(length).toArray();
    }
}
