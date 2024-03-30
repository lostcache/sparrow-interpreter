import java.io.InputStream;
import syntaxtree.*;
import visitor.*;

public class Interpreter {
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
    // heap.debugFunctionParams();

    // heap.debugInstructions();

    Executor instExec = new Executor();
    instExec.startExecution(heap);

    // heap.debugMemory();
  }
}

