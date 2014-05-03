package Scheduler;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

import net.miginfocom.swing.MigLayout;

import com.imagine.component.calendar.CalendarComponent;

public class View extends JFrame	{
	private JList userList;
	private JPanel pickEditPanel;
	private JButton confirmEditedMeetingButton;
	private JButton cancelPickEdMeetingButton;
	private JTable possibleMeetingTable;
	private JButton cancelEditMeetingButton;

	public void update(String card, String prompt, boolean admin)	{
		addRoomButton.setVisible(admin);
		createUserButton.setVisible(admin);
		panel_2.setVisible(admin);
		stack.show(panelStack, card);
		if(prompt != null)	JOptionPane.showMessageDialog(frame, prompt);
	}
	
	/**
	 * Create the application.
	 */
	public View() {
		setupLogin();
		setupEmployee();
		setupAdmin();
		frame.pack();
	}
	
	public void reset()	{
		usernameField.setText("");
		passwordField.setText("");
		makeNameField.setText("");
		makePassField.setText("");
		confirmPassField.setText("");
		changePasswordField.setText("");
		confirmChangeField.setText("");
		newRoomNameField.setText("");
		newRoomCapField.setText("");
		adminCheckBox.setSelected(false);
	}
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void setupLogin() {
		setFrame(new JFrame("QuickMeet"));
		getFrame().setPreferredSize(new Dimension(640, 480));
		getFrame().setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//set up card stack
		panelStack = new JPanel(new CardLayout());
		panelStack.setOpaque(false);
		stack = (CardLayout) panelStack.getLayout();
		getFrame().getContentPane().add(panelStack);
		
		//login panel
		loginPanel = new JPanel();
		loginPanel.setBorder(BorderFactory.createTitledBorder("QuickMeet"));
		loginPanel.setLayout(new MigLayout("", "[][]", "[][][16.00]8[]"));
		panelStack.add(loginPanel, "login");
		
		//login panel components
		lblUsername = new JLabel("Username");
		loginPanel.add(lblUsername, "cell 0 0");
		usernameField = new JTextField();
		loginPanel.add(usernameField, "cell 1 0,growx");
		usernameField.setColumns(10);
		lblNewLabel_1 = new JLabel("Password");
		loginPanel.add(lblNewLabel_1, "cell 0 1");
		passwordField = new JPasswordField();
		loginPanel.add(passwordField, "cell 1 1,growx");
		
		separator_2 = new JSeparator();
		loginPanel.add(separator_2, "cell 0 2");
		signInButton = new JButton("Sign-in");
		loginPanel.add(signInButton, "flowx,cell 0 3");
		
		newUserPanel = new JPanel();
		panelStack.add(newUserPanel, "newuser");
		newUserPanel.setLayout(new MigLayout("", "[][grow]", "[][][][][32.00][grow][]"));
		lblNewLabel_4 = new JLabel("Username");
		newUserPanel.add(lblNewLabel_4, "cell 0 0,alignx trailing");
		
		makeNameField = new JTextField();
		newUserPanel.add(makeNameField, "cell 1 0,growx");
		makeNameField.setColumns(10);
		
		lblNewLabel_5 = new JLabel("Password");
		newUserPanel.add(lblNewLabel_5, "cell 0 1,alignx right");
		
		makePassField = new JPasswordField();
		newUserPanel.add(makePassField, "cell 1 1,growx");
		makePassField.setColumns(10);
		
		lblNewLabel_6 = new JLabel("Confirm password");
		newUserPanel.add(lblNewLabel_6, "cell 0 2,alignx trailing");
		
		confirmPassField = new JPasswordField();
		newUserPanel.add(confirmPassField, "cell 1 2,growx");
		confirmPassField.setColumns(10);
		
		lblSetAdmin = new JLabel("Set admin?");
		newUserPanel.add(lblSetAdmin, "cell 0 3,alignx right");
		
		adminCheckBox = new JCheckBox();
		newUserPanel.add(adminCheckBox, "cell 1 3");
		
		createAccountButton = new JButton("Create account");
		newUserPanel.add(createAccountButton, "cell 1 4");
		
		lblSelectUser = new JLabel("Select user:");
		newUserPanel.add(lblSelectUser, "cell 0 5,aligny top");
		
		userList = new JList();
		userList.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		newUserPanel.add(userList, "cell 1 5,grow");
		
		deleteUserButton = new JButton("Delete user");
		newUserPanel.add(deleteUserButton, "flowx,cell 0 6,growx");
		
		resetUserButton = new JButton("Reset password");
		newUserPanel.add(resetUserButton, "flowx,cell 1 6");
		
		cancelAccountButton = new JButton("Back ");
		newUserPanel.add(cancelAccountButton, "cell 1 6");
	}
	
