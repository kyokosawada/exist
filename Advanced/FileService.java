import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.List;
import java.util.ArrayList;

public class FileService {

    private static final Pattern CELL_PATTERN = Pattern.compile("\\([^,]*,[^)]*\\)");

    public String getFileName(String[] args) throws Exception {

        if (args.length == 0) {
            throw new Exception("No filename provided.");
        }

        String fileName = args[0];

        if (fileName.isEmpty()) {
            throw new Exception("Filename cannot be empty.");
        }

        if (!fileExists(fileName)) {
            throw new Exception("File '" + fileName + "' not found or not readable.");
        }
        return fileName;
    }

    public boolean fileExists(String fileName) {
        Path path = Paths.get(fileName);
        return Files.exists(path) && Files.isReadable(path);
    }

    public String loadFileContent(String fileName) throws IOException {
        return Files.readString(Paths.get(fileName), StandardCharsets.UTF_8);
    }

    public Table parseFileToTable(String content) {
        Table table = new Table();

        if (content.isEmpty()) {
            System.out.println("File is empty, returning empty table.");
            return table;
        }

        String[] lines = content.split("\\r?\\n");

        for (int lineIndex = 0; lineIndex < lines.length; lineIndex++) {
            String line = lines[lineIndex];

            if (!parseLineToRow(line, lineIndex).isEmpty()) {
                table.add(parseLineToRow(line, lineIndex));
            }
        }

        return table;
    }

    private List<String> parseLineToRow(String line, int lineIndex) {
        List<String> rowCells = new ArrayList<>();
        Matcher matcher = CELL_PATTERN.matcher(line);

        while (matcher.find()) {
            String fullMatch = matcher.group(0); 
            rowCells.add(fullMatch);
        }

        return rowCells;
    }

    public String tableToString(Table table) {
        StringBuilder content = new StringBuilder();

        for (int i = 0; i < table.size(); i++) {
            for (int j = 0; j < table.get(i).size(); j++) {
                if (j > 0) {
                    content.append(" ");
                }
                content.append(table.get(i).get(j));
            }

            if (i < table.size() - 1) {
                content.append("\n");
            }
        }

        return content.toString();
    }

    public void saveFile(Table table, String fileName) throws IOException {
        String content = tableToString(table);
        Files.writeString(Paths.get(fileName), content, StandardCharsets.UTF_8);
    }

}