package de.tum.in.mislcontrol;

import de.tum.in.mislcontrol.communication.ASEPConnector;
import de.tum.in.mislcontrol.communication.IConnector;

public class ASEPAdapter {
    private final IConnector connector;

    public ASEPAdapter(IConnector.OnTelemetryReceivedListener receiver) {
        connector = new ASEPConnector();
        connector.setOnTelemetryReceivedListener(receiver);
    }

    public void start() {
        connector.start();
    }

    public void stop() {
        connector.stop();
    }

    public void close() {
        connector.close();
    }

    /**
     * Translates a double value to a corresponding ASEP value
     *
     * @param value the desired value range -1 ~ 1
     * @return the value translated to a discrete range -6 ~ 6
     */
    public short translateToASEPCommand(double value) {
        if (value < -1 || value > 1) {
            throw new IllegalArgumentException(value + "not in range -1 ~ 1");
        }
        return (short) Math.round(value * 6);
    }

    /**
     * Commands the robot to drive in given direction. This method should be on par with the
     * Windows implementation.
     *
     * @param xDirection the rotational / steering left or right command. Range -1 ~ 1
     * @param yDirection the target speed. Range -1 ~ 1
     */
    public void drive(double xDirection, double yDirection) {
        final short x = translateToASEPCommand(xDirection);
        final short y = translateToASEPCommand(yDirection);

        short ch1 = 0, ch2 = 0;
        if (((x < -2) || (x > 2)) && ((y < 2) && (y > -2))) { //X only
            ch1 = x; //rotate
            ch2 = (short) -x;
        } else if ((x == 0) && (y != 0)) {
            ch1 = y; //drive straight
            ch2 = y;
        } else if ((x == 6) && (y == 6)) { // 6 equals full throttle
            ch1 = y;
            ch2 = -2;
        } else if ((x == -6) && (y == 6)) {
            ch1 = -2;
            ch2 = y;
        } else if ((x == 6) && (y == -6)) {
            ch1 = y;
            ch2 = 2;
        } else if ((x == -6) && (y == -6)) {
            ch1 = 2;
            ch2 = y;
        } else if (Math.abs(x) < Math.abs(y)) {
            if (y < 0) {
                if (x < -1) {
                    ch1 = (short) (y + (2 + Math.abs(x)));
                    ch2 = y;
                } else if (x > 1) {
                    ch1 = y;
                    ch2 = (short) (y + (2 + Math.abs(x)));
                } else {
                    ch1 = y;
                    ch2 = y;
                }
            } else if (y > 0) {
                if (x < -1) {
                    ch1 = (short) (y - (2 + Math.abs(x)));
                    ch2 = y;
                } else if (x > 1) {
                    ch1 = y;
                    ch2 = (short) (y - (2 + Math.abs(x)));
                } else {
                    ch1 = y;
                    ch2 = y;
                }
            }
        } else if (Math.abs(x) >= Math.abs(y)) {
            if (y < 0) {
                if (x < 0) {
                    ch1 = 1;
                    ch2 = y;
                } else if (x > 0) {
                    ch1 = y;
                    ch2 = 1;
                }
            } else if (y > 0) {
                if (x < 0) {
                    ch1 = -1;
                    ch2 = y;
                } else if (x > 0) {
                    ch1 = y;
                    ch2 = -1;
                }
            }
        }

        connector.setCommand(ch1, ch2);
    }
}