	private void setupEmployee()	{
		//main panel
		mainPanel = new JPanel();
		mainPanel.setBorder(BorderFactory.createTitledBorder("Main page"));
		panelStack.add(mainPanel, "main");
		mainPanel.setLayout(new MigLayout("", "[grow][grow]", "[][][grow][grow]"));
		
		newMeetingButton = new JButton("New meeting");
		mainPanel.add(newMeetingButton, "flowx,cell 0 1 2 1");
		
		editMeetingsButton = new JButton("Edit meetings");
		mainPanel.add(editMeetingsButton, "cell 0 1 2 1");
		
		editProfileButton = new JButton("Edit Profile");
		mainPanel.add(editProfileButton, "cell 0 1 2 1");
		
		logOutButton = new JButton("Log out");
		mainPanel.add(logOutButton, "cell 1 1 2 1,alignx trailing");
		
		lblYourMeetings = new JLabel("Your meetings:");
		mainPanel.add(lblYourMeetings, "cell 0 2");
		
		meetingTable = new JTable(meetingData, meetingColumns);
		meetingTable.setFillsViewportHeight(true);
		meetingTable.setEnabled(false);
		meetingTable.setGridColor(Color.BLACK);
		JScrollPane scrollPane = new JScrollPane(meetingTable);
		mainPanel.add(scrollPane, "cell 0 3 2 1,grow");
		
		panel_2 = new JPanel();
		panel_2.setBorder(BorderFactory.createTitledBorder("Admin panel:"));
		mainPanel.add(panel_2, "cell 0 4 2 1,grow");
		panel_2.setLayout(new MigLayout("", "[62px][79px][83px]", "[][23px][]"));
		
		createUserButton = new JButton("Edit users");
		panel_2.add(createUserButton, "cell 0 0,alignx left,aligny top");
		
		addRoomButton = new JButton("Edit rooms");
		panel_2.add(addRoomButton, "cell 1 0,alignx left,aligny top");
		
		//new meeting panel
		createMeetingPanel = new JPanel(new MigLayout("", "[][grow]", "[grow][15.00][][][]"));
		createMeetingPanel.setBorder(BorderFactory.createTitledBorder("Create New Meeting"));
	
		panelStack.add(createMeetingPanel, "new");
		
		meetingName = new JLabel("Select invited members:");
		createMeetingPanel.add(meetingName, "flowx,cell 0 0,alignx right,aligny top");
		
		employeeList = new JList(teamArray);
		employeeList.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		createMeetingPanel.add(employeeList, "cell 1 0,grow");
		
		String [] timesOfDay = {"Morning", "Afternoon", "Evening"};
		
		lblDate_1 = new JLabel("Date:");
		createMeetingPanel.add(lblDate_1, "cell 0 1,alignx right");
		
		calendar = new CalendarComponent();
		createMeetingPanel.add(calendar, "cell 1 1,alignx right");
		
		lblDate = new JLabel("Time of day: ");
		createMeetingPanel.add(lblDate, "cell 0 2,alignx right,aligny top");
		timeComboBox = new JComboBox(timesOfDay);
		createMeetingPanel.add(timeComboBox, "cell 1 2,growx,aligny top");
		
		pickMeetingButton = new JButton("Look for times");
		createMeetingPanel.add(pickMeetingButton, "cell 0 3,growx");
		
		cancelCreateMeetingButton = new JButton("Back");
		createMeetingPanel.add(cancelCreateMeetingButton, "cell 0 4,growx,aligny top");
		
		lblshiftToSelect = new JLabel("(shift to select multiple members) ");
		createMeetingPanel.add(lblshiftToSelect, "cell 0 0,alignx right,aligny top");
		
		pickMeetingPanel = new JPanel();
		panelStack.add(pickMeetingPanel, "pick");
		pickMeetingPanel.setLayout(new MigLayout("", "[grow]", "[][grow][]"));
		
		lblNewLabel = new JLabel("Possible meeting times and rooms:");
		pickMeetingPanel.add(lblNewLabel, "cell 0 0");
		

		String[][] rooms = {{"Executive room 1", "11/12/15", "10:00 AM"}, {"Executive room 2", "05/30/12", "4:00 AM"}};
		possibleMeetingTable = new JTable(rooms, possibleRoomColumns);
		possibleMeetingTable.setFillsViewportHeight(true);
		possibleMeetingTable.setGridColor(Color.BLACK);
		pickMeetingPanel.add(new JScrollPane(possibleMeetingTable), "cell 0 1,grow");
		
		confirmMeetingButton = new JButton("Schedule meeting");
		pickMeetingPanel.add(confirmMeetingButton, "flowx,cell 0 2");
		
		cancelPickMeetingButton = new JButton("Cancel");
		pickMeetingPanel.add(cancelPickMeetingButton, "cell 0 2");
		
		//edit profile panel
		editProfilePanel = new JPanel(new MigLayout("", "[][grow][][]", "[][][][][][][][][][][][][]"));
		editProfilePanel.setBorder(BorderFactory.createTitledBorder("Edit Profile"));
		
		lblName = new JLabel("Name");
		editProfilePanel.add(lblName, "cell 0 0,alignx trailing");
		
		nameTextField = new JTextField();
		editProfilePanel.add(nameTextField, "cell 1 0,growx");
		nameTextField.setColumns(10);
		
		lblNewLabel_2 = new JLabel("Phone number");
		editProfilePanel.add(lblNewLabel_2, "cell 0 1,alignx trailing");
		
		phoneTextField = new JTextField("1234567");
		editProfilePanel.add(phoneTextField, "cell 1 1,growx");
		phoneTextField.setColumns(10);
		
		lblNewLabel_3 = new JLabel("Email");
		editProfilePanel.add(lblNewLabel_3, "cell 0 2,alignx trailing,aligny center");
		
		emailTextField = new JTextField("email@email.com");
		editProfilePanel.add(emailTextField, "cell 1 2,growx");
		emailTextField.setColumns(10);
		
		lblAvailableTimes = new JLabel("Available times of day: ");
		editProfilePanel.add(lblAvailableTimes, "cell 0 3");
		
		lblAvailableTimes_1 = new JLabel("Visible to others? ");
		editProfilePanel.add(lblAvailableTimes_1, "cell 0 4,alignx right");
		
		visibleY = new JRadioButton("Yes");
		buttonGroup.add(visibleY);
		editProfilePanel.add(visibleY, "flowx,cell 1 4");
		visibleY.setSelected(true);
		
		visibleN = new JRadioButton("No");
		buttonGroup.add(visibleN);
		editProfilePanel.add(visibleN, "cell 1 4");
		
		saveChangesButton = new JButton("Save changes");
		editProfilePanel.add(saveChangesButton, "cell 0 5,growx");
		
		chckbxM = new JCheckBox("Morning");
		editProfilePanel.add(chckbxM, "flowx,cell 1 3");
		panelStack.add(editProfilePanel, "profile");
		
		chckbxA = new JCheckBox("Afternoon");
		editProfilePanel.add(chckbxA, "cell 1 3");
		
		chckbxE = new JCheckBox("Evening");
		editProfilePanel.add(chckbxE, "cell 1 3");
		
		lblChangePassword = new JLabel("Change password ");
		editProfilePanel.add(lblChangePassword, "cell 0 7,alignx left");
		
		lblNewPassword = new JLabel("New password: ");
		editProfilePanel.add(lblNewPassword, "cell 0 8,alignx trailing");
		
		changePasswordField = new JTextField();
		editProfilePanel.add(changePasswordField, "cell 1 8,growx");
		changePasswordField.setColumns(10);
		
		lblConfirmPassword = new JLabel("Confirm password: ");
		editProfilePanel.add(lblConfirmPassword, "cell 0 9,alignx trailing");
		
		confirmChangeField = new JTextField();
		editProfilePanel.add(confirmChangeField, "cell 1 9,growx");
		confirmChangeField.setColumns(10);
		
		changePasswordButton = new JButton("Change password");
		editProfilePanel.add(changePasswordButton, "cell 0 10");
		cancelProfileButton = new JButton("Back");
		editProfilePanel.add(cancelProfileButton, "flowx,cell 0 11,growx,aligny top");
		
		
	}
	
