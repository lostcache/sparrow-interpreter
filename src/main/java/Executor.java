import syntaxtree.*;
import java.util.*;

class Executor extends GJDepthFirst<Object, Heap> {
  private static final boolean debug = false;
  private List<MemoryUnit> calledFunctionParams = null;
  private MemoryUnit returnValueFromCalledFunction = null;
  private List<String> functionCallStack = new ArrayList<String>();
  private int programCounter = 0;
  private List<Integer> programCounterStack = new ArrayList<Integer>();

  public void startExecution(Heap heap) {
    this.executeInstructionUnderFunction(heap, "Main");
    this.functionCallStack.add("Main");
  }

  private void executeInstructionUnderFunction(Heap heap, String calledFunctionName) {
    this.resetProgramCounter();
    this.pushFunctionInCallStack(calledFunctionName);
    heap.createNewFunctionScope(calledFunctionName);
    this.mapCalledParamsToFuncParams(heap, calledFunctionName);
    List<LabelledInstruction> calledFunctionInstructions = heap.getInstructionsByFuncitonName(calledFunctionName);
    int totalInstructions = calledFunctionInstructions.size();
    while (this.programCounter < totalInstructions) {
      LabelledInstruction currentInstruction = calledFunctionInstructions.get(programCounter);
      InstructionUnit currentInstructionUnit = currentInstruction.getInstructionUnit();
      if (currentInstructionUnit.isInstruction()) {
        Instruction instruction = currentInstructionUnit.getInstruction();
        instruction.accept(this, heap);
        this.programCounter++;
        // Log.log("-----------------------");
        // heap.debugMemory();
        // Log.log("-----------------------");
      } else if (currentInstructionUnit.isReturnStatement()) {
        String returnVarName = currentInstructionUnit.getRuturnIdentifier();
        this.returnValueFromCalledFunction = heap.getMemoryUnitFromScope(this.peekFunctionStack(), returnVarName);
        return;
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
    heap.putVarInScope(this.peekFunctionStack(), varName, new MemoryUnit(intValueImage, type), 1);
    return null;
  }

  /** f0 -> Identifier() f1 -> "=" f2 -> "@" f3 -> FunctionName() */
  public Object visit(SetFuncName n, Heap heap) {
    if (debug) Log.log("Visiting SetFuncName");
    String varName = (String) n.f0.accept(this, heap);
    n.f1.accept(this, heap);
    n.f2.accept(this, heap);
    String funName = (String) n.f3.accept(this, heap);
    heap.putVarInScope(this.peekFunctionStack(), varName, new MemoryUnit(funName, VariableType.FUNCTION), 1);
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
    MemoryUnit operandUnit1 = heap.getMemoryUnitFromScope(this.peekFunctionStack(), operand1);
    MemoryUnit operandUnit2 = heap.getMemoryUnitFromScope(this.peekFunctionStack(), operand2);
    String resultImage = this.addIntMemoryUnitsAndReturnResult(operandUnit1, operandUnit2);
    heap.putVarInScope(this.peekFunctionStack(), resultVar, new MemoryUnit(resultImage, VariableType.INTEGER), 1);
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
    MemoryUnit operandUnit1 = heap.getMemoryUnitFromScope(this.peekFunctionStack(), operand1);
    MemoryUnit operandUnit2 = heap.getMemoryUnitFromScope(this.peekFunctionStack(), operand2);
    String resultImage = this.subtractIntMemroyUnitAndReturnResult(operandUnit1, operandUnit2);
    heap.putVarInScope(this.peekFunctionStack(), varAssignName, new MemoryUnit(resultImage, VariableType.INTEGER), 1);
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
    MemoryUnit operandUnit1 = heap.getMemoryUnitFromScope(this.peekFunctionStack(), operand1);
    MemoryUnit operandUnit2 = heap.getMemoryUnitFromScope(this.peekFunctionStack(), operand2);
    String resultImage = this.multiplyIntMemroyUnitAndReturnResult(operandUnit1, operandUnit2);
    heap.putVarInScope(this.peekFunctionStack(), varAssignName, new MemoryUnit(resultImage, VariableType.INTEGER), 1);
    return null;
  }

  /** f0 -> Identifier() f1 -> "=" f2 -> Identifier() f3 -> "<" f4 -> Identifier() */
  public Object visit(LessThan n, Heap heap) {
    if (debug) Log.log("Visiting LessThan");
    String assignIdentifierName = (String) n.f0.accept(this, heap);
    n.f1.accept(this, heap);
    String operand1 = (String) n.f2.accept(this, heap);
    n.f3.accept(this, heap);
    String operand2 = (String) n.f4.accept(this, heap);
    MemoryUnit operandUnit1 = heap.getMemoryUnitFromScope(this.peekFunctionStack(), operand1);
    MemoryUnit operandUnit2 = heap.getMemoryUnitFromScope(this.peekFunctionStack(), operand2);
    int result = this.lessThanOppOnIntMemroyUnitAndReturnResult(operandUnit1, operandUnit2);
    String resultImage = String.valueOf(result);
    heap.putVarInScope(this.peekFunctionStack(), assignIdentifierName, new MemoryUnit(resultImage, VariableType.INTEGER), 1);
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
    String valueIdentifierName = (String) n.f3.accept(this, heap);
    n.f4.accept(this, heap);
    String offSet = (String) n.f5.accept(this, heap);
    MemoryUnit value = heap.getMemoryUnitWithOffsetFromScope(this.peekFunctionStack(), valueIdentifierName, Integer.parseInt(offSet));
    heap.putVarInScope(this.peekFunctionStack(), assigneeName, new MemoryUnit(value.getValueImage(), value.getType()), 1);
    n.f6.accept(this, heap);
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
    String offset = (String) n.f3.accept(this, heap);
    n.f4.accept(this, heap);
    n.f5.accept(this, heap);
    String valueIdentifier = (String) n.f6.accept(this, heap);
    MemoryUnit valueMemoryUnit = heap.getMemoryUnitFromScope(this.peekFunctionStack(), valueIdentifier);
    heap.putValueAtAddressWithOffset(this.peekFunctionStack(), assigneeName, Integer.parseInt(offset), valueMemoryUnit);
    return null;
  }

  /** f0 -> Identifier() f1 -> "=" f2 -> Identifier() */
  public Object visit(Move n, Heap heap) {
    if (debug) Log.log("Visiting Move");
    String lhs = (String) n.f0.accept(this, heap);
    n.f1.accept(this, heap);
    String rhs = (String) n.f2.accept(this, heap);
    heap.moveValueInScope(this.peekFunctionStack(), lhs, rhs);
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
    heap.allocateMemroy(this.peekFunctionStack(), assigneeName, size / MemoryUnit.size);
    n.f5.accept(this, heap);
    return null;
  }

  /** f0 -> "print" f1 -> "(" f2 -> Identifier() f3 -> ")" */
  public Object visit(Print n, Heap heap) {
    if (debug) Log.log("Visiting Print");
    n.f0.accept(this, heap);
    n.f1.accept(this, heap);
    String printVarName = (String) n.f2.accept(this, heap);
    MemoryUnit printVarMemUnit = heap.getMemoryUnitFromScope(this.peekFunctionStack(), printVarName);
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
    this.exitProgramWithErrorMessage(errMessage);
    n.f3.accept(this, heap);
    return null;
  }

  /** f0 -> "goto" f1 -> Label() */
  public Object visit(Goto n, Heap heap) {
    if (debug) Log.log("Visiting Goto");
    n.f0.accept(this, heap);
    String label = (String) n.f1.accept(this, heap);
    int gotoInstructionAddress = heap.getInstructionAddressByLabel(this.peekFunctionStack(), label);
    this.setProgramCounter(gotoInstructionAddress - 1);
    return null;
  }

  /** f0 -> "if0" f1 -> Identifier() f2 -> "goto" f3 -> Label() */
  public Object visit(IfGoto n, Heap heap) {
    if (debug) Log.log("Visiting IfGoto");
    n.f0.accept(this, heap);
    String varName = (String) n.f1.accept(this, heap);
    MemoryUnit memUnit = heap.getMemoryUnitFromScope(this.peekFunctionStack(), varName);
    n.f2.accept(this, heap);
    String label = (String) n.f3.accept(this, heap);
    if (!memUnit.isInt()) {
      return null;
    } else {
      if (memUnit.getIntValue() == 0) {
        int gotoInstructionAddress = heap.getInstructionAddressByLabel(this.peekFunctionStack(), label);
        this.setProgramCounter(gotoInstructionAddress - 1);
      }
    }
    return null;
  }

  /**
   * f0 -> Identifier() f1 -> "=" f2 -> "call" f3 -> Identifier() f4 -> "(" f5 -> ( Identifier() )*
   * f6 -> ")"
   */
  public Object visit(Call n, Heap heap) {
    if (debug) Log.log("Visiting Call");
    this.pushToProgramCounterStack(this.programCounter);
    String assigneeVarName = (String) n.f0.accept(this, heap);
    n.f1.accept(this, heap);
    n.f2.accept(this, heap);
    String calledVarName = (String) n.f3.accept(this, heap);
    this.checkIfCalledVarIsFunc(heap, calledVarName);
    String calledFunctionName = heap.getMemoryUnitFromScope(this.peekFunctionStack(), calledVarName).getValueImage();
    n.f4.accept(this, heap);
    n.f5.accept(this, heap);

    this.initCalledFunctionParams();
    this.rememberParamsValuesOfCalledFunc(heap, n.f5.nodes);

    n.f6.accept(this, heap);

    this.executeInstructionUnderFunction(heap, calledFunctionName);

    heap.destroyFunctionScope(this.peekFunctionStack());
    this.setProgramCounter(this.popFromProgramCounterStack());
    this.popFunctionStack();

    // assign return value
    heap.putVarInScope(this.peekFunctionStack(), assigneeVarName, this.returnValueFromCalledFunction, 1);

    this.resetCalledFunctionParams();
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

  public Object visit(Label n, Heap heap) {
    if (debug) Log.log("visiting label");
    n.f0.accept(this, heap);
    return n.f0.toString();
  }

  private void rememberParamsValuesOfCalledFunc(Heap heap, List<Node> params) {
    for (Node param : params) {
      String paramVarName = (String) param.accept(this, heap);
      MemoryUnit paramMemUnit = heap.getMemoryUnitFromScope(this.peekFunctionStack(), paramVarName);
      this.calledFunctionParams.add(paramMemUnit);
    }
  }

  private void checkIfCalledVarIsFunc(Heap heap, String varName) {
    MemoryUnit calledVarMemUnit = heap.getMemoryUnitFromScope(this.peekFunctionStack(), varName);
    Log.log("trying to call function in -> " + varName);
    if (!calledVarMemUnit.isFunc()) {
      this.exitWithExecutionError("the var called does not represent a function");
    }
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

  private int validateAndGetSizeVariableValue(Heap heap, String varName) {
    MemoryUnit sizeVarMemUnit = heap.getMemoryUnitFromScope(this.peekFunctionStack(), varName);
    if (!sizeVarMemUnit.isInt()) {
      this.exitWithExecutionError("the size var is not an int");
    }
    if (sizeVarMemUnit.getIntValue() % MemoryUnit.size != 0) {
      this.exitProgramWithErrorMessage("the size must be a multiple of " + MemoryUnit.size);
    }
    return sizeVarMemUnit.getIntValue();
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
      heap.putVarInScope(this.peekFunctionStack(), paramName, new MemoryUnit(paramMemUnit.getValueImage(), paramMemUnit.getType()), 1);
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

  private void resetProgramCounter() {
    this.programCounter = 0;
  }

  private void setProgramCounter(int value) {
    this.programCounter = value;
  }

  private void pushToProgramCounterStack(int value) {
    this.programCounterStack.add(value);
  }

  private int popFromProgramCounterStack() {
    return this.programCounterStack.remove(this.programCounterStack.size() - 1);
  }
}

