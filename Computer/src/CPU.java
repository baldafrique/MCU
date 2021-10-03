
public class CPU {
	// declaration

	private enum EOpcode {
		eHalt, eLDA, eSTA, eADD, eSub, eGT, eEQ, eBGT, // Branch greater than
		eBEQ, // Branch equal
		eBranch,
	}

	private enum ERegister {
		eIR, eSP, ePC, eAC, eMBR, eMAR, eStatus
	}

	private class CU {

	}

	private class ALU {

		public void add() {

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

		public short getValue() {
			return this.value;
		}

		public void setValue(short value) {
			this.value = value;
		}
	}

	private class IR extends Register {
		public short getOperator() {
			return (short) (this.value & 0);
		}

		public short getOperand() {
			return (short) (this.value & 0x00ff);
		}
	}

	// components
	private ALU alu;
	private CU cu;
	Register registers[];
	private Memory memory;

	// states
	private boolean bPowerOn;

	private boolean isPowerOn() {
		return this.bPowerOn;
	}

	public void setPowerOn() {
		this.bPowerOn = true;
		this.run();
	}

	public void shutDown() {
		this.bPowerOn = false;
	}

	// constructor
	public CPU() {
		this.alu = new ALU();
		this.cu = new CU();
		this.registers = new Register[ERegister.values().length];

	}

	// associate
	public void associate(Memory memory) {
		this.memory = memory;
	}

	// methods
	private void fetch() {
		System.out.println(this.registers[1]);
		
		// load next instruction from memory to IR
		// PC -> MAR
		this.registers[ERegister.eMAR.ordinal()].setValue(this.registers[ERegister.ePC.ordinal()].getValue());
		this.memory.load();
		this.registers[ERegister.eIR.ordinal()].setValue(this.registers[ERegister.eMBR.ordinal()].getValue());
	}

	private void load() {
		this.registers[ERegister.eMAR.ordinal()]
				.setValue((short) (((CPU.IR) this.registers[ERegister.eIR.ordinal()]).getOperand()
						+ this.registers[ERegister.eSP.ordinal()].getValue()));
		this.memory.load();
	}

	private void execute() {
		switch (EOpcode.values()[((CPU.IR) this.registers[ERegister.eIR.ordinal()]).getOperator()]) {
		case eHalt:
			break;
		case eLDA:
			this.load();
			break;
		case eSTA:
			this.memory.store();
			break;
		case eADD:
			this.registers[ERegister.eAC.ordinal()].setValue(this.registers[ERegister.eMBR.ordinal()].getValue());
			this.load();
			this.alu.add();
			break;
		case eSub:
			this.registers[ERegister.eAC.ordinal()].setValue(this.registers[ERegister.eMBR.ordinal()].getValue());
			this.load();
			this.alu.subtract();
			break;
		case eGT:
			this.registers[ERegister.eAC.ordinal()].setValue(this.registers[ERegister.eMBR.ordinal()].getValue());
			this.load();
			this.alu.greaterThan();
			break;
		case eEQ:
			this.registers[ERegister.eAC.ordinal()].setValue(this.registers[ERegister.eMBR.ordinal()].getValue());
			this.load();
			this.alu.equal();
			break;
		case eBranch:
			break;
		default:
			break;
		}
	}

	public void run() {
		while (isPowerOn()) {
			this.fetch();
			this.execute();
		}
	}

	public static void main(String[] args) {
		CPU cpu = new CPU();
		Memory memory = new Memory();
		cpu.associate(memory);

		cpu.setPowerOn();
	}
}
