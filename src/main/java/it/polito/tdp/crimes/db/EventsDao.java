package it.polito.tdp.crimes.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polito.tdp.crimes.model.Event;

public class EventsDao {

	public List<Event> listAllEventsByDate(Integer anno, Integer mese, Integer giorno) {
		String sql = "SELECT * FROM events " + "WHERE Year(reported_date) = ? " + "AND Month(reported_date) = ? "
				+ "AND Day(reported_date) = ?";
		try {
			Connection conn = DBConnect.getConnection();

			PreparedStatement st = conn.prepareStatement(sql);

			List<Event> list = new ArrayList<>();

			st.setInt(1, anno);
			st.setInt(2, mese);
			st.setInt(3, giorno);

			ResultSet res = st.executeQuery();

			while (res.next()) {
				try {
					list.add(new Event(res.getLong("incident_id"), res.getInt("offense_code"),
							res.getInt("offense_code_extension"), res.getString("offense_type_id"),
							res.getString("offense_category_id"), res.getTimestamp("reported_date").toLocalDateTime(),
							res.getString("incident_address"), res.getDouble("geo_lon"), res.getDouble("geo_lat"),
							res.getInt("district_id"), res.getInt("precinct_id"), res.getString("neighborhood_id"),
							res.getInt("is_crime"), res.getInt("is_traffic")));
				} catch (Throwable t) {
					t.printStackTrace();
					System.out.println(res.getInt("id"));
				}
			}

			conn.close();
			return list;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public List<Integer> getGiorni() {

		String sql = "Select distinct Day(reported_date) as anno from events order by anno ASC";

		List<Integer> result = new ArrayList<>();

		try {
			Connection conn = DBConnect.getConnection();

			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();

			while (res.next()) {

				result.add(res.getInt("anno"));
			}

			conn.close();
			return result;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}

	
	
	public List<Integer> getMesi() {

		String sql = "Select distinct Month(reported_date) as anno from events order by anno ASC";

		List<Integer> result = new ArrayList<>();

		try {
			Connection conn = DBConnect.getConnection();

			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();

			while (res.next()) {

				result.add(res.getInt("anno"));
			}

			conn.close();
			return result;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}

	
	
	/**
	 * Otteniamo la lista degli anni da caricare nella tendina
	 * 
	 * @return
	 */
	public List<Integer> getAnni() {

		String sql = "Select distinct Year(reported_date) as anno from events order by anno ASC";

		List<Integer> result = new ArrayList<>();

		try {
			Connection conn = DBConnect.getConnection();

			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();

			while (res.next()) {

				result.add(res.getInt("anno"));
			}

			conn.close();
			return result;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}

	public List<Event> listAllEvents() {
		String sql = "SELECT * FROM events";
		try {
			Connection conn = DBConnect.getConnection();

			PreparedStatement st = conn.prepareStatement(sql);

			List<Event> list = new ArrayList<>();

			ResultSet res = st.executeQuery();

			while (res.next()) {
				try {
					list.add(new Event(res.getLong("incident_id"), res.getInt("offense_code"),
							res.getInt("offense_code_extension"), res.getString("offense_type_id"),
							res.getString("offense_category_id"), res.getTimestamp("reported_date").toLocalDateTime(),
							res.getString("incident_address"), res.getDouble("geo_lon"), res.getDouble("geo_lat"),
							res.getInt("district_id"), res.getInt("precinct_id"), res.getString("neighborhood_id"),
							res.getInt("is_crime"), res.getInt("is_traffic")));
				} catch (Throwable t) {
					t.printStackTrace();
					System.out.println(res.getInt("id"));
				}
			}

			conn.close();
			return list;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public List<Integer> getVertici() {
		String sql = "select distinct district_id as id from events";
		List<Integer> result = new ArrayList<>();
		try {
			Connection conn = DBConnect.getConnection();

			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();

			while (res.next()) {

				result.add(res.getInt("id"));
			}

			conn.close();
			return result;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}

	public Double getLonMedia(Integer anno, Integer district) {

		String sql = "Select avg(geo_lon) as lon from events where year(reported_date) = ? and district_id= ?";
		Double risultato = 0.0;

		try {
			Connection conn = DBConnect.getConnection();

			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, anno);
			st.setInt(2, district);

			ResultSet res = st.executeQuery();

			while (res.next()) {

				risultato = res.getDouble("lon");

			}

			conn.close();
			return risultato;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public Double getLatMedia(Integer anno, Integer district) {

		String sql = "Select avg(geo_lat) as lat from events where year(reported_date) = ? and district_id= ?";
		Double risultato = 0.0;

		try {
			Connection conn = DBConnect.getConnection();

			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, anno);
			st.setInt(2, district);

			ResultSet res = st.executeQuery();

			while (res.next()) {

				risultato = res.getDouble("lat");

			}

			conn.close();
			return risultato;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}

	public Integer getDistrettoMin(Integer anno) {

		String sql = "select district_id, count(*) " + "from events " + "where year(reported_date) = ? "
				+ "group by district_id " + "order by count(*) ASC " + "limit 1";
		Integer risultato = 0;

		try {
			Connection conn = DBConnect.getConnection();

			PreparedStatement st = conn.prepareStatement(sql);

			st.setInt(1, anno);
			
			ResultSet res = st.executeQuery();

			while (res.next()) {

				risultato = res.getInt("district_id");

			}

			conn.close();
			return risultato;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}

}
