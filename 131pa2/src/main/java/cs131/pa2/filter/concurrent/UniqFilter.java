package cs131.pa2.filter.concurrent;

import java.util.HashSet;
/**
 * The filter for uniq command
 * @author cs131a
 *
 */
public class UniqFilter extends ConcurrentFilter{
	
	/**
	 * This set will record what strings are existing
	 */
	private HashSet<String> existingStringSet;
	//
	/**
	 * The constructor of the uniq filter
	 */
	public UniqFilter (){
		NAME = "UNIQ";
		existingStringSet = new HashSet<String> ();
	}

	/**
	 * Processes a line from the input queue and returns it if it is not found before
	 * @param line the line as got from the input queue
	 * @return the line if it was not found before, null otherwise 
	 */
	public String processLine(String line) {
		if(existingStringSet.contains(line)) {
			return null;
		}else {
			existingStringSet.add(line);
			return line;
		}
	}
}
