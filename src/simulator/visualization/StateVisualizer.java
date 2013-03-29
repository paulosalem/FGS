/*******************************************************************************
 * FGS - Formally Guided Simulator
 * 
 * This software was developed by Paulo Salem da Silva for his doctoral thesis, 
 * which is entitled
 *   
 *   "Verification of Behaviourist Multi-Agent Systems by means of 
 *    Formally Guided Simulations"
 * 
 * This software, therefore, constitutes a companion to the thesis. As such, 
 * it should be seen as an experimental product, suitable for research purposes,
 * but not ready for production.
 * 
 * 
 * Copyright (c) 2008 - 2012, Paulo Salem da Silva
 * All rights reserved.
 * 
 * This software may be used, modified and distributed freely, provided that the 
 * following rules are followed:
 * 
 *   (i)   this copyright notice must be maintained in any redistribution, in both 
 *         original and modified form,  of this software;
 *   (ii)  this software must be provided free of charge, although services which 
 *         require the software may be charged;
 *   (iii) for non-commercial purposes, this software may be used, modified and 
 *         distributed free of charge;
 *   (iv)  for commercial purposes, only the original, unmodified, version of this 
 *         software may be used.
 * 
 * For other uses of the software, please contact the author.
 ******************************************************************************/
package simulator.visualization;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;

import simulator.agent.IAgent;
import simulator.agent.IAgentControl;
import simulator.agent.relation.IRelation;
import simulator.agent.relation.ITie;
import simulator.engine.SimulationState;
import simulator.visualization.jung.SocialNetworkRenderer;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.DirectedSparseGraph;
import edu.uci.ics.jung.graph.impl.DirectedSparseVertex;
import edu.uci.ics.jung.visualization.SpringLayout;
import edu.uci.ics.jung.visualization.VisualizationViewer;



/**
 * Provides a mechanism to visualize a social network in a particular
 * simulation state.
 * 
 * @author Paulo Salem
 *
 */
public class StateVisualizer {

  // TODO Try colouring the vertices in a way that reveals the values of properties
  // TODO Try colouring the edges differently for each relation
	
	public void visualize(SimulationState s, String windowTitle){
		
		// TODO

    
    // Warn user that we are about to create a window
    System.out.println("Rendering visualization named " + windowTitle + "...");
		
		// Build the graph to draw
		Graph g = new DirectedSparseGraph();
		
		// A map from agents to vertices
		Map<IAgent, Vertex> agents2Vertices = new HashMap<IAgent, Vertex>();
		
		// Add vertices
		for(IAgentControl ac: s.getAgentControls()){
			Vertex v = new DirectedSparseVertex();
			g.addVertex(v);
			agents2Vertices.put(ac.getAgent(), v);
		}
		
		// Add edges
		for(IRelation<IAgent> r: s.getRelations()){
			for(ITie<IAgent> t: r.ties()){
				DirectedSparseEdge e = 
					new DirectedSparseEdge(agents2Vertices.get(t.first()), agents2Vertices.get(t.second()));
				
				g.addEdge(e);
			}
		}
    
    
    
		
		// Plot
		JFrame jf = new JFrame();
    jf.setTitle(windowTitle);
    VisualizationViewer vv = new VisualizationViewer(new SpringLayout(g), new SocialNetworkRenderer());
    jf.getContentPane().add(vv);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.pack();
    jf.setVisible(true);
    
    // Acknoledge that the window has been creater
    System.out.println("Done.");

	}
}
