import java.util.*;

class MemoryBlock {
  private List<MemoryUnit> memoryUnits;
  private int size;

  public MemoryBlock(int size) {
    this.size = size;
    this.memoryUnits = new ArrayList<MemoryUnit>(size);
  }

  public void updateMemoryUnit(int index, MemoryUnit unit) {
    this.memoryUnits.set(index, unit);
  }

  public String getValueImage(int index) {
    return getMemoryUnitByIndex(index).getValueImage();
  }

  public VariableType getMemoryUnitType(int index) {
    MemoryUnit unit = getMemoryUnitByIndex(index);
    return unit.getType();
  }

  public boolean isMemoryUnitInt(int index) {
    MemoryUnit unit = getMemoryUnitByIndex(index);
    return unit.getType() == VariableType.INTEGER;
  }

  public boolean isMemoryUnitStr(int index) {
    MemoryUnit unit = getMemoryUnitByIndex(index);
    return unit.getType() == VariableType.STRING;
  }

  public boolean isMemoryUnitFunc(int index) {
    MemoryUnit unit = getMemoryUnitByIndex(index);
    return unit.getType() == VariableType.FUNCTION;
  }

  public int getBlockSize() {
    return this.size;
  }

  // private/helper methods
  private MemoryUnit getMemoryUnitByIndex(int index) {
    return this.memoryUnits.get(index);
  }
}
