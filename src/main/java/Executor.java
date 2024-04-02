import syntaxtree.*;
import java.util.*;

class Executor extends GJDepthFirst<Object, Heap> {
  private static final boolean debug = false;
  private List<MemoryUnit> calledFunctionParams = null;
  private List<Integer> calledFuncParamSizes = null;
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
        this.returnValueFromCalledFunction = heap.getMemUnitFromScope(this.peekFunctionStack(), returnVarName);
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
    String identifier = (String) n.f0.accept(this, heap);
    n.f1.accept(this, heap);
    String intValueImage = (String) n.f2.accept(this, heap);
    VariableType type = VariableType.INTEGER;
    int size = 1;
    MemoryUnit memUnit = new MemoryUnit(intValueImage, type);
    if (heap.identifierExists(this.peekFunctionStack(), identifier)) {
      heap.updateIdentifierValue(this.peekFunctionStack(), identifier, memUnit);
    } else {
      heap.putIdentifierInScopeMemory(this.peekFunctionStack(), identifier, memUnit, size);
    }
    return null;
  }


  /** f0 -> Identifier() f1 -> "=" f2 -> "@" f3 -> FunctionName() */
  public Object visit(SetFuncName n, Heap heap) {
    String identifier = (String) n.f0.accept(this, heap);
    n.f1.accept(this, heap);
    n.f2.accept(this, heap);
    String functionIdentifier = (String) n.f3.accept(this, heap);
    MemoryUnit memUnit = new MemoryUnit(functionIdentifier, VariableType.FUNCTION);
    if (heap.identifierExists(this.peekFunctionStack(), identifier)) {
      heap.updateIdentifierValue(this.peekFunctionStack(), identifier, memUnit);
    } else {
      heap.putIdentifierInScopeMemory(this.peekFunctionStack(), identifier, memUnit, 1);
    }
    return null;
  }

  /** f0 -> Identifier() f1 -> "=" f2 -> Identifier() f3 -> "+" f4 -> Identifier() */
  public Object visit(Add n, Heap heap) {
    if (debug) Log.log("Visiting Add");
    String lhs = (String) n.f0.accept(this, heap);
    n.f1.accept(this, heap);
    String op1 = (String) n.f2.accept(this, heap);
    n.f3.accept(this, heap);
    String op2 = (String) n.f4.accept(this, heap);
    int result = heap.addIdentifiers(this.peekFunctionStack(), op1, op2);
    MemoryUnit lhsMemUnit = new MemoryUnit(String.valueOf(result), VariableType.INTEGER);
    if (heap.identifierExists(this.peekFunctionStack(), lhs)) {
      heap.updateIdentifierValue(this.peekFunctionStack(), lhs, lhsMemUnit);
    } else {
      heap.putIdentifierInScopeMemory(this.peekFunctionStack(), lhs, lhsMemUnit, 1);
    }
    return null;
  }

  /** f0 -> Identifier() f1 -> "=" f2 -> Identifier() f3 -> "-" f4 -> Identifier() */
  public Object visit(Subtract n, Heap heap) {
    if (debug) Log.log("Visiting Subtract");
    String lhs = (String) n.f0.accept(this, heap);
    n.f1.accept(this, heap);
    String op1 = (String) n.f2.accept(this, heap);
    n.f3.accept(this, heap);
    String op2 = (String) n.f4.accept(this, heap);
    int result = heap.subtractIdentifiers(this.peekFunctionStack(), op1, op2);
    MemoryUnit lhsMemUnit = new MemoryUnit(String.valueOf(result), VariableType.INTEGER);
    if (heap.identifierExists(this.peekFunctionStack(), lhs)) {
      heap.updateIdentifierValue(this.peekFunctionStack(), lhs, lhsMemUnit);
    } else {
      heap.putIdentifierInScopeMemory(this.peekFunctionStack(), lhs, lhsMemUnit, 1);
    }
    return null;
  }

  /** f0 -> Identifier() f1 -> "=" f2 -> Identifier() f3 -> "*" f4 -> Identifier() */
  public Object visit(Multiply n, Heap heap) {
    if (debug) Log.log("Visiting Multiply");
    String lhs = (String) n.f0.accept(this, heap);
    n.f1.accept(this, heap);
    String op1 = (String) n.f2.accept(this, heap);
    n.f3.accept(this, heap);
    String op2 = (String) n.f4.accept(this, heap);
    int result = heap.multiplyIdentifiers(this.peekFunctionStack(), op1, op2);
    MemoryUnit lhsMemUnit = new MemoryUnit(String.valueOf(result), VariableType.INTEGER);
    if (heap.identifierExists(this.peekFunctionStack(), lhs)) {
      heap.updateIdentifierValue(this.peekFunctionStack(), lhs, lhsMemUnit);
    } else {
      heap.putIdentifierInScopeMemory(this.peekFunctionStack(), lhs, new MemoryUnit(String.valueOf(result), VariableType.INTEGER), 1);
    }
    return null;
  }

  /** f0 -> Identifier() f1 -> "=" f2 -> Identifier() f3 -> "<" f4 -> Identifier() */
  public Object visit(LessThan n, Heap heap) {
    if (debug) Log.log("Visiting LessThan");
    String lhs = (String) n.f0.accept(this, heap);
    n.f1.accept(this, heap);
    String op1 = (String) n.f2.accept(this, heap);
    n.f3.accept(this, heap);
    String op2 = (String) n.f4.accept(this, heap);
    int result = heap.compareIdentifiers(this.peekFunctionStack(), op1, op2);
    MemoryUnit lhsMemUnit = new MemoryUnit(String.valueOf(result), VariableType.INTEGER);
    if (heap.identifierExists(this.peekFunctionStack(), lhs)) {
      heap.updateIdentifierValue(this.peekFunctionStack(), lhs, lhsMemUnit);
    } else {
      heap.putIdentifierInScopeMemory(this.peekFunctionStack(), lhs, lhsMemUnit,1);
    }
    return null;
  }

  /**
   * f0 -> Identifier() f1 -> "=" f2 -> "[" f3 -> Identifier() f4 -> "+" f5 -> IntegerLiteral() f6
   * -> "]"
   */
  public Object visit(Load n, Heap heap) {
    if (debug) Log.log("Visiting Load");
    String lhs = (String) n.f0.accept(this, heap);
    n.f1.accept(this, heap);
    n.f2.accept(this, heap);
    String rhs = (String) n.f3.accept(this, heap);
    n.f4.accept(this, heap);
    String offset = (String) n.f5.accept(this, heap);
    n.f6.accept(this, heap);
    MemoryUnit rhsMemUnit = heap.getValueFromArray(this.peekFunctionStack(), rhs, Integer.parseInt(offset));
    MemoryUnit lhsMemUnit = new MemoryUnit(rhsMemUnit.getValueImage(), rhsMemUnit.getType());
    if (heap.identifierExists(this.peekFunctionStack(), lhs)) {
      heap.updateIdentifierValue(this.peekFunctionStack(), lhs, lhsMemUnit);
    } else {
      heap.putIdentifierInScopeMemory(this.peekFunctionStack(), lhs, lhsMemUnit, 1);
    }
    return null;
  }

  /**
   * f0 -> "[" f1 -> Identifier() f2 -> "+" f3 -> IntegerLiteral() f4 -> "]" f5 -> "=" f6 ->
   * Identifier()
   */
  public Object visit(Store n, Heap heap) {
    if (debug) Log.log("Visiting Store");
    n.f0.accept(this, heap);
    String lhs = (String) n.f1.accept(this, heap);
    n.f2.accept(this, heap);
    String offset = (String) n.f3.accept(this, heap);
    n.f4.accept(this, heap);
    n.f5.accept(this, heap);
    int offsetIntValue = Integer.parseInt(offset);
    String rhs = (String) n.f6.accept(this, heap);
    heap.putValueInArray(this.peekFunctionStack(), lhs, rhs, offsetIntValue);
    return null;
  }

  /** f0 -> Identifier() f1 -> "=" f2 -> Identifier() */
  public Object visit(Move n, Heap heap) {
    if (debug) Log.log("Visiting Move");
    String lhs = (String) n.f0.accept(this, heap);
    n.f1.accept(this, heap);
    String rhs = (String) n.f2.accept(this, heap);
    heap.moveIdentifiers(this.peekFunctionStack(), lhs, rhs);
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
    heap.allocateMemoryOfSize(this.peekFunctionStack(), assigneeName, size / MemoryUnit.size);
    n.f5.accept(this, heap);
    return null;
  }

  /** f0 -> "print" f1 -> "(" f2 -> Identifier() f3 -> ")" */
  public Object visit(Print n, Heap heap) {
    if (debug) Log.log("Visiting Print");
    n.f0.accept(this, heap);
    n.f1.accept(this, heap);
    String identifier = (String) n.f2.accept(this, heap);
    MemoryUnit memunit = heap.getMemUnitFromScope(this.peekFunctionStack(), identifier);
    Log.log(memunit.getValueImage());
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
    String identifier = (String) n.f1.accept(this, heap);
    String identifierValueImage = heap.fetchValueImage(this.peekFunctionStack(), identifier);
    n.f2.accept(this, heap);
    String label = (String) n.f3.accept(this, heap);
    if (identifierValueImage.equals("0")) {
      int gotoInstructionAddress = heap.getInstructionAddressByLabel(this.peekFunctionStack(), label);
      this.setProgramCounter(gotoInstructionAddress - 1);
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
    String lhs = (String) n.f0.accept(this, heap);
    n.f1.accept(this, heap);
    n.f2.accept(this, heap);
    String funcPointer = (String) n.f3.accept(this, heap);
    this.checkIfCalledVarIsFunc(heap, funcPointer);
    String calledFunctionName = heap.fetchValueImage(this.peekFunctionStack(), funcPointer);
    n.f4.accept(this, heap);
    n.f5.accept(this, heap);

    this.initCalledFunctionParams();
    this.rememberCalledParamValues(heap, n.f5.nodes);

    n.f6.accept(this, heap);

    this.executeInstructionUnderFunction(heap, calledFunctionName);

    heap.destroyFunctionScope(this.peekFunctionStack());
    this.setProgramCounter(this.popFromProgramCounterStack());
    this.popFunctionStack();

    // assign return value
    heap.putIdentifierInScopeMemory(this.peekFunctionStack(), lhs, this.returnValueFromCalledFunction, 1);

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

  private void rememberCalledParamValues(Heap heap, List<Node> params) {
    for (Node param : params) {
      String paramIdentifier = (String) param.accept(this, heap);
      MemoryUnit paramMemUnit = heap.getMemUnitFromScope(this.peekFunctionStack(), paramIdentifier);
      this.calledFunctionParams.add(paramMemUnit);
      this.calledFuncParamSizes.add(heap.getIdentifierSize(this.peekFunctionStack(), paramIdentifier));
    }
  }

  private void checkIfCalledVarIsFunc(Heap heap, String varName) {
    MemoryUnit calledVarMemUnit = heap.getMemUnitFromScope(this.peekFunctionStack(), varName);
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
    this.calledFuncParamSizes = new ArrayList<Integer>();
  }

  private void resetCalledFunctionParams() {
    this.calledFunctionParams = null;
    this.calledFuncParamSizes = null;
  }

  private int validateAndGetSizeVariableValue(Heap heap, String varName) {
    MemoryUnit sizeVarMemUnit = heap.getMemUnitFromScope(this.peekFunctionStack(), varName);
    int size = sizeVarMemUnit.getIntValue();
    if (size % MemoryUnit.size != 0) {
      this.exitProgramWithErrorMessage("the size must be a multiple of " + MemoryUnit.size);
    }
    return size;
  }

  private void exitProgramWithErrorMessage(String message) {
    Log.log(message);
    System.exit(0);
  }

  private void mapCalledParamsToFuncParams(Heap heap, String calledFunctionName) {
    List<String> declaredParams = heap.getFunDeclaredParamIdentifiers(calledFunctionName);
    for (int i = 0; i < declaredParams.size(); i++) {
      String declaredParamIdentifier = declaredParams.get(i);
      int calledParamSize = this.calledFuncParamSizes.get(i);
      MemoryUnit paramMemUnit = this.calledFunctionParams.get(i);
      heap.putIdentifierInScopeMemory(this.peekFunctionStack(), declaredParamIdentifier, new MemoryUnit(paramMemUnit.getValueImage(), paramMemUnit.getType()), calledParamSize);
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

