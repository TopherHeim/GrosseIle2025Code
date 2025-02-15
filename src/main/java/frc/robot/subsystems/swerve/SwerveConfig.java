package frc.robot.subsystems.swerve;

//import com.ctre.phoenix6.signals.AbsoluteSensorRangeValue;
import com.ctre.phoenix6.signals.SensorDirectionValue;
//import com.ctre.phoenix6.sensors.AbsoluteSensorRange;
//import com.ctre.phoenix6.signals.;
import com.ctre.phoenix6.configs.CANcoderConfiguration;
//import com.ctre.phoenix6.signals.SensorInitializationStrategy;
//import com.ctre.phoenix6.sensors.SensorTimeBase;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;

import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.util.Units;
import frc.lib.util.swerveUtil.COTSNeoSwerveConstants;


public class SwerveConfig 
{
    
    public CANcoderConfiguration canCoderConfig;

    //
    public static final IdleMode driveIdleMode = IdleMode.kBrake; //kBrake
    public static final IdleMode angleIdleMode = IdleMode.kBrake;
    public static final double drivePower = 1;
    public static final double anglePower = .9;


    public static final boolean invertGyro = true; // Always ensure Gyro is CCW+ CW-

    public static final COTSNeoSwerveConstants chosenModule =  
        COTSNeoSwerveConstants.SDSMK4(COTSNeoSwerveConstants.driveGearRatios.SDSMK4_L2);

    /* Drivetrain Constants */
    public static final double trackWidth = Units.inchesToMeters(18.0); 
    public static final double wheelBase = Units.inchesToMeters(20.0); 
    public static final double wheelCircumference = chosenModule.wheelCircumference;


    /* Swerve Kinematics 
     * No need to ever change this unless you are not doing a traditional rectangular/square 4 module swerve */
     public static final SwerveDriveKinematics swerveKinematics = new SwerveDriveKinematics(
        new Translation2d(wheelBase / 2.0, trackWidth / 2.0),
        new Translation2d(wheelBase / 2.0, -trackWidth / 2.0),
        new Translation2d(-wheelBase / 2.0, trackWidth / 2.0),
        new Translation2d(-wheelBase / 2.0, -trackWidth / 2.0));


    /* Module Gear Ratios */
    public static final double driveGearRatio = chosenModule.driveGearRatio;
    public static final double angleGearRatio = chosenModule.angleGearRatio;

    // encoder setup
    // meters per rotation
    public static final double driveRevToMeters =  wheelCircumference / (driveGearRatio );
    public static final double driveRpmToMetersPerSecond = driveRevToMeters/60 ;
    // the number of degrees that a single rotation of the turn motor turns the wheel.
    public static final double DegreesPerTurnRotation = 360/angleGearRatio;

    
    /* Motor Inverts */
    public static final boolean angleMotorInvert = chosenModule.angleMotorInvert;
    public static final boolean driveMotorInvert = chosenModule.driveMotorInvert;

    /* Angle Encoder Invert */
    public static final boolean canCoderInvert = chosenModule.canCoderInvert;

    /* Swerve Current Limiting */
    public static final int angleContinuousCurrentLimit = 20;
    public static final int anglePeakCurrentLimit = 40;
    public static final double anglePeakCurrentDuration = 0.1;
    public static final boolean angleEnableCurrentLimit = true;

    public static final int driveContinuousCurrentLimit = 35;
    public static final int drivePeakCurrentLimit = 60;
    public static final double drivePeakCurrentDuration = 0.1;
    public static final boolean driveEnableCurrentLimit = true;

    /* These values are used by the drive falcon to ramp in open loop and closed loop driving.
     * We found a small open loop ramp (0.25) helps with tread wear, tipping, etc */
    public static final double openLoopRamp = 0.25;
    public static final double closedLoopRamp = 0.0;

    /* Angle Motor PID Values */
    public static final double angleKP = 0.05;
    public static final double angleKI = 0;
    public static final double angleKD = 0;
    public static final double angleKF = 0;

    /* Drive Motor PID Values */
    public static final double driveKP = 0.1; 
    public static final double driveKI = 0.0;
    public static final double driveKD = 0.0;
    public static final double driveKF = 0.0;

    /* Drive Motor Characterization Values 
     * Divide SYSID values by 12 to convert from volts to percent output for CTRE */
    public static final double driveKS = (0.32); 
    public static final double driveKV = (1.51);
    public static final double driveKA = (0.27);

    /* Swerve Profiling Values */
    /** Meters per Second */
    public static final double maxSpeed = 5.0;
    /** Radians per Second */
    public static final double maxAngularVelocity = 5.0; //max 10 or.....
   

 
 
   

    public SwerveConfig()
    {
        canCoderConfig = new CANcoderConfiguration();
        canCoderConfig.MagnetSensor.AbsoluteSensorDiscontinuityPoint =0.5; //0 to 360 deg
        canCoderConfig.MagnetSensor.SensorDirection = SensorDirectionValue.CounterClockwise_Positive;
        //canCoderConfig.MagnetSensor.initializationStrategy = SensorInitializationStrategy.BootToAbsolutePosition;
        //canCoderConfig.sensorTimeBase = SensorTimeBase.PerSecond;

    }
}

