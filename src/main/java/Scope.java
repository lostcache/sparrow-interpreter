import java.util.*;

class Scope {
  private int addressStartValue = 0;
  private Map<String, MemoryAddress> identifierToAddressMap = null;
  private Map<String, MemoryUnit> scopeMemory = null;
  private Map<String, Integer> identifierSizeMap = null;

  public Scope() {
    this.identifierToAddressMap = new HashMap<String, MemoryAddress>();
    this.scopeMemory = new HashMap<String, MemoryUnit>();
    this.identifierSizeMap = new HashMap<String, Integer>();
  }

  public void createVariable(String identifier, MemoryUnit memUnit, int size) {
    MemoryAddress address = new MemoryAddress(this.addressStartValue);
    this.identifierToAddressMap.put(identifier, address);
    this.scopeMemory.put(address.getStrValue(), memUnit);
    this.identifierSizeMap.put(identifier, size);
    this.incrementAddressStartValue();
  }

  public void putValueInAddress(int address, MemoryUnit memUnit) {
    MemoryAddress desiredAddress = new MemoryAddress(address);
    this.scopeMemory.put(desiredAddress.getStrValue(), memUnit);
    this.incrementAddressStartValue();
  }

  public MemoryUnit getIdentifierValue(String identifier) {
    MemoryAddress memoryAddress = this.getAddressOfIdentifier(identifier);
    return this.getMemoryUnitByAddress(memoryAddress);
  }

  public MemoryUnit getIdentifierValueWithOffset(String identifier, int offset) {
    MemoryAddress memoryAddress = this.getAddressOfIdentifier(identifier);
    int desiredAddress = memoryAddress.getIntValue() + offset;
    return this.getMemoryUnitByAddress(new MemoryAddress(desiredAddress));
  }

  public MemoryAddress getAddressOfIdentifier(String identifier) {
    return this.identifierToAddressMap.get(identifier);
  }

  public void moveValue(String lhs, String rhs) {
    MemoryAddress rhsMemAddress = this.getAddressOfIdentifier(rhs);
    int rhsSize = this.getIdentifierSize(rhs);
    this.identifierToAddressMap.put(lhs, rhsMemAddress);
    this.identifierSizeMap.put(lhs, rhsSize);
  }

  private MemoryUnit getMemoryUnitByAddress(MemoryAddress address) {
    return this.scopeMemory.get(address.getStrValue());
  }

  private void incrementAddressStartValue() {
    this.addressStartValue += MemoryUnit.size;
  }

  public void debugScopeMemory() {
    for (String identifier : identifierToAddressMap.keySet()) {
      String idValueString = "";
      int identifierSize = this.getIdentifierSize(identifier);
      MemoryAddress memAddress = this.getAddressOfIdentifier(identifier);
      for (int i = 0; i < identifierSize; i++) {
        MemoryAddress desiredAddress = new MemoryAddress(memAddress.getIntValue() + (i * MemoryUnit.size));
        idValueString += (this.getMemoryUnitByAddress(desiredAddress).getValueImage() + ", ");
      }
      Log.log(identifier  + "[" + identifierSize + "]"+ " -> " + idValueString);
    }
  }

  private int getIdentifierSize(String identifier) {
    return this.identifierSizeMap.get(identifier);
  }
}
