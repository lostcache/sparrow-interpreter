import syntaxtree.*;

class LabelledInstructionElement {
  private Instruction instruction = null;
  private String returnStatementIdentifier = null;

  public LabelledInstructionElement(Instruction instruction) {
    this.instruction = instruction;
    this.returnStatementIdentifier = null;
  }

  public LabelledInstructionElement(String id) {
    this.instruction = null;
    this.returnStatementIdentifier = id;
  }

  public boolean isInstruction() {
    return this.instruction != null;
  }

  public boolean isReturnStatement() {
    return this.returnStatementIdentifier != null;
  }
}
