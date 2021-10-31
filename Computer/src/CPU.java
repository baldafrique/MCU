import java.io.FileNotFoundException;

public class CPU {
	
	// declaration
	private enum EOpcode {
		eHalt,
		eLDA,
		eLDC,
		eSTA,
		eADDA,
		eADDC,
		eSUBA,
		eSUBC,
		eMULA,
		eMULC,
		eDIVA,
		eDIVC,
		eANDA,
		eJMP,
		eJMPBZ,
		eJMPBZEQ,
		eJMPEQ
		
	}
	
	private class Register {
		protected short value;
		public short getValue() { return this.value; }
		public void setValue(short value) { this.value = value; }
	}
	
	private class CU {
		public boolean isEQ(Register sr) {
			if ((sr.getValue() & 0x8000) == 0) {
				return false;
			} else {
				return true;
			}
		}
		
		public boolean isBZ(Register sr) {
			if ((sr.getValue() & 0x4000) == 0) {
				return false;
			} else {
				return true;
			}
		}
		
		public boolean isBZEQ(Register sr) {
			if (this.isEQ(sr) || this.isBZ(sr)) {
				return true;
			} else {
				return false;
			}
		}
		
	}

	private class ALU {
		private Register[] registers;
		private short value;
		
		public ALU(Register[] registers) {
			this.registers = registers;
		}
		
		public void store(short value) {
			this.value = value;
		}
		public void add(short value) {
			this.registers[ERegister.eAC.ordinal()].setValue((short) (this.value + value));
		}
		
		public void substract(short value) {
			this.registers[ERegister.eAC.ordinal()].setValue((short) (this.value - value));
		}
		
		public void multiply(short value) {
			
		}
		public void divide(short value) {
			this.registers[ERegister.eAC.ordinal()].setValue((short) (this.value / value));
		}
		
		public void and(short value) {
			
		}
	}
	
	private enum ERegister {
		eIR,
		ePC,
		eSP,
		eAC,
		eMBR,
		eMAR,
		eStatus
	}
	
	private class IR extends Register {
		public short getOperator() {
			return (short) (this.value >> 8);
		}
		
		public short getOperand() {
			return (short)(this.value & 0x00ff);
		}
	}
	
	// components
	private CU cu;
	private ALU alu;
	Register registers[];
	
	// association
	private Memory memory;
	private Loader loader;
	
	// states
	private boolean bPowerOn;
	
	private boolean isPowerOn() { return this.bPowerOn; }
	
	public void setPowerOn() { 
		this.bPowerOn = true; 
		this.run();
	}
	
	public void shutDown() { this.bPowerOn = false; }
	
	public void setPC(short size) {
		this.registers[ERegister.ePC.ordinal()].setValue(size);
	}
	
	public void setSP(short size) {
		this.registers[ERegister.eSP.ordinal()].setValue(size);
	}
	
	// instructions
	
	private void Halt() {
		this.shutDown();
	}
	
	private void LDA() {
		this.registers[ERegister.eMAR.ordinal()].setValue(((CPU.IR) this.registers[ERegister.eIR.ordinal()]).getOperand());
		this.registers[ERegister.eMBR.ordinal()].setValue(this.memory.load((short) (this.registers[ERegister.eSP.ordinal()].getValue() + this.registers[ERegister.eMAR.ordinal()].getValue() / 2)));
		this.registers[ERegister.eAC.ordinal()].setValue(this.registers[ERegister.eMBR.ordinal()].getValue());
		increasePC();
	}
	
	private void LDC() {
		this.registers[ERegister.eMBR.ordinal()].setValue(((CPU.IR) this.registers[ERegister.eIR.ordinal()]).getOperand());
		this.registers[ERegister.eAC.ordinal()].setValue(this.registers[ERegister.eMBR.ordinal()].getValue());
		increasePC();
	}
	
	private void STA() {
		this.registers[ERegister.eMBR.ordinal()].setValue(this.registers[ERegister.eAC.ordinal()].getValue());
		this.registers[ERegister.eMAR.ordinal()].setValue(((CPU.IR) this.registers[ERegister.eIR.ordinal()]).getOperand());
		this.memory.store((short) (this.registers[ERegister.eSP.ordinal()].getValue() + this.registers[ERegister.eMAR.ordinal()].getValue() / 2), this.registers[ERegister.eMBR.ordinal()].getValue());
		increasePC();
	}
	
	private void ADDA() {
		this.alu.store(this.registers[ERegister.eAC.ordinal()].getValue());
		this.LDA();
		decreasePC();
		this.alu.add(this.registers[ERegister.eAC.ordinal()].getValue());
		increasePC();
	}
	
	private void ADDC() {
		this.alu.store(this.registers[ERegister.eAC.ordinal()].getValue());
		this.LDC();
		decreasePC();
		this.alu.add(this.registers[ERegister.eAC.ordinal()].getValue());
		increasePC();
	}
	
	private void SUBA() {
		this.alu.store(this.registers[ERegister.eAC.ordinal()].getValue());
		this.LDA();
		decreasePC();
		this.alu.substract(this.registers[ERegister.eAC.ordinal()].getValue());
		setStatus();
		increasePC();
	}
	
	private void SUBC() {
		this.alu.store(this.registers[ERegister.eAC.ordinal()].getValue());
		this.LDC();
		decreasePC();
		this.alu.substract(this.registers[ERegister.eAC.ordinal()].getValue());
		setStatus();
		increasePC();
	}
	
