import java.util.*;

public class Heap {
  private Map<String, List<LabelledInstruction>> functionInstructions;
  private Map<String, List<Scope>> stackMemory = null;
  private Map<String, List<String>> functionDeclaredParamsIdentifiers = null;
  private Map<String, MemoryUnit> heapMemory = null;
  private int heapStartAddress = 0;

  public Heap() {
    this.stackMemory = new HashMap<String, List<Scope>>();
    this.functionInstructions = new HashMap<String, List<LabelledInstruction>>();
    this.functionDeclaredParamsIdentifiers = new HashMap<String, List<String>>();
    this.heapMemory = new HashMap<String, MemoryUnit>();
  }

  private void putValueInHeap(MemoryUnit memUnit) {
    this.heapMemory.put(String.valueOf(this.heapStartAddress), memUnit);
    this.incrememtHeapStartAddress();
  }

  private void incrememtHeapStartAddress() {
    this.heapStartAddress += MemoryUnit.size;
  }

  private void updateValueInHeap(String address, MemoryUnit memUnit) {
    this.heapMemory.put(address, memUnit);
  }

  private MemoryUnit getMemoryUnit(String address) {
    MemoryUnit memUnit = this.heapMemory.get(address);
    if (memUnit == null) {
      Log.log("no value at this address in heap");
      System.exit(1);
    }
    return memUnit;
  }

  // all memory related methods 
  public void putIdentifierInScopeMemory(String function, String identifier, MemoryUnit memUnit, int size) {
    Scope scope = this.getScope(function);
    scope.putIdentifierInMemory(identifier, memUnit, size);
  }

  public int addIdentifiers(String function, String op1, String op2) {
    Scope scope = this.getScope(function);
    return scope.addIdentifiers(op1, op2);
  }

  public int subtractIdentifiers(String function, String op1, String op2) {
    Scope scope = this.getScope(function);
    return scope.subtractIdentifiers(op1, op2);
  }

  public int multiplyIdentifiers(String function, String op1, String op2) {
    Scope scope = this.getScope(function);
    return scope.multiplyIdentifiers(op1, op2);
  }

  public int compareIdentifiers(String function, String op1, String op2) {
    Scope scope = this.getScope(function);
    return scope.compareIdentifiers(op1, op2);
  }

  public void allocateMemoryOfSize(String functionName, String identifier, int size) {
    Scope scope = this.getScope(functionName);
    String firstElementAddress = String.valueOf(this.heapStartAddress);
    MemoryUnit pointer = new MemoryUnit(firstElementAddress, VariableType.POINTER);
    for (int i = 0; i < size; i++) {
      MemoryUnit memUnit = new MemoryUnit("", VariableType.NULL);
      this.putValueInHeap(memUnit);
    }
    scope.putIdentifierInMemory(identifier, pointer, 1);
  }

  public MemoryUnit getDereferencedValue(String function, String identifier) {
    Scope scope = this.getScope(function);
    return scope.getDereferencedValue(identifier);
  }

  public String fetchValueImage(String function, String identifier) {
    Scope scope = this.getScope(function);
    return scope.fetchValueImage(identifier);
  }

  public void moveIdentifiers(String functionName, String lhs, String rhs) {
    Scope scope = this.getScope(functionName);
    scope.moveIdentifiers(lhs, rhs);
  }

  public MemoryUnit getValueFromArray(String function, String identifier, int offset) {
    MemoryUnit pointer = this.getPointerFromScope(function, identifier);
    String firstElementAddress = this.getPointedAddress(pointer);
    int desiredMemAddress = Integer.parseInt(firstElementAddress) + offset;
    return this.getMemoryUnit(String.valueOf(desiredMemAddress));
  }

  public void updateIdentifierValue(String functionName, String identifier, MemoryUnit memUnit) {
    Scope scope = this.getScope(functionName);
    scope.updateMemUnitOfIdentifier(identifier, memUnit);
  }

  public boolean identifierExists(String function, String identtifier) {
    Scope scope = this.getScope(function);
    return scope.identifierExists(identtifier);
  }

  public void putValueInArray(String function, String lhs, String rhs, int offset) {
    MemoryUnit rhsMemUnit = this.getMemUnitFromScope(function, rhs);
    MemoryUnit pointer = this.getPointerFromScope(function, lhs);
    String firstElementAddress = this.getPointedAddress(pointer);
    int desiredMemAddress = Integer.parseInt(firstElementAddress) + offset;
    this.updateValueInHeap(String.valueOf(desiredMemAddress), new MemoryUnit(rhsMemUnit.getValueImage(), rhsMemUnit.getType()));
  }

  public MemoryUnit getMemUnitFromScope(String function, String identifier) {
    Scope scope = this.getScope(function);
    return scope.getMemoryUnitByIdentifier(identifier);
  }

  private MemoryUnit getPointerFromScope(String function, String identifier) {
    Scope scope = this.getScope(function);
    MemoryUnit pointer = scope.getMemoryUnitByIdentifier(identifier);
    if (pointer == null) {
      Log.log("pointer does not exist in scope");
      System.exit(1);
    }
    return pointer;
  }

  private String getPointedAddress(MemoryUnit pointer) {
    if (!pointer.isRef()) {
      Log.log("cannot get address value from non pointer");
      System.exit(1);
    }
    return pointer.getValueImage();
  }

  public int getIdentifierSize(String function, String identifier) {
    Scope scope = this.getScope(function);
    return scope.getIdentifierSize(identifier);
  }

  // all instruction related methods
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

  public void rememberFunParams(String functionName, List<String> params) {
    this.functionDeclaredParamsIdentifiers.put(functionName, params);
  }

  public List<String> getFunDeclaredParamIdentifiers(String functionName) {
    return this.functionDeclaredParamsIdentifiers.get(functionName);
  }

  public List<LabelledInstruction> getInstructionsByFuncitonName(String functionName) {
    return this.functionInstructions.get(functionName);
  }

  public void destroyFunctionScope(String functionName) {
    List<Scope> currentScopeList = this.getScopeList(functionName);
    currentScopeList.remove(currentScopeList.size() - 1);
    this.stackMemory.put(functionName, currentScopeList);
  }

  private Scope getScope(String functionName) {
    List<Scope> scopeList = this.stackMemory.get(functionName);
    if (scopeList == null) {
      return null;
    }
    return scopeList.get(scopeList.size() - 1);
  }

  private List<Scope> getScopeList(String functionName) {
    return this.stackMemory.get(functionName);
  }

  private void addToExistingScope(String functionName) {
      List<Scope> funScopeList = this.getScopeList(functionName);
      funScopeList.add(new Scope());
      this.stackMemory.put(functionName, funScopeList);
  }

  private void createNewScopeList(String functionName) {
    List<Scope> newScopeList = new ArrayList<Scope>();
    newScopeList.add(new Scope());
    this.stackMemory.put(functionName, newScopeList);
  }

  public void debugMemory() {
    Log.log("debugging memory ----------------->");
    for (String functionName : this.stackMemory.keySet()) {
      Log.log("current scope name -> " + functionName);
      Scope scope = this.getScope(functionName);
      scope.debugScopeMemory();
    }
  }

  public void debugFunctionParams() {
    Log.log("Debugging funciton params ----------------->");
    for (String functionName : this.functionDeclaredParamsIdentifiers.keySet()) {
      Log.log("function -> " + functionName);
      for (String paramName : this.functionDeclaredParamsIdentifiers.get(functionName)) {
        Log.log("param -> " + paramName);
      }
    }
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
}

