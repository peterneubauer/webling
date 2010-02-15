package com.tinkerpop.webling.functions;

import com.tinkerpop.webling.functions.util.*;
import com.tinkerpop.gremlin.functions.FunctionLibrary;

/**
 * @author Pavel A. Yaskevich 
 */
public class WeblingFunctions extends FunctionLibrary {

	public static final String NAMESPACE_PREFIX = "w";

    public WeblingFunctions() {
    	this.addFunction(NAMESPACE_PREFIX, new VisualizationJSONFunction());
    }
}
