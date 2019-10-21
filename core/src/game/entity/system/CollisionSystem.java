package game.entity.system;

import game.entity.component.CollisionComponent;
import game.entity.component.player.*;
import game.entity.component.TypeComponent;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

public class CollisionSystem  extends IteratingSystem {

    ComponentMapper<CollisionComponent> cm;
    ComponentMapper<PlayerComponent> pm;


    public CollisionSystem() {
        super(Family.all(CollisionComponent.class).get());
        cm = ComponentMapper.getFor(CollisionComponent.class);
        pm = ComponentMapper.getFor(PlayerComponent.class);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime)
    {
        //get the entity collision component
        CollisionComponent cc = cm.get(entity);
        //get the entity type component
        TypeComponent thisType = entity.getComponent(TypeComponent.class);

        //get collided entity
        Entity collidedEntity = cc.collisionEntity;

        // Do Player Collisions
        if(thisType.type == TypeComponent.PLAYER)
        {
            //if the player has a collision with an entity
            if(collidedEntity != null)
            {
                //get the collided entity type component
                TypeComponent type = collidedEntity.getComponent(TypeComponent.class);

                //if the the collided entity has a type
                if(type != null)
                {
                    switch(type.type)
                    {
                        case TypeComponent.ENEMY:
                            System.out.println("player hit enemy");
                            //Do things
                            break;

                        case TypeComponent.SCENERY:
                            System.out.println("player hit scenery");
                            break;

                        case TypeComponent.OTHER:
                            System.out.println("player hit other");
                            break;

                        default:
                            System.out.println("No matching type found");
                    }
                    //remove the collided entity stored in the collision component of the entity having a collision
                    cc.collisionEntity = null;
                }
                else System.out.println("type == null");
            }
        }
    }
}