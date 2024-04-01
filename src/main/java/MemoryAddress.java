class MemoryAddress {
  private int address = 0;

  public MemoryAddress(int address) {
    this.address = address;
  }

  public MemoryAddress(String address) {
    this.address = Integer.parseInt(address);
  }

  public String getStrValue() {
    return String.valueOf(this.address);
  }

  public int getIntValue() {
    return this.address;
  }
}
