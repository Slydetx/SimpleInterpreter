# Simple Interpreter

A tree-walking interpreter for a small custom programming language, written in Java.
Built as part of a JetBrains internship application.

---

## Building and Running

**Requirements:** Java 24+, Maven

**Build:**
```bash
mvn package
```
**Run (interactive):**
```bash
java -jar target/simple-interpreter-1.0.0.jar
```
Type your program and **press `Enter` after a blank line to execute.**

**Run tests:**
```bash
mvn test
```

---

## Language

The interpreter reads a program from standard input, executes it, and prints the final values of all global variables.

### Statements

Statements are separated by newlines. Commas `,` are used to separate multiple 
statements inside function bodies `{ }` and while loop bodies.

| Statement | Syntax |
|---|---|
| Assignment | `x = <expression>` |
| If/else | `if <condition> then <statement> else <statement>` |
| While loop | `while <condition> do <statement>, <statement>, ...` |
| Function definition | `fun name(params) { <statement>, ... }` |
| Return | `return <expression>` |

> **Note:** Bare expressions are not valid statements. The result of an expression must always be assigned to a variable. For example `add(2, 3)` alone is not a valid statement — use `x = add(2, 3)` instead.

### Expressions

Arithmetic and comparison expressions with standard operator precedence:

| Operators | Description |
|---|---|
| `* /` | Multiplication, integer division |
| `+ -` | Addition, subtraction |
| `> >= < <= == !=` | Comparison (returns 1 for true, 0 for false) |

Parentheses can be used to override precedence: `(2 + 3) * 4`.

### Types

The language has a single numeric type — **integer**. Booleans are represented as integers: `true` = 1, `false` = 0. Any non-zero value is truthy.

> **Note:** Integer division truncates toward zero — `10 / 3 = 3`.

> **Note:** Negative number literals are not supported. Negative values can be produced via subtraction: `x = 0 - 5`.

### Functions

Functions are defined with `fun` and called by name. They support recursion and nested calls.

```
fun add(a, b) { return a + b }
result = add(2, 3)
```

Function parameters are local to the function scope and do not affect global variables.

### While loops

The while body supports multiple comma-separated statements after `do`.
If the while loop is inside a function, a `return` reached anywhere in the while body —
even when nested inside an `if` branch — immediately exits the function and returns the value.

Commas after `do` are **greedily consumed** as part of the while body. This matters in:

```
fun fact_iter(n) { r = 1, while true do if n == 0 then return r else r = r * n, n = n - 1 }
```

This function body has two comma-separated parts:
- `r = 1` → executed once on function entry
- `while true do ...` → the while loop

---

## Sample Programs

**Arithmetic:**
```
x = 2
y = (x + 2) * 2
```
```
x: 2
y: 8
```

**If/else:**
```
x = 20
if x > 10 then y = 100 else y = 0
```
```
x: 20
y: 100
```

**While loop:**
```
x = 0
y = 0
while x < 3 do if x == 1 then y = 10 else y = y + 1, x = x + 1
```
```
x: 3
y: 11
```

**Function:**
```
fun add(a, b) { return a + b }
four = add(2, 2)
```
```
four: 4
```

**Recursive factorial:**
```
fun fact_rec(n) { if n <= 0 then return 1 else return n * fact_rec(n - 1) }
a = fact_rec(5)
```
```
a: 120
```

**Iterative factorial:**
```
fun fact_iter(n) { r = 1, while true do if n == 0 then return r else r = r * n, n = n - 1 }
b = fact_iter(5)
```
```
b: 120
```

---

### Design Decisions

**Integer-only arithmetic** — the language uses a single integer type. Division is integer division — `10 / 3 = 3`.

**Booleans as integers** — `true` and `false` are syntactic sugar for `1` and `0`. Any non-zero value is truthy, which allows expressions like `while true do ...` and `if x then ...`.

**No negative literals** — the tokenizer does not handle unary minus. Negative values must be computed via subtraction: `x = 0 - 5`.

**Comma-separated statements** — commas are only valid inside function bodies and while loop bodies. Writing `x = 10, y = 20` at the top level will silently ignore everything after the comma — only `x: 10` is printed.

**Bare expressions not allowed** — the result of an expression must always be assigned to a variable. `add(2, 3)` alone is not a valid statement — use `x = add(2, 3)` instead.
