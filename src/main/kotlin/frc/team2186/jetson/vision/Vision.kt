package frc.team2186.jetson.vision

import edu.wpi.cscore.*
import org.opencv.core.Mat

object Vision {
    const val FPS = 30
    const val RESOLUTION_X = 320
    const val RESOLUTION_Y = 240

    init {
        Thread {
            run()
        }.start()
    }

    fun run() {
        val inputStream = MjpegServer("Input Stream", 1185)
        val outputStream = MjpegServer("Output Stream", 1186)
        val cam0 = initializeCamera(0)

        inputStream.source = cam0

        val sink = CvSink("From Camera")
        sink.source = cam0

        val source = CvSource("From OpenCV", VideoMode.PixelFormat.kMJPEG, RESOLUTION_X, RESOLUTION_Y, FPS)
        outputStream.source = source

        var img = Mat()

        while (Thread.interrupted().not()) {
            sink.grabFrame(img)
            source.putFrame(img)
        }
    }

    private fun initializeCamera(id: Int): UsbCamera {
        val ret = UsbCamera("Camera" + id, id)
        ret.setResolution(RESOLUTION_X, RESOLUTION_Y)
        ret.setFPS(FPS)

        return ret
    }
}