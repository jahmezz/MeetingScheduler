package Scheduler;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class Controller {
	View view;
	Model model;
	public String notice = "";

	public Controller(View view, Model model) throws Exception	{
		this.view = view;
		this.model = model;
		this.view.addLoginListener(new LoginListener());
		this.view.addEmployeeListener(new EmployeeListener());
	}
	
	private void populateFields() throws SQLException {
		view.updateRooms(model.getRoomData());
		view.updateProfile(model.getDetails(), model.getBooleans());
		view.updateTeam(model.getTeam());
		view.updateMeetings(model.getMeetings(), model.getOwnedMeetingData());
	}

	//handles actions for login and create new user
	class LoginListener implements ActionListener 	{
		public void actionPerformed(ActionEvent e)	{
			String command = e.getActionCommand();
			String nextCard = null;
			String prompt = null;
			switch(command)	{
				case "Sign-in":
					try {
						model.login(view.getUsername(), view.getPassword());
						if(model.isSignedIn())	{
							prompt = "Welcome, " + model.getUser() + "!";
							nextCard = "main";
							populateFields();
							String changes = model.getNotices();
							if(changes.length() >0)
								notice = "Notice: Updates at " + changes + "!";
						}
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
					if(!model.isSignedIn())	{
						prompt = "Username or password is incorrect";
					}
					break;
				case "Edit users":
					nextCard = "newuser";
					break;
				case "Create account":
					if(view.getNewPassword() == null || view.getConfirmPassword() == null)	{
						prompt = "Please enter valid password";
						nextCard = "newuser";
						view.reset();
						break;
					}
				try {
					model.createUser(view.getNewUsername(), view.getNewPassword(), view.getAdmin());
					view.updateTeam(model.getTeam());
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
					prompt = "New account created";
					nextCard = "newuser";
					view.reset();
					break;
				case "Cancel account":
					view.reset();
					nextCard = "main";
					break;
				case "Log out":
					model.logout();
					view.reset();
					nextCard = "login";
					break;
			}
			view.update(nextCard, prompt, model.isAdmin());
			if(notice.length() > 0)
				view.showPrompt(notice);
			notice = "";
		}
	}
	
	//handles actions for employee
	class EmployeeListener implements ActionListener	{
		public void actionPerformed(ActionEvent e)	{
			String command = e.getActionCommand();
			String nextCard = null;
			String prompt = null;
			switch(command)	{
				case "Delete room":
				try {
					model.deleteRoom(view.getDeleteRoom());
					model.fillRooms();
					view.updateRooms(model.getRoomData());
					prompt = "Room deleted.";
				} catch (SQLException e6) {
					// TODO Auto-generated catch block
					e6.printStackTrace();
				}
					break;
				case "New meeting":
					nextCard = "new";
					break;
				case "Edit rooms":
					nextCard = "add";
					break;
				case "Schedule meeting":
				try {
					model.createMeeting(view.getSelectedEmployees(), view.getSelectedDate(), 
							view.getSelectedRoom());
					view.reset();
					view.updateMeetings(model.getMeetings(), model.getOwnedMeetingData());
				} catch (SQLException e4) {
					// TODO Auto-generated catch block
					e4.printStackTrace();
				}
					prompt = "Meeting created";
					nextCard = "main";
					view.updateRooms(model.getRoomData());
					break;
				case "Look for times":
					boolean passed = false;
					try {
						passed = model.searchMeeting(view.getSelectedEmployees(), view.getSearchDate(),
								view.getTimeOfDay());
						view.setPossibleRooms(model.getFoundRooms());
					} catch (SQLException e3) {
						// TODO Auto-generated catch block
						e3.printStackTrace();
					}
					if(!passed)	{
						prompt = "There are no valid meetings for that combination of people on that day.";
						nextCard = "new";
					}
					else
						nextCard = "pick";
					break;
				case "Change password":
				try {
					if(model.changePassword(view.getChangePassword(),
							view.getConfirmChange()))	{
						prompt = "Password changed.";
					}
					else	{
						prompt = "Password not valid, try again.";
					}
				} catch (SQLException e5) {
					// TODO Auto-generated catch block
					e5.printStackTrace();
				}
					view.reset();
					break;
				case "Edit meetings":
				try {
					model.findOwnedMeetings();
				} catch (SQLException e3) {
					// TODO Auto-generated catch block
					e3.printStackTrace();
				}
					view.updateMeetings(model.getMeetings(),model.getOwnedMeetingData());
					nextCard = "edit";
					break;
				case "Edit Profile":
					nextCard = "profile";
					break;
				case "Save changes":
					try {
						model.saveProfile(view.getNameField(), view.getPhone(), view.getEmail(),
								view.getBooleans());
					} catch (SQLException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
					prompt = "Changes have been saved.";
					nextCard = "main";
					break;
				case "Employee main":
					view.reset();
					nextCard = "main";
					break;
				case "Add room":
					prompt = "Room created.";
					nextCard = "add";
					try {
						model.createRoom(view.getNewRoomName(), view.getNewRoomCap());
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					view.reset();
					view.updateRooms(model.getRoomData());
					break;
				case "Reset password":
					try {
						model.resetUser(view.getSelectedUsername());
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					nextCard = "newuser";
					prompt = view.getSelectedUsername() + "'s password has been reset to 'password'.";
					break;
				case "Delete user":
					try {
						model.deleteUser(view.getSelectedUsername());
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					nextCard = "newuser";
					prompt = view.getSelectedUsername() + " has been deleted.";
					view.updateTeam(model.getTeam());
					break;
				case "Edit meeting":
				try {
					view.setEditedFields(view.getSelectedMeetingDetails());
				} catch (ParseException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
					nextCard = "meetingeditor";
					break;
				case "Look for new times":
					passed = false;
					try {
						passed = model.searchEditedMeeting(view.getEditedEmployees(), view.getEditedDate(),
								view.getEditedTod(), view.getSelectedMeetingId());
						view.setEditedPossibleRooms(model.getFoundEditedRooms());
					} catch (Exception e3) {
						// TODO Auto-generated catch block
						e3.printStackTrace();
					}
					if(!passed)	{
						prompt = "This is not a valid edit.";
						nextCard = "meetingeditor";
					}
					else
						nextCard = "pickedit";
					break;
				case "Delete meeting":
					try {
						model.deleteMeeting(view.getSelectedMeetingId());
						model.fillMeetings();
						model.findOwnedMeetings();
						view.updateMeetings(model.getMeetings(), model.getOwnedMeetingData());
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					nextCard = "edit";
					prompt = "The meeting has been deleted.";
					view.updateTeam(model.getTeam());
					break;
				case "Change meeting":
					try	{
					model.deleteMeeting(view.getSelectedMeetingId());
					model.createMeeting(view.getEditedEmployees(), view.getEditedDate(), 
							view.getEditedRoom(), view.getSelectedMeetingId());
					model.fillMeetings();
					model.findOwnedMeetings();
					view.updateMeetings(model.getMeetings(), model.getOwnedMeetingData());
					view.reset();
				} catch (Exception e4) {
					// TODO Auto-generated catch block
					e4.printStackTrace();
				}
					prompt = "Meeting created";
					nextCard = "main";
					break;
			}
			view.update(nextCard, prompt, model.isAdmin());
		}
	}
}
