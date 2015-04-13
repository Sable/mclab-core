package matlab;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CompositePositionMap extends PositionMap {
    private final List<PositionMap> maps;

    // Apply map1, then map2 (i.e. innermost first)
    public CompositePositionMap(PositionMap map1, PositionMap map2) {
        this.maps = new ArrayList<PositionMap>();
        if (map1 != null) {
            this.maps.add(map1);
        }
        if (map2 != null) {
            this.maps.add(map2);
        }
    }

    // Apply the maps in list order (i.e. innermost first)
    public CompositePositionMap(List<PositionMap> maps) {
        if (maps == null) {
            this.maps = Collections.emptyList();
        } else {
            this.maps = Collections.unmodifiableList(maps);
        }
    }

    @Override
    public TextPosition getPreTranslationPosition(TextPosition dest) {
        TextPosition pos = dest;
        for (PositionMap map : maps) {
            pos = map.getPreTranslationPosition(pos);
        }
        return pos;
    }

}
