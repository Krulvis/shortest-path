package shortestpath.requirement;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.Skill;

@RequiredArgsConstructor
public class SkillRequirement implements Requirement {

    @Getter
    private final Skill skill;
    @Getter
    private final int level;
    @Getter
    private final boolean boosted;

    public boolean meets() {
        return false;
    }
}