	private void setupAdmin()	{

		adAdd = new JPanel();
		panelStack.add(adAdd, "add");
		adAdd.setLayout(new MigLayout("", "[][][grow][grow]", "[][][][][][][][][]"));
		
		lblAddRoom = new JLabel("Add room:");
		adAdd.add(lblAddRoom, "cell 0 0");
		
		newRoomNameField = new JTextField();
		adAdd.add(newRoomNameField, "cell 1 1,growx");
		
		newRoomCapField = new JTextField();
		adAdd.add(newRoomCapField, "cell 1 2,growx");
		newRoomCapField.setColumns(10);
		
		confirmRoomButton = new JButton("Add room");
		confirmRoomButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		adAdd.add(confirmRoomButton, "flowx,cell 0 3 2 1,growx");
		
		lblDeleteRoom = new JLabel("Delete room:");
		adAdd.add(lblDeleteRoom, "cell 0 4");
		
		roomTable = new JTable();
		roomTable.setModel(new DefaultTableModel(roomData, roomColumns));
		roomTable.setFillsViewportHeight(true);
		roomTable.setGridColor(Color.BLACK);
		adAdd.add(new JScrollPane(roomTable), "cell 0 5 4 1,grow");
		
		
		
		lblName_1 = new JLabel("Name");
		adAdd.add(lblName_1, "cell 0 1,alignx trailing");
		
		lblCapacity = new JLabel("Capacity");
		adAdd.add(lblCapacity, "cell 0 2,alignx trailing");
		
		deleteRoomButton = new JButton("Delete room");
		adAdd.add(deleteRoomButton, "flowx,cell 0 7,growx");
		
		roomCancelButton = new JButton("Cancel");
		adAdd.add(roomCancelButton, "cell 1 7,growx");
		
		editMeetingsPanel = new JPanel();
		panelStack.add(editMeetingsPanel, "edit");
		editMeetingsPanel.setLayout(new MigLayout("", "[10px][452px,grow]", "[][427px][][]"));
		
		lblSelectMeetingTo = new JLabel("Select meeting to edit:");
		editMeetingsPanel.add(lblSelectMeetingTo, "cell 1 0");


		panel_1 = new JPanel();
		editMeetingsPanel.add(panel_1, "cell 0 1,alignx left,aligny center");
		
		editMeetingsTable = new JTable();
		editMeetingsTable.setModel(meetingTable.getModel());
		editMeetingsTable.setFillsViewportHeight(true);
		editMeetingsTable.setGridColor(Color.BLACK);
		editMeetingsPanel.add(new JScrollPane(editMeetingsTable), "cell 1 1,grow");
		
		editMeetingButton = new JButton("Edit meeting");
		editMeetingsPanel.add(editMeetingButton, "flowx,cell 1 3");
		
		deleteMeetingButton = new JButton("Delete meeting");
		editMeetingsPanel.add(deleteMeetingButton, "cell 1 3");
		
		backButton = new JButton("Back");
		editMeetingsPanel.add(backButton, "cell 1 3");
		
		//new meeting panel
		meetingEditorPanel = new JPanel(new MigLayout("", "[][grow]", "[grow][15.00][][][]"));
		meetingEditorPanel.setBorder(BorderFactory.createTitledBorder("Meeting Editor"));
	
		panelStack.add(meetingEditorPanel, "meetingeditor");
		
		meetingName = new JLabel("Edit invited members:");
		meetingEditorPanel.add(meetingName, "flowx,cell 0 0,alignx right,aligny top");
		
		editedEmployeeList = new JList(teamArray);
		editedEmployeeList.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		meetingEditorPanel.add(editedEmployeeList, "cell 1 0,grow");
		
		String [] timesOfDay = {"Morning", "Afternoon", "Evening"};
		
		lblDate_1 = new JLabel("Change date:");
		meetingEditorPanel.add(lblDate_1, "cell 0 1,alignx right");
		
		editedCalendar = new CalendarComponent();
		meetingEditorPanel.add(editedCalendar, "cell 1 1,alignx right");
		
		lblDate = new JLabel("Change time of day: ");
		meetingEditorPanel.add(lblDate, "cell 0 2,alignx right,aligny top");
		editedTimeComboBox = new JComboBox(timesOfDay);
		meetingEditorPanel.add(editedTimeComboBox, "cell 1 2,growx,aligny top");
		
		pickEditedMeetingButton = new JButton("Look for new times");
		meetingEditorPanel.add(pickEditedMeetingButton, "cell 0 3,growx");
		
		cancelEditMeetingButton = new JButton("Back");
		meetingEditorPanel.add(cancelEditMeetingButton, "cell 0 4,growx,aligny top");
		
		lblshiftToSelect = new JLabel("(shift to select multiple members) ");
		meetingEditorPanel.add(lblshiftToSelect, "cell 0 0,alignx right,aligny top");
		
		pickEditPanel = new JPanel();
		panelStack.add(pickEditPanel, "pickedit");
		pickEditPanel.setLayout(new MigLayout("", "[grow]", "[][grow][]"));
		
		lblNewLabel = new JLabel("Possible meeting times and rooms:");
		pickEditPanel.add(lblNewLabel, "cell 0 0");
		

		String[][] rooms = {{"Executive room 1", "11/12/15", "10:00 AM"}, {"Executive room 2", "05/30/12", "4:00 AM"}};
		possibleEditedMeetingTable = new JTable(rooms, possibleRoomColumns);
		possibleEditedMeetingTable.setFillsViewportHeight(true);
		possibleEditedMeetingTable.setGridColor(Color.BLACK);
		pickEditPanel.add(new JScrollPane(possibleEditedMeetingTable), "cell 0 1,grow");
		
		confirmEditedMeetingButton = new JButton("Change meeting");
		confirmEditedMeetingButton.setActionCommand("Change meeting");
		pickEditPanel.add(confirmEditedMeetingButton, "flowx,cell 0 2");
		
		cancelPickEdMeetingButton = new JButton("Cancel");
		pickEditPanel.add(cancelPickEdMeetingButton, "cell 0 2");
	}
	
