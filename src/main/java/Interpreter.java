import java.io.InputStream;

import syntaxtree.*;
import visitor.*;

public class Interpreter {
    public static void main (String [] args) throws ParseException {
      // main entrance starts here

      // get a syntax tree from the parser
      InputStream in = System.in;
      new SparrowParser(in);
      Node root = SparrowParser.Program();

    }
}