	private void MULA() {
		this.alu.store(this.registers[ERegister.eAC.ordinal()].getValue());
		this.LDA();
		decreasePC();
		this.alu.multiply(this.registers[ERegister.eAC.ordinal()].getValue());
		increasePC();
	}
	
	private void MULC() {
		
	}
	
	private void DIVA() {
		this.alu.store(this.registers[ERegister.eAC.ordinal()].getValue());
		this.LDA();
		decreasePC();
		this.alu.divide(this.registers[ERegister.eAC.ordinal()].getValue());
		increasePC();
	}
	
	private void DIVC() {
		this.alu.store(this.registers[ERegister.eAC.ordinal()].getValue());
		this.LDC();
		decreasePC();
		this.alu.divide(this.registers[ERegister.eAC.ordinal()].getValue());
		increasePC();
	}
	
	private void ANDA() {
		this.alu.store(this.registers[ERegister.eAC.ordinal()].getValue());
		this.LDA();
		decreasePC();
		this.alu.and(this.registers[ERegister.eAC.ordinal()].getValue());
		increasePC();
	}
	
	private void JMP() {
		this.registers[ERegister.ePC.ordinal()].setValue((short) (((CPU.IR) this.registers[ERegister.eIR.ordinal()]).getOperand() - 1));
	}
	
	private void JMPBZ() {
		if (this.cu.isBZ(this.registers[ERegister.eStatus.ordinal()])) {
			this.registers[ERegister.ePC.ordinal()].setValue((short) (((CPU.IR) this.registers[ERegister.eIR.ordinal()]).getOperand() - 1));
		} else {
			increasePC();
		}
	}

	private void JMPBZEQ() {
		if (this.cu.isBZEQ(this.registers[ERegister.eStatus.ordinal()])) {
			this.registers[ERegister.ePC.ordinal()].setValue((short) (((CPU.IR) this.registers[ERegister.eIR.ordinal()]).getOperand() - 1));
		} else {
			increasePC();
		}
	}
	
	private void JMPEQ() {
		if (this.cu.isEQ(this.registers[ERegister.eStatus.ordinal()])) {
			this.registers[ERegister.ePC.ordinal()].setValue((short) (((CPU.IR) this.registers[ERegister.eIR.ordinal()]).getOperand() - 1));
		} else {
			increasePC();
		}
	}
	
	private void increasePC() {
		this.registers[ERegister.ePC.ordinal()].setValue((short) (this.registers[ERegister.ePC.ordinal()].getValue() + 1));
	}
	
	private void decreasePC() {
		this.registers[ERegister.ePC.ordinal()].setValue((short) (this.registers[ERegister.ePC.ordinal()].getValue() - 1));
	}
	
	private void setStatus() {
		if (this.registers[ERegister.eAC.ordinal()].getValue() == 0) {
			this.registers[ERegister.eStatus.ordinal()].setValue((short) 0x8000);
		} else if (this.registers[ERegister.eAC.ordinal()].getValue() < 0) {
			this.registers[ERegister.eStatus.ordinal()].setValue((short) 0x4000);
		} else {
			this.registers[ERegister.eStatus.ordinal()].setValue((short) 0x0000);
		}
	}
	
	// constructor
	public CPU() {
		this.registers = new Register[ERegister.values().length];
		this.alu = new ALU(this.registers);
		this.cu = new CU();
		for (ERegister eRegister: ERegister.values()) {
			this.registers[eRegister.ordinal()] = new Register();
		}
		this.registers[ERegister.eIR.ordinal()] = new IR();
	}
	
	// associate
	public void associate(Memory memory) { this.memory = memory; }
	public void associate(Loader loader) { this.loader = loader; }
	
	// methods
	private void fetch() {
		this.registers[ERegister.eMAR.ordinal()].setValue(this.registers[ERegister.ePC.ordinal()].getValue());
		this.registers[ERegister.eMBR.ordinal()].setValue(this.memory.load(this.registers[ERegister.eMAR.ordinal()].getValue()));
		this.registers[ERegister.eIR.ordinal()].setValue(this.registers[ERegister.eMBR.ordinal()].getValue());
	}
	
	private void execute() {
		switch (EOpcode.values()[((CPU.IR) this.registers[ERegister.eIR.ordinal()]).getOperator()]) {
			case eHalt:
				this.Halt();
				break;
			case eLDA:
				this.LDA();
				break;
			case eLDC:
				this.LDC();
				break;
			case eSTA:
				this.STA();
				break;
			case eADDA:
				this.ADDA();
				break;
			case eADDC:
				this.ADDC();
				break;
			case eSUBA:
				this.SUBA();
				break;
			case eSUBC:
				this.SUBC();
				break;
			case eMULA:
				this.MULA();
				break;
			case eMULC:
				this.MULC();
				break;
			case eDIVA:
				this.DIVA();
				break;
			case eDIVC:
				this.DIVC();
				break;
			case eANDA:
				this.ANDA();
				break;
			case eJMP:
				this.JMP();
				break;
			case eJMPBZ:
				this.JMPBZ();
				break;
			case eJMPEQ:
				this.JMPEQ();
				break;
			case eJMPBZEQ:
				this.JMPBZEQ();
				break;
			default:
				break;
		}
	}

	public void run() {
		while(isPowerOn()) {
			this.fetch();
			this.execute();
		}
	}
	
	public static void main(String[] args) throws FileNotFoundException {
		CPU cpu = new CPU();
		Memory memory = new Memory();
		Loader loader = new Loader(cpu, memory);
		cpu.associate(memory);
		cpu.associate(loader);
		loader.load("rank");
		cpu.setPowerOn();
	}

}
