package it.polito.tdp.PremierLeague.model;

import java.nio.InvalidMarkException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.PremierLeague.db.PremierLeagueDAO;

public class Model {
	
	Graph<Player, DefaultWeightedEdge> grafo;
	Map<Integer, Player> idMap;
	PremierLeagueDAO dao;
	List<Player> best;
	public Model () {
		dao= new PremierLeagueDAO();
		idMap= new HashMap<>();
	}
	
	public void creaGrafo (double x) {
		this.grafo= new SimpleWeightedGraph<Player, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		dao.listAllPlayers(idMap);
		List<Player> giocatori=new ArrayList<Player>(dao.getVertici(x, idMap));
		
		Graphs.addAllVertices(this.grafo,giocatori);
		for (Adiacenza a: dao.getArchi(idMap, giocatori)) {
			if (a.getPeso()>0) {
				Graphs.addEdgeWithVertices(this.grafo, a.getP1(), a.getP2(), a.getPeso());
				
			}
			else if (a.getPeso()<0) {
				Graphs.addEdgeWithVertices(this.grafo, a.getP2(), a.getP1(), (-1)*a.getPeso());
			}
		
	
		}
		/*for (Player p1: giocatori) {
		for (Player p2: giocatori) {
			
				if (!p1.equals(p2)) {
					if (dao.getPeso(p1, p2)!=0)
						Graphs.addEdgeWithVertices(this.grafo, p1, p2, dao.getPeso(p1, p2));
				}
			
		}
		}*/
		}

	public int nVertici() {
		return this.grafo.vertexSet().size();
		
	}

	public int nArchi() {
		return this.grafo.edgeSet().size();
	
	}
	
	public TopPlayer getMigliore () {
		
			
//			double delta = pesoUscente;
//
//			//double delta = pesoUscente;
//			if(delta > maxDelta) {
//				best = p;
//				maxDelta = delta;
//				
//			
			
		//}
		
		if (grafo==null) {
			return null;
		}
		Player best = null;
		Integer maxDegree = Integer.MIN_VALUE;
		for(Player p : grafo.vertexSet()) {
			if(grafo.outDegreeOf(p) > maxDegree) {
				maxDegree = grafo.outDegreeOf(p);
				best = p;
			}
		}
		
		TopPlayer topPlayer = new TopPlayer();
		topPlayer.setPlayer(best);
		
		List<Opponent> opponents = new ArrayList<>();
		for(DefaultWeightedEdge edge : grafo.outgoingEdgesOf(topPlayer.getPlayer())) {
			opponents.add(new Opponent(grafo.getEdgeTarget(edge), (int) grafo.getEdgeWeight(edge)));
		}
		Collections.sort(opponents);
		topPlayer.setOpponents(opponents);
		return topPlayer;
	}
		
	
	public List<Player> cercaLista(int k) {
		best= new LinkedList<Player>();
		List<Player> parziale= new LinkedList<Player>();
		ricorsione(parziale, k);
		return best;
		
	}
	
	public void ricorsione (List<Player> parziale, int k) {
		
		if (parziale.size()==k) {
			if (this.getGrado(parziale)>this.getGrado(best))  {
				best= new ArrayList<Player>(parziale);
				
			return;
			}
		}
		List<Player> nonAmmessi= new ArrayList<Player>();
		for (Player p: grafo.vertexSet()) {
			
		if (!parziale.contains(p)) {
			parziale.add(p);
			
		}
		}
	}
	
	public int getGrado(List<Player> giocatore) {
		int grado=0;
		
		for (Player p: giocatore) {
			int gradoUscente=0;
			int gradoEntrante=0;
		for (DefaultWeightedEdge edge: grafo.outgoingEdgesOf(p)) {
			gradoUscente+=this.grafo.getEdgeWeight(edge);
		}
		for (DefaultWeightedEdge edge: grafo.incomingEdgesOf(p)) {
			gradoEntrante+=this.grafo.getEdgeWeight(edge);
		}
		grado+=gradoUscente-gradoEntrante;
		
		}
		return grado;
		
	}
	
}
