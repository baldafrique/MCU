
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
		eDIVA,
		eANDA,
		eJMP,
		eJMPBZ,
		eJMPBZEQ, // (A<=B)
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
		public void store(short value) {
			
		}
		public void add(short value) {
			
		}
		
		public void substract(short value) {
			
		}
		
		public void multiply(short value) {
			
		}
		public void divide(short value) {
			
		}
		
		public void and(short value) {
			
		}
	}
	
	private enum ERegister {
		eIR,
		eSP,
		ePC,
		eAC,
		eMBR,
		eMAR,
		eStatus
	}
	
	private class IR extends Register {
		public short getOperator() {
			return (short) (this.value & 0);
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
	
	// states
	private boolean bPowerOn;
	
	private boolean isPowerOn() { return this.bPowerOn; }
	
	public void setPowerOn() { 
		this.bPowerOn = true; 
		this.run();
	}
	
	public void shutDown() { this.bPowerOn = false; }
	
	public void setPointers() {
		this.registers[ERegister.ePC.ordinal()].setValue(this.memory.getPC());
		this.registers[ERegister.eSP.ordinal()].setValue(this.memory.getSP());
	}
	
	// instructions
	private void Halt() {
		
	}
	
	private void LDA() {
		this.registers[ERegister.eMAR.ordinal()].setValue(((CPU.IR) this.registers[ERegister.eIR.ordinal()]).getOperand());
		this.registers[ERegister.eMBR.ordinal()].setValue(this.memory.load(this.registers[ERegister.eMAR.ordinal()].getValue()));
		this.registers[ERegister.eAC.ordinal()].setValue(this.registers[ERegister.eMBR.ordinal()].getValue());
	}
	
	private void LDC() {
		this.registers[ERegister.eMBR.ordinal()].setValue(((CPU.IR) this.registers[ERegister.eIR.ordinal()]).getOperand());
		this.registers[ERegister.eAC.ordinal()].setValue(this.registers[ERegister.eMBR.ordinal()].getValue());
	}
	
	private void STA() {
		this.registers[ERegister.eMBR.ordinal()].setValue(this.registers[ERegister.eAC.ordinal()].getValue());
		this.registers[ERegister.eMAR.ordinal()].setValue(((CPU.IR) this.registers[ERegister.eIR.ordinal()]).getOperand());
		this.memory.store(this.registers[ERegister.eMAR.ordinal()].getValue(), this.registers[ERegister.eMBR.ordinal()].getValue());
	}
	
	private void ADDA() {
		this.alu.store(this.registers[ERegister.eAC.ordinal()].getValue());
		this.LDA();
		this.alu.add(this.registers[ERegister.eAC.ordinal()].getValue());
	}
	
	private void ADDC() {
		this.alu.store(this.registers[ERegister.eAC.ordinal()].getValue());
		this.LDC();
		this.alu.add(this.registers[ERegister.eAC.ordinal()].getValue());
	}
	
	private void SUBA() {
		this.alu.store(this.registers[ERegister.eAC.ordinal()].getValue());
		this.LDA();
		this.alu.substract(this.registers[ERegister.eAC.ordinal()].getValue());
	}
	
	private void SUBC() {
		this.alu.store(this.registers[ERegister.eAC.ordinal()].getValue());
		this.LDC();
		this.alu.substract(this.registers[ERegister.eAC.ordinal()].getValue());	
	}
	
	private void MULA() {
		this.alu.store(this.registers[ERegister.eAC.ordinal()].getValue());
		this.LDA();
		this.alu.multiply(this.registers[ERegister.eAC.ordinal()].getValue());
	}
	
	private void DIVA() {
		this.alu.store(this.registers[ERegister.eAC.ordinal()].getValue());
		this.LDA();
		this.alu.divide(this.registers[ERegister.eAC.ordinal()].getValue());
	}
	
	private void ANDA() {
		this.alu.store(this.registers[ERegister.eAC.ordinal()].getValue());
		this.LDA();
		this.alu.and(this.registers[ERegister.eAC.ordinal()].getValue());
	}
	
	private void JMP() {
		this.registers[ERegister.ePC.ordinal()].setValue(((CPU.IR) this.registers[ERegister.eIR.ordinal()]).getOperand());
	}
	
	private void JMPBZ() {
		if (this.cu.isBZ(this.registers[ERegister.eStatus.ordinal()])) {
			this.registers[ERegister.ePC.ordinal()].setValue(((CPU.IR) this.registers[ERegister.eIR.ordinal()]).getOperand());
		}
	}

	private void JMPBZEQ() {
		if (this.cu.isBZEQ(this.registers[ERegister.eStatus.ordinal()])) {
			this.registers[ERegister.ePC.ordinal()].setValue(((CPU.IR) this.registers[ERegister.eIR.ordinal()]).getOperand());
		}
	}
	
	private void JMPEQ() {
		if (this.cu.isEQ(this.registers[ERegister.eStatus.ordinal()])) {
			this.registers[ERegister.ePC.ordinal()].setValue(((CPU.IR) this.registers[ERegister.eIR.ordinal()]).getOperand());
		}
	}
	
	// constructor
	public CPU() {
		this.alu = new ALU();
		this.cu = new CU();
		this.registers = new Register[ERegister.values().length];
		for (ERegister eRegister: ERegister.values()) {
			this.registers[eRegister.ordinal()] = new Register();
		}
		this.registers[ERegister.eIR.ordinal()] = new IR();
	}
	
	// associate
	public void associate(Memory memory) { this.memory = memory; }
	
	// methods
	private void fetch() {
		this.registers[ERegister.eMAR.ordinal()].setValue(this.registers[ERegister.ePC.ordinal()].getValue());
		this.registers[ERegister.eMBR.ordinal()].setValue(this.registers[ERegister.eMAR.ordinal()].getValue());
		this.registers[ERegister.eIR.ordinal()].setValue(this.registers[ERegister.eMBR.ordinal()].getValue());
	}
	
	private void decode() {
		
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
			case eDIVA:
				this.DIVA();
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
			case eJMPBZEQ:
				this.JMPBZEQ();
				break;
			case eJMPEQ:
				this.JMPEQ();
				break;
			default:
				break;
		}
	}

	public void run() {
		this.setPointers();
		while(isPowerOn()) {
			this.fetch();
			this.execute();
		}
	}
	
	public static void main(String[] args) {
		CPU cpu = new CPU();
		Memory memory = new Memory();
		cpu.associate(memory);
		memory.loadProcess("sum");
		cpu.setPowerOn();
	}
}
