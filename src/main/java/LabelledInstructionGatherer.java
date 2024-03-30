import java.util.ArrayList;
import java.util.List;
import syntaxtree.*;

public class LabelledInstructionGatherer extends GJDepthFirst<Object, Heap> {
  private static final boolean debug = false;
  private String currentLabel = new String("");
  private List<LabelledInstruction> instructionsUnderCurrentFunction = null;
  private String currentFunction = null;

  /** f0 -> ( FunctionDeclaration() )* f1 -> <EOF> */
  public Object visit(Program n, Heap heap) {
    if (debug) Log.log("visiting program");
    n.f0.accept(this, heap);
    n.f1.accept(this, heap);
    return null;
  }

  /** f0 -> "func" f1 -> FunctionName() f2 -> "(" f3 -> ( Identifier() )* f4 -> ")" f5 -> Block() */
  public Object visit(FunctionDeclaration n, Heap heap) {
    if (debug) Log.log("visiting program");
    this.initInstructionsUnderCurrentFunction();
    this.resetCurrentLabel();
    n.f0.accept(this, heap);
    String functionName = (String) n.f1.accept(this, heap);
    this.updateCurrentFunction(functionName);
    n.f2.accept(this, heap);
    n.f3.accept(this, heap);
    this.gatherFunctionParams(heap, n.f3.nodes, functionName);
    n.f4.accept(this, heap);
    n.f5.accept(this, heap);
    return null;
  }

  /** f0 -> ( Instruction() )* f1 -> "return" f2 -> Identifier() */
  public Object visit(Block n, Heap heap) {
    if (debug) Log.log("visiting block");
    n.f0.accept(this, heap);
    n.f1.accept(this, heap);
    String returnIdentifier = (String) n.f2.accept(this, heap);
    this.addToInstrutionsUnderCurrentFunction(returnIdentifier);
    this.addInstructionsUnderCurrentFunctionToHeap(heap);
    return null;
  }

  /**
   * f0 -> LabelWithColon() | SetInteger() | SetFuncName() | Add() | Subtract() | Multiply() |
   * LessThan() | Load() | Store() | Move() | Alloc() | Print() | ErrorMessage() | Goto() | IfGoto()
   * | Call()
   */
  public Object visit(Instruction n, Heap heap) {
    if (debug) Log.log("visiting instruction");
    // if the instruction is not decleration of new label add to instructions.
    if (n.f0.which != 0) this.addToInstrutionsUnderCurrentFunction(n);
    n.f0.accept(this, heap);
    return null;
  }

  /** f0 -> Label() f1 -> ":" */
  public Object visit(LabelWithColon n, Heap heap) {
    if (debug) Log.log("visiting label");
    String label = (String) n.f0.accept(this, heap);
    this.updateCurrentLabel(label);
    n.f1.accept(this, heap);
    return null;
  }

  /** f0 -> Identifier() f1 -> "=" f2 -> IntegerLiteral() */
  public Object visit(SetInteger n, Heap heap) {
    if (debug) Log.log("visiting set int");
    n.f0.accept(this, heap);
    n.f1.accept(this, heap);
    n.f2.accept(this, heap);
    return null;
  }

  /** f0 -> Identifier() f1 -> "=" f2 -> "@" f3 -> FunctionName() */
  public Object visit(SetFuncName n, Heap heap) {
    if (debug) Log.log("visiting set func");
    n.f0.accept(this, heap);
    n.f1.accept(this, heap);
    n.f2.accept(this, heap);
    n.f3.accept(this, heap);
    return null;
  }

  /** f0 -> Identifier() f1 -> "=" f2 -> Identifier() f3 -> "+" f4 -> Identifier() */
  public Object visit(Add n, Heap heap) {
    if (debug) Log.log("visiting set add");
    n.f0.accept(this, heap);
    n.f1.accept(this, heap);
    n.f2.accept(this, heap);
    n.f3.accept(this, heap);
    n.f4.accept(this, heap);
    return null;
  }

  /** f0 -> Identifier() f1 -> "=" f2 -> Identifier() f3 -> "-" f4 -> Identifier() */
  public Object visit(Subtract n, Heap heap) {
    if (debug) Log.log("visiting set sub");
    n.f0.accept(this, heap);
    n.f1.accept(this, heap);
    n.f2.accept(this, heap);
    n.f3.accept(this, heap);
    n.f4.accept(this, heap);
    return null;
  }

  /** f0 -> Identifier() f1 -> "=" f2 -> Identifier() f3 -> "*" f4 -> Identifier() */
  public Object visit(Multiply n, Heap heap) {
    if (debug) Log.log("visiting multiply");
    n.f0.accept(this, heap);
    n.f1.accept(this, heap);
    n.f2.accept(this, heap);
    n.f3.accept(this, heap);
    n.f4.accept(this, heap);
    return null;
  }

