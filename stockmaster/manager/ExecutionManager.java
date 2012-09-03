package stockmaster.manager;

/**
 * Execution Manager - Singleton
 * Responsible for executing trades
 * 
 * @author Qingwei Yang
 *
 */
public class ExecutionManager {

	private static ExecutionManager instance;
	
	private ExecutionManager() {
		
	}
	
	public static ExecutionManager getInstance() {
		if (instance == null)
			instance = new ExecutionManager();
		
		return instance;
	}
}
