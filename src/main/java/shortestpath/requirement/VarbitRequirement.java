package shortestpath.requirement;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import shortestpath.TransportVarCheck;

@RequiredArgsConstructor
public class VarbitRequirement implements Requirement {

    @Getter
    private final int varbit;
    @Getter
    private final int value;
    @Getter
    private final TransportVarCheck type;

    public boolean meets() {
        return false;
    }
}
