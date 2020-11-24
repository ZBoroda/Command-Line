package cs131.pa2.filter.concurrent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

import cs131.pa2.filter.Filter;

/**
 * An abstract class that extends the Filter and implements the basic functionality of all filters. Each filter should
 * extend this class and implement functionality that is specific for that filter.
 * @author cs131a
 *
 */
public abstract class ConcurrentFilter extends Filter implements Runnable {
	
	/**
	 * The filter name used for logging and debugging
	 */
	public String NAME;
	
	/**
	 * The input queue for this filter
	 */
	protected LinkedBlockingQueue<String> input;
	/**
	 * The output queue for this filter
	 */
	protected LinkedBlockingQueue<String> output;
	
	/**
	 * Whether or not this filter has been killed
	 */
	protected boolean killed = false;
	
	@Override
	public void setPrevFilter(Filter prevFilter) {
		prevFilter.setNextFilter(this);
	}
	
	@Override
	public void setNextFilter(Filter nextFilter) {
		if (nextFilter instanceof ConcurrentFilter){
			ConcurrentFilter concurrentNext = (ConcurrentFilter) nextFilter;
			this.next = concurrentNext;
			concurrentNext.prev = this;
			if (this.output == null){
				this.output = new LinkedBlockingQueue<String>();
			}
			concurrentNext.input = this.output;
		} else {
			throw new RuntimeException("Should not attempt to link dissimilar filter types.");
		}
	}
	/**
	 * Gets the next filter
	 * @return the next filter
	 */
	public Filter getNext() {
		return next;
	}
	/**
	 * processes the input queue and writes the result to the output queue
	 */
	public void process(){
		while (!(input.isEmpty() || killed)){
			String line;
			try {
				FileWriter fw = null;
				line = input.take();
				String processedLine = processLine(line);
				if (processedLine != null && output!=null){
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
	}
	
	@Override
	public boolean isDone() {
		return ((input.size() == 0) && (prev.isDone()) || killed);
	}
	
	/**
	 * Kills this filter and kills the next filter after this one
	 */
	protected void kill() {
		FileWriter fw = null;
		if(ConcurrentREPL.DEBUG) {
			try {
				fw = new FileWriter(new File("TestKillFlaglLog"), true);
				fw.append(NAME +"Turned flag killed \n");
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
		this.killed = true;
		output = null;
		if (next != null) {
			((ConcurrentFilter)next).kill();
		}
	}
	
	@Override
	public void run() {
		while(!this.isDone()) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				kill();
			}
			if(Thread.currentThread().isInterrupted()) {
				return;
			}
			if(this.killed) {
				kill();
			} else {
				this.process();
			}
		}
	}
	
	/**
	 * processes a line of text
	 * @param line the line being processed 
	 * @return the line processed 
	 */
	protected abstract String processLine(String line);
	
}
