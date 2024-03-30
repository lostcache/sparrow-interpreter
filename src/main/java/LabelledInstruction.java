import java.util.*;

class LabelledInstruction {
  private String label = new String("");
  private InstructionUnit instructionUnit = null;

  public LabelledInstruction(String label, InstructionUnit instructionUnit) {
    this.label = label;
    if (instructionUnit == null) {
      Log.log("instruction unit cannot be null");
      System.exit(1);
    }
    this.instructionUnit = instructionUnit;
  }

  public InstructionUnit getInstructionUnit() {
    return this.instructionUnit;
  }

  public String getLabel() {
    return new String(this.label);
  }
}
