package converter;

import net.runelite.api.coords.WorldPoint;
import shortestpath.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Utils {

    public static String fromWorldPoint(WorldPoint wp) {
        return "new WorldPoint(" + wp.getX() + ", " + wp.getY() + ", " + wp.getPlane() + ")";
    }

    public static String fromWorldPoint(int packed) {
        return fromWorldPoint(WorldPointUtil.unpackWorldPoint(packed));
    }

    public static String action(Transport t) {
        return t.getObjectInfo().split(" ")[0];
    }

    public static String objectId(Transport t) {
        String[] parts = t.getObjectInfo().split(" ");
        return parts[parts.length - 1];
    }

    public static String entityName(Transport t) {
        String objInfo = t.getObjectInfo();
        String[] parts = objInfo.split(" ");
        if (parts.length == 3) {
            return parts[1];
        } else {
            // Take all parts except the first and last (indices 1 to length-2)
            StringBuilder name = new StringBuilder();
            for (int i = 1; i < parts.length - 1; i++) {
                if (i > 1) {
                    name.append(" ");
                }
                name.append(parts[i]);
            }
            return name.toString();
        }
    }


    public static List<Transport> parseTransports(String path, TransportType transportType) {
        List<Transport> newTransports = new ArrayList<>();
        final String DELIM_COLUMN = "\t";
        final String PREFIX_COMMENT = "#";

        try {
            String s = new String(Util.readAllBytes(ShortestPathPlugin.class.getResourceAsStream(path)), StandardCharsets.UTF_8);
            Scanner scanner = new Scanner(s);

            // Header line is the first line in the file and will start with either '#' or '# '
            String headerLine = scanner.nextLine();
            headerLine = headerLine.startsWith(PREFIX_COMMENT + " ") ? headerLine.replace(PREFIX_COMMENT + " ", PREFIX_COMMENT) : headerLine;
            headerLine = headerLine.startsWith(PREFIX_COMMENT) ? headerLine.replace(PREFIX_COMMENT, "") : headerLine;
            String[] headers = headerLine.split(DELIM_COLUMN);

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();

                if (line.startsWith(PREFIX_COMMENT) || line.isBlank()) {
                    continue;
                }

                String[] fields = line.split(DELIM_COLUMN);
                Map<String, String> fieldMap = new HashMap<>();
                for (int i = 0; i < headers.length; i++) {
                    if (i < fields.length) {
                        fieldMap.put(headers[i], fields[i]);
                    }
                }

                Transport transport = new Transport(fieldMap, transportType);
                newTransports.add(transport);
            }
            scanner.close();
        } catch (Exception e) {
            System.out.println("Error parsing " + path + ": " + e.getMessage());
            e.printStackTrace();
        }
        return newTransports;
    }

    public static void writeEnumToFile(String enumName, String enumSource, String outputDir) {
        try {
            Path outputPath = Path.of(outputDir, enumName + ".java");

            // Ensure the directory exists (works on Windows, Linux, macOS)
            Files.createDirectories(outputPath.getParent());

            // Write the file
            Files.writeString(outputPath, enumSource, StandardCharsets.UTF_8);

            System.out.println("Wrote enum to " + outputPath.toAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException("Failed to write enum to file", e);
        }
    }
}
