package de.serverlessbuch.slack.command;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

/**
 * @author Niko Köbler, http://www.n-k.de, @dasniko
 */
@Slf4j
class CalculatorCommand {

    private static ScriptEngine scriptEngine = (new ScriptEngineManager().getEngineByName("JavaScript"));

    @SneakyThrows
    static String processCommand(String expression) {
        log.info("Try to evaluate {}", expression);
        String result = scriptEngine.eval(expression).toString();
        log.info("Got result: {}", result);
        return expression + " = " + result;
    }

}
