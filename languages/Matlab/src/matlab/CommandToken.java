package matlab;

/**
 * From the text of a command style invocation: either an argument (after 
 * re-scanning) or an ellipsis comment.
 */
abstract class CommandToken {
    private int line; //note: appears entirely on one line
    private int startCol;
    private int endCol;
    private StringBuffer textBuf;

    private CommandToken() {
        this.line = -1;
        this.startCol = -1;
        this.endCol = -1;
        this.textBuf = new StringBuffer();
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public int getStartCol() {
        return startCol;
    }

    public void setStartCol(int startCol) {
        this.startCol = startCol;
    }

    public int getEndCol() {
        return endCol;
    }

    public void setEndCol(int endCol) {
        this.endCol = endCol;
    }

    public void appendText(String text) {
        textBuf.append(text);
    }

    public String getText() {
        return textBuf.toString();
    }

    public boolean isArg() {
        return false;
    }

    /**
     * An argument to a command style call.  Built up by CommandScanner.
     */
    static class Arg extends CommandToken {
        private StringBuffer argBuf;

        Arg() {
            this.argBuf = new StringBuffer();
        }

        public void appendArgText(String argText) {
            argBuf.append(argText);
        }

        public String getArgText() {
            return argBuf.toString();
        }

        public boolean isArgTextEmpty() {
            return argBuf.length() == 0;
        }

        public boolean isArg() {
            return true;
        }
    }

    /**
     * An ellipsis comment between arguments to a command style call.
     * Built up by CommandScanner.
     */
    static class EllipsisComment extends CommandToken {
        EllipsisComment(String text) {
            appendText(text);
        }
    }

    /**
     * Whitespace between arguments to a command style call.
     * No linebreaks.
     * Built up by CommandScanner.
     */
    static class InlineWhitespace extends CommandToken {
        InlineWhitespace(String text) {
            appendText(text);
        }
        public int getLength() {
            return getEndCol() - getStartCol() + 1;
        }
    }
}
