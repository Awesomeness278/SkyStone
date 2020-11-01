package org.firstinspires.ftc.teamcode.Odometry;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

/**
* Created by Sarthak on 10/4/2019.
*/
@TeleOp(name = "My Odometry OpMode")
public class MyOdometryOpmode extends LinearOpMode {
    private static final double moveSpeed = 0.5;
    //private Servo[] conveyorBeltServos = new Servo[3];
   //Drive motors
   DcMotor right_front, right_back, left_front, left_back;
   //Odometry Wheels
   DcMotor verticalLeft, verticalRight, horizontal;

   final double COUNTS_PER_INCH = 307.699557;

   //Hardware Map Names for drive motors and odometry wheels. THIS WILL CHANGE ON EACH ROBOT, YOU NEED TO UPDATE THESE VALUES ACCORDINGLY
   String rfName = "Right Front Motor", rbName = "Right Back Motor", lfName = "Left Front Motor", lbName = "Left Back Motor";
   String verticalLeftEncoderName = rfName, verticalRightEncoderName = lfName, horizontalEncoderName = rbName;

   OdometryGlobalCoordinatePosition globalPositionUpdate;

   @Override
   public void runOpMode() throws InterruptedException {

       DcMotor leftFrontMotor = hardwareMap.get(DcMotor.class, "Left Front Motor");
       leftFrontMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
       DcMotor leftBackMotor = hardwareMap.get(DcMotor.class, "Left Back Motor");
       leftBackMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
       DcMotor rightFrontMotor = hardwareMap.get(DcMotor.class, "Right Front Motor");
       rightFrontMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
       DcMotor rightBackMotor = hardwareMap.get(DcMotor.class, "Right Back Motor");
       rightBackMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

       //Initialize hardware map values. PLEASE UPDATE THESE VALUES TO MATCH YOUR CONFIGURATION
       initDriveHardwareMap(rfName, rbName, lfName, lbName, verticalLeftEncoderName, verticalRightEncoderName, horizontalEncoderName);

       telemetry.addData("Status", "Init Complete");
       telemetry.update();
       waitForStart();

       //Create and start GlobalCoordinatePosition thread to constantly update the global coordinate positions
       globalPositionUpdate = new OdometryGlobalCoordinatePosition(verticalLeft, verticalRight, horizontal, COUNTS_PER_INCH, 75);
       Thread positionThread = new Thread(globalPositionUpdate);
       positionThread.start();

       globalPositionUpdate.reverseRightEncoder();
       globalPositionUpdate.reverseNormalEncoder();

       while(opModeIsActive()){
           //Display Global (x, y, theta) coordinates
           telemetry.addData("X Position", globalPositionUpdate.returnXCoordinate() / COUNTS_PER_INCH);
           telemetry.addData("Y Position", globalPositionUpdate.returnYCoordinate() / COUNTS_PER_INCH);
           telemetry.addData("Orientation (Degrees)", globalPositionUpdate.returnOrientation());

           telemetry.addData("Vertical left encoder position", verticalLeft.getCurrentPosition());
           telemetry.addData("Vertical right encoder position", verticalRight.getCurrentPosition());
           telemetry.addData("horizontal encoder position", horizontal.getCurrentPosition());

           telemetry.addData("Thread Active", positionThread.isAlive());
           telemetry.update();

           if (gamepad1.a) {
               leftFrontMotor.setPower(moveSpeed);
               leftBackMotor.setPower(0);
               rightFrontMotor.setPower(0);
               rightBackMotor.setPower(0);
           } else if (gamepad1.b) {
               leftFrontMotor.setPower(0);
               leftBackMotor.setPower(moveSpeed);
               rightFrontMotor.setPower(0);
               rightBackMotor.setPower(0);
           } else if (gamepad1.x) {
               leftFrontMotor.setPower(0);
               leftBackMotor.setPower(0);
               rightFrontMotor.setPower(moveSpeed);
               rightBackMotor.setPower(0);
           } else if (gamepad1.y) {
               leftFrontMotor.setPower(0);
               leftBackMotor.setPower(0);
               rightFrontMotor.setPower(0);
               rightBackMotor.setPower(moveSpeed);
           } else if (gamepad1.dpad_up) {
               leftFrontMotor.setPower(moveSpeed);
               leftBackMotor.setPower(moveSpeed);
               rightFrontMotor.setPower(moveSpeed);
               rightBackMotor.setPower(moveSpeed);
           } else if (gamepad1.dpad_right) {
               leftFrontMotor.setPower(moveSpeed);
               leftBackMotor.setPower(-moveSpeed);
               rightFrontMotor.setPower(-moveSpeed);
               rightBackMotor.setPower(moveSpeed);
           } else if (gamepad1.dpad_down) {
               leftFrontMotor.setPower(-moveSpeed);
               leftBackMotor.setPower(-moveSpeed);
               rightFrontMotor.setPower(-moveSpeed);
               rightBackMotor.setPower(-moveSpeed);
           } else if (gamepad1.dpad_left) {
               leftFrontMotor.setPower(-moveSpeed);
               leftBackMotor.setPower(moveSpeed);
               rightFrontMotor.setPower(moveSpeed);
               rightBackMotor.setPower(-moveSpeed);
           } else if (gamepad1.right_stick_x != 0) {
               leftFrontMotor.setPower(moveSpeed*gamepad1.right_stick_x);
               leftBackMotor.setPower(moveSpeed*gamepad1.right_stick_x);
               rightFrontMotor.setPower(-moveSpeed*gamepad1.right_stick_x);
               rightBackMotor.setPower(-moveSpeed*gamepad1.right_stick_x);
           } else {
               double scalar = Math.sqrt(Math.pow(gamepad1.left_stick_y,2)+Math.pow(gamepad1.left_stick_x,2))/(Math.abs(gamepad1.left_stick_y)+Math.abs(gamepad1.left_stick_x));
               leftFrontMotor.setPower((-gamepad1.left_stick_y+gamepad1.left_stick_x)*moveSpeed*scalar);
               leftBackMotor.setPower((-gamepad1.left_stick_y-gamepad1.left_stick_x)*moveSpeed*scalar);
               rightFrontMotor.setPower((-gamepad1.left_stick_y-gamepad1.left_stick_x)*moveSpeed*scalar);
               rightBackMotor.setPower((-gamepad1.left_stick_y+gamepad1.left_stick_x)*moveSpeed*scalar);
           }


       }

       //Stop the thread
       globalPositionUpdate.stop();

   }

