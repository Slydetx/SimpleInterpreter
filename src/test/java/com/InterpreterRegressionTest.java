package com;

import com.interpreter.InterpreterException;
import org.junit.jupiter.api.Test;

import static com.ProgramRunner.run;
import static org.junit.jupiter.api.Assertions.*;

/**
 * One test per fixed defect, named after its issue so a failure says what regressed.
 */
public class InterpreterRegressionTest {

  /**
   * Too few arguments left the parameter unbound, so the body resolved it to the
   * global of the same name and returned 100 instead of failing.
   */
  @Test
  void test_issue2_tooFewArguments_throws() {
    assertThrows(InterpreterException.class,
        () -> run("""
            b = 100
            fun f(a, b) { return b }
            r = f(1)
            """));
  }

  /** Too many arguments indexed past the parameter list: IndexOutOfBoundsException. */
  @Test
  void test_issue13_tooManyArguments_throws() {
    assertThrows(InterpreterException.class,
        () -> run("""
            fun f(a) { return a }
            r = f(1, 2)
            """));
  }
}
