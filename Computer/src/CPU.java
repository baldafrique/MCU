
public class CPU {
	
	// declaration
	private enum EOpcode {
		eHalt,
		eLDA,
		eSTA,
		eADD,
		eAND,
		eJMP,
		eJMPBZ,
		eJMPEQ,
		eSETAC,
//		eSub,
//		eGT,
//		eEQ,
//		eBGT, // Branch greater than
//		eBEQ, // Branch equal
//		eBranch,
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
	
	private class CU {
		
	}

	private class ALU {
		public void add(CPU.Register[] registers) {
			registers[ERegister.eAC.ordinal()].setValue(
					(short) (registers[ERegister.eAC.ordinal()].getValue() + registers[ERegister.eMBR.ordinal()].getValue())
					);
		}

		public void subtract() {
			
		}

		public void greaterThan() {
			
		}

		public void equal() {
			
		}
		
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
	
	// components
	private ALU alu;
	private CU cu;
	int PC;
	int SP;
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
		public void associate(Memory memory) {
			this.memory = memory;
			this.registers[ERegister.ePC.ordinal()].setValue(this.memory.process[PC]); // Set PC
			this.registers[ERegister.eSP.ordinal()].setValue(this.memory.process[SP]); // Set SP
			String size;
			size = Integer.toHexString((int)this.memory.process[1]);
			this.PC = Integer.parseInt(size) / 2;
			System.out.println("PC is set up");
			size = Integer.toHexString((int)this.memory.process[2]);
			this.SP = this.PC + Integer.parseInt(size) / 2;
			System.out.println("SP is set up");
		}
	
	// methods
	private void fetch() {
		// load next instruction from memory to IR
		// PC -> MAR
		this.registers[ERegister.eMAR.ordinal()].setValue(this.registers[ERegister.ePC.ordinal()].getValue());
		System.out.println("MAR <- PC");
		this.registers[ERegister.eMBR.ordinal()].setValue(this.registers[ERegister.eMAR.ordinal()].getValue());
		System.out.println("MBR <- (MAR)");
		this.registers[ERegister.eIR.ordinal()].setValue(this.registers[ERegister.eMBR.ordinal()].getValue());
		System.out.println("IR <- MBR");
	}
	
	private void load() {
		// load data from memory
		this.registers[ERegister.eMAR.ordinal()].setValue(
				(short)
				(((CPU.IR) this.registers[ERegister.eIR.ordinal()]).getOperand()
				+ this.registers[ERegister.eSP.ordinal()].getValue()));
		this.registers[ERegister.eMBR.ordinal()].setValue(this.memory.load(this.registers[ERegister.eMAR.ordinal()].getValue()));
		this.registers[ERegister.eAC.ordinal()].setValue(this.registers[ERegister.eMBR.ordinal()].getValue());
	}
	
	private void store() {
		this.memory.store(this.registers[ERegister.eMAR.ordinal()].getValue(), this.registers[ERegister.eMBR.ordinal()].getValue());
	}
	
	private void execute() {
		System.out.println(((CPU.IR) this.registers[ERegister.eIR.ordinal()]).getOperator());
		switch (EOpcode.values()[((CPU.IR) this.registers[ERegister.eIR.ordinal()]).getOperator()]) {
			case eHalt:
				break;
			case eLDA:
				this.load();
				break;
			case eSTA:
				this.store();
				System.out.println("STA");
				break;
			case eADD:
				this.registers[ERegister.eMAR.ordinal()].setValue(
						(short)
						(((CPU.IR) this.registers[ERegister.eIR.ordinal()]).getOperand()
						+ this.registers[ERegister.eSP.ordinal()].getValue()));
				this.registers[ERegister.eMBR.ordinal()].setValue(this.memory.load(this.registers[ERegister.eMAR.ordinal()].getValue()));
//				this.registers[ERegister.eAC.ordinal()].setValue(this.registers[ERegister.eMBR.ordinal()].getValue());
//				this.load();
				this.alu.add(this.registers);
				break;
			case eAND:
				break;
			case eJMP:
				break;
			case eJMPBZ:
				break;
			case eJMPEQ:
				break;
			case eSETAC:
				this.registers[ERegister.eAC.ordinal()].setValue(((CPU.IR) this.registers[ERegister.eIR.ordinal()]).getOperand());
				System.out.println("SETAC");
				break;
//			case eSub:
//				this.registers[ERegister.eAC.ordinal()].setValue(this.registers[ERegister.eMBR.ordinal()].getValue());
//				this.load();
//				this.alu.subtract();
//				break;
//			case eGT:
//				this.registers[ERegister.eAC.ordinal()].setValue(this.registers[ERegister.eMBR.ordinal()].getValue());
//				this.load();
//				this.alu.greaterThan();
//				break;
//			case eEQ:
//				this.registers[ERegister.eAC.ordinal()].setValue(this.registers[ERegister.eMBR.ordinal()].getValue());
//				this.load();
//				this.alu.equal();
//				break;
//			case eBranch:
//				break;
			default:
				break;
		}
		this.registers[ERegister.ePC.ordinal()].setValue(this.memory.process[++PC]);
	}

	
	public void run() {
		while(isPowerOn()) {
			this.fetch();
			this.execute();
			
			if (this.PC == this.SP) {
				System.out.println(this.memory.process[13]);
				break;
			}
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
