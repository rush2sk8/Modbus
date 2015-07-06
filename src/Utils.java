
public class Utils {

	public static float hexToFloat(String hex) {
		return Float.intBitsToFloat(new Long(Long.parseLong(hex,16)).intValue());
	}
	
	
}
