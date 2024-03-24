import java.util.ArrayList;
import java.util.List;
import syntaxtree.*;

public class LabelledInstructionGatherer extends GJDepthFirst<Object, LabelledInstructions> {
  private static final boolean debug = false;
  private String currentLabel = null;
  private List<LabelledInstructionElement> instructionsUnderCurrentLabel = null;

  /** f0 -> ( FunctionDeclaration() )* f1 -> <EOF> */
  public Object visit(Program n, LabelledInstructions labelledInstructions) {
    if (debug) log("visiting program");
    n.f0.accept(this, labelledInstructions);
    n.f1.accept(this, labelledInstructions);
    return null;
  }

  /** f0 -> "func" f1 -> FunctionName() f2 -> "(" f3 -> ( Identifier() )* f4 -> ")" f5 -> Block() */
  public Object visit(FunctionDeclaration n, LabelledInstructions labelledInstructions) {
    if (debug) log("visiting program");
    n.f0.accept(this, labelledInstructions);
    n.f1.accept(this, labelledInstructions);
    n.f2.accept(this, labelledInstructions);
    n.f3.accept(this, labelledInstructions);
    n.f4.accept(this, labelledInstructions);
    n.f5.accept(this, labelledInstructions);
    return null;
  }

  /** f0 -> ( Instruction() )* f1 -> "return" f2 -> Identifier() */
  public Object visit(Block n, LabelledInstructions labelledInstructions) {
    if (debug) log("visiting block");
    n.f0.accept(this, labelledInstructions);
    n.f1.accept(this, labelledInstructions);
    String returnIdentifier = (String) n.f2.accept(this, labelledInstructions);
    this.addToInstructionsUnderCurrentLabel(returnIdentifier);
    this.gatherInstructionsUnderCurrentLabel(labelledInstructions);
    this.resetCurrentLabelAndInstructions();
    return null;
  }

  /**
   * f0 -> LabelWithColon() | SetInteger() | SetFuncName() | Add() | Subtract() | Multiply() |
   * LessThan() | Load() | Store() | Move() | Alloc() | Print() | ErrorMessage() | Goto() | IfGoto()
   * | Call()
   */
  public Object visit(Instruction n, LabelledInstructions labelledInstructions) {
    if (debug) log("visiting instruction");
    if (n.f0.which != 0) this.addToInstructionsUnderCurrentLabel(n);
    n.f0.accept(this, labelledInstructions);
    return null;
  }

  /** f0 -> Label() f1 -> ":" */
  public Object visit(LabelWithColon n, LabelledInstructions labelledInstructions) {
    if (debug) log("visiting label");
    this.gatherInstructionsUnderCurrentLabel(labelledInstructions);
    this.resetCurrentLabelAndInstructions();
    String label = (String) n.f0.accept(this, labelledInstructions);
    this.updateCurrentLabel(label);
    this.initializeInstructionList();
    n.f1.accept(this, labelledInstructions);
    return null;
  }

  /** f0 -> Identifier() f1 -> "=" f2 -> IntegerLiteral() */
  public Object visit(SetInteger n, LabelledInstructions labelledInstructions) {
    if (debug) log("visiting set int");
    n.f0.accept(this, labelledInstructions);
    n.f1.accept(this, labelledInstructions);
    n.f2.accept(this, labelledInstructions);
    return null;
  }

  /** f0 -> Identifier() f1 -> "=" f2 -> "@" f3 -> FunctionName() */
  public Object visit(SetFuncName n, LabelledInstructions labelledInstructions) {
    if (debug) log("visiting set func");
    n.f0.accept(this, labelledInstructions);
    n.f1.accept(this, labelledInstructions);
    n.f2.accept(this, labelledInstructions);
    n.f3.accept(this, labelledInstructions);
    return null;
  }

  /** f0 -> Identifier() f1 -> "=" f2 -> Identifier() f3 -> "+" f4 -> Identifier() */
  public Object visit(Add n, LabelledInstructions labelledInstructions) {
    if (debug) log("visiting set add");
    n.f0.accept(this, labelledInstructions);
    n.f1.accept(this, labelledInstructions);
    n.f2.accept(this, labelledInstructions);
    n.f3.accept(this, labelledInstructions);
    n.f4.accept(this, labelledInstructions);
    return null;
  }

