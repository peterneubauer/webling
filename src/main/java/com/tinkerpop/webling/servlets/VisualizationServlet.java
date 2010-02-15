package com.tinkerpop.webling.servlets;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.tinkerpop.webling.GremlinWorkerPool;

	
/**
 * @author Pavel A. Yaskevich
 */
public class VisualizationServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ServletContext sc = getServletContext();
        String sessionId = request.getSession(true).getId();
        String code	= "w:vis-json(" + request.getParameter("v") + ")";
        
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);
        sc.log("[GET /visualize?v=" + request.getParameter("v") + "] 200 OK");

        try {
        	List<String> result = GremlinWorkerPool.evaluate(sessionId, code);
        	
        	for(String line : result) {
        		response.getWriter().println(line);
        	}
        } catch(Exception e) {
            response.getWriter().println(e.getMessage());
        }
    }
}
