package com.getbase.hackkrk.tanks;

import java.util.List;
import java.util.Random;

import com.getbase.hackkrk.tanks.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NaiveBot {
    private static final Logger log = LoggerFactory.getLogger(NaiveBot.class);
    private Random rand = new Random();

    public static void main(String... args) throws Exception {
        new NaiveBot().run();
    }

    public void run() throws Exception {
        TanksClient client = new TanksClient("http://10.12.202.141:9999/", "master", "AttentiveBeigeDogAardvark");
//        TanksClient client = new TanksClient("http://10.12.202.144:9999/", "sandbox-5", "AttentiveBeigeDogAardvark");

        while (true) {
            try {
                log.info("Waiting for the next game...");
                GameSetup gameSetup = client.getMyGameSetup();
                log.info("Playing {}", gameSetup);

                playGame(client);
            } catch (Exception e){

            }
        }
    }

    private void playGame(TanksClient client) {
        boolean gameFinished = false;
        TurnResult result = null;
        int roundNumber = 0;
        while (!gameFinished) {
            result = client.submitMove(generateCommand(result, ++roundNumber));
            gameFinished = result.last;
        }
    }

    public Command generateCommand(TurnResult result, int i) {

        if(result == null)
            return gulczasaStuff();
        else {
            if(i % 3 == 0)
                return Command.move(rand.nextDouble() > 0.5 ? -100 : 100);
            else
                return killThemAll(result);
//                return killThemAll(result);
        }
    }

    private Command killThemAll(TurnResult result) {
        Tank ownTank = selectOwnTank(result);
        Tank nearestTank = selectNearestTank(ownTank, result.tanks);

        int power = 100;

        double diffX = ownTank.position.get(0) - nearestTank.position.get(0);
        double diffY = ownTank.position.get(1) - nearestTank.position.get(1);

//        double angle = 90 - Math.atan(Math.abs(diffY) / Math.abs(diffX)) * 180 / Math.PI;
        int angle = rand.nextInt(90) - 45;

        double ratio = diffX/500;

//        int angle = Double.valueOf(45 * ratio).intValue();
//        int angle = 45;

        if(diffX*angle > 0)
            angle = -angle;

        return Command.fire(angle, power);


    }

    private Tank selectTank(TurnResult result) {
        for(Tank tank : result.tanks) {


            if(!tank.name.equals("ArrowTeam")) {
                return tank;
            }
        }
        return  null;
    }

    private Tank selectMyTank(List<Tank> allTanks) {
        for(Tank tank : allTanks) {
            if(tank.name.equals("ArrowTeam")) {
                return tank;
            }
        }

        return null;
    }

    private Tank selectNearestTank(Tank myTank, List<Tank> allTanks) {
        double minimumDistance = 1000.0;
        Tank nearestTank = null;
        for(Tank tank : allTanks) {
            if(tank.name.equals(myTank.name)) {
                continue;
            }

            double distance = getDistance(myTank, tank);
            if(distance < minimumDistance) {
                minimumDistance = distance;
                nearestTank = tank;
            }
        }

        return nearestTank;
    }

    private double getDistance(Tank myTank, Tank tank) {
        return Math.abs(myTank.position.getX() - tank.position.getX());
    }

    private Tank selectOwnTank(TurnResult result) {
        for(Tank tank : result.tanks) {
            if(tank.name.equals("ArrowTeam")) {
                return tank;
            }
        }
        return null;
    }
    private Command gulczasaStuff() {
        if (rand.nextDouble() > 0.2) {
            return Command.fire(rand.nextInt(90) - 45, rand.nextInt(100) + 30);
        } else {
            return Command.move(rand.nextDouble() > 0.5 ? -100 : 100);
        }
    }
}