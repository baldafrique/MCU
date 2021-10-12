import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Memory {

	short process[];
	
	public Memory() {
		this.process = new short[512];
	}
	
	public void loadProcess(String fileName) {
		Scanner scanner = null;
		try {
			scanner = new Scanner(new File("exe/" + fileName));
			short address = 0;
			while (scanner.hasNext()) {
				this.process[address] = scanner.nextShort(16);
				address++;
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public short load(short mar) {
		short data = process[mar];
		return data;
	}

	public void store(short mar, short mbr) {
		process[mar] = mbr;
	}

}
