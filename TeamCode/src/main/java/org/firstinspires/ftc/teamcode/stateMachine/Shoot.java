package org.firstinspires.ftc.teamcode.stateMachine;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

public class Shoot extends StateManager {
    public Shoot(double angle, double power){
        this.angle = angle;
        this.power = power;
    }
    double power;
    double angle;
    @Override
    public void Run(StateMachine machine) {
        double delay = 3;
        double time = 0.12;
        int maxRings = 4;
        int rings = maxRings;
        int[] ticks = {100-machine.opMode.Conveyor.getCurrentPosition(),200-machine.opMode.Conveyor.getCurrentPosition(),300-machine.opMode.Conveyor.getCurrentPosition(),400-machine.opMode.Conveyor.getCurrentPosition()};
        boolean updated = false;
        boolean shoot = false;
        double currentTime = machine.opMode.getRuntime();
        while(!ExitCondition(machine)&&machine.opMode.opModeIsActive()) {
            double leftFront = 0;
            double rightFront = 0;
            double leftBack = 0;
            double rightBack = 0;
            DcMotor left_front = machine.opMode.left_front;
            DcMotor left_back = machine.opMode.left_back;
            DcMotor right_front = machine.opMode.right_front;
            DcMotor right_back = machine.opMode.right_back;
            leftFront += -(machine.opMode.odometry.returnOrientation()-angle)/5;
            leftBack += -(machine.opMode.odometry.returnOrientation()-angle)/5;
            rightFront += (machine.opMode.odometry.returnOrientation()-angle)/5;
            rightBack += (machine.opMode.odometry.returnOrientation()-angle)/5;
            double scalar = Math.max(Math.max(Math.abs(leftFront), Math.abs(leftBack)), Math.max(Math.abs(rightFront), Math.abs(rightBack)));
            if (scalar < 1) scalar = 1;
            machine.updater.addTelemetry();
            left_front.setPower(leftFront / scalar * 0.3);
            left_back.setPower(leftBack / scalar * 0.3);
            right_front.setPower(rightFront / scalar * 0.3);
            right_back.setPower(rightBack / scalar * 0.3);
            machine.opMode.Shooter.setVelocity(-310, AngleUnit.DEGREES);
            if(machine.opMode.getRuntime()-currentTime>2.5){
                currentTime = machine.opMode.getRuntime();
                shoot = true;
            }
            if (machine.opMode.Shooter.getVelocity(AngleUnit.DEGREES)<-300) {
                int targetTicks = ticks[maxRings-rings];
                machine.opMode.Conveyor.setTargetPosition(-targetTicks);
                if(!updated){
                    rings--;
                    updated=true;
                }
            } else {
                updated = false;
                if(rings == 0){
                    break;
                }
            }
        }
        //machine.opMode.Shooter.setPower(0);
        machine.opMode.Shooter.setVelocity(0);
        Exit(machine);
    }
    @Override
    public boolean ExitCondition(StateMachine machine) {
        return machine.opMode.getRuntime()>25;
    }
    @Override
    public void Exit(StateMachine machine) {
        machine.runState(States.Rotate4);
    }
}