	public void setupMenu()	{
		//setup menu bar
		menuBar = new JMenuBar();
		getFrame().setJMenuBar(menuBar);
		
		menu = new JMenu("File");
		menuBar.add(menu);
		
		frame.pack();
	}

	public String getUsername() {
		return usernameField.getText();
	}

	public String getPassword() {
		return passwordField.getText();
	}
	
	public String getNewUsername()	{
		return makeNameField.getText();
	}
	
	public String getNewPassword()	{
		return makePassField.getText();
	}
	
	public String getConfirmPassword()	{
		return confirmPassField.getText();
	}
	
	public boolean getAdmin()	{
		return adminCheckBox.isSelected();
	}
	public String getNewRoomName() {
		return newRoomNameField.getText();
	}

	public String getNewRoomCap() {
		return newRoomCapField.getText();
	}

	public JFrame getFrame() {
		return frame;
	}

	public void setFrame(JFrame frame) {	
		this.frame = frame;
	}
	
	
	
	private void addSeparator(JPanel panel, String text)	{
		JLabel l = createLabel(text);
		l.setForeground(LABEL_COLOR);
		panel.add(l, "cell 0 0,aligny center,gapbottom 1");
		panel.add(new JSeparator(), "cell 0 0,growx,gapx rel");
	}
	
