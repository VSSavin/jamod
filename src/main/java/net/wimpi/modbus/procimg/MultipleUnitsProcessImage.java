package net.wimpi.modbus.procimg;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Class implementing a multiple units process image
 * to handle cases with multiple units on the same port
 * @author vssavin on 16.01.2023
 */
public class MultipleUnitsProcessImage implements ProcessImageImplementation {
    protected Map<Integer, ProcessImageImplementation> units = new HashMap<>();
    private int unit = 0;

    public MultipleUnitsProcessImage(Set<Integer> units) {
        if (units.size() == 0) throw new IllegalStateException("The constructor parameter can not be empty!");
        for(Integer unit : units) {
            this.units.put(unit, new SimpleProcessImage());
        }
    }

    public void setCurrentUnit(int unit) {
        if (units.containsKey(unit)) this.unit = unit;
        else throw new UnitNotFoundException(String.format("Unit [%s] not found!", unit));
    }

    @Override
    public DigitalOut[] getDigitalOutRange(int offset, int count) throws IllegalAddressException {
        return getCurrentProcessImage().getDigitalOutRange(offset, count);
    }

    @Override
    public DigitalOut getDigitalOut(int ref) throws IllegalAddressException {
        return getCurrentProcessImage().getDigitalOut(ref);
    }

    @Override
    public int getDigitalOutCount() {
        return getCurrentProcessImage().getDigitalOutCount();
    }

    @Override
    public DigitalIn[] getDigitalInRange(int offset, int count) throws IllegalAddressException {
        return getCurrentProcessImage().getDigitalInRange(offset, count);
    }

    @Override
    public DigitalIn getDigitalIn(int ref) throws IllegalAddressException {
        return getCurrentProcessImage().getDigitalIn(ref);
    }

    @Override
    public int getDigitalInCount() {
        return getCurrentProcessImage().getDigitalInCount();
    }

    @Override
    public InputRegister[] getInputRegisterRange(int offset, int count) throws IllegalAddressException {
        return getCurrentProcessImage().getInputRegisterRange(offset, count);
    }

    @Override
    public InputRegister getInputRegister(int ref) throws IllegalAddressException {
        return getCurrentProcessImage().getInputRegister(ref);
    }

    @Override
    public int getInputRegisterCount() {
        return getCurrentProcessImage().getInputRegisterCount();
    }

    @Override
    public Register[] getRegisterRange(int offset, int count) throws IllegalAddressException {
        return getCurrentProcessImage().getRegisterRange(offset, count);
    }

    @Override
    public Register getRegister(int ref) throws IllegalAddressException {
        return getCurrentProcessImage().getRegister(ref);
    }

    @Override
    public int getRegisterCount() {
        return getCurrentProcessImage().getRegisterCount();
    }

    @Override
    public void setDigitalOut(int ref, DigitalOut _do) throws IllegalAddressException {
        getCurrentProcessImage().setDigitalOut(ref, _do);
    }

    @Override
    public void addDigitalOut(DigitalOut _do) {
        getCurrentProcessImage().addDigitalOut(_do);
    }

    @Override
    public void removeDigitalOut(DigitalOut _do) {
        getCurrentProcessImage().removeDigitalOut(_do);
    }

    @Override
    public void setDigitalIn(int ref, DigitalIn di) throws IllegalAddressException {
        getCurrentProcessImage().setDigitalIn(ref, di);
    }

    @Override
    public void addDigitalIn(DigitalIn di) {
        getCurrentProcessImage().addDigitalIn(di);
    }

    @Override
    public void removeDigitalIn(DigitalIn di) {
        getCurrentProcessImage().removeDigitalIn(di);
    }

    @Override
    public void setInputRegister(int ref, InputRegister reg) throws IllegalAddressException {
        getCurrentProcessImage().setInputRegister(ref, reg);
    }

    @Override
    public void addInputRegister(InputRegister reg) {
        getCurrentProcessImage().addInputRegister(reg);
    }

    @Override
    public void removeInputRegister(InputRegister reg) {
        getCurrentProcessImage().removeInputRegister(reg);
    }

    @Override
    public void setRegister(int ref, Register reg) throws IllegalAddressException {
        getCurrentProcessImage().setRegister(ref, reg);
    }

    @Override
    public void addRegister(Register reg) {
        getCurrentProcessImage().addRegister(reg);
    }

    @Override
    public void removeRegister(Register reg) {
        getCurrentProcessImage().removeRegister(reg);
    }

    private ProcessImageImplementation getCurrentProcessImage() {
        return units.get(unit);
    }

    private static class UnitNotFoundException extends RuntimeException {
        private UnitNotFoundException(String message) {
            super(message);
        }
    }
}
