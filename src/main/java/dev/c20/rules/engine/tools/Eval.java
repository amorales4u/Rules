package dev.c20.rules.engine.tools;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import groovy.lang.GroovyRuntimeException;
import groovy.lang.MissingPropertyException;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.groovy.control.MultipleCompilationErrorsException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class Eval {

    ClassLoader parent;
    GroovyClassLoader loader;
    Map<String,GroovyObject> cache = new HashMap<>();

    static private Eval instance;

    static public Eval getInstance() {
        if( instance == null ) {
            instance = new Eval();
        }
        return instance;
    }

    private Eval() {

        parent = Eval.class.getClassLoader();
        loader = new GroovyClassLoader(parent);

    }

    public EvalResult run(String code, Object context, String expressionName ) {

        try {
            Class groovyClass = null;
            GroovyObject groovyObject = null;
            if( !cache.containsKey(expressionName) ) {
                groovyClass = loader.parseClass(code);
                groovyObject = (GroovyObject)groovyClass.newInstance();
                cache.put(expressionName,groovyObject);
            } else {
                groovyObject = cache.get(expressionName);
            }

            groovyObject.setProperty("context", context);
            groovyObject.setProperty("expressionName", expressionName);

            Object response = groovyObject.invokeMethod("run", null);

            loader.close();

            return new EvalResult(false, null, response);

        } catch( MultipleCompilationErrorsException ex ) {
            log.error("Compilation:",ex);
            return new EvalResult(true, "Compilation error:" + ex.getMessage(), null);
        } catch( MissingPropertyException ex ) {
            log.error("Missing property:",ex);
            return new EvalResult(true, "Missing property error:" + ex.getMessage(), null);
        } catch( GroovyRuntimeException ex ) {
            log.error("Runtime exception:",ex);
            return new EvalResult(true, "Runtime exception error:" + ex.getMessage(), null);
        } catch( Exception ex ) {
            log.error("Exception:",ex);
            return new EvalResult(true, "Exception error:" + ex.getMessage(), null);
        }

    }
}
