import java.io.InputStream;
import syntaxtree.*;
import visitor.*;

public class Interpreter {
  private static final boolean debug = true;

  public static void main(String[] args) throws ParseException {
    // main entrance starts here

    // get a syntax tree from the parser
    InputStream in = System.in;
    new SparrowParser(in);
    Node root = SparrowParser.Program();

    // initialize heap
    Heap heap = new Heap();

    LabelledInstructions labelledInstructions = new LabelledInstructions();

    LabelledInstructionGatherer labelledInstructionGatherer = new LabelledInstructionGatherer();

    // collect all the labelled instructions in the first pass to implement goto.
    root.accept(labelledInstructionGatherer, labelledInstructions);

    if (debug) labelledInstructions.debug();
  }
}
