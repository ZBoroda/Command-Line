package cs131.pa2.filter.concurrent;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * The filter for printing in the console
 * @author cs131a
 *
 */
public class PrintFilter extends ConcurrentFilter {
	/**
	 * Constructs new print filter
	 */
	public PrintFilter() {
		super();
		NAME = "PRINT";
	}
	
	public void process() {
		FileWriter fw = null;
		while(!isDone()) {
			try {
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
				processLine(input.take());
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public String processLine(String line) {
		System.out.println(line);
		return null;
	}
}
