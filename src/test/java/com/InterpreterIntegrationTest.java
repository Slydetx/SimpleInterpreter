package com;

import com.interpreter.InterpreterException;
import com.parser.ParseException;
import com.parser.Parser;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class InterpreterIntegrationTest {

    private Map<String, Object> run(String input) {
        return new Program().execute(input).getVariables();
    }

    // Programs from the assignment
    // -------------------------------------------------------------------------

    @Test
    void test_firstExample() {
        Map<String,Object> variables =
                run("""
                x = 2
                y = (x + 2) * 2
                """);
        assertEquals(2, variables.get("x"));
        assertEquals(8, variables.get("y"));
    }

    @Test
    void test_secondExample() {
        Map<String,Object> variables
                = run("""
                x = 20
                if x > 10 then y = 100 else y = 0
                """);
        assertEquals(20, variables.get("x"));
        assertEquals(100, variables.get("y"));
    }

    @Test
    void test_thirdExample() {
        Map<String,Object> variables = run("""
                x = 5
                if x > 10 then y = 100 else y = 0
                """);
        assertEquals(5, variables.get("x"));
        assertEquals(0, variables.get("y"));
    }

    @Test
    void test_forthExample() {
        Map<String,Object> variables = run("""
                x = 0
                y = 0
                while x < 3 do if x == 1 then y = 10 else y = y + 1, x = x + 1
                """);
        assertEquals(3, variables.get("x"));
        assertEquals(11, variables.get("y"));
    }

    @Test
    void test_fifthExample() {
        Map<String,Object> variables = run("""
                fun add(a, b) { return a + b }
                four = add(2, 2)
                """);
        assertEquals(4, variables.get("four"));
    }

    @Test
    void test_sixthExample() {
        Map<String,Object> variables = run("""
                fun fact_rec(n) { if n <= 0 then return 1 else return n * fact_rec(n - 1) }
                a = fact_rec(5)
                """);
        assertEquals(120, variables.get("a"));
    }


    // Mathematical Expressions
    // -------------------------------------------------------------------------


    @Test
    void test_parenthesesPrecedence() {
        Map<String,Object> variables = run("x = (2 + 3) * 4");
        assertEquals(20, variables.get("x"));
    }

    @Test
    void test_integerDivision() {
        Map<String,Object> variables = run("x = 10 / 3");
        assertEquals(3, variables.get("x"));
    }

    @Test
    void test_divisionByZero() {
        assertThrows(InterpreterException.class, () -> run("x = 10 / 0"));
    }

    @Test
    void test_nestedParentheses() {
        Map<String,Object> variables = run("x = ((4 - 2) * (3 + 4)) / 7");
        assertEquals(2, variables.get("x"));
    }

    // Comparison operators
    // -------------------------------------------------------------------------

    @Test
    void test_equalityTrue() {
        Map<String,Object> variables = run("x = 5 == 5");
        assertEquals(1, variables.get("x"));
    }

    @Test
    void test_equalityFalse() {
        Map<String,Object> variables = run("x = 5 == 6");
        assertEquals(0, variables.get("x"));
    }

    @Test
    void test_notEqual() {
        Map<String,Object> variables = run("x = 5 != 6");
        assertEquals(1, variables.get("x"));
    }

    @Test
    void test_greaterThan() {
        Map<String,Object>  variables = run("x = 10 > 3");
        assertEquals(1, variables.get("x"));
    }

    @Test
    void test_lessThanOrEqual() {
        Map<String,Object>  variables = run("x = 3 <= 3");
        assertEquals(1, variables.get("x"));
    }

    @Test
    void test_greaterThanOrEqual() {
        Map<String,Object>  variables = run("x = 4 >= 5");
        assertEquals(0, variables.get("x"));
    }

    // Boolean tokens
    // -------------------------------------------------------------------------

    @Test
    void test_trueToken() {
        Map<String,Object>  variables = run("x = true");
        assertEquals(1, variables.get("x"));
    }

    @Test
    void test_falseToken() {
        Map<String,Object>  variables = run("x = false");
        assertEquals(0, variables.get("x"));
    }

    // Functions
    // -------------------------------------------------------------------------

    @Test
    void test_nestedFunctionCalls() {
        Map<String,Object>  variables = run("""
                fun add(a, b) { return a + b }
                fun mul(a, b) { return a * b }
                x = mul(add(2, 3), add(4, 5))
                """);
        assertEquals(45, variables.get("x"));
    }

    @Test
    void test_fibonacci() {
        Map<String,Object>  variables = run("""
                fun fib(n) { if n <= 1 then return n else return fib(n - 1) + fib(n - 2) }
                a = fib(10)
                """);
        assertEquals(55, variables.get("a"));
    }

    @Test
    void test_recursiveFactorial_6() {
        Map<String,Object>  variables = run("""
                fun fact_rec(n) { if n <= 0 then return 1 else return n * fact_rec(n - 1) }
                a = fact_rec(6)
                """);
        assertEquals(720, variables.get("a"));
    }

    // While loops
    // -------------------------------------------------------------------------

    @Test
    void test_countdown() {
        Map<String,Object>  variables = run("""
                x = 10
                y = 0
                while x > 0 do y = y + x, x = x - 1
                """);
        assertEquals(0, variables.get("x"));
        assertEquals(55, variables.get("y"));
    }

    @Test
    void test_whileWithTrueCondition() {
        Map<String,Object>  variables = run("""
            fun loop(n) { while true do if n == 3 then return n else n = n + 1 }
            x = loop(0)
            """);
        assertEquals(3, variables.get("x"));
    }

    // Error handling
    // -------------------------------------------------------------------------

    @Test
    void test_undefinedVariable() {
        assertThrows(Exception.class, () -> run("x = y + 1"));
    }

    @Test
    void test_undefinedFunction() {
        assertThrows(Exception.class, () -> run("x = foo(1, 2)"));
    }
}