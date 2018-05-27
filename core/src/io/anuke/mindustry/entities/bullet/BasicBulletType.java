package io.anuke.mindustry.entities.bullet;

import com.badlogic.gdx.graphics.Color;
import io.anuke.mindustry.content.bullets.TurretBullets;
import io.anuke.mindustry.graphics.Palette;
import io.anuke.ucore.util.Angles;
import io.anuke.ucore.util.Mathf;

/**A BulletType for most ammo-based bullets shot from turrets and units.*/
public abstract class BasicBulletType extends BulletType {
    public Color backColor = Palette.bulletYellowBack, frontColor = Palette.bulletYellow;
    public String bulletSprite = "bullet";
    public float bulletWidth = 5f, bulletHeight = 7f;

    public boolean frag;
    public int fragBullets = 9;
    public float fragVelocityMin = 0.2f, fragVelocityMax = 1f;
    public BulletType fragBullet = TurretBullets.basicLeadFrag;

    public BasicBulletType(float speed, float damage) {
        super(speed, damage);
    }

    @Override
    public void hit(Bullet b, float x, float y) {
        super.hit(b, x, y);

        if(frag) {
            for (int i = 0; i < fragBullets; i++) {
                float len = Mathf.random(1f, 7f);
                float a = Mathf.random(360f);
                Bullet bullet = Bullet.create(fragBullet, b,
                        x + Angles.trnsx(a, len), y + Angles.trnsy(a, len), a);
                bullet.velocity.scl(Mathf.random(fragVelocityMin, fragVelocityMax));
            }
        }
    }

    @Override
    public void despawned(Bullet b) {
        if(frag){
            hit(b);
        }
    }
}