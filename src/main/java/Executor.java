import java.util.*;
import syntaxtree.*;
import visitor.*;

public class Executor extends GJDepthFirst<Object, Heap> {
  private static final boolean debug = false;
  private String currentFunction = null;

  /** f0 -> ( FunctionDeclaration() )* f1 -> <EOF> */
  public Object visit(Program n, Heap heap) {
    n.f0.accept(this, heap);
    n.f1.accept(this, heap);
    return null;
  }

  /** f0 -> "func" f1 -> FunctionName() f2 -> "(" f3 -> ( Identifier() )* f4 -> ")" f5 -> Block() */
  public Object visit(FunctionDeclaration n, Heap heap) {
    if (debug) log("visiting function declaration");
    n.f0.accept(this, heap);
    String functionName = (String) n.f1.accept(this, heap);
    this.updateCurrentFunction(functionName);
    heap.createNewFunctionScope(functionName);
    n.f2.accept(this, heap);
    n.f3.accept(this, heap);
    n.f4.accept(this, heap);
    n.f5.accept(this, heap);
    return null;
  }

  /** f0 -> ( Instruction() )* f1 -> "return" f2 -> Identifier() */
  public Object visit(Block n, Heap heap) {
    n.f0.accept(this, heap);
    n.f1.accept(this, heap);
    n.f2.accept(this, heap);
    return null;
  }

  /**
   * f0 -> LabelWithColon() | SetInteger() | SetFuncName() | Add() | Subtract() | Multiply() |
   * LessThan() | Load() | Store() | Move() | Alloc() | Print() | ErrorMessage() | Goto() | IfGoto()
   * | Call()
   */
  public Object visit(Instruction n, Heap heap) {
    n.f0.accept(this, heap);
    return null;
  }

  /** f0 -> Label() f1 -> ":" */
  public Object visit(LabelWithColon n, Heap heap) {
    n.f0.accept(this, heap);
    n.f1.accept(this, heap);
    return null;
  }

  /** f0 -> Identifier() f1 -> "=" f2 -> IntegerLiteral() */
  public Object visit(SetInteger n, Heap heap) {
    String varName = (String) n.f0.accept(this, heap);
    n.f1.accept(this, heap);
    String intValueImage = (String) n.f2.accept(this, heap);
    VariableType type = VariableType.INTEGER;
    this.putVarInMemory(heap, varName, new MemoryUnit(intValueImage, type));
    return null;
  }

  /** f0 -> Identifier() f1 -> "=" f2 -> "@" f3 -> FunctionName() */
  public Object visit(SetFuncName n, Heap heap) {
    String varName = (String) n.f0.accept(this, heap);
    n.f1.accept(this, heap);
    n.f2.accept(this, heap);
    String funName = (String) n.f3.accept(this, heap);
    this.putVarInMemory(heap, varName, new MemoryUnit(funName, VariableType.FUNCTION));
    return null;
  }

  /** f0 -> Identifier() f1 -> "=" f2 -> Identifier() f3 -> "+" f4 -> Identifier() */
  public Object visit(Add n, Heap heap) {
    n.f0.accept(this, heap);
    n.f1.accept(this, heap);
    n.f2.accept(this, heap);
    n.f3.accept(this, heap);
    n.f4.accept(this, heap);
    return null;
  }

  /** f0 -> Identifier() f1 -> "=" f2 -> Identifier() f3 -> "-" f4 -> Identifier() */
  public Object visit(Subtract n, Heap heap) {
    n.f0.accept(this, heap);
    n.f1.accept(this, heap);
    n.f2.accept(this, heap);
    n.f3.accept(this, heap);
    n.f4.accept(this, heap);
    return null;
  }

  /** f0 -> Identifier() f1 -> "=" f2 -> Identifier() f3 -> "*" f4 -> Identifier() */
  public Object visit(Multiply n, Heap heap) {
    n.f0.accept(this, heap);
    n.f1.accept(this, heap);
    n.f2.accept(this, heap);
    n.f3.accept(this, heap);
    n.f4.accept(this, heap);
    return null;
  }

  /** f0 -> Identifier() f1 -> "=" f2 -> Identifier() f3 -> "<" f4 -> Identifier() */
  public Object visit(LessThan n, Heap heap) {
    n.f0.accept(this, heap);
    n.f1.accept(this, heap);
    n.f2.accept(this, heap);
    n.f3.accept(this, heap);
    n.f4.accept(this, heap);
    return null;
  }

  /**
   * f0 -> Identifier() f1 -> "=" f2 -> "[" f3 -> Identifier() f4 -> "+" f5 -> IntegerLiteral() f6
   * -> "]"
   */
  public Object visit(Load n, Heap heap) {
    n.f0.accept(this, heap);
    n.f1.accept(this, heap);
    n.f2.accept(this, heap);
    n.f3.accept(this, heap);
    n.f4.accept(this, heap);
    n.f5.accept(this, heap);
    n.f6.accept(this, heap);
    return null;
  }

