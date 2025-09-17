package shortestpath.requirement;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TotalLevelRequirement implements Requirement {

    @Getter
    private final int level;

    public boolean meets() {
        return false;
    }
}
