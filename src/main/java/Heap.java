import java.util.*;

public class Heap {
  public static final int memBlockSize = 4;
  private Map<String, List<LabelledInstructions>> functionInstructions;
  private Map<String, Scope> memory = null;

  public Heap() {
    this.memory = new HashMap<String, Scope>();
    this.functionInstructions = new HashMap<String, List<LabelledInstructions>>();
  }

  public void addFunctionInstructions(
      String functionName, List<LabelledInstructions> instructions) {
    this.functionInstructions.put(functionName, instructions);
  }

  public void debugInstructions() {
    this.log(this.functionInstructions.keySet());
    for (String functionName : this.functionInstructions.keySet()) {
      List<LabelledInstructions> instructions = this.functionInstructions.get(functionName);
      this.log("function-> " + functionName);
      this.log("total->" + instructions.size() + "instructions-> " + instructions);
      for (LabelledInstructions instruction : instructions) {
        this.log("Label -> " + instruction.getLabel() + " Instr -> " + instruction.toString());
      }
    }
  }

  private void log(Object message) {
    System.out.println(message);
  }
}
