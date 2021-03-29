import jcuda.Pointer;
import jcuda.driver.*;
import jcuda.runtime.JCuda;
import jcuda.runtime.cudaDeviceProp;
import jcuda.runtime.cudaGraphicsResource;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import java.awt.*;
import java.awt.image.BufferedImage;

import static jcuda.driver.JCudaDriver.*;
import static jcuda.runtime.JCuda.*;
import static jcuda.runtime.cudaMemcpyKind.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL21.GL_PIXEL_PACK_BUFFER;
import static org.lwjgl.opengl.GL21.GL_PIXEL_UNPACK_BUFFER;
import javax.imageio.ImageIO;
import java.io.File;
class Keyboard
{
    private static boolean[] keys = new boolean[GLFW.GLFW_KEY_LAST];

    public static boolean keyDown(int keyId)
    {
        return GLFW.glfwGetKey(Window.getWindow().id, keyId) == 1;
    }

    public static boolean keyPreesed(int keyId)
    {
        return keyDown(keyId) && !keys[keyId];
    }

    public static boolean keyReleased(int keyId)
    {
        return !keyDown(keyId) && keys[keyId];
    }

    public static void handleKeyboardInput()
    {
        for (int i = 0; i < GLFW.GLFW_KEY_LAST; i++)
        {
            keys[i] = keyDown(i);
        }
    }
}

public class Camera {
    private Vector position;
    private CUdeviceptr deviceOutput, deviceInput;
    private Window Display;
    private byte[] byteImage;
    private int width;
    private int height;
    private int textureID, bufferID;
    public cudaGraphicsResource resource;
    public float verticalAngle = 0, horizontalAngle = 0;
    public float lightIntensity = 1, lightSize = (float) 0.2;

    Camera(){


        JCudaDriver.setExceptionsEnabled(true);
        cuInit(0);

        CUdevice device = new CUdevice();
        cuDeviceGet(device, 0);

        CUcontext context = new CUcontext();
        cuCtxCreate(context, 0, device);

        byteImage = new byte[width * height * 3];
        deviceOutput = new CUdeviceptr();
        position = new Vector(-2,0,0);


        deviceInput = Object.getPointer();
    }

    public void finalize() {
        thread1.stop();
    }

    public void bindDisplay(Window display){
        this.Display = display;
        this.Display.create();

        width = Display.getWidth();
        height = Display.getHeight();

        this.init();
        this.update();
    }

    private void render(int width, int height){
        CUmodule module = new CUmodule();
        CUfunction function = new CUfunction();
        JCuda.cudaMalloc(deviceOutput, width * height * 3);

        cuModuleLoad(module, "kernels/shader.ptx");
        cuModuleGetFunction(function, module, "draw");

        Pointer kernelParameters = Pointer.to(
                Pointer.to(deviceOutput),
                Pointer.to(new int[]{width}),
                Pointer.to(new int[]{height}),
                Pointer.to(new double[]{position.x}),
                Pointer.to(new double[]{position.y}),
                Pointer.to(new double[]{position.z}),
                Pointer.to(new double[]{lightIntensity}),
                Pointer.to(new double[]{lightSize}),
                Pointer.to(new double[]{lightIntensity}),
                Pointer.to(new double[]{lightSize}),
                Pointer.to(new int[]{0}),
                Pointer.to(new int[]{1})
        );

        cuLaunchKernel(function,
                width/32,  height/32, 1,      // Grid dimension
                32, 32, 1,      // Block dimension
                0, null,               // Shared memory size and stream
                kernelParameters, null // Kernel- and extra parameters
        );

        Pointer p = new Pointer();
        JCuda.cudaGLMapBufferObject(p, 123);

        cuCtxSynchronize();

        JCuda.cudaMemcpy(Pointer.to(byteImage), deviceOutput, width * height * 3, cudaMemcpyDeviceToHost);
        JCuda.cudaFree(deviceOutput);
    }

    CUfunction function;

    private void initCuda()
    {
        JCudaDriver.setExceptionsEnabled(true);

        cuInit(0);
        CUdevice device = new CUdevice();
        cuDeviceGet(device, 0);
        CUcontext context = new CUcontext();
        cuCtxCreate(context, 0, device);
    }

    Thread thread1, thread2, thread3, thread4, thread5, thread6;

    class cuThread implements Runnable{
        private int id;

        cuThread(int id){
            this.id = id;
        }

