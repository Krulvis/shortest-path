package shortestpath.requirement;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.Quest;

@RequiredArgsConstructor
public class QuestRequirement implements Requirement {

    @Getter
    private final Quest quest;

    public boolean meets() {
        return false;
    }
}
