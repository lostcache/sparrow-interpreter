import syntaxtree.*;
import java.util.*;

class Executor extends GJDepthFirst<Object, Heap> {
  private static final boolean debug = false;
  private String currentFunction = null;
  private List<MemoryUnit> calledFunctionParams = null;
  private String parentFunctionName = null;
  private MemoryBlock returnValueFromCalledFunction = null;
  private List<String> functionCallStack = new ArrayList<String>();

  public void startExecution(Heap heap) {
    this.executeInstructionUnderFunction(heap, "Main");
    this.functionCallStack.add("Main");
  }

  private void executeInstructionUnderFunction(Heap heap, String calledFunctionName) {
    this.pushFunctionInCallStack(calledFunctionName);
    heap.createNewFunctionScope(calledFunctionName);
    this.mapCalledParamsToFuncParams(heap, calledFunctionName);
    List<LabelledInstruction> calledFunctionInstructions = heap.getInstructionsByFuncitonName(calledFunctionName);
    int totalInstructions = calledFunctionInstructions.size();
    for (int i = 0; i < totalInstructions; i++) {
      LabelledInstruction currentInstruction = calledFunctionInstructions.get(i);
      InstructionUnit currentInstructionUnit = currentInstruction.getInstructionUnit();
      if (currentInstructionUnit.isInstruction()) {
        Instruction instruction = currentInstructionUnit.getInstruction();
        instruction.accept(this, heap);
      } else if (currentInstructionUnit.isReturnStatement()) {
        String returnVarName = currentInstructionUnit.getRuturnIdentifier();
        this.returnValueFromCalledFunction = heap.getMemoryBlockFromScope(this.peekFunctionStack(), returnVarName);
      }
    }
  }

  /**
   * f0 -> LabelWithColon() | SetInteger() | SetFuncName() | Add() | Subtract() | Multiply() |
   * LessThan() | Load() | Store() | Move() | Alloc() | Print() | ErrorMessage() | Goto() | IfGoto()
   * | Call()
   */
  public Object visit(Instruction n, Heap heap) {
    if (debug) Log.log("Visiting Instruction");
    n.f0.accept(this, heap);
    return null;
  }

  /** f0 -> Label() f1 -> ":" */
  public Object visit(LabelWithColon n, Heap heap) {
    if (debug) Log.log("Visiting Label");
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
    if (debug) Log.log("Visiting SetFuncName");
    String varName = (String) n.f0.accept(this, heap);
    n.f1.accept(this, heap);
    n.f2.accept(this, heap);
    String funName = (String) n.f3.accept(this, heap);
    this.putVarInMemory(heap, varName, new MemoryUnit(funName, VariableType.FUNCTION));
    return null;
  }

  /** f0 -> Identifier() f1 -> "=" f2 -> Identifier() f3 -> "+" f4 -> Identifier() */
  public Object visit(Add n, Heap heap) {
    if (debug) Log.log("Visiting Add");
    String resultVar = (String) n.f0.accept(this, heap);
    n.f1.accept(this, heap);
    String operand1 = (String) n.f2.accept(this, heap);
    n.f3.accept(this, heap);
    String operand2 = (String) n.f4.accept(this, heap);
    MemoryUnit operandUnit1 = this.getMemoryUnitFromScope(heap, this.peekFunctionStack(), operand1);
    MemoryUnit operandUnit2 = this.getMemoryUnitFromScope(heap, this.peekFunctionStack(), operand2);
    String resultImage = this.addIntMemoryUnitsAndReturnResult(operandUnit1, operandUnit2);
    this.putVarInMemory(heap, resultVar, new MemoryUnit(resultImage, VariableType.INTEGER));
    return null;
  }

