package it.polito.tdp.crimes.model;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;

import it.polito.tdp.crimes.db.EventsDao;
import it.polito.tdp.crimes.model.Evento.TipoEvento;

public class Simulator {

	// TIPI DI EVENTO:
	// 1) Evento criminoso:
	// a) selezionare l'agente libero più vicino (se lo trovo, altrimenti è mal
	// gestito)
	// b) una volta trovato l'agente, lo setto occupato

	// 2) L'agente arriva sul posto:
	// a) Definisco quanto durerà l'intervento
	// b) Controllo se il crimine è mal gestito (ritardo agente >=15)

	// 3) Crimine terminato --> setto l'agente libero, nella stessa località

	// Input utente
	private Integer N;
	private Integer anno;
	private Integer mese;
	private Integer giorno;

	// STATO DEL SISTEMA
	private Graph<Integer, DefaultWeightedEdge> grafo;
	// creo una mappa che associa a ogni distretto un numero di agenti
	private Map<Integer, Integer> agenti;

	// Coda degli eventi
	private PriorityQueue<Evento> coda;

	// Output: numero eventi mal gestiti
	private Integer malGestiti;

	public void init(Integer N, Integer anno, Integer mese, Integer giorno, Graph<Integer, DefaultWeightedEdge> grafo) {

		this.N = N;
		this.anno = anno;
		this.mese = mese;
		this.giorno = giorno;
		this.grafo = grafo;

		this.malGestiti = 0;
		this.agenti = new HashMap<Integer, Integer>();
		// metto 0 agenti in tutti
		for (Integer d : this.grafo.vertexSet()) {
			this.agenti.put(d, 0);
		}
		// metto N agenti nella centrale
		EventsDao dao = new EventsDao();
		Integer midD = dao.getDistrettoMin(anno);
		this.agenti.put(midD, N);

		// creo e inizializzo la coda
		this.coda = new PriorityQueue<Evento>();

		// Bisogna trasformare gli event in eventi prima di metterli nella coda
		for (Event e : dao.listAllEventsByDate(anno, mese, giorno)) {

			coda.add(new Evento(TipoEvento.CRIMINE, e.getReported_date(), e));
		}
	}

	public int run() {

		Evento e;
		while ((e = coda.poll()) != null) {

			switch (e.getTipo()) {

			case CRIMINE:
				System.out.println("Nuovo crimine " + e.getCrimine().getIncident_id());// debug

				// cerco l'agente libero più vicino
				Integer partenza = null;
				partenza = cercaAgente(e.getCrimine().getDistrict_id());
				// se ho trovato
				if (partenza != null) {
					// mettiamo l'agente come occupato (decremento value nella map)
					this.agenti.put(partenza, this.agenti.get(partenza) - 1);

					// vediamo quanto ci mette ad arrivare l'agente
					Double distanza;
					if (partenza.equals(e.getCrimine().getDistrict_id())) {
						distanza = 0.0;
					} else {
						distanza = this.grafo
								.getEdgeWeight(this.grafo.getEdge(partenza, e.getCrimine().getDistrict_id()));

						// in secondi
						Long seconds = (long) ((distanza * 1000) / (60 / 3.6));

						// scheduliamo l'evento arriva agente
						this.coda.add(
								new Evento(TipoEvento.ARRIVA_AGENTE, e.getData().plusSeconds(seconds), e.getCrimine()));

					}

				} else {
					// no agenti liberi --> crimine mal gestito
					System.out.println("crimine " + e.getCrimine().getIncident_id() + " è mal gestito");
					this.malGestiti++;
				}

				break;

			case ARRIVA_AGENTE:
				// vedo quanto dura l'evento criminoso e controllo se sono arrivato tardi
				System.out.println("Arriva gente per crimine! " + e.getCrimine().getIncident_id());

				// in secondi
				Long duration = getDurata(e.getCrimine().getOffense_category_id());

				this.coda.add(new Evento(TipoEvento.GESTITO, e.getData().plusSeconds(duration), e.getCrimine()));

				// controllo se è mal gestito
				if (e.getData().isAfter(e.getCrimine().getReported_date().plusMinutes(15))) {
					System.out.println("CRIMINE " + e.getCrimine().getIncident_id() + " MAL GESTITO!");
					this.malGestiti++;
				}

				break;

			case GESTITO:
				// libero l'agente che ritorna ad essere disponibile
				System.out.println("Crimine " + e.getCrimine().getIncident_id() + " GESTITO!\n");
				this.agenti.put(e.getCrimine().getDistrict_id(), this.agenti.get(e.getCrimine().getDistrict_id()) + 1);

				break;

			}
		}

		return this.malGestiti;
	}

	private Long getDurata(String offense_category_id) {

		Long durata;

		// controllo tipo di reato

		if (offense_category_id.equals("all_other_crimes")) {

			Random r = new Random();
			// crea un numero tra 0 e 1.0:
			// al 50% può durare 1 ora o 2 ore
			if (r.nextDouble() > 0.5) {

				return Long.valueOf(2 * 60 * 60);
			} else {
				return Long.valueOf(1 * 60 * 60);
			}
		} else {
			return Long.valueOf(2 * 60 * 60);
		}

	}

	private Integer cercaAgente(Integer district_id) {

		Double distanza = Double.MAX_VALUE;
		Integer distretto = null;

		// prendo il distretto con
		for (Integer d : this.agenti.keySet()) {

			// se il distretto corrente ha agenti disponibili
			if (this.agenti.get(d) > 0) {

				// controllo se coincide con quello di arrivo, se si, distanza 0
				if (district_id.equals(d)) {
					distanza = 0.0;
					distretto = d;
				} else {

					// se il distretto corrente ha distanza minore della minima precedente
					if (this.grafo.getEdgeWeight(this.grafo.getEdge(district_id, d)) < distanza) {
						distanza = this.grafo.getEdgeWeight(this.grafo.getEdge(district_id, d));
						distretto = d;

					}

				}
			}

		}

		return distretto;
	}

}