	private JLabel createLabel(String text)	{
		return createLabel(text, SwingConstants.LEADING);
	}
	private JLabel createLabel(String text, int align)	{
		final JLabel b = new JLabel(text, align);
		return b;
	}
	public void addLoginListener(ActionListener listener)	{
		usernameField.setActionCommand("Sign-in");
		passwordField.setActionCommand("Sign-in");
		usernameField.addActionListener(listener);
		passwordField.addActionListener(listener);
		signInButton.addActionListener(listener);
		createUserButton.addActionListener(listener);
		createAccountButton.addActionListener(listener);
		logOutButton.addActionListener(listener);
	}
	
	public void addEmployeeListener(ActionListener listener)	{
		changePasswordButton.addActionListener(listener);
		resetUserButton.addActionListener(listener);
		deleteUserButton.addActionListener(listener);
		deleteRoomButton.addActionListener(listener);
		newMeetingButton.addActionListener(listener);
		editProfileButton.addActionListener(listener);
		editMeetingsButton.addActionListener(listener);
		pickEditedMeetingButton.addActionListener(listener);
		pickMeetingButton.addActionListener(listener);
		confirmMeetingButton.addActionListener(listener);
		confirmEditedMeetingButton.addActionListener(listener);
		cancelPickEdMeetingButton.setActionCommand("Employee main");
		cancelPickEdMeetingButton.addActionListener(listener);
		cancelPickMeetingButton.setActionCommand("Employee main");
		cancelPickMeetingButton.addActionListener(listener);
		cancelCreateMeetingButton.setActionCommand("Employee main");
		cancelCreateMeetingButton.addActionListener(listener);
		cancelEditMeetingButton.setActionCommand("Employee main");
		cancelEditMeetingButton.addActionListener(listener);
		saveChangesButton.addActionListener(listener);
		cancelProfileButton.setActionCommand("Employee main");
		cancelProfileButton.addActionListener(listener);
		addRoomButton.addActionListener(listener);
		confirmRoomButton.addActionListener(listener);
		cancelAccountButton.setActionCommand("Employee main");
		cancelAccountButton.addActionListener(listener);
		roomCancelButton.setActionCommand("Employee main");
		roomCancelButton.addActionListener(listener);
		backButton.setActionCommand("Employee main");
		backButton.addActionListener(listener);
		deleteMeetingButton.addActionListener(listener);
		editMeetingButton.addActionListener(listener);
		
	}
	public void updateRooms(String[][] roomData) {
		this.roomData = roomData;
		roomTable.setModel(new DefaultTableModel(roomData, roomColumns));
	}

