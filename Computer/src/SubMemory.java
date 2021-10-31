
public class SubMemory {
	private short memory[];
			
	public SubMemory() {
		this.memory = new short[512];
	}
	
	public short load(short mar) {
		return memory[mar];
	}
	
	public void store(short mar, short mbr) {
		memory[mar] = mbr;
	}
}
