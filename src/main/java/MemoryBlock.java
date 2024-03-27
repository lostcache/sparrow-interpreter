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

  public MemoryUnit getMemoryUnitByIndex(int index) {
    MemoryUnit memUnit = this.memoryUnits.get(index);
    return new MemoryUnit(memUnit.getValueImage(), memUnit.getType());
  }
}
