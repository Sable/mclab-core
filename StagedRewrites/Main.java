import AST.*;

public class Main {
  public static void main(String[] args) {
    Program p = new Program(new A());
    System.out.println("p.getStmt().string(): " + p.getStmt().string());
    System.out.println("p.string(): " + p.string());
    p.enterRewritePhase(2);
    System.out.println("p.getStmt().string() in phase 2: " + p.getStmt().string());
    System.out.println("p.string() in phase 2: " + p.string());
    p.flushCaches();
    System.out.println("p.string() in phase 2 after flushing caches: " + p.string());
    p.enterRewritePhase(3);
    System.out.println("p.getStmt().string() in phase 3: " + p.getStmt().string());
  }
}
