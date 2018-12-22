package yousui115.mochi.util;

import objmodel.IModelCustom;

public class MUtils
{
    public static void drawOBJ(IModelCustom baseIn, IModelCustom targetIn, float rateIn)
    {
        //■
        if (baseIn == null || targetIn == null ||
            baseIn.getType().compareTo(targetIn.getType()) != 0)
        {
            return;
        }

        //■
        baseIn.renderTrans(targetIn, rateIn);

    }
}
