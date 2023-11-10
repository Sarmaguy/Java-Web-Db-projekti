package oprpp2.jmbag.webserver.workers;

import oprpp2.jmbag.webserver.IWebWorker;
import oprpp2.jmbag.webserver.RequestContext;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class CircleWorker implements IWebWorker {
    @Override
    public void processRequest(RequestContext context) throws Exception {
        BufferedImage bim = new BufferedImage(200, 200, BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D g2d = bim.createGraphics();

        g2d.setColor(Color.MAGENTA);
        g2d.fillOval(0, 0, 200, 200);
        g2d.dispose();

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        context.setMimeType("image/png");
        try{
            ImageIO.write(bim, "png", bos);
            context.write(bos.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
