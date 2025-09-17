package converter;

import net.runelite.api.Quest;
import net.runelite.api.Skill;
import shortestpath.Transport;
import shortestpath.TransportType;
import shortestpath.TransportVarbit;

import java.util.*;

import static converter.Utils.*;

public class Converter {

    final static String[] LADDER_NAMES = {"ladder"};
    final static String[] STAIR_NAMES = {"stair"};
    final static String[] ENTRANCE_NAMES = {"trapdoor", "cave"};
    final static String[] DOOR_NAMES = {"door", "gate"};

    public static void main(String[] args) {
        List<Transport> simpleTransports = parseTransports("/transports/transports.tsv", TransportType.TRANSPORT);
        List<Transport> ladders = new ArrayList<>();
        List<Transport> doors = new ArrayList<>();
        List<Transport> others = new ArrayList<>();
        List<Transport> stairs = new ArrayList<>();
        List<Transport> caves = new ArrayList<>();
        for (Transport t : simpleTransports) {
            String info = t.getObjectInfo().toLowerCase();
            if (Arrays.stream(LADDER_NAMES).anyMatch(info::contains)) {
                ladders.add(t);
            } else if (Arrays.stream(STAIR_NAMES).anyMatch(info::contains)) {
                stairs.add(t);
            } else if (Arrays.stream(ENTRANCE_NAMES).anyMatch(info::contains)) {
                caves.add(t);
            } else if (Arrays.stream(DOOR_NAMES).anyMatch(info::contains)) {
                doors.add(t);
            } else {
                others.add(t);
            }
        }
        generateAndWriteEnum("LadderTransport", ladders);
        generateAndWriteEnum("DoorTransport", doors);
        generateAndWriteEnum("StairTransport", stairs);
        generateAndWriteEnum("CaveTransport", caves);
        generateAndWriteEnum("SimpleTransport", others);

        parseAndWriteEnum("/transports/agility_shortcuts.tsv", "AgilityTransport", TransportType.AGILITY_SHORTCUT);
    }


    private static void parseAndWriteEnum(String path, String enumName, TransportType type) {
        List<Transport> transports = parseTransports(path, type);
        generateAndWriteEnum(enumName, transports);
    }

    private static void generateAndWriteEnum(String enumName, List<Transport> transports) {
        String classString = generateEnum(enumName, transports);
        writeEnumToFile(enumName, classString, "src/main/java/shortestpath/transport");
    }

    private static String generateEnum(String enumName, List<Transport> transports) {
        StringBuilder sb = new StringBuilder()
                .append("package shortestpath.transport;\n\n")
                .append("import net.runelite.api.Skill;\n")
                .append("import lombok.Getter;\n")
                .append("import net.runelite.api.Quest;\n")
                .append("import shortestpath.TransportVarCheck;\n")
                .append("import shortestpath.requirement.Requirement;\n")
                .append("import lombok.RequiredArgsConstructor;\n")
                .append("import shortestpath.requirement.*;\n")
                .append("import net.runelite.api.coords.WorldPoint;\n\n")
                .append("@RequiredArgsConstructor\n")
                .append("public enum ").append(enumName).append(" implements ITransport {\n");

        int count = 0;
        Set<String> existingNames = new HashSet<>();
        for (Transport t : transports) {
            String constName = makeEnumConstantName(t); // we’ll write this helper
            if (existingNames.contains(constName)) continue;

            existingNames.add(constName);
            sb.append("    ").append(constName).append("(")
                    .append(Utils.fromWorldPoint(t.getOrigin())).append(", ")
                    .append(Utils.fromWorldPoint(t.getDestination())).append(", ")
                    .append(objectId(t)).append(", ")
                    .append("\"" + entityName(t) + "\"").append(", ")
                    .append("\"" + action(t) + "\"").append(", ")
                    .append(buildRequirementsArray(t))
                    .append(")");

            count++;
            if (count < transports.size()) {
                sb.append(",");
            }
            sb.append("\n");
        }

        sb.append(";\n\n")
                .append("    @Getter\n")
                .append("    private final WorldPoint origin;\n")
                .append("    @Getter\n")
                .append("    private final WorldPoint destination;\n")
                .append("    @Getter\n")
                .append("    private final int objectId;\n")
                .append("    @Getter\n")
                .append("    private final String objectName;\n")
                .append("    @Getter\n")
                .append("    private final String action;\n")
                .append("    @Getter\n")
                .append("    private final Requirement[] requirements;\n\n");

        sb.append("}\n");

        String classString = sb.toString();
        System.out.println(classString);
        return classString;
    }


    private static String makeEnumConstantName(Transport t) {
        String name = entityName(t);
        String origin = String.valueOf(Math.abs(t.getOrigin()));
        return name.replaceAll("\\s+", "_").replaceAll("-", "_")
                .replaceAll("[()\\->,]", "").replaceAll("'", "")
                .replaceAll("__", "_")
                .toUpperCase() + "_" + origin;
    }

    private static String buildRequirementsArray(Transport t) {
        List<String> reqs = new ArrayList<>();

        int[] skillRequirements = t.getSkillLevels();
        for (int skill = 0; skill < skillRequirements.length; skill++) {
            int level = skillRequirements[skill];
            if (level <= 0) continue;
            Skill[] skills = Skill.values();
            if (skill < skills.length) {
                reqs.add("new SkillRequirement(Skill." + skills[skill].name() + ", " + skillRequirements[skill] + ", true)");
            } else if (skill == skills.length) {
                return "new TotalLevelRequirement(" + level + ")";
            } else if (skill == skills.length + 1) {
                return "new CombatLevelRequirement(" + level + ")";
            } else if (skill == skills.length + 2) {
                return "new QuestPointRequirement(" + level + ")";
            }
        }
        for (Quest qr : t.getQuests()) {
            reqs.add("new QuestRequirement(Quest." + qr.name() + ")");
        }
        for (TransportVarbit vr : t.getVarbits()) {
            reqs.add("new VarbitRequirement(" + vr.getId() + ", " + vr.getValue() + ", TransportVarCheck." + vr.getCheck() + ")");
        }

        if (reqs.isEmpty()) {
            return "new Requirement[]{}";
        }
        return "new Requirement[]{" + String.join(", ", reqs) + "}";
    }
}
