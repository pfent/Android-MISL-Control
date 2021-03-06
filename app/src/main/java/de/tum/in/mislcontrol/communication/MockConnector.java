package de.tum.in.mislcontrol.communication;

import android.os.Handler;

import de.tum.in.mislcontrol.communication.data.TelemetryPacket;
import de.tum.in.mislcontrol.controls.IInputController;
import de.tum.in.mislcontrol.math.MathHelper;
import de.tum.in.mislcontrol.math.Vector2D;

/**
 * Mock implementation of the connector, mainly for UI testing purposes.
 */
public class MockConnector implements IConnector {

    /**
     * The input controller.
     */
    IInputController inputController;

    /**
     * The test interval for events.
     */
    private static final long TEST_INTERVAL = 3000;

    /**
     * The mock location.
     */
    float latitude = 30.617326f, longitude = -96.341768f;

    /**
     * The first few packets from Hectors packetdump
     */
    private final byte[][] mockPackets = {
            {(byte) 0x02, (byte) 0x03, (byte) 0x00, (byte) 0x40,        //Version and size
                    (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x01, //Sequence count, note that this is not changing
                    (byte) 0x40, (byte) 0x48, (byte) 0x1f, (byte) 0xb2, //X-Euler
                    (byte) 0x3d, (byte) 0x3e, (byte) 0x58, (byte) 0xa9, //Y-Euler
                    (byte) 0x40, (byte) 0x34, (byte) 0x79, (byte) 0x55, //Z-Euler
                    (byte) 0x3d, (byte) 0x2c, (byte) 0x34, (byte) 0xae, //X-Accel
                    (byte) 0xbd, (byte) 0x09, (byte) 0xac, (byte) 0x00, //Y-Accel
                    (byte) 0x3f, (byte) 0x7f, (byte) 0xde, (byte) 0x9f, //Z-Accel
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, //Latitude
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, //Longitude
                    (byte) 0x30, (byte) 0x30, (byte) 0x31, (byte) 0x31, //Padding starts here
                    (byte) 0x32, (byte) 0x32, (byte) 0x33, (byte) 0x33,
                    (byte) 0x34, (byte) 0x34, (byte) 0x35, (byte) 0x35,
                    (byte) 0x36, (byte) 0x36, (byte) 0x37, (byte) 0x37,
                    (byte) 0x38, (byte) 0x38, (byte) 0x39, (byte) 0x39,
                    (byte) 0x3a, (byte) 0x3a, (byte) 0x3b, (byte) 0x3b},
            {(byte) 0x02, (byte) 0x03, (byte) 0x00, (byte) 0x40,
                    (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x01,
                    (byte) 0x40, (byte) 0x48, (byte) 0x20, (byte) 0xf8,
                    (byte) 0x3d, (byte) 0x3e, (byte) 0xd8, (byte) 0x1f,
                    (byte) 0x40, (byte) 0x34, (byte) 0x74, (byte) 0x7a,
                    (byte) 0x3d, (byte) 0x28, (byte) 0x71, (byte) 0xd2,
                    (byte) 0xbd, (byte) 0x0c, (byte) 0x11, (byte) 0xf5,
                    (byte) 0x3f, (byte) 0x7f, (byte) 0x44, (byte) 0xea,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x30, (byte) 0x30, (byte) 0x31, (byte) 0x31,
                    (byte) 0x32, (byte) 0x32, (byte) 0x33, (byte) 0x33,
                    (byte) 0x34, (byte) 0x34, (byte) 0x35, (byte) 0x35,
                    (byte) 0x36, (byte) 0x36, (byte) 0x37, (byte) 0x37,
                    (byte) 0x38, (byte) 0x38, (byte) 0x39, (byte) 0x39,
                    (byte) 0x3a, (byte) 0x3a, (byte) 0x3b, (byte) 0x3b},
            {(byte) 0x02, (byte) 0x03, (byte) 0x00, (byte) 0x40,
                    (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x01,
                    (byte) 0x40, (byte) 0x48, (byte) 0x1b, (byte) 0xe7,
                    (byte) 0x3d, (byte) 0x3e, (byte) 0xf5, (byte) 0x45,
                    (byte) 0x40, (byte) 0x34, (byte) 0x75, (byte) 0x4f,
                    (byte) 0x3d, (byte) 0x2c, (byte) 0xf8, (byte) 0xc2,
                    (byte) 0xbd, (byte) 0x0a, (byte) 0x16, (byte) 0xa4,
                    (byte) 0x3f, (byte) 0x7f, (byte) 0xc3, (byte) 0x67,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x30, (byte) 0x30, (byte) 0x31, (byte) 0x31,
                    (byte) 0x32, (byte) 0x32, (byte) 0x33, (byte) 0x33,
                    (byte) 0x34, (byte) 0x34, (byte) 0x35, (byte) 0x35,
                    (byte) 0x36, (byte) 0x36, (byte) 0x37, (byte) 0x37,
                    (byte) 0x38, (byte) 0x38, (byte) 0x39, (byte) 0x39,
                    (byte) 0x3a, (byte) 0x3a, (byte) 0x3b, (byte) 0x3b},
            {(byte) 0x02, (byte) 0x03, (byte) 0x00, (byte) 0x40,
                    (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x01,
                    (byte) 0x40, (byte) 0x48, (byte) 0x1c, (byte) 0x1e,
                    (byte) 0x3d, (byte) 0x3e, (byte) 0x97, (byte) 0xb0,
                    (byte) 0x40, (byte) 0x34, (byte) 0x73, (byte) 0x26,
                    (byte) 0x3d, (byte) 0x29, (byte) 0x06, (byte) 0xca,
                    (byte) 0xbd, (byte) 0x05, (byte) 0xbb, (byte) 0xd8,
                    (byte) 0x3f, (byte) 0x7f, (byte) 0xe9, (byte) 0xa2,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x30, (byte) 0x30, (byte) 0x31, (byte) 0x31,
                    (byte) 0x32, (byte) 0x32, (byte) 0x33, (byte) 0x33,
                    (byte) 0x34, (byte) 0x34, (byte) 0x35, (byte) 0x35,
                    (byte) 0x36, (byte) 0x36, (byte) 0x37, (byte) 0x37,
                    (byte) 0x38, (byte) 0x38, (byte) 0x39, (byte) 0x39,
                    (byte) 0x3a, (byte) 0x3a, (byte) 0x3b, (byte) 0x3b},
            {(byte) 0x02, (byte) 0x03, (byte) 0x00, (byte) 0x40,
                    (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x01,
                    (byte) 0x40, (byte) 0x48, (byte) 0x1d, (byte) 0xd4,
                    (byte) 0x3d, (byte) 0x3f, (byte) 0x66, (byte) 0xd3,
                    (byte) 0x40, (byte) 0x34, (byte) 0x75, (byte) 0xbf,
                    (byte) 0x3d, (byte) 0x26, (byte) 0xb3, (byte) 0xfa,
                    (byte) 0xbd, (byte) 0x07, (byte) 0x7b, (byte) 0xf1,
                    (byte) 0x3f, (byte) 0x7f, (byte) 0xc2, (byte) 0x3e,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x30, (byte) 0x30, (byte) 0x31, (byte) 0x31,
                    (byte) 0x32, (byte) 0x32, (byte) 0x33, (byte) 0x33,
                    (byte) 0x34, (byte) 0x34, (byte) 0x35, (byte) 0x35,
                    (byte) 0x36, (byte) 0x36, (byte) 0x37, (byte) 0x37,
                    (byte) 0x38, (byte) 0x38, (byte) 0x39, (byte) 0x39,
                    (byte) 0x3a, (byte) 0x3a, (byte) 0x3b, (byte) 0x3b}
    };

    /**
     * The rotation mock data (with several test cases).
     */
    private float[][] rotationMockData = {
            {0, 0, 0}, // --- SIGNAL TEST CASE START
            {-3.12f,-0.05f, -2f}, // FLAT
            { -2.9f, -0.37f, -2.0f }, // >> FRONT LEFT
            {-3.12f,-0.05f, -2f}, // FLAT
            { -2.8f, +0.18f, -1.94f }, // >> FRONT RIGHT
            {-3.12f,-0.05f, -2}, // FLAT
            { -2.6f, -0.16f, -1.94f }, // z=? // >> FRONT
            {-3.12f,-0.05f, -2}, // FLAT
            { 2.74f, -0.338f, -1.84f }, // >> BACK LEFT
            {-3.12f,-0.05f, -2}, // FLAT
            { 2.8f, +0.32f, -1.90f }, // >> BACK RIGHT
            {-3.12f,-0.05f, -2}, // FLAT
            { 2.71f, -0.02f, -1.93f }, // >> BACK

            {0, 0, 0}, // --- SIGNAL TEST CASE START
            {(float)Math.PI, 0, 0},
            {(float)Math.PI, 0.5f, 0},
            {(float)Math.PI, 0, 0},
            {(float)Math.PI + 0.5f, 0, 0},
            {(float)Math.PI, 0, 0},
            {(float)Math.PI, 0, MathHelper.PI_HALF},
            {(float)Math.PI, 0.5f, MathHelper.PI_HALF},
            {(float)Math.PI, 0, MathHelper.PI_HALF},
            {(float)Math.PI + 0.5f, 0, MathHelper.PI_HALF},
            {(float)Math.PI, 0, MathHelper.PI_HALF},
            {(float)Math.PI, 0, MathHelper.PI},
            {(float)Math.PI, 0.5f, MathHelper.PI},
            {(float)Math.PI, 0, MathHelper.PI},
            {(float)Math.PI + 0.5f, 0, MathHelper.PI},
            {(float)Math.PI, 0, MathHelper.PI},
            {(float)Math.PI, 0, MathHelper.PI + MathHelper.PI_HALF},
            {(float)Math.PI, 0.5f, MathHelper.PI + MathHelper.PI_HALF},
            {(float)Math.PI, 0, MathHelper.PI + MathHelper.PI_HALF},
            {(float)Math.PI + 0.5f, 0, MathHelper.PI + MathHelper.PI_HALF},
            {(float)Math.PI, 0, MathHelper.PI + MathHelper.PI_HALF},
            {(float)Math.PI, 0, MathHelper.PI + MathHelper.PI_HALF},
            {(float)Math.PI, 0.5f, MathHelper.PI + MathHelper.PI_HALF},
            {(float)Math.PI, 0, MathHelper.PI + MathHelper.PI_HALF},
            {(float)Math.PI, 0 + 0.5f, MathHelper.PI + MathHelper.PI_HALF},
            {(float)Math.PI, 0, MathHelper.PI + MathHelper.PI_HALF},

            { 0, 0, 0 }, // --- SIGNAL TEST CASE START
            { 1.0f, 0.0f, 0.0f },
            { 2.0f, 0.0f, 0.0f },
            { 3.0f, 0.0f, 0.0f },
            { 4.0f, 0.0f, 0.0f },
            { 5.0f, 0.0f, 0.0f },
            { 6.0f, 0.0f, 0.0f },
            { 0.0f, 0.0f, 0.0f },
            { 1.0f, 0.0f, 0.0f },
            { 2.0f, 0.0f, 0.0f },
            { 3.0f, 0.0f, 0.0f },
            { 4.0f, 0.0f, 0.0f },
            { 5.0f, 0.0f, 0.0f },
            { 6.0f, 0.0f, 0.0f },
            { 0.0f, 0.0f, 0.0f },
            { 0.0f, 1.0f, 0.0f },
            { 0.0f, 2.0f, 0.0f },
            { 0.0f, 3.0f, 0.0f },
            { 0.0f, 4.0f, 0.0f },
            { 0.0f, 5.0f, 0.0f },
            { 0.0f, 6.0f, 0.0f },
            { 0.0f, 0.0f, 0.0f },
            { 0.0f, 1.0f, 0.0f },
            { 0.0f, 2.0f, 0.0f },
            { 0.0f, 3.0f, 0.0f },
            { 0.0f, 4.0f, 0.0f },
            { 0.0f, 5.0f, 0.0f },
            { 0.0f, 6.0f, 0.0f },
            { 0.0f, 0.0f, 0.0f },
            { 0.0f, 0.0f, 6.0f },
            { 0.0f, 0.0f, 5.0f },
            { 0.0f, 0.0f, 4.0f },
            { 0.0f, 0.0f, 3.0f },
            { 0.0f, 0.0f, 2.0f },
            { 0.0f, 0.0f, 1.0f },
            { 0.0f, 0.0f, 0.0f },
            { 0.0f, 0.0f, 6.0f },
            { 0.0f, 0.0f, 5.0f },
            { 0.0f, 0.0f, 4.0f },
            { 0.0f, 0.0f, 3.0f },
            { 0.0f, 0.0f, 2.0f },
            { 0.0f, 0.0f, 1.0f },
            { 0.0f, 0.0f, 0.0f }
    };

    /**
     * The current mock rotation index.
     */
    private int currentRotationIndex;

    /**
     * The handler for repeated events.
     */
    private final Handler repeatHandler = new Handler();
    /**
     * The mock telemetry package to fire.
     */
    private final TelemetryPacket mockTelemetryPacket;
    /**
     * Thread safe flag variable to indicate the active state for event generation.
     */
    private volatile boolean isActive = false;
    /**
     * The telemetry received callback.
     */
    private OnTelemetryReceivedListener receiver;

    /**
     * The index for the mock packages.
     */
    private int mockPackageIndex = 0;

    /**
     * The repeat callback of the handler for repeated events.
     */
    private final Runnable repeatCallback = new Runnable() {
        @Override
        public void run() {
            if (isActive) {
                // fire event
                if (receiver != null) {
                    updateTelemetryPacket(mockTelemetryPacket);
                    receiver.onTelemetryReceived(mockTelemetryPacket);
                }

                repeatHandler.postDelayed(this, TEST_INTERVAL);
            }
        }

        /**
         * Slightly updates the telemetry packet to see some variatoin.
         * @param telemetryPacket The telemetry packet to update.
         */
        private void updateTelemetryPacket(TelemetryPacket telemetryPacket) {
            //Simulate Latitude / Longitude movement
            //Let Latitude equal x movement and Longitude y movement
            final float singleSpeed = (float) (360.0 / 6367444.0);
            Vector2D direction = inputController.getValue();
            latitude += singleSpeed * 6 * direction.getX();
            longitude += singleSpeed * 6 * direction.getY();

            mockPackageIndex++;
            //Return the same packet 40 times, which equals ~ 40*25ms = 1s
            byte[] mockPacket = mockPackets[(mockPackageIndex / 40) % mockPackets.length];
            System.arraycopy(mockPacket, 0, telemetryPacket.getData(), 0, mockPacket.length);

            // set test location
            telemetryPacket.setLatitude(latitude);
            telemetryPacket.setLongitude(longitude);

            // set test rotation
            telemetryPacket.setXEuler(rotationMockData[currentRotationIndex][0]);
            telemetryPacket.setYEuler(rotationMockData[currentRotationIndex][1]);
            telemetryPacket.setZEuler(rotationMockData[currentRotationIndex][2]);
            currentRotationIndex = (++currentRotationIndex) % rotationMockData.length;
        }
    };

    /**
     * Creates a mock connector instance.
     */
    public MockConnector() {
        mockTelemetryPacket = new TelemetryPacket();
    }

    @Override
    public void setCommand(short ch1, short ch2) {
        // NOP
    }

    @Override
    public void start() {
        isActive = true;
        repeatHandler.postDelayed(repeatCallback, DEFAULT_INTERVAL);
    }

    @Override
    public synchronized void stop() {
        if (!isActive)
            return;

        isActive = false;
        repeatHandler.removeCallbacks(repeatCallback);
    }

    @Override
    public boolean checkConnection() {
        return true;
    }

    @Override
    public void setOnTelemetryReceivedListener(OnTelemetryReceivedListener receiver) {
        this.receiver = receiver;
    }

    @Override
    public void setInputController(IInputController inputController) {
        this.inputController = inputController;
    }

    @Override
    public void close() {
        stop();
    }
}