  /** f0 -> Identifier() f1 -> "=" f2 -> Identifier() f3 -> "<" f4 -> Identifier() */
  public Object visit(LessThan n, Heap heap) {
    if (debug) Log.log("visiting less than");
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
    if (debug) Log.log("visiting load");
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
    if (debug) Log.log("visiting store");
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
    if (debug) Log.log("visiting move");
    n.f0.accept(this, heap);
    n.f1.accept(this, heap);
    n.f2.accept(this, heap);
    return null;
  }

  /** f0 -> Identifier() f1 -> "=" f2 -> "alloc" f3 -> "(" f4 -> Identifier() f5 -> ")" */
  public Object visit(Alloc n, Heap heap) {
    if (debug) Log.log("visiting alloc");
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
    if (debug) Log.log("visiting print");
    n.f0.accept(this, heap);
    n.f1.accept(this, heap);
    n.f2.accept(this, heap);
    n.f3.accept(this, heap);
    return null;
  }

  /** f0 -> "error" f1 -> "(" f2 -> StringLiteral() f3 -> ")" */
  public Object visit(ErrorMessage n, Heap heap) {
    if (debug) Log.log("visiting error message");
    n.f0.accept(this, heap);
    n.f1.accept(this, heap);
    n.f2.accept(this, heap);
    n.f3.accept(this, heap);
    return null;
  }

  /** f0 -> "goto" f1 -> Label() */
  public Object visit(Goto n, Heap heap) {
    if (debug) Log.log("visiting label");
    n.f0.accept(this, heap);
    n.f1.accept(this, heap);
    return null;
  }

  /** f0 -> "if0" f1 -> Identifier() f2 -> "goto" f3 -> Label() */
  public Object visit(IfGoto n, Heap heap) {
    if (debug) Log.log("visiting if goto");
    // this.resetCurrentLabelAndInstructionsList();
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
    if (debug) Log.log("visiting call");
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
    if (debug) Log.log("visiting function name");
    n.f0.accept(this, heap);
    return n.f0.toString();
  }

  /** f0 -> <IDENTIFIER> */
  public Object visit(Label n, Heap heap) {
    if (debug) Log.log("visiting label");
    n.f0.accept(this, heap);
    return n.f0.toString();
  }

  /** f0 -> <IDENTIFIER> */
  public Object visit(Identifier n, Heap heap) {
    if (debug) Log.log("visiting id");
    n.f0.accept(this, heap);
    return n.f0.toString();
  }

  /** f0 -> <INTEGER_LITERAL> */
  public Object visit(IntegerLiteral n, Heap heap) {
    if (debug) Log.log("visiting int literal");
    n.f0.accept(this, heap);
    return null;
  }

  /** f0 -> <STRINGCONSTANT> */
  public Object visit(StringLiteral n, Heap heap) {
    if (debug) Log.log("visiting string literal");
    n.f0.accept(this, heap);
    return n.f0.toString();
  }

  private void gatherFunctionParams(Heap heap, List<Node> params, String functionName) {
    List<String> funParams = new ArrayList<String>();
    for (Node param : params) {
      String paramName = (String) param.accept(this, heap);
      funParams.add(paramName);
    }
    heap.rememberFunParams(functionName, funParams);
  }

  private void updateCurrentFunction(String functionName) {
    this.currentFunction = functionName;
  }

  private void addToInstrutionsUnderCurrentFunction(Instruction instruction) {
    this.instructionsUnderCurrentFunction.add(
        new LabelledInstruction(this.currentLabel, new InstructionUnit(instruction)));
  }

  private void addToInstrutionsUnderCurrentFunction(String returnId) {
    this.instructionsUnderCurrentFunction.add(
        new LabelledInstruction(this.currentLabel, new InstructionUnit(returnId)));
  }

  private void initInstructionsUnderCurrentFunction() {
    this.instructionsUnderCurrentFunction = new ArrayList<LabelledInstruction>();
  }

  private void updateCurrentLabel(String label) {
    this.currentLabel = label;
  }

  private void resetCurrentLabel() {
    this.currentLabel = new String("");
  }

  private void addInstructionsUnderCurrentFunctionToHeap(Heap heap) {
    if (this.currentFunction == null || this.instructionsUnderCurrentFunction.size() <= 0) {
      this.failWithMessage("function name and instruction cannot be empty while adding to heap");
    }
    heap.addFunctionInstructions(this.currentFunction, this.instructionsUnderCurrentFunction);
  }

  private void failWithMessage(String message) {
    Log.log(message);
    System.exit(1);
  }
}