  /** f0 -> Identifier() f1 -> "=" f2 -> Identifier() f3 -> "-" f4 -> Identifier() */
  public Object visit(Subtract n, Heap heap) {
    if (debug) Log.log("Visiting Subtract");
    String varAssignName = (String) n.f0.accept(this, heap);
    n.f1.accept(this, heap);
    String operand1 = (String) n.f2.accept(this, heap);
    n.f3.accept(this, heap);
    String operand2 = (String) n.f4.accept(this, heap);
    MemoryUnit operandUnit1 = this.getMemoryUnitFromScope(heap, this.peekFunctionStack(), operand1);
    MemoryUnit operandUnit2 = this.getMemoryUnitFromScope(heap, this.peekFunctionStack(), operand2);
    String resultImage = this.subtractIntMemroyUnitAndReturnResult(operandUnit1, operandUnit2);
    this.putVarInMemory(heap, varAssignName, new MemoryUnit(resultImage, VariableType.INTEGER));
    return null;
  }

  /** f0 -> Identifier() f1 -> "=" f2 -> Identifier() f3 -> "*" f4 -> Identifier() */
  public Object visit(Multiply n, Heap heap) {
    if (debug) Log.log("Visiting Multiply");
    String varAssignName = (String) n.f0.accept(this, heap);
    n.f1.accept(this, heap);
    String operand1 = (String) n.f2.accept(this, heap);
    n.f3.accept(this, heap);
    String operand2 = (String) n.f4.accept(this, heap);
    MemoryUnit operandUnit1 = this.getMemoryUnitFromScope(heap, this.peekFunctionStack(), operand1);
    MemoryUnit operandUnit2 = this.getMemoryUnitFromScope(heap, this.peekFunctionStack(), operand2);
    String resultImage = this.multiplyIntMemroyUnitAndReturnResult(operandUnit1, operandUnit2);
    this.putVarInMemory(heap, varAssignName, new MemoryUnit(resultImage, VariableType.INTEGER));
    return null;
  }

  /** f0 -> Identifier() f1 -> "=" f2 -> Identifier() f3 -> "<" f4 -> Identifier() */
  public Object visit(LessThan n, Heap heap) {
    if (debug) Log.log("Visiting LessThan");
    String varAssignName = (String) n.f0.accept(this, heap);
    n.f1.accept(this, heap);
    String operand1 = (String) n.f2.accept(this, heap);
    n.f3.accept(this, heap);
    String operand2 = (String) n.f4.accept(this, heap);
    MemoryUnit operandUnit1 = this.getMemoryUnitFromScope(heap, this.peekFunctionStack(), operand1);
    MemoryUnit operandUnit2 = this.getMemoryUnitFromScope(heap, this.peekFunctionStack(), operand2);
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
    if (debug) Log.log("Visiting Load");
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
    if (debug) Log.log("Visiting Store");
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
    if (debug) Log.log("Visiting Move");
    String asigneeVarName = (String) n.f0.accept(this, heap);
    n.f1.accept(this, heap);
    String valueVarName = (String) n.f2.accept(this, heap);
    this.moveVarValue(heap, asigneeVarName, valueVarName);
    return null;
  }

  /** f0 -> Identifier() f1 -> "=" f2 -> "alloc" f3 -> "(" f4 -> Identifier() f5 -> ")" */
  public Object visit(Alloc n, Heap heap) {
    if (debug) Log.log("Visiting Alloc");
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
    if (debug) Log.log("Visiting Print");
    n.f0.accept(this, heap);
    n.f1.accept(this, heap);
    String printVarName = (String) n.f2.accept(this, heap);
    MemoryUnit printVarMemUnit = this.getMemoryUnitFromScope(heap, this.peekFunctionStack(), printVarName);
    Log.log(printVarMemUnit.getValueImage());
    n.f3.accept(this, heap);
    return null;
  }

  /** f0 -> "error" f1 -> "(" f2 -> StringLiteral() f3 -> ")" */
  public Object visit(ErrorMessage n, Heap heap) {
    if (debug) Log.log("Visiting ErrorMessage");
    n.f0.accept(this, heap);
    n.f1.accept(this, heap);
    String errMessage = (String) n.f2.accept(this, heap);
    // this.exitProgramWithErrorMessage(errMessage);
    n.f3.accept(this, heap);
    return null;
  }

