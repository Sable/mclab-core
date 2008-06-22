package matlab;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class OffsetTracker {

    private final Map<TextPosition, OffsetChange> offsetChangeMap;

    public OffsetTracker() {
        this.offsetChangeMap = new TreeMap<TextPosition, OffsetChange>();
    }

    public void recordOffsetChange(TextPosition pos, int lineOffsetChange, int colOffsetChange) {
        OffsetChange existingOC = offsetChangeMap.get(pos);
        OffsetChange newOC = new OffsetChange(lineOffsetChange, colOffsetChange);
        if(existingOC == null) {
            offsetChangeMap.put(pos, newOC);
        } else {
            existingOC.incorporate(newOC);
        }
    }
    public void recordOffsetChange(int line, int col, int lineOffsetChange, int colOffsetChange) {
        recordOffsetChange(new TextPosition(line, col), lineOffsetChange, colOffsetChange);
    }

    public void recordOffsetChange(int pos, int lineOffsetChange, int colOffsetChange) {
        recordOffsetChange(new TextPosition(pos), lineOffsetChange, colOffsetChange);
    }

    public PositionMap buildPositionMap() { //TODO-AC: verify correctness of map?
        SortedMap<TextPosition, Offset> offsetMap = new TreeMap<TextPosition, Offset>();
        Offset currOffset = new Offset(0, 0);
        //NB: must iterate in order of increasing position
        for(Map.Entry<TextPosition, OffsetChange> entry : offsetChangeMap.entrySet()) {
            currOffset.incorporate(entry.getValue());
            offsetMap.put(entry.getKey(), new Offset(currOffset));
        }
        return new OffsetPositionMap(offsetMap);
    }

    private static class OffsetChange {
        int lineOffsetChange;
        int colOffsetChange;

        OffsetChange(int lineOffsetChange, int colOffsetChange) {
            this.lineOffsetChange = lineOffsetChange;
            this.colOffsetChange = colOffsetChange;
        }

        OffsetChange(OffsetChange other) {
            this(other.lineOffsetChange, other.colOffsetChange);
        }

        void incorporate(OffsetChange other) {
            this.lineOffsetChange += other.lineOffsetChange;
            this.colOffsetChange += other.colOffsetChange;
        }
    }

    private static class Offset {
        int lineOffset;
        int colOffset;

        Offset(int lineOffset, int colOffset) {
            this.lineOffset = lineOffset;
            this.colOffset = colOffset;
        }

        Offset(Offset other) {
            this(other.lineOffset, other.colOffset);
        }

        void incorporate(OffsetChange change) {
            this.lineOffset += change.lineOffsetChange;
            this.colOffset += change.colOffsetChange;
        }
        
        TextPosition forwardOffset(TextPosition original) {
            return new TextPosition(original.getLine() + lineOffset, original.getColumn() + colOffset);
        }
        
        TextPosition reverseOffset(TextPosition original) {
            return new TextPosition(original.getLine() - lineOffset, original.getColumn() - colOffset);
        }
    }

    private static class OffsetPositionMap extends PositionMap {
        private final SortedMap<TextPosition, Offset> offsetMap;

        public OffsetPositionMap(SortedMap<TextPosition, Offset> offsetMap) {
            this.offsetMap = offsetMap;
        }

        @Override
        //TODO-AC: return null if the character has been deleted?
        public TextPosition sourceToDest(TextPosition source) {
            Offset offset = null;
            if(offsetMap.containsKey(source)) {
                offset = offsetMap.get(source);
            } else {
                SortedMap<TextPosition, Offset> headMap = offsetMap.headMap(source);
                offset = headMap.get(headMap.lastKey());
            }
            return offset.forwardOffset(source);
        }

        @Override
        //TODO-AC: return null if the character has been added?
        public TextPosition destToSource(TextPosition dest) {
            Offset offset = new Offset(0, 0);
            for(Map.Entry<TextPosition, Offset> entry : offsetMap.entrySet()) {
                TextPosition sourcePos = entry.getKey();
                Offset currOffset = entry.getValue();
                TextPosition destPos = currOffset.forwardOffset(sourcePos);
                int comp = destPos.compareTo(dest);
                if(comp == 0) {
                    return sourcePos;
                } else if(comp < 0) {
                    //not there yet
                    offset = currOffset;
                } else {
                    //too far - use previous, as stored in offset
                    return offset.reverseOffset(dest);
                }
            }
            //stepping off the end is the same as going too far
            return offset.reverseOffset(dest);
        }
    }
}
