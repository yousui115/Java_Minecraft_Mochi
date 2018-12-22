package objmodel;

import java.util.ArrayList;

import net.minecraftforge.fml.relauncher.Side;
//import net.minecraft.client.renderer.Tessellator;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GroupObject
{
    public String name;
    public ArrayList<Face> faces = new ArrayList<Face>();
    public int glDrawingMode;

    public GroupObject()
    {
        this("");
    }

    public GroupObject(String name)
    {
        this(name, -1);
    }

    public GroupObject(String name, int glDrawingMode)
    {
        this.name = name;
        this.glDrawingMode = glDrawingMode;
    }

    @SideOnly(Side.CLIENT)
    public void render()
    {
        if (faces.size() > 0)
        {
        	Tessellator2 tessellator = Tessellator2.instance;
            tessellator.startDrawing(glDrawingMode);
            render(tessellator);
            tessellator.draw();
        }
    }

    @SideOnly(Side.CLIENT)
    public void render(Tessellator2 tessellator)
    {
        if (faces.size() > 0)
        {
            for (Face face : faces)
            {
                face.addFaceForRender(tessellator);
            }
        }
    }


    //===================================================

    @SideOnly(Side.CLIENT)
    public void render(GroupObject targetIn, float rateIn)
    {
        if (faces.size() <= 0) { return; }

//        for (Face face : faces)
        for (int idx = 0; idx < faces.size(); idx++)
        {
            Face faceB = this.faces.get(idx);
            Face faceT = targetIn.faces.get(idx);

            Tessellator2 tessellator = Tessellator2.instance;
            tessellator.startDrawing(glDrawingMode);

//            render(tessellator);
            faceB.addFaceForRenderRate(tessellator, 0.0005F, faceT, rateIn);

            tessellator.draw();
        }
    }
}