  /** f0 -> Identifier() f1 -> "=" f2 -> Identifier() f3 -> "-" f4 -> Identifier() */
  public Object visit(Subtract n, LabelledInstructions labelledInstructions) {
    if (debug) log("visiting set sub");
    n.f0.accept(this, labelledInstructions);
    n.f1.accept(this, labelledInstructions);
    n.f2.accept(this, labelledInstructions);
    n.f3.accept(this, labelledInstructions);
    n.f4.accept(this, labelledInstructions);
    return null;
  }

  /** f0 -> Identifier() f1 -> "=" f2 -> Identifier() f3 -> "*" f4 -> Identifier() */
  public Object visit(Multiply n, LabelledInstructions labelledInstructions) {
    if (debug) log("visiting multiply");
    n.f0.accept(this, labelledInstructions);
    n.f1.accept(this, labelledInstructions);
    n.f2.accept(this, labelledInstructions);
    n.f3.accept(this, labelledInstructions);
    n.f4.accept(this, labelledInstructions);
    return null;
  }

  /** f0 -> Identifier() f1 -> "=" f2 -> Identifier() f3 -> "<" f4 -> Identifier() */
  public Object visit(LessThan n, LabelledInstructions labelledInstructions) {
    if (debug) log("visiting less than");
    n.f0.accept(this, labelledInstructions);
    n.f1.accept(this, labelledInstructions);
    n.f2.accept(this, labelledInstructions);
    n.f3.accept(this, labelledInstructions);
    n.f4.accept(this, labelledInstructions);
    return null;
  }

  /**
   * f0 -> Identifier() f1 -> "=" f2 -> "[" f3 -> Identifier() f4 -> "+" f5 -> IntegerLiteral() f6
   * -> "]"
   */
  public Object visit(Load n, LabelledInstructions labelledInstructions) {
    if (debug) log("visiting load");
    n.f0.accept(this, labelledInstructions);
    n.f1.accept(this, labelledInstructions);
    n.f2.accept(this, labelledInstructions);
    n.f3.accept(this, labelledInstructions);
    n.f4.accept(this, labelledInstructions);
    n.f5.accept(this, labelledInstructions);
    n.f6.accept(this, labelledInstructions);
    return null;
  }

  /**
   * f0 -> "[" f1 -> Identifier() f2 -> "+" f3 -> IntegerLiteral() f4 -> "]" f5 -> "=" f6 ->
   * Identifier()
   */
  public Object visit(Store n, LabelledInstructions labelledInstructions) {
    if (debug) log("visiting store");
    n.f0.accept(this, labelledInstructions);
    n.f1.accept(this, labelledInstructions);
    n.f2.accept(this, labelledInstructions);
    n.f3.accept(this, labelledInstructions);
    n.f4.accept(this, labelledInstructions);
    n.f5.accept(this, labelledInstructions);
    n.f6.accept(this, labelledInstructions);
    return null;
  }

  /** f0 -> Identifier() f1 -> "=" f2 -> Identifier() */
  public Object visit(Move n, LabelledInstructions labelledInstructions) {
    if (debug) log("visiting move");
    n.f0.accept(this, labelledInstructions);
    n.f1.accept(this, labelledInstructions);
    n.f2.accept(this, labelledInstructions);
    return null;
  }

  /** f0 -> Identifier() f1 -> "=" f2 -> "alloc" f3 -> "(" f4 -> Identifier() f5 -> ")" */
  public Object visit(Alloc n, LabelledInstructions labelledInstructions) {
    if (debug) log("visiting alloc");
    n.f0.accept(this, labelledInstructions);
    n.f1.accept(this, labelledInstructions);
    n.f2.accept(this, labelledInstructions);
    n.f3.accept(this, labelledInstructions);
    n.f4.accept(this, labelledInstructions);
    n.f5.accept(this, labelledInstructions);
    return null;
  }

  /** f0 -> "print" f1 -> "(" f2 -> Identifier() f3 -> ")" */
  public Object visit(Print n, LabelledInstructions labelledInstructions) {
    if (debug) log("visiting print");
    n.f0.accept(this, labelledInstructions);
    n.f1.accept(this, labelledInstructions);
    n.f2.accept(this, labelledInstructions);
    n.f3.accept(this, labelledInstructions);
    return null;
  }

