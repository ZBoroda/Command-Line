package cs131.pa2.filter.concurrent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import cs131.pa2.filter.Message;

/**
* This filter output the entirety of one or more files to the output message queue
* Known Bugs: "None”
*
* @author Zachary Boroda
* zacharyboroda@brandeis.edu
* September 10, 2020
* COSI 131A PA1
*/
public class CatFilter extends ConcurrentFilter {
	/**
	 * The scanner scanning the file
	 */
	private Scanner reader;
	
	/**
	 * Whether or not this program is done
	 */
	private boolean isDone;
	
	/**
	 * The constructor of the cat filter
	 * @param line the parameters for cat
	 * @throws Exception throws exception when there is an error with the given parameters,
	 * 			or when the file is not found
	 */
	public CatFilter(String line) throws Exception {
		super();
		NAME = "CAT";
		//parsing the cat options
		String[] args = line.split(" ");
		String filename;
		//obviously incorrect number of parameters
		if(args.length == 1) {
			System.out.printf(Message.REQUIRES_PARAMETER.toString(), line);
			throw new Exception();
		}
		filename = args[1];
		try {
			reader = new Scanner(new File(ConcurrentREPL.currentWorkingDirectory+"/"+filename));
		} catch (FileNotFoundException e) {
			System.out.printf(Message.FILE_NOT_FOUND.toString(), line);
			throw new Exception();
		}
		isDone = false;
	}
	
	/**
	 * Overrides the process() method of ConcurrentFilter to
	 * check whether the file has more lines (through the reader object)
	 * and calls processLine() for each line until the limit (in variable total) is reached
	 */
	public void process() {
		while(true) {
			String processedLine = processLine("");
			if(processedLine == null) {
				break;
			}
			try {
				if (output!=null) {
					FileWriter fw = null;
					if(ConcurrentREPL.DEBUG) {
						try {
							fw = new FileWriter(new File("TESTRUNNINGCONCURRENTLYLOG"), true);
							fw.append(NAME +"Is running \n");
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						try {
							fw.flush();
							fw.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					output.put(processedLine);
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		reader.close();
		isDone = true;
	}
	/**
	 * Processes each line by reading from the reader object and adding the result to the output queue
	 * @param line the line to be processed
	 */
	public String processLine(String line) {
		if(reader.hasNextLine()) {
			return reader.nextLine();
		} else {
			return null;
		}
	}
	
	public boolean isDone() {
		return isDone;
	}

	/**
	 * Closes the input file reader whenever the filter is created but not executed properly
	 * (for example due to error in linking filters)
	 */
	public void terminate() {
		reader.close();
		isDone = true;
	}
}
