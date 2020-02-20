/******************************************************************
 *Name: Oliver Pope 18344822
 *Completion Date: 30/10/2019
 *ASSERTION: SwingArena
 ******************************************************************/


import java.util.HashMap;
import java.util.Map;

import java.awt.*;
import javax.swing.*;

/**
 * A Swing GUI element that displays a grid on which you can draw images, text and lines.
 */
public class SwingArena extends JPanel
{
    // Represents the image to draw. You can modify this to introduce multiple images.
    private static final String IMAGE_FILE = "bot.png";
    private static final String IMAGE_FILE2 = "bot_hit_bot.png";
    private static final String IMAGE_FILE3 = "tombstone.png";
    private static final String IMAGE_FILE4 = "bot_got_hit.png";

    private ImageIcon robot1;
    private ImageIcon botHitBot;
    private ImageIcon headStone;
    private ImageIcon hitRobot;


    private Map<String, RobotInfo> robotInfoMap;

    // The following values are arbitrary, and you may need to modify them according to the 
    // requirements of your application.
    private int gridWidth;
    private int gridHeight;


    private double gridSquareSize; // Auto-calculated
    private Canvas canvas; // Used to provide a 'drawing surface'.

    
    /**
     * Creates a new arena object, loading the robot image and initialising a drawing surface.
     */
    public SwingArena(int inHeight, int inWidth)
    {
        this.gridWidth = inWidth+1;
        this.gridHeight = inHeight+1;
        robotInfoMap = new HashMap<String, RobotInfo>();

        // Here's how you get an Image object from an image file (which you provide in the 
        // 'resources/' directory.
        robot1 = new ImageIcon(getClass().getClassLoader().getResource(IMAGE_FILE));
        botHitBot = new ImageIcon(getClass().getClassLoader().getResource(IMAGE_FILE2));
        headStone = new ImageIcon(getClass().getClassLoader().getResource(IMAGE_FILE3));
        hitRobot = new ImageIcon(getClass().getClassLoader().getResource(IMAGE_FILE4));

    }


    /******************************************************************
     *SUBMODULE: initaliseRobots
     *ASSERTION: sets the all info map and updates gui
     ******************************************************************/
    public void initaliseRobots(Map<String, RobotInfo> inMap)
    {
        this.robotInfoMap = inMap;
        repaint();
    }


    /******************************************************************
     *SUBMODULE: updateRobotGUI
     *ASSERTION: Takes in an individual robotInfo, updates it wihtin the
     * local map and updates gui
     ******************************************************************/
    public void updateRobotGUI(RobotInfo inBot)
    {
        robotInfoMap.put(inBot.getName(), inBot);
        repaint();
    }



