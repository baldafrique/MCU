import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Loader {

	private CPU cpu;
	private Memory memory;
	int startAddress;
	int sizeHeader;
	int sizeCodeSegment; 
	int sizeDataSegment;
	
	private short decodeLine(String line) {
		short digit0, digit1, digit2, digit3;
		digit0 = (short) (line.charAt(0) << 12);
		digit1 = (short) (line.charAt(1) << 8);
		digit2 = (short) (line.charAt(2) << 4);
		digit3 = (short) line.charAt(3);
		return (short) (digit0 + digit1 + digit2 + digit3);
	}
	
	public void loadHeader(Scanner scanner) {
		String lineDataSegmentSize = scanner.nextLine();
		String lineCodeSegmentSize = scanner.nextLine();
		this.sizeHeader = 
		this.sizeCodeSegment =
		this.sizeDataSegment = 
		this.startAddress = this.memory.allocateMemory(sizeHeader +sizeDataSegment + sizeCodeSegment);
		this.cpu.setPC(startAddress + sizeHeader);
		this.cpu.setSP(startAddress + sizeHeader + sizeCodeSegment);
	}
	
	public void loadBody(Scanner scanner) {
		while (scanner.hasNext()) {
			String line = scanner.nextLine();
			memory.store((short) (startAddress + sizeHeader), decodeLine(line));
		}
	}
	
	public void load(String fileName) {
		try {
			Scanner scanner = new Scanner(new File("exe/" + fileName));
			this.loadHeader(scanner);
			this.loadBody(scanner);
			scanner.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
