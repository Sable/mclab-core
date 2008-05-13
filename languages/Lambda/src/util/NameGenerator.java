package util;
import java.util.Set;

public class NameGenerator {
	private static final String BASE_NAME = "gen";

	private NameGenerator() {}

	public static String makeName(Set<String> badNames) {
		int i = 1;
		String name;
		do {
			name = BASE_NAME + (i++);
		} while (badNames.contains(name));
		return name;
	}
}
