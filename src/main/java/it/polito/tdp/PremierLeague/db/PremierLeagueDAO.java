package it.polito.tdp.PremierLeague.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.PremierLeague.model.Action;
import it.polito.tdp.PremierLeague.model.Adiacenza;
import it.polito.tdp.PremierLeague.model.Player;

public class PremierLeagueDAO {
	
	public List<Player> listAllPlayers(Map<Integer, Player>idMap){
		String sql = "SELECT * FROM Players";
		List<Player> result = new ArrayList<Player>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Player player = new Player(res.getInt("PlayerID"), res.getString("Name"));
				idMap.put(player.getPlayerID(), player);
				result.add(player);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Action> listAllActions(){
		String sql = "SELECT * FROM Actions";
		List<Action> result = new ArrayList<Action>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Action action = new Action(res.getInt("PlayerID"),res.getInt("MatchID"),res.getInt("TeamID"),res.getInt("Starts"),res.getInt("Goals"),
						res.getInt("TimePlayed"),res.getInt("RedCards"),res.getInt("YellowCards"),res.getInt("TotalSuccessfulPassesAll"),res.getInt("totalUnsuccessfulPassesAll"),
						res.getInt("Assists"),res.getInt("TotalFoulsConceded"),res.getInt("Offsides"));
				
				result.add(action);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Player> getVertici (double soglia, Map<Integer, Player>idMap) {
		String sql="SELECT DISTINCT  a.PlayerID AS id, AVG(a.Goals) AS gol "
				+ "FROM actions a "
				+ "GROUP BY a.PlayerID "
				+ "HAVING gol>?";
		List<Player> result = new ArrayList<Player>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setDouble(1, soglia);
			ResultSet res = st.executeQuery();
			while (res.next()) {
				Player p= idMap.get(res.getInt("id"));
				
				
				result.add(p);
			}
			conn.close();
			return result;
		}catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	
	
	public List<Adiacenza> getArchi (Map<Integer, Player>idMap, List<Player> vertici) {
		String sql="SELECT a1.PlayerID AS id1, a2.PlayerID AS id2, (SUM(a1.TimePlayed) -SUM(a2.TimePlayed)) AS peso "
				+ "FROM actions a1, actions a2 "
				+ "WHERE a1.PlayerID>a2.PlayerID AND a1.`Starts`='1' AND a2.`Starts`='1' AND a1.MatchID=a2.MatchID AND a1.TeamID<>a2.TeamID "
				+ "GROUP BY a1.PlayerID, a2.PlayerID";
		List<Adiacenza> result = new ArrayList<Adiacenza>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
		
			ResultSet res = st.executeQuery();
			while (res.next()) {
//				double time1= res.getDouble("time1");
//				double time2=res.getDouble("time2");
//				double peso=time1-time2;
				
				Player p1=idMap.get(res.getInt("id1"));
				Player p2= idMap.get(res.getInt("id2"));
				if (vertici.contains(p1) && vertici.contains(p2))
				result.add(new Adiacenza(p1, p2, res.getDouble("peso")));
			}
			conn.close();
			return result;
		}catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	
	
	
	public List<Adiacenza> getCoppie (Map<Integer, Player>idMap,  List<Player> vertici) {
		String sql="SELECT a1.PlayerID AS id1, a2.PlayerID AS id2, SUM(a1.TimePlayed) AS time1, SUM(a2.TimePlayed) AS time2, ABS(SUM(a1.TimePlayed-a2.TimePlayed)) AS peso "
				+ "FROM actions a1, actions a2 "
				+ "WHERE a1.PlayerID>a2.PlayerID AND a1.`Starts`='1' AND a2.`Starts`='1' AND a1.MatchID=a2.MatchID AND a1.TeamID<>a2.TeamID "
				+ "AND a1.PlayerID=? AND a2.PlayerID=?";
						List<Adiacenza> result = new ArrayList<Adiacenza>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
		
			ResultSet res = st.executeQuery();
			while (res.next()) {
				//double time1= res.getDouble("time1");
				//double time2=res.getDouble("time2");
				double peso=res.getDouble("peso");
				
				Player p1=idMap.get(res.getInt("id1"));
				Player p2= idMap.get(res.getInt("id2"));
				if (vertici.contains(p1) && vertici.contains(p2)) {
				
				result.add(new Adiacenza(p1, p2, peso));
					
				}
				
			}
			conn.close();
			return result;
		}catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	public double getPeso (Player p1, Player p2) {
		String sql="SELECT a1.PlayerID AS id1, a2.PlayerID AS id2, a1.TimePlayed AS time1, a2.TimePlayed AS time2, ABS(SUM(a1.TimePlayed-a2.TimePlayed)) AS peso "
				+ "FROM actions a1, actions a2 "
				+ "WHERE a1.PlayerID>a2.PlayerID AND a1.`Starts`='1' AND a2.`Starts`='1' AND a1.MatchID=a2.MatchID AND a1.TeamID<>a2.TeamID "
				+ "AND a1.PlayerID=? AND a2.PlayerID=?";
		double peso=0;
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, p1.getPlayerID());
			st.setInt(2, p2.getPlayerID());
			ResultSet res = st.executeQuery();
			if (res.next()) {
				peso= res.getDouble("peso");
			}
		conn.close();
		return peso;
	}catch (SQLException e) {
		e.printStackTrace();
		return 0;
	}
		
	}
}
