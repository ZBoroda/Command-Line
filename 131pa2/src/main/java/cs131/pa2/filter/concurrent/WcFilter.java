package cs131.pa2.filter.concurrent;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * The filter for wc command
 * @author cs131a
 *
 */
public class WcFilter extends ConcurrentFilter {
	/**
	 * The count of lines found
	 */
	private int linecount;
	/**
	 * The count of words found
	 */
	private int wordcount;
	/**
	 * The count of characters found
	 */
	private int charcount;
	
	/**
	 * Constructs new wc filter
	 */
	public WcFilter() {
		super();
		NAME = "WC";
		linecount = 0;
		wordcount = 0;
		charcount = 0;
	}
	
	@Override
	public void process(){
		FileWriter fw = null;
		while(!isDone()) {
			String line;
			try {
				line = input.take();
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
				processLine(line);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Counts the number of lines, words and characters from the input queue
	 * @param line the line as got from the input queue
	 * @return the number of lines, words, and characters when finished, null otherwise
	 */
	public String processLine(String line) {
		//prints current result if ever passed a null
		if(line == null) {
			return linecount + " " + wordcount + " " + charcount;
		}
		linecount++;
		String[] wct = line.split(" ");
		wordcount += wct.length;
		String[] cct = line.split("|");
		charcount += cct.length;
		return null;
	}
	
	@Override
	public void run() {
		super.run();
		String finalLine = processLine(null);
		try {
			if(output!=null) {
				output.put(finalLine);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	 
}
