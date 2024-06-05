package main.java.org.example;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import javafx.scene.control.Button;

import javafx.event.EventHandler;

import java.util.List;
import java.util.stream.Collectors;


public class SpaceInvadersApp extends Application {

    private Pane root = new Pane();
    private StackPane rootPane = new StackPane();
    private Pane labelPane = new Pane();
    private Stage stage;
    private Scene gameScene;
    private Scene mainMenuScene;
    private Scene retryScene;
    private Sprite player = new Sprite(300, 750, 40, 40, "player", Color.BLUE);
    private Label levelOut;
    private Label playerHealth;
    private Label playerPoints;
    private double t = 0;
    private int level = 1;
    private int enemy_level = 1;
    private double randomShoot = 0.15;
    private JSONHandler handledJSON = new JSONHandler("saves.json");
    private boolean finishedLevel = false;
    private boolean stageDone=false;

    public Scene getRetryScene()
    {
        return retryScene;
    }

    public Scene getGameScene()
    {
        return gameScene;
    }

    public Scene getMainMenuScene()
    {
        return mainMenuScene;
    }

    public Scene getActualScene()
    {
        return stage.getScene();
    }

    private Parent createContent() {
        JSONNeededData tmp = handledJSON.read();

        player.health = tmp.health;
        player.points = tmp.points;
        player.doubleShoot= tmp.doubleShoot;
        levelOut = new Label("Level " + level);
        playerPoints = new Label("Points: " + player.points);
        playerHealth = new Label("HP remaining: " + player.health);
        playerHealth.setTranslateX(200);
        playerPoints.setTranslateX(400);
        root.setPrefSize(600, 800);
        root.getChildren().add(player);
        labelPane.getChildren().add(levelOut);
        labelPane.getChildren().add(playerHealth);
        labelPane.getChildren().add(playerPoints);
        rootPane.getChildren().addAll(root, labelPane);
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();
            }
        };
        timer.start();
        nextLevel();
        return rootPane;
    }


    private void nextLevel() {

        levelOut.setText("Level " + level);
        sprites().forEach(s -> {
            if (s.type.contains("bullet")) {
                root.getChildren().remove(s);
            }
        });

        if (level % 5 == 0) {
            randomShoot /= level;
        }
        if (level % 5 == 0 && enemy_level < 5) {
            enemy_level++;
        }
        System.out.println(enemy_level);
        for (int i = 0; i < enemy_level; i++) {
            for (int j = 0; j < 5; j++) {
                Sprite s = new Sprite(90 + j * 100, 150 + i * 100, 30, 30, "enemy", Color.RED);

                root.getChildren().add(s);
            }
        }

    }

    private List<Sprite> sprites() {
        return root.getChildren().stream().map(n -> (Sprite) n).collect(Collectors.toList());
    }

    private void update() {
        t += 0.016; //update happens every 0.016 seconds
        sprites().forEach(s -> {
            switch (s.type) {
                case "enemybullet":
                    s.moveDown();
                    if (s.getBoundsInParent().intersects(player.getBoundsInParent())) {
                        if (player.health == 1) {
                            player.dead = true;
                            playerDead();
                        } else {
                            player.health--;
                            playerHealth.setText("HP remaining: " + player.health);

                        }
                        s.dead = true;
                    }
                    break;
                case "playerbullet":
                    s.moveUp();
                    sprites().stream().filter(e -> e.type.equals("enemy")).forEach(enemy ->
                    {
                        if (s.getBoundsInParent().intersects(enemy.getBoundsInParent())) {
                            enemy.dead = true;
                            s.dead = true;
                        }
                    });
                case "enemy":
                    if (t > 2) {

                        if (Math.random() < level * randomShoot) {
                            shoot(s);
                        }
                    }
                    break;


            }
        });

        root.getChildren().removeIf(n ->
        {
            Sprite s = (Sprite) n;
            return s.dead;
        });

        if (allEnemiesDead() && !player.dead) {
            level++;
            player.points += (int) Math.round(0.5 * level * 50);
            playerPoints.setText("Points: " + player.points);
            JSONNeededData tmp = new JSONNeededData(player.health, player.points, player.doubleShoot);
            handledJSON.write(tmp);
            nextLevel();
        }


        if (t > 2) {
            t = 0;
        }
    }

    private boolean allEnemiesDead() {
        for (Node node : root.getChildren()) {
            Sprite s = (Sprite) node;
            if (s.type.equals("enemy") && !s.dead) {
                return false;
            }
        }
        return true;
    }

    private void shoot(Sprite who) {
        if (who.type.equals("player") && who.doubleShoot) {
            Sprite s2 = new Sprite((int) who.getTranslateX() - 20, (int) who.getTranslateY(), 5, 20, who.type + "bullet", Color.BLACK);
            root.getChildren().add(s2);
        }
        Sprite s = new Sprite((int) who.getTranslateX() + 20, (int) who.getTranslateY(), 5, 20, who.type + "bullet", Color.BLACK);
        root.getChildren().add(s);
    }

    private void playerDead() {
        Pane retry_pane = new Pane();
        Button retry = new Button("Retry");
        Button exit = new Button("Exit");
        Button upgrade = new Button("Upgrade");
        Label tryre = new Label("You died, would you like to retry?");
        tryre.setTranslateY(200);
        tryre.setTranslateX(200);
        tryre.setPrefSize(200, 150);

        exit.setTranslateY(650);
        exit.setTranslateX(200);
        retry.setTranslateY(400);
        retry.setTranslateX(50);
        retry.setPrefSize(200, 100);
        exit.setPrefSize(200, 100);
        upgrade.setTranslateY(400);
        upgrade.setTranslateX(350);
        upgrade.setPrefSize(200, 100);

        retryScene = new Scene(retry_pane, 600, 800);

        retry_pane.getChildren().add(retry);
        retry_pane.getChildren().add(exit);
        retry_pane.getChildren().add(tryre);
        retry_pane.getChildren().add(upgrade);
        stage.setScene(retryScene);

        EventHandler<ActionEvent> eventUpgrade = new EventHandler<>() {
            @Override
            public void handle(ActionEvent event) {
                upgrade();
            }
        };

        upgrade.setOnAction(eventUpgrade);

        EventHandler<ActionEvent> event = new EventHandler<>() {

            public void handle(ActionEvent event) {

                retry();
                stage.setScene(gameScene);
            }
        };

        EventHandler<ActionEvent> eventExit = new EventHandler<>() {
            @Override
            public void handle(ActionEvent event) {
                System.exit(0);
            }
        };
        retry.setOnAction(event);
        exit.setOnAction(eventExit);


    }

    private void upgrade() {
        Pane upgradePane = new Pane();
        Label forHelp = new Label("Select what you want to upgrade:");
        Label points = new Label("Points: " + player.points);
        Button doubleShot = new Button("Press for double shooting, needed points: 10000");
        player.neededForUpgrade=player.health*300;
        Button upgradeHealth = new Button("Press for more health, needed points: "+player.neededForUpgrade);
        Button done = new Button("Done");
        upgradePane.getChildren().addAll(forHelp, points, doubleShot, upgradeHealth, done);
        doubleShot.setTranslateY(400);
        doubleShot.setTranslateX(350);
        upgradeHealth.setTranslateY(400);
        upgradeHealth.setTranslateX(50);
        doubleShot.setPrefSize(200, 100);
        upgradeHealth.setPrefSize(200, 100);
        done.setTranslateY(650);
        done.setTranslateX(200);
        done.setPrefSize(200, 100);
        forHelp.setTranslateY(200);
        forHelp.setTranslateX(200);
        forHelp.setPrefSize(200, 150);
        EventHandler<ActionEvent> eventDoubleShot = new EventHandler<>() {
            @Override
            public void handle(ActionEvent event) {
                if(player.points>=10000)
                {
                    if(!player.doubleShoot)
                    {
                        player.points-=10000;
                        playerPoints.setText("Points: "+player.points);
                        points.setText("Points: "+player.points);
                        player.doubleShoot = true;
                    }
                }

            }
        };

        EventHandler<ActionEvent> eventUpgradeHealth = new EventHandler<>() {
            @Override
            public void handle(ActionEvent event) {
                if(player.points>=player.neededForUpgrade)
                {
                    player.points-=player.neededForUpgrade;
                    player.neededForUpgrade=player.health*300;
                    upgradeHealth.setText("Press for more health, needed points: "+player.neededForUpgrade);
                    playerPoints.setText("Points: "+player.points);

                    points.setText("Points: "+player.points);
                    JSONNeededData tmp=handledJSON.read();
                    player.health=tmp.health+1;
                    playerHealth.setText("HP remaining: "+player.health);
                }

            }
        };

        EventHandler<ActionEvent> eventDone = new EventHandler<>() {
            @Override
            public void handle(ActionEvent event) {
                JSONNeededData tmp = new JSONNeededData(player.health, player.points, player.doubleShoot);
                handledJSON.write(tmp);

                retry();
                stage.setScene(gameScene);
            }
        };

        done.setOnAction(eventDone);
        upgradeHealth.setOnAction(eventUpgradeHealth);
        doubleShot.setOnAction(eventDoubleShot);

        Scene scene = new Scene(upgradePane, 600, 800);

        stage.setScene(scene);
    }

    private void retry() {
        JSONNeededData tmp = handledJSON.read();
        root.getChildren().clear();
        root.getChildren().add(player);
        player.health = tmp.health;
        randomShoot = 0.15;
        if (level % 5 == 0) {
            enemy_level = 0;
        } else {
            enemy_level = 1;
        }
        level = 1;
        nextLevel();
        player.dead = false;
    }

    private void setStageDone()
    {
        stageDone=true;
    }

    public Pane getUsedPane()
    {
        return (Pane) stage.getScene().getRoot();
    }

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        Pane mainMenu = new Pane();
        mainMenu.setPrefSize(600, 800);
        Label wannaPlay = new Label("If you'd like to play the game press start!");
        Button start = new Button("Start");
        Button exit = new Button("Exit");
        Button upgrade = new Button("Upgrade");
        mainMenu.getChildren().addAll(wannaPlay, start, exit,upgrade);
        exit.setTranslateY(650);
        exit.setTranslateX(200);
        start.setTranslateY(400);
        start.setTranslateX(50);
        start.setPrefSize(200, 100);
        exit.setPrefSize(200, 100);
        wannaPlay.setTranslateY(200);
        wannaPlay.setTranslateX(200);
        upgrade.setTranslateY(400);
        upgrade.setTranslateX(350);
        upgrade.setPrefSize(200, 100);

        EventHandler<ActionEvent> eventUpgrade=new EventHandler<>() {
            @Override
            public void handle(ActionEvent event) {

                if(!stageDone)
                {
                    setStageDone();
                    gameScene = new Scene(createContent());

                    stage.setTitle("SpaceInvaders");
                    gameScene.setOnKeyPressed(e -> {
                        switch (e.getCode()) {
                            case A:
                                player.moveLeft();
                                break;
                            case D:
                                player.moveRight();
                                break;
                            case SPACE:
                                shoot(player);
                                break;
                            case R:
                                try {
                                    retry();
                                } catch (Exception ex) {
                                    throw new RuntimeException(ex);
                                }
                                break;

                        }
                    });
                }
                upgrade();
            }
        };
        upgrade.setOnAction(eventUpgrade);
        EventHandler<ActionEvent> eventExit = new EventHandler<>() {
            @Override
            public void handle(ActionEvent event) {
                System.exit(0);

            }
        };
        exit.setOnAction(eventExit);

        EventHandler<ActionEvent> eventStart = new EventHandler<>() {
            @Override
            public void handle(ActionEvent event) {
                if(!stageDone)
                {
                    setStageDone();
                    gameScene = new Scene(createContent());

                    stage.setTitle("SpaceInvaders");
                    stage.setScene(gameScene);
                    gameScene.setOnKeyPressed(e -> {
                        switch (e.getCode()) {
                            case A:
                                player.moveLeft();
                                break;
                            case D:
                                player.moveRight();
                                break;
                            case SPACE:
                                shoot(player);
                                break;
                            case R:
                                try {
                                    retry();
                                } catch (Exception ex) {
                                    throw new RuntimeException(ex);
                                }
                                break;

                        }
                    });
                    stageDone=true;
                }else {
                    stage.setScene(gameScene);
                }

            }

        };
        start.setOnAction(eventStart);
        mainMenuScene = new Scene(mainMenu);

        stage.setScene(mainMenuScene);
        stage.show();


    }

    static class Sprite extends Rectangle {
        boolean dead = false;
        int health = 1;
        final String type;
        int points = 0;
        boolean doubleShoot = false;
        int neededForUpgrade=200;

        Sprite(int x, int y, int w, int h, String type, Color color) {

            super(w, h, color);
            if(type.equals("player"))
            {
                health=3;
            }
            this.type = type;
            setTranslateX(x);
            setTranslateY(y);
        }


        void moveLeft() {
            setTranslateX(getTranslateX() - 5);
        }

        void moveRight() {
            setTranslateX(getTranslateX() + 5);
        }

        void moveUp() {
            setTranslateY(getTranslateY() - 5);
        }

        void moveDown() {
            setTranslateY(getTranslateY() + 5);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}