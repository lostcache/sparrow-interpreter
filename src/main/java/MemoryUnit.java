class MemoryUnit {
  public static final int size = 4;
  private String valueImage;
  private VariableType type;

  public MemoryUnit(String valueImage, VariableType type) {
    if (valueImage.length() == 0) {
      Failure.failWithMessage("value image cannot be empty string");
    }
    this.valueImage = valueImage;
    this.type = type;
  }

  public boolean isInt() {
    return this.type == VariableType.INTEGER;
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
}
