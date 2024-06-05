package main.java.org.example;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.Assertions.*;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;
import static org.testfx.api.FxAssert.verifyThat;

class SpaceInvadersAppTest extends ApplicationTest {

    SpaceInvadersApp app;
    @Before
    public void setUpClass() throws Exception
    {
        ApplicationTest.launch(SpaceInvadersApp.class);

    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.show();
    }

    @After
    public void afterEachTest() throws TimeoutException
    {
        FxToolkit.hideStage();
        release(new KeyCode[]{});
        release(new MouseButton[]{});
    }

    @Test
    public void goUpgrade()
    {
        clickOn("Upgrade");


    }

}