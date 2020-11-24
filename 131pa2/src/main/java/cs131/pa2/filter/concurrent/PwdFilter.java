package cs131.pa2.filter.concurrent;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * The filter for pwd command
 * @author cs131a
 *
 */
public class PwdFilter extends ConcurrentFilter {
	/**
	 * If the filter is done processing
	 */
	private boolean isDone;
	
	/**
	 * Constructs a new pwd filter
	 */
	public PwdFilter() {
		super();
		NAME = "PWD";
		isDone = false;
	}
	
	@Override
	public void process() {
		FileWriter fw = null;
		try {
			if(output!=null) {
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
				output.put(processLine(""));
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		isDone = true;
	}
	
	@Override
	public boolean isDone() {
		return isDone;
	}
	
	@Override
	public String processLine(String line) {
		return ConcurrentREPL.currentWorkingDirectory;
	}
}
