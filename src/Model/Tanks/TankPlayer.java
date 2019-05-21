package Model.Tanks;

import View.DataBaseConnector;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;

import java.util.List;

public class TankPlayer extends Tank{

    private Scene gameScene;

    private boolean isLeftKeyPressed;
    private boolean isRightKeyPressed;
    private boolean isUpKeyPressed;
    private boolean isDownKeyPressed;
    private boolean isShootKeyPressed;  //shoot key

    private KeyCode moveLeftKey;
    private KeyCode moveRightKey;
    private KeyCode moveUpKey;
    private KeyCode moveDownKey;
    private KeyCode shootKey;


    public TankPlayer(AnchorPane gamePane, Scene gameScene, int spawnPosArrayX, int spawnPosArrayY, String tankSpriteUrl, List<Tank> tankList, String[][] collisionMatrix,
                      KeyCode moveLeftKey, KeyCode moveRightKey, KeyCode moveUpKey, KeyCode moveDownKey, KeyCode shootKey, DataBaseConnector dbConnector) {
        super(gamePane, spawnPosArrayX, spawnPosArrayY, tankSpriteUrl, tankList, collisionMatrix, 5,dbConnector);
        this.gameScene = gameScene;
        this.moveLeftKey = moveLeftKey;
        this.moveRightKey = moveRightKey;
        this.moveUpKey = moveUpKey;
        this.moveDownKey = moveDownKey;
        this.shootKey = shootKey;
        createKeyListeners();
    }

    //Creating Listeners to inform which buttons are pressed - used to determine which animation is called
    private void createKeyListeners() {
        gameScene.setOnKeyPressed(event -> {    //lambda function to handle key pressing event
            if(event.getCode() == moveLeftKey) {
                isLeftKeyPressed = true;
            }
            else if (event.getCode() == moveRightKey) {
                isRightKeyPressed = true;

            }
            else if (event.getCode() == moveUpKey) {
                isUpKeyPressed = true;

            }
            else if (event.getCode() == moveDownKey) {
                isDownKeyPressed = true;

            }
            else if (event.getCode() == shootKey) {
                isShootKeyPressed = true;
            }
        });

        gameScene.setOnKeyReleased( event -> {
            if(event.getCode() == moveLeftKey) {
                isLeftKeyPressed = false;
            }
            else if (event.getCode() == moveRightKey) {
                isRightKeyPressed = false;
            }
            else if (event.getCode() == moveUpKey) {
                isUpKeyPressed = false;
            }
            else if (event.getCode() == moveDownKey) {
                isDownKeyPressed = false;
            }
            else if (event.getCode() == shootKey) {
                isShootKeyPressed = false;
            }
        });
    }

    //////////////////////////ANIMATIONS AND TANK MOTION////////////////////////////////////

    private void startTankMovement() {
        //Checking if only one key is pressed
        if(isLeftKeyPressed && !isRightKeyPressed && !isUpKeyPressed && !isDownKeyPressed) {
            if(angle == 90)
                fullSpin=true;
            else
                fullSpin=false;

            directionOfMovement = 'L';  //giving direction to continue movement
            allowedToMove = checkIfLeftEmpty();
            if (moveTankLeftOneIteration()) {
                positionMatrix[currentX-1][currentY]=Integer.toString(ID);
                positionMatrix[currentX][currentY]=null;
                currentX--;
            }
            moveIterator = BLOCK_SIZE/5 - 1;           //moveIterator is set to 9, so continueTankMovement will be called in next frame instead of startTankMovement
        }

        if(isRightKeyPressed && !isLeftKeyPressed && !isUpKeyPressed && !isDownKeyPressed) {
            if(angle == -90)
                fullSpin=true;
            else
                fullSpin=false;

            directionOfMovement = 'R';  //giving direction to continue movement
            allowedToMove = checkIfRightEmpty();
            if (moveTankRightOneIteration()) {
                positionMatrix[currentX+1][currentY]=Integer.toString(ID);
                positionMatrix[currentX][currentY]=null;
                currentX++;
            }
            moveIterator = BLOCK_SIZE/5 - 1;            //moveIterator is set to 9, so continueTankMovement will be called in next frame instead of startTankMovement
        }

        if(isUpKeyPressed && !isDownKeyPressed && !isLeftKeyPressed && !isRightKeyPressed) {
            if(angle == -180 || angle == 180)
                fullSpin=true;
            else
                fullSpin=false;

            directionOfMovement = 'U';  //giving direction to continue movement
            allowedToMove = checkIfUpEmpty();
            if (moveTankUpOneIteration()) {
                positionMatrix[currentX][currentY-1]=Integer.toString(ID);
                positionMatrix[currentX][currentY]=null;
                currentY--;
            }
            moveIterator = BLOCK_SIZE/5 - 1;           //moveIterator is set to 9, so continueTankMovement will be called in next frame instead of startTankMovement
        }

        if(isDownKeyPressed && !isUpKeyPressed && !isLeftKeyPressed && !isRightKeyPressed) {
            if(angle == 0)
                fullSpin=true;
            else
                fullSpin=false;

            directionOfMovement = 'D';  //giving direction to continue movement
            allowedToMove = checkIfDownEmpty();
            if (moveTankDownOneIteration()) {
                positionMatrix[currentX][currentY+1]=Integer.toString(ID);
                positionMatrix[currentX][currentY]=null;
                currentY++;
            }
            moveIterator = BLOCK_SIZE/5 - 1;           //moveIterator is set to 9, so continueTankMovement will be called in next frame instead of startTankMovement
        }
    }

    private void continueTankMovement() {   //function to continue movement started by pressing button, that make sure tank moves only by 50 pixels
        moveIterator--;

        if(directionOfMovement =='L')
            moveTankLeftOneIteration();

        if(directionOfMovement =='R')
            moveTankRightOneIteration();

        if(directionOfMovement == 'U')
            moveTankUpOneIteration();

        if(directionOfMovement == 'D')
            moveTankDownOneIteration();
    }

    public void moveTank() {
        if(moveIterator>0)
            continueTankMovement();
        else
            startTankMovement();
    }

    ///////////////////////////////////SHOOTING////////////////////////////
    public void moveProjectiles() {
        if(isShootKeyPressed && shootDelay==0)
            shoot();

        for (int x = 0; x<listOfActiveProjectiles.size(); x++) {
            listOfActiveProjectiles.get(x).moveProjectile();
            if( listOfActiveProjectiles.get(x).getHitConfirmed())
                listOfActiveProjectiles.remove(x);  //if projectile hit anything it is deleted
        }

        //delay to prevent spam shooting
        if(shootDelay==20)      // TODO fix shooting delay (not variable, maybe timer object that count cooldown)
            shootDelay=0;
        else
            shootDelay++;
    }

}
