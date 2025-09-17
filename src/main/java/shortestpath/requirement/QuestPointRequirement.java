package shortestpath.requirement;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class QuestPointRequirement implements Requirement {

    @Getter
    private final int points;

    public boolean meets() {
        return false;
    }
}
