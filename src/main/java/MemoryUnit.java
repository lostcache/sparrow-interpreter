class MemoryUnit {
  public static final int size = 4;
  private String valueImage;
  private VariableType type;

  public MemoryUnit(String valueImage, VariableType type) {
    this.valueImage = valueImage;
    this.type = type;
  }

  public boolean isInt() {
    return this.type == VariableType.INTEGER;
  }

  public int getIntValue() {
    if (!this.isInt()) {
      this.unexpectedError("Cannot get integer value because is not an integer");
    }
    return Integer.parseInt(this.valueImage);
  }

  public boolean isStr() {
    return this.type == VariableType.STRING;
  }

  public boolean isFunc() {
    return this.type == VariableType.FUNCTION;
  }

  public String getValueImage() {
    return new String(this.valueImage);
  }

  public VariableType getType() {
    return this.type;
  }

  public boolean isNULL() {
    return this.valueImage.length() == 0 && this.type == VariableType.NULL;
  }

  public boolean isRef() {
    return this.type == VariableType.POINTER;
  }

  private void unexpectedError(String message) {
    Log.log(message);
    System.exit(1);
  }
}
