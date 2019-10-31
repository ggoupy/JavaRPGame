package game.entity.utils;

import com.badlogic.ashley.core.ComponentMapper;
import game.entity.component.*;


public class Mappers {
    public static final ComponentMapper<AnimationComponent> animationMapper = ComponentMapper.getFor(AnimationComponent.class);
    public static final ComponentMapper<AttachedComponent> attachedMapper = ComponentMapper.getFor(AttachedComponent.class);
    public static final ComponentMapper<BodyComponent> bodyMapper = ComponentMapper.getFor(BodyComponent.class);
    public static final ComponentMapper<CollisionComponent> collisionMapper = ComponentMapper.getFor(CollisionComponent.class);
    public static final ComponentMapper<EnemyComponent> enemyMapper = ComponentMapper.getFor(EnemyComponent.class);
    public static final ComponentMapper<EnemyHealthComponent> enemyHealthMapper = ComponentMapper.getFor(EnemyHealthComponent.class);
    public static final ComponentMapper<PlayerComponent> playerMapper = ComponentMapper.getFor(PlayerComponent.class);
    public static final ComponentMapper<ReceiveAttackComponent> receiveAttackMapper = ComponentMapper.getFor(ReceiveAttackComponent.class);
    public static final ComponentMapper<StateComponent> stateMapper = ComponentMapper.getFor(StateComponent.class);
    public static final ComponentMapper<TextureComponent> textureMapper = ComponentMapper.getFor(TextureComponent.class);
    public static final ComponentMapper<TransformComponent> transformMapper = ComponentMapper.getFor(TransformComponent.class);
    public static final ComponentMapper<TypeComponent> typeMapper = ComponentMapper.getFor(TypeComponent.class);
}

