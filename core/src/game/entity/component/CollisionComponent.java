package game.entity.component;
import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;


//Stores collision data such as entity that this entity has collided with
public class CollisionComponent implements Component {
    public Array<Entity> collisionEntity = null;

    public void addCollidedEntity(Entity e) {collisionEntity.add(e);}
    public void removeCollidedEntity(Entity e) {collisionEntity.removeValue(e, false);}
    public boolean hasCollidedEntity(Entity e) {return collisionEntity.contains(e, false);}
    public void removeCollidedEntities()
    {
        for (Entity e : collisionEntity) removeCollidedEntity(e);
    }
}