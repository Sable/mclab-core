package matlab;

abstract class CommandToken {
    private int line;
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

    static class Arg extends CommandToken {
        private StringBuffer argBuf;
        private int quoteCount;

        Arg() {
            this.argBuf = new StringBuffer();
            this.quoteCount = 0;
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

        public void incrQuoteCount() {
            quoteCount++;
        }

        public boolean isQuoteCountOdd() {
            return (quoteCount % 2) == 1;
        }

        public boolean isPrevTextCharQuote() {
            return getText().endsWith("'");
        }
    }

    static class EllipsisComment extends CommandToken {
        EllipsisComment(String text) {
            appendText(text);
        }
    }
}
