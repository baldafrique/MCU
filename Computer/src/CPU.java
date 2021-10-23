
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
		eJMPEQ
	}
	
	private class CU {
		
	}

	private class ALU {
		public void add(short value) {
			
		}
		
		public void store(short value) {
			
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
	
	private class Register {
		protected short value;
		public short getValue() { return this.value; }
		public void setValue(short value) { this.value = value; }
	}
	
	private class IR extends Register {
		public short getOperator() {
			return (short) (this.value & 0);
		}
		
		public short getOperand() {
			return (short)(this.value & 0x00ff);
		}
	}
	
	private class SR extends Register {
		// eq
		public boolean checkEQ() {
			if ((this.getValue() & 0x8000) == 0) {
				return false;
			} else {
				return true;
			}
		}
		
		// bz
		public boolean checkBZ() {
			if ((this.getValue() & 0x0800) == 0) {
				return false;
			} else {
				return true;
			}
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
	
	// instructions
	private void Halt() {}
	
	private void LDA() {
		// IR.operand -> MAR
		this.registers[ERegister.eMAR.ordinal()].setValue(((CPU.IR) this.registers[ERegister.eIR.ordinal()]).getOperand());
		// memory[MAR] -> MBR
		this.registers[ERegister.eMBR.ordinal()].setValue(this.memory.load(this.registers[ERegister.eMAR.ordinal()].getValue()));
		// MBR -> AC
		this.registers[ERegister.eAC.ordinal()].setValue(this.registers[ERegister.eMBR.ordinal()].getValue());
	}
	
	private void LDC() {
		// IR.operand -> MBR
		this.registers[ERegister.eMBR.ordinal()].setValue(((CPU.IR) this.registers[ERegister.eIR.ordinal()]).getOperand());
		// MBR -> AC
		this.registers[ERegister.eAC.ordinal()].setValue(this.registers[ERegister.eMBR.ordinal()].getValue());
	}
	
	private void STA() {
		// AC -> MBR
		this.registers[ERegister.eMBR.ordinal()].setValue(this.registers[ERegister.eAC.ordinal()].getValue());
		// IR.operand -> MAR
		this.registers[ERegister.eMAR.ordinal()].setValue(((CPU.IR) this.registers[ERegister.eIR.ordinal()]).getOperand());
		// MBR -> memory[MAR]
		this.memory.store(this.registers[ERegister.eMAR.ordinal()].getValue(), this.registers[ERegister.eMBR.ordinal()].getValue());
	}
	
	private void ADDA() {
		this.alu.store(this.registers[ERegister.eAC.ordinal()].getValue());
		this.LDA();
		this.alu.add(this.registers[ERegister.eAC.ordinal()].getValue());
	}
	
	private void ADDC() {
		// AC -> ALU
		this.alu.store(this.registers[ERegister.eAC.ordinal()].getValue());
		this.LDC();
		this.alu.add(this.registers[ERegister.eAC.ordinal()].getValue());
	}
	
	private void SUBA() {}
	
	private void SUBC() {}
	
	private void MULA() {}
	
	private void DIVA() {}
	
	private void ANDA() {}
	
	private void JMP() {
		
	}
	
	private void JMPBZ() {}
	
	private void JMPEQ() {}
	
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
		// load next instruction from memory to IR
		// PC -> MAR
		this.registers[ERegister.eMAR.ordinal()].setValue(this.registers[ERegister.ePC.ordinal()].getValue());
		// memory[MAR] -> MBR
		this.registers[ERegister.eMBR.ordinal()].setValue(this.registers[ERegister.eMAR.ordinal()].getValue());
		// MBR -> IR
		this.registers[ERegister.eIR.ordinal()].setValue(this.registers[ERegister.eMBR.ordinal()].getValue());
	}
	
	private void decode() {
		
	}
	
	private void execute() {
		System.out.println(((CPU.IR) this.registers[ERegister.eIR.ordinal()]).getOperator());
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
			case eJMPEQ:
				this.JMPEQ();
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
	
	public static void main(String[] args) {
		CPU cpu = new CPU();
		Memory memory = new Memory();
		cpu.associate(memory);
		memory.loadProcess("sum");
		cpu.setPowerOn();
	}
}
