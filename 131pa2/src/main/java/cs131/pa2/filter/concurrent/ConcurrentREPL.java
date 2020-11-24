package cs131.pa2.filter.concurrent;

import java.util.Scanner;

import cs131.pa2.filter.Message;

/**
 * The main implementation of the REPL loop (read-eval-print loop).
 * It reads commands from the user, parses them, executes them and displays the result.
 * @author cs131a
 *
 */
public class ConcurrentREPL {
	/**
	 * the path of the current working directory
	 */
	static String currentWorkingDirectory;
	
	/**
	 * Debug mode
	 */
	public final static boolean DEBUG = true;
	/**
	 * The main method that will execute the REPL loop
	 * @param args not used
	 */
	public static void main(String[] args){
		currentWorkingDirectory = System.getProperty("user.dir");
		Scanner s = new Scanner(System.in);
		System.out.print(Message.WELCOME);
		String command;
		ConcurrentCommandManager commandManager = new ConcurrentCommandManager();
		while(true) {
			//obtaining the command from the user
			System.out.print(Message.NEWCOMMAND);
			command = s.nextLine();
			if(command.equals("exit")) {
				break;
			}else if (command.trim().equals("repl_jobs")) {
				System.out.print(commandManager.getNames());
			}else if (command.split(" ")[0].equals("kill")){
				String[] killcommand = command.split(" ");
				if (killcommand.length < 2) {
					System.out.print(Message.REQUIRES_PARAMETER.with_parameter("kill"));
				}else {
					try {
						int kill = Integer.parseInt(killcommand[1]);
						if (!commandManager.kill(kill)) {
							System.out.print(Message.INVALID_PARAMETER.with_parameter(command));
						}
					} catch(NumberFormatException ex) {
						System.out.print(Message.INVALID_PARAMETER.with_parameter(command));
					}
				}
			}else if(!command.trim().equals("")) {
				boolean background = false;
				if(command.trim().endsWith("&")) {
					background = true;
					command = command.replace("&", "");
				}
				//building the filters list from the command
				ConcurrentFilter filterlist = ConcurrentCommandBuilder.createFiltersFromCommand(command);
				if (filterlist != null) {
					commandManager.createCommand(filterlist, command, background);
				}
				while(!commandManager.ready()){
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		s.close();
		System.out.print(Message.GOODBYE);
	}

}
