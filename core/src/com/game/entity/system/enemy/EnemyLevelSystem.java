package com.game.entity.system.enemy;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Vector3;
import com.game.utils.Constants;
import com.game.entity.component.*;
import com.game.loader.AssetsManager;

import static com.game.entity.utils.Mappers.*;


public class EnemyLevelSystem extends IteratingSystem {

    private Vector3 vectPos;
    private OrthographicCamera cameraUI;
    private OrthographicCamera cameraBox2D;
    private AssetsManager assetsManager;

    public EnemyLevelSystem(OrthographicCamera cameraBox2D, OrthographicCamera cameraUI, AssetsManager assetsManager) {
        super(Family.all(EnemyComponent.class).get());

        this.vectPos = new Vector3();
        this.cameraUI = cameraUI;
        this.cameraBox2D = cameraBox2D;
        this.assetsManager = assetsManager;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
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
        //Project delivered Y pos to upper corner, we will need to flip it
        fontCom.worldPos = cameraBox2D.project(vectPos.set(pos.position.x, pos.position.y+1f, 0).cpy());

        //To get text size
        GlyphLayout lvlText = new GlyphLayout();
        lvlText.setText(assetsManager.getFont(), fontCom.text);

        fontCom.worldPos.y = cameraUI.viewportHeight - fontCom.worldPos.y;
        fontCom.worldPos.x -= lvlText.width / 2;

        fontCom.screenPos = cameraUI.unproject(fontCom.worldPos);
    }
}