   private void initDriveHardwareMap(String rfName, String rbName, String lfName, String lbName, String vlEncoderName, String vrEncoderName, String hEncoderName){
       right_front = hardwareMap.dcMotor.get(rfName);
       right_back = hardwareMap.dcMotor.get(rbName);
       left_front = hardwareMap.dcMotor.get(lfName);
       left_back = hardwareMap.dcMotor.get(lbName);

       verticalLeft = hardwareMap.dcMotor.get(vlEncoderName);
       verticalRight = hardwareMap.dcMotor.get(vrEncoderName);
       horizontal = hardwareMap.dcMotor.get(hEncoderName);

       right_front.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
       right_back.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
       left_front.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
       left_back.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

       right_front.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
       right_back.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
       left_front.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
       left_back.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

       verticalLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
       verticalRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
       horizontal.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

       verticalLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
       verticalRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
       horizontal.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);


       right_front.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
       right_back.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
       left_front.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
       left_back.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

       left_back.setDirection(DcMotorSimple.Direction.REVERSE);


       telemetry.addData("Status", "Hardware Map Init Complete");
       telemetry.update();
   }

   /**
    * Calculate the power in the x direction
    * @param desiredAngle angle on the x axis
    * @param speed robot's speed
    * @return the x vector
    */
   private double calculateX(double desiredAngle, double speed) {
       return Math.sin(Math.toRadians(desiredAngle)) * speed;
   }

   /**
    * Calculate the power in the y direction
    * @param desiredAngle angle on the y axis
    * @param speed robot's speed
    * @return the y vector
    */
   private double calculateY(double desiredAngle, double speed) {
       return Math.cos(Math.toRadians(desiredAngle)) * speed;
   }
}
