package com.tinkerpop.webling.functions.util;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.commons.jxpath.ExpressionContext;
import org.apache.commons.jxpath.Function;

import org.json.JSONObject;
import org.json.JSONArray;

import com.tinkerpop.gremlin.functions.FunctionHelper;
import com.tinkerpop.gremlin.models.pgm.Vertex;
import com.tinkerpop.gremlin.models.pgm.Edge;
import com.tinkerpop.gremlin.models.pgm.Element;

/**
 *	@author Pavel A. Yaskevich
 */
public class VisualizationJSONFunction implements Function {

    public static final String FUNCTION_NAME = "vis-json";
	  protected Map<Vertex, Map<Edge, Vertex>> graphMatrix;

    // required strings
    private static final String ID          = "id";
    private static final String VERTEX      = "vertex";
    private static final String EDGE        = "edge";
    private static final String LABEL       = "label";
    private static final String PROPERTIES  = "properties";
    private static final String RELATIONS   = "relations";

	  static {
	  }

	  public String invoke(final ExpressionContext context, final Object[] params) {
        JSONArray jsonGraph = new JSONArray();
        this.graphMatrix = new HashMap<Vertex, Map<Edge, Vertex>>();
		    Object object = FunctionHelper.nodeSetConversion(params[0]);
	     
		    if (object instanceof Vertex) {
            getTree(null, (Vertex) object, 0);
            try {
			          for (Vertex vertex : graphMatrix.keySet()) {
                    Map<Edge, Vertex> relations = graphMatrix.get(vertex);
                    jsonGraph.put(JSONMap(vertex, relations));
				        }        
            } catch(Exception e) {
                return null;
            }
		    } else {
			      return null;
		    }
        
	      return jsonGraph.toString();
    }

	  private void getTree(final Vertex parentVertex, final Vertex vertex, final int depth) {
		    if (depth == 4)
			    return;
        
        Iterator<Edge> inE  = vertex.getInEdges().iterator();
        Iterator<Edge> outE = vertex.getOutEdges().iterator();

        while (outE.hasNext()) {
            Edge edge = outE.next();
            Vertex inVertex = edge.getInVertex();
            
            if (parentVertex == inVertex) 
                continue;
        
            this.setGraphMatrixRow(vertex, edge, inVertex);
            getTree(vertex, inVertex, depth + 1);
        }
         
        while (inE.hasNext()) {
            Edge edge = inE.next();
            Vertex outVertex = edge.getOutVertex();

            if (parentVertex == outVertex) 
                continue;

            this.setGraphMatrixRow(outVertex, edge, vertex);
            getTree(vertex, outVertex, depth + 1);
        }
    }

    private void setGraphMatrixRow(final Vertex outV, final Edge edge, final Vertex inV) {
        Map<Edge, Vertex> relatives = null;

        if ((relatives = this.graphMatrix.get(outV)) == null) {
            relatives = new HashMap<Edge, Vertex>();
            relatives.put(edge, inV);
            this.graphMatrix.put(outV, relatives);
        } else {
            relatives.put(edge, inV);
        }
    }

    @SuppressWarnings("unchecked")
	private static JSONObject JSONElement(final Element element) {
        JSONObject jsonElement = new JSONObject();
        Map properties = new HashMap();
        
        try {
            jsonElement.put(ID, element.getId());

            if (element instanceof Edge)
                properties.put(LABEL, ((Edge) element).getLabel());

            for (String key : element.getPropertyKeys()) {
                properties.put(key, element.getProperty(key));
            }
            jsonElement.put(PROPERTIES, properties);
        } catch(Exception e) {
            return null;
        }
        return jsonElement;
    }

    @SuppressWarnings("unchecked")
	private static List relationsList(final Map<Edge, Vertex> relations) {
        Map map = null;
        List list = new ArrayList();

        for (Edge edge : relations.keySet()) {
            map = new HashMap();
            map.put(EDGE, JSONElement(edge));
            map.put(VERTEX, JSONElement(relations.get(edge)));
            list.add(map);
        }
        
        return list;
    }

    @SuppressWarnings("unchecked")
	private static Map JSONMap(final Vertex vertex, final Map<Edge, Vertex> relations) {
        Map map = new HashMap();

        map.put(VERTEX, JSONElement(vertex));
        map.put(RELATIONS, relationsList(relations));
        return map;
    }
}