  /** f0 -> "goto" f1 -> Label() */
  public Object visit(Goto n, Heap heap) {
    if (debug) Log.log("Visiting Goto");
    n.f0.accept(this, heap);
    String label = (String) n.f1.accept(this, heap);
    List<InstructionUnit> goToInstructions = heap.getInstructionByLabel(this.peekFunctionStack(), label);
    return null;
  }

  /** f0 -> "if0" f1 -> Identifier() f2 -> "goto" f3 -> Label() */
  public Object visit(IfGoto n, Heap heap) {
    if (debug) Log.log("Visiting IfGoto");
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
    if (debug) Log.log("Visiting Call");
    String assigneeVarName = (String) n.f0.accept(this, heap);
    n.f1.accept(this, heap);
    n.f2.accept(this, heap);
    String calledVarName = (String) n.f3.accept(this, heap);
    this.checkIfCalledVarIsFunc(heap, calledVarName);
    String calledFunctionName = this.getMemoryUnitFromScope(heap, this.peekFunctionStack(), calledVarName).getValueImage();
    n.f4.accept(this, heap);
    n.f5.accept(this, heap);
    this.initCalledFunctionParams();
    this.rememberParamsValuesOfCalledFunc(heap, n.f5.nodes);
    n.f6.accept(this, heap);
    this.setCalleeFunctionMetadata(this.peekFunctionStack());
    this.executeInstructionUnderFunction(heap, calledFunctionName);

    this.popFunctionStack();

    // assign return value
    heap.updateMemoryBlockInScope(this.peekFunctionStack(), assigneeVarName, this.returnValueFromCalledFunction);

    this.resetCalledFunctionParams();
    this.resetCalleeFunctionMetadata();
    this.returnValueFromCalledFunction = null;
    return null;
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
    return n.f0.toString();
  }

  /** f0 -> <STRINGCONSTANT> */
  public Object visit(StringLiteral n, Heap heap) {
    if (debug) Log.log("visiting string literal");
    n.f0.accept(this, heap);
    return n.f0.toString();
  }

  /** f0 -> <IDENTIFIER> */
  public Object visit(FunctionName n, Heap heap) {
    if (debug) Log.log("visiting function name");
    n.f0.accept(this, heap);
    return n.f0.toString();
  }

  private void putVarInMemory(Heap heap, String varName, MemoryUnit memUnit) {
    MemoryBlock memBlock = new MemoryBlock();
    memBlock.addMemoryUnit(memUnit);
    heap.addVarToScope(this.peekFunctionStack(), varName, memBlock);
  }

  private void rememberParamsValuesOfCalledFunc(Heap heap, List<Node> params) {
    for (Node param : params) {
      String paramVarName = (String) param.accept(this, heap);
      MemoryUnit paramMemUnit = this.getMemoryUnitFromScope(heap, this.peekFunctionStack(), paramVarName);
      this.calledFunctionParams.add(paramMemUnit);
    }
  }

  private void checkIfCalledVarIsFunc(Heap heap, String varName) {
    MemoryUnit calledVarMemUnit = this.getMemoryUnitFromScope(heap, this.peekFunctionStack(), varName);
    if (!calledVarMemUnit.isFunc()) {
      this.exitWithExecutionError("the var called does not represent a function");
    }
  }

  private MemoryUnit getMemoryUnitFromScope(Heap heap, String functionName, String varName) {
    return heap.getMemoryUnitFromScope(functionName, varName);
  }

  private void exitWithExecutionError(String message) {
    Log.log(message);
    System.exit(1);
  }

  private void initCalledFunctionParams() {
    this.calledFunctionParams = new ArrayList<MemoryUnit>();
  }

  private void resetCalledFunctionParams() {
    this.calledFunctionParams = null;
  }

  private void setCalleeFunctionMetadata(String functionName) {
    this.parentFunctionName = this.peekFunctionStack();
  }

  private void resetCalleeFunctionMetadata() {
    this.parentFunctionName = null;
  }

  private void allocateMemoryInScope(Heap heap, String varName, int size) {
    MemoryBlock memBlock = new MemoryBlock();
    for (int i = 0; i < size; i++) {
      memBlock.addMemoryUnit(new MemoryUnit("", VariableType.NULL));
    }
    heap.addVarToScope(this.peekFunctionStack(), varName, memBlock);
  }

