package aed.recursion;

import es.upm.aedlib.map.*;
import es.upm.aedlib.Position;
import es.upm.aedlib.positionlist.*;
import es.upm.aedlib.lifo.*;

public class StackMachine {
     Map<String, PositionList<Instruction>> code; // Mapa de rutinas
     LIFO<Integer> stack; // Pila de enteros para la ejecución

    // Constructor que inicializa el código y la pila
    public StackMachine(Map<String, PositionList<Instruction>> code) {
        this.stack = new LIFOArray<>(); // Pila implementada con LIFOArray
        this.code = code;
    }

    // Método para ejecutar una rutina específica
    public void run(String name) {
        PositionList<Instruction> instructions = code.get(name);
        if (instructions == null) {
            throw new RuntimeException("No existe la rutina con nombre: " + name);
        }
        executeInstructions(instructions, instructions.first());
    }

    // Método recursivo para ejecutar las instrucciones
    private void executeInstructions(PositionList<Instruction> instructions, Position<Instruction> cursor) {
        if (cursor == null) return; // Caso base: fin de la lista de instrucciones

        Instruction inst = cursor.element(); // Instrucción actual
        Instruction.InstType type = inst.getInstType();

        // Ejecuta la instrucción según su tipo
        switch (type) {
            case PUSH:
                stack.push(inst.getIntParm());
                break;

            case ADD:
                int addOp1 = stack.pop();
                int addOp2 = stack.pop();
                stack.push(addOp1 + addOp2);
                break;

            case SUB:
                int subOp1 = stack.pop();
                int subOp2 = stack.pop();
                stack.push(subOp2 - subOp1); // Orden correcto para la resta
                break;

            case MULT:
                int multOp1 = stack.pop();
                int multOp2 = stack.pop();
                stack.push(multOp1 * multOp2);
                break;

            case PRINT:
                System.out.println(stack.top()); // Muestra el elemento en la cima de la pila
                stack.pop(); // Remueve el elemento después de imprimir
                break;

            case DROP:
                stack.pop();
                break;

            case DUP:
                stack.push(stack.top()); // Duplica el elemento en la cima de la pila
                break;

            case SWAP:
                int top = stack.pop();
                int second = stack.pop();
                stack.push(top);
                stack.push(second);
                break;

            case CALL:
                String procName = inst.getNameParm();
                PositionList<Instruction> procInstructions = code.get(procName);
                if (procInstructions == null) {
                    throw new RuntimeException("No existe la rutina con nombre: " + procName);
                }
                executeInstructions(procInstructions, procInstructions.first());
                return; // Regresa al finalizar la subrutina para evitar recursión infinita

            case RET:
                return; // Finaliza la ejecución de la rutina actual

            case EQ:
                int eqOp1 = stack.pop();
                int eqOp2 = stack.pop();
                stack.push(eqOp1 == eqOp2 ? 1 : 0); // Empuja 1 si son iguales, 0 si no
                break;

            case IF_SKIP:
                int condition = stack.pop();
                int skipCount = inst.getIntParm();
                if (condition != 0) {
                    cursor = skipInstructions(cursor, instructions, skipCount);
                }
                break;

            default:
                throw new UnsupportedOperationException("Instrucción desconocida: " + type);
        }

        // Llama a la siguiente instrucción si no se ha encontrado un RET
        if (type != Instruction.InstType.RET) {
            Position<Instruction> nextCursor = instructions.next(cursor);
            executeInstructions(instructions, nextCursor);
        }
    }

    // Método para saltar instrucciones (usado en IF_SKIP)
    private Position<Instruction> skipInstructions(Position<Instruction> cursor, PositionList<Instruction> instructions, int n) {
        Position<Instruction> pos = cursor;
        while (n > 0 && pos != null) {
            pos = instructions.next(pos);
            n--;
        }
        return pos;
    }
}