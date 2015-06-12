package de.tum.in.mislcontrol;

import android.util.Pair;

import de.tum.in.mislcontrol.communication.ASEPConnector;
import de.tum.in.mislcontrol.communication.IConnector;

public class ASEPAdapter {

    private ASEPAdapter(){
    }

    /**
     * Translates a double value to a corresponding ASEP value
     *
     * @param value the desired value range -1 ~ 1
     * @return the value translated to a discrete range -6 ~ 6
     */
    public static short translateToASEPCommand(double value) {
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
     * @return a Pair with channel1 command and channel2 command
     */
    public static Pair<Short, Short> drive(double xDirection, double yDirection) {
        final short x = translateToASEPCommand(xDirection);
        final short y = translateToASEPCommand(yDirection);
        //According to Hectors logs, a command 6 6 is the idle / non moving command for ASEP
        final short ch1Base = 6, ch2Base = 6;

        short ch1Delta = 0, ch2Delta = 0;
        if (((x < -2) || (x > 2)) && ((y < 2) && (y > -2))) { //X only
            ch1Delta = x; //rotate
            ch2Delta = (short) -x;
        } else if ((x == 0) && (y != 0)) {
            ch1Delta = y; //drive straight
            ch2Delta = y;
        } else if ((x == 6) && (y == 6)) { // 6 equals full throttle
            ch1Delta = y;
            ch2Delta = -2;
        } else if ((x == -6) && (y == 6)) {
            ch1Delta = -2;
            ch2Delta = y;
        } else if ((x == 6) && (y == -6)) {
            ch1Delta = y;
            ch2Delta = 2;
        } else if ((x == -6) && (y == -6)) {
            ch1Delta = 2;
            ch2Delta = y;
        } else if (Math.abs(x) < Math.abs(y)) {
            if (y < 0) {
                if (x < -1) {
                    ch1Delta = (short) (y + (2 + Math.abs(x)));
                    ch2Delta = y;
                } else if (x > 1) {
                    ch1Delta = y;
                    ch2Delta = (short) (y + (2 + Math.abs(x)));
                } else {
                    ch1Delta = y;
                    ch2Delta = y;
                }
            } else if (y > 0) {
                if (x < -1) {
                    ch1Delta = (short) (y - (2 + Math.abs(x)));
                    ch2Delta = y;
                } else if (x > 1) {
                    ch1Delta = y;
                    ch2Delta = (short) (y - (2 + Math.abs(x)));
                } else {
                    ch1Delta = y;
                    ch2Delta = y;
                }
            }
        } else if (Math.abs(x) >= Math.abs(y)) {
            if (y < 0) {
                if (x < 0) {
                    ch1Delta = 1;
                    ch2Delta = y;
                } else if (x > 0) {
                    ch1Delta = y;
                    ch2Delta = 1;
                }
            } else if (y > 0) {
                if (x < 0) {
                    ch1Delta = -1;
                    ch2Delta = y;
                } else if (x > 0) {
                    ch1Delta = y;
                    ch2Delta = -1;
                }
            }
        }

        return new Pair<>((short)(ch1Base + ch1Delta), (short)(ch1Base + ch2Delta));
    }
}