  private int validateAndGetSizeVariableValue(Heap heap, String varName) {
    MemoryUnit sizeVarMemUnit = this.getMemoryUnitFromScope(heap, this.peekFunctionStack(), varName);
    if (!sizeVarMemUnit.isInt()) {
      this.exitWithExecutionError("the size var is not an int");
    }
    if (sizeVarMemUnit.getIntValue() % MemoryUnit.size != 0) {
      this.exitProgramWithErrorMessage("the size must be a multiple of " + MemoryUnit.size);
    }
    return sizeVarMemUnit.getIntValue() / MemoryUnit.size;
  }

  private void exitProgramWithErrorMessage(String message) {
    Log.log(message);
    System.exit(0);
  }

  private String addIntMemoryUnitsAndReturnResult(MemoryUnit unit1, MemoryUnit unit2) {
    this.ifNotIntegersExitWithExecutorError(unit1, unit2);
    return String.valueOf(unit1.getIntValue() + unit2.getIntValue());
  }

  private void ifNotIntegersExitWithExecutorError(MemoryUnit unit1, MemoryUnit unit2) {
    if (!unit1.isInt() || !unit2.isInt()) {
      this.exitWithExecutionError("Operands must be of type integers");
    }
  }

  private void moveVarValue(Heap heap, String assigneVarName, String valueVarName) {
    MemoryUnit valueVarMemUnit = this.getMemoryUnitFromScope(heap, this.peekFunctionStack(), valueVarName);
    this.putVarInMemory(heap, assigneVarName,new MemoryUnit(valueVarMemUnit.getValueImage(), valueVarMemUnit.getType()));
  }

  private void getValueFromBlockAndAssignToVar(Heap heap, String assigneeName, String valueVarName, String indexImage) {
    MemoryBlock valueMemBlock = this.getMemoryBlockFromScope(heap, this.peekFunctionStack(), valueVarName);
    int index = Integer.parseInt(indexImage);
    MemoryUnit memUnitToAssign = valueMemBlock.getMemoryUnitByIndex(index);
    this.putVarInMemory(
      heap,
      assigneeName,
      new MemoryUnit(memUnitToAssign.getValueImage(), memUnitToAssign.getType())
    );
  }

  private MemoryBlock getMemoryBlockFromScope(Heap heap, String functionName, String varName) {
    return heap.getMemoryBlockFromScope(functionName, varName);
  }

  private void updateValueOfUnitInMemBlock(Heap heap, String assigneeName, String indexImage, String valVarName) {
    MemoryBlock assigneeMemBlock = this.getMemoryBlockFromScope(heap, this.peekFunctionStack(), assigneeName);
    int index = Integer.parseInt(indexImage);
    MemoryUnit valVarMemUnit = this.getMemoryUnitFromScope(heap, this.peekFunctionStack(), valVarName);
    assigneeMemBlock.updateMemoryUnit(
      index,
      new MemoryUnit(valVarMemUnit.getValueImage(), valVarMemUnit.getType())
    );
    heap.updateMemoryBlockInScope(this.peekFunctionStack(), assigneeName, assigneeMemBlock);
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

  private void mapCalledParamsToFuncParams(Heap heap, String calledFunctionName) {
    List<String> funcParams = heap.getFumParams(calledFunctionName);
    for (int i = 0; i < funcParams.size(); i++) {
      String paramName = funcParams.get(i);
      MemoryUnit paramMemUnit = this.calledFunctionParams.get(i);
      this.putVarInMemory(heap, paramName, new MemoryUnit(paramMemUnit.getValueImage(), paramMemUnit.getType()));
    }
  }

  private String peekFunctionStack() {
    return this.functionCallStack.get(this.functionCallStack.size() - 1);
  }

  private void popFunctionStack () {
    this.functionCallStack.remove(this.functionCallStack.size() - 1);
  }

  private void pushFunctionInCallStack(String functionName) {
    this.functionCallStack.add(functionName);
  }
}

