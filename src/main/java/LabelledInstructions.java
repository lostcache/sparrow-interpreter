import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import syntaxtree.*;

class LabelledInstructions {
  private Map<String, List<LabelledInstructionElement>> labelledInstructions;

  public LabelledInstructions() {
    this.labelledInstructions = new HashMap<String, List<LabelledInstructionElement>>();
  }

  public List<LabelledInstructionElement> getInstructionByLabel(String label) {
    return new ArrayList<LabelledInstructionElement>(this.labelledInstructions.get(label));
  }

  public void put(String label, List<LabelledInstructionElement> instructions) {
    if (label != null && instructions.size() > 0) {
      this.labelledInstructions.put(label, instructions);
    }
  }

  public void debug() {
    System.out.println("------ Printing Labelled Instructions ------");
    for (String label : this.labelledInstructions.keySet()) {
      System.out.printf("Label -> %s\n", label);
      List<LabelledInstructionElement> instructions = labelledInstructions.get(label);
      if (instructions != null) {
        for (LabelledInstructionElement element : instructions) {
          System.out.println(element);
        }
      }
    }
  }
}
