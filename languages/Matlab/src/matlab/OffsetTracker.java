package matlab;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * A class for building up a PositionMap by keeping track of the offset required
 * to get from the position in the post-translation file (i.e. Natlab) to the
 * position in the pre-translation file (i.e. Matlab).
 * 
 * For example, if 'a' appears at (1,2) in the original file but is shifted to
 * (1, 4) in the translated file, then the offset required to get back is (0,-2).
 * In the absence of other information, it is assumed that this same offset
 * applies to the following character as well.
 */
public class OffsetTracker {

    //Marks changes to the cumulative offset and the positions at which they
    //occurred.  For example, if the offset up to (1,10) was (0,-3) and then 
    //it changed to (0,-2), the map would contain an entry <(1,10),(0,1)>.
    //NB: we assume later that this map is sorted.
    private final SortedMap<TextPosition, OffsetChange> offsetChangeMap;
    //The current position in the post-translation file
    private TextPosition currPos;

    /**
     * Create a new OffsetTracker with a starting position (usually (1,1)).
     */
    public OffsetTracker(TextPosition basePos) {
        this.offsetChangeMap = new TreeMap<TextPosition, OffsetChange>();
        this.currPos = basePos;
    }

    /** 
     * Advance the position in the post-translation file.
     * e.g. (1, 3) -> advanceInLine(2) -> (1, 5)
     */
    public void advanceInLine(int numCols) {
        if(numCols < 0) {
            throw new IllegalArgumentException("Attempted to move backwards in line: " + numCols);
        }
        currPos = new TextPosition(currPos.getLine(), currPos.getColumn() + numCols);
    }

    /** 
     * Advance the position in the post-translation file.
     * e.g. (1, 3) -> advanceToNewLine(2, 4) -> (3, 4)
     */
    public void advanceToNewLine(int numLines, int newCol) {
        if(numLines < 1) {
            throw new IllegalArgumentException("Attempted to move to an earlier line: " + numLines);
        } else if(newCol < 1) {
            throw new IllegalArgumentException("Attempted to move to an invalid column: " + newCol);
        }
        currPos = new TextPosition(currPos.getLine() + numLines, newCol);
    }

    /** 
     * Advance the position in the post-translation file.
     * e.g. (1, 3) -> advanceByTextSize(blah\nblah\nblah) -> (3, 4)
     * NB: slower than specifying directly using advanceInLine or advanceToNewLine
     */
    public void advanceByTextSize(String text) {
        TextPosition eofPos = LengthScanner.getLength(text);
        if(eofPos.getLine() == 1) {
            advanceInLine(eofPos.getColumn() - 1);
        } else {
            advanceToNewLine(eofPos.getLine() - 1, eofPos.getColumn());
        }
    }

    /**
     * Record a change to the cumulative offset, beginning at the current position.
     */
    public void recordOffsetChange(int lineOffsetChange, int colOffsetChange) {
        if(lineOffsetChange == 0 && colOffsetChange == 0) {
            return;
        }
        //System.err.println(currPos + ": recordOffsetChange(" + lineOffsetChange + ", " + colOffsetChange + ")");
        OffsetChange existingOC = offsetChangeMap.get(currPos);
        OffsetChange newOC = new OffsetChange(lineOffsetChange, colOffsetChange);
        if(existingOC == null) {
            offsetChangeMap.put(currPos, newOC);
        } else {
            existingOC.incorporate(newOC);
        }
    }

    /**
     * Record a change to the cumulative offset, beginning at the current position.
     */
    public void recordOffsetChange(String text, int startPos, boolean insert) {
        TextPosition eofPos = LengthScanner.getLength(text);
        int lineOffsetChange = -1;
        int colOffsetChange = -1;
        if(eofPos.getLine() == 1) {
            lineOffsetChange = 0;
            colOffsetChange = eofPos.getColumn() - 1;
        } else {
            lineOffsetChange = eofPos.getLine() - 1;
            colOffsetChange = eofPos.getColumn() - startPos;
        }
        if(insert) {
            recordOffsetChange(-1 * lineOffsetChange, -1 * colOffsetChange);
        } else {
            recordOffsetChange(lineOffsetChange, colOffsetChange);
        }
    }

    /**
     * Convert the list of changes to the cumulative offset into a list of
     * cumulative offsets and build a map from them.
     */
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
            offsetMap.put(pos, new Offset(accumulatedLineOffset, lineAccumulatedColOffset));
            offsetMap.put(new TextPosition(pos.getLine() + 1, 1), new Offset(accumulatedLineOffset, 0)); //expect this to be overridden
        }
        return new OffsetPositionMap(offsetMap);
    }

    /* A change to the cumulative offset. */
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

        public String toString() {
            return "<|" + lineOffsetChange + ", " + colOffsetChange + "|>";
        }
    }

    /* A cumulative offset. */
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

        public String toString() {
            return "<" + lineOffset + ", " + colOffset + ">";
        }
    }

    /*
     * An implementation of PositionMap.
     * Essentially a run-length encoding.  Consists of an ordered list of
     * positions, each associated with a cumulative offset that lasts until
     * the next position.
     */
    private static class OffsetPositionMap extends PositionMap {
        private final SortedMap<TextPosition, Offset> offsetMap;

        public OffsetPositionMap(SortedMap<TextPosition, Offset> offsetMap) {
            this.offsetMap = offsetMap;
        }

        @Override
        public TextPosition getPreTranslationPosition(TextPosition source) {
            //find the most recent change to the offset and add it to the
            //position
            Offset offset = null;
            if(offsetMap.containsKey(source)) { //offset change starts at the specified position
                offset = offsetMap.get(source);
            } else {
                SortedMap<TextPosition, Offset> headMap = offsetMap.headMap(source);
                if(headMap.isEmpty()) { //no offset change occurs before the position
                    offset = new Offset(0, 0);
                } else { //offset change strictly precedes the position
                    offset = headMap.get(headMap.lastKey());
                }
            }
            return offset.forwardOffset(source);
        }
    }
}
