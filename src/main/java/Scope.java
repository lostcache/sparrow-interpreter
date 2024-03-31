import java.util.*;

class Scope {
  private int addressStartValue = 0;
  private Map<String, MemoryAddress> identifierToAddressMap = null;
  private Map<String, MemoryUnit> scopeMemory = null;

  public Scope() {
    this.identifierToAddressMap = new HashMap<String, MemoryAddress>();
    this.scopeMemory = new HashMap<String, MemoryUnit>();
  }

  public void createVariable(String identifier, MemoryUnit memUnit) {
    MemoryAddress address = new MemoryAddress(this.addressStartValue);
    this.identifierToAddressMap.put(identifier, address);
    this.scopeMemory.put(address.getStrValue(), memUnit);
    this.addressStartValue += MemoryUnit.size;
  }

  public void putValueinAddress(int address, MemoryUnit memUnit) {
    MemoryAddress desiredAddress = new MemoryAddress(address);
    this.scopeMemory.put(desiredAddress.getStrValue(), memUnit);
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
    this.identifierToAddressMap.put(lhs, rhsMemAddress);
  }

  private MemoryUnit getMemoryUnitByAddress(MemoryAddress address) {
    return this.scopeMemory.get(address.getStrValue());
  }

  public void debugScopeMemory() {
    for (String identifier : identifierToAddressMap.keySet()) {
      Log.log("id -> " + identifier + " -> " + this.getAddressOfIdentifier(identifier).getStrValue());
    }
    for (String memAddress : this.scopeMemory.keySet()) {
      Log.log("add -> " + memAddress + " -> " + this.scopeMemory.get(memAddress));
    }
  }
}