        @Override
        public void run() {
            System.out.println("cu"+Thread.currentThread().getName() + " is running.");
            JCudaDriver.setExceptionsEnabled(true);

            cuInit(0);
            CUdevice device = new CUdevice();
            cuDeviceGet(device, 0);
            CUcontext context = new CUcontext();
            cuCtxCreate(context, 0, device);

            CUmodule module = new CUmodule();
            CUfunction function = new CUfunction();

            cuModuleLoad(module, "kernels/shader.ptx");
            cuModuleGetFunction(function, module, "draw");

            while (true){
                cuCtxSynchronize();
                Pointer kernelParameters = Pointer.to(
                        Pointer.to(deviceOutput),
                        Pointer.to(deviceInput),
                        Pointer.to(new int[]{Object.getSize()}),
                        Pointer.to(new int[]{width}),
                        Pointer.to(new int[]{height}),
                        Pointer.to(new double[]{position.x}),
                        Pointer.to(new double[]{position.y}),
                        Pointer.to(new double[]{position.z}),
                        Pointer.to(new float[]{verticalAngle}),
                        Pointer.to(new float[]{horizontalAngle}),
                        Pointer.to(new float[]{lightIntensity}),
                        Pointer.to(new float[]{lightSize}),
                        Pointer.to(new int[]{this.id}),
                        Pointer.to(new int[]{6})
                );

                cuLaunchKernel(function,
                        width/32,  6, 1,      // Grid dimension
                        32, 32, 1,      // Block dimension
                        0, null,               // Shared memory size and stream
                        kernelParameters, null // Kernel- and extra parameters
                );

                cuCtxSynchronize();
            }

        }
    }

    public void init(){
        this.initCuda();
        glClearColor(0, 0, 0, 1);
        glClear(GL11.GL_COLOR_BUFFER_BIT);

        cudaDeviceProp prop = new cudaDeviceProp();
        cudaGetDeviceProperties(prop, 0);

        String str = new String(prop.name);

        System.out.println(str);

        bufferID = glGenBuffers();

        GL30.glBindBuffer(GL_ARRAY_BUFFER, bufferID);
        GL30.glBufferData(GL_ARRAY_BUFFER, width * height * 3,  GL_STREAM_DRAW);

        textureID = glGenTextures();

        glBindTexture( GL_TEXTURE_2D, textureID);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        glTexImage2D( GL_TEXTURE_2D, 0, GL_RGB, width, height, 0, GL_RGB,
                GL_UNSIGNED_BYTE, 0);
        glBindTexture(GL_TEXTURE_2D, 0);


        glEnable(GL_TEXTURE_2D);
        resource = new cudaGraphicsResource();
        JCuda.cudaGraphicsGLRegisterBuffer(resource, bufferID, CUgraphicsMapResourceFlags.CU_GRAPHICS_MAP_RESOURCE_FLAGS_NONE);

        cudaGraphicsMapResources(1, new cudaGraphicsResource[]{resource}, null);
        cudaGraphicsResourceGetMappedPointer(deviceOutput, new long[1], resource);
        cuThread t1 = new cuThread(0);
        cuThread t2 = new cuThread(1);
        cuThread t3 = new cuThread(2);
        cuThread t4 = new cuThread(3);
        cuThread t5 = new cuThread(4);
        cuThread t6 = new cuThread(5);

        thread1 = new Thread(t1);
        thread1.setPriority(9);
        thread2 = new Thread(t2);
        thread2.setPriority(9);
        thread3 = new Thread(t3);
        thread3.setPriority(9);
        thread4 = new Thread(t4);
        thread4.setPriority(9);
        thread5 = new Thread(t5);
        thread5.setPriority(9);
        thread6 = new Thread(t6);
        thread6.setPriority(9);

        thread1.start();thread2.start();thread3.start();thread4.start();thread5.start();thread6.start();
    }

    int step = 0;
    long prevTimeNS = -1;

    private void cuRender(){
        cuCtxSynchronize();
        CUdeviceptr devOutput = new CUdeviceptr();

        cudaGraphicsMapResources(1, new cudaGraphicsResource[]{resource}, null);
        cudaGraphicsResourceGetMappedPointer(devOutput, new long[1], resource);

        Pointer kernelParameters = Pointer.to(
                Pointer.to(deviceOutput),
                Pointer.to(new int[]{width}),
                Pointer.to(new int[]{height}),
                Pointer.to(new double[]{position.x}),
                Pointer.to(new double[]{position.y}),
                Pointer.to(new double[]{position.z}),
                Pointer.to(new double[]{lightIntensity}),
                Pointer.to(new double[]{lightSize}),
                Pointer.to(new double[]{lightIntensity}),
                Pointer.to(new double[]{lightSize}),
                Pointer.to(new int[]{0}),
                Pointer.to(new int[]{1})
        );

        cuLaunchKernel(function,
                width/32,  height/32, 1,      // Grid dimension
                32, 32, 1,      // Block dimension
                0, null,               // Shared memory size and stream
                kernelParameters, null // Kernel- and extra parameters
        );
        cuCtxSynchronize();

        cudaGraphicsUnmapResources(1, new cudaGraphicsResource[]{resource}, null);
    }


