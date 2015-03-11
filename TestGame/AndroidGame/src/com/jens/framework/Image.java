package com.jens.framework;

import com.jens.framework.Graphics.ImageFormat;

//Image interface make use of the ImageFormat from the Graphics interface
public interface Image {
    public int getWidth();
    public int getHeight();
    public ImageFormat getFormat();
    public void dispose();
}
