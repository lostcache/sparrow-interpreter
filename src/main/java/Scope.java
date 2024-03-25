import java.util.*;

class Scope {
  private Map<String, MemoryBlock> scopeMemory = null;

  public Scope() {
    this.scopeMemory = new HashMap<String, MemoryBlock>();
  }

  public void addToMemory(String varName, MemoryBlock memBlock) {
    this.scopeMemory.put(varName, memBlock);
  }

  public void updateVarInMemory(String varName, MemoryBlock memBlock) {
    if (!isExistingBlockSizeSame(varName, memBlock.getBlockSize())) {
      this.exitBecauseOfUnexpectedBehaviour(
          "Memory block size does not match aka segmentation fault?");
    }
    this.scopeMemory.put(varName, memBlock);
  }

  // private/helper methods
  private boolean variableExists(String varName) {
    return this.getMemoryBlockByVarName(varName) != null;
  }

  private MemoryBlock getMemoryBlockByVarName(String varName) {
    return this.scopeMemory.get(varName);
  }

  private void exitBecauseOfUnexpectedBehaviour(String message) {
    System.out.println(message);
    System.exit(1);
  }

  private boolean isExistingBlockSizeSame(String varName, int newSize) {
    return this.getMemoryBlockByVarName(varName).getBlockSize() == newSize;
  }
}