    public void update(){
        while (!this.Display.isCloseRequest()){

            if(Keyboard.keyPreesed(GLFW.GLFW_KEY_W)){
                position.x += 0.05 * Math.cos(horizontalAngle);
                position.y += 0.05 * Math.sin(horizontalAngle);
                position.z += 0.05 * Math.sin(verticalAngle);
            }
            else if(Keyboard.keyPreesed(GLFW.GLFW_KEY_S)){
                position.x -= 0.05 * Math.cos(horizontalAngle);
                position.y -= 0.05 * Math.sin(horizontalAngle);
                position.z -= 0.05 * Math.sin(verticalAngle);
            }
            else if(Keyboard.keyPreesed(GLFW.GLFW_KEY_A)){
                position.x += 0.05 * Math.sin(horizontalAngle);
                position.y -= 0.05 * Math.cos(horizontalAngle);
            }
            else if(Keyboard.keyPreesed(GLFW.GLFW_KEY_D)){
                position.x -= 0.05 * Math.sin(horizontalAngle);
                position.y += 0.05 * Math.cos(horizontalAngle);
            }
            else if(Keyboard.keyPreesed(GLFW.GLFW_KEY_Q)){
                position.z -= 0.05;
            }
            else if(Keyboard.keyPreesed(GLFW.GLFW_KEY_Z)){
                position.z += 0.05;
            }
            else if(Keyboard.keyPreesed(GLFW.GLFW_KEY_UP)){
                if(verticalAngle > -Math.PI){
                    verticalAngle -= Math.PI/360;
                }
            }
            else if(Keyboard.keyPreesed(GLFW.GLFW_KEY_DOWN)){
                if(verticalAngle < Math.PI){
                    verticalAngle += Math.PI/360;
                }
            }
            else if(Keyboard.keyPreesed(GLFW.GLFW_KEY_RIGHT)){
                    horizontalAngle += Math.PI/360;
            }
            else if(Keyboard.keyPreesed(GLFW.GLFW_KEY_LEFT)){
                    horizontalAngle -= Math.PI/360;
            }
            else if(Keyboard.keyPreesed(GLFW.GLFW_KEY_2)){
                if(lightIntensity < 1)
                    lightIntensity += 0.01;
            }
            else if(Keyboard.keyPreesed(GLFW.GLFW_KEY_1)){
                if(lightIntensity > 0)
                    lightIntensity -= 0.01;
            }
            else if(Keyboard.keyPreesed(GLFW.GLFW_KEY_4)){
                if(lightSize < 1)
                    lightSize += 0.01;
            }
            else if(Keyboard.keyPreesed(GLFW.GLFW_KEY_3)){
                if(lightSize > 0)
                    lightSize -= 0.01;
            }

            GL30.glBindBuffer( GL_PIXEL_UNPACK_BUFFER, bufferID);

            glBindTexture( GL_TEXTURE_2D, textureID);
            glTexSubImage2D( GL_TEXTURE_2D, 0, 0, 0, width, height,
                    GL_RGB, GL_UNSIGNED_BYTE, 0);

            glBindBuffer(GL_PIXEL_PACK_BUFFER, 0);
            glBindBuffer(GL_PIXEL_UNPACK_BUFFER, 0);

            glMatrixMode(GL_MODELVIEW);
            glViewport(0, 0, width, height);

            glBegin(GL_QUADS);
                glTexCoord2f(0.0f, 1.0f);
                glVertex2f(-1.0f, -1.0f);

                glTexCoord2f(1.0f, 1.0f);
                glVertex2f(1.0f, -1.0f);

                glTexCoord2f(1.0f, 0.0f);
                glVertex2f(1.0f, 1.0f);

                glTexCoord2f(0.0f, 0.0f);
                glVertex2f(-1.0f, 1.0f);
            glEnd();

            this.Display.update();

            try
            {
                Thread.sleep(10);
            }
            catch (InterruptedException e)
            {
                System.err.println(e.toString());
            }
        }

        glBindBuffer(GL_PIXEL_PACK_BUFFER, 0);
        glBindBuffer(GL_PIXEL_UNPACK_BUFFER, 0);

        this.Display.destroy();
    }

    public void setPosition(Vector position){
        this.position = position;
    }


    protected void getRenderedImage(int width, int height) {
        render(width, height);
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        File output = new File("C:\\IdeaProjects\\renderedImage.jpg");

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                byte newRed = byteImage[width * 3 * j + 3 * i];
                byte newGreen = byteImage[width * 3 * j + 3 * i + 1];
                byte newBlue = byteImage[width * 3 * j + 3 * i + 2];

                Color newColor = new Color(newRed & 0xFF, newGreen & 0xFF, newBlue & 0xFF);

                bufferedImage.setRGB(i, j, newColor.getRGB());
            }
        }

        try {
            ImageIO.write(bufferedImage, "jpg", output);
        }
        catch (Exception e){
            System.err.println(e.toString());
        }
    }

}