	public void updateProfile(String[] details, boolean[] booleans) {
		nameTextField.setText(details[0]);
		phoneTextField.setText(details[1]);
		emailTextField.setText(details[2]);
		chckbxM.setSelected(booleans[1]);
		chckbxA.setSelected(booleans[2]);
		chckbxE.setSelected(booleans[3]);
		if(booleans[0])
			visibleY.setSelected(true);
		else
			visibleN.setSelected(true);
	}

	public String getPhone() {
		return phoneTextField.getText();
	}
	
	public String getEmail()	{
		return emailTextField.getText();
	}

	public void updateTeam(ArrayList<String> team) {
		DefaultListModel<String> def = new DefaultListModel<String>();
		if (team == null) return;
		while(!team.isEmpty())	{
			def.addElement(team.remove(0));
		}
		employeeList.setModel(def);
		editedEmployeeList.setModel(def);
		userList.setModel(def);
	}

	public String getNameField() {
		return nameTextField.getText();
	}

	public void updateMeetings(String[][] meetingData, String[][] ownedMeetingData) {
		this.meetingData = meetingData;
		meetingTable.setModel(new MyTableModel(meetingData, meetingColumns));
		editMeetingsTable.setModel(new MyTableModel(ownedMeetingData, meetingColumns));
	}

	public List<String> getSelectedEmployees() {
		return employeeList.getSelectedValuesList();
	}

	public int getTimeOfDay() {
		return timeComboBox.getSelectedIndex();
	}

	public Date getSearchDate() {
		return calendar.getCalendarSelectionModel().getLeadSelectionDate();
	}

	public void setPossibleRooms(String[][] foundRooms) {
		possibleMeetingTable.setModel(new DefaultTableModel(foundRooms, pickMeetingColumns));
	}

	public String getSelectedDate() {
		int meetingRow = possibleMeetingTable.getSelectedRow();
		String timestamp = (String) possibleMeetingTable.getValueAt(meetingRow, 1) +
				" " + (String) possibleMeetingTable.getValueAt(meetingRow, 2);
		return timestamp;
	}

	public String getSelectedRoom() {
		int meetingRow = possibleMeetingTable.getSelectedRow();
		return (String) possibleMeetingTable.getValueAt(meetingRow, 0);
	}

