package Scheduler;

public class Scheduler {
	public static void main(String [] args) throws Exception	{
		View view = new View();
		Model model = new Model();
		Controller control = new Controller(view, model);
		view.getFrame().setVisible(true);
	}
}