  /**
   * f0 -> "[" f1 -> Identifier() f2 -> "+" f3 -> IntegerLiteral() f4 -> "]" f5 -> "=" f6 ->
   * Identifier()
   */
  public Object visit(Store n, Heap heap) {
    n.f0.accept(this, heap);
    n.f1.accept(this, heap);
    n.f2.accept(this, heap);
    n.f3.accept(this, heap);
    n.f4.accept(this, heap);
    n.f5.accept(this, heap);
    n.f6.accept(this, heap);
    return null;
  }

  /** f0 -> Identifier() f1 -> "=" f2 -> Identifier() */
  public Object visit(Move n, Heap heap) {
    n.f0.accept(this, heap);
    n.f1.accept(this, heap);
    n.f2.accept(this, heap);
    return null;
  }

  /** f0 -> Identifier() f1 -> "=" f2 -> "alloc" f3 -> "(" f4 -> Identifier() f5 -> ")" */
  public Object visit(Alloc n, Heap heap) {
    n.f0.accept(this, heap);
    n.f1.accept(this, heap);
    n.f2.accept(this, heap);
    n.f3.accept(this, heap);
    n.f4.accept(this, heap);
    n.f5.accept(this, heap);
    return null;
  }

  /** f0 -> "print" f1 -> "(" f2 -> Identifier() f3 -> ")" */
  public Object visit(Print n, Heap heap) {
    n.f0.accept(this, heap);
    n.f1.accept(this, heap);
    n.f2.accept(this, heap);
    n.f3.accept(this, heap);
    return null;
  }

  /** f0 -> "error" f1 -> "(" f2 -> StringLiteral() f3 -> ")" */
  public Object visit(ErrorMessage n, Heap heap) {
    n.f0.accept(this, heap);
    n.f1.accept(this, heap);
    String errMessage = (String) n.f2.accept(this, heap);
    // this.exitProgramWithErrorMessage(errMessage);
    n.f3.accept(this, heap);
    return null;
  }

  /** f0 -> "goto" f1 -> Label() */
  public Object visit(Goto n, Heap heap) {
    n.f0.accept(this, heap);
    n.f1.accept(this, heap);
    return null;
  }

  /** f0 -> "if0" f1 -> Identifier() f2 -> "goto" f3 -> Label() */
  public Object visit(IfGoto n, Heap heap) {
    n.f0.accept(this, heap);
    n.f1.accept(this, heap);
    n.f2.accept(this, heap);
    n.f3.accept(this, heap);
    return null;
  }

  /**
   * f0 -> Identifier() f1 -> "=" f2 -> "call" f3 -> Identifier() f4 -> "(" f5 -> ( Identifier() )*
   * f6 -> ")"
   */
  public Object visit(Call n, Heap heap) {
    n.f0.accept(this, heap);
    n.f1.accept(this, heap);
    n.f2.accept(this, heap);
    n.f3.accept(this, heap);
    n.f4.accept(this, heap);
    n.f5.accept(this, heap);
    n.f6.accept(this, heap);
    return null;
  }

  /** f0 -> <IDENTIFIER> */
  public Object visit(FunctionName n, Heap heap) {
    if (debug) log("visiting function name");
    n.f0.accept(this, heap);
    return n.f0.toString();
  }

  /** f0 -> <IDENTIFIER> */
  public Object visit(Label n, Heap heap) {
    n.f0.accept(this, heap);
    return null;
  }

  /** f0 -> <IDENTIFIER> */
  public Object visit(Identifier n, Heap heap) {
    if (debug) log("visiting id");
    n.f0.accept(this, heap);
    return n.f0.toString();
  }

  /** f0 -> <INTEGER_LITERAL> */
  public Object visit(IntegerLiteral n, Heap heap) {
    n.f0.accept(this, heap);
    return n.f0.toString();
  }

  /** f0 -> <STRINGCONSTANT> */
  public Object visit(StringLiteral n, Heap heap) {
    n.f0.accept(this, heap);
    return null;
  }

  private void log(String message) {
    System.out.println(message);
  }

  private void updateCurrentFunction(String functionName) {
    this.currentFunction = functionName;
  }

  private void putVarInMemory(Heap heap, String varName, MemoryUnit memUnit) {
    MemoryBlock memBlock = new MemoryBlock();
    memBlock.addMemoryUnit(memUnit);
    heap.addVarToScope(this.currentFunction, varName, memBlock);
  }

  private void exitProgramWithErrorMessage(String message) {
    Log.log(message);
    System.exit(0);
  }

}
