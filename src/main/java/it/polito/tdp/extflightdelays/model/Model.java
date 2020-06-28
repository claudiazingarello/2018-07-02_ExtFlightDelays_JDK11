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
	private List<Airport> aereoportiGrafo;
	private List<Airport> percorsoBest;
	private Double pesoBest;

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
		aereoportiGrafo = new ArrayList<Airport>(grafo.vertexSet());
		return aereoportiGrafo;
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

	public List<Airport> cercaItinerario(Airport scelto, int oreTot) {
		//inizializza le variabili
		percorsoBest = new ArrayList<Airport>();
		pesoBest = 0.0;

		//imposta parziale, con vertice da cui partire 
		List<Airport> parziale = new ArrayList<Airport>();
		parziale.add(scelto);

		cerca(parziale, scelto, oreTot); //partiamo dalla posizione 1 di parziale perchè alla posizione 0 c'è aeroporto

		return percorsoBest;
	}

	private void cerca(List<Airport> parziale, Airport scelto, int oreTot) {
		double pesoCorrente = calcolaPeso(parziale);
		
		//Supera le ore disponibili
		if(pesoCorrente > oreTot) {
				return;
		}
		
		if(pesoCorrente > this.pesoBest && pesoCorrente <= oreTot ) {
			this.percorsoBest = new ArrayList<Airport>(parziale);
			this.pesoBest = calcolaPeso(parziale);
		}
		
		//se l'ultimo non è uguale a scelto, devo ritornare indietro
		if(parziale.get(parziale.size()-1).compareTo(scelto) != 0) {
			parziale.add(scelto);
			cerca(parziale, scelto, oreTot);
			parziale.remove(parziale.size()-1);
		}
		else {
			//se l'ultimo è l'aereoporto scelto, cerco nei vicini
			List<Airport> vicini = Graphs.neighborListOf(grafo, scelto);
			for(Airport a : vicini) {
				if(!parziale.contains(a)) {
				parziale.add(a);
				cerca(parziale, scelto, oreTot);
				parziale.remove(parziale.size()-1);
				}
			}
		}	
	}

	private double calcolaPeso(List<Airport> parziale) {
		double peso = 0.0;
		for(int i = 1; i<parziale.size(); i++) {
			peso += this.grafo.getEdgeWeight(grafo.getEdge(parziale.get(i-1), parziale.get(i)));
		}
		return peso;
	}
	
	public Double getOreComplessive() {
		return calcolaPeso(percorsoBest);
	}

}
