class MemoryAddress {
  private int address = 0;
  private String strValue = null;

  public MemoryAddress(int address) {
    this.address = address;
    this.strValue = String.valueOf(address);
  }

  public String getStrValue() {
    return new String(strValue);
  }

  public int getIntValue() {
    return this.address;
  }
}
