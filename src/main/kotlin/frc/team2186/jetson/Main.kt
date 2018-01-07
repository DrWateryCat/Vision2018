package frc.team2186.jetson

import edu.wpi.first.wpilibj.networktables.NetworkTable
import org.opencv.core.Core


object Main {
    init {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME)
    }

    @JvmStatic
    fun main(args: Array<String>) {
        //Initialize NetworkTables
        NetworkTable.setClientMode()
        NetworkTable.setTeam(2186)
        NetworkTable.initialize()


    }
}