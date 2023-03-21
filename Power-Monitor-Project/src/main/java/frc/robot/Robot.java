// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;
import edu.wpi.first.wpilibj.motorcontrol.Spark;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import edu.wpi.first.wpilibj.PowerDistribution;

/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  private static final String kDefaultAuto = "Default";
  private static final String kCustomAuto = "My Auto";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();

  PowerDistribution kPDP = new PowerDistribution(0, PowerDistribution.ModuleType.kCTRE);

  Spark driveMotor1 = new Spark(0);
  Spark driveMotor2 = new Spark(1);
  Spark driveMotor3 = new Spark(2);
  Spark driveMotor4 = new Spark(3);

  MotorControllerGroup leftDriveMotors = new MotorControllerGroup(driveMotor1, driveMotor2);
  MotorControllerGroup rightDriveMotors = new MotorControllerGroup(driveMotor3, driveMotor4);

  DifferentialDrive driveTrain = new DifferentialDrive(leftDriveMotors, rightDriveMotors);



  double[] kCurrent = {0.0,0.0,0.0,0.0}; double kVoltage; double kTemp; double kTotalCurrent; double kTotalPower; double kTotalEnergy;
  /**
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */
  @Override
  public void robotInit() {
    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    SmartDashboard.putData("Auto choices", m_chooser);
    SmartDashboard.putData("Test Date", m_chooser);
    SmartDashboard.putString("Test String", "Value");
  }

  /**
   * This function is called every 20 ms, no matter the mode. Use this for items like diagnostics
   * that you want ran during disabled, autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before LiveWindow and
   * SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {}

  /**
   * This autonomous (along with the chooser code above) shows how to select between different
   * autonomous modes using the dashboard. The sendable chooser code works with the Java
   * SmartDashboard. If you prefer the LabVIEW Dashboard, remove all of the chooser code and
   * uncomment the getString line to get the auto name from the text box below the Gyro
   *
   * <p>You can add additional auto modes by adding additional comparisons to the switch structure
   * below with additional strings. If using the SendableChooser make sure to add them to the
   * chooser code above as well.
   */
  @Override
  public void autonomousInit() {
    m_autoSelected = m_chooser.getSelected();
    // m_autoSelected = SmartDashboard.getString("Auto Selector", kDefaultAuto);
    System.out.println("Auto selected: " + m_autoSelected);
  }

  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() {
    switch (m_autoSelected) {
      case kCustomAuto:
        // Put custom auto code here
        break;
      case kDefaultAuto:
      default:
        // Put default auto code here
        break;
    }
  }

  /** This function is called once when teleop is enabled. */
  @Override
  public void teleopInit() {
    int j = 0;
    double[] voltsList = new double[10000];
    double meanVolts;
    double upperQuartileVolts;
    double lowerQuartileVolts;
    double lowVolts = 12;
    double highVolts = 0;
    double total = 0;

    do{
    kVoltage = kPDP.getVoltage();
    kTemp = kPDP.getTemperature();
    kTotalCurrent = kPDP.getTotalCurrent();
    kTotalEnergy = kPDP.getTotalEnergy();
    kTotalPower = kPDP.getTotalPower();
    
    for(int i = 0; i > 3; i++) {
      kCurrent[i] = kPDP.getCurrent(i);
      System.out.println("Current drawn by specific channel - Current Channel [" + i + "] : (" + kCurrent[i] + " A)");
    } 

    if(kVoltage > highVolts) {
      highVolts = kVoltage;
    } else if (kVoltage < lowVolts) {
      lowVolts = kVoltage;
    }

    voltsList[j] = kTotalCurrent;

    total = total + kVoltage;
    meanVolts = total / j;



    System.out.println("\nVoltage of battery to PDP : (" + kVoltage + " Volts)");
    System.out.println("\nTemperature of PDP : (" + kTemp + " Celsius)");
    System.out.println("\nTotal Current drawn by PDP : (" + kTotalCurrent + " A)");
    System.out.println("\nTotal Energy in Watts : (" + kTotalEnergy + " W)");
    System.out.println("\nTotal Joules : (" + kTotalPower + " J)");

    try {
      Thread.sleep(1000);
    } catch(InterruptedException e) {}
    System.out.printf("\033[H\033[2J");
    j++;
  }
    while(j != 10000);

    int k;
    for(k = 1; k < 1000; k++) {
      double l = voltsList[k];
      double m = voltsList[k - 1];
      if(m > l) {
        double temp = m;
        m = l;
        l = temp;
      }
    }
    
    upperQuartileVolts = voltsList[7500];
    lowerQuartileVolts = voltsList[2500];
    
    System.out.printf("Mean volts: %f\n", meanVolts);
    System.out.printf("Upper Quartile Volts: %f\n", upperQuartileVolts);
    System.out.printf("Lower Quartile volts: %f\n", lowerQuartileVolts);
    System.out.printf("High volts: %f\n", highVolts);
    System.out.printf("Low volts: %f\n", lowVolts);

  }

  /** This function is called periodically during operator control. */
  @Override
  public void teleopPeriodic() {
    
  }

  /** This function is called once when the robot is disabled. */
  @Override
  public void disabledInit() {}

  /** This function is called periodically when disabled. */
  @Override
  public void disabledPeriodic() {}

  /** This function is called once when test mode is enabled. */
  @Override
  public void testInit() {}

  /** This function is called periodically during test mode. */
  @Override
  public void testPeriodic() {}

  /** This function is called once when the robot is first started up. */
  @Override
  public void simulationInit() {}

  /** This function is called periodically whilst in simulation. */
  @Override
  public void simulationPeriodic() {}
}
