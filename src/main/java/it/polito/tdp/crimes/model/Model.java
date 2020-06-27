package it.polito.tdp.crimes.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import com.javadocmd.simplelatlng.LatLng;
import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;

import it.polito.tdp.crimes.db.EventsDao;

public class Model {

	EventsDao dao;
	List<Integer> vertici;
	Graph<Integer, DefaultWeightedEdge> grafo;

	public Model() {
		dao = new EventsDao();
	}

	public List<Integer> getAnni() {
		return this.dao.getAnni();
	}

	public List<Integer> getVertici() {
		return this.dao.getVertici();
	}

	public int simula(Integer anno, Integer mese, Integer giorno, Integer N) {

		Simulator sim = new Simulator();
		sim.init(N, anno, mese, giorno, grafo);
		return sim.run();
	}

	public List<Vicino> getVicini(Integer distretto) {

		List<Vicino> vicini = new ArrayList<>();

		List<Integer> viciniId = Graphs.neighborListOf(this.grafo, distretto);

		for (Integer v : viciniId) {
			vicini.add(new Vicino(v, this.grafo.getEdgeWeight(this.grafo.getEdge(distretto, v))));
		}

		Collections.sort(vicini);

		return vicini;

	}

	public void creaGrafo(Integer anno) {

		// creo grafo
		grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);

		vertici = this.dao.getVertici();
		Graphs.addAllVertices(this.grafo, vertici);

		// Dato che abbiamo pochi vertici facciamo doppio ciclo for
		for (Integer v1 : this.grafo.vertexSet()) {
			for (Integer v2 : this.grafo.vertexSet()) {

				if (!v1.equals(v2)) {
					if (this.grafo.getEdge(v1, v2) == null) {
						// devo fare la distanza tra i centri del crimine distrettuale

						// mi servono lat media e long media per ogni distretto, poi calcolo la distanza
						// tra le due
						Double latMediaV1 = dao.getLatMedia(anno, v1);
						Double latMediaV2 = dao.getLatMedia(anno, v2);
						Double lonMediaV1 = dao.getLonMedia(anno, v1);
						Double lonMediaV2 = dao.getLonMedia(anno, v2);

						Double distanzaMedia = LatLngTool.distance(new LatLng(latMediaV1, lonMediaV1),
								new LatLng(latMediaV2, lonMediaV2), LengthUnit.KILOMETER);

						Graphs.addEdgeWithVertices(this.grafo, v1, v2, distanzaMedia);
					}
				}
			}
		}
		System.out.println(
				"grafo creato!\n#Vertici " + this.grafo.vertexSet().size() + "\n#Archi " + this.grafo.edgeSet().size());

	}

	public List<Integer> getMesi() {

		return this.dao.getMesi();
	}

	public List<Integer> getGiorni() {
		return this.dao.getGiorni();
	}

}
