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

    // init heap
    Heap heap = new Heap();

    LabelledInstructionGatherer labelledInstructionGatherer = new LabelledInstructionGatherer();
    // collect all the labelled instructions in the first pass to implement goto.
    root.accept(labelledInstructionGatherer, heap);

    // init executor
    Executor exec = new Executor();
    root.accept(exec, heap);

    // if (debug) heap.debugInstructions();
    if (debug) heap.debugMemory();
  }
}
