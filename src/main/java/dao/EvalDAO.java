package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.NamingException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import util.ConnectionPool;

public class EvalDAO {	
	

public boolean setTeamByFeedNoWithId(String uid, String no, int peopleJoined) throws NamingException, SQLException, ParseException {
		Connection conn = ConnectionPool.get();
		PreparedStatement stmt = null; 
		ResultSet rs = null;
		try {				
			String sql = "SELECT jsonstr FROM feed where no = ?";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, no);
			rs = stmt.executeQuery();
			
			String jsonstr = "";
			int cnt = 0;
			while(rs.next()) {
				if(cnt ++ > 0) jsonstr += ", ";
				jsonstr += rs.getString("jsonstr");
			}
			jsonstr += "";
            
            stmt.close(); rs.close();
            
			JSONParser parser = new JSONParser();
			JSONObject jsonobj = (JSONObject) parser.parse(jsonstr);
            String myUserId = "UserId" + Integer.toString(peopleJoined);
			jsonobj.put(myUserId, uid);
			jsonobj.remove("peopleJoined");
			jsonobj.put("peopleJoined", peopleJoined);
			
			String sql2 = "UPDATE feed SET jsonstr = ? where no = ?";
			stmt = conn.prepareStatement(sql2);
			stmt.setString(1, jsonobj.toJSONString());
			stmt.setInt(2, Integer.parseInt(no));
			int count = stmt.executeUpdate();
			return (count == 1) ? true : false;

		} finally {
			if(rs != null) rs.close();
			if (stmt != null) stmt.close();
			if (conn != null) conn.close();
		}
	}

    
public boolean evaluateUserById(String userId, String evaluation) throws NamingException, SQLException, ParseException {
		Connection conn = ConnectionPool.get();
		PreparedStatement stmt = null; 
		ResultSet rs = null;
		try {				
			String sql = "SELECT evaluation FROM user where id = ?";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, userId);
			rs = stmt.executeQuery();
			
			int evaluationUpdate = 0;
			int cnt = 0;
			while(rs.next()) {
				if(cnt ++ > 0) evaluationUpdate += 0;
				evaluationUpdate += rs.getInt("evaluation");
			}
			
            
            evaluationUpdate += Integer.parseInt(evaluation);
			String sql2 = "UPDATE user SET evaluation = ? where id = ?";
			stmt = conn.prepareStatement(sql2);
			stmt.setInt(1, evaluationUpdate);
			stmt.setString(2, userId);
			int count = stmt.executeUpdate();
			return (count == 1) ? true : false;

		} finally {
			if(rs != null) rs.close();
			if (stmt != null) stmt.close();
			if (conn != null) conn.close();
		}
	}

	public boolean insertUserFeed(String userId, int feedNo) throws NamingException, SQLException, ParseException {
		Connection conn = ConnectionPool.get();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			synchronized (this) {
	
				String sql = "INSERT INTO userfeed(feedNo, userId) VALUES(?, ?)";
				stmt = conn.prepareStatement(sql);
				stmt.setInt(1, feedNo);
				stmt.setString(2, userId);
				int count = stmt.executeUpdate();
				return (count == 1) ? true : false;
			}
		} finally {
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
			if (conn != null)
				conn.close();
		}
	}
	
	public int getUserFeedCount(String userId) throws NamingException, SQLException, ParseException {
		Connection conn = ConnectionPool.get();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			String sql = "SELECT count(*) as userfeedCount FROM userfeed where userId = ?";

			stmt = conn.prepareStatement(sql);
			stmt.setString(1, userId);
			rs = stmt.executeQuery();
			if (!rs.next())
				return 1;

			int userfeedCount = rs.getInt("userfeedCount");

			return userfeedCount;

		} finally {
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
			if (conn != null)
				conn.close();
		}
	}
	
}
