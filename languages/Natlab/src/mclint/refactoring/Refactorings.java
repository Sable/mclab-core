package mclint.refactoring;


public class Refactorings {
  public static Refactoring repmatToZeros() {
    return Refactoring.of("repmat(0, %x)", "zeros(%x)", Refactoring.Visit.Expressions);
  }

  public static Refactoring repmatToOnes() {
    return Refactoring.of("repmat(1, %x)", "ones(%x)", Refactoring.Visit.Expressions);
  }

  public static Refactoring dispSprintfToFprintf() {
    return Refactoring.of("disp(sprintf(%x))", "fprintf(%x)", Refactoring.Visit.Expressions);
  }
  
  public static Refactoring lengthEqZeroToIsempty() {
    return Refactoring.of("(length(%x) == 0)", "isempty(%x)", Refactoring.Visit.Expressions);
  }

  public static Refactoring lengthNeqZeroToNotIsempty() {
    return Refactoring.of("(length(%x) ~= 0)", "~isempty(%x)", Refactoring.Visit.Expressions);
  }
}
