import java.util.*;
import syntaxtree.*;
import visitor.*;

public class Executor extends GJDepthFirst<Object, Heap> {
  private static final boolean debug = false;
  private String currentFunction = null;
  private List<MemoryUnit> calledFunctionParams = null;
  private String parentFunctionName = null;
  private String parentFuncAssigneeVarName = null;

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
    if (this.isTheFuncitonDeclaredIsNotMain(functionName)) {
      this.mapDeclaredParamsToCalledParams(heap, n.f3.nodes);
      this.resetCalledFunctionParams();
    }
    n.f3.accept(this, heap);
    n.f4.accept(this, heap);
    String returnVarName = (String) n.f5.accept(this, heap);
    if (this.isTheFuncitonDeclaredIsNotMain(functionName)) {
      this.returnValueFromThisFunc(heap, returnVarName);
    }
    return null;
  }

  /** f0 -> ( Instruction() )* f1 -> "return" f2 -> Identifier() */
  public Object visit(Block n, Heap heap) {
    n.f0.accept(this, heap);
    n.f1.accept(this, heap);
    return (String) n.f2.accept(this, heap);
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
    String resultVar = (String) n.f0.accept(this, heap);
    n.f1.accept(this, heap);
    String operand1 = (String) n.f2.accept(this, heap);
    n.f3.accept(this, heap);
    String operand2 = (String) n.f4.accept(this, heap);
    MemoryUnit operandUnit1 = this.getMemoryUnitFromScope(heap, this.currentFunction, operand1);
    MemoryUnit operandUnit2 = this.getMemoryUnitFromScope(heap, this.currentFunction, operand2);
    String resultImage = this.addIntMemoryUnitsAndReturnResult(operandUnit1, operandUnit2);
    this.putVarInMemory(heap, resultVar, new MemoryUnit(resultImage, VariableType.INTEGER));
    return null;
  }

  /** f0 -> Identifier() f1 -> "=" f2 -> Identifier() f3 -> "-" f4 -> Identifier() */
  public Object visit(Subtract n, Heap heap) {
    String varAssignName = (String) n.f0.accept(this, heap);
    n.f1.accept(this, heap);
    String operand1 = (String) n.f2.accept(this, heap);
    n.f3.accept(this, heap);
    String operand2 = (String) n.f4.accept(this, heap);
    MemoryUnit operandUnit1 = this.getMemoryUnitFromScope(heap, this.currentFunction, operand1);
    MemoryUnit operandUnit2 = this.getMemoryUnitFromScope(heap, this.currentFunction, operand2);
    String resultImage = this.subtractIntMemroyUnitAndReturnResult(operandUnit1, operandUnit2);
    this.putVarInMemory(heap, varAssignName, new MemoryUnit(resultImage, VariableType.INTEGER));
    return null;
  }

  /** f0 -> Identifier() f1 -> "=" f2 -> Identifier() f3 -> "*" f4 -> Identifier() */
  public Object visit(Multiply n, Heap heap) {
    String varAssignName = (String) n.f0.accept(this, heap);
    n.f1.accept(this, heap);
    String operand1 = (String) n.f2.accept(this, heap);
    n.f3.accept(this, heap);
    String operand2 = (String) n.f4.accept(this, heap);
    MemoryUnit operandUnit1 = this.getMemoryUnitFromScope(heap, this.currentFunction, operand1);
    MemoryUnit operandUnit2 = this.getMemoryUnitFromScope(heap, this.currentFunction, operand2);
    String resultImage = this.multiplyIntMemroyUnitAndReturnResult(operandUnit1, operandUnit2);
    this.putVarInMemory(heap, varAssignName, new MemoryUnit(resultImage, VariableType.INTEGER));
    return null;
  }

  /** f0 -> Identifier() f1 -> "=" f2 -> Identifier() f3 -> "<" f4 -> Identifier() */
  public Object visit(LessThan n, Heap heap) {
    String varAssignName = (String) n.f0.accept(this, heap);
    n.f1.accept(this, heap);
    String operand1 = (String) n.f2.accept(this, heap);
    n.f3.accept(this, heap);
    String operand2 = (String) n.f4.accept(this, heap);
    MemoryUnit operandUnit1 = this.getMemoryUnitFromScope(heap, this.currentFunction, operand1);
    MemoryUnit operandUnit2 = this.getMemoryUnitFromScope(heap, this.currentFunction, operand2);
    int result = this.lessThanOppOnIntMemroyUnitAndReturnResult(operandUnit1, operandUnit2);
    String resultImage = String.valueOf(result);
    this.putVarInMemory(heap, varAssignName, new MemoryUnit(resultImage, VariableType.INTEGER));
    return null;
  }

  /**
   * f0 -> Identifier() f1 -> "=" f2 -> "[" f3 -> Identifier() f4 -> "+" f5 -> IntegerLiteral() f6
   * -> "]"
   */
  public Object visit(Load n, Heap heap) {
    String assigneeName = (String) n.f0.accept(this, heap);
    n.f1.accept(this, heap);
    n.f2.accept(this, heap);
    String valueVarName = (String) n.f3.accept(this, heap);
    n.f4.accept(this, heap);
    String indexImage = (String) n.f5.accept(this, heap);
    n.f6.accept(this, heap);
    this.getValueFromBlockAndAssignToVar(heap, assigneeName, valueVarName, indexImage);
    return null;
  }

  /**
   * f0 -> "[" f1 -> Identifier() f2 -> "+" f3 -> IntegerLiteral() f4 -> "]" f5 -> "=" f6 ->
   * Identifier()
   */
  public Object visit(Store n, Heap heap) {
    n.f0.accept(this, heap);
    String assigneeName = (String) n.f1.accept(this, heap);
    n.f2.accept(this, heap);
    String indexImage = (String) n.f3.accept(this, heap);
    n.f4.accept(this, heap);
    n.f5.accept(this, heap);
    String valVarName = (String) n.f6.accept(this, heap);
    this.updateValueOfUnitInMemBlock(heap, assigneeName, indexImage, valVarName); 
    return null;
  }

  /** f0 -> Identifier() f1 -> "=" f2 -> Identifier() */
  public Object visit(Move n, Heap heap) {
    String asigneeVarName = (String) n.f0.accept(this, heap);
    n.f1.accept(this, heap);
    String valueVarName = (String) n.f2.accept(this, heap);
    this.moveVarValue(heap, asigneeVarName, valueVarName);
    return null;
  }

  /** f0 -> Identifier() f1 -> "=" f2 -> "alloc" f3 -> "(" f4 -> Identifier() f5 -> ")" */
  public Object visit(Alloc n, Heap heap) {
    String assigneeName = (String) n.f0.accept(this, heap);
    n.f1.accept(this, heap);
    n.f2.accept(this, heap);
    n.f3.accept(this, heap);
    String sizeVarName = (String) n.f4.accept(this, heap);
    int size = this.validateAndGetSizeVariableValue(heap, sizeVarName);
    this.allocateMemoryInScope(heap, assigneeName, size);
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
    String assigneeVarName = (String) n.f0.accept(this, heap);
    n.f1.accept(this, heap);
    n.f2.accept(this, heap);
    String calledVarName = (String) n.f3.accept(this, heap);
    this.checkIfCalledVarIsFunc(heap, calledVarName);
    n.f4.accept(this, heap);
    n.f5.accept(this, heap);
    this.initCalledFunctionParams();
    this.rememberParamsOfCalledFunc(heap, n.f5.nodes);
    n.f6.accept(this, heap);
    this.setParentFuncMetaDataToReturnValueFromCalledFunc(this.currentFunction, assigneeVarName);
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
    return n.f0.toString();
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

  private MemoryUnit getMemoryUnitFromScope(Heap heap, String functionName, String varName) {
    return heap.getMemoryUnitFromScope(functionName, varName);
  }

  private MemoryBlock getMemoryBlockFromScope(Heap heap, String functionName, String varName) {
    return heap.getMemoryBlockFromScope(functionName, varName);
  }

  private String addIntMemoryUnitsAndReturnResult(MemoryUnit unit1, MemoryUnit unit2) {
    this.ifNotIntegersExitWithExecutorError(unit1, unit2);
    return String.valueOf(unit1.getIntValue() + unit2.getIntValue());
  }

  private String subtractIntMemroyUnitAndReturnResult(MemoryUnit unit1, MemoryUnit unit2) {
    this.ifNotIntegersExitWithExecutorError(unit1, unit2);
    return String.valueOf(unit1.getIntValue() - unit2.getIntValue());
  }

  private String multiplyIntMemroyUnitAndReturnResult(MemoryUnit unit1, MemoryUnit unit2) {
    this.ifNotIntegersExitWithExecutorError(unit1, unit2);
    return String.valueOf(unit1.getIntValue() * unit2.getIntValue());
  }

  private int lessThanOppOnIntMemroyUnitAndReturnResult(MemoryUnit unit1, MemoryUnit unit2) {
    this.ifNotIntegersExitWithExecutorError(unit1, unit2);
    if (unit1.getIntValue() < unit2.getIntValue()) {
      return 1;
    }
    return 0;
  }

  private void ifNotIntegersExitWithExecutorError(MemoryUnit unit1, MemoryUnit unit2) {
    if (!unit1.isInt() || !unit2.isInt()) {
      this.exitWithExecutionError("Operands must be of type integers");
    }
  }

  private boolean isTheFuncitonDeclaredIsNotMain(String functionName) {
    return functionName != "Main";
  }

  private void rememberParamsOfCalledFunc(Heap heap, List<Node> params) {
    for (Node param : params) {
      String paramVarName = (String) param.accept(this, heap);
      MemoryUnit paramMemUnit =
          this.getMemoryUnitFromScope(heap, this.currentFunction, paramVarName);
      this.calledFunctionParams.add(paramMemUnit);
    }
  }

  private void checkIfCalledVarIsFunc(Heap heap, String varName) {
    MemoryUnit calledVarMemUnit = this.getMemoryUnitFromScope(heap, this.currentFunction, varName);
    if (!calledVarMemUnit.isFunc()) {
      this.exitWithExecutionError("the var called is not a function");
    }
  }

  private void mapDeclaredParamsToCalledParams(Heap heap, List<Node> declaredParams) {
    int paramIndex = -1;
    for (Node param : declaredParams) {
      paramIndex += 1;
      String paramName = (String) param.accept(this, heap);
      MemoryUnit paramMemUnit = this.calledFunctionParams.get(paramIndex);
      this.putVarInMemory(heap, paramName, paramMemUnit);
    }
  }

  private void allocateMemoryInScope(Heap heap, String varName, int size) {
    MemoryBlock memBlock = new MemoryBlock();
    for (int i = 0; i < size; i++) {
      memBlock.addMemoryUnit(new MemoryUnit("", VariableType.NULL));
    }
    heap.addVarToScope(this.currentFunction, varName, memBlock);
  }

  private void exitWithExecutionError(String message) {
    Log.log(message);
    System.exit(1);
  }

  private int validateAndGetSizeVariableValue(Heap heap, String varName) {
    MemoryUnit sizeVarMemUnit = this.getMemoryUnitFromScope(heap, this.currentFunction, varName);
    if (!sizeVarMemUnit.isInt()) {
      this.exitWithExecutionError("the size var is not an int");
    }
    if (sizeVarMemUnit.getIntValue() % MemoryUnit.size != 0) {
      this.exitProgramWithErrorMessage("the size must be a multiple of " + MemoryUnit.size);
    }
    return sizeVarMemUnit.getIntValue() / MemoryUnit.size;
  }

  private void updateValueOfUnitInMemBlock(Heap heap, String assigneeName, String indexImage, String valVarName) {
    MemoryBlock assigneeMemBlock = this.getMemoryBlockFromScope(heap, this.currentFunction, assigneeName);
    int index = Integer.parseInt(indexImage);
    MemoryUnit valVarMemUnit = this.getMemoryUnitFromScope(heap, this.currentFunction, valVarName);
    assigneeMemBlock.updateMemoryUnit(
      index,
      new MemoryUnit(valVarMemUnit.getValueImage(),
      valVarMemUnit.getType())
    );
    heap.updateMemoryBlockInScope(this.currentFunction, assigneeName, assigneeMemBlock);
  }

  private void getValueFromBlockAndAssignToVar(Heap heap, String assigneeName, String valueVarName, String indexImage) {
    MemoryBlock valueMemBlock = this.getMemoryBlockFromScope(heap, this.currentFunction, valueVarName);
    int index = Integer.parseInt(indexImage);
    MemoryUnit memUnitToAssign = valueMemBlock.getMemoryUnitByIndex(index);
    this.putVarInMemory(
      heap,
      assigneeName,
      new MemoryUnit(memUnitToAssign.getValueImage(), memUnitToAssign.getType())
    );
  }

  private void initCalledFunctionParams() {
    this.calledFunctionParams = new ArrayList<MemoryUnit>();
  }

  private void resetCalledFunctionParams() {
    this.calledFunctionParams = null;
  }

  private void moveVarValue(Heap heap, String assigneVarName, String valueVarName) {
    MemoryUnit valueVarMemUnit = this.getMemoryUnitFromScope(heap, this.currentFunction, valueVarName);
    this.putVarInMemory(heap, assigneVarName,new MemoryUnit(valueVarMemUnit.getValueImage(), valueVarMemUnit.getType()));
  }

  private void setParentFuncMetaDataToReturnValueFromCalledFunc(String functionName, String assigneeVarName) {
    this.parentFunctionName = this.currentFunction;
    this.parentFuncAssigneeVarName = assigneeVarName;
  }

  private void resetParentFuncMetaData() {
      this.parentFuncAssigneeVarName = null;
      this.parentFunctionName = null;
  }

  private void returnValueFromThisFunc(Heap heap, String returnVarName) {
    MemoryBlock returnedMemBlock = this.getMemoryBlockFromScope(heap, this.currentFunction, returnVarName);
    heap.updateMemoryBlockInScope(this.parentFunctionName, this.parentFuncAssigneeVarName, returnedMemBlock);
    this.resetParentFuncMetaData();
  }
}

