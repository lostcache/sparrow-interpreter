import syntaxtree.*;

// An Instruction unit is either a Instruction or a return statment.
// this has to be becuase for some reason return statement is not an Instruction.
class InstructionUnit {
  private Instruction instruction = null;
  private String returnStatementIdentifier = null;

  public InstructionUnit(Instruction instruction) {
    this.instruction = instruction;
    this.returnStatementIdentifier = null;
  }

  public InstructionUnit(String id) {
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
