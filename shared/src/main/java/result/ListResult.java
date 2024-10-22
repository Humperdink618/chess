package result;

import model.GameData;

import java.util.Collection;

public record ListResult(Collection<GameData> games) {
}
// parameter names have to match with the names given in the spec