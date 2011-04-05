package natlab;

import beaver.Symbol;

//A queue of comments shared by the scanner and the parser.
public class CommentBuffer {
	private final java.util.Queue<Symbol> commentQueue;

	public CommentBuffer() {
		this.commentQueue = new java.util.LinkedList<Symbol>();
	}
	
	public void info(Symbol stmt){
	    Symbol comment = peekComment();
	    System.err.println("info:");
        System.err.println(comment.getLine(comment.getStart())+" "+comment.getLine(comment.getEnd()));
        System.err.println(comment.getLine(stmt.getEnd())+" "+comment.getLine(stmt.getEnd()));
	    System.err.println(stmt.getClass().getName());
	}

	public void pushComment(Symbol comment) {
		commentQueue.add(comment);
	}

	public Symbol peekComment() {
		return commentQueue.peek();
	}

	public Symbol pollComment() {
		return commentQueue.poll();
	}
	
	public boolean isEmpty(){
	    return commentQueue.isEmpty();
	}

	public java.util.List<Symbol> pollAllComments() {
		java.util.List<Symbol> allComments = new java.util.ArrayList<Symbol>();
		allComments.addAll(commentQueue);
		commentQueue.clear();
		return allComments;
	}
}
