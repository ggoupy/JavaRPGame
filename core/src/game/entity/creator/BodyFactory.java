package game.entity.creator;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

import game.utils.Constants;


public class BodyFactory {

    //int number corresponding to a fixture type
    public static final int WOOD = 0;
    public static final int STONE = 1;
    public static final int GRASS = 2;
    public static final int HUMAN = 3;

    //the bodyFactory class will be created only one time
    //So we will store the instance in the class
    //static -> single copy of variable is created and shared among all objects at class level (=efficient memory)
    private static BodyFactory thisInstance = null;

    //reference to the game world
    private World world;


    //private constructor because it's singleton class
    private BodyFactory(World world){
        this.world = world;
    }

    //get the body factory instance and create it if not instanced
    //used to create the instance (like a constructor)
    public static BodyFactory getInstance(World world)
    {
        if(thisInstance == null) thisInstance = new BodyFactory(world);
        return thisInstance;
    }

    //Create a fixture according to real world materials
    //density = mass of the object (kg/meter)
    //friction = friction during a collision
    //restitution = how mich it will bounce after a collision
    public static FixtureDef makeFixture(int material, Shape shape)
    {
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;

        switch(material)
        {
            case WOOD:
                fixtureDef.density = 0f;
                fixtureDef.friction = 0f;
                fixtureDef.restitution = 0f;
                break;
            case STONE:
                fixtureDef.density = 0f;
                fixtureDef.friction = 0f;
                fixtureDef.restitution = 0f;
            case GRASS:
                fixtureDef.density = 0f;
                fixtureDef.friction = 0f;
                fixtureDef.restitution = 0f;
            default:
                fixtureDef.density = 0f;
                fixtureDef.friction = 0f;
                fixtureDef.restitution = 0f;
        }

        return fixtureDef;
    }

    //Util function to convert a rectangle into PolygonShape in the world
    //width & height divided per 2 because of centering process in Box2D
    public static PolygonShape getShapeFromRectangle(Rectangle rectangle)
    {
        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(rectangle.width*0.5F/ Constants.TILE_SIZE,rectangle.height*0.5F/ Constants.TILE_SIZE);
        return polygonShape;
    }

    //Util function to get center position of a rectangle in the world
    public static Vector2 getTransformedCenterForRectangle(Rectangle rectangle)
    {
        Vector2 center = new Vector2();
        rectangle.getCenter(center);
        return center.scl(1/Constants.TILE_SIZE);
    }

    //Create and return a polygon box2D in the world with a rectangle given and specifics attributes
    public Body makeBox(Rectangle r, BodyType type, int material)
    {
        //definition of the box
        BodyDef def = new BodyDef();
        def.type = type;
        def.fixedRotation = true; //always true in this case, might change in the future

        //we create a box in the world with it's definition
        Body box = world.createBody(def);

        //setting the position of the box's origin. In this case with zero rotation
        box.setTransform(getTransformedCenterForRectangle(r), 0);

        //create a body fixture (= physical properties) with the material of the box and it's shape
        box.createFixture(makeFixture(material, getShapeFromRectangle(r)));

        return box;
    }

    //Create and return a circle box2D in the world with given attributes
    public Body makeCircleBox(float x, float y, float radius, BodyType type, int material)
    {
        //definition of the box
        BodyDef boxBodyDef = new BodyDef();
        boxBodyDef.type = type;
        boxBodyDef.position.x = x;
        boxBodyDef.position.y = y;
        boxBodyDef.fixedRotation = true; //always true in this case, might change in the future

        //we create a box in the world with it's definition
        Body box = world.createBody(boxBodyDef);

        //shape of the box
        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(radius /2);

        //fixture of the box
        box.createFixture(makeFixture(material, circleShape));

        //we don't need the shape no more
        circleShape.dispose();

        return box;
    }
}
