import java.util.*;

class MemoryBlock {
  private List<MemoryUnit> memoryUnits;

  public MemoryBlock() {
    this.memoryUnits = new ArrayList<MemoryUnit>();
  }

  public void updateMemoryUnit(int index, MemoryUnit unit) {
    this.memoryUnits.set(index, unit);
  }

  public void addMemoryUnit(MemoryUnit memUnit) {
    this.memoryUnits.add(memUnit);
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
    return this.memoryUnits.size();
  }

  public String getValueImageOfMemUnits() {
    String returnString = new String();
    for (MemoryUnit memUnit : this.memoryUnits) {
      returnString += memUnit.getValueImage() + ", ";
    }
    return returnString;
  }

  // private/helper methods
  private MemoryUnit getMemoryUnitByIndex(int index) {
    return this.memoryUnits.get(index);
  }
}
