package it.polito.tdp.extflightdelays;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import it.polito.tdp.extflightdelays.model.Airport;
import it.polito.tdp.extflightdelays.model.AirportDuration;
import it.polito.tdp.extflightdelays.model.Model;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

//controller turno B --> switchare ai branch master_turnoA o master_turnoC per turno A o C

public class FXMLController {

	private Model model;

	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	private TextArea txtResult;

	@FXML
	private TextField voliMinimo;

	@FXML
	private Button btnAnalizza;

	@FXML
	private ComboBox<Airport> cmbBoxAeroportoPartenza;

	@FXML
	private Button btnAeroportiConnessi;

	@FXML
	private TextField numeroOreTxtInput;

	@FXML
	private Button btnOttimizza;

	@FXML
	void doAnalizzaAeroporti(ActionEvent event) {
		txtResult.clear();

		int x;

		try {
			x = Integer.parseInt(voliMinimo.getText());
		} catch (NumberFormatException e) {
			txtResult.appendText("ERRORE: inserire valore nel formato numerico");
			return;
		}

		model.creaGrafo(x);
		cmbBoxAeroportoPartenza.getItems().clear();
		cmbBoxAeroportoPartenza.getItems().addAll(model.getAereoportiGrafo());
	}

	@FXML
	void doCalcolaAeroportiConnessi(ActionEvent event) {
		txtResult.clear();
		if(cmbBoxAeroportoPartenza.getItems().isEmpty()) {
			txtResult.appendText("ERRORE: devi creare il grafo!");
			return;
		}
		Airport airport = cmbBoxAeroportoPartenza.getValue();
		if(airport == null) {
			txtResult.appendText("ERRORE: seleziona un aereoporto");
		}

		List<AirportDuration> listaConnessi = model.getAirportConnected(airport);
		for(AirportDuration a : listaConnessi) {
			txtResult.appendText(a.getA().getId()+" "+a.getA().getAirportName()+"\n");
		}
	}

	@FXML
	void doCercaItinerario(ActionEvent event) {
		txtResult.clear();
		int oreTot ;
		try {
			oreTot = Integer.parseInt(numeroOreTxtInput.getText());
		} catch(NumberFormatException e ) {
			txtResult.appendText("ERRORE: inserire un valore numerico!");
			return;
		}
		
		Airport airport = cmbBoxAeroportoPartenza.getValue();
		if(airport == null) {
			txtResult.appendText("ERRORE: seleziona un aereoporto");
		}

		List<Airport> itinerario = model.cercaItinerario(airport, oreTot);
		txtResult.appendText("Numero TOT ore consumante = "+model.getOreComplessive()+"\n");
		for(Airport a : itinerario) {
			txtResult.appendText(a.getId() +" "+a.getAirportName()+ "\n");
		}
	}

	@FXML
	void initialize() {
		assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'ExtFlightDelays.fxml'.";
		assert voliMinimo != null : "fx:id=\"voliMinimo\" was not injected: check your FXML file 'ExtFlightDelays.fxml'.";
		assert btnAnalizza != null : "fx:id=\"btnAnalizza\" was not injected: check your FXML file 'ExtFlightDelays.fxml'.";
		assert cmbBoxAeroportoPartenza != null : "fx:id=\"cmbBoxAeroportoPartenza\" was not injected: check your FXML file 'ExtFlightDelays.fxml'.";
		assert btnAeroportiConnessi != null : "fx:id=\"btnAeroportiConnessi\" was not injected: check your FXML file 'ExtFlightDelays.fxml'.";
		assert numeroOreTxtInput != null : "fx:id=\"numeroOreTxtInput\" was not injected: check your FXML file 'ExtFlightDelays.fxml'.";
		assert btnOttimizza != null : "fx:id=\"btnOttimizza\" was not injected: check your FXML file 'ExtFlightDelays.fxml'.";

	}

	public void setModel(Model model) {
		this.model = model;
	}
}
