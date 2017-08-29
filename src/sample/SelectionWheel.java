package sample;

import javafx.animation.Transition;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Arrays;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

/**
 * Created by David on 28/08/2017.
 */
public class SelectionWheel {
    private double HEIGHT;
    private double WIDTH;
    private double RADIUS = 200;
    private double SCROLL_SPEED = 0.2;

    private ObjectProperty<Arc> clickedArc = new SimpleObjectProperty<>();

    private Canvas canvas = new Canvas();
    private GraphicsContext gc = canvas.getGraphicsContext2D();
    private Pane mainPane = new Pane();
    private int divisions;

    private ArrayList<Arc> arcList = new ArrayList();
    private int selected = 0;
    private ArrayList<Color> arcColor = new ArrayList();
    private ArrayList<ImageView> imageList = new ArrayList<>();

    private Circle circle;

    public SelectionWheel(Pane parent, double width, double height, int divisions){
        this.divisions = divisions;
        this.HEIGHT = height;
        this.WIDTH = width;

        initiateListeners();
        createArcs();
        createImages();
        createCenterSelection();

        parent.getChildren().add(mainPane);
    }

    private void initiateListeners() {
        clickedArc.addListener((obs, oldSelection, newSelection) -> {
            if(newSelection != null){
                handleSelection(arcList.indexOf(newSelection));
            }
        });

//        mouseArc.addListener((obs, oldSelection, newSelection) -> {
//            expandArcRadius(arcList.indexOf(newSelection));
//        });

        mainPane.setOnScroll(event -> {
            rotateSelection(event.getDeltaY());
        });


    }

    private void rotateSelection(double deltaY) {
        //Adjust based on scroll up or down
        if(deltaY < 0){
            selected += 1;
        } else {
            selected -= 1;
        }

        //Handle the upper and lower bounds
        if(selected < 0){
            selected = arcList.size() - 1;
        }
        if(selected >= arcList.size()){
            selected = 0;
        }

        //Expand after changing selection
        expandArcRadius(selected);
        updateCircle(arcColor.get(selected));
    }

    private void createCenterSelection() {
        circle = new Circle();

        circle.setCenterX(WIDTH/2);
        circle.setCenterY(HEIGHT/2);
        circle.setRadius(RADIUS/3);
        circle.setFill(Color.WHITE);

        mainPane.getChildren().add(circle);

    }

    private void updateCircle(Color color){
        circle.setFill(color);
    }

    private void createArcs(){
        ArrayList<Color> colours = new ArrayList<>(Arrays.asList(Color.BLUE, Color.YELLOW, Color.GREEN, Color.PURPLE, Color.HOTPINK, Color.LIGHTBLUE));

        double arcExtent = 360/divisions;

        for (int i = 0; i < divisions; i++){
            gc.setFill(colours.get(i));
            double start = arcExtent * i;

            Arc arc = new Arc();
            arc.setCenterX(WIDTH/2);
            arc.setCenterY(HEIGHT/2);
            arc.setRadiusX(RADIUS);
            arc.setRadiusY(RADIUS);
            arc.setStartAngle(90 - start);
            arc.setLength(-arcExtent);
            arc.setType(ArcType.ROUND);
            arc.setFill(colours.get(i));


            arc.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                clickedArc.set(arc);
            });
//            arc.addEventHandler(MouseEvent.MOUSE_MOVED, e ->{
//                mouseArc.set(arc);
//            });

            System.out.println("Adding: "+ arc);
            arcList.add(arc);
            arcColor.add(colours.get(i));

            mainPane.getChildren().add(arc);
        }
    }

    private void createImages() {
        Image img = new Image("sample/block.png");

        double segment = 360/divisions;

        for (int i = 0; i < divisions; i++) {
            ImageView imgView = new ImageView(img);
            imgView.setFitHeight(50);
            imgView.setFitWidth(50);

            setImageViewCoords(i, imgView);

            imageList.add(imgView);
            mainPane.getChildren().add(imgView);
        }
    }



    private void handleSelection(int selection){
        System.out.println(selection);
        selected = selection;
        updateCircle(arcColor.get(selection));
        expandArcRadius(selection);
    }

    private void expandArcRadius(int selection) {
        for(Arc a : arcList){
            a.setRadiusX(RADIUS);
            a.setRadiusY(RADIUS);
        }

        Transition expand = new Transition() {
            {
                setCycleDuration(Duration.millis(25));
            }
            @Override
            protected void interpolate(double frac) {
                double newRadius = RADIUS + frac * (RADIUS+25 - RADIUS);
                arcList.get(selection).setRadiusX(newRadius);
                arcList.get(selection).setRadiusY(newRadius);
            }
        };
        expand.play();

        for(int i = 0; i < arcList.size(); i++){
            if(i != selection){
                arcList.get(i).setRadiusY(RADIUS);
                arcList.get(i).setRadiusX(RADIUS);
            }

        }

    }

    private void setImageViewCoords(int i, ImageView imgView){
        //Coords
        double cX = WIDTH/2;
        double cY = HEIGHT/2;

        double theta = - (arcList.get(i).getStartAngle() + arcList.get(i).getLength()/2);
        double rX = cX + RADIUS*(cos(theta*Math.PI/180));
        double rY = cY + RADIUS*(sin(theta*Math.PI/180));

        System.out.println(rX + " " + rY);

        double cSegX = (rX+cX)/2;
        double cSegY = (rY+cY)/2;

        //Can be used to change the pos of the image by editing the / 2
//        double cSegX = cX + ((rX - cX)/1.5);
//        double cSegY = cY + ((rY - cY)/1.5);

        imgView.setX(cSegX - (imgView.getFitWidth()/2));
        imgView.setY(cSegY - (imgView.getFitHeight()/2));
    }
}
