import java.util.*;

public class Heap {
  public static final int memBlockSize = 4;
  private Map<String, List<LabelledInstruction>> functionInstructions;
  private Map<String, Scope> memory = null;
  private Map<String, List<String>> funcParamMap = null;

  public Heap() {
    this.memory = new HashMap<String, Scope>();
    this.functionInstructions = new HashMap<String, List<LabelledInstruction>>();
    this.funcParamMap = new HashMap<String, List<String>>();
  }

  public void addFunctionInstructions(
      String functionName, List<LabelledInstruction> instructions) {
    this.functionInstructions.put(functionName, instructions);
  }

  public void createNewFunctionScope(String functionName) {
    memory.put(functionName, new Scope());
  }

  public void addVarToScope(String functionName, String varName, MemoryBlock memBlock) {
    Scope scope = this.getScopeMemory(functionName);
    scope.addVar(varName, memBlock);
  }

  public MemoryUnit getMemoryUnitFromScope(String functionName, String varName) {
    MemoryBlock memBlock = this.getMemoryBlockFromScope(functionName, varName);
    return memBlock.getMemoryUnitByIndex(0);
  }

  public MemoryBlock getMemoryBlockFromScope(String functionName, String varName) {
    Scope scope = this.getScopeMemory(functionName);
    return scope.getMemoryBlockByVarName(varName);
  }

  public void updateMemoryBlockInScope(String functionName, String varName, MemoryBlock memBlock) {
    Scope scope = this.getScopeMemory(functionName);
    scope.updateMemoryBlock(varName, memBlock);
  }

  public List<InstructionUnit> getInstructionByLabel(String currentFunction, String desiredLabel) {
    List <InstructionUnit> instructionsToReturn = new ArrayList<InstructionUnit>();
    for (LabelledInstruction labelledInstruction : this.getInstructionsByFuncitonName(currentFunction)) {
      if (labelledInstruction.getLabel().equals(desiredLabel)) {
        instructionsToReturn.add(labelledInstruction.getInstructionUnit());
      }
    }
    return instructionsToReturn;
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

  public void debugMemory() {
    Log.log("debugging memory ----------------->");
    for (String functionName : this.memory.keySet()) {
      Log.log("current scope name -> " + functionName);
      Scope scope = this.getScopeMemory(functionName);
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

  public void rememberFunParams(String functionName, List<String> params) {
    this.funcParamMap.put(functionName, params);
  }

  public List<String> getFumParams(String functionName) {
    return this.funcParamMap.get(functionName);
  }

  private Scope getScopeMemory(String functionName) {
    return this.memory.get(functionName);
  }

  public List<LabelledInstruction> getInstructionsByFuncitonName(String functionName) {
    return this.functionInstructions.get(functionName);
  }
}

