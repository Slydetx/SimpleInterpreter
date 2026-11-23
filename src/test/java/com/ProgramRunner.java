package com;

import java.util.Map;

/** Shared harness for the interpreter test classes. */
final class ProgramRunner {

  private ProgramRunner() {}

  /** Runs a slydet program and returns its global variables. */
  static Map<String, Object> run(String input) {
    return new Program().execute(input).getVariables();
  }
}
