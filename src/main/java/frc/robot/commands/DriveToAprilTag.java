// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import java.util.List;
import java.util.function.BooleanSupplier;

import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.path.GoalEndState;
import com.pathplanner.lib.path.PathPlannerPath;
import com.pathplanner.lib.path.Waypoint;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants;
import frc.robot.LimelightHelpers;
import frc.robot.Utility;
import frc.robot.subsystems.swerve.Swerve;

// Put command on a "whileTrue" button, otherwise driver will have no way to cancel the command
public class DriveToAprilTag extends Command {
  private final Swerve m_driveSubsystem;
  private final boolean m_leftSide; // Left or right side of the coral to go to
  
  private boolean m_foundTag;
  private Command m_path;
  private BooleanSupplier activate;
  // 0 is left side, 1 is right side
  public DriveToAprilTag(Swerve driveSubsystem, boolean leftSide, BooleanSupplier activate) {
    m_driveSubsystem = driveSubsystem;
    m_leftSide = leftSide;
    this.activate = activate;
    m_foundTag = false;

    addRequirements(driveSubsystem); // Prevents from driving while the path is active
  }

  // Checking for the AprilTag ID corresponding to the current team color
  public static boolean coralTagInView() {
    return (Utility.aprilTagInView() &&
      (
        (Utility.teamColorIsRed() && Utility.aprilTagIDIsInList(Constants.AprilTags.coralRedTags)) ||
        (Utility.aprilTagIDIsInList(Constants.AprilTags.coralBlueTags))
      )
    );
  }

  // Finds the field position of the robot facing the AprilTag, lined up to the coral.
  // MATHS DESMOS: https://www.desmos.com/calculator/uagr4pd9gv
  public static Pose2d findGoalPos(Pose2d robotPos, Pose2d aprilTagPos, boolean leftSide) {
    double robotRot = robotPos.getRotation().getRadians();
    double faceTagAngle = robotRot - aprilTagPos.getRotation().getRadians(); // robotRot - tagRot, finds angle to face the AprilTag
    
    // Calculate the AprilTag's position on the field
    Translation2d tagFieldPos = new Translation2d(
      // X = (tagX * cos(robotRot)) + (tagY * cos(robotRot - pi/2))
      (aprilTagPos.getX() * Math.cos(robotRot))  +  (aprilTagPos.getY() * Math.cos(robotRot - (Math.PI / 2))),
      (aprilTagPos.getX() * Math.sin(robotRot))  +  (aprilTagPos.getY() * Math.sin(robotRot - (Math.PI / 2)))
    );

    double offsetHoriz = Constants.AprilTags.coralOffset.getX();
    double offsetOut = Constants.AprilTags.coralOffset.getY();

    if (leftSide) {
      offsetHoriz *= -1; // Flip to other side of the AprilTag
    }

    // Add offsets to find the position of the robot on the field next to the AprilTag
    Translation2d finalGoalPos = new Translation2d(
      // X = tagFieldX + (offsetX * Math.cos(faceAngle - PI/2)) + (offsetY * Math.cos(faceAngle - PI/2))
      tagFieldPos.getX()  +  (offsetHoriz * Math.cos(faceTagAngle - (Math.PI / 2)))  +  (offsetOut * Math.cos(faceTagAngle - Math.PI)),
      tagFieldPos.getY()  +  (offsetHoriz * Math.sin(faceTagAngle - (Math.PI / 2)))  +  (offsetOut * Math.sin(faceTagAngle - Math.PI))
    );

    return new Pose2d(finalGoalPos, Rotation2d.fromRadians(faceTagAngle));
  }

  @Override
  public void execute() {
    LimelightHelpers.PoseEstimate mt2 = LimelightHelpers.getBotPoseEstimate_wpiBlue_MegaTag2("limelight");
      if (!m_foundTag) {
          if (!(mt2.tagCount == 0)) {
                System.out.println("aaaaaaaaaaaaa");
              m_foundTag = true;
  
              Pose2d currentRobotPose = m_driveSubsystem.getAprilOdom();
              Pose3d tagBotSpace = Utility.getTagPoseRelativeToBot();
  
              // Convert AprilTag Pose3d to Pose2d
              Pose2d aprilTagPose = new Pose2d(tagBotSpace.getZ(), tagBotSpace.getX(), Rotation2d.fromRadians(tagBotSpace.getRotation().getY()));
  
              // Calculate goal pose
              Pose2d goalPos = findGoalPos(currentRobotPose, aprilTagPose, m_leftSide);
  
              // Create path from current robot position to the new position
              List<Waypoint> waypoints = PathPlannerPath.waypointsFromPoses(currentRobotPose, goalPos);
  
              PathPlannerPath path = new PathPlannerPath(
                  waypoints,
                  Constants.AprilTags.constraints,
                  null,
                  new GoalEndState(0, goalPos.getRotation())
              );
  
              path.preventFlipping = true;
              m_path = AutoBuilder.followPath(path);
              
              // Initialize the path command
              if (m_path != null) {
                  m_path.initialize();
                  System.out.println("Path initialized!");
              }
          }
      } 
      if (m_path != null) {
          m_path.schedule();
      }
  }
  @Override
  public void end(boolean interrupted) {
    if (m_foundTag && m_path != null) {
        m_path.end(interrupted);
        System.out.println("Path ended, interrupted: " + interrupted);
    }
}

  @Override
  public boolean isFinished() {
    return m_foundTag && m_path != null && m_path.isFinished();
  }
}