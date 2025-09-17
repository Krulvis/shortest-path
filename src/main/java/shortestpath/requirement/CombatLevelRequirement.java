package shortestpath.requirement;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CombatLevelRequirement implements Requirement {

    @Getter
    private final int level;

    public boolean meets() {
        return false;
    }
}
