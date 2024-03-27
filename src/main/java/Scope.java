import java.util.*;

class Scope {
  private Map<String, MemoryBlock> scopeMemory = null;

  public Scope() {
    this.scopeMemory = new HashMap<String, MemoryBlock>();
  }

  public void updateVarInMemory(String varName, MemoryBlock memBlock) {
    if (!isExistingBlockSizeSame(varName, memBlock.getBlockSize())) {
      this.exitBecauseOfUnexpectedBehaviour(
          "Memory block size does not match aka segmentation fault");
    }
    this.scopeMemory.put(varName, memBlock);
  }

  public void addVar(String varName, MemoryBlock memBlock) {
    this.scopeMemory.put(varName, memBlock);
  }

  public void debugScopeMemory() {
    for (String varName : this.scopeMemory.keySet()) {
      MemoryBlock block = this.getMemoryBlockByVarName(varName);
      Log.log(varName + " -> " + block.getValueImageOfMemUnits());
    }
  }

  public MemoryBlock getMemoryBlockByVarName(String varName) {
    return this.scopeMemory.get(varName);
  }

  // private/helper methods
  private boolean variableExists(String varName) {
    return this.getMemoryBlockByVarName(varName) != null;
  }

  private void exitBecauseOfUnexpectedBehaviour(String message) {
    System.out.println(message);
    System.exit(1);
  }

  private boolean isExistingBlockSizeSame(String varName, int newSize) {
    return this.getMemoryBlockByVarName(varName).getBlockSize() == newSize;
  }
}
