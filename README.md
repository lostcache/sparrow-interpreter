# Sparrow Interpreter

A Java-based interpreter for the **Sparrow** intermediate representation language - a low-level, assembly-like language designed for educational purposes in compiler construction.

## Overview

The Sparrow interpreter is a two-pass execution engine that processes intermediate representation code. It simulates a runtime environment with:

- Stack-based function calls
- Heap memory allocation
- Jump-based control flow (goto/if-goto)
- First-class function pointers
- Pointer arithmetic for array operations

This project was developed as part of CMSI-585 (Compiler Construction).

## Features

- **Two-Pass Execution**: First pass collects metadata (labels, functions, parameters), second pass executes instructions
- **Function Calling**: Support for nested function calls with function pointers
- **Memory Management**: Separate stack (per-function scope) and heap (global allocation)
- **Control Flow**: Labels, unconditional jumps (goto), and conditional jumps (if-goto)
- **Type System**: INTEGER, STRING, FUNCTION, POINTER, NULL
- **Error Handling**: Null pointer checks and custom error messages

## Getting Started

### Prerequisites

- Java 8 (JavaSE-1.8) or higher
- JavaCC (included in `misc/javacc.jar`)
- Pre-compiled parser (`lib/sparrow-parser.jar`)

## Sparrow Language

### Syntax

Programs consist of function declarations with instructions:

```sparrow
func FunctionName(param1 param2 ...)
    instruction1
    instruction2
    ...
    return return_value
```

### Instructions

| Instruction | Syntax | Description |
|------------|--------|-------------|
| **SetInteger** | `var = 5` | Assign integer literal |
| **SetFuncName** | `var = @FunctionName` | Store function pointer |
| **Add** | `result = var1 + var2` | Addition |
| **Subtract** | `result = var1 - var2` | Subtraction |
| **Multiply** | `result = var1 * var2` | Multiplication |
| **LessThan** | `result = var1 < var2` | Comparison (returns 0 or 1) |
| **Load** | `result = [pointer + offset]` | Load from memory/array |
| **Store** | `[pointer + offset] = value` | Store to memory/array |
| **Move** | `var1 = var2` | Variable assignment |
| **Alloc** | `pointer = alloc(size)` | Heap allocation |
| **Print** | `print(value)` | Output to stdout |
| **Error** | `error("message")` | Print error and exit |
| **Goto** | `goto label` | Unconditional jump |
| **IfGoto** | `if0 condition goto label` | Jump if condition is 0 |
| **Call** | `result = call func(arg1 arg2)` | Function call |

### Example Program

Factorial computation (object-oriented style):

```sparrow
func Main()
    v0 = 4
    w0 = alloc(v0)
    vmt_Fac = alloc(v0)
    v0 = @FacComputeFac
    [vmt_Fac + 0] = v0
    v0 = vmt_Fac
    [w0 + 0] = v0
    if0 w0 goto null1
    w1 = [w0 + 0]
    w1 = [w1 + 0]
    v0 = 6
    w2 = call w1(w0 v0)
    print(w2)
    goto main_end
  null1:
    error("null pointer")
  main_end:
    return v0

func FacComputeFac(this num)
    v0 = 1
    w0 = num < v0
    if0 w0 goto if1_else
      num_aux = 1
      goto if1_end
  if1_else:
      w1 = [this + 0]
      w1 = [w1 + 0]
      v0 = 1
      w2 = num - v0
      w3 = call w1(this w2)
      num_aux = num * w3
  if1_end:
    return num_aux
```

Output: `720` (6! = 720)

## Architecture

### Components

```
┌─────────────────────────────────────────────────────────────┐
│                    Interpreter.java                       │
│                    (Entry Point)                          │
└───────────────┬─────────────────────────────────────────────┘
                │
                ├──> SparrowParser.java (from sparrow-parser.jar)
                │    Parses input → Abstract Syntax Tree (AST)
                │
                ├──> Heap.java
                │    Memory manager & metadata store
                │
                ├──> LabelledInstructionGatherer.java (Pass 1)
                │    Collects functions, labels, parameters
                │
                └──> Executor.java (Pass 2)
                     Executes instructions sequentially
                     │
                     ├──> Scope.java
                     │    Function call frames (stack)
                     │
                     └──> MemoryUnit.java
                          Variable containers with types
```

**Key Classes:**

- **`Interpreter.java`**: Entry point that orchestrates parsing and execution (located at `src/main/java/Interpreter.java`)
- **`Executor.java`**: Instruction execution engine with visitor pattern (`src/main/java/Executor.java:4`)
- **`LabelledInstructionGatherer.java`**: Preprocessing pass to build execution metadata
- **`Heap.java`**: Central memory and metadata manager
- **`Scope.java`**: Represents a function's call frame with local variables
- **`MemoryUnit.java`**: Container for variable values with type information
- **`VariableType.java`**: Enum defining INTEGER, STRING, FUNCTION, POINTER, NULL

### Execution Flow

1. **Parse Phase**:
   - Read Sparrow code from stdin
   - `SparrowParser` generates AST

2. **Metadata Collection (Pass 1)**:
   - `LabelledInstructionGatherer` traverses AST
   - Collects all functions and their instructions
   - Maps labels to instruction indices
   - Extracts function parameters
   - Populates `Heap` with metadata

3. **Execution (Pass 2)**:
   - `Executor` starts from `Main()` function
   - For each instruction:
     - Fetch at current program counter
     - Execute via visitor pattern
     - Increment program counter
     - On `goto`/`if-goto`: Jump to new location
     - On `call`: Create new scope, execute function
     - On `return`: Destroy scope, return to caller

### Memory Model

**Stack:**
- Each function gets a `Scope` object representing its call frame
- Local variables stored with stack addresses starting at 4
- Automatically destroyed on function return

**Heap:**
- Allocated via `alloc(size)` instruction
- Returns pointer (memory address) as integer
- Used for arrays and object-like structures
- Persists across function calls
- Memory unit size: 4 bytes (`MemoryUnit.size`)

**Example:**
```sparrow
v0 = 4              // v0 = 4
w0 = alloc(v0)      // Allocate 4 bytes on heap
[w0 + 0] = v0       // Store 4 at address w0
w1 = [w0 + 0]       // Load value from w0 into w1
```

### Available Tests

1. **Factorial.sparrow** - Recursive factorial with OO-style method calls
2. **QuickSort.sparrow** - Quicksort algorithm with array operations
3. **ManyCalls.sparrow** - Tests multiple function calls with many arguments
4. **CalleeSave.sparrow** - Tests callee-save register conventions
5. **ManyArgs2.sparrow** - Function calls with many arguments
6. **strech.sparrow** - Stress test for complex scenarios

### Grammar

The Sparrow grammar is defined in `misc/sparrow.jj` (JavaCC format). To regenerate the parser:

1. Modify `misc/sparrow.jj`
2. Run JavaCC: `java -jar misc/javacc.jar misc/sparrow.jj`
3. Run JTB: `java -jar misc/jtb.jar misc/sparrow.jj`
4. Compile generated files into `lib/sparrow-parser.jar`

### Notable Implementation Details

- **Memory Unit Size**: Fixed at 4 bytes
- **Heap Start Address**: 4 (address 0 reserved for null)
- **Values**: All stored as strings, parsed as needed
- **Visitor Pattern**: Extensive use for AST traversal
- **Program Counter**: Manual management for control flow

