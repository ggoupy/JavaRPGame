package game.entity.system.enemy;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.utils.Array;
import game.entity.component.EnemySpawnComponent;
import game.entity.factory.EntityFactory;
import game.entity.utils.Spawn;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static game.entity.utils.Mappers.*;


public class EnemySpawnSystem extends IteratingSystem {

    private Random rand;
    private EntityFactory entityFactory;

    public EnemySpawnSystem(EntityFactory entityFactory)
    {
        super(Family.all(EnemySpawnComponent.class).get());
        this.entityFactory = entityFactory;
        this.rand = new Random();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime)
    {
        EnemySpawnComponent spawnCom = enemySpawnMapper.get(entity);

        /*Freeing available spawns*/
        for(Spawn s : spawnCom.spawns)
        {
            if (s.user != null) //for all spawn taken by an entity user
            {
                //if the entity does not have components
                //it means that it has been destroyed, so we free the spawn
                //CHANGE TO ENEMY COMPONENT TEST
                if (s.user.getComponents().size() == 0)
                {
                    s.user = null;
                    s.taken = false;
                }
            }
        }

        //if there is a spawn not taken
        if (spawnCom.nbEnemies() < spawnCom.nbSpawns())
        {
            if (spawnCom.RespawnTimer.update(deltaTime)) //we update the respawn time
            {
                //when the respawn time is reached, we create a new enemy in one of free spawns (random)
                //get a list of available spawn indexes
                Array<Integer> freeSpawnIndexes = getAvailableSpawns(spawnCom);
                //get a random index in the list
                int index = freeSpawnIndexes.get(rand.nextInt(freeSpawnIndexes.size));
                //create an enemy in the spawn 'entity' at the spawn[index]
                entityFactory.createEnemy(entity, index);
                //reset the spawn timer to allow another respawn
                spawnCom.RespawnTimer.reset();
            }
        }


    }

    //return indexes of available spawns in a spawn entity
    private Array<Integer> getAvailableSpawns(EnemySpawnComponent spawnCom)
    {
        Array<Integer> freeSpawnIndexes = new Array<>();
        for(int i=0; i<spawnCom.spawns.size; ++i)
        {
            if (!spawnCom.spawns.get(i).taken) freeSpawnIndexes.add(i);
        }
        return freeSpawnIndexes;
    }
}
