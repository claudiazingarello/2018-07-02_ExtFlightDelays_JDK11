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

	private ExtFlightDelaysDAO dao ;
	private Graph<Airport, DefaultWeightedEdge> grafo;
	private List<Airport> aeroporti;
	private Map<Integer, Airport> airportIdMap;
	
	//variabili ricorsione
	private List<Airport> percorsoBest;
	private Double migliaBest;

	public Model() {
		dao = new ExtFlightDelaysDAO();
	}

	public void creaGrafo(int distanza) {
		grafo = new SimpleWeightedGraph<Airport, DefaultWeightedEdge>(DefaultWeightedEdge.class);

		//Aggiungi i vertici --> NO
		
		//Imposta idMap
		airportIdMap = new HashMap<Integer, Airport>();
		for(Airport a : dao.loadAllAirports()) {
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
		
		//Salviamo i vertici del grafo in una lista
		aeroporti = new ArrayList<Airport>(grafo.vertexSet());
	}
	
	public List<Airport> getAirportGrafo(){
		return aeroporti;
	}

	public List<Vicino> getAirportAdiacenti(Airport airport) {
		List<Vicino> vicini = new ArrayList<Vicino>();
		
		List<Airport> listaAeroportiVicini = Graphs.neighborListOf(grafo, airport);
		for(Airport a : listaAeroportiVicini) {
			Double peso = grafo.getEdgeWeight(grafo.getEdge(airport, a));
			vicini.add(new Vicino(a, peso));
		}
		Collections.sort(vicini);
		return vicini;
	}
	
	public List<Airport> cercaItinerario(Double migliaDisponibili, Airport airport){
		//inizializza le variabili
		percorsoBest = new ArrayList<Airport>();
		migliaBest = 0.0;
		
		//imposta parziale, con vertice da cui partire 
		List<Airport> parziale = new ArrayList<Airport>();
		parziale.add(airport);
		
		cerca(parziale, migliaDisponibili, 1); //partiamo dalla posizione 1 di parziale perchè alla posizione 0 c'è aeroporto
		
		return percorsoBest;
	}

	/*
	 * RICORSIONE
	 * 
	 * Soluzione parziale: Lista di Airport(lista di vertici)
	 * Livello ricorsione: lunghezza della lista
	 * casi terminali: non trova altri vertici da aggiungere -> verifica se il cammino ha lunghezza max
	 * 					tra quelli visti fino ad ora
	 * Generazione delle soluzioni: vertici connessi all'ultimo vertice del percorso
	 * 								non ancora parte del percorso,
	 * 								relativi ad archi con peso uguale alle migliaDisponibili
	 */
	
	private void cerca(List<Airport> parziale, Double migliaDisponibili, Integer livello) {
		Airport ultimo = parziale.get(parziale.size()-1);
		
		//Caso TERMINALE
		//il livello indica il numero di città attraversate
		//se è maggiore della dimensione del percorsoBest allora 
		//abbiamo trovato un percorso più lungo
		if(livello > this.percorsoBest.size()) {
			this.percorsoBest = new ArrayList<Airport>(parziale);
			this.migliaBest = migliaDisponibili;
		}
		
		//aggiorniamo la soluzione anche se il numero di città è lo stesso
		//ma abbiamo percorso più miglia
		if(livello == this.percorsoBest.size() && migliaDisponibili > migliaBest) {
			this.percorsoBest = new ArrayList<Airport>(parziale);
			this.migliaBest = migliaDisponibili;
		}
		
		
		//Casi INTERMEDI
		List<Airport> vicini = Graphs.neighborListOf(grafo, ultimo);
		for (Airport prossimo : vicini) {
			Double peso = this.grafo.getEdgeWeight(grafo.getEdge(ultimo, prossimo));
			if(!parziale.contains(prossimo) && migliaDisponibili >= peso) {
				
				parziale.add(prossimo);
				migliaDisponibili -= peso;
				cerca(parziale, migliaDisponibili, livello+1);
				
				//backtracking
				parziale.remove(parziale.size()-1);
				migliaDisponibili += peso;
			}
		}
	}
	
	public Double getMigliaBest() {
		return migliaBest;
	}
}
