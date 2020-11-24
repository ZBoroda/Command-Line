package cs131.pa2.filter.concurrent;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

/**
* This class works to manage the different command threads running both in foreground and backgroundd
* Known Bugs: "None”
*
* @author Zachary Boroda
* zacharyboroda@brandeis.edu
* September 10, 2020
* COSI 131A PA1
*/
public class ConcurrentCommandManager {
	
	/**
	 * The command running in foreground
	 */
	private Thread commandForeground;
	
	/**
	 * The default max size of the hash map containing the filters running in background
	 */
	private static final int MAX_SIZE = 10;
	
	/**
	 * Whether or not the command manager is ready to accept a new command to manage
	 */
	private boolean ready;
	
	/**
	 * The cnt of total commands that have been created to run in background
	 */
	private int cnt;
	
	/**
	 * The hashmap containing the background command threads mapped to by the number they were from the first thread created
	 */
	private HashMap<Integer, ConcurrentCommand> commandsBackground;
	
	/**
	 * Creates a new command manager with the default size for the hashmap
	 */
	public ConcurrentCommandManager() {
		this(MAX_SIZE);
	}
	
	/**
	 * Creates a new command manager with maxSize as the size of the  original hashmap 
	 * @param maxSize the original size of this hash map
	 */
	public ConcurrentCommandManager(int maxSize) {
		cnt = 0;
		ready = true;
		commandsBackground = new HashMap<Integer,ConcurrentCommand>(maxSize);
		commandForeground = new Thread();
	}
	
	/**
	 * Creates a new command to be managed 
	 * @param filterlist the list of filters in this command
	 * @param name the string defining this command
	 * @param background whether or not this command will run in background
	 */
	public void createCommand (ConcurrentFilter filterlist, String name, boolean background) {
		ready = false;
		if (background) {
			cnt++;
			ConcurrentCommand commandThread = new ConcurrentCommand(filterlist, cnt +". " +name +"&");
			commandsBackground.put(cnt,commandThread);
			commandThread.start();
		} else if (!commandForeground.isAlive()){
			ConcurrentCommand commandThread = new ConcurrentCommand(filterlist, name);
			commandForeground = commandThread;
			commandThread.start();
		}
		ready = true;
	}
	
	/**
	 * Returns the names of all currently running commands being managed
	 * @return the names of all currently running commands being managed
	 */
	public String getNames() {
		run();
		if (commandsBackground.isEmpty()) {
			return "";
		}
		String names = "";
		Iterator<Integer> keyIterator = commandsBackground.keySet().iterator();
		while (keyIterator.hasNext()) {
			int key = keyIterator.next();
			Thread command = commandsBackground.get(key);
			if (command.isAlive()) {
				names += "\t" +command.getName() +"\n";
			}
		}	
		if(commandForeground.isAlive()) {
			names += "\t" +commandForeground.getName();
		}
		return names;
	}
	
	/**
	 * Whether or not this command manager is ready to create a new command without error
	 * @return whether or not this command manager is ready to take input without error
	 */
	public boolean ready() {
		return (!commandForeground.isAlive() && ready);
	}
	
	/**
	 * Kills the command running in background with given number
	 * @param threadNum the number of the command to be killed
	 * @return true if the command was deleted false otherwise
	 */
	public boolean kill(int threadNum) {
		try {
		Thread command = commandsBackground.get(threadNum);
		((ConcurrentCommand) command).kill();
		commandsBackground.remove(threadNum);
		}catch(NullPointerException ex) {
			return false;
		}
		return true;
	}

	/**
	 * Goes through the hashmap and deletes all threads that are not alive
	 */
	public void run() {
		//while(true) {
			Iterator<Integer> keyIterator = commandsBackground.keySet().iterator();
			while (keyIterator.hasNext()) {
				int key = keyIterator.next();
				Thread command = commandsBackground.get(key);
				if (!command.isAlive()) {
					commandsBackground.remove(key);
				}
			}
	//	}
	}
	
	
	/**
	* This class functions as a thread managing a linked list of filter threads 
	* Known Bugs: "None”
	*
	* @author Zachary Boroda
	* zacharyboroda@brandeis.edu
	* September 10, 2020
	* COSI 131A PA1
	*/
	private class ConcurrentCommand extends Thread{
		
		/**
		 * The linked list of threads contained in this command
		 */
		private LinkedList <Thread> filterThreads;
		
		/**
		 * The first filter 
		 */
		private ConcurrentFilter first;
		
		/**
		 * Creates a new command with name, name and filters filterlist and generates threads for each filter
		 * @param filterlist the list of filters
		 * @param name the name of the command
		 */
		private ConcurrentCommand(ConcurrentFilter filterlist, String name) {
			super(name);
			first = filterlist;
			filterThreads = new LinkedList<Thread>();
			while(filterlist != null) { 
				filterThreads.add(new Thread(filterlist, filterlist.NAME));
				filterlist = (ConcurrentFilter) filterlist.getNext(); 
			}
		}
		
		/**
		 * Kills this command and all of the filters inside by interrupting each filter 
		 * and setting each filter's killed flag to true 
		 */
		public void kill() {
			try {
				Thread.currentThread().join(30);
				first.kill();
				FileWriter fw = new FileWriter(new File("TestStuffKillLog"));
				for(Thread filterThread: filterThreads) {
					if(ConcurrentREPL.DEBUG) {
						fw.append("filterThread " +filterThread.getName() +"killed" + "\n");
					}
					filterThread.interrupt();
				}
				if(ConcurrentREPL.DEBUG) {
					fw.flush();
					fw.close();
				}
				this.interrupt();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		public void run() {
			FileWriter fw = null;
			try {
				if(ConcurrentREPL.DEBUG) {
					fw = new FileWriter(new File("TestCreationLog"));
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			for(Thread filterThread: filterThreads) {
				filterThread.start();
				if(ConcurrentREPL.DEBUG) {
					try {
						fw.append("filterThread " +filterThread.getName() +"started" + "\n");
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				//Important to sleep here for a bit
			}
			if(ConcurrentREPL.DEBUG) {
				try {
					fw.flush();
					fw.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
			}
			}
			while (filterThreads.getLast().isAlive()) {
				if (Thread.currentThread().isInterrupted()) {
					kill();
				}
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					kill();
				}
			}
		}

	}

}
