package fir.ast;

import java.util.Vector;

public class Block extends Stmt {
	public final Vector<Stmt> stmts;
	
	public Block(){
		stmts = new Vector<Stmt>();		
	}
	
	public Stmt getStatement(int index){
		return stmts.get(index);
	}
	public void setStatement(int index,Stmt stmt){
		stmts.set(index, stmt);
	}
	public void addStatement(Stmt stmt){
		stmts.add(stmt);
	}
	public void insertStatement(int index,Stmt stmt){
		stmts.insertElementAt(stmt,index);
	}
	public int lastIndex(){
		return stmts.lastIndexOf(stmts.lastElement());
	}
}
