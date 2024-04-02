import java.util.*;

class Scope {
  private int stackStartAddress = 0;
  private Map<String, MemoryAddress> identifierToAddressMap = null;
  private Map<String, MemoryUnit> memory = null;
  private Map<String, Integer> identifierSizeMap = null;

  public Scope() {
    this.identifierToAddressMap = new HashMap<String, MemoryAddress>();
    this.memory = new HashMap<String, MemoryUnit>();
    this.identifierSizeMap = new HashMap<String, Integer>();
  }

  public void putIdentifierInMemory(String identifier, MemoryUnit memUnit, int size) {
    MemoryAddress address = new MemoryAddress(this.stackStartAddress);
    this.createAddressValuePair(address, memUnit);
    this.createIdentifierSizePair(identifier, size);
    this.createIdentifierAddressPair(identifier, address);
    this.incrementAddressStartValue();
  }

  public void moveIdentifiers(String lhs, String rhs) {
    MemoryUnit rhsMemUnit = this.getMemoryUnitByIdentifier(rhs);
    MemoryUnit lhsMemUnit = new MemoryUnit(rhsMemUnit.getValueImage(), rhsMemUnit.getType());
    if (this.identifierExists(lhs)) {
      this.updateMemUnitOfIdentifier(lhs, lhsMemUnit);
    } else {
      this.putIdentifierInMemory(lhs, lhsMemUnit, 1);
    }
  }

  public String fetchValueImage(String identifier) {
    return this.getMemoryUnitByIdentifier(identifier).getValueImage();
  }

  public MemoryUnit getDereferencedValue(String identifier) {
    return this.getDereferencedMemoryUnitByIdentifier(identifier);
  }

  public void updateMemUnitOfIdentifier(String identifier, MemoryUnit memUnit) {
    MemoryAddress memAddress = this.getAddressByIdentifier(identifier);
    this.memory.put(memAddress.getStrValue(), memUnit);
  }

  public boolean identifierExists(String identifier) {
    return this.getMemoryUnitByIdentifier(identifier) != null;
  }

  public int addIdentifiers(String op1, String op2) {
    MemoryUnit op1MemUnit = this.getDereferencedMemoryUnitByIdentifier(op1);
    MemoryUnit op2MemUnit = this.getDereferencedMemoryUnitByIdentifier(op2);
    if (!op1MemUnit.isInt() || !op2MemUnit.isInt()) {
      Log.log("operands must be int to be added");
      System.exit(1);
    }
    return op1MemUnit.getIntValue() + op2MemUnit.getIntValue();
  }

  public int subtractIdentifiers(String op1, String op2) {
    MemoryUnit op1MemUnit = this.getDereferencedMemoryUnitByIdentifier(op1);
    MemoryUnit op2MemUnit = this.getDereferencedMemoryUnitByIdentifier(op2);
    if (!op1MemUnit.isInt() || !op2MemUnit.isInt()) {
      Log.log("operands must be int to be subtracted");
      System.exit(1);
    }
    return op1MemUnit.getIntValue() - op2MemUnit.getIntValue();
  }

  public int multiplyIdentifiers(String op1, String op2) {
    MemoryUnit op1MemUnit = this.getDereferencedMemoryUnitByIdentifier(op1);
    MemoryUnit op2MemUnit = this.getDereferencedMemoryUnitByIdentifier(op2);
    if (!op1MemUnit.isInt() || !op2MemUnit.isInt()) {
      Log.log("operands must be int to be multiplied");
      System.exit(1);
    }
    return op1MemUnit.getIntValue() * op2MemUnit.getIntValue();
  }

  public int compareIdentifiers(String op1, String op2) {
    MemoryUnit op1MemUnit = this.getDereferencedMemoryUnitByIdentifier(op1);
    MemoryUnit op2MemUnit = this.getDereferencedMemoryUnitByIdentifier(op2);
    if (!op1MemUnit.isInt() || !op2MemUnit.isInt()) {
      Log.log("operands must be int to be compared");
      System.exit(1);
    }
    if (op1MemUnit.getIntValue() < op2MemUnit.getIntValue()) {
      return 1;
    } else {
      return 0;
    }
  }

  private MemoryUnit getDereferencedMemoryUnitByIdentifier(String id) {
    MemoryUnit memUnit = this.getMemoryUnitByIdentifier(id);
    if (!memUnit.isRef()) {
      return memUnit;
    }
    return dereferenceMemoryUnit(memUnit);
  }

  private MemoryUnit dereferenceMemoryUnit(MemoryUnit memUnit) {
    MemoryUnit memUnitToReturn = memUnit;
    while (memUnitToReturn.isRef()) {
      String addrString = memUnitToReturn.getValueImage();
      memUnitToReturn  = this.getMemoryUnitByAddress(addrString);
    }
    return memUnitToReturn;
  }

  private void createIdentifierSizePair(String identifier, int size) {
    this.identifierSizeMap.put(identifier, size);
  }

  private void createAddressValuePair(MemoryAddress address, MemoryUnit memUnit) {
    this.memory.put(address.getStrValue(), memUnit);
  }

  private void createIdentifierAddressPair(String identifier, MemoryAddress address) {
    this.identifierToAddressMap.put(identifier, address);
  }

  public MemoryUnit getMemoryUnitByIdentifier(String identifier) {
    MemoryAddress memoryAddress = this.getAddressByIdentifier(identifier);
    if (memoryAddress == null) {
      return null;
    }
    return this.getMemoryUnitByAddress(memoryAddress);
  }

  private MemoryAddress getAddressByIdentifier(String identifier) {
    return this.identifierToAddressMap.get(identifier);
  }

  private MemoryUnit getMemoryUnitByAddress(MemoryAddress address) {
    return this.memory.get(address.getStrValue());
  }

  private MemoryUnit getMemoryUnitByAddress(String address) {
    return this.memory.get(address);
  }

  public int getSizeByIdentfier(String id) {
    return this.identifierSizeMap.get(id);
  }

  private void incrementAddressStartValue() {
    this.stackStartAddress += MemoryUnit.size;
  }

  public void debugScopeMemory() {
    for (String identifier : identifierToAddressMap.keySet()) {
      String idValueString = "";
      MemoryUnit memUnit = this.getMemoryUnitByIdentifier(identifier);
      if (memUnit.isRef()) {
        String firstUnitAddr = memUnit.getValueImage();
        int size = this.getSizeByIdentfier(identifier);
        for (int i = 0; i < size; i++) {
          int address = Integer.parseInt(firstUnitAddr) + (i * MemoryUnit.size);
          MemoryUnit valueMemUnit = this.getDereferencedMemoryUnitByAddress(String.valueOf(address));
          idValueString += (valueMemUnit.getValueImage() + ", ");
        }
      } else {
        idValueString += memUnit.getValueImage();
      }
      Log.log(identifier + " -> " + idValueString);
    }
  }

  private MemoryUnit getDereferencedMemoryUnitByAddress(String address) {
    MemoryUnit memUnitToReturn = this.getMemoryUnitByAddress(address);
    if (!memUnitToReturn.isRef()) {
      return memUnitToReturn;
    }
    while (memUnitToReturn.isRef()) {
      String addrString = memUnitToReturn.getValueImage();
      memUnitToReturn  = this.getMemoryUnitByAddress(addrString);
    }
    return memUnitToReturn;
  }

  private int getIdentifierSize(String identifier) {
    return this.identifierSizeMap.get(identifier);
  }
}