	public boolean[] getBooleans() {
		boolean[] booleans = new boolean[4];
		booleans[0] = visibleY.isSelected();
		booleans[1] = chckbxM.isSelected();
		booleans[2] = chckbxA.isSelected();
		booleans[3] = chckbxE.isSelected();
		return booleans;
		
	}
	
	private JFrame frame;
	private JLabel lblUsername;
	private JTextField usernameField;

	private CardLayout stack;
	private JPanel panelStack, loginPanel, mainPanel;
	private JLabel lblNewLabel_1;
	private JPasswordField passwordField;
	private JButton signInButton;
	private JMenuBar menuBar;
	private JMenu menu;
	private JButton newMeetingButton;
	private JButton editProfileButton;
	private JTable meetingTable;
	private JPanel createMeetingPanel;
	private JButton cancelCreateMeetingButton, cancelProfileButton;
	private JComponent editProfilePanel;
	private JButton pickMeetingButton;
	private JButton pickEditedMeetingButton;
	private JSpinner spinner1;
	private JButton saveChangesButton;
	private JTable possibleEditedMeetingTable;
	private JPanel adAdd;
	private JButton confirmRoomButton;
	private JButton roomCancelButton;
	static final Color LABEL_COLOR = new Color(0, 70, 213);
	private JLabel meetingName;
	private JLabel lblDate;
	private JList<String> employeeList;
	private JList<String> editedEmployeeList;
	private JComboBox<String> timeComboBox;
	private JComboBox<String> editedTimeComboBox;
	private JPanel pickMeetingPanel;
	private JButton confirmMeetingButton;
	private JLabel lblNewLabel;
	private JTable table_1;
	private JButton cancelPickMeetingButton;
	private JLabel lblNewLabel_2;
	private JLabel lblNewLabel_3;
	private JTextField phoneTextField;
	private JTextField emailTextField;
	private JLabel lblAvailableTimes;
	private JCheckBox chckbxM;
	private JCheckBox chckbxA;
	private JCheckBox chckbxE;
	private JLabel lblAvailableTimes_1;
	private JSpinner spinner;
	private JSpinner spinner_1;
	private JButton btnSaveChanges;
	private JSeparator separator_2;
	private JButton btnLookForTime;
	private JButton logOutButton;
	private JTable roomTable;
	private JPanel newUserPanel;
	private JLabel lblNewLabel_4;
	private JTextField makeNameField;
	private JLabel lblNewLabel_5;
	private JPasswordField makePassField;
	private JButton createAccountButton;
	private JLabel lblNewLabel_6;
	private JPasswordField confirmPassField;
	private JButton cancelAccountButton;
	private JLabel lblName_1;
	private JTextField newRoomNameField;
	private JLabel lblCapacity;
	private JTextField newRoomCapField;
	private JLabel lblSetAdmin;
	private JCheckBox adminCheckBox;
	private JLabel lblshiftToSelect;
	private String[][] roomData = {{"Test", "if", "changed"}};
	private String[][] meetingData = {{"A","A","A","A"}};
	String[] roomColumns = {"Name", "Capacity"};
	String[] pickMeetingColumns = {"Name", "Date", "Available Times"};
	String[] meetingColumns = {"Time", "People", "Room", "Meeting ID"};
	String[] possibleRoomColumns = {"Room", "Date", "Time"};
	String[] teamArray = new String[1];
	private JButton editMeetingsButton;
	private JLabel lblDate_1;
	private JLabel lblName;
	private JTextField nameTextField;
	private CalendarComponent calendar;
	private CalendarComponent editedCalendar;
	private JRadioButton visibleY;
	private JRadioButton visibleN;
	private final ButtonGroup buttonGroup = new ButtonGroup();
	private JPanel editMeetingsPanel;
	private JButton addRoomButton;
	private JButton createUserButton;
	private JTable editMeetingsTable;
	private JPanel panel_1;
	private JButton deleteMeetingButton;
	private JButton editMeetingButton;
	private JLabel lblSelectMeetingTo;
	private JButton backButton;
	private JLabel lblAddRoom;
	private JLabel lblDeleteRoom;
	private JButton deleteRoomButton;
	private JLabel lblSelectUser;
	private JButton deleteUserButton;
	private JButton resetUserButton;
	private JPanel panel_2;
	private JLabel lblYourMeetings;
	private JPanel meetingEditorPanel;
	private JLabel lblSelectInvitedMembers;
	private JList list;
	private JLabel lblChangePassword;
	private JTextField changePasswordField;
	private JLabel lblNewPassword;
	private JLabel lblConfirmPassword;
	private JTextField confirmChangeField;
	private JButton changePasswordButton;
	
