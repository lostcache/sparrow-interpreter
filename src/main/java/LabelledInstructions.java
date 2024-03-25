import java.util.*;

class LabelledInstructions {
  private String label = new String("");
  private InstructionUnit instructionUnit = null;

  public LabelledInstructions(String label, InstructionUnit instructionUnit) {
    this.label = label;
    if (instructionUnit == null) {
      Log.log("instruction unit cannot be null");
      System.exit(1);
    }
    this.instructionUnit = instructionUnit;
  }

  public InstructionUnit getInstruction() {
    return this.instructionUnit;
  }

  public String getLabel() {
    return new String(this.label);
  }
}
