package m68k.cpu.instructions;

import m68k.cpu.*;
import m68k.TaintedValue;

/*
//  M68k - Java Amiga MachineCore
//  Copyright (c) 2008-2010, Tony Headford
//  All rights reserved.
//
//  Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
//  following conditions are met:
//
//    o  Redistributions of source code must retain the above copyright notice, this list of conditions and the
//       following disclaimer.
//    o  Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the
//       following disclaimer in the documentation and/or other materials provided with the distribution.
//    o  Neither the name of the M68k Project nor the names of its contributors may be used to endorse or promote
//       products derived from this software without specific prior written permission.
//
//  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
//  INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
//  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
//  SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
//  SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
//  WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
//  OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
//
*/
public class EORI_TO_CCR implements InstructionHandler
{
	protected final Cpu cpu;

	public EORI_TO_CCR(Cpu cpu)
	{
		this.cpu = cpu;
	}

    @Override
	public final void register(InstructionSet is)
	{
        int base;
        Instruction i;
        base = 0x0a3c;
        i = new Instruction()
        {
            public int execute(TaintedValue opcode)
            {
                return eori_word(opcode.value);
            }
            public DisassembledInstruction disassemble(int address, int opcode)
            {
                return disassembleOp(address, opcode, Size.Word);
            }
        };
        is.addInstruction(base, i);
	}

	
	protected final int eori_word(int opcode)
	{
        // mask out bits 5-7,they are always 0
		int s = cpu.fetchPCWordSigned().value & 31 ;
        int sr = cpu.getCCRegister();
        s = s ^ (sr & 0x00ff);
        cpu.setCCRegister(s);
        return 8;
	}

	protected final DisassembledInstruction disassembleOp(int address, int opcode, Size sz)
	{
        int imm_bytes;
		int imm;
		String is;
		imm = cpu.readMemoryWord(address + 2).value;
        is = String.format("#$%04x", imm);
        imm_bytes = 2;

		DisassembledOperand src = new DisassembledOperand(is, imm_bytes, imm);
		DisassembledOperand dst = cpu.disassembleDstEA(address + 2 + imm_bytes, (opcode >> 3) & 0x07, (opcode & 0x07), sz);
		return new DisassembledInstruction(address, opcode, "ori" + sz.ext(), src, dst);
	}
}
