package it.polito.tdp.extflightdelays.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.extflightdelays.db.ExtFlightDelaysDAO;

public class Model {

	private ExtFlightDelaysDAO dao ;
	private Graph<Airport, DefaultWeightedEdge> grafo;
	private List<Airport> aeroporti;
	private Map<Integer, Airport> airportIdMap;

	public Model() {
		dao = new ExtFlightDelaysDAO();
	}

	public void creaGrafo(int distanza) {
		grafo = new SimpleWeightedGraph<Airport, DefaultWeightedEdge>(DefaultWeightedEdge.class);

		//Aggiungi i vertici
		aeroporti = dao.loadAllAirports();
		

		//Imposta idMap
		airportIdMap = new HashMap<Integer, Airport>();
		for(Airport a : aeroporti) {
			if(!airportIdMap.containsKey(a.getId()))
				airportIdMap.put(a.getId(), a);
		}

		//aggiungi gli archi
		for (Adiacenza a : dao.getAdiacenze(distanza, airportIdMap)) {
			//Si aggiungono solo i vertici che hanno archi con la specifica indicata
			//quindi quelli trovati con le adiacenze
			if(!grafo.containsVertex(a.getA1())) {
				grafo.addVertex(a.getA1());
			}
			if(!grafo.containsVertex(a.getA2())) {
				grafo.addVertex(a.getA2());
			}
			Graphs.addEdge(grafo, a.getA1(), a.getA2(), a.getPeso());
		}

		System.out.println("Grafo creato!\n#vertici: "+grafo.vertexSet().size()+"\n#archi: "+grafo.edgeSet().size()+"\n");
	}
}