    /**
     * This method is called in order to redraw the screen, either because the user is manipulating 
     * the window, OR because you've called 'repaint()'.
     *
     * You will need to modify the last part of this method; specifically the sequence of calls to
     * the other 'draw...()' methods. You shouldn't need to modify anything else about it.
     */
    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D gfx = (Graphics2D) g;
        gfx.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC);

        // First, calculate how big each grid cell should be, in pixels. (We do need to do this
        // every time we repaint the arena, because the size can change.)
        gridSquareSize = Math.min(
                (double) getWidth() / (double) gridWidth,
                (double) getHeight() / (double) gridHeight);

        int arenaPixelWidth = (int) ((double) gridWidth * gridSquareSize);
        int arenaPixelHeight = (int) ((double) gridHeight * gridSquareSize);


        // Draw the arena grid lines. This may help for debugging purposes, and just generally
        // to see what's going on.
        gfx.setColor(Color.GRAY);
        gfx.drawRect(0, 0, arenaPixelWidth - 1, arenaPixelHeight - 1); // Outer edge

        for(int gridX = 1; gridX < gridWidth; gridX++) // Internal vertical grid lines
        {
            int x = (int) ((double) gridX * gridSquareSize);
            gfx.drawLine(x, 0, x, arenaPixelHeight);
        }

        for(int gridY = 1; gridY < gridHeight; gridY++) // Internal horizontal grid lines
        {
            int y = (int) ((double) gridY * gridSquareSize);
            gfx.drawLine(0, y, arenaPixelWidth, y);
        }



        /*for each loop iterates through all the robots in the local map, if its dead it draws it as a tombstone, if its
        still allive then it draws its label, which may be a different colour indicating the robot hit another tobot.
        The colour is dependent on the AI implemetnation. If the bot is firing then a line is drawn using its current location
        and the target location. If a bot has been notified that its been hit its icon will also change to indicate this
        notification reaciton.
         */
        for(RobotInfo bot: robotInfoMap.values())
        {
            if(bot.isAlive())
            {
                drawLabel(gfx, bot.getName() + " " +  bot.getHealth(), bot.getColumn(), bot.getRow(), new Color(bot.getBotColor()[0],bot.getBotColor()[1],bot.getBotColor()[2]));

                if(bot.isFiring())
                {
                    drawLine(gfx, bot.getColumn(), bot.getRow(), bot.getTargetColumn(), bot.getTargetRow());
                }

                if(bot.getHit())
                {
                    drawImage(gfx, hitRobot, bot.getColumn(), bot.getRow());
                }
                else
                {
                    drawImage(gfx, robot1, bot.getColumn(), bot.getRow());
                }
            }
            else
            {
                drawImage(gfx, headStone, bot.getColumn(), bot.getRow());
                drawLabel(gfx, bot.getName() + " " +  bot.getHealth(), bot.getColumn(), bot.getRow(), Color.BLUE);

            }


        }

    }


    /**
     * Draw an image in a specific grid location. *Only* call this from within paintComponent().
     *
     * Note that the grid location can be fractional, so that (for instance), you can draw an image
     * at location (3.5,4), and it will appear on the boundary between grid cells (3,4) and (4,4).
     *
     * You shouldn't need to modify this method.
     */
    private void drawImage(Graphics2D gfx, ImageIcon icon, double gridX, double gridY)
    {
        // Get the pixel coordinates representing the centre of where the image is to be drawn.
        double x = (gridX + 0.5) * gridSquareSize;
        double y = (gridY + 0.5) * gridSquareSize;

        // We also need to know how "big" to make the image. The image file has a natural width
        // and height, but that's not necessarily the size we want to draw it on the screen. We
        // do, however, want to preserve its aspect ratio.
        double fullSizePixelWidth = (double) robot1.getIconWidth();
        double fullSizePixelHeight = (double) robot1.getIconHeight();

        double displayedPixelWidth, displayedPixelHeight;
        if(fullSizePixelWidth > fullSizePixelHeight)
        {
            // Here, the image is wider than it is high, so we'll display it such that it's as
            // wide as a full grid cell, and the height will be set to preserve the aspect
            // ratio.
            displayedPixelWidth = gridSquareSize;
            displayedPixelHeight = gridSquareSize * fullSizePixelHeight / fullSizePixelWidth;
        }
        else
        {
            // Otherwise, it's the other way around -- full height, and width is set to
            // preserve the aspect ratio.
            displayedPixelHeight = gridSquareSize;
            displayedPixelWidth = gridSquareSize * fullSizePixelWidth / fullSizePixelHeight;
        }

        // Actually put the image on the screen.
        gfx.drawImage(icon.getImage(),
                (int) (x - displayedPixelWidth / 2.0),  // Top-left pixel coordinates.
                (int) (y - displayedPixelHeight / 2.0),
                (int) displayedPixelWidth,              // Size of displayed image.
                (int) displayedPixelHeight,
                null);
    }


    /**
     * Displays a string of text underneath a specific grid location. *Only* call this from within 
     * paintComponent().
     *
     * You shouldn't need to modify this method.
     */
    private void drawLabel(Graphics2D gfx, String label, double gridX, double gridY, Color color)
    {
        gfx.setColor(color);
        FontMetrics fm = gfx.getFontMetrics();
        gfx.drawString(label,
                (int) ((gridX + 0.5) * gridSquareSize - (double) fm.stringWidth(label) / 2.0),
                (int) ((gridY + 1.0) * gridSquareSize) + fm.getHeight());
    }

    /**
     * Draws a (slightly clipped) line between two grid coordinates. 
     *
     * You shouldn't need to modify this method.
     */
    private void drawLine(Graphics2D gfx, double gridX1, double gridY1,
                          double gridX2, double gridY2)
    {
        gfx.setColor(Color.RED);

        // Recalculate the starting coordinate to be one unit closer to the destination, so that it
        // doesn't overlap with any image appearing in the starting grid cell.
        final double radius = 0.5;
        double angle = Math.atan2(gridY2 - gridY1, gridX2 - gridX1);
        double clippedGridX1 = gridX1 + Math.cos(angle) * radius;
        double clippedGridY1 = gridY1 + Math.sin(angle) * radius;

        gfx.drawLine((int) ((clippedGridX1 + 0.5) * gridSquareSize),
                (int) ((clippedGridY1 + 0.5) * gridSquareSize),
                (int) ((gridX2 + 0.5) * gridSquareSize),
                (int) ((gridY2 + 0.5) * gridSquareSize));
    }
}
