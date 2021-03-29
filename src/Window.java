import java.nio.IntBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.*;

import javax.swing.*;

public class Window  {
    private String title;
    public long id;
    private int width;
    private int height;
    public IntBuffer bufferedWidth;
    public IntBuffer bufferedHeight;
    private GLFWVidMode videoMode;
    public static Window instance;

    public Window(String title, int width, int height) {
        instance = this;
        this.title = title;
        this.width = width;
        this.height = height;
    }

    public void create()
    {
        if(!GLFW.glfwInit())
        {
            System.err.println("GLFW error.");
            System.exit(-1);
        }

        this.id = GLFW.glfwCreateWindow(this.width, this.height, this.title,
                0, 0);

        if(this.id == 0)
        {
            System.err.println("Window not created.");
            System.exit(-1);
        }

        try
        {
            this.bufferedWidth = BufferUtils.createIntBuffer(1);

            this.bufferedHeight = BufferUtils.createIntBuffer(1);
            GLFW.glfwGetWindowSize(this.id, this.bufferedWidth, this.bufferedHeight);
        }
        catch (Exception e)
        {
            System.err.println(e.toString());
        }

        this.videoMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());

        GLFW.glfwSetWindowTitle(this.id, this.title);
        GLFW.glfwSetWindowSize(this.id, this.width, this.height);
        GLFW.glfwSetWindowAspectRatio(this.id, this.width, this.height);
        GLFW.glfwSetWindowPos(this.id,
                ((this.videoMode.width() - this.bufferedWidth.get(0)) / 2),
                (this.videoMode.height() - this.bufferedHeight.get(0)) / 2);
        GLFW.glfwSetWindowSizeLimits(this.id, this.width, this.height, 1920, 1080);

        GLFW.glfwMakeContextCurrent(this.id);
        GL.createCapabilities();
        GL11.glViewport(0, 0, this.bufferedWidth.get(0), this.bufferedHeight.get(0));
    }

    public void update()
    {
        GLFW.glfwPollEvents();
        GLFW.glfwSwapBuffers(this.id);
    }

    public void destroy()
    {
        GLFW.glfwDestroyWindow(this.id);
        System.exit(0);
    }

    public boolean isCloseRequest()
    {
        return GLFW.glfwWindowShouldClose(this.id);
    }

    public int getWidth()
    {
        return width;
    }

    public void setWidth(int width)
    {
        this.width = width;
    }

    public int getHeight()
    {
        return height;
    }

    public void setHeight(int height)
    {
        this.height = height;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public static Window getWindow()
    {
        return instance;
    }

    public static void main(String[] args) throws InterruptedException {
        Object cube = new Object((byte)4, (byte)138, (byte)68, (byte)2, new Vector(0,0,0), new Vector(1.5,2.5,1.3), 2);
        Object cil = new Object((byte)9, (byte)25, (byte)5, (byte)255, new Vector(0,-2.6,-1.3), new Vector(0,2.6,-1.3), 0.8);
        Object cube2 = new Object((byte)6, (byte)138, (byte)68, (byte)2, new Vector(0,0,-0.8), new Vector(1.6,0.8,1.4), 2);
        Object cube3 = new Object((byte)6, (byte)138, (byte)68, (byte)2, new Vector(0,-2.3,-0.8), new Vector(1.6,0.8,1.4), 2);
        Object cube4 = new Object((byte)6, (byte)138, (byte)68, (byte)2, new Vector(0,2.3,-0.8), new Vector(1.6,0.8,1.4), 2);
        Object.LoadObjects();

        ControlWindow control = new ControlWindow("Add elements...");

        control.setVisible(true);
        control.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        control.setSize(315,400);
        control.setResizable(false);

        Camera c = new Camera();
        Window w = new Window("JavaRender", 800, 600);
        c.bindDisplay(w);
    }
}
