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

public class UserDAO {
	public boolean insert(String jsonstr) throws NamingException, SQLException, ParseException {
		Connection conn = ConnectionPool.get();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			synchronized(this) {
				String sql = "select no from user order by no desc limit 1";
				stmt = conn.prepareStatement(sql);
				rs = stmt.executeQuery();
				
				int max = (!rs.next()) ? 0 : rs.getInt("no");
				
				JSONObject jsonobj = (JSONObject) (new JSONParser()).parse(jsonstr);
				jsonobj.put("no", max + 1);
				stmt.close();
				
				sql = "insert into user(no, id, jsonstr) values(?, ?, ?)";
				stmt = conn.prepareStatement(sql);
				stmt.setInt(1, max + 1);
				stmt.setString(2, jsonobj.get("id").toString());
				stmt.setString(3, jsonstr);
				
				int count = stmt.executeUpdate();
				return (count == 1) ? true : false;
			}
		} finally {
			if (rs != null) conn.close();
			if (stmt != null) stmt.close();
			if (conn != null) conn.close();
		}
	}
	
	public boolean exists(String uid) throws NamingException, SQLException {
		Connection conn = ConnectionPool.get();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			String sql = "select id from user where id = ?";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, uid);
			
			rs = stmt.executeQuery();
			return rs.next(); //�엳�쑝硫� true, �뾾�쑝硫� false
		} finally {
			if (rs != null) rs.close();
			if (stmt != null) stmt.close();
			if (conn != null) conn.close();
		}
	}
	
	public boolean withdraw(String uid, String upass) throws NamingException, SQLException{
		Connection conn = ConnectionPool.get();
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement("DELETE FROM user WHERE id = ? and json_extract(jsonstr, '$.password') = ? ");
			stmt.setString(1, uid);
			stmt.setString(2, upass);
			int count = stmt.executeUpdate();
			return (count == 1) ? true : false; 
		} finally {
			if(stmt != null) stmt.close();
			if(conn != null) conn.close();
		}
	}
	
	public boolean delete(String uid) throws NamingException, SQLException {
		Connection conn = ConnectionPool.get();
		PreparedStatement stmt = null;
		try {
			String sql = "DELETE FROM user WHERE id = ?";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, uid);
			
			int count = stmt.executeUpdate();
			return (count > 0) ? true : false;
		} finally {
			if (stmt != null) stmt.close();
			if (conn != null) conn.close();
		}
	}
	
	public int login(String uid, String upass) throws NamingException, SQLException, ParseException {
		Connection conn = ConnectionPool.get();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			String sql = "SELECT jsonstr FROM user WHERE id = ?";	
			
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, uid);
			
			rs = stmt.executeQuery();
			if (!rs.next()) return 1;
			
			String jsonstr = rs.getString("jsonstr");
			JSONObject obj = (JSONObject) (new JSONParser()).parse(jsonstr);
			String pass = obj.get("password").toString();		
			if (!upass.equals(pass)) return 2;
			
			return 0;
			
		} finally {
			if (rs != null) rs.close();
			if (stmt != null) stmt.close();
			if (conn != null) conn.close();
		}
	}
	
	//login함수가 int형 리턴, 로그인한 사람 정보 String 형태로 가져오기
	public String loginPerson(String uid) throws NamingException, SQLException, ParseException {
		Connection conn = ConnectionPool.get();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			String sql = "select y, x from user where id = ?";
			
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, uid);
			rs = stmt.executeQuery();
			
			String str = "";
			if (rs.next()) {
				str += rs.getString("y");
				str += ", ";
				str += rs.getString("x");
			}
			return str;
			
		} finally {
			if (rs != null) rs.close();
			if (stmt != null) stmt.close();
			if (conn != null) conn.close();
		}
	}
	
	public String getList() throws NamingException, SQLException {
		Connection conn = ConnectionPool.get();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			String sql = "SELECT * FROM user";
			stmt = conn.prepareStatement(sql);
			rs = stmt.executeQuery();
			
			String str = "[";
			int cnt = 0;
			while(rs.next()) {
				if (cnt++ > 0) str += ", ";
				str += rs.getString("jsonstr");
			}
			return str + "]";
			
		} finally {
			if (rs != null) rs.close();
			if (stmt != null) stmt.close();
			if (conn != null) conn.close();
		}
	}
	
	public String myInfo(String uid) throws NamingException, SQLException {
		Connection conn = ConnectionPool.get();
		PreparedStatement stmt = null;
		ResultSet rs = null;	
		try {
			stmt = conn.prepareStatement("select jsonstr from user where id = ?");
			stmt.setString(1, uid);
			rs = stmt.executeQuery();
			
			String str = "[";
			int cnt = 0;
			while(rs.next()) {
				if(cnt ++ > 0) str += ", ";
				str += rs.getString("jsonstr");
			}
			return str + "]";
			
		}finally {
			if(rs != null) rs.close();
			if(stmt != null) stmt.close();
			if(conn != null) conn.close();
		}
	}

	public String get(String uid) throws NamingException, SQLException {
		Connection conn = ConnectionPool.get();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			String sql = "SELECT jsonstr FROM user WHERE id = ?";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, uid);
			
			rs = stmt.executeQuery();
			return rs.next() ? rs.getString("jsonstr") : "{}";
			
		} finally {
			if (rs != null) rs.close();
			if (stmt != null) stmt.close();
			if (conn != null) conn.close();
		}
	}
	
	public String getProfile(String uid) throws NamingException, SQLException {
		Connection conn = ConnectionPool.get();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			String sql = "SELECT * FROM user WHERE id = ?";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, uid);
			rs = stmt.executeQuery();
			
			String str = "[";
			int cnt = 0;
			while(rs.next()) {
				if (cnt++ > 0) str += ", ";
				str += rs.getString("jsonstr");
			}
			return str + "]";
			
		} finally {
			if (rs != null) rs.close();
			if (stmt != null) stmt.close();
			if (conn != null) conn.close();
		}
	}
	
	public boolean update(String uid, String jsonstr) throws NamingException, SQLException {
		Connection conn = ConnectionPool.get();
		PreparedStatement stmt = null;
		try {
			String sql = "UPDATE user SET jsonstr = ? WHERE id = ?";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, jsonstr);
			stmt.setString(2, uid);
			
			int count = stmt.executeUpdate();
			return (count == 1) ? true : false;
		} finally {
			if (stmt != null) stmt.close();
			if (conn != null) conn.close();
		}
	}
	
	/* 211120 �߰� */
	public String getUserGeo(String uid) throws NamingException, SQLException {
		Connection conn = ConnectionPool.get();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			String sql = "SELECT y, x FROM user WHERE id = ?";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, uid);
			rs = stmt.executeQuery();
			
			
			String y = "";
			String x = "";
			int cnt = 0;
			while(rs.next()) {
				y += rs.getString("y");
				x += rs.getString("x");
			}
			
			JSONObject geo = new JSONObject();
			geo.put("y", y);
			geo.put("x", x);
			return geo.toJSONString();
			
		} finally {
			if (rs != null) rs.close();
			if (stmt != null) stmt.close();
			if (conn != null) conn.close();
		}
	}
	
	public boolean insertGeo(String y, String x, String id) throws NamingException, SQLException, ParseException {
		Connection conn = ConnectionPool.get();
		PreparedStatement stmt = null;
		try {
			synchronized(this) {
				
				
				String sql = "UPDATE user SET y=?, x=? where id = ?";
				stmt = conn.prepareStatement(sql);
				stmt.setString(1, y);
				stmt.setString(2, x);
				stmt.setString(3, id);
				
				int count = stmt.executeUpdate();
				return (count == 1) ? true : false;
			}
		} finally {
			if (stmt != null) stmt.close();
			if (conn != null) conn.close();
		}
	}
	
	public int getUserCount() throws NamingException, SQLException, ParseException {
		Connection conn = ConnectionPool.get();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			String sql = "SELECT count(*) as peopleCount FROM user";	
			
			stmt = conn.prepareStatement(sql);
			
			rs = stmt.executeQuery();
			if (!rs.next()) return 1;
			
			int peopleCount = rs.getInt("peopleCount");

			return peopleCount;
			
		} finally {
			if (rs != null) rs.close();
			if (stmt != null) stmt.close();
			if (conn != null) conn.close();
		}
	}
	
	// 관리자기능 시작
	
	// 관리자기능 - user 전체 목록 불러오기 : UserDAO().getList() 사용
	
	// 관리자기능 - user 정보 : UserDAO().get() 사용
	
	// 관리자기능 - user 수정 : UserDAO().update() 사용
	
	// 관리자기능 - user 삭제 : UserDAO().delete() 사용
	
	//관리자기능 - 사용자 id로 user 찾아서 가져오기
	public String UserSearchById(String uid) throws NamingException, SQLException {
		Connection conn = ConnectionPool.get();
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			String sql = "select jsonstr from user where id = ?" ;
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, uid);
			rs = stmt.executeQuery();

			String str = "[";
			int cnt = 0;
			while(rs.next()) {
				if(cnt ++ > 0) str += ",";
				str += rs.getString("jsonstr");
			}
			return str + "]";

		} finally {
			if (rs != null) rs.close();
			if (stmt != null) stmt.close();
			if (conn != null) conn.close();
		}
	}
}