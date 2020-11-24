package cs131.pa2.filter.concurrent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
/**
 * The filter for ls command
 * @author cs131a
 *
 */
public class LsFilter extends ConcurrentFilter{
	/**
	 * The counter of how many contents are in the directory
	 */
	int counter;
	/**
	 * The folder of the current working directory 
	 */
	File folder;
	/**
	 * The list of files within the current working directory
	 */
	File[] flist;
	
	
	/**
	 * If this filter is done
	 */
	private boolean isDone;
	
	/**
	 * The constructor of the ls filter, no parameters.
	 */
	public LsFilter() {
		super();
		NAME = "LS";
		isDone = false;
		counter = 0;
		folder = new File(ConcurrentREPL.currentWorkingDirectory);
		flist = folder.listFiles();
	}
	
	@Override
	public void process() {
		while(counter < flist.length) {
			try {
				if (output!=null) {
					output.put(processLine(""));
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		isDone = true;
	}
	
	public boolean isDone() {
		return isDone;
	}
	
	@Override
	public String processLine(String line) {
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
		return flist[counter++].getName();
	}
}
