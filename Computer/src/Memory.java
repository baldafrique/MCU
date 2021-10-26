import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Memory {

	private short memory[];
	private Process currentProcess;
	short currentAddress;
	short process[];
	
	public Memory() {
		this.memory = new short[512];
		this.currentAddress = 0;
	}
	
	public class Process {
		static final short sizeHeader = 4;
		static final short indexPC = 0;
		static final short indexSP = 1;
		private short PC, SP;
		private short sizeData, sizeCode;
		
		public short getPC() { return this.PC; }
		
		public short getSP() { return this.SP; }
		
		private void loadHeader(Scanner scanner) {
			this.sizeData = scanner.nextShort(16);
			this.sizeCode = scanner.nextShort(16); 
			
			// make process header
			memory[indexPC] = (short) (currentAddress + sizeHeader/2);
			memory[indexSP] = (short) (currentAddress + sizeHeader/2 + this.sizeCode/2);
		}
		
		private void loadBody(Scanner scanner) {
			// code segment
			PC = memory[indexPC];
			currentAddress = 2;
			for (short i=0; i<sizeCode/2; i++) {
				memory[currentAddress] = scanner.nextShort(16);
				currentAddress++;
			}
			
			// data segment
			SP = memory[indexSP];
			currentAddress = (short) (currentAddress + this.sizeData/2);
		}
		public void loadProcess(String fileName) {
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
	
	public void loadProcess(String fileName) {
		this.currentProcess = new Process();
		this.currentProcess.loadProcess(fileName);
	}
	
	public short getPC() { return this.currentProcess.getPC(); }
	
	public short getSP() { return this.currentProcess.getSP(); }
	
	public short load(short mar) {
		short data = memory[mar];
		return data;
	}

	public void store(short mar, short mbr) {
		memory[mar] = mbr;
	}

}
