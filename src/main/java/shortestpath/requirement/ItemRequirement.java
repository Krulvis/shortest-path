package shortestpath.requirement;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ItemRequirement implements Requirement {

    @Getter
    private final int itemId;
    @Getter
    private final int amount;

    public boolean meets() {
        return false;
    }
}
