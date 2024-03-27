import java.util.*;

public class Heap {
  public static final int memBlockSize = 4;
  private Map<String, List<LabelledInstructions>> functionInstructions;
  private Map<String, Scope> memory = null;

  public Heap() {
    this.memory = new HashMap<String, Scope>();
    this.functionInstructions = new HashMap<String, List<LabelledInstructions>>();
  }

  public void addFunctionInstructions(
      String functionName, List<LabelledInstructions> instructions) {
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
    Scope scope = this.getScopeMemory(functionName);
    MemoryBlock memBlock = scope.getMemoryBlockByVarName(varName);
    return memBlock.getMemoryUnitByIndex(0);
  }

  public void debugInstructions() {
    Log.log(this.functionInstructions.keySet());
    for (String functionName : this.functionInstructions.keySet()) {
      List<LabelledInstructions> instructions = this.functionInstructions.get(functionName);
      Log.log("function-> " + functionName);
      Log.log("total->" + instructions.size() + "instructions-> " + instructions);
      for (LabelledInstructions instruction : instructions) {
        Log.log("Label -> " + instruction.getLabel() + " Instr -> " + instruction.toString());
      }
    }
  }

  public void debugMemory() {
    for (String functionName : this.memory.keySet()) {
      Log.log("current scope name -> " + functionName);
      Scope scope = this.getScopeMemory(functionName);
      scope.debugScopeMemory();
    }
  }

  private Scope getScopeMemory(String functionName) {
    return this.memory.get(functionName);
  }
}
