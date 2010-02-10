package com.tinkerpop.webling.functions;

import com.tinkerpop.gremlin.functions.FunctionHelper;
import com.tinkerpop.webling.functions.util.*;
import com.tinkerpop.gremlin.statements.EvaluationException;
import org.apache.commons.jxpath.Function;
import org.apache.commons.jxpath.Functions;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * @author Pavel A. Yaskevich 
 */
public class WeblingFunctions implements Functions {

    public static final String NAMESPACE_PREFIX = "w";

    private static Set<String> namespaces = new HashSet<String>();
    private static Map<String, Function> functionMap = new HashMap<String, Function>();

    static {
        namespaces.add(NAMESPACE_PREFIX);
        functionMap.put(VisualizationJSONFunction.FUNCTION_NAME, new VisualizationJSONFunction());
    }

    public Function getFunction(final String namespace, final String name, final Object[] parameters) {
        Function function = functionMap.get(name);
        
        if (null != function)
            return function;

        throw EvaluationException.createException(FunctionHelper.makeFunctionName(namespace, name), EvaluationException.EvaluationErrorType.NO_FUNCTION);
    }

    public Set getUsedNamespaces() {
        return namespaces;
    }
}
