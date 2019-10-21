package game;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.ContactListener;
import game.entity.component.CollisionComponent;

public class CollisionListener implements ContactListener {

    public CollisionListener() {}

    @Override
    //detect a contact between 2 box2D
    public void beginContact(Contact contact)
    {
        //System.out.println("Contact");
        //Get the fixture (=physical properties) of boxes in contact
        Fixture fa = contact.getFixtureA(); //box A
        Fixture fb = contact.getFixtureB(); //box B
        //System.out.println(fa.getBody().getType()+" has hit "+ fb.getBody().getType());

        //Check for each box if it is an entity to create an entity collision
        if(fa.getBody().getUserData() instanceof Entity)
        {
            Entity ent = (Entity) fa.getBody().getUserData();
            entityCollision(ent,fb);
            return;
        }
        else if(fb.getBody().getUserData() instanceof Entity)
        {
            Entity ent = (Entity) fb.getBody().getUserData();
            entityCollision(ent,fa);
            return;
        }
    }

    //To store entity in collisionComponent of the other one
    private void entityCollision(Entity ent, Fixture fb)
    {
        //Check if the collided fixture is an entity
        if(fb.getBody().getUserData() instanceof Entity)
        {
            Entity colEnt = (Entity) fb.getBody().getUserData(); //get the entity from it's box2D

            CollisionComponent colA = ent.getComponent(CollisionComponent.class); //get entity collision component
            CollisionComponent colB = colEnt.getComponent(CollisionComponent.class); //get collided entity collision component

            //Store the entity B inside the collision component of the entity A if A has an collision component
            if(colA != null) colA.collisionEntity = colEnt;
            //Store the entity A inside the collision component of the entity B if B has an collision component
            if(colB != null) colB.collisionEntity = ent;
        }
    }

    @Override
    public void endContact(Contact contact) {}
    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {}
    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {}
}