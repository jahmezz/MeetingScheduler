package Scheduler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class Model {
	final int[] HOURS = {9,12,15};
	final int MORNING = 0;
	final int AFTERNOON = 1;
	final int EVENING = 2;
	
	String user = null;
	private String displayName;
	private boolean admin = false;
	private ArrayList<String[]> roomData = new ArrayList<String[]>();
	private ArrayList<String[]> meetingData = new ArrayList<String[]>();
	private ArrayList<String> team;
	private int[] userMeetings;
	private String phone;
	private String email;
	private Connection employees;
	PreparedStatement statement;
	ResultSet result;
	private int nextMeeting;
	private ArrayList<String[]> searchedMeetingData;
	private ArrayList<String[]> editedPossibleRooms;
	private String userMeetingStrings;
	private boolean visible;
	private boolean userMorning;
	private boolean userAfternoon;
	private boolean userEvening;
	private ArrayList<String[]> ownedMeetingData;
	private ArrayList<Integer> noticeMeetings = new ArrayList<Integer>();
	private ArrayList<String> noticeStrings = new ArrayList<String>();

	
	public Model() throws Exception	{
		Class.forName("com.mysql.jdbc.Driver");
		employees = DriverManager.getConnection("jdbc:mysql://localhost:3306/employees", "root", "");
	}
	
	public String getUser()	{
		return user;
	}
	
	public void setUser(String name)	{
		user = name;
	}

	public void setAdmin(boolean b) {
		admin = b;
	}

	public boolean isSignedIn() {
		return user != null;
	}

	public boolean isAdmin() {
		return admin;
	}
	public String[][] getRoomData() {
		if (roomData == null)	{
			return null;
		}
		return roomData.toArray(new String[roomData.size()][2]);
	}
	public void setRoomData(ArrayList<String[]> roomData) {
		this.roomData = roomData;
	}
	public String[] getDetails() {
		String[] details = {displayName, phone, email};
		return details;
	}

	public void setDetails(String displayName, String phone, String email,
			String listOfMeetings, boolean visible,
			boolean morning, boolean afternoon, boolean evening) {
		this.displayName = displayName;
		this.phone = phone;
		this.email = email;
		String[] meetingStrings = listOfMeetings.split(",");
		this.userMeetingStrings = listOfMeetings;
		this.userMeetings = toIntArray(meetingStrings);
		if(userMeetings.length == 0) userMeetings = null;
		this.visible = visible;
		this.userMorning = morning;
		this.userAfternoon = afternoon;
		this.userEvening = evening;
	}
	
	private int[] toIntArray(String[] meetingStrings) {
		int[] result = new int[meetingStrings.length];
		for(int i = 0; i < meetingStrings.length; i++)	{
			if(meetingStrings[i].equals("")) result[i] = 0;
			else result[i] = Integer.parseInt(meetingStrings[i]);
		}
			
		return result;
	}

	public void login(String username, String password) throws SQLException {
		if (username == "" || password == "") return;
		statement = employees.prepareStatement("SELECT * FROM users "+
				"WHERE username = '" + username + "' and password = '" + password + "'");
		result = statement.executeQuery();
		
		if(result.next())	{
			setAdmin(result.getBoolean("admin"));
			setUser(result.getString(1));
			setDetails(result.getString(4), result.getString(5),
					result.getString(6), result.getString(7),
					result.getBoolean(8), result.getBoolean(9), 
					result.getBoolean(10), result.getBoolean(11));
			fillTeam();
			fillMeetings();
			findOwnedMeetings();
			fillNotices();
		}
		else	{
			setAdmin(false);
			setUser(null);
		}
		if(isAdmin()) fillRooms();
		
	}
	
	private void fillNotices() throws SQLException {
		for(int notice : noticeMeetings)	{
			statement = employees.prepareStatement("SELECT * FROM meetings where " +
		"meeting_id = " + notice);
			result = statement.executeQuery();
			if(result.next())	{
				noticeStrings.add(result.getString("datetime"));
			}
		}
		
	}

	private void fillTeam() throws SQLException {
		statement = employees.prepareStatement("SELECT * FROM users where username != '" +getUser()+"'");
		result = statement.executeQuery();
		team = new ArrayList<String>();
		while(result.next())
			if(result.getBoolean("visible"))
				team.add(result.getString(1));
	}

	public ArrayList<String> getTeam() {
		return (ArrayList<String>) team.clone();
	}

	public void fillRooms() throws SQLException	{
		//fill rooms table
		statement = employees.prepareStatement("SELECT * FROM rooms");
		result = statement.executeQuery();
		roomData = new ArrayList();
		String[] fields = new String[2];
		while(result.next())	{
			fields[0] = result.getString(1);
			fields[1] = result.getString(2);
			roomData.add(fields.clone());
		}
	}
	
	public void fillMeetings() throws SQLException	{
		statement = employees.prepareStatement("SELECT * FROM meetings");
		result = statement.executeQuery();
		meetingData = new ArrayList<String[]>();
		if(!result.next()) nextMeeting = 0;
		else	{
			result.last();
			nextMeeting = result.getInt("meeting_id") + 1;
		}
		result.beforeFirst();
		String[] meeting = new String[4];
		for(int i : userMeetings)	{
			System.out.println(i);
			statement = employees.prepareStatement("SELECT * FROM meetings where meeting_id = '" + i + "'");
			result = statement.executeQuery();
			if(!result.next()) continue;
			meeting[0] = getDateTime(result.getTimestamp("datetime"));
			meeting[1] = result.getString(4);
			meeting[2] = result.getString(5);
			meeting[3] = result.getString("meeting_id");
			if(result.getBoolean("changed")) noticeMeetings.add(i);
			meetingData.add(meeting.clone());
			statement = employees.prepareStatement("UPDATE meetings SET changed = 0 " +
			"where meeting_id = '" + i + "'");
			statement.executeUpdate();
		}
	}

	public void addRoom(String newRoomName, String newRoomCap) {
		String[] room = {newRoomName, newRoomCap};
		roomData.add(room);
	}

	public void createUser(String newUsername, String newPassword,
			boolean isAdmin) throws SQLException {
		PreparedStatement statement = employees.prepareStatement("SELECT * FROM users "+
				"WHERE username = '" + newUsername + "' and password = '" +
				newPassword + "'");
		ResultSet result = statement.executeQuery();
		if(!result.next())	{
			statement = employees.prepareStatement("INSERT users" +
					" VALUES ('" +
						newUsername + "', '" + newPassword + "', " + isAdmin + ",'','','','',1,1,1,1)");
				statement.executeUpdate();
		}
		team.add(newUsername);
	}
	
	public void createRoom(String newRoomName, String newRoomCap) throws SQLException {
		String times = "";
		PreparedStatement statement = employees.prepareStatement("INSERT rooms" +
				" VALUES ('" +
					newRoomName + "', '" + newRoomCap + "')");
		statement.executeUpdate();
		addRoom(newRoomName, newRoomCap);
	}
	
	public void createMeeting(List<String> employeeList, String time, String room) throws SQLException {
		String concatEmployees = getUser() + ",";
		statement = employees.prepareStatement("INSERT employeemeeting" +
				" VALUES ('" + getUser() + "', '" + time + 
				"', '" + room + "', '" + (nextMeeting+1)+ "')");
		statement = employees.prepareStatement("SELECT * from users " +
				" WHERE username = '" + getUser() + "'");
		result = statement.executeQuery();
		result.next();
		String meetings = result.getString("meetings");
		if(meetings.length() == 0)	{
			meetings = "" + (nextMeeting+1);
		}
		else	{
			meetings = meetings + "," +(nextMeeting+1);
		}
		statement = employees.prepareStatement("INSERT employeemeeting" +
				" VALUES ('" + getUser() + "', '" + time + 
				"', '" + room + "', '" + (nextMeeting+1)+ "')");
		statement.executeUpdate();
		statement = employees.prepareStatement("UPDATE users" +
				" SET meetings ='" + meetings + "' WHERE username = '" + getUser() + "'");
		statement.executeUpdate();
		for(String employee : employeeList)	{
			concatEmployees = concatEmployees + employee + ",";
			statement = employees.prepareStatement("INSERT employeemeeting" +
					" VALUES ('" + employee + "', '" + time + 
					"', '" + room + "', '" + (nextMeeting+1)+ "')");
			System.out.println("creation: " + statement.toString());
			statement.executeUpdate();
			statement = employees.prepareStatement("SELECT * from users " +
					" WHERE username = '" + employee + "'");
			result = statement.executeQuery();
			result.next();
			meetings = result.getString("meetings");
			if(meetings.length() == 0)	{
				meetings = "" + (nextMeeting+1);
			}
			else	{
				meetings = meetings + "," +(nextMeeting+1);
			}
			statement = employees.prepareStatement("UPDATE users" +
					" SET meetings ='" + meetings + "' WHERE username = '" + employee + "'");
			statement.executeUpdate();
		}
		concatEmployees = concatEmployees.substring(0, concatEmployees.length()-1);
		//add to meetings here
		statement = employees.prepareStatement("INSERT meetings" +
				" VALUES ('" + (nextMeeting+1) + "', '" + time + "', '" + getUser() + "', '" +
				concatEmployees + "', '" + room + "', 1)");
		statement.executeUpdate();
		nextMeeting++;
		String[] newMeeting = {time, concatEmployees, room, ""+nextMeeting};
		meetingData.add(newMeeting);
	}
	
	public void logout() {
		setAdmin(false);
		setUser(null);
		meetingData.clear();
		roomData.clear();
		team.clear();
	}
	
	public void saveProfile(String name, String phone, String email, boolean[] booleans) throws SQLException {
		statement = employees.prepareStatement("UPDATE users" +
				" SET display_name = '" + name + "', phone = '" + phone + "', email = '" +
				email + "', visible = ?, morning = ?, afternoon = ?, evening = ? WHERE username = '" + getUser() + "'");
		statement.setBoolean(1, booleans[0]);
		statement.setBoolean(2, booleans[1]);
		statement.setBoolean(3, booleans[2]);
		statement.setBoolean(4, booleans[3]);
		statement.executeUpdate();
	}

	public String[][] getMeetings() {
		if (meetingData == null)	return null;
		return meetingData.toArray(new String[meetingData.size()][4]);
	}

	public boolean searchMeeting(List<String> selectedEmployees, Date date, int timeOfDay) throws SQLException {
		if(selectedEmployees.isEmpty())
			return false;
		selectedEmployees.add(getUser());
		Calendar calendar = new GregorianCalendar();
		editedPossibleRooms = new ArrayList<String[]>();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        System.out.println("calendar?" + calendar.getTime());
		//check the time of day we want
		statement = employees.prepareStatement("SELECT * FROM rooms WHERE capacity > " +
				selectedEmployees.size());
		ResultSet roomResult = statement.executeQuery();
		List<String> checkRooms = makeList(roomResult, selectedEmployees.size());
		List<Integer> possibleTimes = checkMeetingTimes(timeOfDay);
		//check employee's time of day settings
		for(String employee : selectedEmployees)	{
			if(!available(employee, timeOfDay))	{
				return false;
			}
		}
		//we have set of possible meeting times!
		//check for possible rooms
		editedPossibleRooms = new ArrayList<String[]>();
		String[] roomData;
		for(int time : possibleTimes)	{
			for(String room : checkRooms)	{
				calendar.set(Calendar.HOUR_OF_DAY, time);
				Timestamp timestamp = new Timestamp(calendar.getTime().getTime());
				statement = employees.prepareStatement("SELECT * FROM employeemeeting " +
						"WHERE datetime = ?");
				statement.setTimestamp(1, timestamp);
				System.out.println("latest thang: "  + statement.toString());
				result = statement.executeQuery();
				//check if any employees are busy at that time
				boolean skip = false;
				while(result.next())	{
					System.out.println("person" + result.getString("employee"));
					if(selectedEmployees.contains(result.getString("employee")))
						skip = true;
				}
				if(skip) continue;
				//if not list the sucker!
				roomData = new String[3];
				roomData[0] = room;
				roomData[1] = getDate(calendar.getTime());
				roomData[2] = getTime(calendar.getTime());
				editedPossibleRooms.add(roomData);
			}
		}
		return !editedPossibleRooms.isEmpty();
	}

	private List makeList(ResultSet roomResult, int size) throws SQLException {
		List<String> rooms = new ArrayList<String>();
		while(roomResult.next())	{
			rooms.add(roomResult.getString("name"));
		}
		return rooms;
	}

	private List<Integer> checkMeetingTimes(int timeOfDay) throws SQLException {
		List<Integer> possibleTimes = new ArrayList<Integer>();
		switch(timeOfDay)	{
			case MORNING:
				possibleTimes.add(9);
				possibleTimes.add(10);
				possibleTimes.add(11);
				break;
			case AFTERNOON:
				possibleTimes.add(12);
				possibleTimes.add(13);
				possibleTimes.add(14);
				break;
			case EVENING:
				possibleTimes.add(15);
				possibleTimes.add(16);
				possibleTimes.add(17);
				break;
		}
		return possibleTimes;
	}

	private boolean available(String employee, int timeOfDay) throws SQLException {
		statement = employees.prepareStatement("SELECT * from users where username = '" +
				employee + "'");
		result = statement.executeQuery();
		result.next();
		String timeToCheck = "";
		switch(timeOfDay)	{
			case MORNING:
				timeToCheck = "morning";
				break;
			case AFTERNOON:
				timeToCheck = "afternoon";
				break;
			case EVENING:
				timeToCheck = "evening";
				break;
		}
		if(!result.getBoolean(timeToCheck)) return false;
		return true;
	}

	public String[][] getFoundRooms() {
		if (editedPossibleRooms == null)	return null;
		return editedPossibleRooms.toArray(new String[editedPossibleRooms.size()][3]);
	}

	public String[][] getSearchedMeetings() {
		if (searchedMeetingData == null)	return null;
		return searchedMeetingData.toArray(new String[searchedMeetingData.size()][4]);
	}

	private String getTime(Date date) {
		SimpleDateFormat printFormat = new SimpleDateFormat("HH:mm:ss");
		return printFormat.format(date);
	}

	private String getDate(Date date) {
		SimpleDateFormat printFormat = new SimpleDateFormat("yyyy-MM-dd");
		return printFormat.format(date);
	}
	
	private String getDateTime(Date date)	{
		SimpleDateFormat printFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return printFormat.format(date);
	}

	public boolean[] getBooleans() {
		boolean[] booleans = {visible, userMorning, userAfternoon, userEvening};
		return booleans;
	}

	public void resetUser(String newUsername) throws SQLException {
		statement = employees.prepareStatement("UPDATE users" +
				" SET password = 'password' WHERE username = '" + newUsername + "'");
		statement.executeUpdate();
	}

	public void deleteUser(String newUsername) throws SQLException {
		statement = employees.prepareStatement("DELETE FROM users" +
				" WHERE username ='" + newUsername + "'");
		statement.executeUpdate();
		team.remove(newUsername);
	}

	public void deleteMeeting(int meetingId) throws Exception {
		//delete all records of the meeting
		statement = employees.prepareStatement("DELETE FROM employeemeeting" +
				" WHERE meeting_id =" + meetingId);
		statement.executeUpdate();
		statement = employees.prepareStatement("DELETE FROM meetings" +
				" WHERE meeting_id =" + meetingId);
		statement.executeUpdate();
		//update user database and remove all record of this meeting
		statement = employees.prepareStatement("SELECT * FROM users");
		result = statement.executeQuery();
		while(result.next())	{
			String changedUser = result.getString("username");
			String[] parsedMeetings = result.getString("meetings").split(",");
			if(parsedMeetings.length == 0)
				continue;
			String newMeetings = "";
			for(String parsedMeetingId : parsedMeetings)	{
				if(parsedMeetingId.length() == 0) continue;
				if (Integer.parseInt(parsedMeetingId) != meetingId)	{
					newMeetings = newMeetings + parsedMeetingId + ",";
				}
			}
			if(newMeetings.length() > 0)
				newMeetings = newMeetings.substring(0, newMeetings.length()-1);
			statement = employees.prepareStatement("UPDATE users " +
					"SET meetings = '" + newMeetings + 
					"' 	WHERE username ='" + changedUser + "'");
			statement.executeUpdate();
		}
	}

	public void findOwnedMeetings() throws SQLException {
		statement = employees.prepareStatement("SELECT * FROM meetings" +
				" WHERE owner ='" + getUser() + "'");
		result = statement.executeQuery();
		ownedMeetingData = new ArrayList<String[]>();
		String[] meeting = new String[4];
		while(result.next())	{
			meeting[0] = getDateTime(result.getTimestamp("datetime"));
			meeting[1] = result.getString(4);
			meeting[2] = result.getString(5);
			meeting[3] = result.getString("meeting_id");
			ownedMeetingData.add(meeting.clone());
		}
	}
	
	public String[][] getOwnedMeetingData()	{
		if (ownedMeetingData == null)	return null;
		return ownedMeetingData.toArray(new String[ownedMeetingData.size()][4]);
	}

	public boolean editMeeting(int meetingId) throws SQLException	{
		if(meetingId == -1) return false;
		// TODO Auto-generated method stub
		return admin;
		
	}

	public String[][] getFoundEditedRooms() {
		if (editedPossibleRooms == null)	return null;
		return editedPossibleRooms.toArray(new String[editedPossibleRooms.size()][3]);
	}

	public boolean searchEditedMeeting(List<String> editedEmployees,
			String date, int editedTod, int meeting_id) throws SQLException, ParseException {
		if(editedEmployees.isEmpty())
			return false;
		editedEmployees.add(getUser());
		Calendar calendar = new GregorianCalendar();
		editedPossibleRooms = new ArrayList<String[]>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		calendar.setTime(sdf.parse(date));
		calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        System.out.println("calendar?" + calendar.getTime());
		//check the time of day we want
		statement = employees.prepareStatement("SELECT * FROM rooms WHERE capacity > " +
				editedEmployees.size());
		ResultSet roomResult = statement.executeQuery();
		List<String> checkRooms = makeList(roomResult, editedEmployees.size());
		List<Integer> possibleTimes = checkMeetingTimes(editedTod);
		//check employee's time of day settings
		for(String employee : editedEmployees)	{
			if(!available(employee, editedTod))	{
				return false;
			}
		}
		//we have set of possible meeting times!
		//check for possible rooms
		editedPossibleRooms = new ArrayList<String[]>();
		String[] roomData;
		for(int time : possibleTimes)	{
			for(String room : checkRooms)	{
				calendar.set(Calendar.HOUR_OF_DAY, time);
				Timestamp timestamp = new Timestamp(calendar.getTime().getTime());
				statement = employees.prepareStatement("SELECT * FROM employeemeeting " +
						"WHERE datetime = ?");
				statement.setTimestamp(1, timestamp);
				System.out.println("latest thang: "  + statement.toString());
				result = statement.executeQuery();
				//check if any employees are busy at that time
				boolean skip = false;
				while(result.next())	{
					System.out.println("person" + result.getString("employee"));
					if(editedEmployees.contains(result.getString("employee")))
						skip = true;
				}
				if(skip) continue;
				//if not list the sucker!
				roomData = new String[3];
				roomData[0] = room;
				roomData[1] = getDate(calendar.getTime());
				roomData[2] = getTime(calendar.getTime());
				editedPossibleRooms.add(roomData);
			}
		}
		return !editedPossibleRooms.isEmpty();
	}

	public void createMeeting(List<String> editedEmployees, String time,
			String room, int selectedMeetingId) throws SQLException {
		String concatEmployees = getUser() + ",";
		statement = employees.prepareStatement("INSERT employeemeeting" +
				" VALUES ('" + getUser() + "', '" + time + 
				"', '" + room + "', '" + selectedMeetingId + "')");
		statement = employees.prepareStatement("SELECT * from users " +
				" WHERE username = '" + getUser() + "'");
		result = statement.executeQuery();
		result.next();
		String meetings = result.getString("meetings");
		if(meetings.length() == 0)	{
			meetings = "" + selectedMeetingId;
		}
		else	{
			meetings = meetings + "," +selectedMeetingId;
		}
		statement = employees.prepareStatement("INSERT employeemeeting" +
				" VALUES ('" + getUser() + "', '" + time + 
				"', '" + room + "', '" + selectedMeetingId+ "')");
		statement.executeUpdate();
		statement = employees.prepareStatement("UPDATE users" +
				" SET meetings ='" + meetings + "' WHERE username = '" + getUser() + "'");
		statement.executeUpdate();
		for(String employee : editedEmployees)	{
			concatEmployees = concatEmployees + employee + ",";
			statement = employees.prepareStatement("INSERT employeemeeting" +
					" VALUES ('" + employee + "', '" + time + 
					"', '" + room + "', '" + selectedMeetingId+ "')");
			System.out.println("creation: " + statement.toString());
			statement.executeUpdate();
			statement = employees.prepareStatement("SELECT * from users " +
					" WHERE username = '" + employee + "'");
			result = statement.executeQuery();
			result.next();
			meetings = result.getString("meetings");
			if(meetings.length() == 0)	{
				meetings = "" + selectedMeetingId;
			}
			else	{
				meetings = meetings + "," +selectedMeetingId;
			}
			statement = employees.prepareStatement("UPDATE users" +
					" SET meetings ='" + meetings + "' WHERE username = '" + employee + "'");
			statement.executeUpdate();
		}
		concatEmployees = concatEmployees.substring(0, concatEmployees.length()-1);
		//add to meetings here
		statement = employees.prepareStatement("INSERT meetings" +
				" VALUES ('" + selectedMeetingId + "', '" + time + "', '" + getUser() + "', '" +
				concatEmployees + "', '" + room + "', 1)");
		statement.executeUpdate();
		String[] newMeeting = {time, concatEmployees, room, ""+selectedMeetingId};
		meetingData.add(newMeeting);
	}

	public boolean changePassword(String changePassword, String confirmPassword) throws SQLException {
		if(!changePassword.equals(confirmPassword)) return false;
		statement = employees.prepareStatement("UPDATE users" +
				" SET password = '" + changePassword + "' WHERE username ='" + getUser() + "'");
		statement.executeUpdate();
		return true;
	}

	public String getNotices() {
		String result = "";
		if(noticeStrings.isEmpty()) return result;
		for(String notice : noticeStrings )	{
			result = result + notice + ", ";
		}
		result = result.substring(0, result.length()-2);
		noticeStrings = new ArrayList();
		return result;
	}

	public void deleteRoom(String deleteRoom) throws SQLException {
		statement = employees.prepareStatement("DELETE FROM rooms" +
				" WHERE name = '" + deleteRoom + "'");
		statement.executeUpdate();
		roomData.remove(deleteRoom);
		
	}
}