  /** f0 -> "error" f1 -> "(" f2 -> StringLiteral() f3 -> ")" */
  public Object visit(ErrorMessage n, LabelledInstructions labelledInstructions) {
    if (debug) log("visiting error message");
    this.gatherInstructionsUnderCurrentLabel(labelledInstructions);
    this.resetCurrentLabelAndInstructions();
    n.f0.accept(this, labelledInstructions);
    n.f1.accept(this, labelledInstructions);
    n.f2.accept(this, labelledInstructions);
    n.f3.accept(this, labelledInstructions);
    return null;
  }

  /** f0 -> "goto" f1 -> Label() */
  public Object visit(Goto n, LabelledInstructions labelledInstructions) {
    if (debug) log("visiting label");
    n.f0.accept(this, labelledInstructions);
    n.f1.accept(this, labelledInstructions);
    return null;
  }

  /** f0 -> "if0" f1 -> Identifier() f2 -> "goto" f3 -> Label() */
  public Object visit(IfGoto n, LabelledInstructions labelledInstructions) {
    if (debug) log("visiting if goto");
    // this.resetCurrentLabelAndInstructionsList();
    n.f0.accept(this, labelledInstructions);
    n.f1.accept(this, labelledInstructions);
    n.f2.accept(this, labelledInstructions);
    n.f3.accept(this, labelledInstructions);
    return null;
  }

  /**
   * f0 -> Identifier() f1 -> "=" f2 -> "call" f3 -> Identifier() f4 -> "(" f5 -> ( Identifier() )*
   * f6 -> ")"
   */
  public Object visit(Call n, LabelledInstructions labelledInstructions) {
    if (debug) log("visiting call");
    n.f0.accept(this, labelledInstructions);
    n.f1.accept(this, labelledInstructions);
    n.f2.accept(this, labelledInstructions);
    n.f3.accept(this, labelledInstructions);
    n.f4.accept(this, labelledInstructions);
    n.f5.accept(this, labelledInstructions);
    n.f6.accept(this, labelledInstructions);
    return null;
  }

  /** f0 -> <IDENTIFIER> */
  public Object visit(FunctionName n, LabelledInstructions labelledInstructions) {
    if (debug) log("visiting function name");
    n.f0.accept(this, labelledInstructions);
    return null;
  }

  /** f0 -> <IDENTIFIER> */
  public Object visit(Label n, LabelledInstructions labelledInstructions) {
    if (debug) log("visiting label");
    n.f0.accept(this, labelledInstructions);
    return n.f0.toString();
  }

  /** f0 -> <IDENTIFIER> */
  public Object visit(Identifier n, LabelledInstructions labelledInstructions) {
    if (debug) log("visiting id");
    n.f0.accept(this, labelledInstructions);
    return n.f0.toString();
  }

  /** f0 -> <INTEGER_LITERAL> */
  public Object visit(IntegerLiteral n, LabelledInstructions labelledInstructions) {
    if (debug) log("visiting int literal");
    n.f0.accept(this, labelledInstructions);
    return null;
  }

  /** f0 -> <STRINGCONSTANT> */
  public Object visit(StringLiteral n, LabelledInstructions labelledInstructions) {
    if (debug) log("visiting string literal");
    n.f0.accept(this, labelledInstructions);
    return null;
  }

  private void log(String message) {
    System.out.println(message);
  }

  private void addToInstructionsUnderCurrentLabel(Instruction instruction) {
    if (this.currentLabel != null) {
      this.instructionsUnderCurrentLabel.add(new LabelledInstructionElement(instruction));
    }
  }

  private void addToInstructionsUnderCurrentLabel(String returnIdentifier) {
    if (this.currentLabel != null) {
      this.instructionsUnderCurrentLabel.add(new LabelledInstructionElement(returnIdentifier));
    }
  }

  private void updateCurrentLabel(String currentLabel) {
    this.currentLabel = currentLabel;
  }

  private void initializeInstructionList() {
    this.instructionsUnderCurrentLabel = new ArrayList<LabelledInstructionElement>();
  }

  private void resetCurrentLabelAndInstructions() {
    this.currentLabel = null;
    this.instructionsUnderCurrentLabel = null;
  }

  private void gatherInstructionsUnderCurrentLabel(LabelledInstructions labelledInstructions) {
    labelledInstructions.put(this.currentLabel, this.instructionsUnderCurrentLabel);
  }
}
