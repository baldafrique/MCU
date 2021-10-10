
public class Memory {

	public short process[] = { 
			0x0000, // start address
			0x0010, // header memory size
			0x0016, // code segment size
			0x0004, // data segment size
			// code segment
			// setac 3
			0x0803,
			// sta A
			0x0200,
			// setac 4
			0x0803,
			// STA B
			0x0202,

			// B = A + B
			// LDA A
			0x0100,

			// ADD B
			0x0302,

			// STA B
			0x0202,

			// halt
			0x0000,

			// data segment
			0x0000, // A
			0x0000 // B
	};

	public short load(short mar) {
		short data = process[mar];
		return data;
	}

	public void store(short mar, short mbr) {
		process[mar] = mbr;
	}

}