	public class MyTableModel extends DefaultTableModel	{
		public MyTableModel(String[][] meetingData, String[] meetingColumns) {
			super(meetingData, meetingColumns);
		}

		public boolean isCellEditable(int row, int column)	{
			return false;
		}
	}

	public String getSelectedUsername() {
		return (String) userList.getSelectedValue();
	}

	public int getSelectedMeetingId() {
		int meetingToEdit = editMeetingsTable.getSelectedRow();
		if(meetingToEdit == -1)	{
			return -1;
		}
		return Integer.parseInt((String) editMeetingsTable.getValueAt(meetingToEdit, 3));
	}

	public void removeMeeting(int selectedMeeting) {
		editMeetingsTable.remove(selectedMeeting);
	}

	public int getSelectedMeetingRow() {
		return editMeetingsTable.getSelectedRow();
	}

	public List<String> getEditedEmployees() {
		return editedEmployeeList.getSelectedValuesList();
	}

	public String getEditedDate() {
		int meetingRow = editMeetingsTable.getSelectedRow();
		String timestamp = (String) editMeetingsTable.getValueAt(meetingRow, 0);
		return timestamp;
	}

	public int getEditedTod() {
		return editedTimeComboBox.getSelectedIndex();
	}

	public void setEditedPossibleRooms(String[][] foundEditedRooms) {
		possibleEditedMeetingTable.setModel(new DefaultTableModel(foundEditedRooms, pickMeetingColumns));
	}

	public String[] getSelectedMeetingDetails() {
		int selectedRow = editMeetingsTable.getSelectedRow();
		String[] details = new String[4];
		details[0] = (String) editMeetingsTable.getValueAt(selectedRow, 0);
		details[1] = (String) editMeetingsTable.getValueAt(selectedRow, 1);
		details[2] = (String) editMeetingsTable.getValueAt(selectedRow, 2);
		details[3] = (String) editMeetingsTable.getValueAt(selectedRow, 3);
		return details;
	}

	public void setEditedFields(String[] selectedMeetingDetails) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		editedCalendar.setDate(sdf.parse(selectedMeetingDetails[0]));
		Calendar cal = new GregorianCalendar();
		cal.setTime(editedCalendar.getDate());
		String[] parsedPeople = selectedMeetingDetails[1].split(",");
		int size = parsedPeople.length;
		int[] indices = new int[size-1];
		int index = 0;
		DefaultListModel dlm = (DefaultListModel) editedEmployeeList.getModel();
		for(String person : parsedPeople)	{
			for(int i = 0; i < dlm.getSize(); i++)	{
				System.out.println(person + " compared to " + dlm.getElementAt(i));
				System.out.println(index);
				if(person.equals(dlm.getElementAt(i)))	{
					indices[index] = i;
					index++;
				}
			}
		}
		editedEmployeeList.setSelectedIndices(indices);
		int threshold = cal.getTime().getHours();
		int comboSelect = 0;
		if(threshold > 14)	{
			comboSelect = 2;
		}
		else if(threshold > 11)	{
			comboSelect = 1;
		}
		else comboSelect = 0;
		editedTimeComboBox.setSelectedIndex(comboSelect);
	}

	public String getEditedRoom() {
		int meetingRow = possibleEditedMeetingTable.getSelectedRow();
		return (String) possibleEditedMeetingTable.getValueAt(meetingRow, 0);
	}

	public String getChangePassword() {
		return changePasswordField.getText();
	}

	public String getConfirmChange() {
		return confirmChangeField.getText();
	}

	public void showPrompt(String notice) {
		JOptionPane.showMessageDialog(frame, notice);
		
	}

	public String getDeleteRoom() {
		int roomRow = roomTable.getSelectedRow();
		return (String) roomTable.getValueAt(roomRow, 0);
	}
}
