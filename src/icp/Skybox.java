package icp;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;
import java.io.File;
import java.io.IOException;

/**
 *
 * @author Ales
 */
public class Skybox implements I_DrawableGrob {

    private double size;
    Texture front, back, left, right, down, up;

    /**
     * Skybox
     *
     * @param size
     * @param gl
     * @throws IOException V p��pad�, �e se nepoda�ilo na��st textury
     */
    public Skybox(double size, GL2 gl) throws IOException {
        this.size = size;
        front = TextureIO.newTexture(new File("C:\\Users\\Ales\\Documents\\NetBeansProjects\\ICP\\textures\\front.jpg"), true);
        back = TextureIO.newTexture(new File("C:\\Users\\Ales\\Documents\\NetBeansProjects\\ICP\\textures\\back.jpg"), true);
        left = TextureIO.newTexture(new File("C:\\Users\\Ales\\Documents\\NetBeansProjects\\ICP\\textures\\left.jpg"), true);
        right = TextureIO.newTexture(new File("C:\\Users\\Ales\\Documents\\NetBeansProjects\\ICP\\textures\\right.jpg"), true);
        down = TextureIO.newTexture(new File("C:\\Users\\Ales\\Documents\\NetBeansProjects\\ICP\\textures\\down.jpg"), true);
        up = TextureIO.newTexture(new File("C:\\Users\\Ales\\Documents\\NetBeansProjects\\ICP\\textures\\up.jpg"), true);
    }

    @Override
    public void draw(GL2 gl) {
        gl.glEnable(GL2.GL_TEXTURE_2D);
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, new float[]{1, 1, 1}, 0);

        // bottom
        down.enable(gl);
        down.bind(gl);
        gl.glBegin(GL2.GL_QUADS);
        gl.glTexCoord2d(0, 1);
        gl.glVertex3d(-(size / 2), -(size / 2), -(size / 2));
        gl.glTexCoord2d(1, 1);
        gl.glVertex3d(-(size / 2), -(size / 2), (size / 2));
        gl.glTexCoord2d(1, 0);
        gl.glVertex3d((size / 2), -(size / 2), (size / 2));
        gl.glTexCoord2d(0, 0);
        gl.glVertex3d((size / 2), -(size / 2), -(size / 2));
        gl.glEnd();
        down.disable(gl);

        // top
        up.enable(gl);
        up.bind(gl);
        gl.glBegin(GL2.GL_QUADS);
        gl.glTexCoord2d(0, 1);
        gl.glVertex3d(-(size / 2), (size / 2), -(size / 2));
        gl.glTexCoord2d(0, 0);
        gl.glVertex3d((size / 2), (size / 2), -(size / 2));
        gl.glTexCoord2d(1, 0);
        gl.glVertex3d((size / 2), (size / 2), (size / 2));
        gl.glTexCoord2d(1, 1);
        gl.glVertex3d(-(size / 2), (size / 2), (size / 2));
        gl.glEnd();
        up.disable(gl);

        // left
        left.enable(gl);
        left.bind(gl);
        gl.glBegin(GL2.GL_QUADS);
        gl.glTexCoord2d(1, 0);
        gl.glVertex3d(-(size / 2), -(size / 2), -(size / 2));
        gl.glTexCoord2d(1, 1);
        gl.glVertex3d(-(size / 2), (size / 2), -(size / 2));
        gl.glTexCoord2d(0, 1);
        gl.glVertex3d(-(size / 2), (size / 2), (size / 2));
        gl.glTexCoord2d(0, 0);
        gl.glVertex3d(-(size / 2), -(size / 2), (size / 2));
        gl.glEnd();
        left.disable(gl);

        // right
        right.enable(gl);
        right.bind(gl);
        gl.glBegin(GL2.GL_QUADS);
        gl.glTexCoord2d(0, 0);
        gl.glVertex3d((size / 2), -(size / 2), -(size / 2));
        gl.glTexCoord2d(1, 0);
        gl.glVertex3d((size / 2), -(size / 2), (size / 2));
        gl.glTexCoord2d(1, 1);
        gl.glVertex3d((size / 2), (size / 2), (size / 2));
        gl.glTexCoord2d(0, 1);
        gl.glVertex3d((size / 2), (size / 2), -(size / 2));
        gl.glEnd();
        right.disable(gl);

        // front
        front.enable(gl);
        front.bind(gl);
        gl.glBegin(GL2.GL_QUADS);
        gl.glTexCoord2d(1, 0);
        gl.glVertex3d(-(size / 2), -(size / 2), (size / 2));
        gl.glTexCoord2d(1, 1);
        gl.glVertex3d(-(size / 2), (size / 2), (size / 2));
        gl.glTexCoord2d(0, 1);
        gl.glVertex3d((size / 2), (size / 2), (size / 2));
        gl.glTexCoord2d(0, 0);
        gl.glVertex3d((size / 2), -(size / 2), (size / 2));
        gl.glEnd();
        front.disable(gl);

        // back
        back.enable(gl);
        back.bind(gl);
        gl.glBegin(GL2.GL_QUADS);
        gl.glTexCoord2d(0, 0);
        gl.glVertex3d(-(size / 2), -(size / 2), -(size / 2));
        gl.glTexCoord2d(1, 0);
        gl.glVertex3d((size / 2), -(size / 2), -(size / 2));
        gl.glTexCoord2d(1, 1);
        gl.glVertex3d((size / 2), (size / 2), -(size / 2));
        gl.glTexCoord2d(0, 1);
        gl.glVertex3d(-(size / 2), (size / 2), -(size / 2));
        gl.glEnd();
        back.disable(gl);
    }
}
