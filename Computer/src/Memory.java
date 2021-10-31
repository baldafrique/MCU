
public class Memory  {
	private short memory[];
	
	public short load(short mar) { 
		return memory[mar];
	}
	
	public void store(short mar, short mbr) {
		memory[mar] = mbr;
	}

	public short allocateMemory(int size) {
		this.memory = new short[size];
		return 0;
	}
}
