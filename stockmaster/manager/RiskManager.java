package stockmaster.manager;

/**
 * Risk Manager - Singleton
 * Responsible for managing risks before any trade execution
 * 
 * @author Qingwei Yang
 *
 */
public class RiskManager {

	private static RiskManager instance;
	
	private RiskManager() {
		
	}
	
	public static RiskManager getInstance() {
		if (instance == null)
			instance = new RiskManager();
		
		return instance;
	}
	
	public void executeBuy(String stockCode, float price) {
		
	}
	
	public void executeSell(String stockCode, float price) {
		
	}
}
