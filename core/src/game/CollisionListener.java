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
        //Get the fixture (=physical properties) of boxes in contact
        Fixture fa = contact.getFixtureA(); //box A
        Fixture fb = contact.getFixtureB(); //box B

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

    //To store collided entity in collisionComponent of the other one
    //~> Entity ent has a collision with fixture fb
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
    //handle the end of the contact between 2 entities
    public void endContact(Contact contact)
    {
        //Get the fixture (=physical properties) of boxes in end of contact
        Fixture fa = contact.getFixtureA(); //box A
        Fixture fb = contact.getFixtureB(); //box B

        //Check for each box if it is an entity to end entity collision
        if(fa.getBody().getUserData() instanceof Entity)
        {
            Entity ent = (Entity) fa.getBody().getUserData();
            entityEndCollision(ent,fb);
            return;
        }
        else if(fb.getBody().getUserData() instanceof Entity)
        {
            Entity ent = (Entity) fb.getBody().getUserData();
            entityEndCollision(ent,fa);
            return;
        }
    }

    //To remove the collided entity in collisionComponent of the other one
    //~> Entity ent has a collision with fixture fb
    private void entityEndCollision(Entity ent, Fixture fb)
    {
        //Check if the collided fixture is an entity
        if(fb.getBody().getUserData() instanceof Entity)
        {
            Entity colEnt = (Entity) fb.getBody().getUserData(); //get the entity from it's box2D

            CollisionComponent colA = ent.getComponent(CollisionComponent.class); //get entity collision component
            CollisionComponent colB = colEnt.getComponent(CollisionComponent.class); //get collided entity collision component

            //remove the entity B inside the collision component of the entity A if A has an collision component
            if(colA != null) colA.collisionEntity = null;
            //remove the entity A inside the collision component of the entity B if B has an collision component
            if(colB != null) colB.collisionEntity = null;
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {}
    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {}
}