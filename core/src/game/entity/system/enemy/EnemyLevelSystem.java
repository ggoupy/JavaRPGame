package game.entity.system.enemy;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import game.entity.component.*;
import game.utils.Constants;

import static game.entity.utils.Mappers.*;


public class EnemyLevelSystem extends IteratingSystem {

    private Vector3 vectPos;
    private OrthographicCamera cameraUI;
    private OrthographicCamera cameraBox2D;

    public EnemyLevelSystem(OrthographicCamera cameraBox2D, OrthographicCamera cameraUI)
    {
        super(Family.all(EnemyComponent.class).get());

        this.vectPos = new Vector3();
        this.cameraUI = cameraUI;
        this.cameraBox2D = cameraBox2D;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime)
    {
        TransformComponent pos = transformMapper.get(entity);
        TextureComponent texture = textureMapper.get(entity);
        EnemyComponent enemy = enemyMapper.get(entity);
        FontComponent fontCom = fontMapper.get(entity);

        //Update the enemy level label
        fontCom.text = "Lv. " + enemy.level;


        //To display font component associated to the entity, we need to convert
        //its world position (metres) into screen position (pixels)
        //We get the world position with the world camera and position of the entity
        //we change the Y position of the world position to match with the head of the entity
        //finally we convert world position into screen position

        //cpy attributes a different vector for each entity
        fontCom.worldPos = cameraBox2D.project(vectPos.set(pos.position.x, pos.position.y, 0).cpy());
        float upPadd = texture.region.getRegionHeight() + Constants.TILE_SIZE;
        fontCom.worldPos.y = cameraUI.viewportHeight - fontCom.worldPos.y - upPadd;
        fontCom.worldPos.x -= texture.region.getRegionWidth()/2f;
        fontCom.screenPos = cameraUI.unproject(fontCom.worldPos);
    }
}
