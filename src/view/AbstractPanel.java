package view;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Observer;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 * A generic panel for each of the screens in the game.
 * Loads the images for the panel upon construction.
 * 
 * @author Terron Ishihara, Zachary Van Uum, Kegan Schaub
 *
 */
public abstract class AbstractPanel extends JPanel implements Observer {
	
	private static final long serialVersionUID = 8654352540565658174L;
	protected BufferedImage alu;
	protected BufferedImage and;
	protected BufferedImage data;
	protected BufferedImage exor;
	protected BufferedImage mux;
	protected BufferedImage not;
	protected BufferedImage or;
	protected BufferedImage pc;
	protected BufferedImage reg;
	protected BufferedImage shift2;
	protected BufferedImage andEnemy;
	protected BufferedImage orEnemy;
	protected BufferedImage exorEnemy;
	protected BufferedImage notEnemy;
	protected Image boom;
	protected Image attackImage;
	protected Image hit;
	protected Image tile;
	
	public AbstractPanel(){
		loadImages();
	}

	private void loadImages() {
		try {
			alu = ImageIO.read(new File("./images/alu.png"));
			and = ImageIO.read(new File("./images/and.png"));
			or = ImageIO.read(new File("./images/or.png"));
			exor = ImageIO.read(new File("./images/exor.png"));
			mux = ImageIO.read(new File("./images/mux.png"));
			shift2 = ImageIO.read(new File("./images/shift2.png"));
			not = ImageIO.read(new File("./images/not.png"));
			pc = ImageIO.read(new File("./images/pc.png"));
			reg = ImageIO.read(new File("./images/register.png"));
			andEnemy = ImageIO.read(new File("./images/andEnemy.png"));
			orEnemy = ImageIO.read(new File("./images/orEnemy.png"));
			exorEnemy = ImageIO.read(new File("./images/exorEnemy.png"));
			notEnemy = ImageIO.read(new File("./images/notEnemy.png"));
			tile = Toolkit.getDefaultToolkit().getImage("./images/tile.gif");
			boom = Toolkit.getDefaultToolkit().getImage("./images/boom.gif");
			attackImage = Toolkit.getDefaultToolkit().getImage("./images/attack.gif");
			hit = Toolkit.getDefaultToolkit().getImage("./images/hit.gif");
		} catch (IOException e) {
			System.out.println("Could not find images");
		}
	}
}
