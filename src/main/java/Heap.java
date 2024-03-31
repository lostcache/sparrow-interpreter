import java.util.*;

public class Heap {
  private Map<String, List<LabelledInstruction>> functionInstructions;
  private Map<String, List<Scope>> memory = null;
  private Map<String, List<String>> funcParamMap = null;

  public Heap() {
    this.memory = new HashMap<String, List<Scope>>();
    this.functionInstructions = new HashMap<String, List<LabelledInstruction>>();
    this.funcParamMap = new HashMap<String, List<String>>();
  }

  public void addFunctionInstructions(
    String functionName,
    List<LabelledInstruction> instructions
  ) {
    this.functionInstructions.put(functionName, instructions);
  }

  public void createNewFunctionScope(String functionName) {
    if (this.getScope(functionName) != null) {
      this.addToExistingScope(functionName);
    } else {
      this.createNewScopeList(functionName);
    }
  }

  public void putVarInScope(String functionName, String varName, MemoryUnit memUnit) {
    Scope scope = this.getScope(functionName);
    scope.createVariable(varName, memUnit);
  }

  public MemoryUnit getMemoryUnitFromScope(String functionName, String identifier) {
    Scope currentScope = this.getScope(functionName);
    return currentScope.getIdentifierValue(identifier);
  }

  public MemoryUnit getMemoryUnitWithOffsetFromScope(String functionName, String identifier, int offset) {
    Scope currentScope = this.getScope(functionName);
    return currentScope.getIdentifierValueWithOffset(identifier, offset);
  }

  public void allocateMemroy(String functionName, String identifier, int size) {
    this.putVarInScope(functionName, identifier, new MemoryUnit("", VariableType.NULL));
    Scope currentScope = this.getScope(functionName);
    MemoryAddress baseAddress = currentScope.getAddressOfIdentifier(identifier);
    for (int i = 0; i < size - 1; i++) {
      currentScope.putValueinAddress(baseAddress.getIntValue() + MemoryUnit.size, new MemoryUnit("", VariableType.NULL));
    }
  }

  public void putValueAtAddressWithOffset(String functionName, String baseIdentifier, int offset, MemoryUnit memUnit) {
    Scope scope = this.getScope(functionName);
    MemoryAddress baseAddress = scope.getAddressOfIdentifier(baseIdentifier);
    int desiredAddress = baseAddress.getIntValue() + offset;
    scope.putValueinAddress(desiredAddress, memUnit);
  }

  public void moveValueInScope(String functionName, String lhs, String rhs) {
    Scope scope = this.getScope(functionName);
    scope.moveValue(lhs, rhs);
  }

  public int getInstructionAddressByLabel(String currentFunction, String desiredLabel) {
    int labelAddress = 0;
    List<LabelledInstruction> functionInstruction = this.getInstructionsByFuncitonName(currentFunction);
    for (int i = 0; i < functionInstruction.size(); i++) {
      LabelledInstruction labelledInstruction = functionInstruction.get(i);
      if (labelledInstruction.getLabel().equals(desiredLabel)) {
        labelAddress = i;
        break;
      }
    }
    return labelAddress;
  }

  public void debugInstructions() {
    Log.log(this.functionInstructions.keySet());
    for (String functionName : this.functionInstructions.keySet()) {
      List<LabelledInstruction> instructions = this.functionInstructions.get(functionName);
      Log.log("function-> " + functionName);
      Log.log("total->" + instructions.size() + "instructions-> " + instructions);
      for (LabelledInstruction instruction : instructions) {
        Log.log("Label -> " + instruction.getLabel() + " Instr -> " + instruction.toString());
      }
    }
  }

  public void rememberFunParams(String functionName, List<String> params) {
    this.funcParamMap.put(functionName, params);
  }

  public List<String> getFumParams(String functionName) {
    return this.funcParamMap.get(functionName);
  }

  public List<LabelledInstruction> getInstructionsByFuncitonName(String functionName) {
    return this.functionInstructions.get(functionName);
  }

  public void destroyFunctionScope(String functionName) {
    List<Scope> currentScopeList = this.getScopeList(functionName);
    currentScopeList.remove(currentScopeList.size() - 1);
    this.memory.put(functionName, currentScopeList);
  }

  private Scope getScope(String functionName) {
    List<Scope> scopeList = this.memory.get(functionName);
    if (scopeList == null) {
      return null;
    }
    return scopeList.get(scopeList.size() - 1);
  }

  private List<Scope> getScopeList(String functionName) {
    return this.memory.get(functionName);
  }

  private void addToExistingScope(String functionName) {
      List<Scope> funScopeList = this.getScopeList(functionName);
      funScopeList.add(new Scope());
      this.memory.put(functionName, funScopeList);
  }

  private void createNewScopeList(String functionName) {
    List<Scope> newScopeList = new ArrayList<Scope>();
    newScopeList.add(new Scope());
    this.memory.put(functionName, newScopeList);
  }

  public void debugMemory() {
    Log.log("debugging memory ----------------->");
    for (String functionName : this.memory.keySet()) {
      Log.log("current scope name -> " + functionName);
      Scope scope = this.getScope(functionName);
      scope.debugScopeMemory();
    }
  }

  public void debugFunctionParams() {
    Log.log("Debugging funciton params ----------------->");
    for (String functionName : this.funcParamMap.keySet()) {
      Log.log("function -> " + functionName);
      for (String paramName : this.funcParamMap.get(functionName)) {
        Log.log("param -> " + paramName);
      }
    }
  }
}

