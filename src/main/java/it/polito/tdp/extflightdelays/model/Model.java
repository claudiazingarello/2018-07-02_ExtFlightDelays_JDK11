package it.polito.tdp.extflightdelays.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.extflightdelays.db.ExtFlightDelaysDAO;

public class Model {
	private ExtFlightDelaysDAO dao;
	private Map<Integer, Airport> airportIdMap;
	private Graph<Airport, DefaultWeightedEdge> grafo;
	
	private List<Airport> aereopotiGrafo;
	
	public Model() {
		dao = new ExtFlightDelaysDAO();
	}
	
	public void creaGrafo(int x) {
		grafo = new SimpleWeightedGraph<Airport, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		
		airportIdMap = new HashMap<Integer, Airport>();
		for(Airport a : dao.loadAllAirports()) {
			if(!airportIdMap.containsKey(a.getId())) {
				airportIdMap.put(a.getId(), a);
			}
		}
	
		//Aggiungi i vertici e gli archi
		for (Adiacenza a : dao.getAdiacenze(x, airportIdMap)) {
			if(!this.grafo.containsVertex(a.getA1())) {
				this.grafo.addVertex(a.getA1());
			}
			if(!this.grafo.containsVertex(a.getA2())) {
				this.grafo.addVertex(a.getA2());
			}
			
			if(this.grafo.getEdge(a.getA1(), a.getA2()) == null) {
				Graphs.addEdge(grafo, a.getA1(), a.getA2(), a.getPeso());
			}
		}
		
		System.out.println("Grafo creato!\n#vertici: "+grafo.vertexSet().size()+"\n#archi: "+grafo.edgeSet().size()+"\n");
	}

	public List<Airport> getAereoportiGrafo(){
		aereopotiGrafo = new ArrayList<Airport>(grafo.vertexSet());
		return aereopotiGrafo;
	}

	public List<AirportDuration> getAirportConnected(Airport airport) {
			List<AirportDuration> vicini = new ArrayList<AirportDuration>();
			
			List<Airport> viciniId = Graphs.neighborListOf(this.grafo, airport);
			
			for (Airport v : viciniId) {
				Double peso = this.grafo.getEdgeWeight(this.grafo.getEdge(airport, v));
				vicini.add(new AirportDuration(v, peso));
			}
			Collections.sort(vicini);
			
			return vicini;
	
	}
}
