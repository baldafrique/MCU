
public class Memory  {
	private short memory[];
			
	public Memory() {
	}
	
	public short load(short mar) {
		return memory[mar];
	}
	
	public void store(short mar, short mbr) {
		memory[mar] = mbr;
	}

	public int allocateMemory(int size) {
		this.memory = new short[size];
		return 0;
	}
}
