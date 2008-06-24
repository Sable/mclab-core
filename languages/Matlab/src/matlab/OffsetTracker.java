package matlab;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class OffsetTracker {

    private final Map<TextPosition, OffsetChange> offsetChangeMap;
    private TextPosition currPos;

    public OffsetTracker(TextPosition basePos) {
        this.offsetChangeMap = new TreeMap<TextPosition, OffsetChange>();
        this.currPos = basePos;
    }

    public void advanceInLine(int numCols) {
        if(numCols < 0) {
            throw new IllegalArgumentException("Attempted to move backwards in line: " + numCols);
        }
        currPos = new TextPosition(currPos.getLine(), currPos.getColumn() + numCols);
    }

    public void advanceToNewLine(int numLines, int newCol) {
        if(numLines < 1) {
            throw new IllegalArgumentException("Attempted to move to an earlier line: " + numLines);
        } else if(newCol < 1) {
            throw new IllegalArgumentException("Attempted to move to an invalid column: " + newCol);
        }
        currPos = new TextPosition(currPos.getLine() + numLines, newCol);
    }

    public void recordOffsetChange(int lineOffsetChange, int colOffsetChange) {
        if(lineOffsetChange == 0 && colOffsetChange == 0) {
            return;
        }
        OffsetChange existingOC = offsetChangeMap.get(currPos);
        OffsetChange newOC = new OffsetChange(lineOffsetChange, colOffsetChange);
        if(existingOC == null) {
            offsetChangeMap.put(currPos, newOC);
        } else {
            existingOC.incorporate(newOC);
        }
    }

    public PositionMap buildPositionMap() { //TODO-AC: verify correctness of map?
        int accumulatedLineOffset = 0;
        int line = -1;
        int lineAccumulatedColOffset = 0;
        SortedMap<TextPosition, Offset> offsetMap = new TreeMap<TextPosition, Offset>();
        //NB: must iterate in order of increasing position
        for(Map.Entry<TextPosition, OffsetChange> entry : offsetChangeMap.entrySet()) {
            TextPosition pos = entry.getKey();
            if(line != pos.getLine()) {
                line = pos.getLine();
                lineAccumulatedColOffset = 0;
            }
            OffsetChange offsetChange = entry.getValue();
            accumulatedLineOffset += offsetChange.lineOffsetChange;
            lineAccumulatedColOffset += offsetChange.colOffsetChange;
            offsetMap.put(entry.getKey(), new Offset(accumulatedLineOffset, lineAccumulatedColOffset));
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
        public TextPosition getPreTranslationPosition(TextPosition source) {
            Offset offset = null;
            if(offsetMap.containsKey(source)) {
                offset = offsetMap.get(source);
            } else {
                SortedMap<TextPosition, Offset> headMap = offsetMap.headMap(source);
                if(headMap.isEmpty()) {
                    offset = new Offset(0, 0);
                } else {
                    offset = headMap.get(headMap.lastKey());
                }
            }
            return offset.forwardOffset(source);
        }
    }
}
