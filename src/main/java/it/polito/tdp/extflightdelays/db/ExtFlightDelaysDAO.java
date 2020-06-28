package it.polito.tdp.extflightdelays.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.extflightdelays.model.Adiacenza;
import it.polito.tdp.extflightdelays.model.Airline;
import it.polito.tdp.extflightdelays.model.Airport;
import it.polito.tdp.extflightdelays.model.Flight;

public class ExtFlightDelaysDAO {

	public List<Airline> loadAllAirlines() {
		String sql = "SELECT * from airlines";
		List<Airline> result = new ArrayList<Airline>();

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				result.add(new Airline(rs.getInt("ID"), rs.getString("IATA_CODE"), rs.getString("AIRLINE")));
			}

			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}

	public List<Airport> loadAllAirports() {
		String sql = "SELECT * FROM airports";
		List<Airport> result = new ArrayList<Airport>();

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				Airport airport = new Airport(rs.getInt("ID"), rs.getString("IATA_CODE"), rs.getString("AIRPORT"),
						rs.getString("CITY"), rs.getString("STATE"), rs.getString("COUNTRY"), rs.getDouble("LATITUDE"),
						rs.getDouble("LONGITUDE"), rs.getDouble("TIMEZONE_OFFSET"));
				result.add(airport);
			}

			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}

	public List<Flight> loadAllFlights() {
		String sql = "SELECT * FROM flights";
		List<Flight> result = new LinkedList<Flight>();

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				Flight flight = new Flight(rs.getInt("ID"), rs.getInt("AIRLINE_ID"), rs.getInt("FLIGHT_NUMBER"),
						rs.getString("TAIL_NUMBER"), rs.getInt("ORIGIN_AIRPORT_ID"),
						rs.getInt("DESTINATION_AIRPORT_ID"),
						rs.getTimestamp("SCHEDULED_DEPARTURE_DATE").toLocalDateTime(), rs.getDouble("DEPARTURE_DELAY"),
						rs.getDouble("ELAPSED_TIME"), rs.getInt("DISTANCE"),
						rs.getTimestamp("ARRIVAL_DATE").toLocalDateTime(), rs.getDouble("ARRIVAL_DELAY"));
				result.add(flight);
			}

			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}
	
	public List<Airport> getVertici(int x, Map<Integer,Airport> airportIdMap) {
		String sql = "SELECT a.ID as id, COUNT(a.ID) AS somma " + 
				"FROM airports AS a, flights AS f " + 
				"WHERE a.ID = f.ORIGIN_AIRPORT_ID OR a.ID = f.DESTINATION_AIRPORT_ID " + 
				"GROUP BY a.ID " + 
				"HAVING somma >=?";
		List<Airport> result = new ArrayList<Airport>();

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, x);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				if(airportIdMap.containsKey(rs.getInt("id"))) {
				Airport airport = airportIdMap.get(rs.getInt("id"));
				result.add(airport);
				}
			}

			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}
	
	public List<Adiacenza> getAdiacenze(int x, Map<Integer,Airport> airportIdMap) {
		String sql = "SELECT f.ORIGIN_AIRPORT_ID AS a1,f.DESTINATION_AIRPORT_ID AS a2,AVG(f.ELAPSED_TIME) AS peso " + 
				"FROM flights AS f " + 
				"WHERE f.ORIGIN_AIRPORT_ID IN(SELECT a.ID " + 
				"FROM airports AS a,flights AS f " + 
				"WHERE a.ID=f.ORIGIN_AIRPORT_ID " + 
				"OR a.ID=f.DESTINATION_AIRPORT_ID " + 
				"GROUP BY a.ID " + 
				"HAVING COUNT(a.ID)>= ?) " + 
				"AND f.DESTINATION_AIRPORT_ID IN (SELECT a.ID " + 
				"FROM airports AS a,flights AS f " + 
				"WHERE a.ID=f.ORIGIN_AIRPORT_ID " + 
				"OR a.ID=f.DESTINATION_AIRPORT_ID " + 
				"GROUP BY a.ID " +
				"HAVING COUNT(a.ID)>=?) " + 
				"GROUP BY f.ORIGIN_AIRPORT_ID,f.DESTINATION_AIRPORT_ID";
		List<Adiacenza> result = new ArrayList<Adiacenza>();

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, x);
			st.setInt(2, x);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				if(airportIdMap.containsKey(rs.getInt("a1")) && airportIdMap.containsKey(rs.getInt("a2"))) {
				Airport airport1 = airportIdMap.get(rs.getInt("a1"));
				Airport airport2 = airportIdMap.get(rs.getInt("a2"));
				
				result.add(new Adiacenza(airport1, airport2, rs.getDouble("peso")));
				}
			}

			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}
	
}

