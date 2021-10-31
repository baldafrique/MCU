
public class Enum {
	
	private enum numbers {
		one,
		two,
		three
	}

	public static void main(String[] args) {
		Enum.numbers[] data = numbers.values();
		System.out.println(data[0]);
		System.out.println(numbers.values()[0]);
	}

}
