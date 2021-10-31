import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Loader {

	private CPU cpu;
	private Memory memory;
	short startAddress;
	short currentAddress;
	short sizeHeader;
	short sizeCodeSegment; 
	short sizeDataSegment;
	
	public Loader(CPU cpu, Memory memory) {
		this.memory = memory;
		this.cpu = cpu;
	}
	
	private short decodeLine(String line) {
		short digit0, digit1, digit2, digit3;
		digit0 = (short) (Character.getNumericValue(line.charAt(0)) << 12);
		digit1 = (short) (Character.getNumericValue(line.charAt(1)) << 8);
		digit2 = (short) (Character.getNumericValue(line.charAt(2)) << 4);
		digit3 = (short) Character.getNumericValue(line.charAt(3));
		return (short) (digit0 + digit1 + digit2 + digit3);
	}
	
	public void loadHeader(Scanner scanner) {
		String lineDataSegmentSize = scanner.nextLine();
		String lineCodeSegmentSize = scanner.nextLine();
		this.sizeHeader = 4;
		this.sizeCodeSegment = decodeLine(lineCodeSegmentSize);
		this.sizeDataSegment = decodeLine(lineDataSegmentSize);
		this.startAddress = this.memory.allocateMemory(sizeHeader + sizeDataSegment + sizeCodeSegment);
		this.currentAddress = startAddress;
		this.memory.store(currentAddress++, sizeDataSegment);
		this.memory.store(currentAddress++, sizeCodeSegment);
		this.cpu.setPC((short) (startAddress + sizeHeader / 2));
		this.cpu.setSP((short) (startAddress + sizeHeader/2 + sizeCodeSegment/2));
	}
	
	public void loadBody(Scanner scanner) {
		while (scanner.hasNext()) {
			String line = scanner.nextLine();
			memory.store((short) (currentAddress++), decodeLine(line));
		}
	}
	
	public void load(String fileName) throws FileNotFoundException {
		Scanner scanner = new Scanner(new File("exe/" + fileName));
		this.loadHeader(scanner);
		this.loadBody(scanner);
		scanner.close();
	}
}
