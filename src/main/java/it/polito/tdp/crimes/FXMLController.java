/**
 * Sample Skeleton for 'Scene.fxml' Controller Class
 */

package it.polito.tdp.crimes;

import java.net.URL;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

import it.polito.tdp.crimes.model.Model;
import it.polito.tdp.crimes.model.Vicino;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class FXMLController {
	private Model model;

	@FXML // ResourceBundle that was given to the FXMLLoader
	private ResourceBundle resources;

	@FXML // URL location of the FXML file that was given to the FXMLLoader
	private URL location;

	@FXML // fx:id="boxAnno"
	private ComboBox<Integer> boxAnno; // Value injected by FXMLLoader

	@FXML // fx:id="boxMese"
	private ComboBox<Integer> boxMese; // Value injected by FXMLLoader

	@FXML // fx:id="boxGiorno"
	private ComboBox<Integer> boxGiorno; // Value injected by FXMLLoader

	@FXML // fx:id="btnCreaReteCittadina"
	private Button btnCreaReteCittadina; // Value injected by FXMLLoader

	@FXML // fx:id="btnSimula"
	private Button btnSimula; // Value injected by FXMLLoader

	@FXML // fx:id="txtN"
	private TextField txtN; // Value injected by FXMLLoader

	@FXML // fx:id="txtResult"
	private TextArea txtResult; // Value injected by FXMLLoader

	@FXML
	void doCreaReteCittadina(ActionEvent event) {

		Integer anno = boxAnno.getValue();

		if (anno == null) {
			txtResult.appendText("Selezionare un anno");
			return;
		}

		this.model.creaGrafo(anno);
		txtResult.appendText("Grafo creato!");

		for (Integer distretto : this.model.getVertici()) {

			List<Vicino> vicini = this.model.getVicini(distretto);
			txtResult.appendText("Vicini del distretto " + distretto + ":\n");
			for (Vicino v : vicini) {
				txtResult.appendText(v.getVicino() + " " + v.getDistanza() + "\n");
			}

		}

	}

	@FXML
	void doSimula(ActionEvent event) {

		txtResult.clear();
		Integer anno, mese, giorno, N;

		try {
			N = Integer.parseInt(txtN.getText());

		} catch (NumberFormatException e) {
			txtResult.appendText("Formato non corretto per l'anno");
			return;
		}

		anno = boxAnno.getValue();
		mese = boxMese.getValue();
		giorno = boxGiorno.getValue();

		if (anno == null || mese == null || giorno == null) {
			txtResult.appendText("Seleziona tutti i campi!\n");
		}

		try {
			LocalDate.of(anno, mese, giorno);
		} catch (DateTimeException e) {
			txtResult.appendText("Hai inserito una data non valida!");
		}

		txtResult.appendText("Simulo con " + N + " agenti:\n");

		txtResult.appendText("\nCrimini mal gestiti: " + this.model.simula(anno, mese, giorno, N));

	}

	@FXML // This method is called by the FXMLLoader when initialization is complete
	void initialize() {
		assert boxAnno != null : "fx:id=\"boxAnno\" was not injected: check your FXML file 'Scene.fxml'.";
		assert boxMese != null : "fx:id=\"boxMese\" was not injected: check your FXML file 'Scene.fxml'.";
		assert boxGiorno != null : "fx:id=\"boxGiorno\" was not injected: check your FXML file 'Scene.fxml'.";
		assert btnCreaReteCittadina != null : "fx:id=\"btnCreaReteCittadina\" was not injected: check your FXML file 'Scene.fxml'.";
		assert btnSimula != null : "fx:id=\"btnSimula\" was not injected: check your FXML file 'Scene.fxml'.";
		assert txtN != null : "fx:id=\"txtN\" was not injected: check your FXML file 'Scene.fxml'.";
		assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Scene.fxml'.";

	}

	public void setModel(Model model) {
		this.model = model;
		boxAnno.getItems().addAll(this.model.getAnni());
		boxMese.getItems().addAll(this.model.getMesi());
		boxGiorno.getItems().addAll(this.model.getGiorni());
	}
}
